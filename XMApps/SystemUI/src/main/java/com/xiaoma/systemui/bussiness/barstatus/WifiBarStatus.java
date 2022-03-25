package com.xiaoma.systemui.bussiness.barstatus;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.android.internal.statusbar.StatusBarIcon;
import com.fsl.android.TboxBinderPool;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.fsl.android.tbox.client.WifiClient;
import com.fsl.android.tbox.inter.IWifiCallBackInterface;
import com.fsl.android.tbox.other.Constants;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.controller.TBoxConnector;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.LogWriter;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class WifiBarStatus extends IWifiCallBackInterface implements BarStatus {
    private static final String TAG = "WifiBarStatus";
    private TBoxWiFiConnStatus mCurWifiStatus;
    private TBoxWifiInfo[] mCurScanResults;
    private int mWorkMode = -1;
    private boolean mShowing;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateTask;
    private WifiClient mWifiClient;
    private LogWriter mLogWriter;

    @Override
    public void startup(final Context context, final int iconLevel) {
        mLogWriter = new LogWriter(context, "WifiBarStatus.log");
        mLogWriter.startup();
        mUpdateTask = new Runnable() {
            @Override
            public void run() {
                update(context, iconLevel);
            }
        };
        // 连接TBox并获取状态
        connectTBoxWifiClient();
    }

    private void postUpdate() {
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

    private void update(Context context, int iconLevel) {
        // 热点模式
        if (Constants.WifiMode.AP == mWorkMode) {
            LogUtil.logE(TAG, "update( iconLevel: %s ) [ WifiWorkMode: %s ] AP mode", iconLevel, mWorkMode);
            mLogWriter.printLog("update( iconLevel: %s ) [ WifiWorkMode: %s ] AP mode", iconLevel, mWorkMode);
            StatusBarIcon icon = BarUtil.makeIcon(context, R.drawable.status_icon_hotspot, iconLevel);
            try {
                TopBarController.getInstance()
                        .getStatusBar()
                        .setIcon(TAG, icon);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            dispatchWifiVisibility(false);
            return;
        }

        // Wifi已关闭
        if (Constants.WifiMode.OFF == mWorkMode) {
            LogUtil.logE(TAG, "update( iconLevel: %s ) [ WifiWorkMode: %s ] Wifi OFF", iconLevel, mWorkMode);
            mLogWriter.printLog("update( iconLevel: %s ) [ WifiWorkMode: %s ] Wifi OFF", iconLevel, mWorkMode);
            removeIcon();
            dispatchWifiVisibility(false);
            return;
        }

        // Wifi工作模式
        if (Constants.WifiMode.STA == mWorkMode) {
            TBoxWiFiConnStatus status = mCurWifiStatus;
            if (status == null ||
                    Constants.ConnectStatus.CONNECTED != status.getStatus()) {
                removeIcon();
                dispatchWifiVisibility(false);
                LogUtil.logE(TAG, "update( iconLevel: %s ) Wifi not CONNECTED, status: %s",
                        iconLevel, status != null ? status.getStatus() : "null");
                mLogWriter.printLog("update( iconLevel: %s ) Wifi not CONNECTED, status: %s",
                        iconLevel, status != null ? status.getStatus() : "null");
                return;
            }
            // 从扫描结果中获取当前使用中的Wifi信号
            TBoxWifiInfo wifiInfo = null;
            TBoxWifiInfo[] scanResults = mCurScanResults;
            if (scanResults != null) {
                for (TBoxWifiInfo scanResult : scanResults) {
                    if (scanResult != null &&
                            Objects.equals(status.getSsid(), scanResult.getSsid())) {
                        wifiInfo = scanResult;
                        break;
                    }
                }
            }
            if (wifiInfo == null) {
                // 无法获取到扫描结果,但是当前获取到的是Wifi连接中的状态,默认为满信号
                LogUtil.logE(TAG, "update( iconLevel: %s ) WifiInfo is null", iconLevel);
                mLogWriter.printLog("update( iconLevel: %s ) WifiInfo is null", iconLevel);
                try {
                    TopBarController.getInstance()
                            .getStatusBar()
                            .setIcon(TAG, BarUtil.makeIcon(context, R.drawable.status_icon_wifi_signal_max, iconLevel));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dispatchWifiVisibility(true);
                return;
            }
            // 正常获取到Wifi信号
            final @DrawableRes int iconRes;
            final int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getSigStrength(), 4);
            LogUtil.logE(TAG, "update( iconLevel: %s ) { signalLevel: %s, sigStrength: %s }",
                    iconLevel, signalLevel, wifiInfo.getSigStrength());
            mLogWriter.printLog("update( iconLevel: %s ) { signalLevel: %s, sigStrength: %s }",
                    iconLevel, signalLevel, wifiInfo.getSigStrength());
            if (signalLevel <= 0) {
                iconRes = R.drawable.status_icon_wifi_signal_min;
            } else if (signalLevel == 1) {
                iconRes = R.drawable.status_icon_wifi_signal_2;
            } else if (signalLevel == 2) {
                iconRes = R.drawable.status_icon_wifi_signal_3;
            } else {
                iconRes = R.drawable.status_icon_wifi_signal_max;
            }
            try {
                TopBarController.getInstance()
                        .getStatusBar()
                        .setIcon(TAG, BarUtil.makeIcon(context, iconRes, iconLevel));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            dispatchWifiVisibility(true);
            return;
        }

        removeIcon();
        dispatchWifiVisibility(false);
        if (mWorkMode != -1) {
            // 未知的Wifi工作模式
            LogUtil.logE(TAG, "update( iconLevel: %s ) [ WifiWorkMode: %s ] Unknown wifi work mode !!!", iconLevel, mWorkMode);
            mLogWriter.printLog("update( iconLevel: %s ) [ WifiWorkMode: %s ] Unknown wifi work mode !!!", iconLevel, mWorkMode);
        } else {
            // Wifi工作模式尚未获取到
            LogUtil.logE(TAG, "update( iconLevel: %s ) [ WifiWorkMode: %s ] Wifi work mode did not gain...", iconLevel, mWorkMode);
            mLogWriter.printLog("update( iconLevel: %s ) [ WifiWorkMode: %s ] Wifi work mode did not gain...", iconLevel, mWorkMode);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    private void dispatchWifiVisibility(boolean visible) {
        mShowing = visible;
        if (mCallback != null) {
            mCallback.onWifiVisibleChanged(visible);
        }
    }

    private void removeIcon() {
        try {
            TopBarController.getInstance().getStatusBar().removeIcon(TAG);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void connectTBoxWifiClient() {
        LogUtil.logE(TAG, "connectTBoxWifiClient -> begin...");
        mLogWriter.printLog("connectTBoxWifiClient -> begin...");
        TBoxConnector.getInstance().registerConnectListener(new OnConnectStateListener<TboxBinderPool>() {
            @Override
            public void onConnected(@NonNull TboxBinderPool tBoxBinderPool) {
                LogUtil.logE(TAG, "connectTBoxWifiClient -> onConnected");
                mLogWriter.printLog("connectTBoxWifiClient -> onConnected");
                releaseClient();
                mWifiClient = new WifiClient(tBoxBinderPool.queryClient(TboxBinderPool.WIFI));
                mWifiClient.registerCallback(WifiBarStatus.this);
                mWifiClient.getWorkPattern();
                mWifiClient.getWifiConnectStatus();
                mWifiClient.getTBoxWifiList();
            }

            public void onDisconnected() {
                LogUtil.logE(TAG, "connectTBoxWifiClient -> onDisconnected");
                mLogWriter.printLog("connectTBoxWifiClient -> onDisconnected");
                releaseClient();
            }

            private void releaseClient() {
                if (mWifiClient != null) {
                    mWifiClient.unregisterCallback(WifiBarStatus.this);
                    mWifiClient = null;
                }
                mCurWifiStatus = null;
                mCurScanResults = null;
                mWorkMode = -1;
                postUpdate();
            }
        });
    }

    @Override
    public void onWifiConnectStatus(TBoxWiFiConnStatus status) {
        LogUtil.logE(TAG, "onWifiConnectStatus -> status: %s", status);
        mLogWriter.printLog("onWifiConnectStatus -> status: %s", status);
        mCurWifiStatus = status;
        postUpdate();
    }

    @Override
    public void onWifiConnectStatusChange(TBoxWiFiConnStatus status) {
        LogUtil.logE(TAG, "onWifiConnectStatusChange -> status: %s", status);
        mLogWriter.printLog("onWifiConnectStatusChange -> status: %s", status);
        mCurWifiStatus = status;
        postUpdate();
    }

    @Override
    public void onTBoxWifiList(TBoxWifiInfo[] infos) {
        LogUtil.logE(TAG, "onTBoxWifiList -> infos: %s", Arrays.toString(infos));
        mLogWriter.printLog("onTBoxWifiList -> infos: %s", Arrays.toString(infos));
        mCurScanResults = infos;
        postUpdate();
    }

    @Override
    public void onTBoxWifiListChange(TBoxWifiInfo[] infos) {
        LogUtil.logE(TAG, "onTBoxWifiListChange -> infos: %s", Arrays.toString(infos));
        mLogWriter.printLog("onTBoxWifiListChange -> infos: %s", Arrays.toString(infos));
        mCurScanResults = infos;
        postUpdate();
    }

    @Override
    public void onHotSpot(TBoxHotSpot hotspot) {
        LogUtil.logE(TAG, "onHotSpot -> hotspot: %s", hotspot);
        mLogWriter.printLog("onHotSpot -> hotspot: %s", hotspot);
    }

    @Override
    public void onWorkPattern(int wp) {
        LogUtil.logE(TAG, "onWorkPattern -> wp: %s", wp);
        mLogWriter.printLog("onWorkPattern -> wp: %s", wp);
        mWorkMode = wp;
        postUpdate();
    }

    @Override
    public void onWorkPatternChanged(int wp) {
        LogUtil.logE(TAG, "onWorkPatternChanged -> wp: %s", wp);
        mLogWriter.printLog("onWorkPatternChanged -> wp: %s", wp);
        mWorkMode = wp;
        postUpdate();
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void onWifiVisibleChanged(boolean isShow);
    }
}
