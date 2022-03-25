package com.xiaoma.xkan.ijkplayer;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaoma.utils.log.KLog;
import com.xiaoma.xkan.common.manager.AudioFocusManager;
import com.xiaoma.xkan.common.manager.HardCodeManager;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class NiceVideoPlayer extends FrameLayout implements INiceVideoPlayer {

    //播放错误
    public static final int STATE_ERROR = -1;
    //播放未开始
    public static final int STATE_IDLE = 0;
    //播放准备中
    public static final int STATE_PREPARING = 1;
    //播放准备就绪
    public static final int STATE_PREPARED = 2;
    //正在播放
    public static final int STATE_PLAYING = 3;
    //暂停播放
    public static final int STATE_PAUSED = 4;
    //正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
    public static final int STATE_BUFFERING_PLAYING = 5;
    //正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
    public static final int STATE_BUFFERING_PAUSED = 6;
    //播放完成
    public static final int STATE_COMPLETED = 7;
    //IjkPlayer
    public static final int TYPE_IJK = 111;
    //MediaPlayer
    public static final int TYPE_NATIVE = 222;
    private int mPlayerType = TYPE_IJK;
    private int mCurrentState = STATE_IDLE;
    private Context mContext;
    private IMediaPlayer mMediaPlayer;
    private FrameLayout mContainer;
    private SurfaceView mSurfaceView;
    private NiceVideoPlayerController mController;
    private String mUrl;
    private int mBufferPercentage;
    private long skipToPosition;
    private static final String MEDIACODEC = "mediacodec";
    private static final String MEDIACODEC_AUTO_ROTATE = "mediacodec-auto-rotate";
    private static final String MEDIACODEC_HANDLE_RESOLUTION_CHANGE = "mediacodec-handle-resolution-change";

    private static final String ENABLE_ACCURATE_SEEK = "enable-accurate-seek";
    private static final long MEDIC_ON_VALUE = 1;
    private static final long MEDIC_OFF_VALUE = 0;

    public NiceVideoPlayer(Context context) {
        this(context, null);
    }

    public NiceVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    @Override
    public void setUrl(String url) {
        mUrl = url;
    }

    public void setController(NiceVideoPlayerController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setNiceVideoPlayer(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    //设置播放器类型 IjkPlayer or MediaPlayer
    public void setPlayerType(int playerType) {
        mPlayerType = playerType;
    }

    @Override
    public void setSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mMediaPlayer).setSpeed(speed);
        } else {
            KLog.d("只有IjkPlayer才能设置播放速度");
        }
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            NiceVideoPlayerManager.instance().setCurrentNiceVideoPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        } else {
            KLog.d("NiceVideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法.");
        }
    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        Log.d("videocontroller", "restart: " + mCurrentState);
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
            openMediaPlayer();
        } else {
            KLog.d("NiceVideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
        }
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    /**
     * 0.0f - 1.0范围
     *
     * @param leftVolume
     * @param rightVolume
     */
    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public float getSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getSpeed(speed);
        }
        return 0;
    }


    private void initAudioManager() {
        AudioFocusManager.getInstance().init(mContext);
        AudioFocusManager.getInstance().requestAudioFocus();
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            switch (mPlayerType) {
                case TYPE_NATIVE:
                    mMediaPlayer = new AndroidMediaPlayer();
                    break;
                case TYPE_IJK:
                default:
                    mMediaPlayer = new IjkMediaPlayer();
                    KLog.d("NiceVideoPlayer", "是否支持硬解码" + HardCodeManager.getInstance().isSupportMediaCodecHardDecoder());
                    //硬解码配置
                   /* if (HardCodeManager.getInstance().isSupportMediaCodecHardDecoder()) {
                        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, MEDIACODEC, MEDIC_ON_VALUE);//设置为硬解

                        //目前固定为横屏播放，就不需要开启旋转
                        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, MEDIACODEC_AUTO_ROTATE, MEDIC_OFF_VALUE);
                        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, MEDIACODEC_HANDLE_RESOLUTION_CHANGE, MEDIC_OFF_VALUE);
                        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", MEDIC_OFF_VALUE);//支持高清
                    }*/
                    //解决拖动进度时 产生倒退或者快进的现象(但这样设置会出现视频停止播放的情况)
                    //((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, ENABLE_ACCURATE_SEEK, MEDIC_VALUE);

                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", MEDIC_OFF_VALUE);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", MEDIC_ON_VALUE);//跳帧处理（-1~120）。CPU处理慢时，进行跳帧处理，保证音视频同步
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);//0为一进入就播放,1为进入时不播放
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);//是否开启环路过滤:0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 1);//重连次数
                    ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", MEDIC_OFF_VALUE);//是否开启预缓冲
                    break;
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void initTextureView() {
        if (mSurfaceView == null) {
            mSurfaceView = new SurfaceView(mContext);
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (mMediaPlayer == null) {
                            initMediaPlayer();
                        }
                        String source = mMediaPlayer.getDataSource();
                        if (TextUtils.isEmpty(source)) {
                            openMediaPlayer();
                        } else {
                            mMediaPlayer.setSurface(holder.getSurface());
                            restart();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
        }
    }

    private void addTextureView() {
        try {
            mContainer.removeView(mSurfaceView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mContainer.addView(mSurfaceView, 0, params);
    }

    private void openMediaPlayer() {
        // 屏幕常亮
        mContainer.setKeepScreenOn(true);
        mSurfaceView.setKeepScreenOn(true);
        // 设置监听
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        // 设置dataSource
        try {
            mMediaPlayer.setSurface(mSurfaceView.getHolder().getSurface());
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl));
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mController.onPlayStateChanged(mCurrentState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            mController.onPlayStateChanged(mCurrentState);
            mp.start();
            // 跳到指定位置播放
            if (skipToPosition != 0) {
                mp.seekTo(skipToPosition);
            }
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            try {
                //mTextureView.adaptVideoSize(width, height);
                if (mSurfaceView != null) {
                    ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
                    if (lp != null) {
                        int newWidth = (int) ((float) width * mSurfaceView.getMeasuredHeight() / height);
                        if (lp.width != newWidth) {
                            lp.width = newWidth;
                            mSurfaceView.requestLayout();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            mController.onPlayStateChanged(mCurrentState);
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mCurrentState = STATE_ERROR;
                mController.onPlayStateChanged(mCurrentState);
            }
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                mCurrentState = STATE_PLAYING;
                mController.onPlayStateChanged(mCurrentState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                }
                mController.onPlayStateChanged(mCurrentState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.onPlayStateChanged(mCurrentState);
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.onPlayStateChanged(mCurrentState);
                }
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };

    @Override
    public void releasePlayer() {
        AudioFocusManager.getInstance().abandonAudioFocus();
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mContainer.removeView(mSurfaceView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentState = STATE_IDLE;
        mController.onPlayStateChanged(mCurrentState);
    }

    @Override
    public void release() {
        // 释放播放器
        releasePlayer();
        // 恢复控制器
        if (mController != null) {
            mController.reset();
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public void requestAudioFocus() {
        AudioFocusManager.getInstance().init(mContext);
        AudioFocusManager.getInstance().requestAudioFocus();
    }

    @Override
    public void abandonAudioFocus() {
        AudioFocusManager.getInstance().abandonAudioFocus();
    }

}
