package com.xiaoma.motorcade.common.locationshare;


import android.content.Context;

/**
 * Created by LKF on 2017/5/10 0010.
 */

public class LocationSharerFactory {
    public static ILocationSharer makeLocationSharer(Context context, final String chatId) {
        return new LocationSharer(context, chatId);
    }
}
