package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/28 0028 16:19
 *   desc:   车速View
 * </pre>
 */
public class CarSpeedView extends View {

    private static final String TAG = "CarSpeedView";
    private static final int DEFAULT_WIDTH = 40;
    private static final int DEFAULT_HEIGHT = 500;
    private static final int MAX_SPEED = 180;
    private Paint speedPaint;
    private Paint backgroundPaint;

    private boolean scrolling;
    private int location;


    public CarSpeedView(Context context) {
        this(context, null);
    }

    public CarSpeedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarSpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        setFocusable(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getColor(R.color.text_stroke_color));
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);

        speedPaint = new Paint();
        speedPaint.setColor(context.getColor(R.color.add_product_right_bg));
        speedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        speedPaint.setAntiAlias(true);
        speedPaint.setDither(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        } else {
            width = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        performDraw(canvas);
    }

    private void performDraw(Canvas canvas) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = left + getMeasuredWidth();
        int bottom = top + getMeasuredHeight();
        canvas.drawRect(left, top, right, bottom, backgroundPaint);

        if (scrolling) {
            int foregroundTop = location;
            if (foregroundTop < 0) {
                foregroundTop = 0;
            }
            if (foregroundTop > getMeasuredHeight()) {
                foregroundTop = getMeasuredHeight();
            }
            Rect rect = new Rect(left, foregroundTop, right, bottom);
            canvas.drawRect(rect, speedPaint);
        }
    }


    public void update(boolean scroll, int location) {
        this.scrolling = scroll;
        this.location = location;
        invalidate();
    }


}
