package com.xiaoma.skin.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.lang.ref.SoftReference;

/**
 * Created by Thomas on 2018/12/29 0029
 * app window drawable using
 */

public class XmWindowBgDrawable extends Drawable {

    private static final String TAG = "XmWindowBgDrawable";
    private SoftReference<Bitmap> mBmpRef;
    private final Paint mPaint = new Paint();

    public XmWindowBgDrawable() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            Log.i(TAG, "draw() DrawBitmap called");
            canvas.drawBitmap(bitmap, 0f, 0f, mPaint);
        } else {
            Log.e(TAG, "draw() Bmp is null");
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    /**
     * 可以基于sdcard策略获取Bitmap返回
     * @return
     */
    private Bitmap getBitmap() {
        Bitmap bmp = mBmpRef != null ? mBmpRef.get() : null;
        if (bmp == null) {
            bmp = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(), "bg_common.png").getPath());
            mBmpRef = new SoftReference<>(bmp);
        }
        return bmp;
    }
}
