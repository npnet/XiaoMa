package com.xiaoma.shop.business.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: 带斜线背景的LinearLayout
 */

public class SlashView extends LinearLayout {

    private Paint mSlashPaint;

    public SlashView(Context context) {
        this(context, null);
    }

    public SlashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mSlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSlashPaint.setColor(getResources().getColor(android.R.color.holo_red_dark));
        mSlashPaint.setStrokeWidth(3);
        mSlashPaint.setDither(true);
        mSlashPaint.setStrokeCap(Paint.Cap.ROUND);

    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(0, 6, getWidth(),getHeight()-8, mSlashPaint);
    }


}
