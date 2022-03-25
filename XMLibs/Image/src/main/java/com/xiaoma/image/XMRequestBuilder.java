package com.xiaoma.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

/**
 * Created by LKF on 2019-3-7 0007.
 */
public class XMRequestBuilder<TranscodeType> extends RequestBuilder<TranscodeType> {
    private static final String TAG = "XMRequestBuilder";
    private Context mContext;

    public XMRequestBuilder(@NonNull Glide glide, RequestManager requestManager, Class<TranscodeType> transcodeClass, Context context) {
        super(glide, requestManager, transcodeClass, context);
        mContext = context;
    }

    @NonNull
    @Override
    public RequestBuilder<TranscodeType> load(@Nullable Integer resourceId) {
        if (resourceId != null) {
            Drawable dr = getDrawableByResId(resourceId);
            if (dr != null) {
                return super.load(dr);
            }
        }
        return super.load(resourceId);
    }

    @NonNull
    @Override
    public RequestBuilder<TranscodeType> placeholder(int resourceId) {
        Drawable dr = getDrawableByResId(resourceId);
        if (dr != null) {
            return super.placeholder(dr);
        }
        return super.placeholder(resourceId);
    }

    @NonNull
    @Override
    public RequestBuilder<TranscodeType> error(int resourceId) {
        Drawable dr = getDrawableByResId(resourceId);
        if (dr != null) {
            return super.error(dr);
        }
        return super.error(resourceId);
    }

    @NonNull
    @Override
    public RequestBuilder<TranscodeType> fallback(int resourceId) {
        Drawable dr = getDrawableByResId(resourceId);
        if (dr != null) {
            return super.fallback(dr);
        }
        return super.fallback(resourceId);
    }

    private Drawable getDrawableByResId(int resId) {
        // 由于换肤之后,当前Context的Resource可能会被替换
        // 因此,假如无法通过当前Context获取到对应的Drawable,则从AppContext中获取
        Drawable dr = mContext.getDrawable(resId);
        if (dr == null) {
            dr = mContext.getApplicationContext().getDrawable(resId);
        }
        return dr;
    }
}
