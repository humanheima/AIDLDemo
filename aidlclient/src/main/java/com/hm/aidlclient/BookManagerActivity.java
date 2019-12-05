package com.hm.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hm.aidlserver.Book;
import com.hm.aidlserver.IBookManager;
import com.hm.aidlserver.IOnNewBookArriveListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookManagerActivity extends AppCompatActivity {

    private final String TAG = BookManagerActivity.class.getSimpleName();
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager bookManager;
    private List<Book> bookList;
    private boolean binded;
    private int bookId = 20;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "handleMessage: receive new book:" + msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 当Binder死亡时，我们会收到通知，这个时候，我们可以重新发起连接请求从而恢复链接。
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "binderDied: " + Thread.currentThread().getName());
            if (bookManager != null) {
                bookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
                bookManager = null;
            }
            bind();
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected:" + service.getClass().getCanonicalName());
            bookManager = IBookManager.Stub.asInterface(service);
            Log.e(TAG, "onServiceConnected:" + bookManager.getClass().getCanonicalName());
            try {
                //service.linkToDeath(mDeathRecipient, 0);
                getBookList();
                bookManager.registerListener(mOnNewBookArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onServiceConnected: error" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bookManager = null;
            Log.e(TAG, "onServiceDisconnected: " + Thread.currentThread().getName());
            bind();
        }
    };

    private IOnNewBookArriveListener mOnNewBookArriveListener = new IOnNewBookArriveListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            //这个是在客户端的Binder线程池中运行的
            Log.e(TAG, "onNewBookArrived: " + Thread.currentThread().getName());
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    public static void launch(Context context) {
        Intent starter = new Intent(context, BookManagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_bind)
    public void onViewClicked() {
        bind();
    }

    @OnClick(R.id.btn_addBook)
    public void addBook() {
        if (bookManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bookManager.addBook(new Book(bookId, "Android 进阶" + bookId));
                        bookId++;
                        bookList = bookManager.getBookList();
                        Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                        Log.e(TAG, "query book list,list:" + bookList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "远程服务已断开", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_getBookList)
    public void getBookList() {
        if (bookManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bookList = bookManager.getBookList();
                        Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                        Log.e(TAG, "query book list,list:" + bookList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "远程服务已断开", Toast.LENGTH_SHORT).show();
        }
    }

    private void bind() {
        binded = true;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.BookManagerService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
            Log.e(TAG, "onDestroy: unregister listener:" + mOnNewBookArriveListener);
            try {
                bookManager.unRegisterListener(mOnNewBookArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onDestroy: RemoteException" + e.getMessage());
            }
        }
        super.onDestroy();
        if (binded) {
            unbindService(connection);
        }
    }
}
