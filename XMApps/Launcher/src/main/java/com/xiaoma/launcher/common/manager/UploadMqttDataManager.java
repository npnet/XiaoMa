package com.xiaoma.launcher.common.manager;
//import android.car.hardware.CarSensorEvent;
//
//import com.xiaoma.carlib.XmCarFactory;

import android.support.annotation.NonNull;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.model.GearData;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.favorites.FavoritesDBManager;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mqtt.client.PushManager;
import com.xiaoma.mqtt.model.Device;
import com.xiaoma.mqtt.model.Gps;
import com.xiaoma.mqtt.model.SenseData;
import com.xiaoma.mqtt.model.SensorBean;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.NaviState;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/23
 */
public class UploadMqttDataManager implements LauncherCarEvent.CallBack {
    private static String TAG = "[UploadMqttDataManager]";
    private static UploadMqttDataManager instance;
    private final long CYCLE_UPLOAD_MQTT_DATA_TIME = 1 * 60 * 1000;
    private long mqttInterval;
    private PoiBean destinationPoi;
    private boolean canAction = true;

    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            uploadMqttData();
        }
    };
    private XmMapNaviManagerCallBack mapNaviManagerCallBack = new XmMapNaviManagerCallBack() {
        @Override
        public void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults) {

        }

        @Override
        public void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, List<PoiBean> searchResults) {

        }


        /**
         * 4：导航开始
         * 5：取消导航
         * 6：到达目的地
         * 7：模拟导航开始
         * 8：模拟导航结束
         * @param startPoi
         * 当status为4、5、6、7、8时有值，当status为其他值时为null
         * @param endPoi
         * 当status为4、5、6、7、8时有值，当status为其他值时为null
         * */
        @Override
        public void onNaviStatusChanged(int status, PoiBean startPoi, PoiBean endPoi) {
            if (status == 4 || status == 7) {
                destinationPoi = endPoi;
                RemoteIatManager.getInstance().uploadNaviState(NaviState.navigation);
            } else if (status == 5 || status == 6 || status == 8 || status == 9) {
                destinationPoi = null;
                RemoteIatManager.getInstance().uploadNaviState(NaviState.noNavi);
            } else if (status == 2) {
                RemoteIatManager.getInstance().uploadNaviState(NaviState.routing);
            }
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
            KLog.e(TAG, "onNaviTracking() turnId= " + turnId + " , distanceToTurn= " + distanceToTurn + " ,turnToStart= " + turnToStart);
            if (0 < distanceToTurn && distanceToTurn <= 10 && canAction) {
                canAction = false;
                //因为在三棱锥平板上显示正常，但在椎体里投出的是个倒影，动作相反。所以在这里调整一下
                RobActionManager.getInstance().setRobAction(turnId == 8 ? LauncherConstants.RIGHT_ACTION : LauncherConstants.LEFT_ACTION);
            }
            if (distanceToTurn == 0 || distanceToTurn > 10) {
                canAction = true;
            }
        }
    };

    private UploadMqttDataManager() {
//        LauncherCarEvent.getInstance().setmCallBack(this);
        MapManager.getInstance().setXmMapNaviManagerCallBack(mapNaviManagerCallBack);
    }

    public static UploadMqttDataManager getInstance() {
        if (instance == null) {
            synchronized (UploadMqttDataManager.class) {
                if (instance == null) {
                    instance = new UploadMqttDataManager();
                }
            }
        }
        return instance;
    }


    public void timingUploadMqttData(long interval) {
        ThreadDispatcher.getDispatcher().remove(timeRunable);
        if (interval == 0) {
            mqttInterval = CYCLE_UPLOAD_MQTT_DATA_TIME;
        } else {
            mqttInterval = interval;
        }
        ThreadDispatcher.getDispatcher().postDelayed(timeRunable, mqttInterval, Priority.NORMAL);
    }

    private void uploadMqttData() {
        uploadGpsData(LocationManager.getInstance().getCurrentLocation(), null, -1);
        timingUploadMqttData(mqttInterval);
    }


    /**
     * 上报gps类型数据
     *
     * @param locationInfo
     */
    public void uploadGpsData(LocationInfo locationInfo, GearData gearData, int speedData) {
        SenseData senseData = getSenseData(locationInfo, gearData, speedData);
        PushManager.getInstance().publishTopic(senseData);
    }


    /**
     * 用户上线上报
     *
     * @param locationInfo
     */
    public void uploadOnLineData(LocationInfo locationInfo) {
        SenseData senseData = getSenseData(locationInfo, null, -1);
        PushManager.getInstance().publishOnLineTopic(senseData);
    }

    public long getMqttInterval() {
        return mqttInterval;
    }

    public void setMqttInterval(long mqttInterval) {
        this.mqttInterval = mqttInterval;
    }

    private Device getDevice() {
        Device device = new Device();
        String channelId = ConfigManager.ApkConfig.getChannelID();
        String versionCode = String.valueOf(ConfigManager.ApkConfig.getBuildVersionCode());
        String iccid = ConfigManager.DeviceConfig.getICCID(AppHolder.getInstance().getAppContext());
        String imei = ConfigManager.DeviceConfig.getIMEI(AppHolder.getInstance().getAppContext());
        String loginUserId = LoginManager.getInstance().getLoginUserId();
        String osVersion = ConfigManager.DeviceConfig.getOSVersion();
        String deviceModel = ConfigManager.DeviceConfig.getDeviceModel();
        device.setCd(System.currentTimeMillis());
        device.setUid(loginUserId);
        device.setIccid(iccid);
        device.setImei(imei);
        device.setCid(channelId);
        device.setOv(osVersion);
        device.setDm(deviceModel);
        device.setVc(versionCode);
        //添加工作模式
        device.setPat(TPUtils.get(AppHolder.getInstance().getAppContext(), LauncherConstants.CAR_MODEL, LauncherConstants.LIVE_MODEL));
        return device;
    }

    @NonNull
    private SenseData getSenseData(LocationInfo locationInfo, GearData gearData, int speedData) {
        Gps gps = new Gps();
        GearData currentGearData;
        int currentSpeedData;
        if (locationInfo != null) {
            gps.setLon(locationInfo.getLongitude());
            gps.setLat(locationInfo.getLatitude());
            gps.setP(locationInfo.getProvince());
            gps.setC(locationInfo.getCity());
            gps.setDis(locationInfo.getDistrict());
            gps.setS(String.valueOf(locationInfo.getSpeed()));
            gps.setAc(String.valueOf(locationInfo.getAccuracy()));
            gps.setBe(String.valueOf(locationInfo.getBearing()));
            gps.setGac(String.valueOf(locationInfo.getGpsAccuracyStatus()));
            gps.setSa(String.valueOf(locationInfo.getSatellites()));
            gps.setName(locationInfo.getAddress());
        }

        if (destinationPoi != null) {
            gps.setDlon(destinationPoi.getLongitude());
            gps.setDlat(destinationPoi.getLatitude());
            gps.setDc(destinationPoi.getCityName());
            gps.setDtype(FavoritesDBManager.getInstance().getShortCutsType(destinationPoi));
            gps.setDname(destinationPoi.getAddress() + destinationPoi.getName());
        }

        if (gearData != null) {
            currentGearData = gearData;
        } else {
            currentGearData = XmCarFactory.getCarSensorManager().getCurrentGearData();
        }
        if (currentGearData != null) {
            gps.setGe(String.valueOf(currentGearData.gear));
        } else {
            gps.setGe("-1");
        }

        if (speedData != -1) {
            currentSpeedData = speedData;
        } else {
            currentSpeedData = XmCarFactory.getCarVendorExtensionManager().getCarSpeed();
        }
        if (currentSpeedData != -1) {
            gps.setS(String.valueOf(currentSpeedData));
        } else {
            gps.setS("-1");
        }
        gps.setGac("-1");
        SensorBean sensorBean = new SensorBean(gps);
        sensorBean.setType("gps");
        return new SenseData(getDevice(), sensorBean);
    }

    @Override
    public void onGearDataUpdate(GearData gearData) {
        uploadGpsData(LocationManager.getInstance().getCurrentLocation(), gearData, -1);
    }

    @Override
    public void onSpeedData(int speedData) {
        uploadGpsData(LocationManager.getInstance().getCurrentLocation(), null, speedData);
    }
}
