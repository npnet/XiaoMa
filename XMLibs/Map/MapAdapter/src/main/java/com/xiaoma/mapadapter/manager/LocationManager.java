package com.xiaoma.mapadapter.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xiaoma.config.ConfigConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.mapadapter.BuildConfig;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.interfaces.IRecycle;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.model.GpsInfo;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationClientOption;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.LocationMode;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */
public class LocationManager implements IRecycle, OnLocationChangeListener, AutoTriggerHelp.ITriggerMaxCritical {

    private static final String LOCATION_FILE_NAME = "location";
    private int watchDogInterval = 60 * 1000;
    private int uploadDistance = 100;
    private int maxLocationInterval = 30 * 60 * 1000;
    private int minLocationInterval = 30 * 1000;
    private int defaultLocationInterval = 5 * 60 * 1000;
    private static LocationManager instance;
    private Context context;
    private LocationClient locationClient = null;
    private LocationClient watchDogLocationClient = null;
    private LocationClientOption locationOption = null;
    private LatLng currentLocation;
    private LocationInfo locationInfo;
    private AutoTriggerHelp<GpsInfo> autoTriggerHelp;
    private boolean uploadLocation = false;
    private List<ILocationChangedListener> locationListenerList = new ArrayList<>();
    private ILocationChangedListener locationChangedListener;
    private volatile boolean uploadOnce = true;
    private String loginUserId;

