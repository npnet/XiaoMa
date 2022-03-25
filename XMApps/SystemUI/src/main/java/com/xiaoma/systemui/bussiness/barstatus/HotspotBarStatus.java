//package com.xiaoma.systemui.bussiness.barstatus;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.RemoteException;
//
//import com.android.internal.statusbar.IStatusBar;
//import com.android.internal.statusbar.StatusBarIcon;
//import com.xiaoma.carlib.XmCarFactory;
//import com.xiaoma.carlib.constant.SDKConstants;
//import com.xiaoma.carlib.manager.ICarEvent;
//import com.xiaoma.carlib.manager.XmCarEventDispatcher;
//import com.xiaoma.carlib.model.CarEvent;
//import com.xiaoma.systemui.R;
//import com.xiaoma.systemui.bussiness.BarUtil;
//import com.xiaoma.systemui.common.util.LogUtil;
//import com.xiaoma.systemui.topbar.controller.TopBarController;
//
///**
// * Created by LKF on 2019-3-6 0006.
// */
//public class HotspotBarStatus implements BarStatus, ICarEvent {
//    private static final String TAG = "HotspotBarStatus";
//    private IStatusBar mStatusBar;
//    private int iconLevel = -1;
//    private Context context;
//    private final Handler mHandler = new Handler(Looper.getMainLooper());
//    private Runnable mWorkModePollTask;
//
//    @Override
//    public void startup(final Context context, final int iconLevel) {
//        this.iconLevel = iconLevel;
//        this.context = context;
//        mStatusBar = TopBarController.getInstance().getStatusBar();
//        update(false);
//        XmCarEventDispatcher.getInstance().registerEvent(this);
//        // 轮询AP工作模式
//        mWorkModePollTask = new Runnable() {
//            @Override
//            public void run() {
//                XmCarFactory.getSystemManager().getWorkPattern();
//                mHandler.postDelayed(this, 3000);
//            }
//        };
//        mHandler.post(mWorkModePollTask);
//    }
//
//    private void update(boolean isApOpen) {
//        if (isApOpen) {
//            LogUtil.logI(TAG, "update( iconLevel: %s ) AP enable ~", iconLevel);
//            final StatusBarIcon icon = BarUtil.makeIcon(context, R.drawable.status_icon_hotspot, iconLevel);
//            try {
//                mStatusBar.setIcon(TAG, icon);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } else {
//            LogUtil.logI(TAG, "update( iconLevel: %s ) AP is disable !", iconLevel);
//            removeIcon();
//        }
//    }
//
//    private void removeIcon() {
//        try {
//            mStatusBar.removeIcon(TAG);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onCarEvent(CarEvent event) {
//        if (SDKConstants.WifiMode.WIFI_MODE_ID == event.id) {
//            int wifiMode = (int) event.value;
//            if (wifiMode == SDKConstants.WifiMode.AP) {
//                //热点开启
//                update(true);
//            } else {
////                if (wifiWorkMode == -1 || wifiWorkMode == SDKConstants.WifiMode.AP) {
//                //热点关闭
//                update(false);
////                }
//            }
////            wifiWorkMode = wifiMode;
//        }
//    }
//}
