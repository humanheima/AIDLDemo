package com.hm.aidlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_baseknowledge, R.id.btn_bookmanager, R.id.btn_messenger})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_baseknowledge:
                BaseKnowledgeActivity.launch(this);
                break;
            case R.id.btn_bookmanager:
                BookManagerActivity.launch(this);
                break;
            case R.id.btn_messenger:
                MessengerActivity.launch(this);
                break;
            default:
                break;
        }
    }
}
