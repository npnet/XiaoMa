package com.xiaoma.app.util;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class Transformations {
    private static RoundedCorners sRoundedCorners;

    public static RoundedCorners getRoundCorners() {
        if (sRoundedCorners == null) {
            synchronized (Transformations.class) {
                if (sRoundedCorners == null) {
                    sRoundedCorners = new RoundedCorners(5);
                }
            }
        }
        return sRoundedCorners;
    }
}