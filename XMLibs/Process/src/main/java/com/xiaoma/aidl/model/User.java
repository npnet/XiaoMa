package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/12/5 0005.
 */

public class User implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
