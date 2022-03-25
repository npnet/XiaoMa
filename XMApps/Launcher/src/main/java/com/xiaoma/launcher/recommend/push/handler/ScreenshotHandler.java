package com.xiaoma.launcher.recommend.push.handler;

import android.content.Intent;

import com.xiaoma.autotracker.AutoTrackerConstants;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.recommend.push.model.MqttMessage;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
public class ScreenshotHandler implements IMqttPushHandler {

    @Override
    public void handle(MqttMessage pm) {
        Intent intent = new Intent(AutoTrackerConstants.PUSH_UPLOAD_SCREENSHOT);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_UPLOAD_SCREENSHOT;
    }
}
