package com.xiaoma.bdmap;

import android.content.Context;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.platform.comapi.util.PermissionCheck;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IGeocoderSearch;
import com.xiaoma.mapadapter.listener.OnGeocodeSearchListener;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;
import com.xiaoma.utils.log.KLog;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDGeocodeSearch implements IGeocoderSearch {
    private GeoCoder geoCoder;

    public BDGeocodeSearch(Context context) {
        geoCoder = GeoCoder.newInstance();
    }

    @Override
    public void setOnGeocodeSearchListener(final OnGeocodeSearchListener listener) {
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                int resultCode = 0;
                if (reverseGeoCodeResult.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED || reverseGeoCodeResult.error == SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR) {
                    KLog.e("onGetReverseGeoCodeResult restart permission check");
                    PermissionCheck.permissionCheck();
                } else if (reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    resultCode = MapConstant.SEARCH_SUCCESS;
                } else if (reverseGeoCodeResult.error == SearchResult.ERRORNO.NETWORK_ERROR || reverseGeoCodeResult.error == SearchResult.ERRORNO.NETWORK_TIME_OUT) {
                    resultCode = MapConstant.SEARCH_NO_NETWORK;
                } else {
                    resultCode = MapConstant.SEARCH_FAIL;
                }
                listener.onRegeocodeSearched(MapConverter.getInstance().convertRegeocodeResult(reverseGeoCodeResult), resultCode);
            }
        });
    }

    @Override
    public void getFromLocationAsyn(RegeocodeQueryOption option) {
        geoCoder.reverseGeoCode((ReverseGeoCodeOption) MapConverter.getInstance().convertRegeocodeQueryOption(option));
    }

    @Override
    public void destroy() {
        geoCoder.destroy();
    }
}
