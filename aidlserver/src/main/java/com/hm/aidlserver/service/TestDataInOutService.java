package com.hm.aidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hm.aidlserver.Book;
import com.hm.aidlserver.ITestDataInOut;

import java.util.ArrayList;
import java.util.List;

public class TestDataInOutService extends Service {

    private static final String TAG = "TestDataInOutService";
    private List<Book> mBooks = new ArrayList<>();

    private ITestDataInOut.Stub mBinder = new ITestDataInOut.Stub() {
        @Override
        public List<Book> getBooks() throws RemoteException {
            synchronized (this) {
                Log.e(TAG, "invoking getBooks() method , now the list is : " + mBooks.toString());
                return mBooks;
            }
        }

        @Override
        public Book addBookIn(Book book) throws RemoteException {
            synchronized (this) {
                if (mBooks == null)
                    mBooks = new ArrayList<>();
                book.setBookName(book.getBookName() + "addBookIn");
                mBooks.add(book);
                Log.d(TAG, "invoking addBookIn() method , now the list is : " + mBooks.toString());
                return book;
            }
        }

        @Override
        public Book addBookOut(Book book) throws RemoteException {
            synchronized (this) {
                if (mBooks == null)
                    mBooks = new ArrayList<>();
                book.setBookName(book.getBookName() + "addBookOut");
                mBooks.add(book);
                Log.d(TAG, "invoking addBookOut() method , now the list is : " + mBooks.toString());
                return book;
            }
        }

        @Override
        public Book addBookInout(Book book) throws RemoteException {
            synchronized (this) {
                if (mBooks == null)
                    mBooks = new ArrayList<>();
                book.setBookName(book.getBookName() + "addBookInout");
                mBooks.add(book);
                Log.d(TAG, "invoking addBookInout() method , now the list is : " + mBooks.toString());
                return book;
            }
        }

        @Override
        public int testPrimitiveTypeData(int a) throws RemoteException {
            return a + 1;
        }

    };

    public TestDataInOutService() {
    }

    @Override
    public void onCreate() {
        Book book1 = new Book(1, "疯狂java讲义");
        Book book2 = new Book(2, "Android开发艺术探索");
        mBooks.add(book1);
        mBooks.add(book2);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
