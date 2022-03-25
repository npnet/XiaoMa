package com.xiaoma.vrpractice.common.view.tuneruler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.xiaoma.vrpractice.R;

/**
 * 头向上的尺子
 */

public class TopHeadRuler extends HorizontalRuler {
    private Matrix reflectMatrix;
    private Paint reflectPaint = new Paint();

    public TopHeadRuler(Context context, TuneRuler tuneRuler) {
        super(context, tuneRuler);
    }

    {
        reflectMatrix = new Matrix();
        reflectMatrix.setScale(1, -1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawEdgeEffect(canvas);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        //计算开始和结束刻画时候的刻度
        float start = (getScrollX() - mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        float end = (getScrollX() + canvas.getWidth() + mDrawOffset) / mParent.getInterval() + mParent.getMinScale();

        float middle = Math.round(start + (end - start) / 2);
        float leftGradientEnd = middle;
        float rightGradientStart = middle;

        float middleStartY = (mParent.getBigScaleLength() - mParent.getMiddleScaleLength()) / 2;
        float middleEndY = (mParent.getBigScaleLength() + mParent.getMiddleScaleLength()) / 2;

        float smallStartY = (mParent.getBigScaleLength() - mParent.getSmallScaleLength()) / 2;
        float smallEndY = (mParent.getBigScaleLength() + mParent.getSmallScaleLength()) / 2;

        for (float i = start; i <= end; i++) {
            //将要刻画的刻度转化为位置信息
            float locationX = (i - mParent.getMinScale()) * mParent.getInterval();
            if (i == middle) {
                mTextPaint.setColor(getResources().getColor(R.color.color_tune_text));
                mTextPaint.setFakeBoldText(true);
            } else {
                mTextPaint.setColor(mParent.getTextColor());
                mTextPaint.setFakeBoldText(false);
            }
            if (i <= leftGradientEnd) {
                int alpha = (int) ((i - start) * 1f / (leftGradientEnd - start) * 255);
                mBigScalePaint.setAlpha(alpha);
                mMiddleScalePaint.setAlpha(alpha);
                mSmallScalePaint.setAlpha(alpha);
                mTextPaint.setAlpha(alpha);
            } else if (i >= rightGradientStart) {
                int alpha = (int) ((end - i) * 1f / (end - rightGradientStart) * 255);
                mBigScalePaint.setAlpha(alpha);
                mMiddleScalePaint.setAlpha(alpha);
                mSmallScalePaint.setAlpha(alpha);
                mTextPaint.setAlpha(alpha);
            } else {
                mBigScalePaint.setAlpha(255);
                mMiddleScalePaint.setAlpha(255);
                mSmallScalePaint.setAlpha(255);
                mTextPaint.setAlpha(255);
            }

            if (i >= mParent.getMinScale() && i <= mParent.getMaxScale()) {
                if (i % mCount == 0) {
                    canvas.drawLine(locationX, 0, locationX, mParent.getBigScaleLength(), mBigScalePaint);
                    String scale = RulerStringUtil.resultValueOf(i, mParent.getFactor());
                    canvas.drawText(scale, locationX, mParent.getTextMarginHead(), mTextPaint);
                    Bitmap reflectTextBitmap = getTextBitmap(scale, mTextPaint);
                    canvas.drawBitmap(reflectTextBitmap, locationX - reflectTextBitmap.getWidth() / 2, mParent.getTextMarginHead() + mParent.getReflectTextMargin(), mTextPaint);
                } else {
                    if (i % mMiddleCount == 0) {
                        canvas.drawLine(locationX, middleStartY, locationX, middleEndY, mMiddleScalePaint);
                    } else {
                        canvas.drawLine(locationX, smallStartY, locationX, smallEndY, mSmallScalePaint);
                    }
                }
            }
        }
        //画轮廓线
//        canvas.drawLine(getScrollX(), 0, getScrollX() + canvas.getWidth(), 0, mOutLinePaint);
    }


    //画边缘效果
    private void drawEdgeEffect(Canvas canvas) {
        if (mParent.canEdgeEffect()) {
            if (!mStartEdgeEffect.isFinished()) {
                int count = canvas.save();
                //旋转位移Canvas来使EdgeEffect绘画在正确的地方
                canvas.rotate(270);
                canvas.translate(-mParent.getCursorHeight(), 0);
                if (mStartEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mStartEdgeEffect.finish();
            }
            if (!mEndEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.rotate(90);
                canvas.translate(0, -mLength);
                if (mEndEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mEndEdgeEffect.finish();
            }
        }
    }

    private Bitmap getTextBitmap(String string, TextPaint paint) {
        float width = StaticLayout.getDesiredWidth(string, paint);
        float height = paint.getTextSize();
        Bitmap bitmap = Bitmap.createBitmap(Math.round(width + 0.5f), Math.round(height + 0.5f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(string, bitmap.getWidth() / 2f, paint.getTextSize(), paint);
        Bitmap reflect = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), reflectMatrix, false);
        bitmap.recycle();

        Canvas reflectCanvas = new Canvas(reflect);
        LinearGradient shader = new LinearGradient(0, 0, 0, reflect.getHeight(), 0x99ffffff, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        reflectPaint.setShader(shader);
        reflectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        reflectCanvas.drawRect(0, 0, reflect.getWidth(), reflect.getHeight(), reflectPaint);
        reflectPaint.setXfermode(null);

        return reflect;
    }
}
