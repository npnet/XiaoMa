package com.xiaoma.assistant.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xiaoma.assistant.R;

/**
 * Cover {@link com.xiaoma.ui.view.XmScrollBar}
 */
public class VerticalScrollBar extends View {

    private static final String TAG = "VerticalScrollBar";

    /**
     * foreground bar的最小高度
     */
    public static final int MIN_BAR_HEIGHT = 43;
    /**
     * foreground bar的最大高度
     */
    public static final int MAX_BAR_HEIGHT = 400;
    /**
     * foreground bar 距 background 两端的左右间距
     */
    public static final int MARGIN_TOP_BOTTOM = 3;
    /**
     * foreground bar 距 background 两端的上下间距
     */
    public static final int MARGIN_LEFT_RIGHT = 3;

    private Context mContext;
    private Drawable background;
    private Drawable foregroundBar;
    private int mWidth;
    private int mHeight;
    private int mThumbCenterY;
    private int mThumbHeight;
    private ObservableScrollView mScrollView;
    private boolean mTouchHited;
    private int mSliderTop;
    private int mSliderBottom;
    private int offset;
    private int range;

    public VerticalScrollBar(Context context) {
        this(context, null);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        // 初始化背景和前景
        background = mContext.getResources().getDrawable(R.drawable.vertical_scrollbar_background);
        foregroundBar = mContext.getResources().getDrawable(R.drawable.vertical_scrollbar_foreground);

        // 初始化View的固定宽高
        mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.width_vertical_scroll_bar);
        mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.height_vertical_scroll_bar);

        setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize + MARGIN_LEFT_RIGHT * 2 + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize + MARGIN_TOP_BOTTOM * 2;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSliderTop = (mThumbCenterY - mThumbHeight / 2 + MARGIN_TOP_BOTTOM);
        mSliderBottom = (mThumbCenterY + mThumbHeight / 2 - MARGIN_TOP_BOTTOM);

        // 滚动条背景
        background.setBounds(getPaddingLeft(), 0, getWidth() - getPaddingRight(), getHeight());
        background.draw(canvas);

        // 滚动条前景
        foregroundBar.setBounds(getPaddingLeft() + MARGIN_LEFT_RIGHT, mSliderTop,
                getWidth() - getPaddingRight() - MARGIN_LEFT_RIGHT, mSliderBottom);
        foregroundBar.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int downY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (downY >= mSliderTop && downY <= mSliderBottom) {
                    mTouchHited = true;
                } else {
                    scrollList(downY);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchHited) {
                    scrollList(downY);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchHited = false;
                break;
        }
        return true;
    }

    private void scrollList(int downY) {
        if (mScrollView == null) return;
        int offset = (int) ((downY + 0f) / getHeight() * range);
        mScrollView.scrollBy(0, offset - this.offset);
    }

    public void setScrollView(final ObservableScrollView scrollView) {
        mScrollView = scrollView;
        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy, int offset, int range) {
                calculateThumbY(offset, range);
            }
        });
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                calculateThumbY(scrollView.getVerticalScrollOffset(), scrollView.getVerticalScrollRange());
                setVisibility(scrollView.getVerticalScrollRange() > scrollView.getHeight() ? VISIBLE : GONE);
            }
        });
    }

    public void calculateThumbY(int offset, int range) {
        VerticalScrollBar.this.offset = offset;
        VerticalScrollBar.this.range = range;
        int visibleLength = mScrollView.getHeight();
        if (range == 0) {
            return;
        }
        int height = (mHeight * visibleLength) / range;
        if (height < MIN_BAR_HEIGHT) {
            mThumbHeight = MIN_BAR_HEIGHT;
        } else {
            mThumbHeight = height;
        }
        mThumbCenterY = (int) (mThumbHeight / 2f + ((mHeight - mThumbHeight * 1f) * offset) / (range - visibleLength));
        invalidate();
    }
}