package com.xiaoma.launcher.recommend.push.handler;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.recommend.push.PushConstants;
import com.xiaoma.launcher.recommend.push.model.MqttMessage;
import com.xiaoma.login.UserManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.HashMap;


/**
 * Created by blusehuang on 2017/6/8.
 */
public class UploadFileHandler implements IMqttPushHandler {

    @Override
    public void handle(MqttMessage pm) {
        String path = pm.getData();
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        uploadContents(file);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_UPLOAD_FILE;
    }

    private void uploadContents(File file) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                String zipPath = ConfigManager.FileConfig.getLogFolder() + "/xiaomaUploadFiles.zip";
                try {
                    File zipFile = new File(zipPath);
                    if (!zipFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        zipFile.createNewFile();
                    }
                    ZipUtils.zipFile(file.getPath(), zipFile.getPath());
                    uploadFile(zipFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadFile(File file) {
        try {
            String fileName = file.getName();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

            HashMap<String, Object> params = new HashMap<>(2);
            params.put("userId", UserManager.getInstance().getCurrentUser().getId());
            params.put("ex", ext);
            XmHttp.getDefault().upFile(PushConstants.UPLOAD_LOG_FILE_URL, params, file, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    KLog.i("上传用户日志成功");
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    KLog.i("上传用户日志失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
