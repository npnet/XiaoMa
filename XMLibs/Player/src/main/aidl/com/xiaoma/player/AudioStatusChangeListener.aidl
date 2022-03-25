package com.xiaoma.player;

import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;

interface AudioStatusChangeListener {
    void onAudioFavoritStatusChanged(boolean favorited);

    void onAudioStatusChanged(int status);

    void onAudioInfoChanged(inout AudioInfo info);

    void onAudioProgressChanged(inout ProgressInfo info);
}
