package com.xiaoma.pet.common.utils;

import com.xiaoma.pet.common.callback.OnRefreshTimerCallback;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/24 0024 15:01
 *   desc:   宠物进食定时
 * </pre>
 */
public final class EatFoodTimer {

    private ScheduledExecutorService scheduledExecutorService;
    private long currentSurplusTime;


    private EatFoodTimer() {

    }

    public static EatFoodTimer getInstance() {
        return Holder.EAT_FOOD_TIMER;
    }

    public void startTimer(long surplus, final long interval, final OnRefreshTimerCallback callback) {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
        }
        currentSurplusTime = surplus;
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final int[] tempTime = calculationSurplusTime(currentSurplusTime);
                if (tempTime == null) {
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.finish();
                            //进食结束
                            stopTimer();
                        }
                    });
                } else {
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.refresh(tempTime[0], tempTime[1]);
                        }
                    });
                }
                currentSurplusTime = currentSurplusTime - interval;
            }
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    public void stopTimer() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }

    /**
     * 计算最新剩余时间
     *
     * @param surplusTime 当前剩余
     * @return int[]   0:hour  1:min
     */
    private int[] calculationSurplusTime(long surplusTime) {
        if (surplusTime <= 0) {
            return null;
        }

        int seconds = (int) (surplusTime / 1000);
        int hour = seconds / 3600;
        int min;
        int temp = seconds % 3600;
        if (temp < 60) {
            --hour;
            min = 59;
        } else {
            min = temp / 60;
        }

        if (hour < 0) {
            return null;
        }

        int[] timeArray = new int[2];
        timeArray[0] = hour;
        timeArray[1] = min;
        return timeArray;
    }

    private static class Holder {
        private static final EatFoodTimer EAT_FOOD_TIMER = new EatFoodTimer();
    }

}
