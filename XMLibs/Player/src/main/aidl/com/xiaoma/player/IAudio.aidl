package com.xiaoma.player;

import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.AudioStatusChangeListener;
import com.xiaoma.player.ProgressInfo;

interface IAudio {
    void control(int option);

    int getAudioStatus();

    AudioInfo getAudioInfo();

    ProgressInfo getProgressInfo();

    void onAudioUpdate(IAudio cur);

    boolean getFavoriteStatus();

    void listenAudioStatus(AudioStatusChangeListener listener);
}
