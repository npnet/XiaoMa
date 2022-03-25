package com.xiaoma.shop.common.util.timer;

import com.xiaoma.thread.BgThreadFactory;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/18 0018 11:39
 *       desc：时间计时器
 * </pre>
 */
public class OrderTimer {

    private ScheduledExecutorService EXECUTOR_SERVICE;

    public static OrderTimer getInstance() {
        return Holder.ORDER_TIMER;
    }

    private static class Holder {
        private static final OrderTimer ORDER_TIMER = new OrderTimer();
    }

    private OrderTimer() {
        EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(new BgThreadFactory());
    }

    public void startTimer(int interval, final OnTimerCallback onTimerCallback) {
        if (EXECUTOR_SERVICE == null) {
            EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        }
        EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        onTimerCallback.onTimer();
                    }
                });
            }
        }, 0, interval, TimeUnit.MILLISECONDS);

    }


    public void endTimer() {
        if (EXECUTOR_SERVICE != null) {
            EXECUTOR_SERVICE.shutdownNow();
            EXECUTOR_SERVICE = null;
            KLog.i("Closed EXECUTOR_SERVICE");
        }
    }


}
