package com.hm.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hm.aidlserver.impl.IComputeImpl;
import com.hm.aidlserver.impl.SecurityCenterImpl;

public class BinderPoolService extends Service {

    private static final String TAG = "BinderPoolService";

    private Binder mBinderPool = new BinderPool();

    public BinderPoolService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return mBinderPool;
    }

    private class BinderPool extends IBinderPool.Stub {

        private static final String TAG = "BinderPool";
        private static final int BINDERT_COMPUTE = 0;
        private static final int BINDERT_NOSECURITY_CENTER = 1;

        private BinderPool() {
            Log.e(TAG, "调用构造函数");
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDERT_NOSECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                case BINDERT_COMPUTE:
                    binder = new IComputeImpl();
                    break;
            }
            return binder;
        }
    }
}
