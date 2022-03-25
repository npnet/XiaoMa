package com.xiaoma.bdmap;

import android.content.Context;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.platform.comapi.util.PermissionCheck;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IPoiSearch;
import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;
import com.xiaoma.utils.log.KLog;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDPoiSearch implements IPoiSearch {
    private com.baidu.mapapi.search.poi.PoiSearch poiSearch;
    private PoiCitySearchOption poiCitySearchOption;

    public BDPoiSearch(Context context) {
        poiSearch = PoiSearch.newInstance();
    }

    @Override
    public void setOnPoiSearchListener(final OnPoiSearchListener listener) {
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                int resultCode = 0;
                if (poiResult.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED || poiResult.error == SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR) {
                    KLog.e("onGetReverseGeoCodeResult restart permission check");
                    PermissionCheck.permissionCheck();
                } else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    resultCode = MapConstant.SEARCH_SUCCESS;
                } else if (poiResult.error == SearchResult.ERRORNO.NETWORK_ERROR || poiResult.error == SearchResult.ERRORNO.NETWORK_TIME_OUT) {
                    resultCode = MapConstant.SEARCH_NO_NETWORK;
                } else {
                    resultCode = MapConstant.SEARCH_FAIL;
                }
                listener.onPoiSearched(MapConverter.getInstance().convertPoiResult(poiResult), resultCode);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }

    @Override
    public void setBound(QueryBound queryBound) {

    }

    @Override
    public void doPoiSearch() {
        poiSearch.searchInCity(poiCitySearchOption);
    }

    @Override
    public void setQueryOption(QueryOption option) {
        poiCitySearchOption = new PoiCitySearchOption();
        poiCitySearchOption.city(option.getCity())
                .keyword(option.getQueryContent())
                .pageCapacity(option.getPageSize())
                .pageNum(option.getPageNum());
    }

    @Override
    public void destroy() {
        poiSearch.destroy();
    }
}
