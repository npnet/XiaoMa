package com.xiaoma.gdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.LocationClientControl;
import com.xiaoma.mapadapter.interfaces.ILocation;


/**
 * Created by minxiwen on 2017/12/15 0015.
 */

public class GDLocationClientControl extends LocationClientControl {
    @Override
    public ILocation getLocationClient(Context context) {
        return new GDLocationClient(context);
    }
}
