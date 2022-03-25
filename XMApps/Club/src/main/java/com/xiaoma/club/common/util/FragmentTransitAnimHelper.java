package com.xiaoma.club.common.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by LKF on 2018-12-27 0027.
 * Fragment转场动画辅助类,避免Fragment开启/关闭时,直接跳过动画
 */
public class FragmentTransitAnimHelper {
    /**
     * 重写{@link android.support.v4.app.Fragment#onCreateAnimation(int, boolean, int)}
     *
     * @param duration 入场/退场动画的时长
     */
    public static Animation onCreateAnimation(long duration) {
        Animation anim = null;
        if (duration > 0) {
            anim = new TranslateAnimation(0, 0, 0, 0);
            anim.setDuration(duration);
        }
        return anim;
    }

    /**
     * 重写{@link android.support.v4.app.Fragment#onCreateAnimator(int, boolean, int)}
     *
     * @param duration 入场/退场动画的时长
     */
    public static Animator onCreateAnimator(long duration) {
        Animator anim = null;
        if (duration > 0) {
            anim = ValueAnimator.ofInt(0, 0);
            anim.setDuration(duration);
        }
        return anim;
    }
}
