//package com.xiaoma.carwxsdkimpl;
//
//import android.app.Application;
//import android.content.Intent;
//
//import com.tencent.tinker.anno.DefaultLifeCycle;
//import com.tencent.tinker.loader.shareutil.ShareConstants;
//import com.xiaoma.component.base.BaseApp;
//import com.xiaoma.hotfix.model.PatchConfig;
//import com.xiaoma.network.XmHttp;
//
//import static com.xiaoma.hotfix.HotfixConstants.App.CarWXSDKImpl;
//
//@DefaultLifeCycle(application = CarWXSDKImpl,flags = ShareConstants.TINKER_ENABLE_ALL)
//public class CarWXSDKImpl extends BaseApp {
//    public CarWXSDKImpl(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
//        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
//    }
//
//    @Override
//    protected String[] allNeedPermissions() {
//        return new String[0];
//    }
//
//    @Override
//    public void initLibs() {
//        super.initLibs();
//        XmHttp.getDefault().init(getApplication());
//    }
//
//    @Override
//    public Class<?> firstActivity() {
//        return MainActivity.class;
//    }
//
//    @Override
//    protected PatchConfig obtainPatchConfig() {
//        String baseVersion = BuildConfig.TINKER_BASE_VERSION;
//        int patchVersion = BuildConfig.TINKER_PATCH_VERSION;
//        PatchConfig patchConfig = new PatchConfig();
//        patchConfig.setBasePkgVersion(baseVersion);
//        patchConfig.setPatchVersion(patchVersion);
//        return patchConfig;
//    }
//}
