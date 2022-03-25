package com.xiaoma.dualscreen.utils;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import java.security.MessageDigest;

/**
 * @author: iSun
 * @date: 2018/12/17 0017
 */
public class PresentationUtils {

    private static final String TAG = PresentationUtils.class.getSimpleName();

    public static void showPresentation(Context context, Presentation presentation) {
        if (context == null || presentation == null) {
            return;
        }
        if (presentation != null && Settings.canDrawOverlays(context)) {
            presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            presentation.show();
        } else if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    public static boolean isSecondaryDisplays(Context context) {
        boolean result = false;
        if (context == null) {
            return false;
        }
        DisplayManager mDisplayManager;
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays != null && displays.length > 1) {
            result = true;
        }
        return result;
    }

    public static Display getSecondaryDisplays(Context context) {
        if (context == null) {
            return null;
        }
        DisplayManager mDisplayManager;
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays != null && displays.length > 1) {
            return displays[1];
        }
        return null;
    }

    public static Display getDisplayForName(Context context, String name) {
        if (context == null || TextUtils.isEmpty(name)) {
            return null;
        }
        Display result = null;
        DisplayManager mDisplayManager;
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        for (Display display : displays) {
            if (name.equals(display.getName())) {
                result = display;
                break;
            }
        }
        return result;
    }

    public static String MD5(String key) {
        if (TextUtils.isEmpty(key) || key.length() != 32) {
            return null;
        }
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            StringBuilder pass = new StringBuilder();
            //6,8,10,12,14,18
            pass.append(str[5]).append(str[7]).append(str[9]).append(str[11]).append(str[13]).append(str[17]);
            return pass.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
