package com.xiaoma.cariflytek.ivw;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.model.Score;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.model.SeoptType;

/**
 * @author: iSun
 * @date: 2019/3/15 0015
 */
public class LxWakeupMultipleHelper {
    private static final String TAG = LxWakeupMultipleHelper.class.getSimpleName();
    private volatile int count = 0;
    public static final int SEOPT_CLOSE = 1;
    public static final int SEOPT_LEFT = 2;
    public static final int SEOPT_RIGHT = 3;
    public static final int SEOPT_AUTO = 4;
    private static LxWakeupMultipleHelper instance;
    private Score[] wakeUpScore = new Score[2];
    private volatile boolean isAlreadyEnter = false;//双唤醒标记位
    private LxXfWakeupMultiple lxXfWakeupMultiple;
    private int intervalTime = 300;//二次唤醒最长间隔
    private int sleepMillis = 50;//睡眠等待
    private SeoptType configType = SeoptType.CLOSE;
    private boolean isOpenSeopt = false;
    private boolean delayDirection = false;//延迟处理计算唤醒方向
    private boolean isMainWakeup;


    public static LxWakeupMultipleHelper getInstance() {
        if (instance == null) {
            synchronized (LxWakeupMultipleHelper.class) {
                if (instance == null) {
                    instance = new LxWakeupMultipleHelper();
                }
            }
        }
        return instance;
    }

    private void calculatedScore(Score maxScore, Score maxPower) {
        Log.d(TAG, "more wake up，start calc scores");
        //将唤醒得分较高的唤醒得分、音频能量保存至MaxScore.nScore、MaxScore.fPower；
        //将音频能量较高的唤醒得分、音频能量保存至MaxPower.nScore、MaxPower.fPower
        float ScoreDiffNormal = Math.abs((0.01f + maxScore.nScore - maxPower.nScore) / (0.1f + maxScore.nScore));
        float PowerDiffNormal = Math.abs((0.01f + maxPower.fPower - maxScore.fPower) / (0.1f + maxPower.fPower));
        if (ScoreDiffNormal > 0.25f && PowerDiffNormal < 0.15f) {
            //取得分较高唤醒实例对应的波束方向，即MaxScore对应的唤醒实例
            Log.d(TAG, "计算结果：得分最高:" + maxScore.toString());
            onWakeup(maxScore);
        } else {
            //选取能量较高唤醒实例对应的波束方向，即MaxPower对应的唤醒实例
            Log.d(TAG, "计算结果：能量最高:" + maxPower.toString());
            onWakeup(maxPower);
        }
    }

    private void seopTypeLog() {
        String uid = VrAidlServiceManager.getInstance().getUid();
        int i = XmProperties.build(uid).get(VoiceConfigManager.KEY_SEOPT_TYPE, SeoptType.CLOSE.getValue());
        String str = "关闭";
        switch (i) {
            case 1:
                str = "关闭";
                break;
            case 2:
                str = "左";
                break;
            case 3:
                str = "右";
                break;
            case 4:
                str = "自动";
                break;
        }
        Log.d(TAG, "BuildSerEnv:" + ConfigManager.ApkConfig.getBuildSerEnv() + "   uid:" + uid + "  本地配置为：" + str);
    }

    private void onWakeup(Score score) {
        String tempAll = score.isMainDrive ? "左" : "右";
        Log.d(TAG, "唤醒结果：当前设置为 " + configType + " 本次唤醒方向为：" + tempAll);
        if (isCloseOrAuto(configType) && lxXfWakeupMultiple != null) {
            String temp = score.isMainDrive ? "左" : "右";
            Log.d(TAG, "唤醒结果：当前为关闭或自动唤醒 匹配到：" + temp + "边唤醒");
            lxXfWakeupMultiple.onWakeup(score);
        } else if (isLeft(score, configType) && lxXfWakeupMultiple != null) {
            Log.d(TAG, "唤醒结果： 匹配到左边唤醒");
            lxXfWakeupMultiple.onWakeup(score);
        } else if (isRight(score, configType) && lxXfWakeupMultiple != null) {
            Log.d(TAG, "唤醒结果：匹配到右边唤醒");
            lxXfWakeupMultiple.onWakeup(score);
        }
        this.isMainWakeup = score.isMainDrive;
        reStart();
        seopTypeLog();
    }

    private boolean isRight(Score score, SeoptType configType) {
        return !score.isMainDrive && SeoptType.RIGHT == configType;
    }

