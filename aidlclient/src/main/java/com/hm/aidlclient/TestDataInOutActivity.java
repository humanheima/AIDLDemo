package com.hm.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hm.aidlserver.Book;
import com.hm.aidlserver.ITestDataInOut;

import java.util.List;

public class TestDataInOutActivity extends AppCompatActivity {

    private static final String TAG = "TestDataInOutActivity";
    private ITestDataInOut iTestDataInOut = null;
    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;
    private List<Book> mBooks;

    public static void launch(Context context) {
        Intent starter = new Intent(context, TestDataInOutActivity.class);
        context.startActivity(starter);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "service connected");
            iTestDataInOut = ITestDataInOut.Stub.asInterface(service);
            mBound = true;
            if (iTestDataInOut != null) {
                try {
                    mBooks = iTestDataInOut.getBooks();
                    Log.d(TAG, "onServiceConnected: " + mBooks.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_in_out);
        if (!mBound) {
            attemptToBindService();
        }
    }

    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.service.TestDataInOutService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void addBookIn(View view) {
        Book book = new Book(3, "app研发录");
        try {
            Book returnBook = iTestDataInOut.addBookIn(book);
            Log.d(TAG, "addBookIn: " + returnBook.toString());
            Log.d(TAG, "addBookIn: " + book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addBookOut(View view) {
        Book book = new Book(4, "app研发录");
        try {
            Book returnBook = iTestDataInOut.addBookOut(book);
            Log.d(TAG, "addBookOut: " + returnBook.toString());
            Log.d(TAG, "addBookOut: " + book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addBookInOut(View view) {
        Book book = new Book(5, "app研发录");
        try {
            Book returnBook = iTestDataInOut.addBookInout(book);
            Log.d(TAG, "addBookInOut: " + returnBook.toString());
            Log.d(TAG, "addBookInOut: " + book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void testPrimitiveData(View view) {
        try {
            int result = iTestDataInOut.testPrimitiveTypeData(2);
            Log.d(TAG, "testPrimitiveTypeData: " + result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(connection);
            mBound = false;
        }
    }
}
