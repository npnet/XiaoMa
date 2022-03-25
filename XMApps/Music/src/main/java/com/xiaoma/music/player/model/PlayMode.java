package com.xiaoma.music.player.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zs
 * @date 2018/11/2 0002.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({PlayMode.LIST_ORDER, PlayMode.LIST_LOOP, PlayMode.SINGLE_LOOP, PlayMode.RANDOM})
public @interface PlayMode {

    //顺序播放
    int LIST_ORDER = 1;
    //循环播放
    int LIST_LOOP = 2;
    //单曲循环
    int SINGLE_LOOP = 0;
    //随机播放
    int RANDOM = 3;
}
