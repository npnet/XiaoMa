package com.xiaoma.xting.local.view.tuneruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.views.XmSkinViewGroup;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingConstants;

/**
 * 用于包着尺子的外壳，用于画选取光标、外壳
 */

public class TuneRuler extends XmSkinViewGroup {
    private final String TAG = "ruler";
    public static final float FM_SCALE_FACTOR = 0.1f;
    public static float AM_SCALE_FACTOR = 9f;// 参见{@link XtingConstants#FMAM#AM_ASIA_STEP}
    public int FM_START = Math.round(XtingConstants.FMAM.getFMStart() / 1000f / FM_SCALE_FACTOR);
    ;
    public int FM_END = Math.round(XtingConstants.FMAM.getFMEnd() / 1000f / FM_SCALE_FACTOR);
    ;
    public int AM_START = Math.round(XtingConstants.FMAM.getAMStart() / AM_SCALE_FACTOR);
    ;
    public int AM_END = Math.round(XtingConstants.FMAM.getAMEnd() / AM_SCALE_FACTOR);
    ;

    @IntDef({XtingConstants.FMAM.TYPE_FM, XtingConstants.FMAM.TYPE_AM})
    @interface Mode {
    }

    private @Mode
    int mCurrentMode = XtingConstants.FMAM.TYPE_FM;
    private Context mContext;
    //内部的尺子
    private TopHeadRuler mInnerRuler;
    //最小最大刻度值(以0.1为单位)
    private int mMinScale = FM_START, mMaxScale = FM_END;
    //倒三角、光标宽度、高度
    private int mTriangleWidth = 18, mTriangleHeight = 16, mCursorWidth = 4, mCursorHeight = 80;
    // 倒三角到光标的距离
    private int mTriangleScaleInterval = 40;
    // 倒三角的path
    private Path mTrianglePath;
    // 倒三角的paint
    private Paint mTrianglePaint;
    //大中小刻度的长度
    private int mSmallScaleLength = 20, mMiddleScaleLength = 40, mBigScaleLength = 80;
    //大中小刻度的粗细
    private int mSmallScaleWidth = 2, mMiddleScaleWidth = 2, mBigScaleWidth = 2;
    //数字字体大小
    private int mTextSize = 28;
    //数字Text距离顶部高度
    private int mTextMarginHead = 120;
    //倒影数字Text距离原Text的距离
    private int mReflectTextMargin = 18;
    //刻度间隔
    private int mInterval = 15;
    //数字Text颜色
    private
    @ColorInt
    int mTextColor = Color.WHITE;
    //刻度颜色
    private
    @ColorInt
    int mScaleColor = Color.WHITE;
    //一格大刻度多少格小刻度
    private int mCount = 10;
    //光标drawable
    private Drawable mCursorDrawable;
    private Drawable mLeftEdgeDrawable;
    private Drawable mRightEdgeDrawable;
    //两端的黄色渐变边框宽度
    private int mEdgeWidth = 4;
    private int mEdgeHeight = 140;
    //尺子两端的padding
    private int mPaddingStartAndEnd = 8;
    private int mPaddingLeft = 0, mPaddingTop = mTriangleHeight + mTriangleScaleInterval, mPaddingRight = 0, mPaddingBottom = 0;
    //尺子背景
    private Drawable mRulerBackGround;
    private int mRulerBackGroundColor = Color.TRANSPARENT;
    //是否启用边缘效应
    private boolean mCanEdgeEffect = true;
    //边缘颜色
    private @ColorInt
//    int mEdgeColor = getResources().getColor(R.color.color_tune_mark);
            int mEdgeColor = XmSkinManager.getInstance().getColor(R.color.color_tune_mark);
    //刻度乘积因子
    private float mFactor = 0.1f;
    // 上一次的刻度，防止相同的刻度被多次回调
    private float mLastScale;


    public TuneRuler(Context context) {
        this(context, null);
    }

