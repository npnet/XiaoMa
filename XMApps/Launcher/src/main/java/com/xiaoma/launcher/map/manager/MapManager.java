package com.xiaoma.launcher.map.manager;

import android.content.Context;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/04/23
 *     desc   :
 * </pre>
 */
public class MapManager {
    public String TAG = "MapManager";
    public XmMapNaviManager mXmMapNaviManager;

    private MapManager() {
    }

    public static class MapManagerHolder {
        static MapManager instance = new MapManager();
    }

    public static MapManager getInstance() {
        return MapManagerHolder.instance;
    }

    public void init(Context context) {
        KLog.i(TAG, "MapManager init()");
        mXmMapNaviManager = XmMapNaviManager.getInstance();
        mXmMapNaviManager.init(context);
    }

    public XmMapNaviManager getmXmMapNaviManager() {
        return mXmMapNaviManager;
    }

    public boolean isInitSuccess() {
        return mXmMapNaviManager.isConnectNaviService();
    }

    public void setXmMapNaviManagerCallBack(XmMapNaviManagerCallBack callBack) {
        KLog.d(TAG, "MapManager setXmMapNaviManagerCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setXmMapNaviManagerCallBack(callBack);
        }
    }

    public void removeXmMapNaviManagerCallBack(XmMapNaviManagerCallBack callBack) {
        KLog.d(TAG, "MapManager removeXmMapNaviManagerCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.removeXmMapNaviManagerCallBack(callBack);
        }
    }

    public void clearAllCallBack() {
        KLog.d(TAG, "MapManager clearAllCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.clearAllCallBack();
        }
    }

