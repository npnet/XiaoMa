package com.xiaoma.push.handler;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.push.PushConstants;
import com.xiaoma.push.callback.ParameterCallback;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.HashMap;


/**
 * Created by blusehuang on 2017/6/8.
 */
//TODO:因为需要用户Id，所以需要在Launcher中去注册
public class UploadFileHandler implements IPushHandler {

    private ParameterCallback<String> userIdCallback;

    public UploadFileHandler(ParameterCallback<String> userIdCallback) {
        this.userIdCallback = userIdCallback;
    }

    @Override
    public void handle(PushMessage pm) {
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

    private void uploadFile(File file) {
        try {
            String fileName = file.getName();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

            HashMap<String, Object> params = new HashMap<>(2);
            params.put("userId", userIdCallback.get());
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
