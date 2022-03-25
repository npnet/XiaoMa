package com.xiaoma.image.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Created by LKF on 2018/1/26 0026.
 * 正多边形Glide图片截取
 */

public class PolygonTransformation extends BitmapTransformation {
    private static final String ID = SquareCornerLightTransForm.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));

    private int mEdgeCount = 6;
    private int mStrokeWidth;
    private int mStrokeColor = Color.TRANSPARENT;
    private int mPadding;
    private float mCornerRadius;
    private int mLastRadius;//用来记录内切圆半径

    private Paint createBasePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        if (mCornerRadius > 0) {
            paint.setPathEffect(new CornerPathEffect(mCornerRadius));
        }
        return paint;
    }

    private Paint createBmpPaint() {
        Paint paint = createBasePaint();
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint createStrokePaint() {
        Paint paint = createBasePaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mStrokeColor);
        paint.setStrokeWidth(mStrokeWidth);
        return paint;
    }

    public PolygonTransformation(int edgeCount) {
        setEdgeCount(edgeCount);
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public void setPadding(int padding) {
        mPadding = padding;
    }

    private void setEdgeCount(int edgeCount) {
        if (edgeCount < 3)
            edgeCount = 3;
        this.mEdgeCount = edgeCount;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        //正n边形绘制思路:
        //矩形框内作一内切圆,将圆心角平均分成n个角度.
        //即:每两个相邻点之间对应的圆心角为360/n,依次旋转绘制点,再将各点连结.
        //圆参数方程:x = a + r * cos β , y = b + r * sin β
        final int width = toTransform.getWidth();
        final int height = toTransform.getHeight();
        final int size = Math.min(width, height);
        final float a = width / 2f;
        final float b = height / 2f;
        final float r = size / 2f;
        //输出位图
        Bitmap result = pool.get(width, height, toTransform.getConfig());
        //开始绘制
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清屏
        //绘制图像
        Paint bmpPaint = createBmpPaint();
        bmpPaint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawPath(createPolygonPath(a, b, r - mPadding - mStrokeWidth, width, height), bmpPaint);
        //绘制边框
        if (mStrokeWidth > 0 && Color.alpha(mStrokeColor) > 0) {
            Paint strokePaint = createStrokePaint();
            canvas.drawPath(createPolygonPath(a, b, r - mStrokeWidth, width, height), strokePaint);
        }
        mLastRadius = (int) r;//记录上一次的半径
//        String log = "====================================================\n" +
//                StringUtil.format("outWidth : %d, outHeight : %d\n", outWidth, outHeight) +
//                StringUtil.format("width : %d, height : %d, size : %d\n", width, height, size) +
//                "====================================================\n";
//        Log.d(getClass().getSimpleName(), log);
        return result;
    }

    private Path createPolygonPath(float cx, float cy, float radius, int width, int height) {
        final float perDegree = 360f / mEdgeCount;
        final float startX;
        final float startY;
        final float startDegree;
        if (mEdgeCount % 2 == 0) {
            startX = (float) (width / 2 - (Math.sin(Math.toRadians(perDegree / 2f)) * radius));
            startY = (float) (height / 2 - (Math.cos(Math.toRadians(perDegree / 2f)) * radius));
            startDegree = -90 - perDegree / 2f;
        } else {
            startX = width / 2;
            startY = 0;
            startDegree = -90f;
        }
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        float currDegree = startDegree;//正上方在坐标系里为-90°
        path.moveTo(startX, startY);//移动到内切圆正中最上方
        for (int i = 1; i < mEdgeCount; i++) {
            currDegree += perDegree;
            final double radiansAngle = Math.toRadians(currDegree);//由于Math工具方法里用的是弧度,这里要转成弧度
            final float x = (float) (cx + radius * Math.cos(radiansAngle));
            final float y = (float) (cy + radius * Math.sin(radiansAngle));
            path.lineTo(x, y);
        }
        path.lineTo(startX, startY);//最后回到起点,让图形闭合
        path.close();
        return path;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof PolygonTransformation;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(),
                Util.hashCode(getSubId().hashCode()));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(getSubId().getBytes());
    }

    private String getSubId() {
        return mEdgeCount + "_" + mStrokeWidth + "_" + mStrokeColor + "_" + mPadding + "_" + mCornerRadius + "_" + mLastRadius;
    }
}
