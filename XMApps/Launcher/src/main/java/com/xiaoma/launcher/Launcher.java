package com.xiaoma.launcher;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.NaviSupportBefore;
import com.xiaoma.ad.AdManager;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.alive.AppAliveWatcher;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.manager.XmSystemManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseCustomApp;
import com.xiaoma.config.BuildConfig;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.db.DBManager;
import com.xiaoma.gdmap.XmMapManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.CarInfoClient;
import com.xiaoma.launcher.common.manager.LauncherAppManager;
import com.xiaoma.launcher.common.manager.LauncherCarEvent;
import com.xiaoma.launcher.common.manager.LauncherClient;
import com.xiaoma.launcher.common.manager.LauncherSkillManager;
import com.xiaoma.launcher.common.manager.MqttInfoManager;
import com.xiaoma.launcher.common.manager.OilConsumeManager;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.common.manager.UploadMqttDataManager;
import com.xiaoma.launcher.common.manager.WeatherManager;
import com.xiaoma.launcher.common.receiver.LauncherBluetoothReceiver;
import com.xiaoma.launcher.common.receiver.MqttReceiver;
import com.xiaoma.launcher.favorites.FavoritesDBManager;
import com.xiaoma.launcher.main.manager.CarSettingManager;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.player.manager.BluetoothConnectManager;
import com.xiaoma.launcher.player.manager.BluetoothReceiver;
import com.xiaoma.launcher.player.manager.PlayerAudioManager;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.model.PhoneScheduleInfo;
import com.xiaoma.launcher.wheel.WheelManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.mqtt.client.PushManager;
import com.xiaoma.mqtt.constant.MqttConstants;
import com.xiaoma.mqtt.listener.MqttListener;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.network.XmHttp;
import com.xiaoma.scene.SceneManager;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.smarthome.common.manager.CMDeviceManager;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GetNetTimeUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Thomas on 2019/5/24 0024
 * 桌面
 */

@SuppressWarnings("unused")
@SuppressLint("LogNotTimber")
public class Launcher extends BaseCustomApp {
    private static final String TAG = Launcher.class.getSimpleName();
    private static final String TAG_START = "LauncherStartup";

    @Override
    protected void onCreatePost() {
        super.onCreatePost();
        Thread.setDefaultUncaughtExceptionHandler(new LauncherCrashHandler(this));
    }

    @Override
    public void initLibs() {
        hookWebView();
        initWebsiteDatetime();
        //四维地图初始化
        long t0 = System.currentTimeMillis();
        NaviSupportBefore.aspectInitialzation(getApplication());
        XmCarVendorExtensionManager.getInstance().addCarServiceListener(new CarServiceListener() {
            @Override
            public void onCarServiceConnected(IBinder binder) {
                KLog.d("CarService connect now");
                // 仪表皮肤同步
                boolean result = XmCarVendorExtensionManager.getInstance().setTheme(SkinUtils.getCurSkinStyle());
                XmCarVendorExtensionManager.getInstance().removeCarServiceListener(this);
            }

            @Override
            public void onCarServiceDisconnected() {

            }
        });
        CarSettingManager.getInstance().init(getApplication());
        super.initLibs();
        initDB();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication());
        XmSystemManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initSmartHome();
        postNormalPriorityInit();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        CarInfoClient.getInstance().init();
//        CarInfoClient.startLoop();
        LauncherSkillManager.getInstance().init(getApplication());
        WheelManager.getInstance().init(getApplication());
        BluetoothConnectManager.getInstance().init(getApplication());
        long t1 = System.currentTimeMillis();
        Log.e(TAG_START, String.format("initLibs: %s ms", t1 - t0));
        RemoteIatManager.getInstance().init(getApplication());
        AppObserver.getInstance().addAppStateChangedListener(new AppObserver.AppStateChangedListener() {
            @Override
            public void onForegroundChanged(boolean isForeground) {
                RemoteIatManager.getInstance().uploadAppState(isForeground, AppType.NAVI);
            }
        });
        initSch();

