package com.xiaoma.launcher.recommend.push.handler;


import android.support.annotation.NonNull;

import com.xiaoma.launcher.recommend.push.model.MqttMessage;
import com.xiaoma.utils.log.KLog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author taojin
 * @date 2019/5/17
 */
public class MqttHandlerFactory {

    private static ConcurrentHashMap<Integer, IMqttPushHandler> handlers = new ConcurrentHashMap<>();

    static {
        // 默认添加这4个无需引用外部资源的handler，其他需要引用到外部资源的handler需要在
        // 适当的地方进行初始化并注册，以免造成依赖倒置，和强应用导致的内存泄漏
        DeleteFileHandler deleteFileHandler = new DeleteFileHandler();
        handlers.put(deleteFileHandler.getAction(), deleteFileHandler);
        InstallFileHandler installFileHandler = new InstallFileHandler();
        handlers.put(installFileHandler.getAction(), installFileHandler);
        UploadFileHandler uploadFileHandler = new UploadFileHandler();
        handlers.put(uploadFileHandler.getAction(), uploadFileHandler);
        ScreenshotHandler screenshotHandler = new ScreenshotHandler();
        handlers.put(screenshotHandler.getAction(), screenshotHandler);
        UploadLogFileHandler uploadLogFileHandler = new UploadLogFileHandler();
        handlers.put(uploadLogFileHandler.getAction(), uploadLogFileHandler);
    }

    public IMqttPushHandler produceHandler(MqttMessage pushMessage) {
        return handlers.get(pushMessage.getAction());
    }

    public boolean registerHandler(@NonNull IMqttPushHandler handler) {
        if (handlers.keySet().contains(handler.getAction())) {
            KLog.e(String.format("Action:%s已经有handler进行处理!", handler.getAction()));
            return false;
        } else {
            handlers.put(handler.getAction(), handler);
            return true;
        }
    }

    public void unregisterHandler(IMqttPushHandler handler) {
        handlers.remove(handler.getAction());
    }

    public static MqttHandlerFactory getInstance() {
        return MqttHandlerFactory.Holder.sInstance;
    }

    private static class Holder {
        private static MqttHandlerFactory sInstance = new MqttHandlerFactory();
    }

    private MqttHandlerFactory() {
    }
}
