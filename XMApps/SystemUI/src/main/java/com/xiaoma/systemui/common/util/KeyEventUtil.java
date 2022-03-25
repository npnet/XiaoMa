package com.xiaoma.systemui.common.util;

import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 * Created by LKF on 2018/11/8 0008.
 */
public class KeyEventUtil {
    private static final String TAG = "KeyEventUtil";

    /**
     * 发送一个按键点击事件
     *
     * @param keyCode 按键code
     */
    public static void sendClickKeyEvent(int keyCode) {
        LogUtil.logW(TAG, "sendClickKeyEvent -> [ keyCode: %s ]", keyCode);
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
    }

    private static KeyEvent makeKeyEvent(int keyCode, int action) {
        return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, keyCode,
                0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY, InputDevice.SOURCE_KEYBOARD);
    }
}
