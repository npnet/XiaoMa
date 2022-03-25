package com.xiaoma.push.contract;

import android.content.Context;

import com.xiaoma.push.callback.RegisterCallback;
import com.xiaoma.push.handler.IPushHandler;
import com.xiaoma.push.model.PushMessage;

/**
 * Created by LKF on 2018/4/8 0008.
 * 推送管理器接口
 */

public interface IPushManager {
    /**
     * 注册信鸽推送服务
     *
     * @param context  context
     * @param account  user uuid
     * @param callback register callback
     */
    void registerPush(Context context, String account, RegisterCallback callback);

    /**
     * 反注册信鸽推送服务
     *
     * @param context  context
     * @param account  user uuid
     * @param callback register callback
     */
    void unregisterPush(Context context, String account, RegisterCallback callback);

    /**
     * 注册消息handler
     *
     * @param handler handler
     * @return register result
     */
    boolean registerHandler(IPushHandler handler);

    /**
     * 反注册消息handler
     *
     * @param handler handler
     */
    void unregisterHandler(IPushHandler handler);

    /**
     * 分发推送消息
     *
     * @param message message
     */
    void dispatchPushMessage(PushMessage message);
}