    public TuneRuler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TuneRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFMArea();
        initRuler(context);
        initAttrs(context, attrs);
    }

    private void initFMArea() {
        AM_SCALE_FACTOR = XtingConstants.FMAM.getAMStep();
        FM_START = Math.round(XtingConstants.FMAM.getFMStart() / 1000f / FM_SCALE_FACTOR);
        FM_END = Math.round(XtingConstants.FMAM.getFMEnd() / 1000f / FM_SCALE_FACTOR);
        AM_START = Math.round(XtingConstants.FMAM.getAMStart() / AM_SCALE_FACTOR);
        AM_END = Math.round(XtingConstants.FMAM.getAMEnd() / AM_SCALE_FACTOR);
    }

    @SuppressWarnings("WrongConstant")
    private void initAttrs(Context context, AttributeSet attrs) {
//        mCursorDrawable = getResources().getDrawable(R.drawable.cursor_shape);
//        mLeftEdgeDrawable = getResources().getDrawable(R.drawable.shape_tune_ruler_edge);
//        mRightEdgeDrawable = getResources().getDrawable(R.drawable.shape_tune_ruler_edge);
        mCursorDrawable = XmSkinManager.getInstance().getDrawable(R.drawable.cursor_shape);
        mLeftEdgeDrawable = XmSkinManager.getInstance().getDrawable(R.drawable.shape_tune_ruler_edge);
        mRightEdgeDrawable = XmSkinManager.getInstance().getDrawable(R.drawable.shape_tune_ruler_edge);
        mTrianglePath = new Path();
        mTrianglePaint = new Paint();
        mTrianglePaint.setStyle(Paint.Style.FILL);
//        mTrianglePaint.setColor(mContext.getResources().getColor(R.color.color_tune_mark));
        mTrianglePaint.setColor(XmSkinManager.getInstance().getColor(R.color.color_tune_mark));
        mTrianglePaint.setAntiAlias(true);
    }

    private void initRuler(Context context) {
        mContext = context;
        mInnerRuler = new TopHeadRuler(context, this);
        paddingHorizontal();

        //设置全屏，加入InnerRuler
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        //设置ViewGroup可画
        setWillNotDraw(false);

        initDrawable();
        initRulerBackground();
        //默认展示FM刻度，防止刻度值初始为0的问题
        setMode(XtingConstants.FMAM.TYPE_FM, false);
    }

    private void initRulerBackground() {
        if (mRulerBackGround != null) {
            mInnerRuler.setBackground(mRulerBackGround);
        } else {
            mInnerRuler.setBackgroundColor(mRulerBackGroundColor);
        }
    }

    //在宽高初始化之后定义光标Drawable的边界
    private void initDrawable() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                mCursorDrawable.setBounds((getWidth() - mCursorWidth) / 2, mTriangleHeight + mTriangleScaleInterval
                        , (getWidth() + mCursorWidth) / 2, mCursorHeight + mTriangleHeight + mTriangleScaleInterval);

                mLeftEdgeDrawable.setBounds(0, (getHeight() - mEdgeHeight) / 2, mEdgeWidth, (getHeight() + mEdgeHeight) / 2);
                mRightEdgeDrawable.setBounds(getWidth() - mEdgeWidth, (getHeight() - mEdgeHeight) / 2, getWidth(), (getHeight() + mEdgeHeight) / 2);

                mTrianglePath.moveTo((getWidth() - mTriangleWidth) / 2, 0);
                mTrianglePath.lineTo((getWidth() + mTriangleWidth) / 2, 0);
                mTrianglePath.lineTo(getWidth() / 2, mTriangleHeight);
                mTrianglePath.close();
                return false;
            }
        });

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getTextMarginHead() + getReflectTextMargin() + getTextSize() + mTriangleHeight + mTriangleScaleInterval, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //画中间的选定光标，要在这里画，因为dispatchDraw()执行在onDraw()后面，这样子光标才能不被尺子的刻度遮蔽
        mCursorDrawable.draw(canvas);
        mLeftEdgeDrawable.draw(canvas);
        mRightEdgeDrawable.draw(canvas);
        canvas.drawPath(mTrianglePath, mTrianglePaint);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            mInnerRuler.layout(mPaddingLeft, mPaddingTop + mTriangleHeight + mTriangleScaleInterval, r - l - mPaddingRight, b - t - mPaddingBottom);
        }
    }

    private void paddingHorizontal() {
        mPaddingLeft = mPaddingStartAndEnd;
        mPaddingRight = mPaddingStartAndEnd;
        mPaddingTop = 0;
        mPaddingBottom = 0;
    }

    private void paddingVertical() {
        mPaddingTop = mPaddingStartAndEnd;
        mPaddingBottom = mPaddingStartAndEnd;
        mPaddingLeft = 0;
        mPaddingRight = 0;
    }

    //设置回调
    public void setCallback(RulerCallback rulerCallback) {
        mInnerRuler.setRulerCallback(rulerCallback);
    }

    public void setMode(@Mode int mode, boolean need) {
        this.mCurrentMode = mode;
        mMinScale = mode == XtingConstants.FMAM.TYPE_FM ? FM_START : AM_START;
        mMaxScale = mode == XtingConstants.FMAM.TYPE_FM ? FM_END : AM_END;
        mFactor = mode == XtingConstants.FMAM.TYPE_FM ? FM_SCALE_FACTOR : AM_SCALE_FACTOR;
        refreshRuler();
        setCurrentScale(mMinScale * mFactor);
        mInnerRuler.invalidate();
    }

    public int getMode() {
        return this.mCurrentMode;
    }

    public int getmCurrentMode() {
        return mCurrentMode;
    }

    //设置当前进度
    public void setCurrentScale(float scale) {
        float realScale = scale / mFactor;
        if (mCurrentMode == XtingConstants.FMAM.TYPE_FM) {
            realScale /= 1000;
        }

        if (realScale > mMaxScale) realScale = mMaxScale;
        if (realScale < mMinScale) realScale = mMinScale;
        mInnerRuler.setCurrentScale(realScale);
    }

    //如果控件尺寸变化，中间光标的位置也要重新定义
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawable();
    }

    public void refreshRuler() {
        initDrawable();
        mInnerRuler.init(mContext);
        mInnerRuler.refreshSize();
    }

    public void setRulerOnTouchListener(View.OnTouchListener touchListener) {
        mInnerRuler.setOnTouchListener(touchListener);
    }

    public boolean isScrollingOrTouched() {
        return mInnerRuler.isScrollingOrTouched();
    }

    public int getEdgeColor() {
        return mEdgeColor;
    }

    //设置能否使用边缘效果
    public void setCanEdgeEffect(boolean canEdgeEffect) {
        this.mCanEdgeEffect = canEdgeEffect;
    }

    public float getFactor() {
        return mFactor;
    }

    public void setFactor(float factor) {
        this.mFactor = factor;
        mInnerRuler.postInvalidate();
    }

    public boolean canEdgeEffect() {
        return mCanEdgeEffect;
    }

    public int getCurrentScale() {
        return Math.round(mInnerRuler.getCurrentScale());
    }

    public void setMinScale(int minScale) {
        this.mMinScale = minScale;
    }

    public int getMinScale() {
        return mMinScale;
    }

    public void setMaxScale(int maxScale) {
        this.mMaxScale = maxScale;
    }

    public int getMaxScale() {
        return mMaxScale;
    }

    public void setCursorWidth(int cursorWidth) {
        this.mCursorWidth = cursorWidth;
    }

    public int getCursorWidth() {
        return mCursorWidth;
    }

    public void setCursorHeight(int cursorHeight) {
        this.mCursorHeight = cursorHeight;
    }

    public int getCursorHeight() {
        return mCursorHeight;
    }


    public void setBigScaleLength(int bigScaleLength) {
        this.mBigScaleLength = bigScaleLength;
    }

    public int getBigScaleLength() {
        return mBigScaleLength;
    }

    public void setBigScaleWidth(int bigScaleWidth) {
        this.mBigScaleWidth = bigScaleWidth;
    }

    public int getBigScaleWidth() {
        return mBigScaleWidth;
    }

    public int getMiddleScaleWidth() {
        return mMiddleScaleWidth;
    }

    public void setmMiddleScaleWidth(int mMiddleScaleWidth) {
        this.mMiddleScaleWidth = mMiddleScaleWidth;
    }

    public void setSmallScaleLength(int smallScaleLength) {
        this.mSmallScaleLength = smallScaleLength;
    }

    public int getSmallScaleLength() {
        return mSmallScaleLength;
    }

    public int getMiddleScaleLength() {
        return mMiddleScaleLength;
    }

    public void setmMiddleScaleLength(int mMiddleScaleLength) {
        this.mMiddleScaleLength = mMiddleScaleLength;
    }

    public void setSmallScaleWidth(int smallScaleWidth) {
        this.mSmallScaleWidth = smallScaleWidth;
    }

    public int getSmallScaleWidth() {
        return mSmallScaleWidth;
    }

    public void setTextMarginTop(int textMarginTop) {
        this.mTextMarginHead = textMarginTop;
    }

    public int getTextMarginHead() {
        return mTextMarginHead;
    }

    public int getReflectTextMargin() {
        return mReflectTextMargin;
    }

    public void setReflectTextMargin(int mReflectTextMargin) {
        this.mReflectTextMargin = mReflectTextMargin;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getScaleColor() {
        return mScaleColor;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }

    public int getCount() {
        return mCount;
    }

    public float getLastScale() {
        return mLastScale;
    }

    public void setLastScale(float mLastScale) {
        this.mLastScale = mLastScale;
    }
}
