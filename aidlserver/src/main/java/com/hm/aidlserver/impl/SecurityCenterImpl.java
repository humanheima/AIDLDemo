package com.hm.aidlserver.impl;

import android.os.RemoteException;

import com.hm.aidlserver.ISecurityCenter;

/**
 * Created by dumingwei on 2017/5/7.
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final String TAG = "SecurityCenterImpl";
    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
