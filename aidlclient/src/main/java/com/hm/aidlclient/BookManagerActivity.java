package com.hm.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hm.aidlserver.Book;
import com.hm.aidlserver.IBookManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookManagerActivity extends AppCompatActivity {


    private final String TAG = BookManagerActivity.class.getSimpleName();
    private IBookManager bookManager;
    private List<Book> bookList;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                bookList = bookManager.getBookList();
                Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                Log.e(TAG, "query book list,list:" + bookList.toString());
                bookManager.addBook(new Book(3,"Android 开发艺术探索"));
                bookList = bookManager.getBookList();
                Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                Log.e(TAG, "query book list,list:" + bookList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onServiceConnected: error" + e.getMessage());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

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
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.BookManagerService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
