package com.xiaoma.personal.qrcode.utils;

import com.xiaoma.personal.qrcode.callback.LoopStatusObserver;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 19:04
 *   desc:   轮询全息绑定状态
 * </pre>
 */
public final class LoopHologramStatusUtils {

    private static ScheduledExecutorService EXECUTOR_SERVICE;

    private LoopHologramStatusUtils() {
        throw new RuntimeException("Not create instance.");
    }

    public static void startLoop(int interval, final LoopStatusObserver loopStatusObserver) {
        if (EXECUTOR_SERVICE == null) {
            EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        }
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> ThreadDispatcher.getDispatcher().postOnMain(() -> {
            if (loopStatusObserver != null) {
                loopStatusObserver.execute();
            }
        }), interval, interval, TimeUnit.MILLISECONDS);

    }


    public static void stopLoop() {
        if (EXECUTOR_SERVICE != null) {
            EXECUTOR_SERVICE.shutdownNow();
            EXECUTOR_SERVICE = null;
            KLog.i("Closed EXECUTOR_SERVICE");
        }
    }

}
