package com.xiaoma.utils.media;

import android.media.MediaPlayer;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:
 */
public interface OnInfoListener {
    boolean onInfo(MediaPlayer mp, int what, int extra);
}
