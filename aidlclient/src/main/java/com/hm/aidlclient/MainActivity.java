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
import android.widget.Button;
import android.widget.EditText;

import com.hm.aidlserver.DataTestAidlInterface;
import com.hm.aidlserver.IMyAidlInterface;
import com.hm.aidlserver.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.et_num1)
    EditText etNum1;
    @BindView(R.id.et_num2)
    EditText etNum2;
    @BindView(R.id.edit_show_result)
    EditText editShowResult;
    @BindView(R.id.btn_count)
    Button btnCount;

    private int mNum1;
    private int mNum2;
    private int mTotal;
    private int age = 1;
    private List<Person> persons;
    private IMyAidlInterface iMyAidlInterface;
    private DataTestAidlInterface dataTestAidlInterface;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //服务绑定成功后调用，获取服务端的接口，这里的service就是服务端onBind返
            //回的iBinder即已实现的接口
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyAidlInterface = null;
        }
    };
    //传递一个序列化的对象
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dataTestAidlInterface = DataTestAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (dataTestAidlInterface != null) {
                dataTestAidlInterface = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //bindService();
        bindDataService();
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.IRemoteService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private void bindDataService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.ITestDataService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @OnClick({R.id.btn_count, R.id.btn_pass_object})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_count:
                mNum1 = Integer.parseInt(etNum1.getText().toString());
                mNum2 = Integer.parseInt(etNum2.getText().toString());
                try {
                    mTotal = iMyAidlInterface.add(mNum1, mNum2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                editShowResult.setText("mTotal=" + mTotal);
                break;
            case R.id.btn_pass_object:
                Person person = new Person("dumingwei", age);
                age++;
                try {
                    persons = dataTestAidlInterface.getPersonListIn(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                editShowResult.setText(persons.toString());
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection != null) {
            unbindService(connection);
        }
    }
}
