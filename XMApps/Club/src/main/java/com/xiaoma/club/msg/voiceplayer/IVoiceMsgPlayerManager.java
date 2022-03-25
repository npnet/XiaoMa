package com.xiaoma.club.msg.voiceplayer;

import android.content.Context;

import com.hyphenate.chat.EMMessage;

/**
 * Created by LKF on 2018-12-29 0029.
 */
public interface IVoiceMsgPlayerManager {
    void init(Context context);

    void release();

    void play(EMMessage message);

    void stop();

    boolean isPlaying();

    EMMessage getCurrMessage();

    void addVoiceMsgPlayCallback(VoiceMsgPlayCallback callback);

    void removeVoiceMsgPlayCallback(VoiceMsgPlayCallback callback);
}
