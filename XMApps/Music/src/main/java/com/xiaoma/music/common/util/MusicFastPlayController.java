package com.xiaoma.music.common.util;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.FastPlayStatus;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;
import com.xiaoma.music.manager.UsbPlayerProxy;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * <pre> @author Create by on Gillben date:   2019/7/19 0019 19:04 desc:   快速播放控制器 </pre>
 */
public final class MusicFastPlayController {
    private static final float UPDATE_PROGRESS_RATIO = 0.03f;
    @FastPlayStatus
    private int playStatus = FastPlayStatus.NONE;
    private volatile int recordFastTime = 0;
    private FastPlayProgressObserver mFastPlayProgressObserver;
    private ValueAnimator mValueAnimator;

    private MusicFastPlayController() {
    }

    public static MusicFastPlayController getInstance() {
        return Holder.FAST_PLAY_CONTROLLER;
    }

    public void setFastPlayObserver(FastPlayProgressObserver playObserver) {
        this.mFastPlayProgressObserver = playObserver;
    }

    public void removeFastPlayObserver() {
        if (mFastPlayProgressObserver != null) mFastPlayProgressObserver = null;
    }

    /**
     * 长按触发 @param advance true 快进   false  快退
     */
    public void starter(boolean advance) {
        if (mValueAnimator != null) return;
        ThreadDispatcher.getDispatcher().postOnMain(() -> {
            int totalTime = 0;
            int currentTime = 0;
            switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
                case AudioSource.ONLINE_MUSIC:
                    OnlineMusicFactory.getKWPlayer().pause();
                    totalTime = OnlineMusicFactory.getKWPlayer().getDuration();
                    currentTime = OnlineMusicFactory.getKWPlayer().getCurrentPos();
                    break;

                case AudioSource.USB_MUSIC:
                    UsbMusicFactory.getUsbPlayerProxy().switchPlay(false);
                    totalTime = (int) UsbMusicFactory.getUsbPlayerProxy().getDuration();
                    currentTime = (int) UsbMusicFactory.getUsbPlayerProxy().getCurPosition();
                    break;
            }

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
                    ThreadDispatcher.getDispatcher().postOnMain(() -> mFastPlayProgressObserver.updatePlayProgress(recordFastTime));
                }
                ThreadDispatcher.getDispatcher().postOnMain(() -> {
                            switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
                                case AudioSource.ONLINE_MUSIC:
                                    KuwoPlayControlObserver.getInstance().IPlayControlObserver_Progress(recordFastTime, 0);
                                    break;

                                case AudioSource.USB_MUSIC:
                                    UsbPlayerProxy.getInstance().updateFastPlayProgress(recordFastTime, finalTotalTime);
                                    break;
                            }
                        }
                );
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
                    seekTo(recordFastTime);
                    if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                        OnlineMusicFactory.getKWPlayer().continuePlay();
                    } else if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                        UsbMusicFactory.getUsbPlayerProxy().continuePlayOrPlayFirst();
                    }
                    break;

                case FastPlayStatus.NEXT:
                    if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                        OnlineMusicFactory.getKWPlayer().playNext();
                    } else if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                        UsbMusicFactory.getUsbPlayerListProxy().playNext();
                    }
                    break;
            }
        });
    }

    private void seekTo(int time) {
        switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
            case AudioSource.ONLINE_MUSIC:
                OnlineMusicFactory.getKWPlayer().seek(time);
                break;

            case AudioSource.USB_MUSIC:
                UsbMusicFactory.getUsbPlayerProxy().seekToPos(time);
                break;
        }
    }

    public interface FastPlayProgressObserver {
        void updatePlayProgress(int newTime);
    }

    private static class Holder {
        private static final MusicFastPlayController FAST_PLAY_CONTROLLER = new MusicFastPlayController();
    }

}
