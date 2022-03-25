package com.xiaoma.carlib.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.fsl.android.TboxBinderPool;
import com.fsl.android.tbox.bean.TBoxCallInfo;
import com.fsl.android.tbox.bean.TBoxEndPointInfo;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxNetworkInfo;
import com.fsl.android.tbox.bean.TBoxSystemInfo;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.fsl.android.tbox.client.PhoneClient;
import com.fsl.android.tbox.client.SystemClient;
import com.fsl.android.tbox.client.WifiClient;
import com.fsl.android.tbox.inter.IPhoneCallBackInterface;
import com.fsl.android.tbox.inter.ISystemCallBackInterface;
import com.fsl.android.tbox.inter.IWifiCallBackInterface;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.utils.UpgradeState;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2018/12/26 0026
 */
public class XmSystemManager implements ISystem {
    private static XmSystemManager instance;
    private final String TAG = XmSystemManager.class.getSimpleName();
    private final String LAUNCHER_PKG = "com.xiaoma.launcher";
    private Context context;
    private SystemClient systemClient;
    private WifiClient wifiClient;
    private PhoneClient phoneClient;
    private UpgradeState upgradeState;
    private ContentObserver observer;
    private boolean noUpdate = true;
    private IupDateListener updateListener;
    private ISystemCallBackInterface mSystemCallback = new ISystemCallBackInterface() {
        @Override
        public void onTBoxSystemInfoNotify(final TBoxSystemInfo info) {
            KLog.d(TAG, "TBoxSystemInfo = " + info.toString());

            if (isLauncherAppHome(context)) {
                if (info == null) {
                    return;
                }
                if (TextUtils.isEmpty(info.getImei())) {
                    return;
                }
                if (TextUtils.isEmpty(info.getVin())) {
                    return;
                }
                String tboxInfo = info.getImei() + "-" + info.getVin();
                FileUtils.writeCover(tboxInfo, ConfigManager.FileConfig.getLocalTboxConfigFile());
            }
        }

        @Override
        public void onTBoxNetworkInfoNotify(TBoxNetworkInfo info) {
            KLog.d(TAG, "TBoxNetworkInfo = " + info.toString());
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.SIMMode.SIM_MODE_ID, info));
        }

        @Override
        public void onLogExportProgress(final int progress) {
            KLog.d(TAG, "progress = " + progress);
        }

        @Override
        public void onLogExportState(final int state) {
            KLog.d(TAG, "state = " + state);
        }

        @Override
        public void onCellularData(boolean on) {
            KLog.d(TAG, "onCellularData: " + on);
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.SIMMode.SIM_SWITCH_ID, on));
        }
    };
    private IPhoneCallBackInterface mPhoneCallBack = new IPhoneCallBackInterface() {
        @Override
        public void onPhoneCallInfo(TBoxCallInfo info) {
            KLog.e("TboxCallInfo", "TBox CallBack Info: " + info.toString());
            if (info != null && (info.getState() == SDKConstants.TboxCallState.ANSWER || info.getState() == SDKConstants.TboxCallState.HANGUP_EXPIRE_FAIL)) {
                XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.TboxCallState.ID_TBOX_CALL_STATE, info.getState()));
            }
        }
    };
    private IWifiCallBackInterface mWifiCallback = new IWifiCallBackInterface() {

        @Override
        public void onWorkPattern(final int wp) {
            KLog.d(TAG, "wp = " + wp);
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiMode.WIFI_MODE_ID, wp));
        }

        @Override
        public void onWorkPatternChanged(final int wp) {
            KLog.d(TAG, "wp 状态变化 = " + wp);
            //todo 当wifi工作模式变化时，构造XmEvent分发出去
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiMode.WIFI_MODE_ID, wp));
        }

        @Override
        public void onTBoxWifiList(final TBoxWifiInfo[] infos) {
            KLog.d(TAG, "得到wifi列表");
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_LIST_EVENT, infos));
        }

        @Override
        public void onTBoxWifiListChange(final TBoxWifiInfo[] infos) {
            KLog.d(TAG, "wifi列表变化");
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_LIST_EVENT, infos));
        }

        /**
         * @param status {@link SDKConstants.WifiConnectStatus}
         */
        @Override
        public void onWifiConnectStatus(TBoxWiFiConnStatus status) {
            KLog.d(TAG, "wifi连接状态:" + status.getStatus());
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_CONNECT_STATUS_EVENT, status));
        }

        @Override
        public void onWifiConnectStatusChange(TBoxWiFiConnStatus status) {
            KLog.d(TAG, "wifi连接状态变化:" + status.getStatus());
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_CONNECT_STATUS_EVENT, status));
        }

        @Override
        public void onTBoxEndPointInfo(TBoxEndPointInfo[] infos) {
            KLog.d(TAG, "TBoxEndPointInfo : " + infos.length);
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_AP_INFO, infos));
        }

        @Override
        public void onHotSpot(TBoxHotSpot hotspot) {
            KLog.d(TAG, "onHotSpot : " + hotspot.ssid + ", " + hotspot.password);
            XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(SDKConstants.WifiAboutEventId.ID_WIFI_AP_ACCOUNT_INFO, hotspot));
        }

    };


    public static XmSystemManager getInstance() {
        if (instance == null) {
            synchronized (XmSystemManager.class) {
                if (instance == null) {
                    instance = new XmSystemManager();
                }
            }
        }
        return instance;
    }

    private XmSystemManager() {
    }

    public void init(Context context) {
        this.context = context;
        initTbox();
    }

    private void initTbox() {
        /*KLog.d("hzx","初始化TBox, 当前线程: " + Thread.currentThread().getName());
        final TboxBinderPool tboxBinderPool = TboxBinderPool.getInstance(context);
        tboxBinderPool.setConnListener(new TboxBinderPool.ServiceConnListener() {
            @Override
            public void onResult(int paramInt) {
                KLog.d("hzx","TBox初始化成功");
                if (paramInt == TboxBinderPool.BINDER_SUCCEED) {

                    IBinder systemBinder = tboxBinderPool.queryClient(TboxBinderPool.SYSTEM);
                    systemClient = new SystemClient(systemBinder);
                    //请求Tbox信息
                    systemClient.registerCallback(mSystemCallback);
                    systemClient.reqTBoxSystemInfo();
                    getCellulatData();

                    IBinder wifiBinder = tboxBinderPool.queryClient(TboxBinderPool.WIFI);
                    wifiClient = new WifiClient(wifiBinder);
                    wifiClient.registerCallback(mWifiCallback);

                    IBinder phoneBinder = tboxBinderPool.queryClient(TboxBinderPool.PHONE);
                    phoneClient = new PhoneClient(phoneBinder);
                    phoneClient.registerCallback(mPhoneCallBack);
                } else {
                    if (systemClient != null) {
                        systemClient.unregisterCallback(mSystemCallback);
                    }
                    if (wifiClient != null) {
                        wifiClient.unregisterCallback(mWifiCallback);
                    }
                    if (phoneClient != null) {
                        phoneClient.unregisterCallback(mPhoneCallBack);
                    }
                }
            }
        });
        tboxBinderPool.prepare();*/
        XmTboxBinderPoolManager.getInstance().init(context);
        XmTboxBinderPoolManager.getInstance().registerTboxbinderPoolConnectedListener(new XmTboxBinderPoolManager.OnTboxBinderPoolConnectionListener() {
            @Override
            public void onConnected(TboxBinderPool tboxBinderPool) {
                IBinder systemBinder = tboxBinderPool.queryClient(TboxBinderPool.SYSTEM);
                systemClient = new SystemClient(systemBinder);
                //请求Tbox信息
                systemClient.registerCallback(mSystemCallback);
                systemClient.reqTBoxSystemInfo();
                getCellulatData();

                IBinder wifiBinder = tboxBinderPool.queryClient(TboxBinderPool.WIFI);
                wifiClient = new WifiClient(wifiBinder);
                wifiClient.registerCallback(mWifiCallback);

                IBinder phoneBinder = tboxBinderPool.queryClient(TboxBinderPool.PHONE);
                phoneClient = new PhoneClient(phoneBinder);
                phoneClient.registerCallback(mPhoneCallBack);
            }

            @Override
            public void onDisconnected(TboxBinderPool tboxBinderPool) {
                if (systemClient != null) {
                    systemClient.unregisterCallback(mSystemCallback);
                }
                if (wifiClient != null) {
                    wifiClient.unregisterCallback(mWifiCallback);
                }
                if (phoneClient != null) {
                    phoneClient.unregisterCallback(mPhoneCallBack);
                }
            }
        });
        XmTboxBinderPoolManager.getInstance().prepare();
        initUpdateState();
    }

    private void initUpdateState() {
        upgradeState = new UpgradeState(context);
        observer = new ContentObserver(new Handler()) {
            //状态值改变时会收到此回调
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                noUpdate = upgradeState.getUpdateState() == UpgradeState.NO_UPDATE;
                if (updateListener != null) {
                    updateListener.onUpdateChange(noUpdate);
                }
            }
        };
        //注册ContentObserver
        upgradeState.register(observer);
        //初始化升级状态，非升级状态可正常启动，否则需不初始化语音相关
        noUpdate = upgradeState.getUpdateState() == UpgradeState.NO_UPDATE;
    }


    public boolean noUpdate() {
        if (upgradeState == null) {
            upgradeState = new UpgradeState(context);
            observer = new ContentObserver(new Handler()) {
                //状态值改变时会收到此回调
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    noUpdate = upgradeState.getUpdateState() == UpgradeState.NO_UPDATE;
                    if (updateListener != null) {
                        updateListener.onUpdateChange(noUpdate);
                    }
                }
            };
            //注册ContentObserver
            upgradeState.register(observer);
        }
        //初始化升级状态，非升级状态可正常启动，否则需不初始化语音相关
        noUpdate = upgradeState.getUpdateState() == UpgradeState.NO_UPDATE;
        Log.e(TAG, " update state: " + upgradeState.getUpdateState());
        return noUpdate;
    }

    public void setUpdateListener(IupDateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public boolean getBlueToothStatus() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isEnabled();
    }

    @Nullable
    private BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter adapter = null;
        if (context != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                adapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                adapter = bluetoothManager.getAdapter();
            }
        }
        return adapter;
    }

    @Override
    public boolean setBlueToothStatus(boolean status) {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        } else if (status) {
            return adapter.enable();
        } else {
            return adapter.disable();
        }
    }

    @Override
    public boolean getWIfiStatus() {
        return false;
    }

    @Override
    public void setWIfiStatus(boolean status) {

    }

    /**
     * @param status
     * @return (RESULT_ERROR : - 1 / RESULT_FAILED : 1 / RESULT_SUCCESS : 0)
     */
    @Override
    public int setDataSwitch(boolean status) {
        Log.e(TAG, "setDataSwitch: "+status);
        int code = -1;
        if (systemClient != null) {
            code = systemClient.setDataSwitch(status);
        }
        return code;
    }

    /**
     * @param status 入参参考 Constants.WifiMode
     * @return (RESULT_ERROR : - 1 / RESULT_FAILED : 1 / RESULT_SUCCESS : 0)
     */
    @Override
    public int setWorkPattern(int status) {
        int code = -1;
        if (wifiClient != null) {
            code = wifiClient.setWorkPattern(status);
        }
        return code;
    }

    @Override
    public void getWorkPattern() {
        if (wifiClient != null) {
            wifiClient.getWorkPattern();
        }
    }

    /**
     * @param operation 执行ICall的具体操作，{@link SDKConstants.CallOperation}
     * @return 暂时不清楚返回值含义
     */
    @Override
    public int operateTelePhoneICall(int operation) {
        int code = -1;
        if (phoneClient != null) {
            code = phoneClient.operateTelePhoneCall(operation, SDKConstants.CallType.ICALL);
            KLog.e(TAG, "operateTelePhoneICall" + SDKConstants.CallType.ICALL);
        }
        return code;
    }

    /**
     * @param operation 执行BCall的操作，{@link SDKConstants.CallOperation}.
     * @return 暂时不清楚返回值含义
     */
    @Override
    public int operateTelePhoneBCall(int operation) {
        int code = -1;
        if (phoneClient != null) {
            code = phoneClient.operateTelePhoneCall(operation, SDKConstants.CallType.BCALL);
            KLog.e(TAG, "operateTelePhoneBCall" + SDKConstants.CallType.BCALL);
        }
        return code;
    }


    @Override
    public int hangUpICall() {
        int code = -1;
        if (phoneClient != null) {
            code = phoneClient.operateTelePhoneCall(SDKConstants.CallOperation.HANDUP, SDKConstants.CallType.ICALL);
        }
        return code;
    }

    /**
     * operation 执行BCall的操作，{@link SDKConstants.CallOperation}.
     *
     * @return 暂时不清楚返回值含义
     */
    @Override
    public int hangUpBCall() {
        int code = -1;
        if (phoneClient != null) {
            code = phoneClient.operateTelePhoneCall(SDKConstants.CallOperation.HANDUP, SDKConstants.CallType.BCALL);
        }
        return code;
    }

    public void scanWifiList() {
        if (wifiClient != null) {
            wifiClient.scanWifiList();
        }
    }

    @Override
    public void getWifiList() {
        if (wifiClient != null) {
            wifiClient.getTBoxWifiList();
        }
    }

    @Override
    public void operateConnectedWifi(int op, String ssid) {
        if (wifiClient != null) {
            wifiClient.operateConnectedWifi(op, ssid);
        }
    }

    @Override
    public int connectWifi(int op, int auto, Object info) {
        int code = -1;
        if (info.getClass().equals(TBoxHotSpot.class)) {
            TBoxHotSpot tBoxHotSpot = (TBoxHotSpot) info;
            if (wifiClient != null) {
                try {
                    code = wifiClient.connectWifi(op, auto, tBoxHotSpot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }


    @Override
    public int setHotSpot(Object tBoxHotSpot) {
        if (tBoxHotSpot.getClass().equals(TBoxHotSpot.class)) {
            TBoxHotSpot boxHotSpot = (TBoxHotSpot) tBoxHotSpot;
            if (wifiClient != null) {
                int code = wifiClient.setHotspot(boxHotSpot);
                return code;
            }
        }
        return -1;
    }

    @Override
    public int setDataTrafficThreshold(String thresold) {
        if (wifiClient != null) {
            return wifiClient.setDataTrafficThreshold(thresold);
        }
        return -1;
    }

    @Override
    public void getWifiConnectStatus() {
        if (wifiClient != null) {
            wifiClient.getWifiConnectStatus();
        }
    }

    @Override
    public void getCellulatData() {
        if (systemClient != null) {
            KLog.d(TAG, "getCellulatData: ");
            systemClient.getCellulatData();
        }
    }

    @Override
    public void getHotSpot() {
        if (wifiClient != null) {
            wifiClient.getHotspot();
        }
    }

    private CarEvent carPropertyToXmEvent(int id, Object wp) {
        return new CarEvent(id, -1, wp);
    }

    private CarEvent carPropertyToXmEvent(int id, TBoxWifiInfo[] infos) {
        return new CarEvent(id, -1, infos);
    }

    private CarEvent carPropertyToXmEvent(int id, TBoxEndPointInfo[] infos) {
        return new CarEvent(id, -1, infos);
    }

    private boolean isLauncherAppHome(Context context) {
        return (context != null && LAUNCHER_PKG.equals(context.getPackageName()));
    }

    public interface IupDateListener {
        public void onUpdateChange(boolean noUpdate);
    }

}
