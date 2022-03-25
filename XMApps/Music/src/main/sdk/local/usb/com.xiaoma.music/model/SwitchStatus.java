package com.xiaoma.music.model;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zs
 * @date 2018/11/2 0002.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({SwitchStatus.PLAY, SwitchStatus.PAUSE, SwitchStatus.PRE, SwitchStatus.NEXT})
public @interface SwitchStatus {
    int PLAY = 0;
    int PAUSE = 1;
    int PRE = 2;
    int NEXT = 3;
}
