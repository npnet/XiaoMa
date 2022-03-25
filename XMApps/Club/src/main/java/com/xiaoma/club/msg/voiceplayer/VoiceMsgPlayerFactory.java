package com.xiaoma.club.msg.voiceplayer;

/**
 * Created by LKF on 2018-12-29 0029.
 */
public class VoiceMsgPlayerFactory {
    public static IVoiceMsgPlayerManager createPlayer() {
        return new VoiceMsgPlayerManagerImpl();
    }
}
