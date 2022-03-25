package com.xiaoma.launcher.travel.film.ijkplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.progress.loading.ProgressDrawable;
import com.xiaoma.utils.ViewUtils;


public class TxVideoPlayerController extends NiceVideoPlayerController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private ImageView mImage;
    private View mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mChangePositionCurrent;
    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;
    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;
    private LinearLayout mError;
    private TextView mRetry;
    private LinearLayout mCompleted;
    private TextView mReplay;
    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;
    private static final float SPEED_X1 = 1.0f;
    private static final float SPEED_X2 = 2.0f;
    private static final float SPEED_X4 = 4.0f;
    private static final float SPEED_X25 = 0.25f;
    private static final float SPEED_X50 = 0.5f;
    private float mCurrentSpeed = SPEED_X1;
    private INiceControllerVideoListener niceControllerVideoListener;
    private LinearLayout mPb;
    private ProgressBar mPbar;
    private TextView mTvSize;


    public TxVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.tx_video_palyer_controller, this, true);
        mImage = findViewById(R.id.image);
        mTop = findViewById(R.id.top);
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mTvSize = findViewById(R.id.tv_size);
        mBottom = findViewById(R.id.bottom);
        mRestartPause = findViewById(R.id.restart_or_pause);
        mPosition = findViewById(R.id.position);
        mDuration = findViewById(R.id.duration);
        mSeek = findViewById(R.id.seek);
        mChangePositionCurrent = findViewById(R.id.change_position_current);
        mChangeBrightness = findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = findViewById(R.id.change_brightness_progress);
        mChangeVolume = findViewById(R.id.change_volume);
        mChangeVolumeProgress = findViewById(R.id.change_volume_progress);
        mError = findViewById(R.id.error);
        mCompleted = findViewById(R.id.completed);
        mReplay = findViewById(R.id.replay);
        mPb = findViewById(R.id.progress);
        mPbar = findViewById(R.id.progress_bar);
        mPbar.setIndeterminateDrawable(new ProgressDrawable(mContext, mPbar));
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
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
//        mLength.setText(NiceUtil.formatTime(length));
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                mImage.setVisibility(View.GONE);
                mPb.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case NiceVideoPlayer.STATE_PLAYING:
                mNiceVideoPlayer.requestAudioFocus();
                mPb.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_PAUSED:
                mPb.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                mPb.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                mPb.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_ERROR:
                mNiceVideoPlayer.abandonAudioFocus();
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mPb.setVisibility(View.GONE);
                showTrailerDialog();
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                mNiceVideoPlayer.abandonAudioFocus();
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                break;
            default:

                break;
        }
    }
    /**
     * 网络判断
     */
    private void showTrailerDialog() {
        ConfirmDialog dialog = new ConfirmDialog((FragmentActivity) mContext);
        dialog.setContent(mContext.getString(R.string.not_connect_network))
                .setPositiveButton(mContext.getString(R.string.retry),new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.FILM_TRAILER_RETRY})
                    public void onClick(View v) {
                        mNiceVideoPlayer.restart();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.dialog_cancel),new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.FILM_TRAILER_CANCEL})
                    public void onClick(View v) {
                        mTop.setVisibility(View.VISIBLE);
                        mError.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(mContext, R.layout.dialog_film_trailer, null);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        final XmDialog builder = new XmDialog.Builder((FragmentActivity) mContext)
//                .setView(view)
//                .setWidth(700)
//                .setHeight(400)
//                .setCancelableOutside(false)
//                .create();
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.FILM_TRAILER_RETRY})
//            public void onClick(View v) {
//                mNiceVideoPlayer.restart();
//                builder.dismiss();
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.FILM_TRAILER_CANCEL})
//            public void onClick(View v) {
//                mTop.setVisibility(View.VISIBLE);
//                mError.setVisibility(View.VISIBLE);
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);
        mImage.setVisibility(View.VISIBLE);
        mBottom.setVisibility(View.GONE);
        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mPb.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.BACK, EventConstants.NormalClick.RESTARTORPAUSE, EventConstants.NormalClick.REPLAY})
    @ResId({R.id.back, R.id.restart_or_pause, R.id.replay})
    public void onClick(View v) {
        if (v == mBack) {
            //finish activity
            if (mNiceVideoPlayer.isPlaying()) {
                mNiceVideoPlayer.pause();
            }
            if (niceControllerVideoListener != null) {
                niceControllerVideoListener.onClickClose();
            }

        } else if (v == mRestartPause) {
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
        }  else if (v == mReplay) {
            mNiceVideoPlayer.restart();
        }
    }


    //设置top、bottom的显示和隐藏
    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    //开启top、bottom自动消失的timer
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    //取消top、bottom自动消失的timer
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mNiceVideoPlayer.isBufferingPaused() || mNiceVideoPlayer.isPaused()) {
            mNiceVideoPlayer.restart();
        }
        long position = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
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
        mChangePositionCurrent.setVisibility(View.VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositionCurrent.setVisibility(View.GONE);
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
        if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
            mNiceVideoPlayer.pause();
        } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
            mNiceVideoPlayer.restart();
        }
    }

    @Override
    protected void oneClickController() {
        if (mNiceVideoPlayer.isPlaying()
                || mNiceVideoPlayer.isPaused()
                || mNiceVideoPlayer.isBufferingPlaying()
                || mNiceVideoPlayer.isBufferingPaused()) {
            setTopBottomVisible(!topBottomVisible);
        }
    }

    public void setNiceClickPreviosAndNextListener(INiceControllerVideoListener niceControllerVideoListener) {
        this.niceControllerVideoListener = niceControllerVideoListener;
    }


    public void listenerBackPush() {
        mNiceVideoPlayer.pause();
    }

}
