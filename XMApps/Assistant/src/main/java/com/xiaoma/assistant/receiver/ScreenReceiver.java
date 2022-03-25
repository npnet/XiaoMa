package com.xiaoma.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.assistant.ui.ScreenDialog;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2019/5/6
 */
public class ScreenReceiver extends BroadcastReceiver {
    private final String TAG = ScreenReceiver.class.getSimpleName();

    private ScreenDialog screenDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(VrConstants.ActionScreen.TURN_OFF_SCREEN_ACTION)) {
            KLog.d(TAG, action);
            if (screenDialog == null) {
                screenDialog = new ScreenDialog(context);
            }
            if (!screenDialog.isShowing()) {
                KLog.d(TAG, "screenDialog show");
                screenDialog.show();
            }
        } else if (action.equals(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION) || action.equals(VrConstants.ActionScreen.SHOW_VOICE_ASSISTANT_DIALOG)) {
            KLog.d(TAG, action);
            if (screenDialog != null && screenDialog.isShowing()) {
                KLog.d(TAG, "screenDialog dismiss");
                screenDialog.dismiss();
            }
        }
    }
}
