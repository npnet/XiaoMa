package com.mapbar.android.mapbarnavi;
import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.android.mapbarnavi.D058AidlCallback;
import java.util.List;

interface D058AidlInterface{
    int showMap();
    int showPoiDetail(double lon, double lat);
    int startNaviToPoi(in PoiBean destination);
    int startNaviToHome();
    int startNaviToCompany();
    int setRouteAvoidType(int type);
    int cancelNavi();
    int chooseRoutePlan(int plan);
    int startNavi();
    int addViaPoint(in PoiBean viaPoint);
    int deleteViaPoint(int position);
    int searchByKey(String searchKey);
    int searchNearByKey(String searchKey, double lon, double lat);
    int getNaviShowState();

    PoiBean getCarPosition();
    int searchAndShowResult(String searchKey);
    int showTmc(boolean show);
    int setMapZoomIn();
    int setMapZoomOut();
    int setNaviShowState(int state);
    int switchRouteOverview();
    int setCameraBroadcastType(int type);
    int addCollection(in PoiBean poi);
    List<PoiBean> getCollections();
    int setHome(in PoiBean home);
    int setCompany(in PoiBean company);
    int showHomeSettingPage();
    int showCompanySettingPage();
    int showCarPosition();
    int showTmcForPoi(double lon, double lat);
    int getRemainingDistance();
    int getRemainingTime();
    String getNextRoadName();
    int getDistanceToNextRoad();
    int showLimitInformation();
    int naviBroadcast();
    boolean isNaviMuted();
    int setNaviMuted(boolean mute);

    boolean isRouteOverview();
    int getRoutePlanSize();
    int searchByRoute(String searchKey);

    int setEnableTTSPlay(boolean enable);
    int startNaviWithViaPoint(in PoiBean destination, in PoiBean viaPoint);

    void registerCallback(D058AidlCallback callback);
    void unregisterCallback(D058AidlCallback callback);

    void setICMapMode(int mode);
    void setICARVisible(boolean visible);
    void setZoomMode(int mode);
    boolean isNaviEngineInited();
}