package com.xiaoma.assistant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Util;
import com.xiaoma.image.R;
import com.xiaoma.image.StringUtil;

import java.nio.charset.Charset;
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
    private static final String ID = SquareCornerLightTransForm.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));
    private final String subId;
    private final byte[] subIdBytes;

    private Context context;
    private int radius;
    private boolean highlight = true;
    private boolean clipCorner = true;
    private Drawable highLightDrawable;

    public SquareCornerLightTransForm(Context context, int radius) {
        this(context, radius, true);
    }

    public SquareCornerLightTransForm(Context context, int radius, boolean highlight) {
        this.context = context;
        this.radius = radius;
        this.highlight = highlight;

        subId = StringUtil.join("_",
                radius,
                highlight,
                clipCorner);
        subIdBytes = subId.getBytes();

        highLightDrawable = context.getResources().getDrawable(R.drawable.club_group_card_light, context.getTheme());
    }

    public SquareCornerLightTransForm(Context context, int radius, boolean highlight, Drawable highLightDrawable, boolean cutCorner) {
        this(context, radius, highlight);
        this.highLightDrawable = highLightDrawable;
        this.clipCorner = cutCorner;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (toTransform == null) return null;
        Bitmap result;
        if (clipCorner) {
            result = TransformationUtils.roundedCorners(pool, toTransform, radius);
        } else {
            result = pool.get(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.ARGB_8888);
            }
            drawBitmap(toTransform, result);
        }
        if (highlight) {
            // 覆盖高光
            final Canvas canvas = new Canvas(result);
            final int length = Math.min(result.getWidth(), result.getHeight());// 取边长
            highLightDrawable.setBounds(new Rect(0, 0, length, length));
            highLightDrawable.draw(canvas);
        }
        return result;
    }

    private void drawBitmap(@NonNull Bitmap toTransform, Bitmap result) {
        Bitmap copy = Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight());
        BitmapShader shader = new BitmapShader(copy, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);
        RectF rect = new RectF(0, 0, result.getWidth(), result.getHeight());
        Canvas canvas = new Canvas(result);
        canvas.drawRect(rect, paint);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SquareCornerLightTransForm;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(),
                Util.hashCode(subId.hashCode()));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(subIdBytes);
    }

    public void setHighLightDrawable(Drawable highLight) {
        this.highLightDrawable = highLight;
    }
}
