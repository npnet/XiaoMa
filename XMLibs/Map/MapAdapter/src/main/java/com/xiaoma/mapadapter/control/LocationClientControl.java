package com.xiaoma.mapadapter.control;

import android.content.Context;

import com.xiaoma.mapadapter.interfaces.ILocation;


/**
 * Created by minxiwen on 2017/12/15 0015.
 */

public abstract class LocationClientControl {
    public abstract ILocation getLocationClient(Context context);
}
