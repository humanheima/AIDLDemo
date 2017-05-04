package com.hm.aidlserver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 2;

    public static final String AUTHORITY = "com.hm.aidlserver.bookprovider";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");
    private static UriMatcher uriMatcher;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        uriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = MyDataBaseHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = MyDataBaseHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }


    public BookProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("UpSupport uri");
        }
        int count = db.delete(table, selection, selectionArgs);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType: current thread:" + Thread.currentThread().getName());
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert: current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("UpSupport uri");
        }
        db.insert(table, null, values);
        return uri;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: current thread:" + Thread.currentThread().getName());
        initProviderData();
        return true;
    }

    private void initProviderData() {
        dbHelper = new MyDataBaseHelper(getContext());
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + MyDataBaseHelper.BOOK_TABLE_NAME);
        db.execSQL("delete from " + MyDataBaseHelper.USER_TABLE_NAME);
        db.execSQL("insert into book values (3,'Android');");
        db.execSQL("insert into book values (4,'ios');");
        db.execSQL("insert into book values (5,'html5');");

        db.execSQL("insert into user values (1,'jack',1);");
        db.execSQL("insert into user values (2,'jeny',0);");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("UpSupport uri");
        }
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update: current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("UpSupport uri");
        }
        int row = db.update(table, values, selection, selectionArgs);
        return row;
    }
}
