package com.xiaoma.systemui;

import android.app.Application;
import android.content.Intent;

import com.xiaoma.carlib.wheelcontrol.XmWheelServiceManager;
import com.xiaoma.systemui.bussiness.BarStatusManager;
import com.xiaoma.systemui.bussiness.wheel.WheelKeyListener;
import com.xiaoma.systemui.common.controller.CarConnector;
import com.xiaoma.systemui.common.controller.TBoxConnector;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.navigationbar.NavigationBarController;
import com.xiaoma.systemui.topbar.controller.TopBarController;
import com.xiaoma.systemui.topbar.service.XMNotificationListenerService;

/**
 * Created by LKF on 2018/11/13 0013.
 */
public class SystemUIApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setShowLog(BuildConfig.DEBUG);
        // CarLib初始化
        CarConnector.getInstance().init(this);
        // TBox连接初始化
        TBoxConnector.getInstance().init(this);

        // 启动通知栏监听服务
        startService(new Intent(this, XMNotificationListenerService.class));

        // TODO: 测试图标
        //Test.setDebugIcons(this);
        // 状态栏初始化
        try {
            final TopBarController topBarController = TopBarController.getInstance();
            topBarController.init(this);
            topBarController.showStatusBar();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 状态栏图标初始化
        BarStatusManager.startup(this);

        //导航栏初始化
        try {
            final NavigationBarController navigationBarController = NavigationBarController.getInstance();
            navigationBarController.init(this);
            navigationBarController.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // 方控服务初始化
        CarConnector.getInstance().registerConnectListener(new WheelKeyListener(this));
        XmWheelServiceManager.startService(this);
    }
}