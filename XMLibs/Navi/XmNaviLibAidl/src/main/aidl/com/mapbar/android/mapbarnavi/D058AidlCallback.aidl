package com.mapbar.android.mapbarnavi;
import com.mapbar.android.mapbarnavi.PoiBean;
import java.util.List;

interface D058AidlCallback{
    void onSearchResult(String searchKey, int errorCode, in List<PoiBean> searchResults);
    void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, in List<PoiBean> searchResults);
    void onNaviStatusChanged(int status, in PoiBean startPoi, in PoiBean endPoi);
    void onCarPositionChanged(in PoiBean currentPoi);
    void onNaviShowStateChanged(int state);
    void onSearchByRouteResult(String searchKey, int errorCode, in List<PoiBean> searchResults);
    void onNaviTracking(int turnId, int distanceToTurn, int turnToStart);
    void onNaviEngineInited();
}