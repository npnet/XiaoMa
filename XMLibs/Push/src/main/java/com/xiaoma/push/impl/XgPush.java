package com.xiaoma.push.impl;

import android.content.Context;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.xiaoma.push.callback.RegisterCallback;
import com.xiaoma.push.contract.IPush;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LKF on 2018/3/31 0031.
 * 信鸽推送
 */
public class XgPush extends XGPushBaseReceiver implements IPush {
    @Override
    public void register(Context context, String account, final RegisterCallback callback) {
        XGPushManager.registerPush(context, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                if (callback != null)
                    callback.onSuccess(XgPush.this, o, i);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                if (callback != null)
                    callback.onFail(XgPush.this, o, i, s);
            }
        });
    }

    @Override
    public void unregister(Context context, String account, final RegisterCallback callback) {
        XGPushManager.unregisterPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                if (callback != null)
                    callback.onSuccess(XgPush.this, o, i);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                if (callback != null)
                    callback.onFail(XgPush.this, o, i, s);
            }
        });
    }

    @Override
    public void setIsDebug(Context context, boolean debug) {
        XGPushConfig.enableDebug(context, debug);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        if (xgPushTextMessage == null) {
            KLog.d("onTextMessage -> xgPushTextMessage : null");
            return;
        }
        String title = xgPushTextMessage.getTitle();
        String content = xgPushTextMessage.getContent();
        String customContent = xgPushTextMessage.getCustomContent();
        PushMessage pushMessage = GsonHelper.fromJson(customContent, PushMessage.class);
        if (pushMessage != null) {
            PushManager.getInstance().dispatchPushMessage(pushMessage);
        }
        KLog.d(StringUtil.format("receive text msg \n"
                        + "title : %s \n "
                        + "content : %s \n "
                        + "customContent : %s \n",
                title, content, customContent));
    }


    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
