package com.hm.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class ITestDataService extends Service {

    private List<Person> persons;
    private IBinder iBinder = new DataTestAidlInterface.Stub() {
        @Override
        public List<Person> getPersonListIn(Person person) throws RemoteException {
            persons.add(person);
            return persons;
        }
    };

    public ITestDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        persons=new ArrayList<>();
        return iBinder;
    }
}
