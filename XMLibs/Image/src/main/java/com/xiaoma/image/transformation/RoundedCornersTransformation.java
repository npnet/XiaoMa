package com.xiaoma.image.transformation;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created on 2017/7/10.
 *
 * @author chen_ping
 * Email yy_cping@163.com
 * Edit androidStudio
 * desc : 将图片转化为给类的特殊圆角图片
 */
public class RoundedCornersTransformation extends BitmapTransformation {
    private float mRadius;
    private CornerType mCornerType;
    private static final int VERSION = 1;
    private final String ID;
    private final byte[] ID_BYTES;

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        TOP_LEFT_BOTTOM_RIGHT,
        TOP_RIGHT_BOTTOM_LEFT,
        TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT,
        TOP_RIGHT_BOTTOM_RIGHT_BOTTOM_LEFT,
        DEFAULT,
    }

    public RoundedCornersTransformation(float radius, CornerType cornerType) {
        super();
        mRadius = radius;
        mCornerType = cornerType;
        ID = getClass().getName() + "_" + radius + "_" + cornerType;
        ID_BYTES = ID.getBytes(Key.CHARSET);
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(toTransform);
    }


    private Bitmap roundCrop(Bitmap toTransform) {
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(
                toTransform,
                BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(toTransform);
        drawRoundRect(canvas, paint, new Path(), toTransform.getWidth(), toTransform.getHeight());
        return toTransform;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, Path path, int width, int height) {
        float[] rids;
        switch (mCornerType) {
            case ALL:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM_RIGHT:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case LEFT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case RIGHT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT_BOTTOM_RIGHT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT_BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT_BOTTOM_RIGHT_BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case DEFAULT:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            default:
                throw new RuntimeException("RoundedCorners type not belong to CornerType");
        }
    }


    /**
     * @param rids 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角
     */
    private void drawPath(float[] rids, Canvas canvas, Paint paint, Path path, int width, int height) {
        path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
//        canvas.clipPath(path);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundedCornersTransformation
                && ID.equals(((RoundedCornersTransformation) o).ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}