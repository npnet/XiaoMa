package com.xiaoma.xting.common.playerSource.utils;

import android.util.Log;

import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/31
 */
public class PlayerSourceUtils {

    private PlayerSourceUtils() {

    }

    public static int switchPlayMode() {
        int playMode = PlayerSourceFacade.newSingleton().getPlayMode();
        if (playMode == PlayerPlayMode.SEQUENTIAL) {
            playMode = PlayerPlayMode.SINGLE_LOOP;
        } else if (playMode == PlayerPlayMode.SINGLE_LOOP) {
            playMode = PlayerPlayMode.SHUFFLE;
        } else if (playMode == PlayerPlayMode.SHUFFLE) {
            playMode = PlayerPlayMode.SEQUENTIAL;
        } else {
            Log.d("OnlineClient", "onPlayModeChange: Error mode");
            return PlayerPlayMode.SEQUENTIAL;
        }
        PlayerSourceFacade.newSingleton().setPlayMode(playMode);
        return playMode;
    }

}
