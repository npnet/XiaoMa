package com.xiaoma.xting.player.model;

import com.xiaoma.utils.TimeUtils;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/22
 */
public class FMProgress {

    private int progress;
    private int duration;
    private String progressStr;
    private String durationStr;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getProgressStr() {
        return progressStr;
    }

    public void setProgressStr(String progressStr) {
        this.progressStr = progressStr;
    }

    public String getDurationStr() {
        return durationStr;
    }

    public void setDurationStr(String durationStr) {
        this.durationStr = durationStr;
    }

    public static FMProgress createFmProgress(int progress, int duration) {
        FMProgress fmProgress = new FMProgress();
        fmProgress.setProgress(progress);
        fmProgress.setProgressStr(TimeUtils.timeMsToMMSS(progress));

        fmProgress.setDuration(duration);
        fmProgress.setDurationStr(TimeUtils.timeMsToMMSS(duration));
        return fmProgress;
    }
}
