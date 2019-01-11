package com.hm.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dmw on 2019/1/11.
 * Desc:
 */
public class User implements Parcelable {

    private int userId;
    private String userName;
    private boolean isMale;


    protected User(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeByte((byte) (isMale ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 用来完成反序列化过程
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
