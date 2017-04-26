// IBookManager.aidl
package com.hm.aidlserver;

import com.hm.aidlserver.Book;

interface IBookManager {

   List<Book>getBookList();
   void addBook(in Book book);
}
