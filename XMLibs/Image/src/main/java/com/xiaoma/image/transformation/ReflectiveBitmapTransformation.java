package com.xiaoma.image.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/22 0022 10:11
 *       desc：图片反光
 * </pre>
 */
public class ReflectiveBitmapTransformation extends BitmapTransformation {
    private Context mContext;
    @DrawableRes
    private int mDrawableId;
    private final String ID;
    private final byte[] ID_BYTES;

    public ReflectiveBitmapTransformation(Context context, @DrawableRes int drawableId) {
        this.mContext = context;
        this.mDrawableId = drawableId;
        ID = ReflectiveBitmapTransformation.class.getName() + "_" + drawableId;
        ID_BYTES = ID.getBytes(Charset.defaultCharset());
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (mDrawableId != 0) {
            Drawable dr = mContext.getDrawable(mDrawableId);
            if (dr != null) {
                dr.setBounds(0, 0, toTransform.getWidth(), toTransform.getHeight());
                Canvas canvas = new Canvas(toTransform);
                dr.draw(canvas);
            }
        }
        return toTransform;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ReflectiveBitmapTransformation)
                && ID.equals(((ReflectiveBitmapTransformation) obj).ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.digest(ID_BYTES);
    }
}
