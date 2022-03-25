package com.xiaoma.gdmap;

import android.content.Context;

import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;
import com.xiaoma.mapadapter.listener.OnGeocodeSearchListener;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDGeocodeSearch implements IGeocoderSearch {
    private com.amap.api.services.geocoder.GeocodeSearch geocodeSearch;

    public GDGeocodeSearch(Context context) {
        geocodeSearch = new GeocodeSearch(context.getApplicationContext());
    }

    @Override
    public void setOnGeocodeSearchListener(final OnGeocodeSearchListener listener) {
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
                int resultCode;
                if (code == 1000) {
                    resultCode = MapConstant.SEARCH_SUCCESS;
                } else if (code == 1804) {
                    resultCode = MapConstant.SEARCH_NO_NETWORK;
                } else {
                    resultCode = MapConstant.SEARCH_FAIL;
                }
                listener.onRegeocodeSearched(MapConverter.getInstance().convertRegeocodeResult(regeocodeResult), resultCode);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }

    @Override
    public void getFromLocationAsyn(RegeocodeQueryOption option) {
        geocodeSearch.getFromLocationAsyn((RegeocodeQuery) MapConverter.getInstance().convertRegeocodeQueryOption(option));
    }

    @Override
    public void destroy() {

    }
}
