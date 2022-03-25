package com.xiaoma.xting.sdk.utils;


import com.xiaoma.xting.online.model.PlayMode;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * @author youthyJ
 * @date 2018/10/17
 */
public class PlayModeConverter {
    public static XmPlayListControl.PlayMode conver(PlayMode playMode) {
        if (playMode == null) {
            return null;
        }
        switch (playMode) {
            case SINGLE:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE;
            case SINGLE_LOOP:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
            case LIST:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
            case LIST_LOOP:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case RANDOM:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
        }
        return null;
    }
}
