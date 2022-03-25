package com.mapbar.xiaoma.callback;

import com.mapbar.android.mapbarnavi.PoiBean;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/04/23
 *     desc   :
 * </pre>
 */
public interface XmMapNaviManagerCallBack {

    void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults);
    void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, List<PoiBean> searchResults);
    void onNaviStatusChanged(int status, PoiBean startPoi, PoiBean endPoi);
    void onCarPositionChanged(PoiBean currentPoi);
    void onNaviShowStateChanged(int state);
    void onSearchByRouteResult(String searchKey, int errorCode, List<PoiBean> searchResults);
    void onNaviTracking(int turnId, int distanceToTurn, int turnToStart);
}
