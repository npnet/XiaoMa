package com.xiaoma.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.skin.views.XmSkinImageView;
import com.xiaoma.ui.R;

/**
 * 支持倒影效果的ImageView
 */
public class ReflectionImageView extends XmSkinImageView {
    private int mReflectionHeight;
    private int mReflectionPadding;

    private Paint mReflectPaint;
    private final GradientHolder mGradientHolder = new GradientHolder();

    public ReflectionImageView(Context context) {
        this(context, null);
    }

    public ReflectionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 硬件加速下LinearGradient会出问题,因此使用软件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ReflectionImageView, defStyleAttr, defStyleAttr);

        mReflectionHeight = a.getDimensionPixelSize(R.styleable.ReflectionImageView_reflectionHeight, 30);
        mReflectionPadding = a.getDimensionPixelSize(R.styleable.ReflectionImageView_reflectionPadding, 10);

        a.recycle();
    }

    public int getReflectionHeight() {
        return mReflectionHeight;
    }

    public void setReflectionHeight(int reflectionHeight) {
        if (reflectionHeight == mReflectionHeight)
            return;
        mReflectionHeight = reflectionHeight;
        invalidate();
    }

    public int getReflectionPadding() {
        return mReflectionPadding;
    }

    public void setReflectionPadding(int reflectionPadding) {
        if (reflectionPadding == mReflectionPadding)
            return;
        mReflectionPadding = reflectionPadding;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mReflectionHeight > 0 && getDrawable() != null) {
            canvas.save();

            int oriWidth = getMeasuredWidth();
            int oriHeight = getMeasuredHeight() - mReflectionHeight - mReflectionPadding;
            canvas.scale(1f, (float) oriHeight / getMeasuredHeight(),
                    oriWidth >> 1, 0);
            // 绘制原图
            super.onDraw(canvas);
            // 绘制倒影
            drawReflect(canvas, oriHeight);

            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawReflect(Canvas canvas, int oriHeight) {
        float scale = (float) canvas.getHeight() / oriHeight;
        float scaledReflectPadding = mReflectionPadding * scale;
        float scaledReflectHeight = mReflectionHeight * scale;

        float left = 0;
        float top = canvas.getHeight() - scaledReflectHeight;
        float right = canvas.getWidth();
        float bottom = canvas.getHeight();

        canvas.scale(1, -1, canvas.getWidth() >> 1, canvas.getHeight() >> 1);
        canvas.translate(0f, -(canvas.getHeight() + scaledReflectPadding));
        canvas.clipRect(left, top, right, bottom);
        // 绘制倒影
        getDrawable().draw(canvas);
        // 绘制遮罩层
        canvas.drawRect(left, top, right, bottom, getReflectPaint());
    }

    private Paint getReflectPaint() {
        if (mReflectPaint == null) {
            mReflectPaint = new Paint();
            mReflectPaint.setAntiAlias(true);
            mReflectPaint.setDither(true);
            mReflectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
        mReflectPaint.setShader(mGradientHolder.getGradient(
                getMeasuredHeight() - mReflectionHeight - mReflectionPadding,
                getMeasuredHeight()));
        return mReflectPaint;
    }


    private class GradientHolder {
        private float mY0;
        private float mY1;
        private LinearGradient mGradient;

        LinearGradient getGradient(float y0, float y1) {
            if (mGradient == null
                    || mY0 != y0 || mY1 != y1) {
                mGradient = new LinearGradient(
                        0, getMeasuredHeight() - mReflectionHeight - mReflectionPadding,
                        0, getMeasuredHeight(),
                        Color.TRANSPARENT, 0x60ffffff, Shader.TileMode.CLAMP);
                mY0 = y0;
                mY1 = y1;
            }
            return mGradient;
        }
    }
}
