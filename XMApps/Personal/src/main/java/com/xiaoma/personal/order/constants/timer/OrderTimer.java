package com.xiaoma.personal.order.constants.timer;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    private static ScheduledExecutorService EXECUTOR_SERVICE;

    public static void startTimer(int interval, final OnTimerCallback onTimerCallback) {
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


    public static void endTimer() {
        if (EXECUTOR_SERVICE != null) {
            EXECUTOR_SERVICE.shutdownNow();
            EXECUTOR_SERVICE = null;
            KLog.i("Closed EXECUTOR_SERVICE");
        }
    }

    public static int calculationInterval(long start, long end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String interval = dateFormat.format(new Date(end - start));
        String intervalMinSeconds = interval.substring(interval.indexOf(":") + 1);
        String[] mm_ss = intervalMinSeconds.split(":");
        return Integer.parseInt(mm_ss[0]) * 60 + Integer.parseInt(mm_ss[1]);
    }


}
