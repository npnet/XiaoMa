package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.xiaoma.setting.R;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.skin.views.XmSkinView;


/**
 * Created by qiuboxiang on 2018/10/10 10:30
 * description:
 *
 */
public class TickMarkSeekBar extends XmSkinView {

    private static final String TAG = "TickMarkSeekBar";
    private float mMax;
    private float mProgress;
    private int mTickMarkCount;
    private float mCursorIndex; //当前所在刻度的索引
    private int mCalculateMethod; //计算进度方法
    private float mBottomTextHeight;
    private float mBottomTextLength;
    private int mSeekBarHeight;
    private int mSeekBarWidth;
    private int mBackgroundWidth;
    private int mTotalWidth;
    private int mTotalHeight;
    private float mMarginTop;
    private float mMarginBottom;
    private int mTickMarkTextColor;
    private float mTickMarkTextHeight;
    private int mBottomTextColor;
    private float mSectionLength;
    private Paint mBitmapPaint;
    private Paint mTickMarkTextPaint;
    private Paint mBottomTextPaint;
    private RectF mSeekBarRect;
    private int mTickMarkTextSize;
    private int mBottomTextSize;
    private CharSequence[] mTickMarkTextArray;
    private float[] mTextLengthArray;
    private String mBottomText;
    private int mTickMarkTextGap;
    private int mBottomTextGap;
    private float mDotY;
    private int mSensitivity;
    private int mRightSensitivity;
    private float mSeekBarCenterX;
    private float dValue;//差值
    private boolean mTouchHited; //是否触摸到SeekBar
    private boolean mScrollToTickMark; //是否自动滑动到最近的刻度线位置
    private Scroller mScroller;
    private float mScrollCursorIndex;
    private int mScrollDuration;
    private boolean mIsEnabled = true;
    private boolean mInit = true;
    private boolean mIsScrolling;
    private Bitmap mSeekBarBitmap;
    private Bitmap mShadowBitmap;
    private Bitmap mProgressBitmap;
    private Bitmap mSliderBitmap;
    private int mTickMarkLength;
    private int mShadowWidth;
    private int mShadowMargin;
    private int mTickMarkGap;
    private int mSliderOutsideLength;
    private int mTickMarkPaddingTop;
    private int mTickMarkPaddingBottom;
    private OnProgressChangedListener mListener;

    private final int DEFAULT_SCROLL_DURATION = 150;
    private final int DEFAULT_LINE_COUNT = -1;
    private final int DEFAULT_MAX = 100;
    private final int DEFAULT_PROGRESS = 0;
    private final int DEFAULT_CURSOR_INDEX = 0;
    private final boolean DEFAULT_SCROLL_TO_TICK_MARK = true;

    private final int CALCULATE_BY_PROGRESS = 1;
    private final int CALCULATE_BY_CURSOR_INDEX = 2;

    public TickMarkSeekBar(Context context) {
        this(context, null);
    }

