package com.xiaoma.assistant.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.api.AudioManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/21
 */
public class OpenAppUtils {

    public static boolean openApp(Context context, String packageName) {
        if (checkPackInfo(context, packageName)) {
            boolean isSuccess =  OpenActivityUtils.openPackage(context, packageName);
            return isSuccess;
        } else {
            return false;
        }
    }

    public static boolean checkPackInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static PendingHandle playBlutoothMusic() {
        if (!AudioManager.getInstance().isBluetoothConnected()) return null;
        return new PendingHandle();
    }

    public static PendingHandle playUsbMusic() {
        if (!AudioManager.getInstance().isUsbConnected()) return null;
        return new PendingHandle();
    }

    public static class PendingHandle {

        public void jumpBlutoothMusic(Context context) {
            NodeUtils.jumpTo(context, CenterConstants.MUSIC,
                    "com.xiaoma.music.MainActivity",
                    NodeConst.MUSIC.MAIN_ACTIVITY
                            + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                            + "/" + NodeConst.MUSIC.LOCAL_FRAGMENT
                            + "/" + NodeConst.MUSIC.BLUETOOTH_FRAGMENT);
        }

        public void jumpUsbMusic(Context context) {
            NodeUtils.jumpTo(context, CenterConstants.MUSIC,
                    "com.xiaoma.music.MainActivity",
                    NodeConst.MUSIC.MAIN_ACTIVITY
                            + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                            + "/" + NodeConst.MUSIC.LOCAL_FRAGMENT
                            + "/" + NodeConst.MUSIC.USB_FRAGMENT);
        }
    }
}
