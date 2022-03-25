package com.xiaoma.ui.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.xiaoma.utils.reflect.Reflect;

/**
 * Created by LKF on 2019-2-13 0013.
 * 提供自定义ScrollBar的辅助类
 */
public class XMScrollBarHelper {
    private static final String TAG = XMScrollBarHelper.class.getSimpleName();
    private boolean mIsVertical;
    private Rect mScrollBarBounds;
    private View mApplyView;
    private int mSize;

    /**
     * ScrollBar的显示区域为系统默认区域
     *
     * @param size ScrollBar的size: 如果是vertical则表示高度;否则表示宽度
     */
    public XMScrollBarHelper(boolean isVertical, int size) {
        mIsVertical = isVertical;
        mSize = size;
    }

    /**
     * 自定义ScrollBar显示区域
     *
     * @param scrollBarBounds 指定ScrollBar的显示区域
     */
    public XMScrollBarHelper(boolean isVertical, Rect scrollBarBounds) {
        mIsVertical = isVertical;
        mScrollBarBounds = scrollBarBounds;
    }

    public void apply(View v) {
        if (mApplyView == v)
            return;
        try {
            final Object scrollCache = Reflect.on(View.class)
                    .method("getScrollCache")
                    .invoke(v);
            final Drawable scrollBar = (Drawable) Reflect.on(scrollCache.getClass())
                    .field("scrollBar")
                    .get(scrollCache);
            final String trackField;
            final String thumbField;
            if (mIsVertical) {
                trackField = "mVerticalTrack";
                thumbField = "mVerticalThumb";
            } else {
                trackField = "mHorizontalTrack";
                thumbField = "HorizontalThumb";
            }
            final Reflect scrollBarDrawableReflect = Reflect.on(scrollBar.getClass());

            final Reflect.ReflectField trackF = scrollBarDrawableReflect.field(trackField);
            final Drawable originTrack = (Drawable) trackF.get(scrollBar);
            trackF.set(scrollBar, new ScrollBarDrawable(trackField, originTrack, true));

            final Reflect.ReflectField thumbF = scrollBarDrawableReflect.field(thumbField);
            final Drawable originThumb = (Drawable) thumbF.get(scrollBar);
            thumbF.set(scrollBar, new ScrollBarDrawable(thumbField, originThumb, false));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mApplyView = v;
        }
    }

    private final Rect mOriginTrackBounds = new Rect();
    private final Rect mOriginThumbBounds = new Rect();

    private class ScrollBarDrawable extends Drawable {
        private String mName;
        private Drawable mDrawableInner;
        private boolean mIsTrack;

        ScrollBarDrawable(String name, Drawable drawableInner, boolean isTrack) {
            mName = name;
            mDrawableInner = drawableInner;
            mIsTrack = isTrack;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            final Rect currBounds = new Rect(getBounds());// 先默认为系统给定的Bounds
            Log.e(TAG, String.format("%s -> draw: [ CanvasBounds: %s, DrawableBounds: %s ]", mName, canvas.getClipBounds(), currBounds));
            final Drawable dr = mDrawableInner;
            if (dr != null) {
                Rect newBounds = mScrollBarBounds;
                if (newBounds == null && mSize > 0) {
                    newBounds = new Rect(mIsTrack ? currBounds : mOriginTrackBounds);
                    if (mIsVertical) {
                        final int sizeOffset = ((newBounds.bottom - newBounds.top) - mSize) / 2;
                        newBounds.top += sizeOffset;
                        newBounds.bottom -= sizeOffset;
                    } else {
                        final int sizeOffset = ((newBounds.right - newBounds.left) - mSize) / 2;
                        newBounds.left += sizeOffset;
                        newBounds.right -= sizeOffset;
                    }
                }
                if (newBounds != null) {
                    if (mIsTrack) {
                        mOriginTrackBounds.set(currBounds);
                        currBounds.set(newBounds);
                    } else {
                        mOriginThumbBounds.set(currBounds);
                        if (mIsVertical) {
                            currBounds.left = newBounds.left;
                            currBounds.right = newBounds.right;
                            // 重新计算Thumb的Top和Height
                            final int originThumbTop = mOriginThumbBounds.top;
                            final int originThumbH = mOriginThumbBounds.bottom - originThumbTop;
                            final int originTrackH = mOriginTrackBounds.bottom - mOriginTrackBounds.top;
                            final int newTrackH = newBounds.bottom - newBounds.top;
                            final int newThumbTop = newTrackH * originThumbTop / originTrackH;
                            final int newThumbH = newTrackH * originThumbH / originTrackH;
                            currBounds.top = newBounds.top + newThumbTop;
                            currBounds.bottom = currBounds.top + newThumbH;
                        } else {
                            currBounds.top = newBounds.top;
                            currBounds.bottom = newBounds.bottom;
                            // 重新计算Thumb的Left和Width
                            final int originThumbLeft = mOriginThumbBounds.left;
                            final int originThumbW = mOriginThumbBounds.right - originThumbLeft;
                            final int originTrackW = mOriginTrackBounds.right - mOriginTrackBounds.left;
                            final int newTrackW = newBounds.right - newBounds.left;
                            final int newThumbLeft = newTrackW * originThumbLeft / originTrackW;
                            final int newThumbW = newTrackW * originThumbW / originTrackW;
                            currBounds.left = newBounds.left + newThumbLeft;
                            currBounds.right = currBounds.left + newThumbW;
                        }
                    }
                }
                dr.setBounds(currBounds);
                dr.draw(canvas);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            if (mDrawableInner != null)
                mDrawableInner.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            if (mDrawableInner != null)
                mDrawableInner.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return mDrawableInner != null ? mDrawableInner.getOpacity() : PixelFormat.UNKNOWN;
        }
    }
}
