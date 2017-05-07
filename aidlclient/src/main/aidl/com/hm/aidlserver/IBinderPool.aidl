// IBinderPool.aidl
package com.hm.aidlserver;

// Declare any non-default types here with import statements

interface IBinderPool {

  IBinder queryBinder(int binderCode);
}
