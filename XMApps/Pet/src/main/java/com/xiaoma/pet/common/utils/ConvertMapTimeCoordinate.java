package com.xiaoma.pet.common.utils;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.utils.log.KLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/27 0027 11:19
 *   desc:   地图坐标转换
 * </pre>
 */
public final class ConvertMapTimeCoordinate {

    private static final String TAG = ConvertMapTimeCoordinate.class.getSimpleName();
    private static final long PET_RUNNING_TIME_INTERVAL = 5 * 60 * 1000;
    private static final long GEN_GIFT_TIME_INTERVAL = 3 * 60 * 1000;

    private static final int MAP_TOTAL_LENGTH = 3900;
    private static final int MARGIN_START_MAX = 905;
    private static final int START_X = 300;
    private static final int START_Y = 584;

    private static final int SPEED_UNIT = 60;
    private static volatile boolean startRunning = false;
    private static long currentStarterTime;
    private static Timer mRunningTimer;
    private static Timer giftTimer;

    private ConvertMapTimeCoordinate() {
        throw new RuntimeException("Not create instance.");
    }


    /**
     * @return 数组标识X、Y轴的坐标(数组大小总是为3)
     */
    public static int[] convert(float ratio) {
        if (ratio < 0) {
            ratio = 0;
        } else if (ratio > 1) {
            ratio = 1;
        }

        //direction 0 标识向右， 1 标识向左
        int direction = 0;
        int actuallyX = 0;
        int actuallyY = 0;

        if (ratio <= 0.23) {
            actuallyX = (int) (MAP_TOTAL_LENGTH * ratio);
            actuallyY = START_Y;
            direction = 0;
        } else if (ratio <= 0.26) {
            actuallyX = MARGIN_START_MAX;
            actuallyY = (int) (START_Y - (1000 * (ratio - 0.23)));
            direction = 1;
        } else if (ratio <= 0.49) {
            actuallyX = (int) (1900 - (MAP_TOTAL_LENGTH * ratio));
            actuallyY = START_Y - 100;
            direction = 1;
        } else if (ratio <= 0.52) {
            actuallyX = -20;
            actuallyY = (int) (START_Y - (2000 * (ratio - 0.49)) - 100);
            direction = 1;
        } else if (ratio <= 0.75) {
            actuallyX = (int) ((MAP_TOTAL_LENGTH * ratio) - 2000);
            actuallyY = START_Y - 200;
            direction = 0;
        } else if (ratio <= 0.78) {
            actuallyX = MARGIN_START_MAX;
            actuallyY = (int) (START_Y - (3000 * (ratio - 0.69)));
            direction = 1;
        } else if (ratio <= 1) {
            actuallyX = (int) (3900 - (MAP_TOTAL_LENGTH * ratio));
            actuallyY = START_Y - 300;
            direction = 1;
        }

//        if (actuallyX < 0) {
//            actuallyX = 0;
//        }

        return new int[]{actuallyX + START_X, actuallyY, direction};
    }


    public static float calculationMapDrivePercent(int totalKM, long runningTime) {
        //总时间，小时
        float totalTime = totalKM / (float) SPEED_UNIT;
        if (totalTime <= 0) {
            KLog.w(TAG, "total time < 0   value: " + totalTime);
            return -1;
        }

        long milliSeconds = (long) (totalTime * 60 * 60 * 1000);
        return (float) runningTime / milliSeconds;
    }


    public static int driverDistance(long runningTime) {
        float ratio = 3600 * 1000;
        float hour = runningTime / ratio;
        return (int) (hour * SPEED_UNIT);
    }


    public static int getSurplusKM(int totalKm, long runningTime) {
        int temp = totalKm - driverDistance(runningTime);
        if (temp < 0) {
            temp = 0;
        }
        return temp;
    }


    /**
     * 模拟奔跑时间上报
     */
    public static void postRunningTimePercentToServer() {
        if (startRunning) {
            startRunning = false;
            if (mRunningTimer != null) {
                mRunningTimer.cancel();
                mRunningTimer = null;
            }

            long timestamp = System.currentTimeMillis() - currentStarterTime;
            handleRunningTimestamp(timestamp);
        }
    }


    public static void startRunning() {
        if (!startRunning) {
            startRunning = true;
            currentStarterTime = System.currentTimeMillis();

            mRunningTimer = new Timer();
            mRunningTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handleRunningTimestamp(PET_RUNNING_TIME_INTERVAL);
                    currentStarterTime = System.currentTimeMillis();
                }
            }, PET_RUNNING_TIME_INTERVAL, PET_RUNNING_TIME_INTERVAL);
        }
    }


    private static void handleRunningTimestamp(long timestamp) {
        PetInfo petInfo = SavePetInfoUtils.readPetInfo();
        if (petInfo == null) {
            KLog.d(TAG, "Init pet info is null.");
            return;
        }
        RequestManager.postMapCompleteTime(timestamp, petInfo.getChapterId(), new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {

            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "统计奔跑时间失败.");
            }
        });
    }


    /**
     * 模拟定时礼物生成
     */
    public static void genGiftTimer(final OnGenGiftCallback callback) {
        if (giftTimer != null) {
            return;
        }
        giftTimer = new Timer();
        giftTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                callback.genGift();
            }
        }, GEN_GIFT_TIME_INTERVAL, GEN_GIFT_TIME_INTERVAL);

    }


    public static void cancelGenGift() {
        if (giftTimer != null) {
            giftTimer.cancel();
            giftTimer = null;
        }
    }


    public interface OnGenGiftCallback {
        void genGift();
    }

}
