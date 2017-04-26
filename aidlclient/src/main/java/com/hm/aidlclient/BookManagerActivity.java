package com.hm.aidlclient;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BookManagerActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent starter = new Intent(context, BookManagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
    }
}
