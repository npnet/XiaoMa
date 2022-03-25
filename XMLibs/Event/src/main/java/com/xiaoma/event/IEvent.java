package com.xiaoma.event;

import com.xiaoma.network.callback.Callback;

import java.util.Map;

/**
 * 事件上报接口
 *
 * @author zs
 * @date 2018/9/12 0012.
 */
public interface IEvent {

    void onEvent();

    /**
     * 直接上报
     */
    void onEvent(String eventKey);

    /**
     * @param isNow 是否实时上报
     */
    void onEvent(String eventKey, boolean isNow);

    void onEvent(String eventKey, String data);

    void onEvent(String eventKey, String data, boolean isNow);

    void onEvent(String eventKey, long time);

    void onEvent(String eventKey, long time, boolean isNow);

    void onEvent(String eventKey, String key, String values);

    void onEvent(String eventKey, String key, String values, boolean isNow);

    void onEvent(String eventKey, Map<String, Object> params);

    void onEvent(String eventKey, Map<String, Object> params, boolean isNow);

    /**
     * 批量上报
     *
     * @param eventType 上报类型
     */
    void onBatchEvent(EventType eventType, String data, Callback<String> callback);

    void onBatchEvent(EventType eventType, String data, boolean isNow, Callback<String> callback);

}
