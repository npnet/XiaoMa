package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;

/**
 * Created by Gillben on 2018/12/29 0029
 */
public class PetExperienceView extends View {

    private static final int DEFAULT_CIRCLE_RADIUS = 15;
    private static final int CONTENT_OFFSET = 1;

    private Paint mBoundsPaint;
    private Paint mExperiencePaint;

    private long curExperienceProgress;        //当前经验值
    private long upgradeTotalExperience;       //升级的总经验
    private int boundsColor;                    //边框颜色
    private int growColor;                      //成长经验条颜色
    private int circleRadius;                   //圆角半斤
    private int contentMargin;

    public PetExperienceView(Context context) {
        this(context, null);
    }

    public PetExperienceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PetExperienceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PetExperienceView);
        growColor = typedArray.getColor(R.styleable.PetExperienceView_grow_color, Color.RED);
        boundsColor = typedArray.getColor(R.styleable.PetExperienceView_bounds_color, Color.WHITE);
        circleRadius = typedArray.getDimensionPixelSize(R.styleable.PetExperienceView_circle_radius, DEFAULT_CIRCLE_RADIUS);
        contentMargin = typedArray.getInteger(R.styleable.PetExperienceView_content_offset, CONTENT_OFFSET);
        typedArray.recycle();
    }


    private void initPaint() {
        mBoundsPaint = new Paint();
        mBoundsPaint.setAntiAlias(true);
        mBoundsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBoundsPaint.setColor(boundsColor);

        mExperiencePaint = new Paint();
        mExperiencePaint.setAntiAlias(true);
        mExperiencePaint.setStyle(Paint.Style.FILL);
        mExperiencePaint.setColor(growColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
    }

    private void drawContent(Canvas canvas) {
        int left = 0;
        int top = 0;
        int right = getMeasuredWidth();
        int bottom = getMeasuredHeight();

        //底部背景
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, circleRadius, circleRadius, mBoundsPaint);

        //成长经验背景
        float growPercent = calculationPercent(curExperienceProgress) / 100;
        RectF grow = new RectF(left + contentMargin,
                top + contentMargin,
                (right - contentMargin) * growPercent,
                bottom - contentMargin);
        canvas.drawRoundRect(grow, circleRadius, circleRadius, mExperiencePaint);
    }


    private float calculationPercent(long progress) {
        return (float) progress * 100 / upgradeTotalExperience;
    }


    public void updateExperience(long total, long current) {
        this.upgradeTotalExperience = total;
        this.curExperienceProgress = current;
        invalidate();
    }

}
