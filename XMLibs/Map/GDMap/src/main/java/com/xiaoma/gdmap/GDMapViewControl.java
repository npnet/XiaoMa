package com.xiaoma.gdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.MapViewControl;
import com.xiaoma.mapadapter.interfaces.IMapView;


/**
 * Created by minxiwen on 2017/12/15 0015.
 */

public class GDMapViewControl extends MapViewControl {
    @Override
    public IMapView getView(Context context) {
        return new GDMapView(context);
    }

    @Override
    public IMapView getTextView(Context context) {
        return new GDMapTextureView(context);
    }
}
