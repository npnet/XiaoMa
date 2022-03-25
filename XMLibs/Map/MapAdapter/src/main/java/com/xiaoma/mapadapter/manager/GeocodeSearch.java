package com.xiaoma.mapadapter.manager;

import android.content.Context;

import com.xiaoma.mapadapter.control.GeocodeSearchControl;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;
import com.xiaoma.mapadapter.listener.OnGeocodeSearchListener;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;


/**
 * 对上层提供的地理/反地理编码客户端
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GeocodeSearch implements IGeocoderSearch {
    private static GeocodeSearchControl geocodeSearchControl;
    private IGeocoderSearch realGeocoderSearch;

    public GeocodeSearch(Context context) {
        realGeocoderSearch = geocodeSearchControl.getGeocodeSearch(context);
    }

    @Override
    public void setOnGeocodeSearchListener(OnGeocodeSearchListener listener) {
        realGeocoderSearch.setOnGeocodeSearchListener(listener);
    }

    @Override
    public void getFromLocationAsyn(RegeocodeQueryOption option) {
        realGeocoderSearch.getFromLocationAsyn(option);
    }

    @Override
    public void destroy() {
        realGeocoderSearch.destroy();
    }

    public static void register(GeocodeSearchControl geocodeSearchControl1) {
        geocodeSearchControl = geocodeSearchControl1;
    }
}
