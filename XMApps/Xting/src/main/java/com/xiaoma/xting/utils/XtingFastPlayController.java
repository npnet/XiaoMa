package com.xiaoma.xting.utils;

import android.animation.ValueAnimator;
import android.annotation.IntDef;
import android.view.animation.LinearInterpolator;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author youthyJ
 * @date 2019-07-20
 */
public class XtingFastPlayController {
    private static final float UPDATE_PROGRESS_RATIO = 0.03f;

    @FastPlayStatus
    private int playStatus = FastPlayStatus.NONE;
    private volatile int recordFastTime = 0;
    private FastPlayProgressObserver mFastPlayProgressObserver;
    private ValueAnimator mValueAnimator;

    private XtingFastPlayController() {
    }

    public static XtingFastPlayController getInstance() {
        return Holder.FAST_PLAY_CONTROLLER;
    }

    public void setFastPlayObserver(FastPlayProgressObserver playObserver) {
        this.mFastPlayProgressObserver = playObserver;
    }

    public void removeFastPlayObserver() {
        mFastPlayProgressObserver = null;
    }

    /**
     * 长按触发
     *
     * @param advance true 快进   false  快退
     */
    public void starter(boolean advance) {
        ThreadDispatcher.getDispatcher().postOnMain(() -> {
            if (mValueAnimator != null) {
                return;
            }
            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
            int totalTime = (int) playerInfo.getDuration();
            int currentTime = (int) playerInfo.getProgress();

            int duration;
            if (advance) {
                mValueAnimator = ValueAnimator.ofInt(currentTime, totalTime);
                duration = (int) ((totalTime - currentTime) * UPDATE_PROGRESS_RATIO);
            } else {
                mValueAnimator = ValueAnimator.ofInt(currentTime, 0);
                duration = (int) (currentTime * UPDATE_PROGRESS_RATIO);
            }

            final int finalTotalTime = totalTime;
            mValueAnimator.setDuration(duration);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(animation -> {
                recordFastTime = (int) animation.getAnimatedValue();
                if (recordFastTime >= finalTotalTime) {
                    playStatus = FastPlayStatus.NEXT;
                } else {
                    playStatus = FastPlayStatus.REPLAY;
                }

                if (mFastPlayProgressObserver != null) {
                    ThreadDispatcher.getDispatcher().postOnMain(() ->
                            mFastPlayProgressObserver.updatePlayProgress(recordFastTime));
                }

                ThreadDispatcher.getDispatcher().postOnMain(() ->
                        PlayerInfoImpl.newSingleton().onPlayerProgress(recordFastTime, totalTime));
            });
            mValueAnimator.start();
        });
    }

    public void closeStarter() {
        ThreadDispatcher.getDispatcher().postOnMain(() -> {
            if (mValueAnimator == null) {
                return;
            }
            mValueAnimator.cancel();
            mValueAnimator = null;
            switch (playStatus) {
                case FastPlayStatus.NONE:
                case FastPlayStatus.REPLAY:
                    PlayerSourceFacade.newSingleton().getPlayerControl().seekProgress(recordFastTime);
                    PlayerSourceFacade.newSingleton().getPlayerControl().play();
                    break;

                case FastPlayStatus.NEXT:
                    PlayerSourceFacade.newSingleton().getPlayerControl().playNext();
                    break;
            }
        });
    }

    public interface FastPlayProgressObserver {
        void updatePlayProgress(int newTime);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FastPlayStatus.NONE, FastPlayStatus.NEXT, FastPlayStatus.REPLAY})
    public @interface FastPlayStatus {

        int NONE = 0;   //正常更新进度
        int NEXT = 1;   //一首歌快进完毕，释放后播放下一首
        int REPLAY = 2; // 一首歌回退起点，重新播放
    }

    private static class Holder {
        private static final XtingFastPlayController FAST_PLAY_CONTROLLER = new XtingFastPlayController();
    }
}
