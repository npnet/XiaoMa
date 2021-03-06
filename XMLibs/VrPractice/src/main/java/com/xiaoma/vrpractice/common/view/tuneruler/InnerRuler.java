package com.xiaoma.vrpractice.common.view.tuneruler;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import com.xiaoma.model.pratice.VrPracticeConstants;


/**
 * 内部尺子抽象类
 */

public abstract class InnerRuler extends View {
    protected Context mContext;
    protected TuneRuler mParent;

    protected Paint mSmallScalePaint, mMiddleScalePaint, mBigScalePaint, mOutLinePaint;
    protected TextPaint mTextPaint;
    //当前刻度值
    protected float mCurrentScale = 0;
    //最大刻度数
    protected int mMaxLength = 0;
    //长度、最小可滑动值、最大可滑动值
    protected int mLength, mMinPosition = 0, mMaxPosition = 0;
    //控制滑动
    protected OverScroller mOverScroller;
    //一格大刻度多少格小刻度
    protected int mCount = 10;
    //一格中刻度多少格小刻度
    protected int mMiddleCount = 5;
    //提前刻画量
    protected int mDrawOffset;
    //速度获取
    protected VelocityTracker mVelocityTracker;
    //惯性最大最小速度
    protected int mMaximumVelocity, mMinimumVelocity;
    //回调接口
    protected RulerCallback mRulerCallback;
    //边界效果
    protected EdgeEffect mStartEdgeEffect, mEndEdgeEffect;
    //边缘效应长度
    protected int mEdgeLength;

    public InnerRuler(Context context, TuneRuler tuneRuler) {
        super(context);
        mParent = tuneRuler;
        init(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mParent.getTextMarginHead() + mParent.getReflectTextMargin() + mParent.getTextSize(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void init(Context context) {
        mContext = context;

        mMaxLength = mParent.getMaxScale() - mParent.getMinScale();
        mCurrentScale = mParent.getMinScale();
        mCount = mParent.getCount();
        mDrawOffset = mCount * mParent.getInterval() / 2;

        initPaints();

        mOverScroller = new OverScroller(mContext);

//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //配置速度
        mVelocityTracker = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        initEdgeEffects();

        //第一次进入，跳转到设定刻度
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                goToScale(mCurrentScale);
            }
        });
    }

    //初始化画笔
    private void initPaints() {
        mSmallScalePaint = new Paint();
        mSmallScalePaint.setStrokeWidth(mParent.getSmallScaleWidth());
        mSmallScalePaint.setColor(mParent.getScaleColor());
        mSmallScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mMiddleScalePaint = new Paint();
        mMiddleScalePaint.setStrokeWidth(mParent.getMiddleScaleWidth());
        mMiddleScalePaint.setColor(mParent.getScaleColor());
        mMiddleScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mBigScalePaint = new Paint();
        mBigScalePaint.setColor(mParent.getScaleColor());
        mBigScalePaint.setStrokeWidth(mParent.getBigScaleWidth());
        mBigScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mParent.getTextColor());
        mTextPaint.setTextSize(mParent.getTextSize());
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(0);
        mOutLinePaint.setColor(mParent.getScaleColor());
    }

    //初始化边缘效果
    public void initEdgeEffects() {
        if (mParent.canEdgeEffect()) {
            if (mStartEdgeEffect == null || mEndEdgeEffect == null) {
                mStartEdgeEffect = new EdgeEffect(mContext);
                mEndEdgeEffect = new EdgeEffect(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mStartEdgeEffect.setColor(mParent.getEdgeColor());
                    mEndEdgeEffect.setColor(mParent.getEdgeColor());
                }
                mEdgeLength = mParent.getCursorHeight() + mParent.getInterval() * mParent.getCount();
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            //这是最后OverScroller的最后一次滑动，如果这次滑动完了mCurrentScale不是整数，则把尺子移动到最近的整数位置
            if (!mOverScroller.computeScrollOffset()
                    && mCurrentScale != Math.round(mCurrentScale)) {
//                Log.d("ruler", "computeScroll: " + mCurrentScale);
                if (scaleToScrollX(Math.round(mCurrentScale)) == getScrollX()) {
                    mOverScroller.abortAnimation();
                } else {
                    //Fling完进行一次检测回滚
                    scrollBackToCurrentScale();
                }
            }
            postInvalidate();
        }
    }

    protected abstract void scrollBackToCurrentScale();

    protected abstract void goToScale(float scale);

    public abstract void refreshSize();

    //设置尺子当前刻度
    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = currentScale;
        goToScale(mCurrentScale);
    }

    public void setRulerCallback(RulerCallback RulerCallback) {
        this.mRulerCallback = RulerCallback;
    }

    public float getCurrentScale() {
        if (mParent.getMode() == VrPracticeConstants.FMAM.TYPE_FM) {
            return Math.round(mCurrentScale) * 1000 * mParent.getFactor();
        } else {
            return mCurrentScale * mParent.getFactor();
        }
    }

    public void setMaxLength(int mMaxLength) {
        this.mMaxLength = mMaxLength;
    }

    //把Scale转化为ScrollX
    private int scaleToScrollX(float scale) {
//        Log.d(TAG, "scaleToScrollX: ");
        return (int) ((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPosition);
    }
}
