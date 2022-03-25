package com.xiaoma.music.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.autotracker.model.PlayType;
import com.xiaoma.music.BuildConfig;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.AssistantShowManager;
import com.xiaoma.music.common.manager.AudioFocusHelper;
import com.xiaoma.music.common.manager.UploadPlayManager;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.utils.UsbScanManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import org.ijkplayer.IMediaPlayer;
import org.ijkplayer.IjkMediaPlayer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Created by ZYao.
 * Date ：2018/12/18 0018
 */
class IJKPlayerManager implements IUsbMusic {
    private static final String TAG = "IJKPlayerManager";
    private static final int PROGRESS_UPDATE_INTERVAL = 1000;

    @SuppressLint("StaticFieldLeak")
    private static IJKPlayerManager sInstance;
    private Context mContext;

    public static IJKPlayerManager getInstance() {
        if (sInstance == null) {
            synchronized (IJKPlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new IJKPlayerManager();
                }
            }
        }
        return sInstance;
    }

    private IjkMediaPlayer mPlayer;
    private int mPlayStatus = PlayStatus.INIT;
    private volatile UsbMusic mCurrentMusic;
    private final Set<OnUsbMusicChangedListener> mUsbMusicChangedListeners = new ArraySet<>();
    private final ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    private AudioFocusHelper mAudioFocusHelper;
    private long seekPosition = 0;
    private int nextNumber = 1;


    private IJKPlayerManager() {
    }

    @Override
    public synchronized void init(Context context) {
        if (context == null) {
            throw new NullPointerException("init( context: null ) < context > cannot be null");
        }
        mContext = context.getApplicationContext();
        mAudioFocusHelper = new AudioFocusHelper(mContext, new AudioFocusHelper.PlayerProxy() {
            @Override
            public boolean isPlaying() {
                return IJKPlayerManager.this.isPlaying();
            }

            @Override
            public void continuePlayWithoutFocus() {
                doStartPlay(false);
                UploadPlayManager.getInstance().initPlayTime();
            }

            @Override
            public void pauseWithoutFocus() {
                IJKPlayerManager.this.doPause(false);
            }

            @Override
            public void setVolumeScale(float scale) {
                if (mPlayer != null) {
                    try {
                        mPlayer.setVolume(scale, scale);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void init() {
        if (mPlayer != null) {
            Log.e(TAG, "init() Has init, ignore !");
            return;
        }
        Log.i(TAG, "init()");
        mPlayStatus = PlayStatus.INIT;

        IjkMediaPlayer player = new IjkMediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer player) {
                // 有可能在执行了prepare之后 又调用了pause或者stop方法,所以这里要再次判断下状态
                if (PlayStatus.BUFFERING != mPlayStatus) {
                    KLog.e(TAG, String.format("onPrepared() { CurMusic: %s } PlayStatus has changed: %s", getDumpInfo(mCurrentMusic), mPlayStatus));
                    return;
                }
                if (seekPosition > 0) {
                    seekToPos(seekPosition);
                    seekPosition = 0;
                }
                KLog.i(TAG, String.format("onPrepared() CurMusic: %s", getDumpInfo(mCurrentMusic)));
                if (mAudioFocusHelper.hasAudioFocus()) {
                    doStartPlay(false);
                } else {
                    doPause(false);
                    KLog.e(TAG, String.format("onPrepared() { CurMusic: %s } AudioFocus has loss: ", getDumpInfo(mCurrentMusic)));
                }
            }
        });
        player.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer player, int percent) {
                KLog.i(TAG, String.format("onBufferingUpdate( percent: %s ) CurMusic: %s", percent, getDumpInfo(mCurrentMusic)));
            }
        });
        player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer player) {
                KLog.i(TAG, String.format("onCompletion() CurMusic: %s", getDumpInfo(mCurrentMusic)));
                mPlayStatus = PlayStatus.STOP;
                cancelProgressUpdate();
                dispatchPlayComplete();
                seekPosition = 0;
            }
        });
        player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer player, int what, int extra) {
                KLog.e(TAG, String.format("onError( what: %s, extra: %s)", what, extra));
                mPlayStatus = PlayStatus.FAILED;
                cancelProgressUpdate();
                doStop(true);
                dispatchPlayError(what);
                UploadPlayManager.getInstance().uploadPlayTime(PlayType.USBMUSIC.getPlayType());

                ThreadDispatcher.getDispatcher().postOnMainDelayed(() -> {
                    if (nextNumber < 3 || !AssistantShowManager.getInstance().isShow()) {
                        UsbMusicFactory.getUsbPlayerListProxy().playNext();
                        nextNumber++;
                    } else {
                        //播放三次失败，停止播放，重新设置失败状态
                        UsbMusicFactory.getUsbPlayerProxy().stop();
                        nextNumber = 1;
                    }
                }, 500);
                return true;
            }
        });
        // 防止SeekTo跳跃
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        mPlayer = player;
    }

    @Override
    public synchronized void destroy() {
        if (hasNotInit("destroy()"))
            return;
        Log.i(TAG, "destroy()");
        if (isPlaying()) {
            doStop(true);
        }
        IjkMediaPlayer player = mPlayer;
        if (player != null) {
            player.setOnErrorListener(null);
            player.setOnCompletionListener(null);
            player.setOnBufferingUpdateListener(null);
            player.setOnPreparedListener(null);
            try {
                player.release();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        mPlayer = null;
        mCurrentMusic = null;
        mPlayStatus = PlayStatus.INIT;
    }

    private boolean hasNotInit(String methodCallInfo) {
        if (mPlayer == null) {
            KLog.e(TAG, String.format("%s Player not init", methodCallInfo));
            return true;
        }
        return false;
    }

    @Override
    public long getCurPosition() {
        if (hasNotInit("getCurPosition()"))
            return -1;
        final long pos = mPlayer.getCurrentPosition();
        KLog.i(TAG, String.format("getCurPosition() ret: %s", pos));
        return pos;
    }

    @Override
    public long getDuration() {
        if (hasNotInit("getDuration()"))
            return -1;
        final long duration = mPlayer.getDuration();
        KLog.i(TAG, String.format("getDuration() ret: %s", duration));
        return duration;
    }

    @Override
    public synchronized boolean play(UsbMusic usbMusic) {
        if (usbMusic == null) {
            KLog.e(TAG, "play( usbMusic: null ) Invalid UsbMusic !!!");
            return false;
        }
        boolean success = mAudioFocusHelper.requestUsbAudioFocus(AudioSource.USB_MUSIC);
        if (!success) {
            mPlayStatus = PlayStatus.PAUSE;
            dispatchPlayPause();
            XMToast.showToast(mContext, R.string.play_failed_for_audio_focus);
            KLog.e(TAG, "request audio focus failed");
            return false;
        }
        String path = usbMusic.getPath();
        if (TextUtils.isEmpty(path)) {
            KLog.e(TAG, String.format("play( usbMusic: %s ) Invalid path !!!", getDumpInfo(usbMusic)));
            return false;
        }
        KLog.i(TAG, String.format("play( usbMusic: %s )", getDumpInfo(usbMusic)));
        switchUsbMusicAudioSource();
        if (isPlaying()) {
            doStop(false);
        } else {
            UploadPlayManager.getInstance().initPlayTime();
        }
        try {
            if (mCurrentMusic != usbMusic) seekPosition = 0;
            mCurrentMusic = usbMusic;
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            init();
            IjkMediaPlayer player = mPlayer;
            KLog.i(TAG, "player reset");

            mPlayStatus = PlayStatus.BUFFERING;
            dispatchPlayBuffering();
            player.setDataSource(path);
            KLog.i(TAG, "player setDataSource");
            player.prepareAsync();
            KLog.i(TAG, "player prepareAsync");
        } catch (Exception e) {
            e.printStackTrace();
            mPlayStatus = PlayStatus.FAILED;
            dispatchPlayError(-1);
            return false;
        }
        UploadPlayManager.getInstance().uploadUsbPlayInfo(usbMusic);
        return true;
    }

    private void doPause(boolean abandonFocus) {
        if (hasNotInit("doPause()"))
            return;
        mPlayStatus = PlayStatus.PAUSE;
        KLog.i(TAG, "doPause()");
        mPlayer.pause();
        if (abandonFocus) {
            mAudioFocusHelper.abandonUsbAudioFocus();
        }
        cancelProgressUpdate();
        dispatchPlayPause();
        UploadPlayManager.getInstance().uploadPlayTime(PlayType.USBMUSIC.getPlayType());
    }

    private void continuePlay() {
        if (hasNotInit("continuePlay()"))
            return;
        KLog.i(TAG, "continuePlay() ");
        doStartPlay(true);
        UploadPlayManager.getInstance().initPlayTime();
    }

    @Override
    public void switchPlayPause() {
        switchPlay(!isPlaying());
    }

    @Override
    public void switchPlay(boolean play) {
        if (play) {
            if (mPlayStatus == PlayStatus.INIT
                    || mPlayStatus == PlayStatus.FAILED
                    || mPlayStatus == PlayStatus.STOP) {
                play(mCurrentMusic);
            } else {
                continuePlay();
            }
        } else {
            doPause(true);
        }
    }

    @Override
    public void continuePlayOrPlayFirst() {
        //有mCurrentMusic就继续播放，第一次挂载就选择第一首播放（双屏使用）
        if (mPlayStatus == PlayStatus.INIT
                || mPlayStatus == PlayStatus.FAILED
                || mPlayStatus == PlayStatus.STOP) {
            if (mCurrentMusic != null) {
                continuePlay();
            } else {
                List<UsbMusic> list = UsbScanManager.getInstance().getUsbMusicList();
                if (list != null && !list.isEmpty()) {
                    play(list.get(0));
                }
            }
        } else {
            continuePlay();
        }
    }

    private final AudioSourceManager.PlayerProxy mAudioSourcePlayerProxy = new AudioSourceManager.PlayerProxy() {
        @Override
        public void continuePlay() {
            IJKPlayerManager.this.continuePlay();
        }

        @Override
        public void pause() {
            switchPlay(false);
        }
    };

    private void doStartPlay(boolean requestFocus) {
        mPlayStatus = PlayStatus.PLAYING;
        switchUsbMusicAudioSource();
        if (requestFocus) {
            boolean success = mAudioFocusHelper.requestUsbAudioFocus(AudioSource.USB_MUSIC);
            if (!success) {
                mPlayStatus = PlayStatus.PAUSE;
                dispatchPlayPause();
                XMToast.showToast(mContext, R.string.play_failed_for_audio_focus);
                KLog.e(TAG, "request audio focus failed");
                return;
            }
        }
        if (mPlayer != null) {
            mPlayer.start();
        }
        scheduleProgressUpdate();
        dispatchPlayStart();
        nextNumber = 1;
    }

    private void switchUsbMusicAudioSource() {
        AudioSourceManager.getInstance().switchAudioSource(AudioSource.USB_MUSIC, mAudioSourcePlayerProxy);
    }

    @Override
    public void stop() {
        if (hasNotInit("stop()"))
            return;
        KLog.i(TAG, "stop() ");
        doStop(true);
    }

    private void doStop(boolean abandonFocus) {
        mPlayStatus = PlayStatus.STOP;
        seekPosition = 0;
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        if (abandonFocus) {
            mAudioFocusHelper.abandonUsbAudioFocus();
        }
        cancelProgressUpdate();
        dispatchPlayStop();
        if (abandonFocus) {
            UploadPlayManager.getInstance().uploadPlayTime(PlayType.USBMUSIC.getPlayType());
        }
    }

    @Override
    public boolean isPlaying() {
        if (hasNotInit("isPlaying()"))
            return false;
        final boolean playing = mPlayer.isPlaying();
        KLog.i(TAG, String.format("isPlaying() ret: %s", playing));
        return playing;
    }

    @Override
    public void seekToPos(long position) {
        if (hasNotInit(String.format("seekToPos( position: %s ) Player not init", position)))
            return;
        seekPosition = position;
        KLog.i(TAG, String.format("seekToPos( position: %s )", position));
        mPlayer.seekTo(position);
    }

    @Override
    public UsbMusic getCurrUsbMusic() {
        return mCurrentMusic;
    }

    @Override
    public int getPlayStatus() {
        return mPlayStatus;
    }

    @Override
    public void addMusicChangeListener(OnUsbMusicChangedListener listener) {
        if (listener != null) {
            mUsbMusicChangedListeners.add(listener);
        }
    }

    @Override
    public void removeMusicChangeListener(OnUsbMusicChangedListener listener) {
        mUsbMusicChangedListeners.remove(listener);
    }

    private void dispatchPlayBuffering() {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onBuffering(mCurrentMusic);
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private synchronized void dispatchPlayStart() {
        final UsbMusic usbMusic = mCurrentMusic;
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onPlay(usbMusic);
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private void dispatchPlayPause() {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onPause();
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private void dispatchPlayStop() {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onPlayStop();
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private void dispatchPlayError(int what) {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onPlayFailed(what);
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private void dispatchPlayComplete() {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onCompletion();
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private void dispatchPlayProgress() {
        if (hasNotInit("dispatchPlayProgress()"))
            return;
        final long total = getDuration();
        long curProgress = getCurPosition();
        /*KLog.i(TAG, String.format("dispatchPlayProgress() RealCallbackTime: %s / %s, %s / %s",
                curProgress, total, TimeUtils.timeMsToMMSS(curProgress), TimeUtils.timeMsToMMSS(total)));*/
        if (curProgress > total) {
            curProgress = total;
        }
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onProgressChange(curProgress, total);
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

    private String getDumpInfo(UsbMusic music) {
        if (music == null)
            return "null";
        String name = music.getDisplayName();
        if (TextUtils.isEmpty(name)) {
            name = music.getName();
        }
        return String.format("{ name: %s, path: %s }", name, music.getPath());
    }

    private ScheduledFuture<?> mProgressUpdateFuture;
    private final Runnable mProgressUpdateTask = new Runnable() {
        @Override
        public void run() {
            dispatchPlayProgress();
        }
    };

    private void scheduleProgressUpdate() {
        synchronized (mProgressUpdateTask) {
            if (mProgressUpdateFuture != null) {
                mProgressUpdateFuture.cancel(false);
            }
            mProgressUpdateFuture = mScheduledExecutorService.scheduleWithFixedDelay(
                    mProgressUpdateTask, 0, PROGRESS_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private void cancelProgressUpdate() {
        synchronized (mProgressUpdateTask) {
            if (mProgressUpdateFuture != null) {
                mProgressUpdateFuture.cancel(false);
            }
        }
    }

    @Override
    public boolean isAudioFocusLossTransient() {
        return mAudioFocusHelper.isAudioFocusLossTransient();
    }

    @Override
    public void updateFastPlayProgress(int cur, int total) {
        Set<OnUsbMusicChangedListener> listeners = new ArraySet<>(mUsbMusicChangedListeners);
        for (OnUsbMusicChangedListener listener : listeners) {
            try {
                listener.onProgressChange(cur, total);
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }

}