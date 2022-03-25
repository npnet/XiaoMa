package com.xiaoma.gdmap;

import android.content.Context;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.interfaces.IPoiSearch;
import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDPoiSearch implements IPoiSearch {
    private com.amap.api.services.poisearch.PoiSearch poiSearch;

    public GDPoiSearch(Context context) {
        poiSearch = new PoiSearch(context, null);
    }

    @Override
    public void setOnPoiSearchListener(final OnPoiSearchListener listener) {
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int code) {
                com.xiaoma.mapadapter.model.PoiResult result = MapConverter.getInstance().convertPoiResult(poiResult);
                listener.onPoiSearched(result, code);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
    }

    @Override
    public void setBound(QueryBound queryBound) {
        poiSearch.setBound((PoiSearch.SearchBound) MapConverter.getInstance().convertQueryBound(queryBound));
    }

    @Override
    public void doPoiSearch() {
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void setQueryOption(QueryOption option) {
        poiSearch.setQuery((PoiSearch.Query) MapConverter.getInstance().convertQueryOption(option));
    }

    @Override
    public void destroy() {

    }
}
