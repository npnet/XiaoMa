package com.xiaoma.xkan.ijkplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.progress.loading.ProgressDrawable;
import com.xiaoma.utils.ViewUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.EventConstants;

public class TxVideoPlayerController extends NiceVideoPlayerController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private ImageView mImage;
    private View mTop;
    private ImageView mBack;
    private ImageView mPrevious;
    private ImageView mNext;
    private TextView mTitle;
    private RelativeLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private Button mSpeed;
    private TextView mChangePositionCurrent;
    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;
    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;
    private LinearLayout mError;
    // 1 1.25 1.5 2 0.5
    private static final float SPEED_X1 = 1.0f;
    private static final float SPEED_X125 = 1.25f;
    private static final float SPEED_X15 = 1.5f;
    private static final float SPEED_X2 = 2.0f;
    private static final float SPEED_X05 = 0.5f;
    private float mCurrentSpeed = SPEED_X1;
    private INiceControllerVideoListener niceControllerVideoListener;
    private ProgressBar mPb;
    private TextView mTvSize;

    private boolean isDragSeekBar = false;

    private static final String TAG = "videocontroller";

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            cancelDismissControlViewTimer();
            hideTopBottomView(true);
        }
    };

    @Override
    public void hideTopBottomView(boolean isHideView) {
        super.hideTopBottomView(isHideView);
        mTop.setVisibility(isHideView ? View.GONE : View.VISIBLE);
        mBottom.setVisibility(isHideView ? View.GONE : View.VISIBLE);
    }

    public TxVideoPlayerController(Activity context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.tx_video_palyer_controller, this, true);
        mImage = findViewById(R.id.image);
        mTop = findViewById(R.id.top);
        mBack = findViewById(R.id.back);
        mPrevious = findViewById(R.id.previous);
        mNext = findViewById(R.id.next);
        mTitle = findViewById(R.id.title);
        mTvSize = findViewById(R.id.tv_size);
        mBottom = findViewById(R.id.bottom);
        mRestartPause = findViewById(R.id.restart_or_pause);
        mPosition = findViewById(R.id.position);
        mDuration = findViewById(R.id.duration);
        mSeek = findViewById(R.id.seek);
        mSpeed = findViewById(R.id.speed);
        mChangePositionCurrent = findViewById(R.id.change_position_current);
        mChangeBrightness = findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = findViewById(R.id.change_brightness_progress);
        mChangeVolume = findViewById(R.id.change_volume);
        mChangeVolumeProgress = findViewById(R.id.change_volume_progress);
        mError = findViewById(R.id.error);
        ImageView ivError = findViewById(R.id.iv_empty);
        ivError.setImageResource(R.drawable.img_video_error);
//        mRetry = findViewById(R.id.retry);
//        mCompleted = findViewById(R.id.completed);
//        mReplay = findViewById(R.id.replay);
        mPb = findViewById(R.id.progress);
        mPb.setIndeterminateDrawable(new ProgressDrawable(mContext, mPb));
        mBack.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
