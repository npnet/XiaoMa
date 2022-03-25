package com.xiaoma.push.impl;

import android.content.Context;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.push.callback.RegisterCallback;
import com.xiaoma.push.contract.IPush;
import com.xiaoma.push.contract.IPushManager;
import com.xiaoma.push.handler.HandlerFactory;
import com.xiaoma.push.handler.IPushHandler;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LKF on 2018/3/26 0026.
 * 推送管理器
 */
public class PushManager implements IPushManager {
    private IPush mPush = new XgPush();

    @Override
    public void registerPush(Context context, String account, RegisterCallback callback) {
        final IPush push = mPush;
        push.setIsDebug(context, ConfigManager.ApkConfig.isDebug());
        push.register(context, account, callback);
    }

    @Override
    public void unregisterPush(Context context, String account, RegisterCallback callback) {
        final IPush push = mPush;
        if (push != null) {
            push.unregister(context, account, callback);
        }
    }

    @Override
    public boolean registerHandler(IPushHandler handler) {
        if (handler != null && handler.getAction() != 0) {
            return HandlerFactory.getInstance().registerHandler(handler);
        } else {
            return false;
        }
    }

    @Override
    public void unregisterHandler(IPushHandler handler) {
        HandlerFactory.getInstance().unregisterHandler(handler);
    }

    @Override
    public void dispatchPushMessage(PushMessage message) {
        if (message == null)
            return;
        IPushHandler handler = HandlerFactory.getInstance().produceHandler(message);
        if (handler != null) {
            handler.handle(message);
        } else {
            KLog.e(String.format("有未能处理的推送消息：\nAction:%s\nTag:%s\nData:%s",
                    message.getAction(), message.getTag(), message.getData()));
        }
    }

    private PushManager() {
    }

    public static PushManager getInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static PushManager sInstance = new PushManager();
    }
}