    public TickMarkSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickMarkSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyConfig(context, attrs);
        initDimension();
        initPaint();
        initBitmap();
        initOther(context);
    }

    private void initDimension() {
        Resources resources = getResources();
        mTickMarkLength = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_tickmark_length);
        mTickMarkGap = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_tickmark_gap);
        mSliderOutsideLength = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_slider_outside_length);
        mTickMarkPaddingTop = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_tickmark_padding_top);
        mTickMarkPaddingBottom = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_tickmark_padding_bottom);
        mShadowMargin = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_shadow_margin);
        mMarginTop = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_margin_top);
        mRightSensitivity = resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_right_sensitivity);
    }

    private void initOther(Context context) {
        setClickable(true);
        mScroller = new Scroller(context, new DecelerateInterpolator());
        mTickMarkCount = DEFAULT_LINE_COUNT;
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TickMarkSeekBar);
        mScrollDuration = ta.getInteger(R.styleable.TickMarkSeekBar_scrollDuration, DEFAULT_SCROLL_DURATION);
        mMax = ta.getInteger(R.styleable.TickMarkSeekBar_max, DEFAULT_MAX);
        mProgress = ta.getInteger(R.styleable.TickMarkSeekBar_progress, DEFAULT_PROGRESS);
        mCursorIndex = ta.getInteger(R.styleable.TickMarkSeekBar_cursorIndex, DEFAULT_CURSOR_INDEX);
        mSensitivity = ta.getDimensionPixelSize(R.styleable.TickMarkSeekBar_sensitivity, resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_tickmark_length));
        mBottomText = ta.getString(R.styleable.TickMarkSeekBar_bottomText);
        mBottomTextGap = ta.getDimensionPixelSize(R.styleable.TickMarkSeekBar_bottomTextGap, resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_default_bottom_text_gap));
        mBottomTextSize = ta.getDimensionPixelSize(R.styleable.TickMarkSeekBar_bottomTextSize, resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_default_bottom_text_size));
        mBottomTextColor = ta.getColor(R.styleable.TickMarkSeekBar_bottomTextColor, resources.getColor(R.color.white));
        mTickMarkTextGap = ta.getDimensionPixelSize(R.styleable.TickMarkSeekBar_tickMarkTextGap, resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_default_tick_mark_text_gap));
        mTickMarkTextSize = ta.getDimensionPixelSize(R.styleable.TickMarkSeekBar_tickMarkTextSize, resources.getDimensionPixelSize(R.dimen.tickmark_seekbar_default_tick_mark_text_size));
        mTickMarkTextColor = ta.getColor(R.styleable.TickMarkSeekBar_tickMarkTextColor, resources.getColor(R.color.default_tick_mark_text_color));
        mScrollToTickMark = ta.getBoolean(R.styleable.TickMarkSeekBar_scrollToTickMark, DEFAULT_SCROLL_TO_TICK_MARK);
        ta.recycle();
    }

    private void initPaint() {
        mBitmapPaint = getPaint();
        mBitmapPaint.setFilterBitmap(true);

        mTickMarkTextPaint = getPaint();
        mTickMarkTextPaint.setColor(mTickMarkTextColor);
        mTickMarkTextPaint.setTextSize(mTickMarkTextSize);

        mBottomTextPaint = getPaint();
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setTextSize(mBottomTextSize);

        Paint.FontMetrics fontMetrics = mTickMarkTextPaint.getFontMetrics();
        mTickMarkTextHeight = fontMetrics.bottom - fontMetrics.top;
//        mMarginTop = mTickMarkTextHeight / 2 - mTickMarkPaddingTop;
        mMarginBottom = mTickMarkTextHeight / 2 - mTickMarkPaddingBottom;

        if (!TextUtils.isEmpty(mBottomText)) {
            fontMetrics = mBottomTextPaint.getFontMetrics();
            mBottomTextHeight = fontMetrics.bottom - fontMetrics.top;
            mBottomTextLength = mBottomTextPaint.measureText(mBottomText);
        }
    }

    private Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        return paint;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initSeekBar();
        setMeasuredDimension(mTotalWidth, mTotalHeight);
    }

    private void initBitmap() {
//        mSeekBarBitmap = BitmapFactory.decodeResource(XmSkinManager.getInstance().getSkinResources(), R.drawable.bg_tickmark_seekbar);
//        mShadowBitmap = BitmapFactory.decodeResource(XmSkinManager.getInstance().getSkinResources(), R.drawable.shadow);
//        mProgressBitmap = BitmapFactory.decodeResource(XmSkinManager.getInstance().getSkinResources(), R.drawable.tickmark_seekbar_progress_background);
//        mSliderBitmap = BitmapFactory.decodeResource(XmSkinManager.getInstance().getSkinResources(), R.drawable.tickmark_seekbar_slider);

        mSeekBarBitmap = SkinUtils.drawable2Bitmap(XmSkinManager.getInstance().getDrawable( R.drawable.bg_tickmark_seekbar));
        mShadowBitmap = SkinUtils.drawable2Bitmap(XmSkinManager.getInstance().getDrawable( R.drawable.shadow));
        mProgressBitmap = SkinUtils.drawable2Bitmap(XmSkinManager.getInstance().getDrawable( R.drawable.tickmark_seekbar_progress_background));
        mSliderBitmap =  SkinUtils.drawable2Bitmap(XmSkinManager.getInstance().getDrawable( R.drawable.tickmark_seekbar_slider));

    }

    private void initSeekBar() {
        mSeekBarHeight = mSeekBarBitmap.getHeight();
        mSeekBarWidth = mSeekBarBitmap.getWidth();

        float maxTextLength = 0;
        if (mTickMarkTextArray != null) {
            mTextLengthArray = new float[mTickMarkTextArray.length];
            for (int i = 0; i < mTickMarkTextArray.length; i++) {
                mTextLengthArray[i] = mTickMarkTextPaint.measureText(mTickMarkTextArray[i].toString());
                maxTextLength = Math.max(mTextLengthArray[i], maxTextLength);
            }
            if (mTickMarkCount == DEFAULT_LINE_COUNT) {
                mTickMarkCount = mTickMarkTextArray.length - 1;
            }
        }
        float totalLength = mSeekBarHeight - mTickMarkPaddingTop - mTickMarkPaddingBottom;
        mSectionLength = mTickMarkCount != DEFAULT_LINE_COUNT ? (totalLength / (mTickMarkCount - 1)) : mSeekBarHeight;

        mShadowWidth = mShadowBitmap.getWidth() + mShadowMargin;
        mBackgroundWidth = mSeekBarWidth + mShadowWidth;
        float left;
        float bottom;
        float right;
        if (mTickMarkTextArray != null && !TextUtils.isEmpty(mBottomText)) {
            left = Math.max(maxTextLength + mTickMarkTextGap + mTickMarkLength + mTickMarkGap, mBottomTextLength / 2 - mSeekBarWidth / 2);
            bottom = Math.max(mMarginBottom, mBottomTextGap + mBottomTextHeight);
            right = mBottomTextLength / 2 - mSeekBarWidth / 2 - mShadowWidth;
        } else if (mTickMarkTextArray == null && TextUtils.isEmpty(mBottomText)) {
            left = mTickMarkLength + mTickMarkGap;
            bottom = 0;
            right = 0;
        } else if (mTickMarkTextArray != null) {
            left = maxTextLength + mTickMarkTextGap + mTickMarkLength + mTickMarkGap;
            bottom = mMarginBottom;
            right = 0;
        } else {
            left = mBottomTextLength / 2 - mSeekBarWidth / 2;
            bottom = mBottomTextGap + mBottomTextHeight;
            right = mBottomTextLength / 2 - mSeekBarWidth / 2 - mShadowWidth;
        }
        mSeekBarRect = new RectF(left, mMarginTop, left + mSeekBarWidth, mMarginTop + mSeekBarHeight);
        mSeekBarCenterX = mSeekBarRect.left + mSeekBarWidth / 2;

        mTotalWidth = (int) (left + mBackgroundWidth + right);
        mTotalHeight = (int) (mMarginTop + mSeekBarHeight + bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mInit) {
            mInit = false;
            switch (mCalculateMethod) {
                case CALCULATE_BY_PROGRESS:
                    if (mProgress == mMax) {
                        mDotY = mMarginTop;
                    } else {
                        mDotY = mSeekBarRect.bottom - mTickMarkPaddingBottom - mSeekBarHeight * (mProgress / mMax);
                    }
                    break;
                default:
                case CALCULATE_BY_CURSOR_INDEX:
                    if (mCursorIndex == mTickMarkCount - 1) {
                        mDotY = mMarginTop;
                    } else {
                        mDotY = mSeekBarRect.bottom - mTickMarkPaddingBottom - mCursorIndex * mSectionLength;
                    }
                    break;
            }
        }

        /**
         * 1.绘制SeekBar
         */
        RectF mShadowRectf = new RectF(mSeekBarRect.right + mShadowMargin, mSeekBarRect.top, mSeekBarRect.right + mShadowWidth, mSeekBarRect.bottom);
        canvas.drawBitmap(mShadowBitmap, null, mShadowRectf, mBitmapPaint);
        canvas.drawBitmap(mSeekBarBitmap, null, mSeekBarRect, mBitmapPaint);

        /**
         * 2.绘制进度背景
         */
        RectF mProgressRect = new RectF(mSeekBarRect.left, mDotY, mSeekBarRect.right, mSeekBarRect.bottom);
//        NinePatch ninePatch = new NinePatch(mProgressBitmap, mProgressBitmap.getNinePatchChunk(), null);
//        ninePatch.draw(canvas, mProgressRect);
        canvas.drawBitmap(mProgressBitmap, null, mProgressRect,mBitmapPaint);

        /**
         * 3.绘制刻度线及文字标注
         */
        for (int i = 0; i < mTickMarkCount; i++) {
            float y = mSeekBarRect.bottom - mTickMarkPaddingBottom - mSectionLength * i;
            canvas.drawLine(mSeekBarRect.left - (mTickMarkLength + mTickMarkGap), y, mSeekBarRect.left - mTickMarkGap, y, mTickMarkTextPaint);

            if (mTickMarkTextArray != null) {
                float textLength = mTextLengthArray[i];
                float x = mSeekBarRect.left - (textLength + mTickMarkTextGap + mTickMarkLength + mTickMarkGap);
                String text = (String) mTickMarkTextArray[i];
                if (i == mTickMarkCount - 1) {
                    y = mTickMarkTextHeight / 2;
                }
                canvas.drawText(text, x, getBaseLine(y, mTickMarkTextPaint), mTickMarkTextPaint);
            }
        }

        /**
         * 4.绘制底部文字
         */
        if (!TextUtils.isEmpty(mBottomText)) {
            float y = mSeekBarRect.bottom + mBottomTextGap + mBottomTextHeight / 2;
            float x = mSeekBarCenterX - mBottomTextLength / 2;
            canvas.drawText(mBottomText, x, getBaseLine(y, mBottomTextPaint), mBottomTextPaint);
        }

        /**
         * 5.绘制滑块
         */
        RectF sliderRectF = new RectF(mSeekBarRect.left - mSliderOutsideLength, mDotY - mSliderBitmap.getHeight() / 2, mSeekBarRect.right + mSliderOutsideLength, mDotY + mSliderBitmap.getHeight() / 2);
        canvas.drawBitmap(mSliderBitmap, null, sliderRectF, mBitmapPaint);
    }

    private float getBaseLine(float centerLine, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return centerLine + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    }

    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (mIsEnabled && !mIsScrolling) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handleTouchDown(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    handleTouchMove(event);
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    handleTouchUp();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void handleTouchDown(MotionEvent event) {
        int downX = (int) event.getX();
        int downY = (int) event.getY();
        RectF rectf = new RectF(mSeekBarRect.left - mSensitivity, mSeekBarRect.top - mSensitivity, mSeekBarRect.right + mRightSensitivity, mSeekBarRect.bottom + mSensitivity);
        if (rectf.contains(downX, downY)) {
            mTouchHited = true;
            setDotY(event.getY());
            invalidate();
        }
    }

    private void handleTouchMove(MotionEvent event) {
        if (mTouchHited) {
            setDotY(event.getY());
            invalidate();
        }
    }

    private void setScrollCursorIndex() {
        mScrollCursorIndex = (mDotY - mMarginTop - mTickMarkPaddingTop) / mSectionLength;
    }

    private void handleTouchUp() {
        if (mTouchHited) {
            if (mScrollToTickMark) {
                setScrollCursorIndex();
                int lower = (int) Math.floor(mScrollCursorIndex);
                int higher = (int) Math.ceil(mScrollCursorIndex);

                float offset = mScrollCursorIndex - lower;
                float mNextIndex = offset < 0.5f ? lower : higher;

                if (!mScroller.computeScrollOffset()) {
                    mIsScrolling = true;
                    float value;
                    float finalDotY;
                    if (mNextIndex == 0) {
                        finalDotY = mMarginTop;
                    } else {
                        finalDotY = mMarginTop + mTickMarkPaddingTop + mNextIndex * mSectionLength;
                    }
                    value = finalDotY - mDotY;
                    int dy = (int) (value);
                    dValue = (mDotY - (int) mDotY) + (value - dy);
                    mScroller.startScroll((int) mSeekBarCenterX, (int) mDotY, 0, dy, mScrollDuration);
                    invalidate();

                    notifyListener(finalDotY);
                }
            } else {
                notifyListener(mDotY);
            }

            mTouchHited = false;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setDotY(mScroller.getCurrY() + dValue);
            setScrollCursorIndex();
            invalidate();
        } else {
            mIsScrolling = false;
        }
        super.computeScroll();
    }

    public int getProgress() {
        return (int) mProgress;
    }

    public int getCursorIndex() {
        return (int) mCursorIndex;
    }

    private void setDotY(float downY) {
        if (downY >= mSeekBarRect.bottom) {
            mDotY = mSeekBarRect.bottom;
        } else if (downY <= mSeekBarRect.top) {
            mDotY = mSeekBarRect.top;
        } else {
            mDotY = downY;
        }

//        notifyListener(mDotY);
//        Log.d(TAG, "mProgress: " + mProgress + " rate:" + rate + " mCursorIndex:" + mCursorIndex);
    }

    private void notifyListener(float dotY) {
        if (mListener != null) {
            float progressLength = mSeekBarRect.bottom - dotY;
            float rate = progressLength / mSeekBarHeight;
            float progress = rate * mMax;
            float cursorIndex = progressLength / mSectionLength;
            mListener.onProgressChanged(getId(), (int) progress, rate, (int) cursorIndex);
        }
    }

    public void setTickMarkTextArray(String[] array) {
        mTickMarkTextArray = array;
    }

    public void setTopTickMarkText(String text) {
        initTickMarkTextArray();
        mTickMarkTextArray[mTickMarkTextArray.length - 1] = text;
    }

    public void setBottomTickMarkText(String text) {
        initTickMarkTextArray();
        mTickMarkTextArray[0] = text;
    }

    private void initTickMarkTextArray() {
        if (mTickMarkCount == DEFAULT_LINE_COUNT) {
            throw new RuntimeException("Please set sectionCount first.");
        }
        if (mTickMarkTextArray == null) {
            mTickMarkTextArray = new String[mTickMarkCount];
            for (int i = 0; i < mTickMarkTextArray.length; i++) {
                mTickMarkTextArray[i] = "";
            }
        }
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mCalculateMethod = CALCULATE_BY_PROGRESS;
        mInit = true;
        invalidate();
    }

    public void setTickMarkCount(int sectionCount) {
        this.mTickMarkCount = sectionCount;
    }

    public void setCursorIndex(int mCursorIndex) {
        this.mCursorIndex = mCursorIndex;
        mCalculateMethod = CALCULATE_BY_CURSOR_INDEX;
        mInit = true;
        invalidate();
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int viewId, int progress, float rate, int cursorIndex);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void applySkin() {
        super.applySkin();
        initBitmap();
        invalidate();
    }
}
