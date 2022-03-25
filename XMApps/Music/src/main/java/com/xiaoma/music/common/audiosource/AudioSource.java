package com.xiaoma.music.common.audiosource;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.music.common.audiosource.AudioSource.BLUETOOTH_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.NONE;
import static com.xiaoma.music.common.audiosource.AudioSource.ONLINE_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.USB_MUSIC;

/**
 * Created by LKF on 2018-12-21 0021.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({NONE, ONLINE_MUSIC, USB_MUSIC, BLUETOOTH_MUSIC})
public @interface AudioSource {
    int NONE = 0;
    int ONLINE_MUSIC = 1;
    int USB_MUSIC = 2;
    int BLUETOOTH_MUSIC = 3;
}
