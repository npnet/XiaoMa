package com.xiaoma.personal.feedback.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.personal.R;


/**
 * Created by Gillben on 2019/1/21 0021
 * <p>
 * desc: NestedScrollView bar
 */
public class XMNestedScrollBar extends View {

    private static final String TAG = "XMNestedScrollBar";

    private Paint mPaint = new Paint();
    private Drawable background;
    private Drawable foreground;
    private Drawable shadowGround;

    private static final int DEFAULT_WIDTH = 32;
    private static final int DEFAULT_HEIGHT = 489;
    private int mWidth;
    private int mHeight;

    /**
     * foreground bar的最小宽度
     */
    public static final int MIN_BAR_HEIGHT = 83;
    /**
     * foreground bar的最大宽度
     */
    public static final int MAX_BAR_HEIGHT = 400;
    /**
     * shadow 高度
     */
    public static final int SHADOW_HEIGHT = 20;

    private int SCROLL_BAR_WIDTH = 14;
    private int SCROLL_BAR_MARGIN = 3;
    private int MARGIN_TOP_BOTTOM;
    private int mVerticalThumbCenterY;
    private int mVerticalThumbHeight;


    public XMNestedScrollBar(Context context) {
        this(context, null);
    }

    public XMNestedScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XMNestedScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setVisibility(INVISIBLE);
    }


    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XMNestedScrollBar);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.XMNestedScrollBar_nested_scroll_bar_width, DEFAULT_WIDTH);
        mHeight = typedArray.getDimensionPixelSize(R.styleable.XMNestedScrollBar_nested_scroll_bar_height, DEFAULT_HEIGHT);
        MARGIN_TOP_BOTTOM = typedArray.getDimensionPixelSize(R.styleable.XMNestedScrollBar_margin_top_bottom, 4);
        typedArray.recycle();

        //初始化scrollBar 背景
        background = context.getDrawable(R.drawable.scrollbar_vertical_background);
        foreground = context.getDrawable(R.drawable.scrollbar_vertical_foreground);
        shadowGround = context.getDrawable(R.drawable.scrollbar_vertical_shadow);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int height = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int top = (mVerticalThumbCenterY - mVerticalThumbHeight / 2 + MARGIN_TOP_BOTTOM);
        int bottom = (mVerticalThumbCenterY + mVerticalThumbHeight / 2 - MARGIN_TOP_BOTTOM);

        //滚动条背景
        background.setBounds(0, 0, SCROLL_BAR_WIDTH, getHeight());
        background.setAlpha(mPaint.getAlpha());
        background.draw(canvas);

        // 滚动条前景
        foreground.setBounds(SCROLL_BAR_MARGIN, top, SCROLL_BAR_WIDTH - SCROLL_BAR_MARGIN, bottom + SCROLL_BAR_MARGIN);
        foreground.setAlpha(mPaint.getAlpha());
        foreground.draw(canvas);

        //滚动条前景阴影
//        shadowGround.setBounds(14, top - MARGIN_TOP_BOTTOM, 14 + SHADOW_HEIGHT, bottom + MARGIN_TOP_BOTTOM);
//        shadowGround.setAlpha(mPaint.getAlpha());
//        shadowGround.draw(canvas);
    }


    /**
     * NestedScrollView 添加额外的滚动条
     *
     * @param nestedScrollView NestedScrollView
     */
    @SuppressLint("RestrictedApi")
    public void setNestedScrollViewBar(final XMNestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View nested, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                handleScroll(nestedScrollView);
            }
        });

        nestedScrollView.setOnNestedStatusCallback(new XMNestedScrollView.OnNestedStatusCallback() {
            @Override
            public void scrollEnable() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        int verticalContentLength = nestedScrollView.computeVerticalScrollRange();
                        int verticalVisibleLength = nestedScrollView.getHeight();
                        boolean visible = (verticalContentLength - verticalVisibleLength) > 0;
                        if (visible) {
                            handleScroll(nestedScrollView);
                            setVisibility(VISIBLE);
                        } else {
                            setVisibility(INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void handleScroll(NestedScrollView nested) {
        int visibleLength = nested.getHeight();
        int offsetY = nested.computeVerticalScrollOffset();
        int contentLength = nested.computeVerticalScrollRange();

        if (contentLength == 0) {
            //内部无child view
            return;
        }
        int height = (mHeight * visibleLength) / contentLength;
        if (height < MIN_BAR_HEIGHT) {
            mVerticalThumbHeight = MIN_BAR_HEIGHT;
        } else if (height > MAX_BAR_HEIGHT) {
            mVerticalThumbHeight = MAX_BAR_HEIGHT;
        } else {
            mVerticalThumbHeight = height;
        }
        mVerticalThumbCenterY = (int) (mVerticalThumbHeight / 2f + ((mHeight - mVerticalThumbHeight * 1f) * offsetY) / (contentLength - visibleLength));

        invalidate();
    }
}
