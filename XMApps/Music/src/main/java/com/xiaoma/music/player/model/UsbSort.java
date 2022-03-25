package com.xiaoma.music.player.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zs
 * @date 2018/12/18 0018.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({UsbSort.DEFAULT, UsbSort.GENRE, UsbSort.ARTIST})
public @interface UsbSort {
    int DEFAULT = 0;
    int GENRE = 1;
    int ARTIST = 2;
}
