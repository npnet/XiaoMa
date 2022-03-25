package com.xiaoma.shop.business.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.xiaoma.shop.R;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/8
 */
public class SlopeDeleteTextView extends AppCompatTextView {

    private Paint mSlopDeletePaint;
    private int mHeight;
    private int mSlopeLineColor;
    private int mSlopeLineWidth;
    private float mLeftTopFactor;
    private float mRightBottomFactor;

    public SlopeDeleteTextView(@NonNull Context context) {
        this(context, null);
    }

    public SlopeDeleteTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlopeDeleteTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainAttrsFromXml(context, attrs);
        prepareDrawTools();
    }

    private void obtainAttrsFromXml(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlopeDeleteTextView);
        mSlopeLineColor = typedArray.getColor(R.styleable.SlopeDeleteTextView_slope_line_color, Color.WHITE);
        mSlopeLineWidth = typedArray.getDimensionPixelSize(R.styleable.SlopeDeleteTextView_slope_line_width, 2);
        mLeftTopFactor = typedArray.getFloat(R.styleable.SlopeDeleteTextView_left_top_span_factor, 0.4f);
        mRightBottomFactor = typedArray.getFloat(R.styleable.SlopeDeleteTextView_right_bottom_span_factor, 0.6f);
        typedArray.recycle();
    }

    private void prepareDrawTools() {
        mSlopDeletePaint = new Paint();
        mSlopDeletePaint.setStrokeCap(Paint.Cap.ROUND);
        mSlopDeletePaint.setDither(true);
        mSlopDeletePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mSlopDeletePaint.setStyle(Paint.Style.FILL);
        mSlopDeletePaint.setStrokeWidth(mSlopeLineWidth);
        mSlopDeletePaint.setColor(mSlopeLineColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mHeight * mLeftTopFactor, getWidth(), mHeight * mRightBottomFactor, mSlopDeletePaint);
    }
}
