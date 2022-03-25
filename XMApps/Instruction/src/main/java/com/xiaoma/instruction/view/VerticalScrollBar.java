package com.xiaoma.instruction.view;

import android.annotation.DimenRes;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.xiaoma.instruction.R;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.views.XmSkinView;


/**
 * Cover {@link com.xiaoma.ui.view.XmScrollBar}
 */
public class VerticalScrollBar extends XmSkinView {

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
    private int marginTopBottom = 0;
    /**
     * foreground bar 距 background 两端的上下间距
     */
    private int marginLeftRight = 0;

    // 背景图
    private Drawable background;
    // 前景图
    private Drawable foregroundBar;
    // 背景默认宽度
    private int mWidth = 4;
    // 背景默认高度
    private int mHeight = 331;
    // 前景图片中心y
    private int mThumbCenterY;
    // 前景图片的高度
    private int mThumbHeight;
    private ObservableScrollView mScrollView;
    private boolean mTouchHited;
    // 前景的top
    private int mSliderTop;
    // 前景的bottom
    private int mSliderBottom;
    // scrollView 滑动的距离 0 <= offset <= range - visiableHeight
    private int offset;
    // scrollView range
    private int range;

    public VerticalScrollBar(Context context) {
        this(context, null);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化背景和前景
//        background = getResources().getDrawable(R.drawable.scroll_bar_container, null);
//        foregroundBar = getResources().getDrawable(R.drawable.scroll_bar_progress, null);

        background = XmSkinManager.getInstance().getDrawable(R.drawable.scroll_bar_container, null);
        foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.scroll_bar_progress, null);
        setVisibility(GONE);
    }

    @Override
    public void applySkin() {
        super.applySkin();
        background = XmSkinManager.getInstance().getDrawable(R.drawable.scroll_bar_container, null);
        foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.scroll_bar_progress, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize + marginLeftRight * 2 + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize + marginTopBottom * 2;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSliderTop = (mThumbCenterY - mThumbHeight / 2 + marginTopBottom);
        mSliderBottom = (mThumbCenterY + mThumbHeight / 2 - marginTopBottom);

        // 滚动条背景
        background.setBounds(getPaddingLeft(), 0, getWidth() - getPaddingRight(), getHeight());
        background.draw(canvas);

        // 滚动条前景
        foregroundBar.setBounds(getPaddingLeft() + marginLeftRight, mSliderTop,
                getWidth() - getPaddingRight() - marginLeftRight, mSliderBottom);
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

    public void setMarginTopBottom(@DimenRes int marginTopBottom) {
        this.marginTopBottom = marginTopBottom;
    }

    public void setMarginLeftRight(@DimenRes int marginLeftRight) {
        this.marginLeftRight = marginLeftRight;
    }
}