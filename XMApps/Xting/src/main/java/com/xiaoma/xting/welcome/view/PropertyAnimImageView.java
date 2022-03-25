package com.xiaoma.xting.welcome.view;


import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.xiaoma.skin.views.XmSkinImageView;
import com.xiaoma.xting.R;



/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/13
 */
public class PropertyAnimImageView extends XmSkinImageView {

    private int imageLevel = 0;
    private int mMinLevel, mMaxLevel;
    public static final String TAG = "PropertyAnimView";
    private boolean mRotateAnim, mLevelAnim;
    private ObjectAnimator mAnimator;

    public PropertyAnimImageView(Context context) {
        this(context, null);
    }

    public PropertyAnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PropertyAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public PropertyAnimImageView setImageLevelBound(@DrawableRes int animRes, int minLevel, int maxLevel) {
        this.mMinLevel = minLevel;
        this.mMaxLevel = maxLevel;
        setImageResource(animRes);
        return this;
    }

    public void setImageLevel(int level) {
        super.setImageLevel(level);
        this.imageLevel = level;
    }

    public void setImageLevel(@DrawableRes int animRes, int level) {
        setImageResource(animRes);
        super.setImageLevel(level);
    }

    public int getImageLevel() {
        return imageLevel;
    }

    public void startLevelAnim(int duration, AnimatorListenerAdapter listenerAdapter) {
        stopRotateAnim();
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofInt(this, "imageLevel", mMinLevel, mMaxLevel);
            mAnimator.setDuration(duration);
            mAnimator.start();

            mAnimator.addListener(listenerAdapter);
        } else {
            if (mAnimator.isStarted()) {
                if (mAnimator.isPaused()) {
                    mAnimator.start();
                }
            }
        }

    }

    public void startRepeatLevelAnim(int duration, AnimatorListenerAdapter listenerAdapter) {
        stopRotateAnim();
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofInt(this, "imageLevel", mMinLevel, mMaxLevel);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mAnimator.setDuration(duration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.start();

            mAnimator.addListener(listenerAdapter);
        } else {
            if (!mAnimator.isRunning()) {
                mAnimator.start();
            }
        }
    }

    public void pauseLevelAnim() {
        stopLevelAnim();
        stopRotateAnim();
    }

    public void stopLevelAnim() {
        if (mAnimator != null) {
            if (mAnimator.isStarted()) {
                mAnimator.cancel();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pauseLevelAnim();
    }

    private Animation mRotateAnimation;

    private Animation getRotateAnimation() {
        if (mRotateAnimation == null) {
            mRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            mRotateAnimation.setRepeatCount(Animation.INFINITE);
            mRotateAnimation.setRepeatMode(Animation.RESTART);
        }
        return mRotateAnimation;
    }

    public void startRotate() {
        stopLevelAnim();
        if (getRotateAnimation().hasEnded()) {
            getRotateAnimation().reset();
        }
        startAnimation(getRotateAnimation());
    }

    private void stopRotateAnim() {
        if (mRotateAnimation != null) {
            mRotateAnimation.reset();
            mRotateAnimation.cancel();
        }
    }

    public void notifyStatus(int status) {

    }

}
