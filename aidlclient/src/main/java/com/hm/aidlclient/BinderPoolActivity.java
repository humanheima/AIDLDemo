package com.hm.aidlclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hm.aidlserver.ICompute;
import com.hm.aidlserver.ISecurityCenter;
import com.hm.aidlserver.impl.IComputeImpl;
import com.hm.aidlserver.impl.SecurityCenterImpl;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";
    private ISecurityCenter mSecurityCenter;
    private ICompute mComputer;
    private BinderPool binderPool;

    public static void launch(Context context) {
        Intent starter = new Intent(context, BinderPoolActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_ISecurityCenter, R.id.btn_ICompute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ISecurityCenter:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        binderPool = BinderPool.getsInstance(BinderPoolActivity.this);
                        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDERT_NOSECURITY_CENTER);
                        mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
                        Log.e(TAG, "doWork: visit ISecurityCenter");
                        String msg = "helloworld-安卓";
                        String password;
                        try {
                            password = mSecurityCenter.encrypt(msg);
                            Log.e(TAG, "doWork: encrypt" + password);
                            String content = mSecurityCenter.decrypt(password);
                            Log.e(TAG, "doWork: decrypt" + content);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.btn_ICompute:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        binderPool = BinderPool.getsInstance(BinderPoolActivity.this);
                        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDERT_COMPUTE);
                        mComputer = IComputeImpl.asInterface(computeBinder);
                        Log.e(TAG, "doWork: visit ICompute");
                        try {
                            Log.e(TAG, "doWork: " + mComputer.add(7, 14));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binderPool = null;
    }
}
