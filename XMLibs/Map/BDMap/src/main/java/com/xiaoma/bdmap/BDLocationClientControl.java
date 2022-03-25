package com.xiaoma.bdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.LocationClientControl;
import com.xiaoma.mapadapter.interfaces.ILocation;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDLocationClientControl extends LocationClientControl {
    @Override
    public ILocation getLocationClient(Context context) {
        return new BDLocationClient(context);
    }
}
