package com.xiaoma.dualscreen.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * <pre>
 *     author : Lai
 *     time   : 2019/05/13
 *     desc   :
 * </pre>
 */
public class DualScreenMapManager {
    public XmMapNaviManager mXmMapNaviManager;

    private DualScreenMapManager() {

    }

    public static class MapManagerHolder {
        static DualScreenMapManager instance = new DualScreenMapManager();
    }

    public static DualScreenMapManager getInstance() {
        return MapManagerHolder.instance;
    }

    public void init(Context context) {
        KLog.i("DualScreenMapManager init()");
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
        KLog.d("MapManager setXmMapNaviManagerCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setXmMapNaviManagerCallBack(callBack);
        }
    }

    public void removeXmMapNaviManagerCallBack(XmMapNaviManagerCallBack callBack) {
        KLog.d("MapManager removeXmMapNaviManagerCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.removeXmMapNaviManagerCallBack(callBack);
        }
    }

    public void clearAllCallBack() {
        KLog.d("MapManager clearAllCallBack()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.clearAllCallBack();
        }
    }

    public int showMap() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showMap();
            KLog.d("MapManager showMap() ret=" + ret);
        }
        return ret;
    }

    public int showPoiDetail(double lon, double lat) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showPoiDetail(lon, lat);
            KLog.d("MapManager showPoiDetail() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToPoi(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToPoi(name, address, longitude, latitude);
            KLog.d("MapManager naviToPoi() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToHome() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToHome();
            KLog.d("MapManager startNaviToHome() ret=" + ret);
        }
        return ret;
    }

    public int startNaviToCompany() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNaviToCompany();
            KLog.d("MapManager startNaviToCompany() ret=" + ret);
        }
        return ret;
    }

    public int setRouteAvoidType(int type) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setRouteAvoidType(type);
            KLog.d("MapManager setRouteAvoidType() ret=" + ret);
        }
        return ret;
    }

    public int cancelNavi() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.cancelNavi();
            KLog.d("MapManager cancelNavi() ret=" + ret);
        }
        return ret;
    }

    public int chooseRoutePlan(int plan) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.chooseRoutePlan(plan);
            KLog.d("MapManager chooseRoutePlan() ret=" + ret);
        }
        return ret;
    }

    public int startNavi() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.startNavi();
            KLog.d("MapManager startNavi() ret=" + ret);
        }
        return ret;
    }

    public int addViaPoint(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.addViaPoint(name, address, longitude, latitude);
            KLog.d("MapManager addViaPoint() ret=" + ret);
        }
        return ret;
    }

    public int deleteViaPoint(int position) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.deleteViaPoint(position);
            KLog.d("MapManager deleteViaPoint() ret=" + ret);
        }
        return ret;
    }

    public void searchByKey(String searchKey) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.searchByKey(searchKey);
            KLog.d("MapManager searchByKey()");
        }
    }

    public void searchNearByKey(String searchKey, double lon, double lat) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.searchNearByKey(searchKey, lon, lat);
            KLog.d("MapManager searchNearByKey()");
        }
    }

    public int getNaviShowState() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getNaviShowState();
            KLog.d("MapManager getNaviShowState() ret=" + ret);
        }
        return ret;
    }

    //V2.0 start
    public PoiBean getCarPosition() {
        PoiBean ret = null;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getCarPosition();
            KLog.d("MapManager getCarPosition() ret=" + ret);
        }
        return ret;
    }

    public int searchAndShowResult(String searchKey) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.searchAndShowResult(searchKey);
            KLog.d("MapManager searchAndShowResult() ret=" + ret);
        }
        return ret;
    }

    public int showTmc(boolean show) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showTmc(show);
            KLog.d("MapManager showTmc() ret=" + ret);
        }
        return ret;
    }

    //放大地图
    public int setMapZoomIn() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setMapZoomIn();
            KLog.d("MapManager setMapZoomIn() ret=" + ret);
        }
        return ret;
    }

    //缩小地图
    public int setMapZoomOut() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setMapZoomOut();
            KLog.d("MapManager setMapZoomOut() ret=" + ret);
        }
        return ret;
    }

    //设置导航显示模式,0：2D北向上,1：2D车头向上,2：3D模式,3：AR模式
    public int setNaviShowState(int state) {
        KLog.e("设置地图模式：" + state);
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setNaviShowState(state);
            KLog.d("MapManager setNaviShowState() ret=" + ret);
        }
        return ret;
    }

    public int switchRouteOverview() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.switchRouteOverview();
            KLog.d("MapManager switchRouteOverview() ret=" + ret);
        }
        return ret;
    }

    public int setCameraBroadcastType(int type) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setCameraBroadcastType(type);
            KLog.d("MapManager setCameraBroadcastType() ret=" + ret);
        }
        return ret;
    }

    public int addCollection(PoiBean poi) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.addCollection(poi);
            KLog.d("MapManager addCollection() ret=" + ret);
        }
        return ret;
    }

    public List<PoiBean> getCollections() {
        List<PoiBean> ret = null;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getCollections();
            KLog.d("MapManager getCollections() ret=" + ret);
        }
        return ret;
    }

    public int setHome(PoiBean home) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setHome(home);
            KLog.d("MapManager setHome() ret=" + ret);
        }
        return ret;
    }

    public int setCompany(PoiBean company) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setCompany(company);
            KLog.d("MapManager setCompany() ret=" + ret);
        }
        return ret;
    }

    public int showHomeSettingPage() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showHomeSettingPage();
            KLog.d("MapManager showHomeSettingPage() ret=" + ret);
        }
        return ret;
    }

    public int showCompanySettingPage() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showCompanySettingPage();
            KLog.d("MapManager showCompanySettingPage() ret=" + ret);
        }
        return ret;
    }

    public int showCarPosition() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showCarPosition();
            KLog.d("MapManager showCarPosition() ret=" + ret);
        }
        return ret;
    }

    public int showTmcForPoi(double lon, double lat) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showTmcForPoi(lon, lat);
            KLog.d("MapManager showTmcForPoi() ret=" + ret);
        }
        return ret;
    }

    public int getRemainingDistance() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getRemainingDistance();
            KLog.d("MapManager getRemainingDistance() ret=" + ret);
        }
        return ret;
    }

    public int getRemainingTime() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getRemainingTime();
            KLog.d("MapManager getRemainingTime() ret=" + ret);
        }
        return ret;
    }

    public String getNextRoadName() {
        String ret = null;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getNextRoadName();
            KLog.d("MapManager getNextRoadName() ret=" + ret);
        }
        return ret;
    }

    public int getDistanceToNextRoad() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getDistanceToNextRoad();
            KLog.d("MapManager getDistanceToNextRoad() ret=" + ret);
        }
        return ret;
    }

    public int showLimitInformation() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.showLimitInformation();
            KLog.d("MapManager showLimitInformation() ret=" + ret);
        }
        return ret;
    }

    public int naviBroadcast() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.naviBroadcast();
            KLog.d("MapManager naviBroadcast() ret=" + ret);
        }
        return ret;
    }

    public boolean isNaviMuted() {
        boolean ret = false;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.isNaviMuted();
            KLog.d("MapManager isNaviMuted() ret=" + ret);
        }
        return ret;
    }

    public boolean isNaviing() {
        boolean ret = false;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.isNaviing();
            KLog.d("MapManager isNaviing() ret=" + ret);
        }
        return ret;
    }

    public boolean isNaviingV2() {
        boolean ret = false;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.isNaviingV2();
            KLog.d("MapManager isNaviing() ret=" + ret);
        }
        return ret;
    }


    public int setNaviMuted(boolean mute) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.setNaviMuted(mute);
            KLog.d("MapManager setNaviMuted() ret=" + ret);
        }
        return ret;
    }
    //V2.0 end

    //V4.0 start

    public boolean isRouteOverview() {
        boolean ret = false;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.isRouteOverview();
            KLog.d("MapManager isRouteOverview() ret=" + ret);
        }
        return ret;
    }

    public int getRoutePlanSize() {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.getRoutePlanSize();
            KLog.d("MapManager getRoutePlanSize() ret=" + ret);
        }
        return ret;
    }

    public int searchByRoute(String searchKey) {
        int ret = -1;
        if (mXmMapNaviManager != null) {
            ret = mXmMapNaviManager.searchByRoute(searchKey);
            KLog.d("MapManager searchByRoute() ret=" + ret);
        }
        return ret;
    }

    //V4.0 end
    public boolean setICMapMode(int mode) {
        if (mXmMapNaviManager != null) {
            KLog.e("setICMapMode=" + mode);
            return mXmMapNaviManager.setICMapMode(mode);
        } else {
            KLog.e("mXmMapNaviManager=" + mXmMapNaviManager);
        }
        return false;
    }

    //V4.0 end
    public boolean isNaviEngineenInited() {
        if (mXmMapNaviManager != null) {
            boolean result = mXmMapNaviManager.isNaviEngineenInited();
            KLog.e("isNaviEngineenInited result = "+result);
            return result;
        } else {
            KLog.e("mXmMapNaviManager=" + mXmMapNaviManager);
        }
        return false;
    }

    //设置仪表盘AR是否显示
    public void setICARVisible(boolean visible) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setICARVisible(visible);
            KLog.d("MapManager setICARVisible()");
        }
    }

    //设置仪表盘地图缩放比例模式;0：正常比例尺,1：大比例尺,2：小比例尺
    public void setZoomMode(int mode) {
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.setZoomMode(mode);
            KLog.d("MapManager setZoomMode()");
        }
    }

    public void destroy() {
        KLog.d("MapManager destroy()");
        if (mXmMapNaviManager != null) {
            mXmMapNaviManager.destroy();
        }
    }
}
