package com.xiaoma.autotracker;

import android.util.Log;

import com.xiaoma.autotracker.model.ContentModel;
import com.xiaoma.autotracker.upload.UploadManager;
import com.xiaoma.autotracker.util.XmEventException;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.utils.GsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 简介:
 *
 * @author lingyan
 */
public class XmTracker {
    private final String TAG = XmTracker.class.getSimpleName();

    /**
     * 构造方法
     */
    private static class TrackEventHolder {
        private static final XmTracker instance = new XmTracker();
    }

    public static XmTracker getInstance() {
        return XmTracker.TrackEventHolder.instance;
    }

    /*
     * 上传埋点事件
     */
    public void uploadEvent(final long value, final int type) {

        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", type);
        hashMap.put("operateTime", System.currentTimeMillis());
        if (value != -1) {
            hashMap.put("content", GsonHelper.toJson(new ContentModel(value)));
        }
        Log.i(TAG, String.format("uploadEvent: { value: %s, type: %s, params: %s }", value, type, hashMap));
        UploadManager.upload(hashMap, new ResultCallback<XMResult>() {
            @Override
            public void onSuccess(XMResult result) {
                Log.i(TAG, String.format("uploadEvent: { value: %s, type: %s } SUCCESS", value, type));
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, String.format("uploadEvent: { value: %s, type: %s, code: %s, msg: %s } FAILED !!!", value, type, code, msg));
            }
        });
    }

    private void noInitAutoTrackerError() {
        String errorMsg = " if you want to use AutoTracker you must in your application init AutoTracker \n for example:"
                + "XmAutoTracker.getInstance().init(AppHolder.getInstance().getAppContext());...please check...";
        XmEventException.doException(errorMsg);
    }

}
