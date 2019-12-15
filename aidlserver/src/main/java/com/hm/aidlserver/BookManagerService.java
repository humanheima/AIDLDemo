package com.hm.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务端的Service
 */
public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getSimpleName();

    //使用CopyOnWriteArrayList，因为可能会有多个客户端同时操作书籍列表
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArriveListener> mListenerList = new RemoteCallbackList<>();
    private int num = 10;

    /**
     * 实现IBookManager接口中的方法
     */
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            //运行在Binder线程池中的线程
            Log.d(TAG, "getBookList: " + Thread.currentThread().getName());
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            //运行在Binder线程池中的线程
            Log.d(TAG, "addBook: " + Thread.currentThread().getName());
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArriveListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unRegisterListener(IOnNewBookArriveListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            /**
             * 验证permission
             */
            Log.e(TAG, "onTransact 验证权限" + Thread.currentThread().getName());
            int check = checkCallingOrSelfPermission("com.hm.aidlserver.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "onTransact: permission denied");
                return false;
            }
            /**
             * 验证包名
             */
            String packageName;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
                if (!packageName.startsWith("com.hm")) {
                    Log.e(TAG, "onTransact: package verify failed");
                    return false;
                }
            } else {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "ios"));
        //这个是在服务端的主线程，我们新开一个线程来通知观察者
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //也可以在这里做权限验证
        int check = checkCallingOrSelfPermission("com.hm.aidlserver.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "onBind: permission denied");
            return null;
        }
        Log.e(TAG, "onBind: permission granted");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    /**
     * 如果IOnNewBookArriveListener#onNewBookArrived(com.hm.aidlserver.Book newBook)方法是耗时方法的话
     * <p>
     * notifyBookArrived不能运行在UI线程
     * 通知观察者有新书到来
     *
     * @param book
     * @throws RemoteException
     */
    private void notifyBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArriveListener l = mListenerList.getBroadcastItem(i);
            if (l != null) {

                l.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }


    /**
     * 模拟新书到来的操作
     */
    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroyed.get() && num < 12) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Book newBook = new Book(num, "new Book#" + num);
                num++;
                try {
                    //在非UI线程运行notifyBookArrived方法，防止阻塞服务端的主线程
                    notifyBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
