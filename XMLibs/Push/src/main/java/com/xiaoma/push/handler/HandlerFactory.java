package com.xiaoma.push.handler;


import android.support.annotation.NonNull;

import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.log.KLog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author KY
 * @date 2018/9/20
 */
public class HandlerFactory {

    private static ConcurrentHashMap<Integer, IPushHandler> handlers = new ConcurrentHashMap<>();

    static {
        // 默认添加这4个无需引用外部资源的handler，其他需要引用到外部资源的handler需要在
        // 适当的地方进行初始化并注册，以免造成依赖倒置，和强应用导致的内存泄漏
        ModifyConfigHandler modifyConfigHandler = new ModifyConfigHandler();
        handlers.put(modifyConfigHandler.getAction(), modifyConfigHandler);
        DeleteFileHandler deleteFileHandler = new DeleteFileHandler();
        handlers.put(deleteFileHandler.getAction(), deleteFileHandler);
        PopupHandler popupHandler = new PopupHandler();
        handlers.put(popupHandler.getAction(), popupHandler);
        NoticeHandler noticeHandler = new NoticeHandler();
        handlers.put(noticeHandler.getAction(), noticeHandler);
    }

    public IPushHandler produceHandler(PushMessage pushMessage) {
        return handlers.get(pushMessage.getAction());
    }

    public boolean registerHandler(@NonNull IPushHandler handler) {
        if (handlers.keySet().contains(handler.getAction())) {
            KLog.e(String.format("Action:%s已经有handler进行处理!", handler.getAction()));
            return false;
        } else {
            handlers.put(handler.getAction(), handler);
            return true;
        }
    }

    public void unregisterHandler(IPushHandler handler) {
        handlers.remove(handler.getAction());
    }

    public static HandlerFactory getInstance() {
        return HandlerFactory.Holder.sInstance;
    }

    private static class Holder {
        private static HandlerFactory sInstance = new HandlerFactory();
    }

    private HandlerFactory() {
    }
}
