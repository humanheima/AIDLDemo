package com.hm.aidlserver.impl;

        import android.os.RemoteException;

        import com.hm.aidlserver.ICompute;

/**
 * Created by dumingwei on 2017/5/7.
 */
public class IComputeImpl extends ICompute.Stub {

    private static final String TAG = "IComputeImpl";

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
