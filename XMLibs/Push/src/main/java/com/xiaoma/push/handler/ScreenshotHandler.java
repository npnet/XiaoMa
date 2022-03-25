package com.xiaoma.push.handler;

import android.app.Activity;
import android.graphics.Bitmap;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.push.PushConstants;
import com.xiaoma.push.callback.ParameterCallback;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.DeviceUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
//TODO：因为需要传入当前的栈顶Activity，所以需要在Launcher中去注册
public class ScreenshotHandler implements IPushHandler {

    private ParameterCallback<Activity> activityCallback;
    private ParameterCallback<String> userIdCallback;

    public ScreenshotHandler(ParameterCallback<Activity> activityCallback, ParameterCallback<String> userId) {
        this.activityCallback = activityCallback;
        this.userIdCallback = userIdCallback;
    }

    @Override
    public void handle(PushMessage pm) {
        Activity activity = this.activityCallback.get();
        if (activity != null) {
            saveCurrentImage(activity);
        }
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_UPLOAD_SCREENSHOT;
    }

    private void saveCurrentImage(Activity activity) {
        //获取activity bitmap
        Bitmap temBitmap = DeviceUtils.screenShot(activity);
        //输出到sd卡
        File file = new File(ConfigManager.FileConfig.getScreenshotFolder(), "screenshot.jpg");
        try {
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            FileOutputStream foStream = new FileOutputStream(file);
            temBitmap.compress(Bitmap.CompressFormat.JPEG, 70, foStream);
            foStream.flush();
            foStream.close();
            KLog.i("--------screenshot success ");

            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userIdCallback.get());
            params.put("ex", "jpg");
            XmHttp.getDefault().upFile(PushConstants.UPLOAD_LOG_FILE_URL, params, file, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    KLog.i("上传截屏成功");
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    KLog.e(String.format("上传截屏失败：%s", response.body()));
                }
            });
        } catch (Exception e) {
            KLog.i(e.toString());
        }
    }
}
