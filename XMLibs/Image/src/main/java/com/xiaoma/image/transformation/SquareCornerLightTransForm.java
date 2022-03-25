package com.xiaoma.image.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.xiaoma.image.R;

import java.security.MessageDigest;

/**
 * Author: loren
 * Date: 2019/2/13 0013
 * <p>
 * 裁剪图片中心最大正方形
 * 自定义圆角
 * 添加高光
 * 无倒影
 * 参考车信部落、车友item
 */

public class SquareCornerLightTransForm extends BitmapTransformation {
    private static final int HIGHLIGHT_RES = R.drawable.club_group_card_light;
    private final String ID;
    private final byte[] ID_BYTES;

    private Context context;
    private int radius;

    public SquareCornerLightTransForm(Context context, int radius) {
        this.context = context;
        this.radius = radius;
        ID = getClass().getName() + "_" + radius;
        ID_BYTES = ID.getBytes(Key.CHARSET);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (radius >= 0) {
            toTransform = TransformationUtils.roundedCorners(pool, toTransform, radius);
        }
        Drawable dr = context.getDrawable(HIGHLIGHT_RES);
        dr.setBounds(0, 0, toTransform.getWidth(), toTransform.getHeight());
        Canvas canvas = new Canvas(toTransform);
        dr.draw(canvas);
        return toTransform;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof SquareCornerLightTransForm)
                && ID.equals(((SquareCornerLightTransForm) o).ID);
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
