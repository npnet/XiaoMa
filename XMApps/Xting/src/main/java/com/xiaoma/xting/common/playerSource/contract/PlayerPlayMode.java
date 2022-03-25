package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/30
 */
@IntDef({PlayerPlayMode.SEQUENTIAL, PlayerPlayMode.LIST_LOOP,
        PlayerPlayMode.SINGLE_LOOP, PlayerPlayMode.SHUFFLE})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerPlayMode {

    int SEQUENTIAL = 0;
    int SINGLE_LOOP = 1;
    int SHUFFLE = 2;
    int LIST_LOOP = 3;
}
