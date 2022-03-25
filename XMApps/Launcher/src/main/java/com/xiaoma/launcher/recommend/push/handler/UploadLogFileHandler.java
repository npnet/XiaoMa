package com.xiaoma.launcher.recommend.push.handler;

import android.text.TextUtils;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.common.api.LauncherAPI;
import com.xiaoma.launcher.recommend.push.PushConstants;
import com.xiaoma.launcher.recommend.push.model.MqttMessage;
import com.xiaoma.login.UserManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.log.LogConfig;

import java.io.File;
import java.util.HashMap;


/**
 * Created by blusehuang on 2017/6/8.
 */
public class UploadLogFileHandler implements IMqttPushHandler {

    @Override
    public void handle(MqttMessage pm) {
        String tag = pm.getTag();
        String packageName = pm.getData();
//        { "action":5, //代表日志文件压缩上传
//            "tag":"adb", //代表是否为adb获取日志类型还是直接压缩com.xiaoma/log下对应包名日志，"adb"代表为adb类型，无需data字段,"log"代表压缩com.xiaoma/log下对应包名日志
//                "data":"com.xiaoma.music" //当tag为"log"时，此为必需字段，为小马应用包名
//        }
        uploadContents(tag.toLowerCase().equals("adb"), packageName);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_UPLOAD_LOG_FILE;
    }

    private void uploadContents(boolean isAdb, String packageName) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                String zipFilePath = LogConfig.zipLogFile(AppHolder.getInstance().getAppContext(), isAdb, packageName);
                if(TextUtils.isEmpty(zipFilePath)){
                    KLog.d("log zip failed, can not upload");
                    return;
                }
                uploadFile(new File(zipFilePath));
            }
        });
    }

    private void uploadFile(File file) {
        try {
            if(file == null || !file.exists() || file.length() == 0) return;
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
