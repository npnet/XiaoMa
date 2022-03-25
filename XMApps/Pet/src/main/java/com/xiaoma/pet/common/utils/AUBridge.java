package com.xiaoma.pet.common.utils;

import android.text.TextUtils;

import com.unity3d.player.UnityPlayer;
import com.xiaoma.pet.model.AUCallForReturn;
import com.xiaoma.pet.model.UACall;
import com.xiaoma.pet.model.UAResultInfo;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 11:31
 *   desc:   unity交互中心枢纽
 * </pre>
 * <p>
 *
 * <p>
 * {
 * "Action":action,
 * "Return":true,
 * "RequestCode":requestCode,
 * "Params": [ ]
 * }
 * <p>
 * 1、Android调用Unity
 * 2、判断是否需要return
 * 3、Unity调用Android的onCall来返回结果
 */
public class AUBridge {

    private static final String TAG = AUBridge.class.getSimpleName();
    private static final String UNITY_BRIDGE = "Script";
    private static final String UNITY_BRIDGE_EXECUTOR_ON_CALL = "OnCall";


    public static AUBridge getInstance() {
        return Holder.AU_BRIDGE;
    }


    /**
     * U -> A
     *
     * @param callJson {Action: "" ，Params: "json串"}
     */
    public void onCall(String callJson) {
        if (TextUtils.isEmpty(callJson)) {
            KLog.w(TAG, "callJson is null.");
        }

        UACall uaCall = GsonHelper.fromJson(callJson, UACall.class);
        if (uaCall != null) {
            AUBridgeDispatcher.getInstance().dispatchMessage(uaCall.getAction(), uaCall.getParams());
        } else {
            KLog.w(TAG, "uaCall instance is null.");
        }
    }


    /**
     * U -> A  return
     *
     * @param callJson {Action: "" ，Params: "json串"}
     * @return 向unity回参
     */
    public String onCallForReturn(String callJson) {
        return null;
    }


    /**
     * A -> U Android端调用unity端，unity处理结果回调
     *
     * @param returnJson U -> A回参
     */
    public void onReturn(String returnJson) {
        if (TextUtils.isEmpty(returnJson)) {
            KLog.w(TAG, "returnJson is null.");
            return;
        }

        UAResultInfo uaInfo = GsonHelper.fromJson(returnJson, UAResultInfo.class);
        if (uaInfo != null) {
            int result = uaInfo.getResult();
            AUBridgeDispatcher.getInstance().dispatchMessage(uaInfo.getAction(), String.valueOf(result));
        } else {
            KLog.w(TAG, "AUCallForReturn instance is null.");
        }
    }


    void callUnity(long requestCode, String action, Object... params) {
        handleInternal(requestCode, action, false, params);
    }


    void callUnityForReturn(long requestCode, String action, Object... params) {
        handleInternal(requestCode, action, true, params);
    }


    private void handleInternal(long requestCode, String action, boolean aReturn, Object... params) {
        AUCallForReturn AUCallForReturn = new AUCallForReturn(action, aReturn, requestCode, params);
        final String unityJson = GsonHelper.toJson(AUCallForReturn);
        KLog.d(TAG, unityJson);
        UnityPlayer.UnitySendMessage(UNITY_BRIDGE, UNITY_BRIDGE_EXECUTOR_ON_CALL, unityJson);
    }


    private AUBridge() {
    }

    private static class Holder {
        private static final AUBridge AU_BRIDGE = new AUBridge();
    }
}
