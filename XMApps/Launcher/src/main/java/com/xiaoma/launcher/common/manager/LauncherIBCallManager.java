package com.xiaoma.launcher.common.manager;

/**
 * @author taojin
 * @date 2019/7/26
 */
public class LauncherIBCallManager {

    public static boolean isIBCall = false;


    public static void setIsIBCall(boolean ibCall) {
        isIBCall = ibCall;
    }

    public static boolean isIBCall() {
        return isIBCall;
    }
}
