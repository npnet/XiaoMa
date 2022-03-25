package com.xiaoma.gdmap;

import android.content.Context;
import com.xiaoma.mapadapter.control.GeoFenceClientControl;
import com.xiaoma.mapadapter.interfaces.IGeoFence;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class GDGeoFenceClientControl extends GeoFenceClientControl {
    @Override
    public IGeoFence getGeoFenceClient(Context context) {
        return new GDGeoFenceClient(context);
    }
}
