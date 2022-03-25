package com.xiaoma.image;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;


/**
 * Created by sxj on 2018/8/20.
 */

public class ImageLoader {
    /**
     * 加载普通图片
     */
    public static RequestManager with(Context context) {
        return Glide.with(context);
    }

    public static RequestManager with(Activity activity) {
        return Glide.with(activity);
    }

    public static RequestManager with(Fragment fragment) {
        return Glide.with(fragment);
    }
}
