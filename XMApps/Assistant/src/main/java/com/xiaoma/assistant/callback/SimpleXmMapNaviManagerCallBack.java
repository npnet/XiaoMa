package com.xiaoma.assistant.callback;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/7/9 19:41
 * Desc:
 */
public class SimpleXmMapNaviManagerCallBack implements XmMapNaviManagerCallBack {

    @Override
    public void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onNaviStatusChanged(int status, PoiBean startPoi, PoiBean endPoi) {

    }

    @Override
    public void onCarPositionChanged(PoiBean currentPoi) {

    }

    @Override
    public void onNaviShowStateChanged(int state) {

    }

    @Override
    public void onSearchByRouteResult(String searchKey, int errorCode, List<PoiBean> searchResults) {

    }

    @Override
    public void onNaviTracking(int turnId, int distanceToTurn, int turnToStart) {

    }
}
