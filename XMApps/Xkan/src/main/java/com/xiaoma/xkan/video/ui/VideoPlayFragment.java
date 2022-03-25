package com.xiaoma.xkan.video.ui;

import android.annotation.SuppressLint;
import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ByteUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.manager.XkanCarEvent;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.ijkplayer.NiceUtil;

import java.io.File;
import java.text.DecimalFormat;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by LKF on 2019-7-26 0026.
 */
@PageDescComponent(EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC)
public class VideoPlayFragment extends BaseFragment implements SurfaceHolder.Callback, View.OnClickListener, XkanCarEvent.CallBack {
    public static final String EXTRA_MEDIA_INFO = "media_info";
    public static final String EXTRA_SHOW_PLAY_CONTROL = "show_play_control";
    private static final int HIDE_PLAY_CONTROL_DELAY_MS = 5000;
    private static final int UPDATE_PROGRESS_DELAY_MS = 1000;
    private static final int MIN_DURATION_CAN_ACCURATE_SEEK = 10_000;// 大于多少时间的视频才允许精确调进度(短视频精确调进度会出现进度无法正常更新的问题)
    private final String TAG = getClass().getSimpleName();
    private SurfaceView mSvVideo;
    private View mTopBg;
    private ImageView mBack;
    private TextView mTitle;
    private TextView mTvSize;
    private TextView mTimeToSeek;
    private View mControlBg;
    private SeekBar mSeek;
    private Button mSpeed;
    private ImageView mPrevious;
    private ImageView mPlayControl;
    private ImageView mNext;
    private TextView mPlayProgress;

    private IjkMediaPlayer mPlayer;
    private UsbMediaInfo mMediaInfo;
    private long mPlayPosition;

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    private OnWheelKeyListener mWheelKeyListener;
    private boolean mShowVideoDialog;

    private final Runnable mHidePlayControlTask = new Runnable() {
        @Override
        public void run() {
            setPlayControlVisible(false);
        }
    };

    private final Runnable mProgressUpdateTask = new Runnable() {
        @Override
        public void run() {
            updateProgress(mPlayer);
            getUIHandler().postDelayed(this, UPDATE_PROGRESS_DELAY_MS);
        }
    };
    private ICarEvent mCarEvent;

