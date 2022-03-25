package com.xiaoma.dualscreen.manager;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ZYao.
 * Date ：2019/7/16 0016
 */
public class MediaViewManager {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 5;
    private int mCurrentSelectIndex = MIN_INDEX;

    @IViewStep
    private int mCurrentStep = IViewStep.MEDIA_MAIN;

    public static MediaViewManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final MediaViewManager instance = new MediaViewManager();
    }

    @IAudioType
    public int getCurrentSelectIndex() {
        return mCurrentSelectIndex;
    }

    public void setCurrentSelectIndex(int mCurrentSelectIndex) {
        this.mCurrentSelectIndex = mCurrentSelectIndex;
    }

    public synchronized void next() {
        if (mCurrentSelectIndex >= MAX_INDEX) {
            this.mCurrentSelectIndex = MIN_INDEX;
            return;
        }
        this.mCurrentSelectIndex = mCurrentSelectIndex + 1;
    }

    public synchronized void pre() {
        if (mCurrentSelectIndex <= MIN_INDEX) {
            this.mCurrentSelectIndex = MAX_INDEX;
            return;
        }
        this.mCurrentSelectIndex = mCurrentSelectIndex - 1;
    }

    public boolean isLocal() {
        return mCurrentSelectIndex == IAudioType.BT_MUSIC
                || mCurrentSelectIndex == IAudioType.USB_MUSIC
                || mCurrentSelectIndex == IAudioType.LOCAL_FM
                || mCurrentSelectIndex == IAudioType.LOCAL_AM;
    }

    public boolean isLocalRadio() {
        return mCurrentSelectIndex == IAudioType.BT_MUSIC
                || mCurrentSelectIndex == IAudioType.LOCAL_FM
                || mCurrentSelectIndex == IAudioType.LOCAL_AM;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IAudioType.ONLINE_MUSIC, IAudioType.BT_MUSIC,
            IAudioType.USB_MUSIC, IAudioType.ONLINE_XTING,
            IAudioType.LOCAL_FM, IAudioType.LOCAL_AM})
    public @interface IAudioType {
        int USB_MUSIC = 0;
        int BT_MUSIC = 1;
        int ONLINE_MUSIC = 2;
        int LOCAL_FM = 3;
        int LOCAL_AM = 4;
        int ONLINE_XTING = 5;
    }

    public void setMainStep() {
        this.mCurrentStep = IViewStep.MEDIA_MAIN;
    }

    public void setDetailStep() {
        this.mCurrentStep = IViewStep.MEDIA_DETAIL;
    }

    public boolean isDetail() {
        return mCurrentStep == IViewStep.MEDIA_DETAIL;
    }

    public boolean isMain() {
        return mCurrentStep == IViewStep.MEDIA_MAIN;
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IViewStep.INACTIVE, IViewStep.MEDIA_MAIN, IViewStep.MEDIA_DETAIL})
    public @interface IViewStep {
        int INACTIVE = 0;
        int MEDIA_MAIN = 1;
        int MEDIA_DETAIL = 2;
    }
}
