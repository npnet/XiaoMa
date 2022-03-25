package com.xiaoma.faultreminder;

import android.app.Application;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.faultreminder.main.ui.MainActivity;
import com.xiaoma.faultreminder.sdk.FaultFactory;
import com.xiaoma.faultreminder.sdk.FaultListener;
import com.xiaoma.faultreminder.sdk.model.CarFault;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 12/26/2018
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.FaultReminder, flags = ShareConstants.TINKER_ENABLE_ALL)
public class FaultReminder extends BaseApp {
    public FaultReminder(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        FaultFactory.getSDK().init(getApplication());
        FaultFactory.getSDK().registerFaultListener(new FaultListener() {
            @Override
            public void onFaultOccur(List<CarFault> carFault) {
//                MainActivity.NewFault(getApplication(), new ArrayList<>(carFault));
            }
        });
        initAutoTracker();
    }

    private void initAutoTracker() {
        XmAutoTracker.getInstance().init(getApplication());
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{};
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    @Override
    protected PatchConfig obtainPatchConfig() {
        String baseVersion = BuildConfig.TINKER_BASE_VERSION;
        int patchVersion = BuildConfig.TINKER_PATCH_VERSION;
        PatchConfig patchConfig = new PatchConfig();
        patchConfig.setBasePkgVersion(baseVersion);
        patchConfig.setPatchVersion(patchVersion);
        return patchConfig;
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User data) {
                if (data != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
                }
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
            }
        });
    }
}
