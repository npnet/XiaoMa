package com.xiaoma.bdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.GeoFenceClientControl;
import com.xiaoma.mapadapter.interfaces.IGeoFence;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDGeoFenceClientControl extends GeoFenceClientControl {
    @Override
    public IGeoFence getGeoFenceClient(Context context) {
        return new BDFenceClient(context);
    }
}
