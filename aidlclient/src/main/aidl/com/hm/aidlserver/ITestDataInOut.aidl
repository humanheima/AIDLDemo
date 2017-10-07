// ITestDataInOut.aidl
package com.hm.aidlserver;

import com.hm.aidlserver.Book;

interface ITestDataInOut {

       //测试定向tag in out inout
       List<Book> getBooks();
       Book addBookIn(in Book book);
       Book addBookOut(out Book book);
       Book addBookInout(inout Book book);

       int testPrimitiveTypeData(int a);
}
