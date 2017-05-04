package com.hm.aidlclient;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProviderActivity extends AppCompatActivity {

    private static final String TAG = "ProviderActivity";
    public static final int REQUEST_CODE = 100;
    @BindView(R.id.list_view)
    ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> contactList;

    public static void launch(Context context) {
        Intent starter = new Intent(context, ProviderActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        ButterKnife.bind(this);
        //getContacts();
        useBookProvider();
    }

    private void useBookProvider() {

        Uri bookUri = Uri.parse("content://com.hm.aidlserver.bookprovider/book");
        //插入操作
        ContentValues values = new ContentValues();
        values.put("id", 6);
        values.put("name", "program design art");
        getContentResolver().insert(bookUri, values);
        Cursor bookCursor = getContentResolver().query(bookUri, null, null, null, null);
        if (bookCursor != null) {
            while (bookCursor.moveToNext()) {
                Log.e(TAG, " bookId:" + bookCursor.getInt(bookCursor.getColumnIndex("id")) + ", bookName:" + bookCursor.getString(bookCursor.getColumnIndex("name")));
            }
            bookCursor.close();
        }
        //更新操作
        ContentValues updateValue = new ContentValues();
        updateValue.put("name", "update program design art");
        int row = getContentResolver().update(bookUri, updateValue, "id = ?", new String[]{"6"});
        Log.e(TAG, "useBookProvider: update row=" + row);
        Cursor updateCursor = getContentResolver().query(bookUri, null, null, null, null);
        if (updateCursor != null) {
            while (updateCursor.moveToNext()) {
                Log.e(TAG, " bookId:" + updateCursor.getInt(updateCursor.getColumnIndex("id")) +
                        ", bookName:" + updateCursor
                        .getString(updateCursor.getColumnIndex("name")));
            }
            updateCursor.close();
        }
        //删除操作
        int count = getContentResolver().delete(bookUri, "id = ?", new String[]{"3"});
        Log.e(TAG, "useBookProvider: delete count=" + count);
        Cursor deleteCursor = getContentResolver().query(bookUri, null, null, null, null);
        if (deleteCursor != null) {
            while (deleteCursor.moveToNext()) {
                Log.e(TAG, " bookId:" + deleteCursor.getInt(deleteCursor.getColumnIndex("id")) +
                        ", bookName:" + deleteCursor
                        .getString(deleteCursor.getColumnIndex("name")));
            }
            deleteCursor.close();
        }

        Uri userUri = Uri.parse("content://com.hm.aidlserver.bookprovider/user");
        Cursor userCursor = getContentResolver().query(userUri, null, null, null, null);
        if (userCursor != null) {
            while (userCursor.moveToNext()) {
                Log.e(TAG, " userId:" + userCursor.getInt(userCursor.getColumnIndex("id")) +
                        " ,userName:" + userCursor.getString(userCursor.getColumnIndex("name"))
                        + " ,sex:" + userCursor.getInt(userCursor.getColumnIndex("sex")));
            }
            userCursor.close();
        }
    }


    private void getContacts() {
        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
        listView.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        } else {
            readContacts();
        }
    }

    private void readContacts() {
        Cursor cursor;
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactList.add(displayName + "\n" + number);

            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

                break;
        }
    }
}
