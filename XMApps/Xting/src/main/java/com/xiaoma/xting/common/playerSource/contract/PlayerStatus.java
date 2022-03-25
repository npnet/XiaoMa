package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */

@IntDef({PlayerStatus.DEFAULT, PlayerStatus.LOADING, PlayerStatus.PAUSE,
        PlayerStatus.PLAYING, PlayerStatus.ERROR, PlayerStatus.ERROR_BY_PLAYER, PlayerStatus.NONE, PlayerStatus.ERROR_BY_DATA_SOURCE})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerStatus {
    int DEFAULT = 0; //无状态  用于本地电台
    int LOADING = 1;
    int PLAYING = 2;
    int PAUSE = 3;
    int ERROR = 4;
    int ERROR_BY_PLAYER = 5;
    int ERROR_BY_DATA_SOURCE = 6;
    int NONE = 20;
}
