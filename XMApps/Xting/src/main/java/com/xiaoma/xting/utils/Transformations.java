package com.xiaoma.xting.utils;

import android.content.Context;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.R;

public class Transformations {
    private static RoundedCorners sRoundedCorners;

    public static RoundedCorners getRoundedCorners() {
        if (sRoundedCorners == null) {
            synchronized (Transformations.class) {
                if (sRoundedCorners == null) {
                    sRoundedCorners = new RoundedCorners(
                            AppHolder.getInstance().getAppContext()
                                    .getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_radius));
                }
            }
        }
        return sRoundedCorners;
    }
}
