package com.xiaoma.autotracker.upload;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.model.AutoTrackInfo;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taojin
 * @date 2018/12/10
 */
public class RequestManager {
    public static RequestManager getInstance() {
        return InstanceHolder.instance;
    }
    private final String DATA = "data";
    public String channelId = ConfigManager.ApkConfig.getChannelID();
    public String versionCode = String.valueOf(ConfigManager.ApkConfig.getBuildVersionCode());
    public String packageName = XmAutoTracker.getInstance().getApplication().getPackageName();

    private static class InstanceHolder {
        static final RequestManager instance = new RequestManager();
    }

    /**
     * 批量上报
     *
     * @param data
     * @param callback
     */
    public void batchUploadLog(String data, final ResultCallback<XMResult> callback) {
        if (callback == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(DATA, getCompressData(data));
        request(AutoTrackAPI.BATCH_UPLOAD_URL, params, callback);
    }

    /**
     * @param autoTrackInfo
     * @param callback
     */
    public void directUploadLog(AutoTrackInfo autoTrackInfo, ResultCallback<XMResult> callback) {

        request(AutoTrackAPI.UPLOAD_URL, GsonHelper.toJson(autoTrackInfo), callback);
    }

    /**
     * @param hashMap
     * @param callback
     */
    public void directUploadEvent(Map<String, Object> hashMap, ResultCallback<XMResult> callback) {

        request(AutoTrackAPI.UPLOAD_EVENT_URL, hashMap, callback);
    }

    private void request(String url, Map<String, Object> params, final ResultCallback<XMResult> callback) {
        XmHttp.getDefault().postString(url, params, new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                XMResult result = GsonHelper.fromJson(response.body(), XMResult.class);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }

                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    private void request(String url, String object, final ResultCallback<XMResult> callback) {

        XmHttp.getDefault().postJsonString(url, object, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                XMResult result = GsonHelper.fromJson(response.body(), XMResult.class);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }

                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    /**
     * 对数据进行GZIP压缩
     *
     * @param data
     * @return
     */
    private String getCompressData(String data) {
        String compressData;
        boolean compressed;
        try {
            compressData = ZipUtils.compress(data);
            compressed = true;
        } catch (IOException e) {
            e.printStackTrace();
            compressData = data;
            compressed = false;
        }
        if (compressData.length() >= data.length()) {
            compressData = data;
            compressed = false;
        }
        KLog.d("batchUploadEvent ,compressed = " + compressed + ", data len = " + data.length() + ", compress len = " + compressData.length() + ", " +
                "data" + " = " + data);
        return compressData;
    }
}
