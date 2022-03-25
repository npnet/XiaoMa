package com.xiaoma.gdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.GeocodeSearchControl;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class GDGeocodeSearchControl extends GeocodeSearchControl {
    @Override
    public IGeocoderSearch getGeocodeSearch(Context context) {
        return new GDGeocodeSearch(context);
    }
}