    public static VideoPlayFragment newInstance(UsbMediaInfo mediaInfo, boolean showPlayControl, Callback callback) {
        VideoPlayFragment f = new VideoPlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MEDIA_INFO, mediaInfo);
        args.putBoolean(EXTRA_SHOW_PLAY_CONTROL, showPlayControl);
        f.setArguments(args);
        f.setCallback(callback);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fmt_video_play, container, false);
        mSvVideo = v.findViewById(R.id.sv_video);
        mTopBg = v.findViewById(R.id.top_bg);
        mBack = v.findViewById(R.id.back);
        mTitle = v.findViewById(R.id.title);
        mTvSize = v.findViewById(R.id.tv_size);
        mTimeToSeek = v.findViewById(R.id.time_to_seek);
        mControlBg = v.findViewById(R.id.control_bg);
        mSeek = v.findViewById(R.id.seek);
        mSpeed = v.findViewById(R.id.speed);
        mPrevious = v.findViewById(R.id.previous);
        mPlayControl = v.findViewById(R.id.play_control);
        mNext = v.findViewById(R.id.next);
        mPlayProgress = v.findViewById(R.id.play_progress);
        // 监听手势事件
        final PlayControlGestureListener gestureListener = new PlayControlGestureListener();
        final GestureDetector detector = new GestureDetector(mContext, gestureListener);
        v.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean consumed = detector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (gestureListener.hasScrolled) {
                            if (Direction.HORIZONTAL == gestureListener.direction) {
                                seekTo(mPlayer, mSeek.getProgress(), true);
                            }
                        }
                        break;
                }
                return consumed;
            }
        });
        mSvVideo.getHolder().addCallback(this);
        mBack.setOnClickListener(this);
        mSpeed.setOnClickListener(this);
        mSeek.setEnabled(false);// 禁用SeekBar拖动,由手势事件来响应拖动
        mPrevious.setOnClickListener(this);
        mPlayControl.setOnClickListener(this);
        mNext.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        File mediaFile;
        if (args != null &&
                (mMediaInfo = (UsbMediaInfo) args.getSerializable(EXTRA_MEDIA_INFO)) != null
                && mMediaInfo.getPath() != null &&
                (mediaFile = new File(mMediaInfo.getPath())).exists()) {
            mTitle.setText(mMediaInfo.getMediaName());
            mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTitle.setSingleLine(true);
            mTitle.setSelected(true);
            mTitle.setFocusable(true);
            mTitle.setFocusableInTouchMode(true);

            mTvSize.setText(ByteUtils.getFileSize(mediaFile.length()));
            setPlayControlVisible(args.getBoolean(EXTRA_SHOW_PLAY_CONTROL, false));
        } else {
            doPlayFailed(mPlayer);
        }
        mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        doPause(mPlayer, true);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        doPause(mPlayer, false);
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        doPlay(mPlayer);
                        break;
                }
            }
        };

        XkanCarEvent.getInstance().setmCallBack(this);
        XmCarEventDispatcher.getInstance().registerEvent(mCarEvent = new ICarEvent() {
            @Override
            public void onCarEvent(CarEvent event) {
                // 监听屏幕开关
                if (SDKConstants.ID_SCREEN_STATUS == event.id) {
                    boolean isScreenOn = (boolean) event.value;
                    if (!isScreenOn) {
                        doPause(mPlayer, true);
                    }
                    Log.e(TAG, String.format("onCarEvent: ID_SCREEN_STATUS: %s", isScreenOn));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelHidePlayControl();
        cancelUpdateProgress();
        XkanCarEvent.getInstance().setmCallBack(null);
        XmCarEventDispatcher.getInstance().unregisterEvent(mCarEvent);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, String.format("onResume -> playPosition: %s, showVideoDialog: %s", mPlayPosition, mShowVideoDialog));
        if (mPlayPosition > 0 && mPlayer != null) {
            if (!mShowVideoDialog) {
                doPlay(mPlayer);
            }
            seekTo(mPlayer, mPlayPosition, true);
            mPlayPosition = 0;
        }
        XmWheelManager.getInstance().register(mWheelKeyListener = new OnWheelKeyListener.Stub() {
            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) {
                if (WheelKeyEvent.ACTION_CLICK == keyAction) {
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                            previous();
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                            next();
                            break;
                    }
                }
                return true;
            }

            @Override
            public String getPackageName() {
                return mContext != null ? mContext.getPackageName() : "com.xiaoma.xkan";
            }
        }, new int[]{
                WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD,
                WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB});
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (mPlayer != null) {
            mPlayPosition = mPlayer.getCurrentPosition();
            doPause(mPlayer, true);
        }
        XmWheelManager.getInstance().unregister(mWheelKeyListener);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        IjkMediaPlayer player = null;
        try {
            player = new IjkMediaPlayer();
            player.setDataSource(mMediaInfo != null ? mMediaInfo.getPath() : "");
            player.setScreenOnWhilePlaying(true);
            player.setDisplay(holder);
            player.setLogEnabled(ConfigManager.ApkConfig.isDebug());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnVideoSizeChangedListener(new IjkMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer player, int width, int height, int sar_num, int sar_den) {
                    Log.i(TAG, String.format("onVideoSizeChanged -> w: %s, h: %s", width, height));
                    int newWidth = (int) ((float) mSvVideo.getMeasuredHeight() * width / height);
                    if (mSvVideo.getLayoutParams().width != newWidth) {
                        mSvVideo.getLayoutParams().width = newWidth;
                        mSvVideo.requestLayout();
                    }
                }
            });
            player.setOnErrorListener(new IjkMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer player, int what, int extra) {
                    doPlayFailed((IjkMediaPlayer) player);
                    return false;
                }
            });
            player.setOnCompletionListener(new IjkMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer player) {
                    doPlayComplete((IjkMediaPlayer) player);
                }
            });
            player.setOnPreparedListener(new IjkMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer player) {
                    Log.i(TAG, String.format("onPrepared -> duration: %s", player.getDuration()));
                    if (player.getDuration() >= MIN_DURATION_CAN_ACCURATE_SEEK) {
                        IjkMediaPlayer ijkPlayer = (IjkMediaPlayer) player;
                        // 防止SeekTo跳跃
                        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                        // 设置seekTo能够快速seek到指定位置并播放
                        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
                    }
                    doPlay((IjkMediaPlayer) player);
                    if (mPlayPosition > 0) {
                        seekTo((IjkMediaPlayer) player, mPlayPosition, true);
                        mPlayPosition = 0;
                    }
                }
            });

            // 关闭mediacodec硬解，使用软解
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            //设置播放前的探测时间 1,达到首屏秒开效果
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            // 关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡住，控制台打印 FFP_MSG_BUFFERING_START
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);

            player.prepareAsync();
            mPlayer = player;
        } catch (Exception e) {
            e.printStackTrace();
            doPlayFailed(player);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, String.format("surfaceChanged -> format: %s, width: %s, height: %s", format, width, height));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        abandonAudioFocus();
        if (mPlayer != null) {
            try {
                mPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer = null;
        }
    }

    private void doPlay(IjkMediaPlayer player) {
        String dumpInfo;
        if (player != null) {
            if (requestAudioFocus()) {
                dumpInfo = "Gain focus and start";
                player.start();
                mPlayControl.setImageResource(R.drawable.ic_player_pause);
            } else {
                dumpInfo = "Cannot gain focus";
                XMToast.toastException(getContext(), R.string.tip_audio_fosuc_fail);
                doPause(player, false);
            }
        } else {
            dumpInfo = "Player is null";
        }
        Log.e(TAG, "doPlay: " + dumpInfo);
        scheduleUpdateProgress();
    }

    private void doPause(IjkMediaPlayer player, boolean abandonFocus) {
        String dumpInfo;
        if (player != null) {
            dumpInfo = "abandon focus and doPause";
            player.pause();
            if (abandonFocus) {
                abandonAudioFocus();
            }
        } else {
            dumpInfo = "Player is null";
        }
        Log.e(TAG, "doPause: " + dumpInfo);
        cancelUpdateProgress();
        updateProgress(player);
        mPlayControl.setImageResource(R.drawable.ic_player_start);
    }

    private boolean mSeekByDragging = false;

    // 滑动到指定的进度的增量
    private void seekBy(IjkMediaPlayer player, int deltaPosition) {
        Log.i(TAG, String.format("seekBy -> dSecond: %s", deltaPosition / 1000));
        seekTo(player, mSeek.getProgress() + deltaPosition, false);
    }

    private void seekTo(IjkMediaPlayer player, long position, boolean setToPlayer) {
        String dumpInfo = String.format("toTime: %s, setToPlayer: %s",
                NiceUtil.formatTime(position), setToPlayer);
        long newPosition = Math.max(Math.min(position, mSeek.getMax()), 0);
        if (setToPlayer) {
            if (player != null) {
                player.seekTo(newPosition);
                boolean playing = isPlaying(player);
                dumpInfo += (", playing: " + dumpInfo);
                if (playing) {
                    scheduleUpdateProgress();
                } else {
                    cancelUpdateProgress();
                    updateProgress(player);
                }
            }
            mTimeToSeek.setVisibility(View.GONE);
            mSeekByDragging = false;
            XmAutoTracker.getInstance().onEvent(
                    EventConstants.SlideEvent.VIDEO_TRACK,
                    getClass().getSimpleName(),
                    EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC);
        } else {
            cancelUpdateProgress();// 拖动过程中,停止进度更新
            mSeekByDragging = true;
            mTimeToSeek.setVisibility(View.VISIBLE);
            mTimeToSeek.setText(NiceUtil.formatTime(newPosition));
        }
        if (player != null) {
            String timeCur = NiceUtil.formatTime(newPosition);
            String timeMax = NiceUtil.formatTime(player.getDuration());
            String timeText = String.format("%s/%s", timeCur, timeMax);
            mPlayProgress.setText(timeText);
            mSeek.setProgress((int) newPosition);
        }
        if (isPlayControlHidden()) {
            setPlayControlVisible(true);
        }
        delayHidePlayControl();
        Log.i(TAG, "seekTo -> " + dumpInfo);
    }

    private void doPlayFailed(IjkMediaPlayer player) {
        Log.e(TAG, "doPlayFailed");
        if (mCallback != null) {
            mCallback.onPlayFailed();
        }
        doPause(player, true);
    }

    private void doPlayComplete(IjkMediaPlayer player) {
        if (player == null) {
            Log.e(TAG, "doPlayComplete -> Player is null");
            return;
        }
        Log.i(TAG, "doPlayComplete");
        doPause(player, true);
        seekTo(player, 0, true);
        //播放结束自动播放下一个视频
        if (mCallback != null) {
            mCallback.onPlayComplete();
        }
    }

    private boolean isPlaying(IjkMediaPlayer player) {
        return player != null && player.isPlaying();
    }

    private boolean requestAudioFocus() {
        int rlt = mAudioManager.requestAudioFocus(
                mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        Log.i(TAG, String.format("requestAudioFocus -> rlt: %s", rlt));
        //XMToast.toastSuccess(getContext(), "焦点抢占结果: " + rlt);
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
    }

    private void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private void setPlayControlVisible(boolean visible) {
        if (visible) {
            mTopBg.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mTvSize.setVisibility(View.VISIBLE);
            mControlBg.setVisibility(View.VISIBLE);
            mSeek.setVisibility(View.VISIBLE);
            mSpeed.setVisibility(View.VISIBLE);
            mPrevious.setVisibility(View.VISIBLE);
            mPlayControl.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.VISIBLE);
            mPlayProgress.setVisibility(View.VISIBLE);
            delayHidePlayControl();
        } else {
            mTopBg.setVisibility(View.GONE);
            mBack.setVisibility(View.GONE);
            mTitle.setVisibility(View.GONE);
            mTvSize.setVisibility(View.GONE);
            mControlBg.setVisibility(View.GONE);
            mSeek.setVisibility(View.GONE);
            mSpeed.setVisibility(View.GONE);
            mPrevious.setVisibility(View.GONE);
            mPlayControl.setVisibility(View.GONE);
            mNext.setVisibility(View.GONE);
            mPlayProgress.setVisibility(View.GONE);
            cancelHidePlayControl();
        }
    }

    private boolean isPlayControlHidden() {
        return View.VISIBLE != mSeek.getVisibility();
    }

    private void delayHidePlayControl() {
        Handler handler = getUIHandler();
        handler.removeCallbacks(mHidePlayControlTask);
        handler.postDelayed(mHidePlayControlTask, HIDE_PLAY_CONTROL_DELAY_MS);
    }

    private void cancelHidePlayControl() {
        getUIHandler().removeCallbacks(mHidePlayControlTask);
    }

    private void adjustPlaySpeed(IjkMediaPlayer player) {
        //倍速
        if (player != null) {
            float curSpeed = player.getSpeed(1f);
            float newSpeed;
            if (0.5f == curSpeed) {
                newSpeed = 1.0f;
            } else if (1.0f == curSpeed) {
                newSpeed = 1.25f;
            } else if (1.25f == curSpeed) {
                newSpeed = 1.5f;
            } else if (1.5f == curSpeed) {
                newSpeed = 2.0f;
            } else if (2.0f == curSpeed) {
                newSpeed = 0.5f;
            } else {
                newSpeed = 1.0f;
            }
            player.setSpeed(newSpeed);
            DecimalFormat df = new DecimalFormat(1.0f == newSpeed ? "#" : "#.#");
            mSpeed.setText(getString(R.string.player_speed_format, df.format(newSpeed)));
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SPEED,
                    String.valueOf(newSpeed),
                    getClass().getSimpleName(),
                    EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC);

            Log.i(TAG, String.format("adjustPlaySpeed -> cur: %s, new: %s", curSpeed, newSpeed));
        } else {
            Log.e(TAG, "adjustPlaySpeed -> Player is null");
        }
        delayHidePlayControl();
    }

    private void back() {
        if (mCallback != null) {
            mCallback.onBack();
        }
    }

    private void previous() {
        if (mShowVideoDialog)
            return;
        delayHidePlayControl();
        if (mCallback != null) {
            mCallback.onPrevious();
        }
    }

    private void next() {
        if (mShowVideoDialog)
            return;
        delayHidePlayControl();
        if (mCallback != null) {
            mCallback.onNext();
        }
    }

    private void playControl() {
        delayHidePlayControl();
        if (isPlaying(mPlayer)) {
            doPause(mPlayer, true);
        } else {
            doPlay(mPlayer);
        }
    }

    private void scheduleUpdateProgress() {
        getUIHandler().removeCallbacks(mProgressUpdateTask);
        getUIHandler().post(mProgressUpdateTask);
    }

    private void cancelUpdateProgress() {
        getUIHandler().removeCallbacks(mProgressUpdateTask);
    }

    private void updateProgress(IjkMediaPlayer player) {
        String dumpInfo;
        if (player != null) {
            long cur = player.getCurrentPosition();
            long max = player.getDuration();
            if (cur > max) {
                cur = max;
            }
            String timeCur = NiceUtil.formatTime(cur);
            String timeMax = NiceUtil.formatTime(max);
            String timeText = String.format("%s/%s", timeCur, timeMax);
            String progressText = String.format("%s / %s", cur, max);
            mSeek.setMax((int) max);
            if (!mSeekByDragging) {
                mSeek.setProgress((int) cur);
                mPlayProgress.setText(timeText);
            }
            dumpInfo = String.format("time: %s, progress: %s, seekByDragging: %s",
                    timeText, progressText, mSeekByDragging);
        } else {
            dumpInfo = "Player is null";
        }
        Log.i(TAG, String.format("updateProgress -> %s", dumpInfo));
    }

    @NormalOnClick({EventConstants.NormalClick.BACK,
            EventConstants.NormalClick.SPEED,
            EventConstants.NormalClick.VIDEOPREVIOUS,
            EventConstants.NormalClick.RESTARTORPAUSE,
            EventConstants.NormalClick.VIDEONEXT})
    @ResId({R.id.back, R.id.speed, R.id.previous, R.id.play_control, R.id.next})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                break;
            case R.id.speed:
                adjustPlaySpeed(mPlayer);
                break;
            case R.id.previous:
                previous();
                break;
            case R.id.play_control:
                playControl();
                break;
            case R.id.next:
                next();
                break;
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onBanVideo() {
        back();
        XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.driving_forbid_watch_video);
    }

    @Override
    public void onUnBanVideo() {
        if (!mShowVideoDialog) {
            showVideoDialog();
        }
    }


    private void showVideoDialog() {
        final boolean isPlayingBeforeShow = isPlaying(mPlayer);
        doPause(mPlayer, true);
        final ConfirmDialog dialog = new ConfirmDialog(getActivity(), false);
        dialog.setCancelable(false);
        dialog.setContent(getString(R.string.driving_watch_video_message) + "\n" + getString(R.string.driving_watch_video_tip))
                .setPositiveButton(getString(R.string.driving_watch_video_replay), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.CONTINUE_PLAY, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (isPlayingBeforeShow) {
                            doPlay(mPlayer);
                        }

                    }
                })
                .setNegativeButton(getString(R.string.driving_watch_video_stop_close), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.STOP_CLOSE, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        back();
                    }
                })
                .show();
        mShowVideoDialog = true;
    }

    public interface Callback {
        void onBack();

        void onPrevious();

        void onNext();

        void onPlayFailed();

        void onPlayComplete();
    }

    private class PlayControlGestureListener extends GestureDetector.SimpleOnGestureListener {
        boolean hasScrolled;
        Direction direction;

        PlayControlGestureListener() {
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "onDown");
            hasScrolled = false;
            direction = Direction.NONE;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(TAG, "onSingleTapUp");
            setPlayControlVisible(isPlayControlHidden());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, String.format("onScroll -> distanceX: %s, distanceY: %s, e1: %s, e2: %s",
                    distanceX, distanceY, MotionEvent.actionToString(e1.getAction()), MotionEvent.actionToString(e2.getAction())));
            hasScrolled = true;
            if (Direction.NONE == direction) {
                direction = Math.abs(distanceX) > Math.abs(distanceY) ?
                        Direction.HORIZONTAL : Direction.VERTICAL;
            }
            if (Direction.HORIZONTAL == direction) {
                // 横向滑动,调节进度
                if (mPlayer != null && Math.abs(distanceX) > 1f) {
                    int deltaProgress = (int) (distanceX * mSeek.getMax() / mSeek.getMeasuredWidth());
                    if (deltaProgress != 0) {
                        seekBy(mPlayer, -deltaProgress);
                    }
                }
            } /*else if (Direction.VERTICAL == direction) {
                // 纵向滑动,调节音量or亮度
                // 暂无此需求
            }*/
            return true;
        }
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }
}
