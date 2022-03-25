//package com.xiaoma.systemui.common;
//
//import android.app.Application;
//import android.content.Intent;
//
//import com.tencent.tinker.anno.DefaultLifeCycle;
//import com.tencent.tinker.loader.shareutil.ShareConstants;
//import com.xiaoma.hotfix.HotfixConstants;
//import com.xiaoma.hotfix.TinkerAppProxy;
//import com.xiaoma.systemui.common.service.SystemUIService;
//
//
///**
// * Created by LKF on 2018/11/1 0001.
// */
//@SuppressWarnings("unused")
//@DefaultLifeCycle(application = HotfixConstants.App.SystemUI, flags = ShareConstants.TINKER_ENABLE_ALL)
//public class SystemUI extends TinkerAppProxy {
//
//    public SystemUI(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
//        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        getApplication().startService(new Intent(getApplication(), SystemUIService.class));
//    }
//}