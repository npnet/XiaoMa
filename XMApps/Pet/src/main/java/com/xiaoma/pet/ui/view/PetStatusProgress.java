package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;

/**
 * Created by Gillben on 2018/12/27 0027
 * <p>
 * desc:
 */
public class PetStatusProgress extends View {

    private int left;
    private int top;
    private int right;
    private int bottom;

    private Paint mPaint;
    private int progressColor;
    private int progressSize;

    private int progressOffset;             //偏移量
    private boolean isEat = false;          //是否处于喂食状态

    private float curProgressPercent = 0f;                //当前进度
    private int curLevelTotalExperience;    //当前等级所需总经验

    public PetStatusProgress(Context context) {
        this(context, null);
    }

    public PetStatusProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PetStatusProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PetStatusProgress);
        progressColor = typedArray.getColor(R.styleable.PetStatusProgress_progress_color, getResources().getColor(R.color.color_progress, null));
        progressSize = typedArray.getDimensionPixelSize(R.styleable.PetStatusProgress_progress_size, 8);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(progressColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressSize);
    }


    private void calculation() {
        progressOffset = progressSize / 2;
        left = getLeft() + progressOffset;
        top = getTop() + progressOffset;
        right = getRight() - progressOffset;
        bottom = getBottom() - progressOffset;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEat) {
            drawPetEatFood(canvas);
        } else {
            drawPetNormal(canvas);
        }
    }

    //普通状态，加载经验值
    private void drawPetNormal(Canvas canvas) {
        calculation();
        canvas.drawArc(left, top, right, bottom, -90, 360 * curProgressPercent, false, mPaint);
    }

    //正在进食，喂食进度
    private void drawPetEatFood(Canvas canvas) {
        calculation();
        canvas.drawArc(left, top, right, bottom, -90, 360 * curProgressPercent, false, mPaint);
    }


    public void update(boolean isEat, float progressPercent) {
        this.isEat = isEat;
        this.curProgressPercent = progressPercent;
        invalidate();
    }

}
