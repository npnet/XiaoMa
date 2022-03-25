package com.xiaoma.bdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.MapViewControl;
import com.xiaoma.mapadapter.interfaces.IMapView;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDMapViewControl extends MapViewControl {
    @Override
    public IMapView getView(Context context) {
        return new BDMapView(context);
    }

    @Override
    public IMapView getTextView(Context context) {
        return null;
    }
}
