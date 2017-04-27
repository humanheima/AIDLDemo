// IBookManager.aidl
package com.hm.aidlserver;

import com.hm.aidlserver.Book;
import com.hm.aidlserver.IOnNewBookArriveListener;

interface IBookManager {

   List<Book>getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBookArriveListener listener);
   void unRegisterListener(IOnNewBookArriveListener listener);
}
