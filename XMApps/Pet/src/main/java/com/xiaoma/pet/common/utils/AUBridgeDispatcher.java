package com.xiaoma.pet.common.utils;

import android.text.TextUtils;

import com.xiaoma.pet.common.callback.OnUnityResultCallback;
import com.xiaoma.utils.log.KLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 17:40
 *   desc:   分发unity消息
 * </pre>
 */
public final class AUBridgeDispatcher {

    private static final String TAG = AUBridgeDispatcher.class.getSimpleName();
    private ConcurrentHashMap<String, OnUnityResultCallback> callbackWeakHashMap = new ConcurrentHashMap<>();


    public static AUBridgeDispatcher getInstance() {
        return Holder.AU_BRIDGE_DISPATCHER;
    }


    public void callUnity(long requestCode, String action, Object... params) {
        AUBridge.getInstance().callUnity(requestCode, action, params);
    }


    public void callUnityForReturn(long requestCode, String action, Object... params) {
        AUBridge.getInstance().callUnityForReturn(requestCode, action, params);
    }


    public void registerResultCallback(String tag, OnUnityResultCallback callback) {
        if (callbackWeakHashMap != null) {
            callbackWeakHashMap.put(tag, callback);
        }
    }


    public void release(String tag) {
        if (callbackWeakHashMap != null) {
            callbackWeakHashMap.remove(tag);
        }
    }


    public void releaseAll() {
        if (callbackWeakHashMap != null) {
            callbackWeakHashMap.clear();
        }
    }


    void dispatchMessage(String action, String result) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(result)) {
            KLog.w(TAG, "action: " + action + " result: " + result);
            return;
        }

        KLog.d(TAG, "action: " + action + " result: " + result);
        for (Map.Entry<String, OnUnityResultCallback> entry : callbackWeakHashMap.entrySet()) {
            OnUnityResultCallback callback = entry.getValue();
            callback.receiveResultForUnity(action, result);
        }
    }


    private AUBridgeDispatcher() {
    }

    private static class Holder {
        private static final AUBridgeDispatcher AU_BRIDGE_DISPATCHER = new AUBridgeDispatcher();
    }


}
