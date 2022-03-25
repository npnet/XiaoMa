package com.xiaoma.utils;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 * @author youthyJ
 * @date 2018/10/24
 */
public class KeyEventUtils {

    private static final String LAUNCHER_PKG = "com.xiaoma.launcher";
    private static final String LAUNCHER_MAINACTIVITY = "com.xiaoma.launcher.main.ui.MainActivity";
    public static final String GO_HOME_MAP = "go_home_map";
    public static final String GO_HOME_SERVICE = "go_home_service";

    private KeyEventUtils() throws Exception {
        throw new Exception();
    }

    public static boolean isGoHome(Context context,String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        if (LAUNCHER_PKG.equals(packageName)) {
            sendKeyEvent(context, KeyEvent.KEYCODE_HOME);
            return true;
        }
        return false;
    }

    public static void sendKeyEvent(Context context, final int keyCode) {
        if ("CAR".equalsIgnoreCase(BuildConfig.BUILD_PLATFORM)) {
            try {
                final InputManager inputManager = InputManager.getInstance();

                inputManager.injectInputEvent(
                        makeKeyEvent(keyCode, KeyEvent.ACTION_DOWN),
                        InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);

                inputManager.injectInputEvent(
                        makeKeyEvent(keyCode, KeyEvent.ACTION_UP),
                        InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sendKeyEventPadImpl(context, keyCode);
        }
    }

    /**
     * 导航时需要先回桌面  桌面进行弹占形式
     *
     * @param context
     */
    public static void goHome(Context context, String key) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(key, true);
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(LAUNCHER_PKG, LAUNCHER_MAINACTIVITY);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendKeyEventPadImpl(Context context, final int keyCode) {
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            if (context == null) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
            return;
        }
        new Thread() {     //不可在主线程中调用
            public void run() {
                try {
                    new Instrumentation().sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static KeyEvent makeKeyEvent(int keyCode, int action) {
        return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, keyCode,
                0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY, InputDevice.SOURCE_KEYBOARD);
    }
}
