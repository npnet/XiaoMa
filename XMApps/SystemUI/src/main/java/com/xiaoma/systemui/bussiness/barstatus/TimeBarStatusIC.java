package com.xiaoma.systemui.bussiness.barstatus;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarVendorExtensionManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.controller.CarConnector;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * 获取IC时间显示的实现
 */
public class TimeBarStatusIC implements BarStatus {
    private static final String TAG = "TimeBarStatusIC";
    private final DecimalFormat mMinuteDf = new DecimalFormat("00");
    private Integer[] mTimeArr;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private CarVendorExtensionManager mManager;

    @Override
    public void startup(final Context context, final int iconLevel) {
        CarConnector.getInstance().registerConnectListener(new OnConnectStateListener<Car>() {
            @Override
            public void onConnected(@NonNull Car car) {
                try {
                    mManager = (CarVendorExtensionManager) car.getCarManager(Car.VENDOR_EXTENSION_SERVICE);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onDisconnected() {
                mManager = null;
            }
        });
        // 开启时间轮询任务
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mManager != null) {
                    try {
                        mTimeArr = mManager.getGlobalProperty(Integer[].class, CarVendorExtensionManager.ID_CAN_TIME_INFO);
                    } catch (CarNotConnectedException e) {
                        e.printStackTrace();
                    }
                }
                updateTime(context, iconLevel);
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private void updateTime(Context context, int iconLevel) {
        try {
            Integer[] timeArr = mTimeArr;
            if (timeArr == null) {
                LogUtil.logE(TAG, "updateTime( timeArr: null )");
                removeIcon();
                return;
            }
            int hour = timeArr[0];
            int minute = timeArr[1];
            String timeText = mMinuteDf.format(hour) + ":" + mMinuteDf.format(minute);
            LogUtil.logI(TAG, "updateTime( timeArr: %s timeText: %s )", Arrays.toString(timeArr), timeText);
            StatusBarIcon icon = BarUtil.makeIcon(context, timeText, iconLevel);
            try {
                TopBarController.getInstance().getStatusBar().setIcon(TAG, icon);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            removeIcon();
        }
    }

    private void removeIcon() {
        try {
            TopBarController.getInstance().getStatusBar().removeIcon(TAG);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}