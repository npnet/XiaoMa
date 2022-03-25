package com.xiaoma.ui.progress.loading;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;


import com.xiaoma.utils.ConvertUtils;

/**
 * Created by youthyJ
 * 2016/12/19 0019.
 */
public class ProgressDrawable extends Drawable implements Animatable {
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final int ANIMATION_DURATION = 1200;
    private static final int DEFAULT_HEADER_WIDTH = 150;
    private static final int DEFAULT_HEADER_HEIGHT = 50;

    private final View parent;
    private final XiaoMaDa xiaoMaDa;
    private final Drawable.Callback callback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(@NonNull Drawable d) {
            invalidateSelf();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable d, @NonNull Runnable what, long when) {
            scheduleSelf(what, when);
        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable d, @NonNull Runnable what) {
            unscheduleSelf(what);
        }
    };

    private Context context;
    private Animation mLoadingAnimation;
    private float animationPreTime = 0;
    private float restartTimes = 0;
    private float mWidth = DEFAULT_HEADER_WIDTH;
    private float mHeight = DEFAULT_HEADER_HEIGHT;

    public ProgressDrawable(Context context, View parent) {
        this.context = context;
        this.parent = parent;
        xiaoMaDa = new XiaoMaDaCycle(callback);
        initData();
        setupAnimation();
    }

    @Override
    public int getIntrinsicHeight() {
        return ConvertUtils.dp2px(context, Math.round(mHeight));
    }

    @Override
    public int getIntrinsicWidth() {
        return ConvertUtils.dp2px(context, Math.round(mWidth));
    }


    @Override
    public void start() {
        mLoadingAnimation.reset();
        parent.startAnimation(mLoadingAnimation);
    }

    @Override
    public void stop() {
        parent.clearAnimation();
        xiaoMaDa.reset();
        initData();
    }

    @Override
    public boolean isRunning() {
        return mLoadingAnimation.hasStarted()
                && !mLoadingAnimation.hasEnded();
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        xiaoMaDa.draw(canvas, bounds);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    public void setDotColor(int color) {
        if (xiaoMaDa != null) {
            xiaoMaDa.setDotColor(color);
        }
    }

    private void initData() {
        restartTimes = 0;
        animationPreTime = 0;
        xiaoMaDa.setAnimation(-1);
    }

    private void setupAnimation() {
        final Animation loadingAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation transformation) {
                xiaoMaDa.setPercent(1f);
                // 将循环变为线性
                if (interpolatedTime < animationPreTime) {
                    restartTimes++;
                }
                animationPreTime = interpolatedTime;
                interpolatedTime = interpolatedTime + restartTimes;

                // 设置插入时间
                xiaoMaDa.setAnimation(interpolatedTime);
            }
        };
        loadingAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        loadingAnimation.setDuration(ANIMATION_DURATION);
        loadingAnimation.setRepeatCount(Animation.INFINITE);
        loadingAnimation.setRepeatMode(Animation.RESTART);

        mLoadingAnimation = loadingAnimation;
    }


    private static abstract class XiaoMaDa {
        private static final int DEFAULT_DOT_COLOR = Color.WHITE;
        private final Drawable.Callback callback;

        protected Paint circlePaint = new Paint();
        protected float percent;
        protected int dotColor = DEFAULT_DOT_COLOR;

        public XiaoMaDa(Drawable.Callback callback) {
            this.callback = callback;
            initPaint();
        }

        public abstract void reset();

        public abstract void draw(Canvas canvas, Rect bounds);

        public abstract void setAnimation(float interpolatedTime);

        public void setPercent(float percent) {
            this.percent = percent;
            invalidateSelf();
        }

        public void setDotColor(int dotColor) {
            this.dotColor = dotColor;
        }

        protected void initPaint() {
            circlePaint.setAntiAlias(true);
            circlePaint.setDither(true);
            circlePaint.setColor(dotColor);
        }

        protected void invalidateSelf() {
            callback.invalidateDrawable(null);
        }
    }

    private static class XiaoMaDaCycle extends XiaoMaDa {
        private float mCircleTrim1 = 1f;
        private float mCircleTrim2 = 1f;
        private float mCircleTrim3 = 1f;
        private float mCircleTrim4 = 1f;
        private float mCircleTrim5 = 1f;
        private float mCircleTrim6 = 1f;
        private float mCircleTrim7 = 1f;
        private float mCircleTrim8 = 1f;

        private float rotate = 0;

        public XiaoMaDaCycle(Drawable.Callback callback) {
            super(callback);
        }

        @Override
        public void draw(Canvas canvas, Rect bounds) {
            float defaultRadius = bounds.height() * 0.35f;
            float bigRadius = bounds.exactCenterY() - defaultRadius;
            float centerX = bounds.exactCenterX();
            float centerY = bounds.exactCenterY();
            float sin45 = 0.707107f * bigRadius;

            float radius1 = defaultRadius * mCircleTrim1;
            float radius2 = defaultRadius * mCircleTrim2;
            float radius3 = defaultRadius * mCircleTrim3;
            float radius4 = defaultRadius * mCircleTrim4;
            float radius5 = defaultRadius * mCircleTrim5;
            float radius6 = defaultRadius * mCircleTrim6;
            float radius7 = defaultRadius * mCircleTrim7;
            float radius8 = defaultRadius * mCircleTrim8;

            float x1 = centerX;
            float x2 = centerX + sin45;
            float x3 = centerX + bigRadius;
            float x4 = centerX + sin45;
            float x5 = centerX;
            float x6 = centerX - sin45;
            float x7 = centerX - bigRadius;
            float x8 = centerX - sin45;

            float y1 = centerY - bigRadius;
            float y2 = centerY - sin45;
            float y3 = centerY;
            float y4 = centerY + sin45;
            float y5 = centerY + bigRadius;
            float y6 = centerY + sin45;
            float y7 = centerY;
            float y8 = centerY - sin45;

            canvas.rotate(rotate -= 2.5, centerX, centerY);
            canvas.drawCircle(x1, y1, radius1, circlePaint);
            canvas.drawCircle(x2, y2, radius2, circlePaint);
            canvas.drawCircle(x3, y3, radius3, circlePaint);
            canvas.drawCircle(x4, y4, radius4, circlePaint);
            canvas.drawCircle(x5, y5, radius5, circlePaint);
            canvas.drawCircle(x6, y6, radius6, circlePaint);
            canvas.drawCircle(x7, y7, radius7, circlePaint);
            canvas.drawCircle(x8, y8, radius8, circlePaint);

        }

        @Override
        public void setPercent(float percent) {
            this.percent = percent;
            setAnimation(this.percent);
        }

        @Override
        public void reset() {
            mCircleTrim1 = 1f;
            mCircleTrim2 = 1f;
            mCircleTrim3 = 1f;
            mCircleTrim4 = 1f;
            mCircleTrim5 = 1f;
            mCircleTrim6 = 1f;
            mCircleTrim7 = 1f;
            mCircleTrim8 = 1f;
        }

        @Override
        public void setAnimation(float interpolatedTime) {
            if (interpolatedTime < 0) {
                mCircleTrim1 = 1f;
                mCircleTrim2 = 1f;
                mCircleTrim3 = 1f;
                mCircleTrim4 = 1f;
                mCircleTrim5 = 1f;
                mCircleTrim6 = 1f;
                mCircleTrim7 = 1f;
                mCircleTrim8 = 1f;
                invalidateSelf();
                return;
            }

            // magic
            final float a = 2f;
            final float b = -a;
            final float c = 1f;

            float x;
            {
                x = interpolatedTime % (8f / 10f);
                mCircleTrim1 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 1f / 10f) {
                x = (interpolatedTime - 1f / 10f) % (8f / 10f);
                mCircleTrim2 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 2f / 10f) {
                x = (interpolatedTime - 2f / 10f) % (8f / 10f);
                mCircleTrim3 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 3f / 10f) {
                x = (interpolatedTime - 3f / 10f) % (8f / 10f);
                mCircleTrim4 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 4f / 10f) {
                x = (interpolatedTime - 4f / 10f) % (8f / 10f);
                mCircleTrim5 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 5f / 10f) {
                x = (interpolatedTime - 5f / 10f) % (8f / 10f);
                mCircleTrim6 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 6f / 10f) {
                x = (interpolatedTime - 6f / 10f) % (8f / 10f);
                mCircleTrim7 = a * x * x + b * x + c;
                invalidateSelf();
            }
            if (interpolatedTime > 7f / 10f) {
                x = (interpolatedTime - 7f / 10f) % (8f / 10f);
                mCircleTrim8 = a * x * x + b * x + c;
                invalidateSelf();
            }
        }

        @Override
        public void setDotColor(int dotColor) {
            super.setDotColor(dotColor);
            circlePaint.setColor(this.dotColor);
        }

    }

}
