package com.hm.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dumingwei on 2017/10/6.
 */
public class TestSerializable implements Parcelable {

    private int age;
    private String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.name);
    }

    public TestSerializable() {
    }

    protected TestSerializable(Parcel in) {
        this.age = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<TestSerializable> CREATOR = new Parcelable.Creator<TestSerializable>() {
        @Override
        public TestSerializable createFromParcel(Parcel source) {
            return new TestSerializable(source);
        }

        @Override
        public TestSerializable[] newArray(int size) {
            return new TestSerializable[size];
        }
    };
}
