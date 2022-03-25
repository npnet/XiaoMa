package com.xiaoma.utils.screentool;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2019/5/20
 */
public class ScreenControlUtil {

    public static void sendTurnOnScreenBroadCast(Context context) {
        KLog.e("ScreenControlUtil", String.format("OnScreen %s", context.getPackageName()));
        Intent intent = new Intent();
        intent.setAction(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION);
        context.sendBroadcast(intent);
    }

    public static void sendTurnOffScreenBroadCast(Context context) {
        KLog.e("ScreenControlUtil", String.format("OffScreen %s", context.getPackageName()));
        Intent intent = new Intent();
        intent.setAction(VrConstants.ActionScreen.TURN_OFF_SCREEN_ACTION);
        context.sendBroadcast(intent);
    }
}
