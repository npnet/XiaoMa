package com.xiaoma.personal.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.personal.R;

/**
 * Created by kaka
 * on 19-1-18 下午4:18
 * <p>
 * desc: #a
 * </p>
 */
public class XmProgressBar extends View {

    private Drawable mBgDrawable;
    private int mBgColor;
    private int mMax;
    private int mProgress;
    private int mBarColor;
    private Drawable mBarDrawable;
    private int mBarMargin;
    private int mWidth;
    private int mHeight;
    private Rect mBackgroundRect;
    private Rect mBarRect;
    private Path mBackgroundPath;
    private Paint mBackgroundPaint;
    private Paint mBarPaint;

    public XmProgressBar(Context context) {
        this(context, null);
    }

    public XmProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XmProgressBar);
        mBgDrawable = typedArray.getDrawable(R.styleable.XmProgressBar_background_drawable);
        mBgColor = typedArray.getInteger(R.styleable.XmProgressBar_background_color, Color.GRAY);
        mMax = typedArray.getInteger(R.styleable.XmProgressBar_max, 100);
        mProgress = typedArray.getInteger(R.styleable.XmProgressBar_progress, 30);
        mProgress = typedArray.getInteger(R.styleable.XmProgressBar_progress, 30);
        mBarColor = typedArray.getInteger(R.styleable.XmProgressBar_bar_color, Color.YELLOW);
        mBarDrawable = typedArray.getDrawable(R.styleable.XmProgressBar_bar_drawable);
        mBarMargin = typedArray.getDimensionPixelOffset(R.styleable.XmProgressBar_bar_margin, 4);
        typedArray.recycle();
        prepareDraw();
    }

    private void prepareDraw() {
        //background
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBgColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPath = new Path();
        if (mBgDrawable == null) {
            mBgDrawable = getResources().getDrawable(R.drawable.shape_semicircle_round, null);
        }

        //bar
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStyle(Paint.Style.FILL);
    }

    public void setProgress(int progress, int max) {
        mMax = max;
        mProgress = progress;
        postInvalidate();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //background
        mBgDrawable.setBounds(mBackgroundRect);
        mBgDrawable.draw(canvas);

        //foreground
        int radius = (mHeight - 2 * mBarMargin) / 2;
        int right = Math.round((mProgress * 1f / mMax) * (mWidth - 2 * mBarMargin)) + mBarMargin;
        if (mBarDrawable != null) {
            mBarRect.right = right;
            mBgDrawable.setBounds(mBarRect);
            mBgDrawable.draw(canvas);
        } else {
            canvas.drawRoundRect(mBarMargin, mBarMargin, right, mHeight - mBarMargin, radius, radius, mBarPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mBackgroundRect == null) {
            mBackgroundRect = new Rect(0, 0, mWidth, mHeight);
        }

        if (mBarRect == null) {
            mBarRect = new Rect(mBarMargin, mBarMargin, mWidth - mBarMargin, mHeight - mBarMargin);
        }
    }
}
