package com.xiaoma.mapadapter.manager;

import android.content.Context;

import com.xiaoma.mapadapter.control.PoiSearchControl;
import com.xiaoma.mapadapter.interfaces.IPoiSearch;
import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;


/**
 * 封装的一个给上层使用的Poi搜索客户端
 * Created by minxiwen on 2017/12/12 0012.
 */

public class PoiSearch implements IPoiSearch {
    private static PoiSearchControl poiSearchControl;
    private IPoiSearch realPoiSearch;

    public PoiSearch(Context context) {
        realPoiSearch = poiSearchControl.getPoiSearch(context);
//        if (MapConstant.mapType == MapType.MAP_GD) {
//            realPoiSearch = new GDPoiSearch(context);
//        } else if (MapConstant.mapType == MapType.MAP_BD) {
////            realPoiSearch = new BDPoiSearch(context);
//        }
    }

    @Override
    public void setOnPoiSearchListener(OnPoiSearchListener listener) {
        realPoiSearch.setOnPoiSearchListener(listener);
    }

    @Override
    public void setBound(QueryBound queryBound) {
        realPoiSearch.setBound(queryBound);
    }

    @Override
    public void doPoiSearch() {
        realPoiSearch.doPoiSearch();
    }

    @Override
    public void setQueryOption(QueryOption option) {
        realPoiSearch.setQueryOption(option);
    }

    @Override
    public void destroy() {
        realPoiSearch.destroy();
    }

    public static void registerPoiSearch(PoiSearchControl poiSearchControl1) {
        poiSearchControl = poiSearchControl1;
    }
}
