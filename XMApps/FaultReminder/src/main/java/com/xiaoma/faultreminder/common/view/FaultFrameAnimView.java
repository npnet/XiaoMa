package com.xiaoma.faultreminder.common.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xiaoma.faultreminder.R;
import com.xiaoma.faultreminder.sdk.model.CarFault;

/**
 * @author KY
 * @date 12/27/2018
 */
public class FaultFrameAnimView extends ConstraintLayout {
    private static final float START_ALPHA = 0.7f;
    private static final int DURATION = 500;

    private AnimationDrawable mSourceDrawable;
    private AnimationDrawable mTargetDrawable;
    private View mAnimView;
    private ImageView mKeyFrame;
    private KeyFrameTask mKeyFrameTask;
    private ObjectAnimator mKeyFrameInAnim;
    private ObjectAnimator mKeyFrameOutAnim;
    private int mLastEndIndex;
    private NextFaultCallback mNextFaultCallback;
    private int mStayDuration;
    private int mFlickerDuration;

    public FaultFrameAnimView(Context context) {
        this(context, null);
    }

    public FaultFrameAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaultFrameAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView();
        initAnim();
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FaultFrameAnimView);
        mSourceDrawable = (AnimationDrawable) typedArray.getDrawable(R.styleable.FaultFrameAnimView_source_drawable);
        mStayDuration = typedArray.getInt(R.styleable.FaultFrameAnimView_stay_duration, DURATION * 2 * 10);
        mFlickerDuration = typedArray.getInt(R.styleable.FaultFrameAnimView_flicker_duration, DURATION * 2 );
        typedArray.recycle();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_fault_frame_anim, this, true);
        mAnimView = findViewById(R.id.anim_view);
        mKeyFrame = findViewById(R.id.key_frame);

        mTargetDrawable = new AnimationDrawable();
    }

    private void initAnim() {
        mKeyFrameInAnim = ObjectAnimator.ofFloat(mKeyFrame, "alpha", START_ALPHA, 1f, START_ALPHA);
        mKeyFrameInAnim.setRepeatCount(mStayDuration / mFlickerDuration);
        mKeyFrameInAnim.setRepeatMode(ValueAnimator.RESTART);
        mKeyFrameInAnim.setDuration(mFlickerDuration);
        mKeyFrameInAnim.setStartDelay(300);
        mKeyFrameInAnim.addListener(new AnimListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mKeyFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mNextFaultCallback != null && ((ObjectAnimator) animation).getAnimatedFraction() == 1f) {
                    mNextFaultCallback.onNextFault();
                }
            }
        });

        mKeyFrameOutAnim = ObjectAnimator.ofFloat(mKeyFrame, "alpha", START_ALPHA, 0f);
        mKeyFrameOutAnim.addListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mKeyFrame.setVisibility(View.GONE);
                startFrameAnim();
            }
        });
    }

    public void setNextFaultCallback(NextFaultCallback mNextFaultCallback) {
        this.mNextFaultCallback = mNextFaultCallback;
    }

    public void startAnim(CarFault carFault) {
        startRealAnim(prepareAnim(carFault));
    }

    private boolean prepareAnim(CarFault carFault) {
        // 取消之前的动画,定时任务以及回调
        removeCallbacks(mKeyFrameTask);
        mKeyFrameInAnim.cancel();
        mKeyFrameOutAnim.cancel();

        boolean isInterrupt;
        int startIndex;
        int endIndex = carFault.getEndFrameIndex();
        int keyFrame = carFault.getEndFrameResId();
        if (mTargetDrawable.getCurrentIndex() != mTargetDrawable.getNumberOfFrames() - 1) {
            mTargetDrawable.stop();
            startIndex = (mLastEndIndex + mTargetDrawable.getCurrentIndex()) % mSourceDrawable.getNumberOfFrames();
            isInterrupt = true;
        } else {
            startIndex = mLastEndIndex;
            isInterrupt = false;
        }

        if (startIndex == -1) {
            startIndex = 0;
        }

        if (mTargetDrawable != null) {
            mTargetDrawable.stop();
            mTargetDrawable = null;
        }
        mTargetDrawable = new AnimationDrawable();
        mTargetDrawable.setOneShot(true);
        if (startIndex < endIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                mTargetDrawable.addFrame(mSourceDrawable.getFrame(i), mSourceDrawable.getDuration(i));
            }
        } else if (startIndex > endIndex) {
            for (int i = startIndex; i < mSourceDrawable.getNumberOfFrames(); i++) {
                mTargetDrawable.addFrame(mSourceDrawable.getFrame(i), mSourceDrawable.getDuration(i));
            }

            for (int i = 0; i <= endIndex; i++) {
                mTargetDrawable.addFrame(mSourceDrawable.getFrame(i), mSourceDrawable.getDuration(i));
            }
        } else {
            mTargetDrawable.addFrame(mSourceDrawable.getFrame(startIndex), mSourceDrawable.getDuration(startIndex));
        }

        mAnimView.setBackground(mTargetDrawable);

        if (mKeyFrameTask == null) {
            mKeyFrameTask = new KeyFrameTask(keyFrame, endIndex);
        } else {
            mKeyFrameTask.setKeyFrame(keyFrame, endIndex);
        }

        return isInterrupt;
    }

    private void startRealAnim(boolean isInterrupt) {
        if (isInterrupt) {
            startFrameAnim();
        } else {
            mKeyFrameOutAnim.start();
        }
    }

    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    public long getTotalDuration() {

        long iDuration = 0;

        for (int i = 0; i < mTargetDrawable.getNumberOfFrames(); i++) {
            iDuration += mTargetDrawable.getDuration(i);
        }

        return iDuration;
    }

    private void startFrameAnim() {
        mKeyFrame.setVisibility(GONE);
        mTargetDrawable.start();
        postDelayed(mKeyFrameTask, getTotalDuration());
    }

    public void release() {
        mNextFaultCallback = null;
        removeCallbacks(mKeyFrameTask);
        mKeyFrameInAnim.cancel();
        mKeyFrameOutAnim.cancel();
    }

    class KeyFrameTask implements Runnable {


        KeyFrameTask(@DrawableRes int keyFrame, int keyFrameIndex) {
            this.keyFrame = keyFrame;
            this.keyFrameIndex = keyFrameIndex;
        }

        void setKeyFrame(@DrawableRes int keyFrame, int keyFrameIndex) {
            this.keyFrame = keyFrame;
            this.keyFrameIndex = keyFrameIndex;
        }

        @DrawableRes
        private int keyFrame;
        private int keyFrameIndex;

        @Override
        public void run() {
            mLastEndIndex = keyFrameIndex;
            mKeyFrame.setImageDrawable(getContext().getDrawable(keyFrame));
            mKeyFrameInAnim.start();
        }
    }

    private class AnimListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public interface NextFaultCallback {
        void onNextFault();
    }
}