        AppObserver.getInstance().addAppStateChangedListener(new AppObserver.AppStateChangedListener() {
            @Override
            public void onForegroundChanged(boolean isForeground) {
                if (!isForeground) {
                    XMToast.cancelToast();
                    GuideToast.cancelToast();
                }
            }
        });
    }

    private void initWebsiteDatetime() {
        // TODO: 2019/5/27 0027 由于当前车机无法自动同步网络时间 ，先加此代码主动设置网络时间
        if (BuildConfig.DEBUG) {
            ThreadDispatcher.getDispatcher().post(new Runnable() {
                @Override
                public void run() {
                    if (!GetNetTimeUtils.setWebsiteDatetime(getApplication())) {
                        ThreadDispatcher.getDispatcher().postDelayed(this, 3000);
                    }
                }
            });
        }
    }

    private void initSch() {
        RequestManager.getInstance().fetchPhoneSchs(new ResultCallback<XMResult<List<PhoneScheduleInfo>>>() {
            @Override
            public void onSuccess(XMResult<List<PhoneScheduleInfo>> result) {
                List<PhoneScheduleInfo> data = result.getData();
                if (ListUtils.isEmpty(data)) {
                    KLog.d("fetchPhoneSchs success 从手机端同步过来的日程，数据为空");
                    return;
                }
                List<ScheduleInfo> localAllScheduleInfos = ScheduleDataManager.getLocalScheduleInfos();
                if (ListUtils.isEmpty(localAllScheduleInfos)) {
                    //如果本地数据库为空，直接添加所有手机端数据
                    for (int i = 0; i < data.size(); i++) {
                        PhoneScheduleInfo dataEntity = data.get(i);
                        ScheduleInfo scheduleInfo = new ScheduleInfo();
                        scheduleInfo.setCreateId(dataEntity.getId());
                        scheduleInfo.setDate(dataEntity.getRemindDate());
                        scheduleInfo.setTime(dataEntity.getRemindBeginTime());
                        scheduleInfo.setStartTime(dataEntity.getRemindBeginTime());
                        scheduleInfo.setMessage(dataEntity.getContent());
                        scheduleInfo.setTimestamp(dataEntity.getCreateDate());
                        scheduleInfo.setIsUpload(true);
                        ScheduleDataManager.getDBManager().save(scheduleInfo);
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        PhoneScheduleInfo dataEntity = data.get(i);
                        List<ScheduleInfo> idInfos = ScheduleDataManager.getLocalScheduleInfosForValue("createId", dataEntity.getId());
                        if (ListUtils.isEmpty(idInfos)) {
                            ScheduleInfo scheduleInfo = new ScheduleInfo();
                            scheduleInfo.setCreateId(dataEntity.getId());
                            scheduleInfo.setDate(dataEntity.getRemindDate());
                            scheduleInfo.setTime(dataEntity.getRemindBeginTime());
                            scheduleInfo.setStartTime(dataEntity.getRemindBeginTime());
                            scheduleInfo.setMessage(dataEntity.getContent());
                            scheduleInfo.setTimestamp(dataEntity.getCreateDate());
                            scheduleInfo.setIsUpload(true);
                            ScheduleDataManager.getDBManager().save(scheduleInfo);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.d("fetchPhoneSchs onFailure code = " + code + " msg = " + msg);
            }
        });

    }

    private void postNormalPriorityInit() {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                AdManager.init(getApplication());
                SceneManager.getInstance().init();
                initMqttPushReceived();
                initCenterService();
                appAliveWatcher();
                XmCarEventDispatcher.getInstance().registerEvent(LauncherCarEvent.getInstance());
                registerBluetoothReceiver();
                //XmWheelServiceManager.startService(getApplication());
                MapManager.getInstance().init(getApplication());
                MqttInfoManager.getInstance().init(getApplication());
                //此处先开机拉起，不采用开机广播 因依赖中心服务，开机自启后需处理重连操作
                LauncherAppManager.launcherForPowerOn(getApplication());
                //初始化USB连接状态监听
                UsbDetector.getInstance().init(getApplication());
                //注册蓝牙连接状态监听
                registerBluetoothStateReceiver();
            }
        });
    }

    private void onLoginPostInit(String userId) {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance().initUserDB(userId);
                initAutoTracker();
                initXmMapManager(String.valueOf(userId));
                initMqtt(String.valueOf(userId));
                initFavoritesDBManager(String.valueOf(userId));
                OilConsumeManager.getInstance().init();
            }
        });
    }

    /**
     * 发送广播通知数据已变化
     */
    private void notifyVrPracticeDataChange() {
        Intent intent = new Intent();
        intent.setAction(ConfigConstants.VR_PRACTICE_ACTION);
        sendBroadcast(intent);
    }

    private void initSmartHome() {
        //云米智能家居初始化
        CMDeviceManager.getInstance().initCM(getApplication());
        //公子小白智能家居初始化
        SmartHomeManager.getInstance().init(getApplication(), SmartHomeManager.TYPE_GOLID);
    }

    private void initFavoritesDBManager(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            FavoritesDBManager.getInstance().init(userId);
        }
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
    }

    private void initCenterService() {
        if (!Center.getInstance().isConnected()) {
            Center.getInstance().init(getApplication());
        }
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                boolean register = Center.getInstance().register(LauncherClient.getInstance());
                KLog.d("register state: " + register);
                PlayerAudioManager.getInstance().connectAudio(getApplication());
            }
        });
    }

    private void initMqtt(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            String mqttId = userId;
            if (LauncherConstants.tourist_uid.equals(userId)) {
                mqttId = ConfigManager.DeviceConfig.getICCID(getApplication());
            }

            PushManager.getInstance().init(getApplication(), mqttId, new MqttListener() {
                @Override
                public void onSuccessConnect() {
                    KLog.e(TAG, "Launcher initMqtt onSuccessConnect");

                    String mqttInfoStr = TPUtils.get(getApplication(), MqttConstants.Mqtt_Info, "");
                    KLog.d(TAG, "MqttManager getMqttInfo is " + mqttInfoStr);
                    MqttInfo mqttInfo = null;
                    if (!TextUtils.isEmpty(mqttInfoStr)) {
                        mqttInfo = GsonHelper.fromJson(mqttInfoStr, MqttInfo.class);
                    }

                    if (mqttInfo == null) {
                        mqttInfo = new MqttInfo();
                    }
                    KLog.e(TAG, "mqtt interval " + mqttInfo.interval);
                    UploadMqttDataManager.getInstance().timingUploadMqttData(mqttInfo.interval);

                }

                @Override
                public void onFailConnect() {
                    KLog.e("Launcher initMqtt  onFailConnect");
                }
            });
        }
    }

    private void initMqttPushReceived() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LauncherConstants.MQTT_ACTION);
        AppHolder.getInstance().getAppContext().registerReceiver(new MqttReceiver(), intentFilter);
    }

    private void initAutoTracker() {
        XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
    }

    private void initXmMapManager(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            XmMapManager.getInstance().init(getApplication(), userId);
            LocationManager.getInstance().addLocationListener(new LocationManager.ILocationChangedListener() {
                @Override
                public void onLocationChange(LocationInfo locationInfo) {
                    KLog.d("Launcher OnLocationChange locationInfo is " + locationInfo);
                    //位置变化更新天气图标
                    WeatherManager.getInstance().OnLocationChange(locationInfo);
                }
            });
            LocationManager.getInstance().setUploadLocation(true);
        }
    }

    private void appAliveWatcher() {
        AppAliveWatcher.getInstance().init(getApplication());
        AppAliveWatcher.getInstance().addWatcher(new AppAliveWatcher.Watcher() {
            @Override
            public void onAppIn(String packageName) {
                KLog.d("AppAliveWatcher onAppIn packageName: " + packageName);
            }

            @Override
            public void onAppOut(String packageName) {
                KLog.d("AppAliveWatcher onAppOut packageName: " + packageName);
                LauncherAppManager.launcherAppOutStart(getApplication(), packageName);
            }
        });
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User user) {
                KLog.d(TAG, "login success");
                notifyVrPracticeDataChange();
                if (user != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
                    onLoginPostInit(String.valueOf(user.getId()));
                    initAutoTracker();
                    if (XmCarConfigManager.hasFaceRecognition()) {
                        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.FACERECOGINIZE.getType());
                    } else {
                        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.KEYBIND.getType());
                    }
                }
            }

            @Override
            public void onLogout() {
                KLog.d(TAG, "login out");
                XmHttp.getDefault().addCommonParams("uid", "");
                DBManager.getInstance().onUserLogout();
            }
        });
    }

    private void registerBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(LauncherConstants.PERIOD_ACTION);
        intentFilter.addAction(CenterConstants.IN_A_IBCALL);
        intentFilter.addAction(CenterConstants.END_OF_IBCALL);
        AppHolder.getInstance().getAppContext().registerReceiver(new LauncherBluetoothReceiver(), intentFilter);
    }

    /**
     * 注册蓝牙连接状态监听
     */
    private void registerBluetoothStateReceiver() {
        BluetoothReceiver receiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getApplication().registerReceiver(receiver, intentFilter);
    }


    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i(TAG, "sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i(TAG, "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
                Log.i(TAG, "Hook success!");
            } else {
                Log.i(TAG, "Hook failed!");
            }
        } catch (Throwable e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        KLog.e("Launcher onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        KLog.e("Launcher onTerminate");
    }
}
