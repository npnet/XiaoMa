package com.xiaoma.systemui.bussiness.barstatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.android.internal.statusbar.StatusBarIcon;
import com.fsl.android.TboxBinderPool;
import com.fsl.android.tbox.bean.TBoxNetworkInfo;
import com.fsl.android.tbox.client.SystemClient;
import com.fsl.android.tbox.inter.ISystemCallBackInterface;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.controller.TBoxConnector;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.LogWriter;
import com.xiaoma.systemui.topbar.controller.TopBarController;

/**
 * Created by LKF mIsMobileOn 2019-3-6 0006.5
 */
public class MobileSignalBarStatus extends ISystemCallBackInterface implements BarStatus {
    private static final String TAG = "MobileSignalBarStatus";
    private static final float TEXT_SIZE_NETWORK_TYPE = 16;
    private SystemClient mSystemClient;
    private boolean mIsMobileOn;
    private TBoxNetworkInfo mNetworkInfo;
    private Callback mCallback;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateTask;
    private LogWriter mLogWriter;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void startup(final Context context, final int iconLevel) {
        mLogWriter = new LogWriter(context, "MobileSignalBarStatus.log");
        mLogWriter.startup();
        mUpdateTask = new Runnable() {
            @Override
            public void run() {
                update(context, iconLevel);
            }
        };
        LogUtil.logE(TAG, "startup: TBoxConnector: registerConnectListener");
        mLogWriter.printLog("startup: TBoxConnector: registerConnectListener");
        TBoxConnector.getInstance().registerConnectListener(new OnConnectStateListener<TboxBinderPool>() {
            @Override
            public void onConnected(@NonNull TboxBinderPool tBoxBinderPool) {
                LogUtil.logE(TAG, "startup: TBoxConnector: onConnected");
                mLogWriter.printLog("startup: TBoxConnector: onConnected");
                releaseClient();
                mSystemClient = new SystemClient(tBoxBinderPool.queryClient(TboxBinderPool.SYSTEM));
                mSystemClient.registerCallback(MobileSignalBarStatus.this);
                mSystemClient.getCellulatData();
                LogUtil.logE(TAG, "startup: TBoxConnector: registerCallback And GetCellulatData");
                mLogWriter.printLog("startup: TBoxConnector: registerCallback And GetCellulatData");
            }

            @Override
            public void onDisconnected() {
                LogUtil.logE(TAG, "startup: TBoxConnector: onDisconnected");
                mLogWriter.printLog("startup: TBoxConnector: onDisconnected");
                releaseClient();
            }

            private void releaseClient() {
                if (mSystemClient != null) {
                    mSystemClient.unregisterCallback(MobileSignalBarStatus.this);
                    mSystemClient = null;
                }
                mIsMobileOn = false;
                mNetworkInfo = null;
                postUpdate();
            }
        });
    }

    private void update(Context context, int iconLevel) {
        // 无移动网络
        if (mNetworkInfo == null) {
            removeIcon();
            return;
        }
        // 网络制式:4G/3G/2G
        final int networkClass = mNetworkInfo.getNetworkType();
        // sim卡错误
        if (SDKConstants.SIMMode.SIM_CARD_ERROR == networkClass) {
            LogUtil.logE(TAG, "update() [ networkClass: SIM_CARD_ERROR ]");
            mLogWriter.printLog("update() [ networkClass: SIM_CARD_ERROR ]");
            removeIcon();
            return;
        }
        // 信号强度
        byte signalLevel = mNetworkInfo.getRSSIAbsoluteValue(); // 信号强度等级
        // 无信号
        if (signalLevel <= 0) {
            LogUtil.logE(TAG, "update() [ signalLevel: %s, networkClass: %s ] No signal", signalLevel, networkClass);
            mLogWriter.printLog(String.format("update() [ signalLevel: %s, networkClass: %s ] No signal", signalLevel, networkClass));
            try {
                TopBarController.getInstance().getStatusBar().setIcon(TAG,
                        BarUtil.makeIcon(context, context.getString(R.string.status_text_out_out_service), iconLevel));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
        // 有信号
        int i = SDKConstants.SIMMode.SIM_RSSI_MAX / 4;
        final @DrawableRes int signalDrRes;
        if (signalLevel <= i) {
            // 信号差
            signalDrRes = R.drawable.status_icon_signal_min;
        } else if (signalLevel <= 2 * i) {
            // 信号一般
            signalDrRes = R.drawable.status_icon_signal_3;
        } else if (signalLevel > 2 * i && signalLevel <= 3 * i) {
            // 信号不错
            signalDrRes = R.drawable.status_icon_signal_5;
        } else {
            // 信号等级大于等4表示信号非常好
            signalDrRes = R.drawable.status_icon_signal_max;
        }
        String typeText = "";
        // 流量开启+Wifi关闭时才显示网络类型
        if (mIsMobileOn &&
                (mCallback == null || !mCallback.isWifiStatusShowing())) {
            switch (networkClass) {
                case SDKConstants.SIMMode.FOUR_G:
                    typeText = "4G";
                    break;
                case SDKConstants.SIMMode.THREE_G:
                    typeText = "3G";
                    break;
                case SDKConstants.SIMMode.TWO_G:
                    typeText = "2G";
                    break;
                case SDKConstants.SIMMode.N0_SERVICE:
                    typeText = "无服务";
                    break;
                default:
                    typeText = "E";
            }
        }
        try {
            TopBarController.getInstance()
                    .getStatusBar()
                    .setIcon(TAG, makeIcon(context, signalDrRes, typeText, iconLevel));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LogUtil.logE(TAG, "update() [ signalLevel: %s, typeText: %s ,wifiShowing: %s ]",
                signalLevel, typeText, mCallback != null ? mCallback.isWifiStatusShowing() : "null callback");
        mLogWriter.printLog(String.format("update() [ signalLevel: %s, typeText: %s ,wifiShowing: %s ]",
                signalLevel, typeText, mCallback != null ? mCallback.isWifiStatusShowing() : "null callback"));
    }

    private StatusBarIcon makeIcon(@NonNull Context context, @DrawableRes int drawableRes, @NonNull String text, int iconLevel) {
        final Drawable dr = context.getDrawable(drawableRes);
        if (dr == null)
            return null;
        final Bitmap bmp = Bitmap.createBitmap(dr.getIntrinsicWidth(), dr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        dr.draw(canvas);

        final Paint paint = BarUtil.getTextPaint(context);
        paint.setTextSize(TEXT_SIZE_NETWORK_TYPE);
        Paint.FontMetrics fm = paint.getFontMetrics();
        canvas.drawText(text, 0, fm.bottom - fm.top, paint);
        return BarUtil.makeIcon(context, bmp, iconLevel);
    }

    private void removeIcon() {
        try {
            TopBarController.getInstance().getStatusBar().removeIcon(TAG);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCellularData(boolean on) {
        LogUtil.logE(TAG, "onCellularData -> on: %s ", on);
        mLogWriter.printLog(String.format("onCellularData -> on: %s ", on));
        mIsMobileOn = on;
        postUpdate();
    }

    @Override
    public void onTBoxNetworkInfoNotify(TBoxNetworkInfo info) {
        LogUtil.logE(TAG, "onTBoxNetworkInfoNotify -> info: %s ", info);
        mLogWriter.printLog(String.format("onTBoxNetworkInfoNotify -> info: %s ", info));
        mNetworkInfo = info;
        postUpdate();
    }

    public void postUpdate() {
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

    public interface Callback {
        boolean isWifiStatusShowing();
    }
}