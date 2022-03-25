package com.xiaoma.gdmap;

import android.content.Context;
import android.os.RemoteException;

import com.amap.api.maps.MapsInitializer;
import com.xiaoma.mapadapter.interfaces.ISdkInitializer;

/**
 * Created by minxiwen on 2017/12/13 0013.
 */

public class GDSdkManager implements ISdkInitializer {
    @Override
    public void init(Context context) {
        try {
            MapsInitializer.initialize(context);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
