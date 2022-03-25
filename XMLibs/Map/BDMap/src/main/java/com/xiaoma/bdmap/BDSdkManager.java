package com.xiaoma.bdmap;

import android.content.Context;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.xiaoma.mapadapter.interfaces.ISdkInitializer;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDSdkManager implements ISdkInitializer {
    @Override
    public void init(final Context context) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                SDKInitializer.initialize(context);
                SDKInitializer.setCoordType(CoordType.GCJ02);
            }
        });
    }
}
