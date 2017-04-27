// IOnNewBookArriveListener.aidl
package com.hm.aidlserver;
import com.hm.aidlserver.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArriveListener {

 void onNewBookArrived(in Book newBook);

}
