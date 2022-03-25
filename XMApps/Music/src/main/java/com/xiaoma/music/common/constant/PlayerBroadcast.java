package com.xiaoma.music.common.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.music.common.constant.PlayerBroadcast.Action.ONLINE_PLAY_MODE;
import static com.xiaoma.music.common.constant.PlayerBroadcast.Action.PLAYER_CONTROL;
import static com.xiaoma.music.common.constant.PlayerBroadcast.Action.USB_PLAY_MODE;
import static com.xiaoma.music.common.constant.PlayerBroadcast.Command.PAUSE;
import static com.xiaoma.music.common.constant.PlayerBroadcast.Command.PLAY;
import static com.xiaoma.music.common.constant.PlayerBroadcast.Extra.CONTROL_CMD;

/**
 * Created by LKF on 2018-12-24 0024.
 */
public class PlayerBroadcast {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PLAYER_CONTROL,ONLINE_PLAY_MODE,USB_PLAY_MODE})
    public @interface Action {
        String PLAYER_CONTROL = "com.xiaoma.music.player_control";
        String ONLINE_PLAY_MODE="com.xiaoma.music.online_play_mode";
        String USB_PLAY_MODE ="com.xiaoma.music.usb_play_mode";
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({CONTROL_CMD})
    public @interface Extra {
        String CONTROL_CMD = "player_control_cmd";
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PLAY, PAUSE})
    public @interface Command {
        String PLAY = "play";
        String PAUSE = "pause";
    }
}