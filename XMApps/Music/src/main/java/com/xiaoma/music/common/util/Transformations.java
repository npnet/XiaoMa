package com.xiaoma.music.common.util;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class Transformations {
    private static final int DEFAULT_CORNER_RADIUS = 6;
    private static RoundedCorners sRoundedCorners;
    private static CircleCrop sCircleCrop;

    public static RoundedCorners getRoundedCorners() {
        if (sRoundedCorners == null) {
            synchronized (Transformations.class) {
                if (sRoundedCorners == null) {
                    sRoundedCorners = new RoundedCorners(DEFAULT_CORNER_RADIUS);
                }
            }
        }
        return sRoundedCorners;
    }

    public static CircleCrop getCircleCrop() {
        if (sCircleCrop == null) {
            synchronized (Transformations.class) {
                if (sCircleCrop == null) {
                    sCircleCrop = new CircleCrop();
                }
            }
        }
        return sCircleCrop;
    }
}
