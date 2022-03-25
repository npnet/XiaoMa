package com.xiaoma.autotracker.util;

import android.app.Activity;
import android.graphics.Bitmap;

import com.xiaoma.autotracker.upload.AutoTrackAPI;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.DeviceUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * @author taojin
 * @date 2019/5/20
 */
public class UploadScreenManager {

    private UploadScreenManager() {

    }

    public static UploadScreenManager getInstance() {
        return UploadScreenManagerHolder.instance;
    }

    public static class UploadScreenManagerHolder {
        static final UploadScreenManager instance = new UploadScreenManager();
    }

    public void uploadScreenshot(Activity activity,String userId) {
        if (!AppUtils.isAppForeground()) {
            KLog.d("App in background");
            return;
        }
        KLog.i("-----------get activity");
        if (activity != null) {
            KLog.i("--------activity " + activity.getLocalClassName());
            saveCurrentImage(activity,userId);
        }
    }


    private void saveCurrentImage(Activity activity,String userId) {
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
            params.put("userId", userId);
            params.put("ex", "jpg");
            XmHttp.getDefault().upFile(AutoTrackAPI.UPLOAD_LOG_FILE_URL, params, file, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    KLog.i("upload screenshot success");
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    KLog.e(String.format("upload screenshot failed：%s", response.body()));
                }
            });
        } catch (Exception e) {
            KLog.i(e.toString());
        }
    }

}
