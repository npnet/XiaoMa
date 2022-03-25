package com.mapbar.xiaoma.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mapbar.android.mapbarnavi.D058AidlCallback;
import com.mapbar.android.mapbarnavi.D058AidlInterface;
import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.Receiver.XmNaviReceiver;
import com.mapbar.xiaoma.callback.IMapAidlConnectListen;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.mapbar.xiaoma.constant.NaviConstants;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/4/22
 */
public class XmMapNaviManager {
    public static final String TAG = "[XmMapNaviManager]";
    private D058AidlInterface mService;
    private List<XmMapNaviManagerCallBack> listCallBack = new ArrayList<XmMapNaviManagerCallBack>();
    private IMapAidlConnectListen mapAidlConnectListen;
    private boolean isNaviForeground = true;
    private int currentStatus = -1;
    WeakReference<Context> weakReference;

    private XmMapNaviManager() {
    }

    public static class XmMapNaviManagerHolder {
        public final static XmMapNaviManager instance = new XmMapNaviManager();
    }

    public static XmMapNaviManager getInstance() {
        return XmMapNaviManagerHolder.instance;
    }

    public void init(Context context) {
        KLog.d(TAG, "XmMapNaviManager init()");
        Log.e("jun", "XmMapNaviManager init");
        weakReference=new WeakReference<>(context);
        connectRemoteService();
        XmNaviReceiver.initXmNaviReceiver(weakReference.get());
    }

    /**
     * 导航页面是否在前台展示
     *
     * @return
     */
    public boolean isNaviForeground() {
        return isNaviForeground;
    }

    public boolean isConnectNaviService() {
        return mService != null;
    }

