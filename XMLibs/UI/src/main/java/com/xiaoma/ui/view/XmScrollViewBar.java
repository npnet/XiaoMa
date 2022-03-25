package com.xiaoma.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.R;
import com.xiaoma.ui.constract.ScrollBarDirection;

import skin.support.widget.SkinCompatSupportable;

public class XmScrollViewBar extends View implements SkinCompatSupportable {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    /**
     * 从从滑动状态到静止状态的延时
     */
    public static final int DURATION_TO_STATIC = 1500;
    /**
     * 从静止状态到滑动状态(或反向)的透明变化动画时长
     */
    public static final int DURATION_TO_SCROLL = 40;
    public static final float DEF_STATIC_ALPHA = 0.3f;
    public static final float DEF_SCROLL_ALPHA = 0.75f;
    public static final int ALPHA_MAX = 255;
    public static final int ALPHA_MIN = 0;
    /**
     * foreground bar的最小宽度
     */
    public static final int MIN_BAR_WIDTH = 83;
    /**
     * foreground bar的最大宽度
     */
    public static final int MAX_BAR_WIDTH = 400;
    /**
     * foreground bar 距 background 两端的间距
     */
    public static final int MARGIN_LEFT_RIGHT = 3;
    /**
     * foreground bar高度
     */
    public static final int FOREGROUND_BAR_HEIGHT = 6;
    /**
     * background 高度
     */
    public static final int BACKGROUND_HEIGHT = 12;
    /**
     * shadow 高度
     */
    public static final int SHADOW_HEIGHT = 20;
    /**
     * foreground bar与background 的上下间距
     */
    public static final int FOREGROUND_BAR_MARGIN_TOP_BOTTOM = (BACKGROUND_HEIGHT - FOREGROUND_BAR_HEIGHT) / 2;

    private Paint paint = new Paint();
    /**
     * 静止时的透明度
     * from 0 to 255
     */
    @IntRange(from = 0, to = 255)
    private int staticAlpha;
    /**
     * 滑动式的透明度
     * from 0 to 255
     */
    @IntRange(from = 0, to = 255)
    private int scrollingAlpha;
    private ObjectAnimator animation;
    private Drawable background;
    private Drawable foregroundBar;
    private Drawable shadowBar;
    private int mHorizontalThumbCenterX;
    private int mHorizontalThumbWidth;
    private int mVerticalThumbCenterY;
    private int mVerticalThumbHeight;
    private int mDirection;
    private boolean isShow;
    private Runnable staticTask = new Runnable() {
        @Override
        public void run() {
            setAlphaWithAnim(DURATION_TO_STATIC, staticAlpha);
        }
    };

    public XmScrollViewBar(Context context) {
        this(context, null);
    }

