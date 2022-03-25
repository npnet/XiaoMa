package com.xiaoma.mapadapter.control;

import android.content.Context;

import com.xiaoma.mapadapter.interfaces.IGeoFence;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public abstract class GeoFenceClientControl {
    public abstract IGeoFence getGeoFenceClient(Context context);
}
