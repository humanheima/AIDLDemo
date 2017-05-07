package com.hm.aidlclient;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hm.aidlserver.ICompute;
import com.hm.aidlserver.ISecurityCenter;
import com.hm.aidlserver.impl.IComputeImpl;
import com.hm.aidlserver.impl.SecurityCenterImpl;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";
    private ISecurityCenter mSecurityCenter;
    private ICompute mComputer;

    public static void launch(Context context) {
        Intent starter = new Intent(context, BinderPoolActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getsInstance(this);
        IBinder securityBinder = binderPool.queryBinder(1);
        mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
        Log.e(TAG, "doWork: visit ISecurityCenter");
        String msg = "helloworld-安卓";
        try {
            String password = mSecurityCenter.encrypt(msg);
            Log.e(TAG, "doWork: encrypt" + password);
            String content = mSecurityCenter.decrypt(password);
            Log.e(TAG, "doWork: decrypt" + content);
            IBinder computeBinder = binderPool.queryBinder(0);
            mComputer = IComputeImpl.asInterface(computeBinder);
            Log.e(TAG, "doWork: visit ICompute");
            Log.e(TAG, "doWork: " + mComputer.add(7, 14));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