    public void setLocationChangedListener(ILocationChangedListener locationChangedListener) {
        this.locationChangedListener = locationChangedListener;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    private BroadcastReceiver accBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.hsae.core.acc")) {
                boolean accstate = intent.getBooleanExtra("accstate", false);
                if (!accstate) {
                    saveLastLocation();
                    if (autoTriggerHelp != null) autoTriggerHelp.triggerNow();
                }
            }
        }
    };

    public static LocationManager getInstance() {
        if (instance == null) {
            synchronized (LocationManager.class) {
                instance = new LocationManager();
            }
        }
        return instance;
    }

    /**
     * 使用XmMapManager.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());初始化
     *
     * @param context
     * @param loginUserId
     */
    public void init(Context context, String loginUserId) {
        init(context, 1, loginUserId);
        autoTriggerHelp = new AutoTriggerHelp<>(30, 20 * 60 * 1000, this);
    }

    public void init(Context context, float configFactor, String loginUserId) {
        this.context = context.getApplicationContext();
        this.loginUserId = loginUserId;
        new MapSdkManager().init(context);
        initConfig(configFactor);
        registerAccBroadcast();
        LocationInfo locationInfo = getLocalMapLocation();
        updateLocationInfoInCache(locationInfo);
        initlocationInfoOption();
        initWatchDogLocationOption();
        uploadLocalLocationInfo();
    }

    private void initConfig(float configFactor) {
        this.watchDogInterval = (int) (this.watchDogInterval * configFactor);
        this.maxLocationInterval = (int) (this.maxLocationInterval * configFactor);
        this.minLocationInterval = (int) (this.minLocationInterval * configFactor);
        this.defaultLocationInterval = (int) (this.defaultLocationInterval * configFactor);
        this.uploadDistance = (int) (this.uploadDistance * configFactor);
    }

    @Override
    public void onDestroy() {
        try {
            context.unregisterReceiver(accBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveLastLocation();
        if (null != locationClient) {
            locationClient.destroy();
            locationClient = null;
            locationOption = null;
        }
        if (null != watchDogLocationClient) {
            watchDogLocationClient.destroy();
            watchDogLocationClient = null;
        }
    }

    private void saveLastLocation() {
        if (locationInfo == null) {
            return;
        }
        if (locationInfo.getLatitude() <= 0) {
            return;
        }
        if (locationInfo.getLongitude() <= 0) {
            return;
        }
        File xiaoMaUserFile = ConfigManager.FileConfig.getUserFile(loginUserId);
        File file = new File(xiaoMaUserFile, LOCATION_FILE_NAME);
        LocalLocation localLocation = LocalLocation.fromMapLocation(locationInfo);
        if (localLocation != null) {
            FileUtils.saveAsFile(GsonHelper.toJson(localLocation), file);
        }
    }

    public LatLng getCurrentPosition() {
        if (currentLocation == null) {
            currentLocation = new LatLng(LocationInfo.getDefault().getLatitude(), LocationInfo.getDefault().getLongitude());
        }
        return currentLocation;
    }


    public LocationInfo getCurrentLocation() {
        if (BuildConfig.BUILD_PLATFORM == "CAR") {

            //定位不到
            if (locationInfo == null) {
                locationInfo = LocationInfo.getDefault();
            }

            //定位无效
            if (isLocationInvalid(locationInfo)) {
                locationInfo = LocationInfo.getDefault();
            }
        }
        return locationInfo;
    }

    private boolean isLocationInvalid(LocationInfo locationInfo) {
        return locationInfo.getLatitude() <= 0 || locationInfo.getLongitude() <= 0;
    }


    public LocationInfo getRealCurrentLocation() {
        if (currentLocation == null) return null;
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLongitude(currentLocation.longitude);
        locationInfo.setLatitude(currentLocation.latitude);
        return locationInfo;
    }

    public String getCurrentCity() {
        if (locationInfo == null) {
            return LocationInfo.getDefault().getCity();
        }
        return locationInfo.getCity();
    }

    public void setUploadLocation(boolean uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    private LocationInfo getLocalMapLocation() {
        File xiaoMaUserFile = ConfigManager.FileConfig.getUserFile(loginUserId);
        File file = new File(xiaoMaUserFile, LOCATION_FILE_NAME);
        String stringFromFile = FileUtils.getStringFromFile(file);
        if (TextUtils.isEmpty(stringFromFile)) {
            return null;
        }
        LocalLocation localLocation = GsonHelper.fromJson(stringFromFile, LocalLocation.class);
        if (localLocation == null) {
            return null;
        }
        return localLocation.toMapLocation();
    }

    private void registerAccBroadcast() {
        try {
            IntentFilter intentFilter = new IntentFilter("com.hsae.core.acc");
            context.registerReceiver(accBroadcastReceiver, intentFilter);
        } catch (Exception e) {

        }
    }

    private void uploadLocalLocationInfo() {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(loginUserId)) {
                    return;
                }
                try {
                    IDatabase userDBManager = DBManager.getInstance().getUserDBManager(loginUserId);
                    List<GpsInfo> tempList = userDBManager.queryLimit(GpsInfo.class, 0, MapConstant.EACH_UPDATE_SIZE);
                    if (tempList == null || tempList.size() == 0) {
                        return;
                    }
                    batchUploadGPSInThread(tempList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void batchUploadGPSInThread(final List<GpsInfo> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        String data = GsonHelper.toJson(list);
        if (TextUtils.isEmpty(data)) {
            KLog.d("LocationManager uploadGPS data is Empty");
            return;
        }
        RequestManager.getInstance().batchUploadCompressStr(data, ConfigConstants.CompressType.LOCATION_COMPRESS, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("LocationManager batchUploadCompressStr gps info success");
                int size = list.size();
                IDatabase userDBManager = DBManager.getInstance().getUserDBManager(loginUserId);
                if (userDBManager.delete(list) == size) {
                    uploadLocalLocationInfo();
                }
            }

            @Override
            public void onError(Response<String> response) {
                KLog.d("LocationManager batchUploadCompressStr gps info failed");
            }
        });
    }

    private void initWatchDogLocationOption() {
        watchDogLocationClient = new LocationClient(context);
        LocationClientOption locationOption = new LocationClientOption();
        locationOption.setLocationMode(LocationMode.Hight_Accuracy);
        //语音“附近的酒店”类似的指令需要当前城市
        locationOption.setNeedAddress(true);
        watchDogLocationClient.setLocationListener(new OnLocationChangeListener() {
            @Override
            public void onLocationChange(LocationInfo locationInfo) {
                if (locationInfo == null || locationInfo.getErrorCode() != 0) {
                    return;
                }
                String msg = "initWatchDogLocationOption onLocationChanged," + locationInfo.toString();
                if (uploadOnce) {
                    uploadUserLocation(locationInfo, false);
                }
                KLog.d("LocationManager " + msg);
                if (locationInfo == null) {
                    return;
                }
                if (locationInfo.getErrorCode() != 0) {
                    return;
                }
                updateLocationInfoInCache(locationInfo);
                if (locationInfo.getSpeed() <= 1) {
                    updatelocationInfoInterval(maxLocationInterval);
                    return;
                }
                if (uploadDistance / locationInfo.getSpeed() <= (minLocationInterval / 1000)) {
                    updatelocationInfoInterval(minLocationInterval);
                    return;
                }
                updatelocationInfoInterval((int) (uploadDistance / locationInfo.getSpeed()) * 1000);
            }
        });
        locationOption.setLocationInterval(watchDogInterval);
        locationOption.setMockEnable(true);

        watchDogLocationClient.setLocOption(locationOption);
        watchDogLocationClient.start();
    }

    private void updatelocationInfoInterval(long locationInterval) {
        if (locationOption.getLocationInterval() == locationInterval) {
            return;
        }
        if (locationInterval <= minLocationInterval) {
            locationInterval = minLocationInterval;
        }
        locationOption.setLocationInterval(locationInterval);
        locationClient.setLocOption(locationOption);
        locationClient.stop();
        locationClient.start();
    }

    private void initlocationInfoOption() {
        KLog.d("LocationManager initlocationInfoOption");
        locationClient = new LocationClient(context);
        locationOption = new LocationClientOption();
        // 设置定位模式
        locationOption.setLocationMode(LocationMode.Device_Sensors);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationOption.setNeedAddress(false);
        locationOption.setGpsFirst(true);
        locationOption.setLocationInterval(defaultLocationInterval);
        locationOption.setMockEnable(true);
        locationClient.setLocOption(locationOption);
        locationClient.start();
    }

    @Override
    public void onLocationChange(LocationInfo locationInfo) {
        if (locationInfo == null || locationInfo.getErrorCode() != 0) {
            return;
        }
        KLog.d("LocationManager onLocationChanged" + locationInfo.toString());
        uploadUserLocation(locationInfo, true);
    }

    private void uploadUserLocation(LocationInfo locationInfo, boolean needAutoTrigger) {
        if (!uploadLocation) {
            return;
        }
        if (locationInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(loginUserId)) {
            return;
        }
        if (isLocationInvalid(locationInfo)) {
            return;
        }
        uploadLocation(locationInfo, needAutoTrigger);
    }

    private void uploadLocation(LocationInfo locationInfo, final boolean needAutoTrigger) {
        final Double geoLat = locationInfo.getLatitude();
        final Double geoLng = locationInfo.getLongitude();
        String province = locationInfo.getProvince();
        if (TextUtils.isEmpty(province) && this.locationInfo != null) {
            province = this.locationInfo.getProvince();
        }
        String city = locationInfo.getCity();
        if (TextUtils.isEmpty(city) && this.locationInfo != null) {
            city = this.locationInfo.getCity();
        }
        String district = locationInfo.getDistrict();
        if (TextUtils.isEmpty(district) && this.locationInfo != null) {
            district = this.locationInfo.getDistrict();
        }
        final float speed = locationInfo.getSpeed();
        final float accuracy = locationInfo.getAccuracy();
        final float bearing = locationInfo.getBearing();
        final int satellites = locationInfo.getSatellites();
        int locationType = locationInfo.getLocationType(); //定位方式
        if (this.locationInfo != null) {
            locationType = this.locationInfo.getLocationType();
        }
        final double altitude = locationInfo.getAltitude(); //海拔
        final int gpsAccuracyStatus = locationInfo.getGpsAccuracyStatus();
        boolean isNetWorkConnected = NetworkUtils.isConnected(context);
        String log = "uploadLocation : " + locationInfo.toString() + ", isNetWorkConnected = " + isNetWorkConnected;
        KLog.d("LocationManager: " + log);
        if (uploadOnce) {
            if (!isNetWorkConnected) {
                if (needAutoTrigger)
                    autoTriggerHelp.addData(new GpsInfo(System.currentTimeMillis(), geoLat, geoLng, province, city, speed, accuracy, bearing, district, satellites, gpsAccuracyStatus, locationType, altitude));
                return;
            }
            final String finalProvince = province;
            final String finalCity = city;
            final String finalDistrict = district;
            final int finalLocationType = locationType;
            RequestManager.getInstance().uploadUserLocation(geoLat, geoLng, province, city, speed,
                    accuracy, bearing, district, satellites, gpsAccuracyStatus, locationType, altitude, new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            if (!TextUtils.isEmpty(finalProvince)) {
                                uploadOnce = false;
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            if (needAutoTrigger)
                                autoTriggerHelp.addData(new GpsInfo(System.currentTimeMillis(), geoLat, geoLng, finalProvince, finalCity, speed,
                                        accuracy, bearing, finalDistrict, satellites, gpsAccuracyStatus, finalLocationType, altitude));
                        }
                    });
        } else {
            if (needAutoTrigger)
                autoTriggerHelp.addData(new GpsInfo(System.currentTimeMillis(), geoLat, geoLng, province, city, speed,
                        accuracy, bearing, district, satellites, gpsAccuracyStatus, locationType, altitude));
        }

    }

    private void updateLocationInfoInCache(LocationInfo locationInfo) {
        if (locationInfo == null) {
            return;
        }
        if (locationInfo.getLatitude() <= 0) {
            return;
        }
        if (locationInfo.getLongitude() <= 0) {
            return;
        }
        currentLocation = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        this.locationInfo = locationInfo;
        if (this.locationChangedListener != null) {
            this.locationChangedListener.onLocationChange(locationInfo);
        }
        if (!ListUtils.isEmpty(locationListenerList)) {
            for (int i = 0; i < locationListenerList.size(); i++) {
                locationListenerList.get(i).onLocationChange(locationInfo);
            }
        }
    }

    public void addLocationListener(ILocationChangedListener listener) {
        if (listener == null || locationListenerList.contains(listener)) {
            return;
        }
        locationListenerList.add(listener);
    }

    public void removeLocationListener(ILocationChangedListener listener) {
        if (listener == null || !locationListenerList.contains(listener)) {
            return;
        }
        locationListenerList.remove(listener);
    }

    @Override
    public void triggerCritical(final ArrayList data) {
        if (ListUtils.isEmpty(data)) {
            return;
        }
        String dataString = GsonHelper.toJson(data);
        if (TextUtils.isEmpty(dataString)) {
            KLog.d("LocationManager uploadGPSInfo data is Empty");
            return;
        }
        RequestManager.getInstance().batchUploadCompressStr(dataString, ConfigConstants.CompressType.LOCATION_COMPRESS, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("LocationManager batchUploadCompressStr gps info success");
            }

            @Override
            public void onError(Response<String> response) {
                KLog.d("LocationManager batchUploadCompressStr gps info failed");
                IDatabase userDBManager = DBManager.getInstance().getUserDBManager(loginUserId);
                userDBManager.save(data);
            }
        });
    }

    public interface ILocationChangedListener {
        void onLocationChange(LocationInfo locationInfo);
    }

    static class LocalLocation implements Serializable {
        public double lat;
        public double lon;
        public String province;
        public String city;
        public String address;
        public String district;

        public static LocalLocation fromMapLocation(LocationInfo locationInfo) {
            if (locationInfo == null) {
                return null;
            }
            LocalLocation localLocation = new LocalLocation();
            localLocation.lat = locationInfo.getLatitude();
            localLocation.lon = locationInfo.getLongitude();
            localLocation.province = locationInfo.getProvince();
            localLocation.city = locationInfo.getCity();
            localLocation.address = locationInfo.getAddress();
            localLocation.district = locationInfo.getDistrict();
            return localLocation;
        }

        public LocationInfo toMapLocation() {
            LocationInfo mapLocation = new LocationInfo();
            mapLocation.setLongitude(lon);
            mapLocation.setLatitude(lat);
            mapLocation.setProvince(province);
            mapLocation.setCity(city);
            mapLocation.setDistrict(district);
            return mapLocation;
        }
    }

    public interface DebugLocation {
        double longitude = LocationInfo.getDefault().getLongitude();
        double latitude = LocationInfo.getDefault().getLatitude();
        String lon = String.valueOf(longitude);
        String lat = String.valueOf(latitude);
        String city = LocationInfo.getDefault().getCity();
        String address = LocationInfo.getDefault().getAddress();
        String location = lat + "," + lon;
    }

    public LocationInfo getDebugLocationInfo() {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLatitude(DebugLocation.latitude);
        locationInfo.setLongitude(DebugLocation.longitude);
        locationInfo.setCity(DebugLocation.city);
        locationInfo.setAddress(DebugLocation.address);
        return locationInfo;
    }

    public LatLng getDebugLatLng() {
        return new LatLng(DebugLocation.latitude, DebugLocation.longitude);
    }

}
