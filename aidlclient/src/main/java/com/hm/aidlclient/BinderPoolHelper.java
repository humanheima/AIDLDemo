package com.hm.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hm.aidlserver.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * Created by dumingwei on 2017/5/7.
 * Binder连接池的具体实现
 */
public class BinderPoolHelper {

    private static final String TAG = "BinderPoolHelper";
    public static final int BINDERT_COMPUTE = 0;
    public static final int BINDERT_NOSECURITY_CENTER = 1;

    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BinderPoolHelper sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "binderDied: ");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    private BinderPoolHelper(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();

    }

    public static BinderPoolHelper getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPoolHelper.class) {
                if (sInstance == null) {
                    sInstance = new BinderPoolHelper(context);
                }
            }

        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.BinderPoolService"));
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }
}
