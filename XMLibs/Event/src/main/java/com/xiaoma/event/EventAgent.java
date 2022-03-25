package com.xiaoma.event;

import android.text.TextUtils;

import com.xiaoma.db.DBManager;
import com.xiaoma.event.constant.EventConstants;
import com.xiaoma.event.constant.XMEventKey;
import com.xiaoma.event.util.UploadUtil;
import com.xiaoma.network.callback.Callback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zs
 * @date 2018/9/12 0012.
 */
public class EventAgent implements IEvent, AutoTriggerHelp.ITriggerMaxCritical {

    private final static String STAND_TIME = "StandingTime";
    private final static long MAX_TIME = 24 * 60 * 60 * 1000;
    private long userId;
    private AutoTriggerHelp<EventInfo> autoTriggerHelp;

    private EventAgent() {
        autoTriggerHelp = new AutoTriggerHelp<>(30, 20 * 60 * 1000, this);
    }

    private static class EventAgentHolder {
        private static final EventAgent INSTANCE = new EventAgent();
    }

    public static EventAgent getInstance() {
        return EventAgentHolder.INSTANCE;
    }

    @Override
    public void triggerCritical(final ArrayList data) {
        if (CollectionUtil.isListEmpty(data)) {
            return;
        }
        String dataString = GsonHelper.toJson(data);
//        onBatchEvent(EventType.EVENT_COMPRESS, dataString, new StringCallback() {
//            @Override
//            public void onSuccess(Response<String> response) {
//                KLog.d("batchUpload event info success");
//            }
//
//            @Override
//            public void onError(Response<String> response) {
//                super.onError(response);
//                KLog.d("batchUpload event info failed");
//                DBManager.getInstance().getDBManager().save(data);
//            }
//        });
    }

    /**
     * 将缓存在数据库中的数据上报,每次上报最多为{@link EventConstants#BATCH_UPLOAD_MAX_SIZE}条
     */
    @Override
    public void onEvent() {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<EventInfo> tempList = DBManager.getInstance().getDBManager().queryLimit(EventInfo.class, 0, EventConstants.BATCH_UPLOAD_MAX_SIZE);
                if (tempList == null || tempList.size() == 0) {
                    return;
                }
                KLog.d("onEvent--------" + tempList.size());
                String data = GsonHelper.toJson(tempList);
                if (TextUtils.isEmpty(data)) {
                    KLog.d("batch upload data is empty");
                    return;
                }
                UploadUtil.batchUploadEvent(EventType.EVENT_COMPRESS, data, null);
            }
        }, 5000, Priority.LOW);
    }

    @Override
    public void onEvent(String eventKey) {
        onEvent(eventKey, false);
    }

    @Override
    public void onEvent(final String eventKey, final boolean isNow) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                uploadEvent(eventKey, "", isNow);
            }
        });
    }

    @Override
    public void onEvent(String eventKey, String data) {
        onEvent(eventKey, data, false);
    }

    @Override
    public void onEvent(final String eventKey, final String data, final boolean isNow) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                //onResume会重复上报启动时间，每次递增，逻辑错误先屏蔽
                if (TextUtils.isEmpty(eventKey) || XMEventKey.Common.VIEW_DISPLAY_EXSPENSE.equals(eventKey)) {
                    return;
                }
                String newData = data;
                if (TextUtils.isEmpty(newData)) {
                    newData = "";
                }
                uploadEvent(eventKey, newData, isNow);
            }
        });
    }

    @Override
    public void onEvent(String eventKey, long time) {
        onEvent(eventKey, time, false);
    }

    @Override
    public void onEvent(final String eventKey, final long time, final boolean isNow) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(eventKey) || time >= MAX_TIME) {
                    return;
                }
                String data;
                Map<String, Object> params = new HashMap<>();
                params.put(STAND_TIME, time);
                data = GsonHelper.toJson(params);
                uploadEvent(eventKey, data, isNow);
            }
        });
    }

    @Override
    public void onEvent(String eventKey, String key, String values) {
        onEvent(eventKey, key, values, false);
    }

    @Override
    public void onEvent(final String eventKey, final String key, final String values, final boolean isNow) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(eventKey)) {
                    return;
                }
                String data = "";
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(values)) {
                    params.put(key, values);
                    data = GsonHelper.toJson(params);
                }
                uploadEvent(eventKey, data, isNow);
            }
        });
    }

    @Override
    public void onEvent(String eventKey, Map<String, Object> params) {
        onEvent(eventKey, params, false);
    }

    @Override
    public void onEvent(final String eventKey, final Map<String, Object> params, final boolean isNow) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                String data = "";
                if (params != null && params.size() > 0) {
                    data = GsonHelper.toJson(params);
                }
                uploadEvent(eventKey, data, isNow);
            }
        });
    }

    @Override
    public void onBatchEvent(EventType eventType, String data, Callback<String> callback) {
        onBatchEvent(eventType, data, false, callback);
    }

    @Override
    public void onBatchEvent(final EventType eventType, final String data, boolean isNow, final Callback<String> callback) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                UploadUtil.batchUploadEvent(eventType, data, callback);
            }
        });
    }

    /**
     * 事件上报
     */
    private void uploadEvent(final String eventKey, final String data, boolean isNow) {
        initUserId();
        if (userId <= 0) {
            return;
        }
        if (isNow) {
            ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                @Override
                public void run() {
                    UploadUtil.uploadEvent(userId, eventKey, data);
                }
            });
        } else {
            autoTriggerHelp.addData(new EventInfo(System.currentTimeMillis(), data, eventKey, userId));
        }
    }

    private void initUserId() {

    }

}
