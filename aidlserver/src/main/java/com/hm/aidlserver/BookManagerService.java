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

public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getSimpleName();
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArriveListener> mListenerList = new RemoteCallbackList<>();
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArriveListener listener) throws RemoteException {
            mListenerList.register(listener);
            Log.e(TAG, "registerListener: " + mListenerList.beginBroadcast());
            mListenerList.finishBroadcast();
        }

        @Override
        public void unRegisterListener(IOnNewBookArriveListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            Log.e(TAG, "unRegisterListener:" + mListenerList.beginBroadcast());
            mListenerList.finishBroadcast();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //在这里也可以做权限验证
            int check = checkCallingOrSelfPermission("com.hm.aidlserver.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "onBind: permission denied");
                return false;
            }
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
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "ios"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: 2017/4/28 在这里做验证
       /* int check = checkCallingOrSelfPermission("com.hm.aidlserver.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "onBind: permission denied");
            return null;
        }
        Log.e(TAG, "onBind: permission granted");*/
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private void onNewBookArrived(Book book) throws RemoteException {
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

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new Book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
