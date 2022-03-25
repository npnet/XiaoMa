package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/31
 */
@IntDef({PlayerAction.DEFAULT, PlayerAction.SET_LIST, PlayerAction.PLAY_LIST, PlayerAction.XM_SERVER})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerAction {

    int DEFAULT = 0;
    int SET_LIST = 1;
    int PLAY_LIST = 2;

    int XM_SERVER = 3;
}
