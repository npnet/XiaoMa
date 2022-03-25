package com.xiaoma.bdmap;

import android.content.Context;

import com.xiaoma.mapadapter.control.PoiSearchControl;
import com.xiaoma.mapadapter.interfaces.IPoiSearch;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDPoiSearchControl extends PoiSearchControl {
    @Override
    public IPoiSearch getPoiSearch(Context context) {
        return new BDPoiSearch(context);
    }
}
