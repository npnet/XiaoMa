package com.xiaoma.xting.sdk.utils;

import com.xiaoma.xting.online.model.PlayerState;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;

/**
 * @author youthyJ
 * @date 2018/10/17
 */
public class PlayerStatusConverter {
    public static PlayerState conver(int playerState) {
        if (PlayerConstants.STATE_IDLE == playerState) {
            return PlayerState.IDLE;
        }
        if (PlayerConstants.STATE_INITIALIZED == playerState) {
            return PlayerState.INITIALIZED;
        }
        if (PlayerConstants.STATE_PREPARING == playerState) {
            return PlayerState.PREPARING;
        }
        if (PlayerConstants.STATE_PREPARED == playerState) {
            return PlayerState.PREPARED;
        }
        if (PlayerConstants.STATE_STARTED == playerState) {
            return PlayerState.STARTED;
        }
        if (PlayerConstants.STATE_STOPPED == playerState) {
            return PlayerState.STOPPED;
        }
        if (PlayerConstants.STATE_PAUSED == playerState) {
            return PlayerState.PAUSED;
        }
        if (PlayerConstants.STATE_COMPLETED == playerState) {
            return PlayerState.COMPLETED;
        }
        if (PlayerConstants.STATE_ERROR == playerState) {
            return PlayerState.ERROR;
        }
        if (PlayerConstants.STATE_END == playerState) {
            return PlayerState.END;
        }
        return null;
    }
}
