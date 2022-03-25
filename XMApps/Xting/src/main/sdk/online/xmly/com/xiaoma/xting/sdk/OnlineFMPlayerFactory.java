package com.xiaoma.xting.sdk;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public class OnlineFMPlayerFactory {
    private OnlineFMPlayerFactory() throws Exception {
        throw new Exception();
    }

    public static OnlineFMPlayer getPlayer() {
        return XmlyPlayer.getInstance();
    }
}
