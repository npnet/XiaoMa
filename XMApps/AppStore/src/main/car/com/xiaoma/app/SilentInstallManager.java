package com.xiaoma.app;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.fsl.android.OtaBinderPool;
import com.fsl.android.ota.OtaBinder;
import com.fsl.android.ota.inter.IAppUpdateInterface;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.util.AppNotificationHelper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * Created by Thomas on 2018/11/22 0022
 * 静默安装app manager
 */

public class SilentInstallManager {
    private static final String TAG = "SilentInstallManager";
    private static final String LAUNCHER = "com.xiaoma.launcher";
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;

    private Context context;
    private boolean bindService = false;
    private static SilentInstallManager silentInstallManager = new SilentInstallManager();
    private OtaBinder mOtaBinder;

    private void uninstallFinish(String pkgname, int result) {
        KLog.d("onUninstallResult:" + (result == SUCCESS) + " apk name:" + pkgname);
        AppStateEvent stateEvent = new AppStateEvent();
        stateEvent.setAppState(AppStateEvent.STATE_PACKAGE_REMOVED);
        stateEvent.setPackageName(pkgname);
        stateEvent.setResult(result == SUCCESS);
        EventBus.getDefault().post(stateEvent, AppStoreConstants.APP_INSTALL_RECEIVER);
    }

    private void installFinish(String pkgname, int result) {
        KLog.d("installState:" + (result == SUCCESS) + " apk name:" + pkgname);
        AppStateEvent stateEvent = new AppStateEvent();
        stateEvent.setAppState(AppStateEvent.STATE_PACKAGE_ADDED);
        stateEvent.setPackageName(pkgname);
        stateEvent.setResult(result == SUCCESS);
        EventBus.getDefault().post(stateEvent, AppStoreConstants.APP_INSTALL_RECEIVER);

        if (result == SUCCESS) {
            AppNotificationHelper.getInstance()
                    .handleAppNotification(context, AppNotificationHelper.APP_UPDATE_COMPLETE, "",
                            AppUtils.getAppName(context, pkgname), String.format(context.getString(R.string.install_success),
                                    AppUtils.getAppName(context, pkgname)), pkgname, System.currentTimeMillis());
            if (LAUNCHER.equals(pkgname)) {
                KeyEventUtils.isGoHome(context, pkgname);
            }
        } else {
            AppNotificationHelper.getInstance()
                    .handleAppNotification(context, AppNotificationHelper.APP_INSTALL_FAILED, "",
                            AppUtils.getAppName(context, pkgname), String.format(context.getString(R.string.install_failed),
                                    AppUtils.getAppName(context, pkgname)), pkgname, System.currentTimeMillis());
        }
    }

    private SilentInstallManager() {
    }

    public static SilentInstallManager getInstance() {
        return silentInstallManager;
    }

    public boolean isBindService() {
        KLog.e(TAG,"is bind success" + bindService);
        return bindService;
    }

    public void initService(Context context) {
        KLog.e(TAG,"initService ");
        if (bindService) {
            return;
        }
        this.context = context;
        final OtaBinderPool binderPool = OtaBinderPool.getInstance(context);
        binderPool.setConnListener(new OtaBinderPool.ServiceConnListener() {
            @Override
            public void onResult(int i) {
                if (i == OtaBinderPool.BINDER_SUCCEED) {
                    KLog.e(TAG,"initService SUCCESS");
                    bindService = true;
                    IBinder binder = binderPool.queryClient(OtaBinderPool.LOCAL);
                    mOtaBinder = new OtaBinder(binder);
                    mOtaBinder.registerAppListener(new IAppUpdateInterface() {
                        @Override
                        public void onInstallResult(String path, String pkgName, int result) throws RemoteException {
                            super.onInstallResult(path, pkgName, result);
                            installFinish(pkgName, result);
                        }

                        @Override
                        public void onUninstallResult(String pkgName, int result) throws RemoteException {
                            super.onUninstallResult(pkgName, result);
                            uninstallFinish(pkgName, result);
                        }
                    });
                }else {
                    KLog.e(TAG,"initService FAILED");
                }
            }
        });
        binderPool.prepare();
    }

    /**
     * 安装
     *
     * @param appPathList
     */
    public void installApkFile(@NonNull List<String> appPathList) {
        if (mOtaBinder == null) {
            XMToast.showToast(context, "ota binder init exception");
            KLog.d("installApkFile null binder");
            return;
        }
        try {
            mOtaBinder.installAppSilent(appPathList);
        } catch (Exception e) {
            XMToast.showToast(context, "install failed");
            e.printStackTrace();
        }
    }

    /**
     * 卸载
     *
     * @param appPathList
     */
    public void unInstallApkFile(@NonNull List<String> appPathList) {
        if (mOtaBinder == null) {
            XMToast.showToast(context, "ota binder init exception");
            KLog.d("unInstallApkFile null binder");
            return;
        }
        try {
            mOtaBinder.uninstallAppSilent(appPathList);
        } catch (Exception e) {
            XMToast.showToast(context, R.string.uninstall_failed);
            e.printStackTrace();
        }
    }
}