    private boolean isLeft(Score score, SeoptType configType) {
        return score.isMainDrive && SeoptType.LEFT == configType;
    }

    private boolean isCloseOrAuto(SeoptType configType) {
        return configType == SeoptType.CLOSE || configType == SeoptType.AUTO;
    }

    public void init(LxXfWakeupMultiple lxXfWakeupMultiple) {
        this.lxXfWakeupMultiple = lxXfWakeupMultiple;
        Log.d(TAG, "init : ");
    }


    private void joinWakeupTimer() {
        if (isAlreadyEnter) {
            //第二次进入
            Log.d(TAG, "Re Enter wake up timer");
        } else {
            Log.d(TAG, "Enter wake up timer");
            //第一次进入
            count = 0;
            isAlreadyEnter = true;
            ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
                @Override
                public void run() {
                    try {
                        long temp = SystemClock.elapsedRealtime();
                        Log.d(TAG, "waiting for the second wake:start:" + temp);
                        while ((wakeUpScore[0] == null || wakeUpScore[1] == null) && count < intervalTime / sleepMillis && (SystemClock.elapsedRealtime() - temp) < intervalTime - 50) {
                            Thread.sleep(sleepMillis);
                            Log.d(TAG, "waiting for the second wake:" + SystemClock.elapsedRealtime());
                            count++;
                        }
                        Log.d(TAG, "waiting for the second wake:end:" + SystemClock.elapsedRealtime());
                        if (wakeUpScore[0] != null && wakeUpScore[1] != null) {
                            //将唤醒得分较高的唤醒得分、音频能量保存至MaxScore.nScore、MaxScore.fPower；
                            //将音频能量较高的唤醒得分、音频能量保存至MaxPower.nScore、MaxPower.fPower
                            Score maxScore = wakeUpScore[0].nScore > wakeUpScore[1].nScore ? wakeUpScore[0] : wakeUpScore[1];
                            Score maxPower = wakeUpScore[0].fPower > wakeUpScore[1].fPower ? wakeUpScore[0] : wakeUpScore[1];
                            calculatedScore(maxScore, maxPower);
                        } else if (wakeUpScore[0] != null || wakeUpScore[1] != null) {
                            Score scores = wakeUpScore[0] != null ? wakeUpScore[0] : wakeUpScore[1];
                            onWakeup(scores);
                        } else {
                            Log.e(TAG, "error，please check in the paramenters");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public Score getScore(int nMvwScore, int nMvwScene, int nMvwId, String lParam, boolean isMainDrive) {
        Score result = new Score();
        result.nScore = nMvwScore;
        if (!TextUtils.isEmpty(lParam)) {
            WakeUpInfo wakeUpInfo = GsonHelper.fromJson(lParam, WakeUpInfo.class);
            result.fPower = wakeUpInfo.getPowerValue();
        }
        result.nMvwScene = nMvwScene;
        result.isMainDrive = isMainDrive;
        result.lParam = lParam;
        result.nMvwId = nMvwId;
        return result;
    }

    public void setIvwDataForLeft(Score score) {
        Log.e(TAG, " Ivw left: " + GsonHelper.toJson(score));
        wakeUpScore[0] = score;
        joinWakeupTimer();
    }

    public void setIvwDataForRight(Score score) {
        Log.e(TAG, " Ivw Right: " + GsonHelper.toJson(score));
        wakeUpScore[1] = score;
        joinWakeupTimer();
    }

    public boolean isSingleSeoptType() {
        String uid = VrAidlServiceManager.getInstance().getUid();
        int i = XmProperties.build(uid).get(VoiceConfigManager.KEY_SEOPT_TYPE, SEOPT_AUTO);
        return i != SEOPT_AUTO;
    }

    public void reStart() {
        wakeUpScore[0] = null;
        wakeUpScore[1] = null;
        isAlreadyEnter = false;
    }


    public void upSeopt(boolean isOpenSeopt) {
        this.isOpenSeopt = isOpenSeopt;
        if (!isOpenSeopt) {
            configType = SeoptType.CLOSE;
        } else {
            String uid = VrAidlServiceManager.getInstance().getUid();
            int i = XmProperties.build(uid).get(VoiceConfigManager.KEY_SEOPT_TYPE, SeoptType.CLOSE.getValue());
            configType = SeoptType.values()[i - 1];
        }
    }

    public boolean isMainWakeup() {
        return isMainWakeup;
    }
}
