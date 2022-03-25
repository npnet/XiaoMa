package com.xiaoma.event.util;

import android.text.TextUtils;

import com.xiaoma.event.EventType;
import com.xiaoma.network.callback.Callback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.log.KLog;

/**
 * @author zs
 * @date 2018/9/17 0017.
 */
public class UploadUtil {

    /**
     * 普通上报
     */
    public static void uploadEvent(long userId, String eventKey, String data) {
        NetUtil.upload(userId, eventKey, data);
    }

    /**
     * 批量压缩上报
     */
    public static void batchUploadEvent(EventType eventType, String data, final Callback<String> callback) {
        if (TextUtils.isEmpty(data)) {
            KLog.d("batchUploadEvent data is Empty");
            return;
        }
        NetUtil.batchUpload(eventType, data, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("batchUploadEvent event info success");
                callback.onSuccess(response);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                KLog.d("batchUploadEvent event info failed");
                callback.onError(response);
            }
        });
    }
}
