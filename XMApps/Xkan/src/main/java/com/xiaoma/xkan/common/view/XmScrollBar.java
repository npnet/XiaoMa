package com.xiaoma.xkan.common.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.R;
import com.xiaoma.ui.constract.ScrollBarDirection;
import com.xiaoma.xkan.common.constant.XkanConstants;

import org.simple.eventbus.EventBus;

import skin.support.widget.SkinCompatSupportable;


/**
 * @author KY
 * @date 2018/10/18
 */
public class XmScrollBar extends View implements SkinCompatSupportable {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    /**
     * 从从滑动状态到静止状态的透明变化动画时长
     */
    public static final int DURATION_TO_STATIC = 1500;
    /**
     * 从静止状态到滑动状态的透明变化动画时长
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
    private View mParentView;

    public XmScrollBar(Context context) {
        this(context, null);
    }

    public XmScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XmScrollBar, 0, 0);
        staticAlpha = (int) (255 * attributes.getFloat(R.styleable.XmScrollBar_staticAlpha, DEF_STATIC_ALPHA));
        scrollingAlpha = (int) (255 * attributes.getFloat(R.styleable.XmScrollBar_scrollingAlpha, DEF_SCROLL_ALPHA));
        mDirection = attributes.getInt(R.styleable.XmScrollBar_xmdirection, HORIZONTAL);
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

        // 默认隐藏，当有数据加载时会自动修改可见
        setVisibility(INVISIBLE);
    }

    private void initBackground(Context context) {
        if (mDirection == HORIZONTAL) {
            // 初始化背景和前景和阴影
//            background = getResources().getDrawable(R.drawable.scrollbar_background);
//            foregroundBar = getResources().getDrawable(R.drawable.scrollbar_foreground);
//            shadowBar = getResources().getDrawable(R.drawable.scrollbar_shadow);
            background = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_background, context);
            foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_foreground, context);
            shadowBar = XmSkinManager.getInstance().getDrawable(R.drawable.scrollbar_shadow, context);
        } else {
            // 初始化背景和前景， 竖直模式不需要阴影
//            background = getResources().getDrawable(R.drawable.scrollbar_vertical_background);
//            foregroundBar = getResources().getDrawable(R.drawable.scrollbar_vertical_foreground);
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
        foregroundBar.setBounds(FOREGROUND_BAR_MARGIN_TOP_BOTTOM, top,
                BACKGROUND_HEIGHT - FOREGROUND_BAR_MARGIN_TOP_BOTTOM, bottom);
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

    private RecyclerView mRecyclerView;
    private RecyclerView.OnScrollListener mScrollListener;
    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;

    /**
     * @param recyclerView recyclerView
     */
    public void setRecyclerView(final RecyclerView recyclerView) {
        // 要先移除上一个监听器,避免重复调用之后注册了多个监听
        if (mRecyclerView != null) {
            if (mChildAttachStateChangeListener != null) {
                mRecyclerView.removeOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
            }
            if (mScrollListener != null) {
                mRecyclerView.removeOnScrollListener(mScrollListener);
            }
        }
        if (mAdapter != null) {
            if (mAdapterDataObserver != null) {
                mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
            }
        }

        mRecyclerView = recyclerView;
        mAdapter = mRecyclerView.getAdapter();
        if (mAdapter == null) {
            throw new RuntimeException("pls set adapter first!");
        }
        mAdapter.registerAdapterDataObserver(mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        calculate(recyclerView);
                    }
                }, 200);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onChanged();
            }
        });
        mRecyclerView.addOnScrollListener(mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                calculate(recyclerView);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        setAlphaWithAnim(DURATION_TO_SCROLL, scrollingAlpha);
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        setAlphaWithAnim(DURATION_TO_STATIC, staticAlpha);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                    default:
                }
            }
        });
        mRecyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                //这里需要post一下，直接computeHorizontalScrollRange得到的数据有误
                post(new Runnable() {
                    @Override
                    public void run() {
                        boolean visible;
                        if (mDirection == HORIZONTAL) {
                            int horizontalContentLength = recyclerView.computeHorizontalScrollRange();
                            int horizontalVisibleLength = recyclerView.getWidth();
                            visible = (horizontalContentLength - horizontalVisibleLength) > 0;
                        } else {
                            int VerticalContentLength = recyclerView.computeVerticalScrollRange();
                            int VerticalVisibleLength = recyclerView.getHeight();
                            visible = (VerticalContentLength - VerticalVisibleLength) > 0;
                        }
                        setVisibility(visible ? VISIBLE : INVISIBLE);
                        //解决scrollbar不可见无效的情况
                        EventBus.getDefault().post("", XkanConstants.REFRESH_SCROLL);
                    }
                });
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        boolean visible;
                        if (mDirection == HORIZONTAL) {
                            int horizontalContentLength = recyclerView.computeHorizontalScrollRange();
                            int horizontalVisibleLength = recyclerView.getWidth();
                            visible = (horizontalContentLength - horizontalVisibleLength) > 0;
                        } else {
                            int VerticalContentLength = recyclerView.computeVerticalScrollRange();
                            int VerticalVisibleLength = recyclerView.getHeight();
                            visible = (VerticalContentLength - VerticalVisibleLength) > 0;
                        }
                        setVisibility(visible ? VISIBLE : INVISIBLE);
                        EventBus.getDefault().post(visible, XkanConstants.REFRESH_SCROLL);
                    }
                });
            }
        });
    }

    private void calculate(RecyclerView recyclerView) {
        // scroll 的相关计算逻辑参考原生的FastScroller
        if (mDirection == HORIZONTAL) {
            calculateHorizontal(recyclerView);
        } else {
            calculateVertical(recyclerView);
        }
    }

    private void calculateVertical(RecyclerView recyclerView) {
        int verticalVisibleLength = recyclerView.getHeight();
        int offsetY = recyclerView.computeVerticalScrollOffset();
        int verticalContentLength = recyclerView.computeVerticalScrollRange();

        if (verticalContentLength == 0) {
            return;
        }
        final int measureHeight = getMeasuredHeight();
        final int height = (measureHeight * verticalVisibleLength) / verticalContentLength;
        if (height < MIN_BAR_WIDTH) {
            mVerticalThumbHeight = MIN_BAR_WIDTH;
        } else if (height > MAX_BAR_WIDTH) {
            mVerticalThumbHeight = MAX_BAR_WIDTH;
        } else {
            mVerticalThumbHeight = height;
        }
        mVerticalThumbCenterY = (int) (mVerticalThumbHeight / 2f + ((measureHeight - mVerticalThumbHeight * 1f) * offsetY) / (verticalContentLength - verticalVisibleLength));

        invalidate();
    }

    private void calculateHorizontal(RecyclerView recyclerView) {
        int horizontalVisibleLength = recyclerView.getWidth();
        int offsetX = recyclerView.computeHorizontalScrollOffset();
        int horizontalContentLength = recyclerView.computeHorizontalScrollRange();

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
        animation = ObjectAnimator.ofInt(this, "alphaScroll", paint.getAlpha(), toAlpha).setDuration(duration);
        animation.start();
    }

    @Override
    public void applySkin() {
        initBackground(getContext());
    }
}