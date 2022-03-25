package com.xiaoma.hotfix;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.xiaoma.hotfix.crash.TinkerUncaughtExceptionHandler;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.hotfix.reporter.XMLoadReporter;
import com.xiaoma.hotfix.reporter.XMPatchListener;
import com.xiaoma.hotfix.reporter.XMPatchReporter;
import com.xiaoma.hotfix.service.TinkerResultService;
import com.xiaoma.hotfix.service.TinkerUpgradeService;


/**
 * Tinker使用apt在编译的时候生成真正的Application
 * TinkerAppProxy只是一个代理类
 */
public abstract class TinkerAppProxy extends DefaultApplicationLike {
    private static TinkerAppProxy instance;
    private TinkerHelper helper = new TinkerHelper();

    public static TinkerAppProxy getInstance() {
        return instance;
    }

    public TinkerAppProxy(Application app, int flags, boolean verify,
                          long appStartElapsedTime, long appStartMillisTime,
                          Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
        instance = this;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
        helper.initFastCrashProtect();
        helper.setUpgradeRetryEnable(true);
        helper.installTinker();
        Tinker.with(getApplication());
    }

    public void startTinkerUpgradeService() {
        try {
            PatchConfig patchConfig = obtainPatchConfig();
            TinkerUpgradeService.launch(getApplication(), patchConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract PatchConfig obtainPatchConfig();

    private class TinkerHelper {
        private static final String TAG = "Tinker.TinkerHelper";
        private TinkerUncaughtExceptionHandler exceptionHandler;
        private boolean isInstalled = false;

        private void initFastCrashProtect() {
            if (exceptionHandler == null) {
                exceptionHandler = new TinkerUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            }
        }

        private void setUpgradeRetryEnable(boolean enable) {
            UpgradePatchRetry.getInstance(getApplication()).setRetryEnable(enable);
        }

        private void installTinker() {
            if (isInstalled) {
                TinkerLog.w(TAG, "install tinker, but has installed, ignore");
                return;
            }
            ApplicationLike appLike = TinkerAppProxy.this;
            LoadReporter loadReporter = new XMLoadReporter(getApplication());
            PatchReporter patchReporter = new XMPatchReporter(getApplication());
            PatchListener patchListener = new XMPatchListener(getApplication());
            AbstractPatch upgradePatchProcessor = new UpgradePatch();

            TinkerInstaller.install(
                    appLike,
                    loadReporter,
                    patchReporter,
                    patchListener,
                    TinkerResultService.class,
                    upgradePatchProcessor
            );

            isInstalled = true;
        }
    }
}
