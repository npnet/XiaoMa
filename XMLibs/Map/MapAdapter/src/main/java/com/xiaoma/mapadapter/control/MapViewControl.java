package com.xiaoma.mapadapter.control;

import android.content.Context;

import com.xiaoma.mapadapter.interfaces.IMapView;


/**
 * Created by minxiwen on 2017/12/15 0015.
 */

public abstract class MapViewControl {
    public abstract IMapView getView(Context context);
    public abstract IMapView getTextView(Context context);
}