//        mRetry.setOnClickListener(this);
//        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        mSpeed.setOnClickListener(this);
        this.setOnClickListener(this);
        mTitle.setSelected(true);
        //增大close点击区域
        ViewUtils.expandViewTouchDelegate(mBack);
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void setSize(String size) {
        mTvSize.setText(String.format(getResources().getString(R.string.file_size), size));
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public void setLength(long length) {
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        Log.d(TAG, " play state: " + playState);
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                setBottomViewClickable(true);
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                cancelDismissControlViewTimer();
                hideTopBottomView(true);
                mImage.setVisibility(View.GONE);
                mPb.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
//                startDismissControlViewTimer();
                break;
            case NiceVideoPlayer.STATE_PLAYING:
                mNiceVideoPlayer.requestAudioFocus();
                mPb.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);

                break;
            case NiceVideoPlayer.STATE_PAUSED:
                mPb.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                mPb.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                mPb.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                break;
            case NiceVideoPlayer.STATE_ERROR:
                mNiceVideoPlayer.abandonAudioFocus();
                cancelUpdateProgressTimer();
                mPb.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mSeek.setProgress(0);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mPosition.setText(NiceUtil.formatTime(0));
                mDuration.setText(NiceUtil.formatTime(0));
                setBottomViewClickable(false);
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                mNiceVideoPlayer.abandonAudioFocus();
                cancelUpdateProgressTimer();
                mImage.setVisibility(View.VISIBLE);
                if (niceControllerVideoListener != null) {
                    niceControllerVideoListener.onFinish();
                }
                break;
            default:

                break;
        }
    }

    public void setBottomViewClickable(boolean clickable) {
        mSeek.setOnSeekBarChangeListener(clickable ? this : null);
        mSpeed.setClickable(clickable);
        mRestartPause.setClickable(clickable);
    }

    @Override
    protected void reset() {
        cancelUpdateProgressTimer();

        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);
        mImage.setVisibility(View.VISIBLE);
        mBottom.setVisibility(View.VISIBLE);
        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mPb.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);

        setBottomViewClickable(true);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.BACK, EventConstants.NormalClick.RESTARTORPAUSE, EventConstants.NormalClick.RETRY, EventConstants.NormalClick.REPLAY, EventConstants.NormalClick.SPEED, EventConstants.NormalClick.VIDEOPREVIOUS, EventConstants.NormalClick.VIDEONEXT})
    @ResId({R.id.back, R.id.restart_or_pause, /*R.id.retry, R.id.replay,*/ R.id.speed, R.id.previous, R.id.next})
    public void onClick(View v) {
        if (v == mBack) {
            //finish activity
            if (niceControllerVideoListener != null) {
                niceControllerVideoListener.onClickClose();
            }

        } else if (v == mRestartPause) {
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
            Log.d(TAG, "click play");
        } else if (v == mSpeed) {
            setSpeed(mNiceVideoPlayer.getSpeed(mCurrentSpeed));
        } else if (v == mPrevious) {
            if (niceControllerVideoListener != null) {
                niceControllerVideoListener.onClickPrevious();
            }
        } else if (v == mNext) {
            if (niceControllerVideoListener != null) {
                niceControllerVideoListener.onClickNext();
            }
        }
    }

    public void setSpeed(float speed) {
        if (speed == SPEED_X1) {
            mNiceVideoPlayer.setSpeed(SPEED_X125);
            mSpeed.setText(R.string.x125_speed);
            mCurrentSpeed = SPEED_X125;
        } else if (speed == SPEED_X125) {
            mNiceVideoPlayer.setSpeed(SPEED_X15);
            mSpeed.setText(R.string.x15_speed);
            mCurrentSpeed = SPEED_X15;
        } else if (speed == SPEED_X15) {
            mNiceVideoPlayer.setSpeed(SPEED_X2);
            mSpeed.setText(R.string.x2_speed);
            mCurrentSpeed = SPEED_X2;
        } else if (speed == SPEED_X2) {
            mNiceVideoPlayer.setSpeed(SPEED_X05);
            mSpeed.setText(R.string.x05_speed);
            mCurrentSpeed = SPEED_X05;

        } else if (speed == SPEED_X05) {
            mNiceVideoPlayer.setSpeed(SPEED_X1);
            mSpeed.setText(R.string.x1_speed);
            mCurrentSpeed = SPEED_X1;
        }
        //倍速
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SPEED, String.valueOf(mCurrentSpeed), "VideoPlayActivity", EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isDragSeekBar) {
            if (mChangePositionCurrent.getVisibility() == View.GONE) {
                mChangePositionCurrent.setVisibility(View.VISIBLE);
            }
            long newPosition = (long) (mNiceVideoPlayer.getDuration() * progress / 100f);
            mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
            mPosition.setText(NiceUtil.formatTime(newPosition));
            Log.d("videocontroller", "onProgressChanged : " + NiceUtil.formatTime(newPosition) + " ---speed: " + mNiceVideoPlayer.getSpeed(mCurrentSpeed));
        } else {
            mChangePositionCurrent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragSeekBar = true;
        mChangePositionCurrent.setVisibility(View.VISIBLE);
        long newPosition = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDragSeekBar = false;
        long position = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVideoPlayer.seekTo(position);
        XmAutoTracker.getInstance().onEvent(EventConstants.SlideEvent.VIDEO_TRACK,
                "VideoPlayActivity", EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC);

    }

    @Override
    protected void updateProgress() {
        long position = mNiceVideoPlayer.getCurrentPosition();
        long duration = mNiceVideoPlayer.getDuration();
        int bufferPercentage = mNiceVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        isDragSeekBar = true;
        if (mChangePositionCurrent.getVisibility() == View.GONE) {
            mChangePositionCurrent.setVisibility(View.VISIBLE);
        }
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
        //滑动屏幕 显示seekbar
        hideTopBottomView(false);
        cancelDismissControlViewTimer();
    }


    @Override
    protected void hideChangePosition() {
        isDragSeekBar = false;
        mChangePositionCurrent.setVisibility(View.GONE);
        hideTopBottomView(false);
        cancelDismissControlViewTimer();
        //5秒后 隐藏view
        ThreadDispatcher.getDispatcher().removeOnMain(hideRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(hideRunnable, 5000);

    }


    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolumeProgress);
    }

    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    @Override
    protected void doubleClickController() {
//        if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
//            mNiceVideoPlayer.pause();
//        } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
//            mNiceVideoPlayer.restart();
//        }
    }

    @Override
    protected void oneClickController() {
//        if (mNiceVideoPlayer.isPlaying()
//                || mNiceVideoPlayer.isPaused()
//                || mNiceVideoPlayer.isBufferingPlaying()
//                || mNiceVideoPlayer.isBufferingPaused()) {
////            setTopBottomVisible(!topBottomVisible);
//        }
    }

    public void setNiceClickPreviosAndNextListener(INiceControllerVideoListener niceControllerVideoListener) {
        this.niceControllerVideoListener = niceControllerVideoListener;
    }
}
