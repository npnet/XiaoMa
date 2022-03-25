package com.xiaoma.ui.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.views.XmSkinView;
import com.xiaoma.ui.R;

/**
 * Cover {@link XmScrollBar}
 */
public class VerticalScrollBar extends XmSkinView {

    private static final String TAG = "VerticalScrollBar";

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
     * foreground bar的最小高度
     */
    public static final int MIN_BAR_HEIGHT = 83;
    /**
     * foreground bar的最大高度
     */
    public static final int MAX_BAR_HEIGHT = 400;
    /**
     * foreground bar 距 background 两端的上下间距
     */
    public static final int MARGIN_TOP_BOTTOM = 1;
    /**
     * foreground bar宽度
     */
    public static final int FOREGROUND_BAR_WIDTH = 6;
    /**
     * background 宽度
     */
    public static final int BACKGROUND_WIDTH = 12;
    /**
     * shadow 宽度
     */
    public static final int SHADOW_WIDTH = 20;
    /**
     * foreground bar与background 的左右间距
     */
    public static final int MARGIN_LEFT_RIGHT = (BACKGROUND_WIDTH - FOREGROUND_BAR_WIDTH) / 2;

    private Paint paint = new Paint();
    private Context mContext;
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
    private int mWidth;
    private int mHeight;
    private int mThumbCenterY;
    private int mThumbHeight;
    private boolean needChangeAlpha;
    private RecyclerView mRecyclerView;
    private boolean mTouchHited;
    private int mSliderTop;
    private int mSliderBottom;

    public VerticalScrollBar(Context context) {
        this(context, null);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.VerticalScrollBar, 0, 0);
        staticAlpha = (int) (255 * attributes.getFloat(R.styleable.VerticalScrollBar_staticAlpha, DEF_STATIC_ALPHA));
        scrollingAlpha = (int) (255 * attributes.getFloat(R.styleable.VerticalScrollBar_scrollingAlpha, DEF_SCROLL_ALPHA));
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
        if (needChangeAlpha) {
            paint.setAlpha(staticAlpha);
        }

        // 初始化背景和前景
//        background = mContext.getResources().getDrawable(R.drawable.vertical_scrollbar_background);
//        foregroundBar = mContext.getResources().getDrawable(R.drawable.vertical_scrollbar_foreground);
//        shadowBar = mContext.getResources().getDrawable(R.drawable.vertical_scrollbar_shadow);

         initView();

        // 初始化View的固定宽高
        mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.width_new_vertical_scrollbar);
        mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.height_new_vertical_scrollbar);
        // 默认隐藏，当有数据加载时会自动修改可见
        setVisibility(GONE);
    }

    private void initView(){

        background = XmSkinManager.getInstance().getDrawable(R.drawable.vertical_scrollbar_background);
        foregroundBar = XmSkinManager.getInstance().getDrawable(R.drawable.vertical_scrollbar_foreground);
        shadowBar = XmSkinManager.getInstance().getDrawable(R.drawable.vertical_scrollbar_shadow);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            Log.d(TAG, "widthMode == MeasureSpec.EXACTLY");
            mWidth = widthSize + MARGIN_LEFT_RIGHT * 2 + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            Log.d(TAG, "heightMode == MeasureSpec.EXACTLY");
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
        if (mRecyclerView != null) {
            Log.d(TAG, "scrollBy: " + 0);
            mRecyclerView.scrollBy(0,1);
            mRecyclerView.scrollBy(0,-1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSliderTop = (mThumbCenterY - mThumbHeight / 2 + MARGIN_TOP_BOTTOM);
        mSliderBottom = (mThumbCenterY + mThumbHeight / 2 - MARGIN_TOP_BOTTOM);

        // 滚动条背景
        background.setBounds(getPaddingLeft(), 0, getWidth() - getPaddingRight(), getHeight());
        if (needChangeAlpha) {
            background.setAlpha(paint.getAlpha());
        }
        background.draw(canvas);

        // 滚动条前景
        foregroundBar.setBounds(getPaddingLeft() + MARGIN_LEFT_RIGHT, mSliderTop,
                getWidth() - getPaddingRight() - MARGIN_LEFT_RIGHT, mSliderBottom);
        if (needChangeAlpha) {
            foregroundBar.setAlpha(paint.getAlpha());
        }
        foregroundBar.draw(canvas);

       /* shadowBar.setBounds(mSliderTop - MARGIN_TOP_BOTTOM, BACKGROUND_WIDTH, mSliderBottom + MARGIN_TOP_BOTTOM,
                BACKGROUND_WIDTH + SHADOW_WIDTH);
        if (needChangeAlpha) {
            shadowBar.setAlpha(paint.getAlpha() / 2);
        }
        shadowBar.draw(canvas);*/
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
        if (mRecyclerView == null) return;
        int offset = (int) ((downY + 0f) / getHeight() * mRecyclerView.computeVerticalScrollRange());
        mRecyclerView.scrollBy(0, offset - mRecyclerView.computeVerticalScrollOffset());
    }

    /**
     * 用于属性动画的set方法
     *
     * @param alpha 透明度
     */
    public void setAlphaScroll(int alpha) {
        if (needChangeAlpha) {
            paint.setAlpha(alpha);
            invalidate();
        }
    }

    /**
     * @param recyclerView recyclerView
     */
    public void setRecyclerView(final RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // scroll 的相关计算逻辑参考原生的FastScroller
                int visibleLength = recyclerView.getHeight();
                int offsetX = recyclerView.computeVerticalScrollOffset();
                int contentLength = recyclerView.computeVerticalScrollRange();

                if (contentLength == 0) {
                    return;
                }
                int width = (mHeight * visibleLength) / contentLength;
                if (width < MIN_BAR_HEIGHT) {
                    mThumbHeight = MIN_BAR_HEIGHT;
                } else if (width > MAX_BAR_HEIGHT) {
                    mThumbHeight = MAX_BAR_HEIGHT;
                } else {
                    mThumbHeight = width;
                }
                mThumbCenterY = (int) (mThumbHeight / 2f + ((mHeight - mThumbHeight * 1f) * offsetX) / (contentLength - visibleLength));

                invalidate();
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

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                //这里需要post一下，直接computeVerticalScrollRange得到的数据有误
                post(new Runnable() {
                    @Override
                    public void run() {
                        int verticalContentLength = recyclerView.computeVerticalScrollRange();
                        int verticalVisibleLength = recyclerView.getHeight();
                        boolean visible = (verticalContentLength - verticalVisibleLength) > 0;
                        setVisibility(visible ? VISIBLE : GONE);
                    }
                });
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        int verticalContentLength = recyclerView.computeVerticalScrollRange();
                        int verticalVisibleLength = recyclerView.getHeight();
                        boolean visible = (verticalContentLength - verticalVisibleLength) > 0;
                        setVisibility(visible ? VISIBLE : GONE);
                    }
                });
            }
        });
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
        super.applySkin();
        initView();
    }
}