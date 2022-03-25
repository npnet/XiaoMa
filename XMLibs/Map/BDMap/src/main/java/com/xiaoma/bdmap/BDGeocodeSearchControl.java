package com.xiaoma.bdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.GeocodeSearchControl;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDGeocodeSearchControl extends GeocodeSearchControl {
    @Override
    public IGeocoderSearch getGeocodeSearch(Context context) {
        return new BDGeocodeSearch(context);
    }
}
