package com.hm.aidlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final int RC_PROVIDER = 100;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_baseknowledge, R.id.btn_bookmanager, R.id.btn_messenger, R.id.btn_provider, R.id.btn_binderpool})
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
            case R.id.btn_provider:
                openProviderActivity();
                break;
            case R.id.btn_binderpool:
                BinderPoolActivity.launch(this);
                break;
            default:
                break;
        }
    }


    private void openProviderActivity() {
        ProviderActivity.launch(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_PROVIDER) {
            Log.e(TAG, "onPermissionsGranted: " + perms.toString());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_PROVIDER) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
    }
}
