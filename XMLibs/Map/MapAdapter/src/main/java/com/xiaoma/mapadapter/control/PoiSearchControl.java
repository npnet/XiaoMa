package com.xiaoma.mapadapter.control;

import android.content.Context;

import com.xiaoma.mapadapter.interfaces.IPoiSearch;


/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public abstract class PoiSearchControl {
    public abstract IPoiSearch getPoiSearch(Context context);
}
