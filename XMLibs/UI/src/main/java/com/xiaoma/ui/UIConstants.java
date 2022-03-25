package com.xiaoma.ui;

import android.content.Context;

/**
 * Created by youthyj on 2018/9/7.
 */
public class UIConstants {

    public static final String LAUNCHER_PKG = "com.xiaoma.launcher";
    public static final String LAUNCHER_MAINACTIVITY = "com.xiaoma.launcher.main.ui.MainActivity";

    private UIConstants() throws Exception {
        throw new Exception();
    }

    public static final boolean SHOW_NAVI_BAR = true;

    public static boolean isLauncherAppHome(Context context) {
        return (context != null && LAUNCHER_PKG.equals(context.getPackageName()));
    }
}
