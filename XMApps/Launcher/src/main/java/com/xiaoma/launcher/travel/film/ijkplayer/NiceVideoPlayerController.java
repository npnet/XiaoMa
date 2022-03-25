package com.xiaoma.launcher.travel.film.ijkplayer;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public abstract class NiceVideoPlayerController extends FrameLayout implements View.OnTouchListener {

    private Context mContext;
    protected INiceVideoPlayer mNiceVideoPlayer;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;
    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;
    private GestureDetector mGesture;

    public NiceVideoPlayerController(final Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
        mGesture = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                doubleClickController();
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                oneClickController();
                return false;
            }
        });
    }

    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        mNiceVideoPlayer = niceVideoPlayer;
    }

    public abstract void setTitle(String title);

    public abstract void setSize(String size);

    //视频底图
    public abstract void setImage(@DrawableRes int resId);

    public abstract ImageView imageView();

    public abstract void setLength(long length);

    protected abstract void onPlayStateChanged(int playState);

    protected abstract void reset();

    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    NiceVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    protected abstract void updateProgress();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mNiceVideoPlayer.isIdle()
                || mNiceVideoPlayer.isError()
                || mNiceVideoPlayer.isPreparing()
                || mNiceVideoPlayer.isPrepared()
                || mNiceVideoPlayer.isCompleted()) {
            hideChangePosition();
            hideChangeBrightness();
            hideChangeVolume();
            return false;
        }
        mGesture.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
                    if (absDeltaX >= THRESHOLD) {
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = mNiceVideoPlayer.getCurrentPosition();
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX < getWidth() * 0.5f) {
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = NiceUtil.scanForActivity(mContext)
                                    .getWindow().getAttributes().screenBrightness;
                        } else {
                            // 右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mNiceVideoPlayer.getVolume();
                        }
                    }
                }
                if (mNeedChangePosition) {
                    long duration = mNiceVideoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }
//                if (mNeedChangeBrightness) {
//                    deltaY = -deltaY;
//                    float deltaBrightness = deltaY * 3 / getHeight();
//                    float newBrightness = mGestureDownBrightness + deltaBrightness;
//                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
//                    float newBrightnessPercentage = newBrightness;
//                    WindowManager.LayoutParams params = NiceUtil.scanForActivity(mContext)
//                            .getWindow().getAttributes();
//                    params.screenBrightness = newBrightnessPercentage;
//                    NiceUtil.scanForActivity(mContext).getWindow().setAttributes(params);
//                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
//                    showChangeBrightness(newBrightnessProgress);
//                }
//                if (mNeedChangeVolume) {
//                    deltaY = -deltaY;
//                    int maxVolume = mNiceVideoPlayer.getMaxVolume();
//                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
//                    int newVolume = mGestureDownVolume + deltaVolume;
//                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
//                    mNiceVideoPlayer.setVolume(newVolume);
//                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
//                    showChangeVolume(newVolumeProgress);
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    mNiceVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
//                if (mNeedChangeBrightness) {
//                    hideChangeBrightness();
//                    return true;
//                }
//                if (mNeedChangeVolume) {
//                    hideChangeVolume();
//                    return true;
//                }
                break;
        }
        return false;
    }

    //手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
    //在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
    //duration            视频总时长ms
    //newPositionProgress 新的位置进度，取值0到100。
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    //手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
    //在手势ACTION_UP或ACTION_CANCEL时调用。
    protected abstract void hideChangePosition();

    //手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
    //在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
    //newVolumeProgress 新的音量进度，取值1到100。
    protected abstract void showChangeVolume(int newVolumeProgress);

    //手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
    //在手势ACTION_UP或ACTION_CANCEL时调用。
    protected abstract void hideChangeVolume();

    //手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
    //在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
    //newBrightnessProgress 新的亮度进度，取值1到100。
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    //手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
    //在手势ACTION_UP或ACTION_CANCEL时调用。
    protected abstract void hideChangeBrightness();

    //手势判断 双击暂停/双击播放
    protected abstract void doubleClickController();

    //手势判断 单击
    protected abstract void oneClickController();
}
