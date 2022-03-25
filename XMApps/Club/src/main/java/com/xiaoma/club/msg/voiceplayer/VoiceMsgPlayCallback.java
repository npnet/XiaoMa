package com.xiaoma.club.msg.voiceplayer;

import com.hyphenate.chat.EMMessage;

/**
 * Created by LKF on 2019-1-2 0002.
 */
public interface VoiceMsgPlayCallback {
    void onPlay(EMMessage message);

    void onStop(EMMessage message);

    void onComplete(EMMessage message);

    void onError(int code, String msg);
}