    public int showMap() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showMap();
            KLog.d(TAG, "MapManager showMap() ret=" + ret);
        }
        return ret;
    }

    public int showPoiDetail(double lon, double lat) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showPoiDetail(lon, lat);
            KLog.d(TAG, "MapManager showPoiDetail() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToPoi(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToPoi(name, address, longitude, latitude);
            KLog.d(TAG, "MapManager naviToPoi() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToHome() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToHome();
            KLog.d(TAG, "MapManager startNaviToHome() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToCompany() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToCompany();
            KLog.d(TAG, "MapManager startNaviToCompany() ret=" + ret);
        }
        return ret;
    }

    public int setRouteAvoidType(int type) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setRouteAvoidType(type);
            KLog.d(TAG, "MapManager setRouteAvoidType() ret=" + ret);
        }
        return ret;
    }

    public int cancelNavi() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.cancelNavi();
            KLog.d(TAG, "MapManager cancelNavi() ret=" + ret);
        }
        return ret;
    }

    public int chooseRoutePlan(int plan) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.chooseRoutePlan(plan);
            KLog.d(TAG, "MapManager chooseRoutePlan() ret=" + ret);
        }
        return ret;
    }

    public int startNavi() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNavi();
            KLog.d(TAG, "MapManager startNavi() ret=" + ret);
        }
        return ret;
    }

    public int addViaPoint(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.addViaPoint(name, address, longitude, latitude);
            KLog.d(TAG, "MapManager addViaPoint() ret=" + ret);
        }
        return ret;
    }

    public int deleteViaPoint(int position) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.deleteViaPoint(position);
            KLog.d(TAG, "MapManager deleteViaPoint() ret=" + ret);
        }
        return ret;
    }

    public void searchByKey(String searchKey) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.searchByKey(searchKey);
            KLog.d(TAG, "MapManager searchByKey()");
        }
    }

    public void searchNearByKey(String searchKey, double lon, double lat) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.searchNearByKey(searchKey, lon, lat);
            KLog.d(TAG, "MapManager searchNearByKey()");
        }
    }

    public int getNaviShowState() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getNaviShowState();
            KLog.d(TAG, "MapManager getNaviShowState() ret=" + ret);
        }
        return ret;
    }

    //V2.0 start
    public PoiBean getCarPosition(){
        PoiBean ret=null;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getCarPosition();
            KLog.d(TAG, "MapManager getCarPosition() ret=" + ret);
        }
        return ret;
    }
    public int searchAndShowResult(String searchKey){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.searchAndShowResult(searchKey);
            KLog.d(TAG, "MapManager searchAndShowResult() ret=" + ret);
        }
        return ret;
    }
    public int showTmc(boolean show){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showTmc(show);
            KLog.d(TAG, "MapManager showTmc() ret=" + ret);
        }
        return ret;
    }

    //放大地图
    public int setMapZoomIn(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setMapZoomIn();
            KLog.d(TAG, "MapManager setMapZoomIn() ret=" + ret);
        }
        return ret;
    }
    //缩小地图
    public int setMapZoomOut(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setMapZoomOut();
            KLog.d(TAG, "MapManager setMapZoomOut() ret=" + ret);
        }
        return ret;
    }
    //设置导航显示模式,0：2D北向上,1：2D车头向上,2：3D模式,3：AR模式
    public int setNaviShowState(int state){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setNaviShowState(state);
            KLog.d(TAG, "MapManager setNaviShowState() ret=" + ret);
        }
        return ret;
    }
    public int switchRouteOverview(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.switchRouteOverview();
            KLog.d(TAG, "MapManager switchRouteOverview() ret=" + ret);
        }
        return ret;
    }
    public int setCameraBroadcastType(int type){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setCameraBroadcastType(type);
            KLog.d(TAG, "MapManager setCameraBroadcastType() ret=" + ret);
        }
        return ret;
    }
    public int addCollection(PoiBean poi){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.addCollection(poi);
            KLog.d(TAG, "MapManager addCollection() ret=" + ret);
        }
        return ret;
    }
    public List<PoiBean> getCollections(){
        List<PoiBean> ret=null;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getCollections();
            KLog.d(TAG, "MapManager getCollections() ret=" + ret);
        }
        return ret;
    }
    public int setHome(PoiBean home){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setHome(home);
            KLog.d(TAG, "MapManager setHome() ret=" + ret);
        }
        return ret;
    }
    public int setCompany(PoiBean company){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setCompany(company);
            KLog.d(TAG, "MapManager setCompany() ret=" + ret);
        }
        return ret;
    }
    public int showHomeSettingPage(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showHomeSettingPage();
            KLog.d(TAG, "MapManager showHomeSettingPage() ret=" + ret);
        }
        return ret;
    }

    public int showCompanySettingPage(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showCompanySettingPage();
            KLog.d(TAG, "MapManager showCompanySettingPage() ret=" + ret);
        }
        return ret;
    }
    public int showCarPosition(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showCarPosition();
            KLog.d(TAG, "MapManager showCarPosition() ret=" + ret);
        }
        return ret;
    }
    public int showTmcForPoi(double lon, double lat){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showTmcForPoi(lon,lat);
            KLog.d(TAG, "MapManager showTmcForPoi() ret=" + ret);
        }
        return ret;
    }
    public int getRemainingDistance(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getRemainingDistance();
            KLog.d(TAG, "MapManager getRemainingDistance() ret=" + ret);
        }
        return ret;
    }
    public int getRemainingTime(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getRemainingTime();
            KLog.d(TAG, "MapManager getRemainingTime() ret=" + ret);
        }
        return ret;
    }
    public String getNextRoadName(){
        String ret=null;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getNextRoadName();
            KLog.d(TAG, "MapManager getNextRoadName() ret=" + ret);
        }
        return ret;
    }
    public int getDistanceToNextRoad(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getDistanceToNextRoad();
            KLog.d(TAG, "MapManager getDistanceToNextRoad() ret=" + ret);
        }
        return ret;
    }
    public int showLimitInformation(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.showLimitInformation();
            KLog.d(TAG, "MapManager showLimitInformation() ret=" + ret);
        }
        return ret;
    }
    public int naviBroadcast(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.naviBroadcast();
            KLog.d(TAG, "MapManager naviBroadcast() ret=" + ret);
        }
        return ret;
    }
    public boolean isNaviMuted(){
        boolean ret=false;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.isNaviMuted();
            KLog.d(TAG, "MapManager isNaviMuted() ret=" + ret);
        }
        return ret;
    }
    public int setNaviMuted(boolean mute){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setNaviMuted(mute);
            KLog.d(TAG, "MapManager setNaviMuted() ret=" + ret);
        }
        return ret;
    }
    //V2.0 end

    //V4.0 start

    public boolean isRouteOverview(){
        boolean ret=false;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.isRouteOverview();
            KLog.d(TAG, "MapManager isRouteOverview() ret=" + ret);
        }
        return ret;
    }

    public int getRoutePlanSize(){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.getRoutePlanSize();
            KLog.d(TAG, "MapManager getRoutePlanSize() ret=" + ret);
        }
        return ret;
    }

    public int searchByRoute(String searchKey){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.searchByRoute(searchKey);
            KLog.d(TAG, "MapManager searchByRoute() ret=" + ret);
        }
        return ret;
    }

    public int setEnableTTSPlay(boolean enable){
        int ret=-1;
        if(mXmMapNaviManager!=null){
            ret=mXmMapNaviManager.setEnableTTSPlay(enable);
            KLog.d(TAG, "MapManager setEnableTTSPlay() ret=" + ret);
        }
        return ret;
    }

    //V4.0 end
    public void setICMapMode(int mode) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setICMapMode(mode);
            KLog.d(TAG, "MapManager setICMapMode()");
        }
    }

    //设置仪表盘AR是否显示
    public void setICARVisible(boolean visible) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setICARVisible(visible);
            KLog.d(TAG, "MapManager setICARVisible()");
        }
    }

    //设置仪表盘地图缩放比例模式
    public void setZoomMode(int mode) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setZoomMode(mode);
            KLog.d(TAG, "MapManager setZoomMode()");
        }
    }

    public void destroy() {
        KLog.d(TAG, "MapManager destroy()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.destroy();
        }
    }
}
