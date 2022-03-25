package com.xiaoma.launcher.recommend.push.handler;


import android.content.Intent;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.recommend.push.model.MqttMessage;
import com.xiaoma.utils.StringUtil;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
class InstallFileHandler implements IMqttPushHandler {

    private final String TAG = "InstallHandler";

    @Override
    public void handle(MqttMessage pm) {
        downloadFile(pm);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_INSTALL_FILE;
    }

    private void downloadFile(MqttMessage message) {
        if (StringUtil.isNotEmpty(message.getData())) {
            //通知车应用去下载安装
            sendInstallBroadCast(message.getData(), message.getMd5File());
        }
    }

    private void sendInstallBroadCast(String filePath, String md5File) {
        Intent intent = new Intent(LauncherConstants.INSTALL_ACTION);
        intent.putExtra(LauncherConstants.INSTALL_PATH, filePath);
        intent.putExtra(LauncherConstants.INSTALL_FILE_MD5, md5File);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }
}
