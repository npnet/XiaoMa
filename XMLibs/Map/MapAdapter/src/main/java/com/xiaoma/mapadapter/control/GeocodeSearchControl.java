package com.xiaoma.mapadapter.control;

import android.content.Context;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;

/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public abstract class GeocodeSearchControl {
    public abstract IGeocoderSearch getGeocodeSearch(Context context);
}
