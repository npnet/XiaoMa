package com.xiaoma.oilconsumption;

import android.app.Application;

import com.xiaoma.network.XmHttp;

/**
 * Created by Thomas on 2019/5/23 0023
 */

public class OilConsumption extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XmHttp.getDefault().init(getApplicationContext());
    }

}