    public void setNaviForeground(boolean naviForeground) {
        isNaviForeground = naviForeground;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(mapAidlConnectListen != null) mapAidlConnectListen.disConnected();
            //取消死亡监听
            if (mService != null) {
                mService.asBinder().unlinkToDeath(mDeathRecipient, 0);
                //释放资源
                mService = null;
            }
            //重新连接服务
            connectRemoteService();
        }
    };

    private void connectRemoteService() {
        Log.e("jun", TAG + " connectRemoteService");
        Intent intent = new Intent(NaviConstants.BINDER_NAVI_MAP_SERVICE);
        intent.setPackage(NaviConstants.LAUNCHER_PACKAGE_NAME);
        Context mContext=weakReference.get();
        if(mContext!=null){
            boolean flag = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.e("jun", TAG + " connectRemoteService flag=" + flag);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            KLog.d(TAG, "ServiceConnection.onServiceConnected()");
            Log.e("jun", TAG + " ServiceConnection.onServiceConnected()");
            mService = D058AidlInterface.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (Exception e) {
                KLog.e("registerCall error " + e);
            }
            //绑定死亡监听
            try {
                if (mService != null) {
                    mService.asBinder().linkToDeath(mDeathRecipient, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(mapAidlConnectListen != null) mapAidlConnectListen.connected();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            if(mapAidlConnectListen != null) mapAidlConnectListen.disConnected();
            KLog.d(TAG, "ServiceConnection.onServiceDisconnected()");
            Log.e("jun", TAG + " ServiceConnection.onServiceDisconnected()");
        }
    };

    public void setMapAidlConnectListen(IMapAidlConnectListen mapAidlConnectListen) {
        this.mapAidlConnectListen = mapAidlConnectListen;
    }

    public void setXmMapNaviManagerCallBack(XmMapNaviManagerCallBack callBack) {
        KLog.d(TAG, "XmMapNaviManager setXmMapNaviManagerCallBack()");
        listCallBack.add(callBack);
    }

    public void removeXmMapNaviManagerCallBack(XmMapNaviManagerCallBack callBack) {
        KLog.d(TAG, "XmMapNaviManager removeXmMapNaviManagerCallBack()");
        listCallBack.remove(callBack);
    }

    public void clearAllCallBack() {
        KLog.d(TAG, "XmMapNaviManager clearAllCallBack()");
        listCallBack.clear();
    }

    /**
     * 显示地图首页
     *
     * @return 0：成功；-1：地图初始化未完成；-2：正在导航中
     */
    public int showMap() {
        KLog.d(TAG, "XmMapNaviManager showMap()");
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showMap();
                KLog.d(TAG, "showMap ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 根据经纬度查看poi
     *
     * @param lon 经度
     * @param lat 纬度
     * @return 0：成功；-1：地图初始化未完成；-2：正在导航中；-3：获取POI信息失败
     */
    public int showPoiDetail(double lon, double lat) {
        KLog.d(TAG, "XmMapNaviManager showPoiDetail()");
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showPoiDetail(lon, lat);
                KLog.d(TAG, "showPoiDetail ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 传入终点直接导航
     *
     * @param name
     * @param address
     * @param longitude
     * @param latitude
     * @return 0：成功；-1：地图初始化未完成
     */
    public int startNaviToPoi(String name, String address, double longitude, double latitude) {
        KLog.e(TAG, "startNaviToPoi() name= "+name+" , address= "+address+" , longitude= "+longitude+" , latitude= "+latitude);
        int ret = -1;
        if (mService != null) {
            switchToLauncher();
            try {
                PoiBean poiBean = new PoiBean();
                poiBean.setName(name);
                poiBean.setAddress(address);
                poiBean.setLongitude(longitude);
                poiBean.setLatitude(latitude);
                ret = mService.startNaviToPoi(poiBean);
                KLog.d(TAG, "naviToPoi ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 导航到家
     *
     * @return 0：成功；-1：地图初始化未完成；-2：家未设置
     */
    public int startNaviToHome() {
        int ret = -1;
        if (mService != null) {
            switchToLauncher();
            try {
                ret = mService.startNaviToHome();
                KLog.d(TAG, "naviToHome ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 导航到公司
     *
     * @return 0：成功；-1：地图初始化未完成；-2：公司未设置
     */
    public int startNaviToCompany() {
        int ret = -1;
        if (mService != null) {
            switchToLauncher();
            try {
                ret = mService.startNaviToCompany();
                KLog.d(TAG, "naviToCompany ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 路线偏好设置
     *
     * @param type 参数必须为以下数值中的一个：
     *             0：智能推荐
     *             1：避让拥堵
     *             2：最短路线
     *             3：少收费
     *             4：避让高速
     *             5：高速优先
     *             6：避让轮渡
     * @return 0：成功；-1：地图初始化未完成；-2：当前非导航状态；-3：非法参数
     */
    public int setRouteAvoidType(int type) {
        KLog.e(TAG, "setRouteAvoidType() type= "+type);
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setRouteAvoidType(type);
                KLog.d(TAG, "setRouteAvoidType ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 结束导航
     *
     * @return 0：成功；-1：地图初始化未完成；-2：当前非导航状态
     */
    public int cancelNavi() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.cancelNavi();
                KLog.d(TAG, "cancelNavi ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 路线方案选择
     *
     * @param plan 路线方案序号，取值为[0, n)，n为当前路线方案总数
     * @return 0:成功；-1：地图初始化未完成；-2:当前非路线选择界面；-3：非法参数
     */
    public int chooseRoutePlan(int plan) {
        KLog.e(TAG, "chooseRoutePlan() plan= "+plan);
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.chooseRoutePlan(plan);
                KLog.d(TAG, "chooseRoutePlan ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 开始导航
     *
     * @return 0：成功；-1：地图初始化未完成；-2：当前非路线选择界面
     */
    public int startNavi() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.startNavi();
                KLog.d(TAG, "startNavi ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 添加途经点
     *
     * @param name
     * @param address
     * @param longitude
     * @param latitude
     * @return 0：成功；-1：地图初始化未完成；-2：当前非导航状态；-3：当前途径点个数已达上限（5个）
     */
    public int addViaPoint(String name, String address, double longitude, double latitude) {
        KLog.e(TAG, "addViaPoint() name= "+name+" , address= "+address+" , longitude= "+longitude+" , latitude= "+latitude);
        int ret = -1;
        if (mService != null) {
            try {
                PoiBean poiBean = new PoiBean();
                poiBean.setName(name);
                poiBean.setAddress(address);
                poiBean.setLongitude(longitude);
                poiBean.setLatitude(latitude);
                ret = mService.addViaPoint(poiBean);
                KLog.d(TAG, "addViaPoint ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 带途径点导航
     *
     * @param longitude
     * @param latitude
     * @param viaLongitude
     * @param viaLatitude
     * @return 0：成功；-1：地图初始化未完成
     */
    public int startNaviWithViaPoint(double longitude, double latitude, double viaLongitude, double viaLatitude) {
        KLog.e(TAG, "startNaviWithViaPoint() viaLongitude= "+viaLongitude+" , viaLatitude= "+viaLatitude+" , longitude= "+longitude+" , latitude= "+latitude);
        int ret = -1;
        if (mService != null) {
            try {
                PoiBean destination = new PoiBean();
                destination.setLongitude(longitude);
                destination.setLatitude(latitude);

                PoiBean viaPoint = new PoiBean();
                viaPoint.setLongitude(viaLongitude);
                viaPoint.setLatitude(viaLatitude);
                ret = mService.startNaviWithViaPoint(destination, viaPoint);
                KLog.d(TAG, "startNaviWithViaPoint ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 删除途径点
     *
     * @param position 途经点序号，取值为[0, n)，n为当前已添加途经点个数
     * @return 0：成功；-1：地图初始化未完成；-2：当前非导航状态；-3：非法参数
     */
    public int deleteViaPoint(int position) {
        KLog.e(TAG, "deleteViaPoint() position= "+position);
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.deleteViaPoint(position);
                KLog.d(TAG, "deleteViaPoint ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 关键字搜索
     *
     * @param searchKey
     * @return 0:操作执行成功；-1：地图初始化未完成。
     */
    public synchronized int searchByKey(String searchKey) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.searchByKey(searchKey);
                KLog.d(TAG, "searchByKey ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 周边搜索
     *
     * @param searchKey
     * @param lon
     * @param lat
     * @return 0:操作执行成功；-1：地图初始化未完成。
     */
    public synchronized int searchNearByKey(String searchKey, double lon, double lat) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.searchNearByKey(searchKey, lon, lat);
                KLog.d(TAG, "searchNearByKey ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取当前导航模式
     *
     * @return -1：地图初始化完成；0：2D 北向上；1：2D 车头向上；2:3D模式；3：AR模式
     */
    public int getNaviShowState() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.getNaviShowState();
                KLog.d(TAG, "getNaviShowState ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取自车位置
     *
     * @return 表示自车位置PoiBean对象。若地图初始化未完成，或获取自车位置信息失败，返回null。
     */
    public PoiBean getCarPosition() {
        PoiBean poiBean = null;
        if (mService != null) {
            try {
                poiBean = mService.getCarPosition();
                KLog.d(TAG, "getCarPosition poiBean " + poiBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return poiBean;
    }

    /**
     * 搜索并展示结果
     *
     * @param searchKey
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int searchAndShowResult(String searchKey) {
        int ret = -1;
        if (mService != null) {
            switchToLauncher();
            try {
                ret = mService.searchAndShowResult(searchKey);
                KLog.d(TAG, "searchAndShowResult ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 开关实时路况
     *
     * @param show
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int showTmc(boolean show) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showTmc(show);
                KLog.d(TAG, "showTmc ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 放大地图
     *
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setMapZoomIn() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setMapZoomIn();
                KLog.d(TAG, "setMapZoomIn ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 缩小地图
     *
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setMapZoomOut() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setMapZoomOut();
                KLog.d(TAG, "setMapZoomOut ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置导航显示模式
     *
     * @param state 0：2D北向上
     *              1：2D车头向上
     *              2：3D模式
     *              3：AR模式
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：非法参数
     */
    public int setNaviShowState(int state) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setNaviShowState(state);
                KLog.d(TAG, "setNaviShowState ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 切换地图全览
     *
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：当前非真实导航状态。
     */
    public int switchRouteOverview() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.switchRouteOverview();
                KLog.d(TAG, "switchRouteOverview ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置电子眼播报类型
     * <p>
     * 如果想同时播报1、2、3，需要调三次接口。因为这三个类型是独立的，就相当于用户点击三个按钮
     *
     * @param type 0：关闭所有类型电子眼播报
     *             1：打开超速摄像头播报
     *             2：打开违章摄像头播报
     *             3：打开警示牌播报
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：非法参数。
     */
    public int setCameraBroadcastType(int type) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setCameraBroadcastType(type);
                KLog.d(TAG, "setCameraBroadcastType ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 添加收藏
     *
     * @param poi PoiBean:经纬度是必须的;名称和地址有的话就也设置上，如果没有四维会补充;distance、cityName、typeName可以不设置。
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int addCollection(PoiBean poi) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.addCollection(poi);
                KLog.d(TAG, "addCollection ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 添加收藏
     *
     * @param name      可以为null
     * @param address   可以为null
     * @param longitude
     * @param latitude
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int addCollection(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mService != null) {
            try {
                PoiBean poi = new PoiBean();
                poi.setName(name);
                poi.setAddress(address);
                poi.setLongitude(longitude);
                poi.setLatitude(latitude);
                ret = mService.addCollection(poi);
                KLog.d(TAG, "addCollection ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取收藏列表
     *
     * @return 收藏点列表。若地图初始化未完成，返回null。
     */
    public List<PoiBean> getCollections() {
        List list = null;
        if (mService != null) {
            try {
                list = mService.getCollections();
                KLog.d(TAG, "getCollections list: " + list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 设置家
     *
     * @param home PoiBean:经纬度是必须的;名称和地址有的话就也设置上，如果没有四维会补充;distance、cityName、typeName可以不设置。
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setHome(PoiBean home) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setHome(home);
                KLog.d(TAG, "setHome ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置家
     *
     * @param name      可以为null
     * @param address   可以为null
     * @param longitude
     * @param latitude
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setHome(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mService != null) {
            try {
                PoiBean home = new PoiBean();
                home.setName(name);
                home.setAddress(address);
                home.setLongitude(longitude);
                home.setLatitude(latitude);
                ret = mService.setHome(home);
                KLog.d(TAG, "setHome ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置公司
     *
     * @param company PoiBean:经纬度是必须的;名称和地址有的话就也设置上，如果没有四维会补充;distance、cityName、typeName可以不设置。
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setCompany(PoiBean company) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setCompany(company);
                KLog.d(TAG, "setCompany ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置公司
     *
     * @param name      可以为null
     * @param address   可以为null
     * @param longitude
     * @param latitude
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setCompany(String name, String address, double longitude, double latitude) {
        int ret = -1;
        if (mService != null) {
            try {
                PoiBean company = new PoiBean();
                company.setName(name);
                company.setAddress(address);
                company.setLongitude(longitude);
                company.setLatitude(latitude);
                ret = mService.setCompany(company);
                KLog.d(TAG, "setCompany ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 显示设置家页面
     *
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：正在导航中。
     */
    public int showHomeSettingPage() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showHomeSettingPage();
                KLog.d(TAG, "showHomeSettingPage ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 显示设置公司页面
     *
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：正在导航中。
     */
    public int showCompanySettingPage() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showCompanySettingPage();
                KLog.d(TAG, "showCompanySettingPage ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 显示自车位置
     *
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：正在导航中；-3：获取自车位置信息失败。
     */
    public int showCarPosition() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showCarPosition();
                KLog.d(TAG, "showCarPosition ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 路况查询
     *
     * @param lon
     * @param lat
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int showTmcForPoi(double lon, double lat) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showTmcForPoi(lon, lat);
                KLog.d(TAG, "showTmcForPoi ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取路线剩余距离(m)
     *
     * @return >=0:剩余距离；-1：地图初始化未完成；-2：当前非导航状态。
     */
    public int getRemainingDistance() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.getRemainingDistance();
                KLog.d(TAG, "getRemainingDistance ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取路线剩余时间(s)
     *
     * @return >=0:剩余时间；-1：地图初始化未完成；-2：当前非导航状态。
     */
    public int getRemainingTime() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.getRemainingTime();
                KLog.d(TAG, "getRemainingTime ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取下个机动点名称
     *
     * @return 返回下个机动点名称。若地图初始化未完成，或当前非导航状态，返回null。
     */
    public String getNextRoadName() {
        String ret = null;
        if (mService != null) {
            try {
                ret = mService.getNextRoadName();
                KLog.d(TAG, "getNextRoadName ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取到下个机动点的距离(m)
     *
     * @return >=0：到下个机动点的距离；-1：地图初始化未完成；-2：当前非导航状态。
     */
    public int getDistanceToNextRoad() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.getDistanceToNextRoad();
                KLog.d(TAG, "getDistanceToNextRoad ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 显示限行信息
     *
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int showLimitInformation() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.showLimitInformation();
                KLog.d(TAG, "showLimitInformation ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 下个机动点播报
     *
     * @return 0：操作执行成功；-1：地图初始化未完成；-2：当前非导航状态。
     */
    public int naviBroadcast() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.naviBroadcast();
                KLog.d(TAG, "naviBroadcast ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取导航是否静音
     *
     * @return true：静音；false：未静音或初始化未完成。
     */
    public boolean isNaviMuted() {
        boolean ret = false;
        if (mService != null) {
            try {
                ret = mService.isNaviMuted();
                KLog.d(TAG, "isNaviMuted ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置导航静音状态
     *
     * @param mute 静音状态，true为静音，false为取消静音
     * @return 0：操作执行成功；-1：地图初始化未完成。
     */
    public int setNaviMuted(boolean mute) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setNaviMuted(mute);
                KLog.d(TAG, "setNaviMuted ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取是否路线全览
     *
     * @return true:当前为路线全览状态;false:当前非路线全览状态.
     */
    public boolean isRouteOverview() {
        boolean ret = false;
        if (mService != null) {
            try {
                ret = mService.isRouteOverview();
                KLog.d(TAG, "isRouteOverview ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取路线数目
     *
     * @return >=0:当前路线数目;-1:地图初始化未完成;-2:当前非算路或导航状态.
     */
    public int getRoutePlanSize() {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.getRoutePlanSize();
                KLog.d(TAG, "getRoutePlanSize ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 沿路搜索
     *
     * @param searchKey
     * @return 0:操作执行成功;-1:地图初始化未完成;-2:当前非导航状态.
     */
    public int searchByRoute(String searchKey) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.searchByRoute(searchKey);
                KLog.d(TAG, "searchByRoute ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置TTS播报状态
     *
     * @param enable 是否打开TTS播报，true为打开，false为关闭
     * @return 0:操作执行成功
     */
    public int setEnableTTSPlay(boolean enable) {
        int ret = -1;
        if (mService != null) {
            try {
                ret = mService.setEnableTTSPlay(enable);
                KLog.d(TAG, "setEnableTTSPlay ret: " + ret);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 设置仪表盘地图模式
     *
     * @param mode 参数必须为以下数值中的一个：
     *             0：不显示地图
     *             1：时速表地图
     *             2：仪表盘全屏地图
     *             101:双屏挂瓢后重启时，通知导航可以重新绑定display写数据
     */
    public boolean setICMapMode(int mode) {
        if (mService != null) {
            try {
                Log.e("jun", TAG + " mService setICMapMode,mode=" + mode);
                mService.setICMapMode(mode);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("jun", TAG + "mService=" + mService);
        }
        return false;
    }

    public boolean isNaviEngineenInited() {
        if (mService != null) {
            try {
                boolean result = mService.isNaviEngineInited();
                Log.e("jun", TAG + " mService isNaviEngineenInited, result = "+result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("jun", TAG + "mService=" + mService);
        }
        return false;
    }

    /**
     * 设置仪表盘AR是否显示
     *
     * @param visible true：显示
     *                false：不显示
     */
    public void setICARVisible(boolean visible) {
        if (mService != null) {
            try {
                mService.setICARVisible(visible);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置仪表盘地图缩放比例模式
     *
     * @param mode 参数必须为以下数值中的一个：
     *             0：正常比例尺
     *             1：大比例尺
     *             2：小比例尺
     */
    public void setZoomMode(int mode) {
        if (mService != null) {
            try {
                mService.setZoomMode(mode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置监听回调
     */
    private D058AidlCallback mCallback = new D058AidlCallback.Stub() {

        /**
         * 关键字搜索结果回调
         *
         * @param searchKey
         * @param errorCode
         * 0：搜索成功
         * 1：取消导致失败
         * 2：有数据但未搜索到结果
         * 3：没有数据导致失败
         * 4：不支持对应搜索
         * 5：网络错误
         * 6：没有授权
         * @param searchResults 搜索结果列表
         * @throws RemoteException
         */
        @Override
        public void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults) throws RemoteException {
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onSearchResult(searchKey, errorCode, searchResults);
            }
        }

        /**
         * 周边搜索结果回调
         *
         * @param searchKey
         * @param lon
         * @param lat
         * @param errorCode
         * 0：搜索成功
         * 1：取消导致失败
         * 2：有数据但未搜索到结果
         * 3：没有数据导致失败
         * 4：不支持对应搜索
         * 5：网络错误
         * 6：没有授权
         * @param searchResults 搜索结果列表
         * @throws RemoteException
         */
        @Override
        public void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, List<PoiBean> searchResults) throws RemoteException {
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onSearchNearResult(searchKey, lon, lat, errorCode, searchResults);
            }
        }

        /**
         * 导航状态改变回调
         *
         * @param status
         * 0：地图初始化完成
         * 1：算路开始
         * 2：算路完成
         * 3：算路失败
         * 4：导航开始
         * 5：取消导航
         * 6：到达目的地
         * 7：模拟导航开始
         * 8：模拟导航结束
         * 9:在路线选择界面点击取消路线
         * @param startPoi
         * 当status为4、5、6、7、8时有值，当status为其他值时为null
         * @param endPoi
         * 当status为4、5、6、7、8时有值，当status为其他值时为null
         * @throws Exception
         */
        @Override
        public void onNaviStatusChanged(int status, PoiBean startPoi, PoiBean endPoi) throws RemoteException {
            currentStatus = status;
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onNaviStatusChanged(status, startPoi, endPoi);
            }
        }

        /**
         * 自车位置改变回调
         *
         * @param currentPoi 自车位置
         * @throws RemoteException
         */
        @Override
        public void onCarPositionChanged(PoiBean currentPoi) throws RemoteException {
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onCarPositionChanged(currentPoi);
            }
        }

        /**
         * 导航显示模式改变回调
         *
         * @param state 导航显示模式
         * 0：2D北向上
         * 1：2D车头向上
         * 2：3D模式
         * 3：AR模式
         * @throws RemoteException
         */
        @Override
        public void onNaviShowStateChanged(int state) throws RemoteException {
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onNaviShowStateChanged(state);
            }
        }

        /**
         * 沿路搜索结果回调
         *
         * @param searchKey
         * @param errorCode
         * 0：搜索成功
         * 1：取消导致失败
         * 2：有数据但未搜索到结果
         * 3：没有数据导致失败
         * 4：不支持对应搜索
         * 5：网络错误
         * 6：没有授权
         * @param searchResults
         * @throws RemoteException
         */
        @Override
        public void onSearchByRouteResult(String searchKey, int errorCode, List<PoiBean> searchResults) throws RemoteException {
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onSearchByRouteResult(searchKey, errorCode, searchResults);
            }
        }

        /**
         * 导航下个转向标回调
         *
         * @param turnId
         * 8：左转
         * 9：右转
         * @param distanceToTurn
         * 距离下个转向标的距离
         */
        @Override
        public void onNaviTracking(int turnId, int distanceToTurn, int turnToStart){
            for (XmMapNaviManagerCallBack callBack : listCallBack) {
                callBack.onNaviTracking(turnId, distanceToTurn, turnToStart);
            }
        }

        @Override
        public void onNaviEngineInited() throws RemoteException {
            if(mapAidlConnectListen != null) mapAidlConnectListen.mapInitFinish();
        }
    };

    public boolean isPathPlanSuccess() {
        return currentStatus == 2;
    }

    public boolean isNaviing() {
        return currentStatus == 4;
    }


    public boolean isNaviingV2() {
        return currentStatus == 4 || currentStatus == 7;
    }


    public void destroy() {
        KLog.d(TAG, "XmMapNaviManager destroy()");
        Context mContext=weakReference.get();
        if (mContext != null) {
            mContext.unbindService(mConnection);
        }
        clearAllCallBack();
    }

    public void switchToLauncher() {
        Context mContext=weakReference.get();
        if (mContext != null) {
            KeyEventUtils.goHome(mContext, KeyEventUtils.GO_HOME_MAP);
        }
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

}
