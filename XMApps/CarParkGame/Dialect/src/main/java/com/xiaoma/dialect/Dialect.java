package com.xiaoma.dialect;

import android.app.Application;

import com.xiaoma.network.XmHttp;

public class Dialect extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XmHttp.getDefault().init(getApplicationContext());
    }
}
