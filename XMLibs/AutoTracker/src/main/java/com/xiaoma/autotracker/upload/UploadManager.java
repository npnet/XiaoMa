package com.xiaoma.autotracker.upload;

import com.xiaoma.autotracker.model.AutoTrackInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.utils.StringUtil;

import java.util.Map;

/**
 * @author zs
 * @date 2018/9/17 0017.
 */
public class UploadManager {

    /**
     * 普通上报
     */
    public static void uploadEvent(AutoTrackInfo autoTrackInfo, ResultCallback<XMResult> callback) {
        if (autoTrackInfo == null) {
            return;
        }

        RequestManager.getInstance().directUploadLog(autoTrackInfo, callback);
    }

    /**
     * 普通埋点事件上报
     */
    public static void upload(Map<String, Object> hashMap, ResultCallback<XMResult> callback) {
        if (hashMap == null) {
            return;
        }
        RequestManager.getInstance().directUploadEvent(hashMap, callback);
    }

    /**
     * 批量压缩上报
     */
    public static void batchUploadEvent(String data, ResultCallback<XMResult> callback) {
        if (StringUtil.isEmpty(data)) {
            return;
        }
        RequestManager.getInstance().batchUploadLog(data, callback);
    }
}
