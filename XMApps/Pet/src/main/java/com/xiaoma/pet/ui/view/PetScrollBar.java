package com.xiaoma.pet.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;


/**
 * Created by Gillben on 2019/1/21 0021
 * <p>
 */
public class PetScrollBar extends View {

    private static final int DEFAULT_WIDTH = 6;
    private static final int DEFAULT_HEIGHT = 420;
    private static final int MIN_BAR_HEIGHT = 83;
    private static final int MAX_BAR_HEIGHT = 400;
    private static final int CORNERS = 6;
    private static final int SCROLL_BAR_WIDTH = 6;
    private Paint backgroundPaint = new Paint();
    private Paint foregroundPaint = new Paint();
    private int mWidth;
    private int mHeight;
    private int foregroundBarWidth;
    private int foregroundBarColor;
    private int backgroundBarColor;

    private int MARGIN_TOP_BOTTOM;
    private int mVerticalThumbCenterY;
    private int mVerticalThumbHeight;


    public PetScrollBar(Context context) {
        this(context, null);
    }

    public PetScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PetScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setVisibility(INVISIBLE);
    }


    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PetScrollBar);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.PetScrollBar_scroll_bar_height, DEFAULT_WIDTH);
        mHeight = typedArray.getDimensionPixelSize(R.styleable.PetScrollBar_scroll_bar_height, DEFAULT_HEIGHT);
        MARGIN_TOP_BOTTOM = typedArray.getDimensionPixelSize(R.styleable.PetScrollBar_margin_top_bottom, 4);
        foregroundBarWidth = typedArray.getDimensionPixelOffset(R.styleable.PetScrollBar_foreground_bar_width, SCROLL_BAR_WIDTH);
        foregroundBarColor = typedArray.getColor(R.styleable.PetScrollBar_foreground_bar_color, Color.GRAY);
        backgroundBarColor = typedArray.getColor(R.styleable.PetScrollBar_background_bar_color, Color.WHITE);
        typedArray.recycle();

        backgroundPaint.setColor(backgroundBarColor);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);

        foregroundPaint.setColor(foregroundBarColor);
        foregroundPaint.setAntiAlias(true);
        foregroundPaint.setDither(true);
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
//        int height = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
//        setMeasuredDimension(width,height);
//    }


    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int top = (mVerticalThumbCenterY - mVerticalThumbHeight / 2);
        int bottom = (mVerticalThumbCenterY + mVerticalThumbHeight / 2);

        //滚动条背景
        RectF rectF = new RectF(0, 0, foregroundBarWidth, getHeight());
        canvas.drawRoundRect(rectF, CORNERS, CORNERS, backgroundPaint);

        // 滚动条前景
        RectF foreground = new RectF(0, top, foregroundBarWidth, bottom);
        canvas.drawRoundRect(foreground, CORNERS, CORNERS, foregroundPaint);
    }


    @SuppressLint("RestrictedApi")
    public void setRecyclerViewBar(final RecyclerView recyclerView) {
        recyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View nested, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                handleScroll(recyclerView);
            }
        });


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        boolean visible;
                        int verticalContentLength = recyclerView.computeVerticalScrollRange();
                        int verticalVisibleLength = recyclerView.getHeight();
                        visible = (verticalContentLength - verticalVisibleLength) > 0;
                        setVisibility(visible ? VISIBLE : INVISIBLE);
                    }
                });
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        boolean visible;
                        int verticalContentLength = recyclerView.computeVerticalScrollRange();
                        int verticalVisibleLength = recyclerView.getHeight();
                        visible = (verticalContentLength - verticalVisibleLength) > 0;
                        setVisibility(visible ? VISIBLE : INVISIBLE);
                    }
                });
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void handleScroll(RecyclerView recyclerView) {
        int visibleLength = recyclerView.getHeight();
        int offsetY = recyclerView.computeVerticalScrollOffset();
        int contentLength = recyclerView.computeVerticalScrollRange();

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
        mVerticalThumbCenterY = (int) (mVerticalThumbHeight / 2f +
                ((mHeight - mVerticalThumbHeight * 1f) * offsetY) / (contentLength - visibleLength));
        invalidate();
    }
}
