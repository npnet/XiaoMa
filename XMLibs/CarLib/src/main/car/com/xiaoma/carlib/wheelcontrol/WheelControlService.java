package com.xiaoma.carlib.wheelcontrol;

import android.annotation.SuppressLint;
import android.car.input.CarInputHandlingService;
import android.car.input.HardKeyEvent;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by LKF on 2019-5-6 0006.
 */
@SuppressLint("LogNotTimber")
public class WheelControlService extends CarInputHandlingService {
    private static final String TAG = "WheelControlService";

    // 仪表按键 begin
    private static final Set<Integer> LCD_KEY_CODES;

    static {
        LCD_KEY_CODES = new ArraySet<>(Arrays.asList(
                WheelKeyEvent.KEYCODE_WHEEL_UP,
                WheelKeyEvent.KEYCODE_WHEEL_DOWN,
                WheelKeyEvent.KEYCODE_WHEEL_LEFT,
                WheelKeyEvent.KEYCODE_WHEEL_RIGHT,
                WheelKeyEvent.KEYCODE_WHEEL_OK,
                WheelKeyEvent.KEYCODE_WHEEL_BACK
        ));
    }
    // 仪表按键 end

    public WheelControlService() {
        this(new InputFilter[0]);
    }

    protected WheelControlService(InputFilter[] handledKeys) {
        super(handledKeys);
        StringBuilder sb = new StringBuilder("[ ");
        for (final InputFilter key : handledKeys) {
            sb.append("{ ")
                    .append(String.format("keyCode: %s, tarDisplay: %s", key.mKeyCode, key.mTargetDisplay))
                    .append(" },")
            ;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ]");
        Log.i(TAG, String.format("WheelControlService( handledKeys: %s )", sb.toString()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, String.format("onBind( intent: %s )", intent));
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, String.format("onUnbind( intent: %s )", intent));
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("onStartCommand( intent: %s, flags: %s, startId: %s )", intent, flags, startId));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onKeyEvent(KeyEvent event, int targetDisplay) {
        Log.i(TAG, String.format("onKeyEvent( event: %s, targetDisplay: %s )", event, targetDisplay));
    }

    @Override
    protected void onHardKeyEvent(HardKeyEvent event) {
        Log.i(TAG, String.format("onHardKeyEvent( keyEvent: %s )", event));
        final int keyCode = event.getCode();
        // 由于HU给过来的仪表按键码错乱,目前通过CarVendorExtension发送过来,不走onHardKeyEvent
        if (LCD_KEY_CODES.contains(keyCode))
            return;
        final int keyAction = event.getAction();
        sendBroadcast(new Intent(WheelConstant.ACTION_WHEEL_KEY_EVENT)
                .putExtra(WheelConstant.EXTRA_KEY_ACTION, keyAction)
                .putExtra(WheelConstant.EXTRA_KEY_CODE, keyCode));
    }
}