    public XmScrollViewBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmScrollViewBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XmScrollViewBar, 0, 0);
        staticAlpha = (int) (255 * DEF_STATIC_ALPHA);
        scrollingAlpha = (int) (255 * DEF_SCROLL_ALPHA);
        mDirection = attributes.getInt(R.styleable.XmScrollViewBar_scroll_direction, HORIZONTAL);
        attributes.recycle();

        // 检查并纠错透明度值
        if (staticAlpha > ALPHA_MAX) {
            staticAlpha = ALPHA_MAX;
        } else if (staticAlpha < ALPHA_MIN) {
            staticAlpha = 0;

        }
        if (scrollingAlpha > ALPHA_MAX) {
            scrollingAlpha = ALPHA_MAX;
        } else if (scrollingAlpha < ALPHA_MIN) {
            scrollingAlpha = ALPHA_MIN;
        }
        paint.setAlpha(staticAlpha);
        initBackground(context);
    }

    private void initBackground(Context context) {
        if (mDirection == HORIZONTAL) {
            // 初始化背景和前景和阴影
            background = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_background, context);
            foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_foreground, context);
            shadowBar = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_shadow, context);
        } else {
            // 初始化背景和前景， 竖直模式不需要阴影
            background = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_vertical_background, context);
            foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_vertical_foreground, context);
        }

    }

    /**
     * 0    horizontal
     * 1    vertical
     *
     * @param direction
     */
    public void setDirection(@ScrollBarDirection int direction) {
        mDirection = direction;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getSize(widthMeasureSpec) == 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelSize(R.dimen.width_scrollbar), MeasureSpec.EXACTLY);
        }
        if (MeasureSpec.getSize(heightMeasureSpec) == 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelSize(R.dimen.height_scrollbar), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDirection == HORIZONTAL) {
            drawHorizontal(canvas);
        } else {
            drawVertical(canvas);
        }
    }

    private void drawVertical(Canvas canvas) {
        int top = (mVerticalThumbCenterY - mVerticalThumbHeight / 2 + MARGIN_LEFT_RIGHT);
        int bottom = (mVerticalThumbCenterY + mVerticalThumbHeight / 2 - MARGIN_LEFT_RIGHT);

        // 滚动条背景
        background.setBounds(0, 0, BACKGROUND_HEIGHT, getHeight());
        background.setAlpha(paint.getAlpha());
        background.draw(canvas);

        // 滚动条前景
        foregroundBar.setBounds(
                FOREGROUND_BAR_MARGIN_TOP_BOTTOM,
                top,
                BACKGROUND_HEIGHT - FOREGROUND_BAR_MARGIN_TOP_BOTTOM,
                bottom
        );
        foregroundBar.setAlpha(paint.getAlpha());
        foregroundBar.draw(canvas);
    }

    private void drawHorizontal(Canvas canvas) {
        int left = (mHorizontalThumbCenterX - mHorizontalThumbWidth / 2 + MARGIN_LEFT_RIGHT);
        int right = (mHorizontalThumbCenterX + mHorizontalThumbWidth / 2 - MARGIN_LEFT_RIGHT);

        // 滚动条背景
        background.setBounds(0, 0, getWidth(), BACKGROUND_HEIGHT);
        background.setAlpha(paint.getAlpha());
        background.draw(canvas);

        // 滚动条前景
        foregroundBar.setBounds(left, FOREGROUND_BAR_MARGIN_TOP_BOTTOM, right,
                BACKGROUND_HEIGHT - FOREGROUND_BAR_MARGIN_TOP_BOTTOM);
        foregroundBar.setAlpha(paint.getAlpha());
        foregroundBar.draw(canvas);

        shadowBar.setBounds(left - MARGIN_LEFT_RIGHT, BACKGROUND_HEIGHT, right + MARGIN_LEFT_RIGHT,
                BACKGROUND_HEIGHT + SHADOW_HEIGHT);
        shadowBar.setAlpha(paint.getAlpha() / 2);
        shadowBar.draw(canvas);
    }

    /**
     * 用于属性动画的set方法
     *
     * @param alpha 透明度
     */
    public void setAlphaScroll(int alpha) {
        paint.setAlpha(alpha);
        invalidate();
    }

    private Scrollable mScrollView;
    private OnScrollChangeListener mScrollListener;

    /**
     * @param scrollable recyclerView
     */
    public void setScrollableView(final Scrollable scrollable) {
        mScrollView = scrollable;
        mScrollView.setOnScrollChangeListener(mScrollListener = new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isShow) {
                    setAlphaWithAnim(DURATION_TO_SCROLL, scrollingAlpha);
                }
                calculate(scrollable);
                removeCallbacks(staticTask);
                postDelayed(staticTask, 500);
            }
        });
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calculate(scrollable);
            }
        });
    }

    private void calculate(Scrollable scrollView) {
        // scroll 的相关计算逻辑参考原生的FastScroller
        if (mDirection == HORIZONTAL) {
            calculateHorizontal(scrollView);
        } else {
            calculateVertical(scrollView);
        }
    }

    private void calculateVertical(Scrollable scrollView) {
        int totalHeight = scrollView.getVerticalScrollRange();
        int visibleHeight = scrollView.getMeasuredHeight();
        int offsetY = scrollView.getVerticalScrollOffset();


        if (totalHeight == 0) {
            return;
        }
        final int measureHeight = getMeasuredHeight();
        final int height = (measureHeight * visibleHeight) / totalHeight;
        if (height < MIN_BAR_WIDTH) {
            mVerticalThumbHeight = MIN_BAR_WIDTH;
        } else if (height > MAX_BAR_WIDTH) {
            mVerticalThumbHeight = MAX_BAR_WIDTH;
        } else {
            mVerticalThumbHeight = height;
        }
        mVerticalThumbCenterY = (int) (mVerticalThumbHeight / 2f + ((measureHeight - mVerticalThumbHeight * 1f) * offsetY) / (totalHeight - visibleHeight));

        invalidate();
    }

    private void calculateHorizontal(Scrollable scrollView) {
        int horizontalVisibleLength = scrollView.getMeasuredWidth();
        int offsetX = scrollView.getHorizontalScrollOffset();
        int horizontalContentLength = scrollView.getHorizontalScrollRange();

        if (horizontalContentLength == 0) {
            return;
        }
        final int measureWidth = getMeasuredWidth();
        int width = (measureWidth * horizontalVisibleLength) / horizontalContentLength;
        if (width < MIN_BAR_WIDTH) {
            mHorizontalThumbWidth = MIN_BAR_WIDTH;
        } else if (width > MAX_BAR_WIDTH) {
            mHorizontalThumbWidth = MAX_BAR_WIDTH;
        } else {
            mHorizontalThumbWidth = width;
        }
        mHorizontalThumbCenterX = (int) (mHorizontalThumbWidth / 2f + ((measureWidth - mHorizontalThumbWidth * 1f) * offsetX) / (horizontalContentLength - horizontalVisibleLength));

        invalidate();
    }

    /**
     * @param duration duration
     * @param toAlpha  toAlpha
     */
    private void setAlphaWithAnim(long duration, int toAlpha) {
        if (animation != null) {
            animation.end();
            animation.cancel();
            animation = null;
        }

        if (toAlpha == staticAlpha) {
            isShow = false;
        } else if (toAlpha == scrollingAlpha) {
            isShow = true;
        }

        animation = ObjectAnimator.ofInt(this, "alphaScroll", paint.getAlpha(), toAlpha).setDuration(duration);
        animation.start();
    }

    @Override
    public void applySkin() {
        initBackground(getContext());
    }

    interface Scrollable {
        int getVerticalScrollOffset();

        int getVerticalScrollRange();

        int getHorizontalScrollOffset();

        int getHorizontalScrollRange();

        void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener);

        int getMeasuredHeight();

        int getMeasuredWidth();

        ViewTreeObserver getViewTreeObserver();
    }
}
