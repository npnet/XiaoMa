package com.xiaoma.app;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Thomas on 2018/11/22 0022
 * 静默安装app manager
 */

public class SilentInstallManager {

    private static final String TAG = "com.xiaoma.app.SilentInstallManager";

    private boolean bindService = false;
    private static SilentInstallManager silentInstallManager = new SilentInstallManager();

    private SilentInstallManager() {
    }

    public static SilentInstallManager getInstance() {
        return silentInstallManager;
    }

    public boolean isBindService() {
        return bindService;
    }

    public void initService(Context context) {
        if (bindService) {
            return;
        }
    }

    /**
     * 安装
     *
     * @param appPathList
     */
    public void installApkFile(@NonNull List<String> appPathList) {
    }

    /**
     * 卸载
     *
     * @param appPathList
     */
    public void unInstallApkFile(@NonNull List<String> appPathList) {
    }
}
