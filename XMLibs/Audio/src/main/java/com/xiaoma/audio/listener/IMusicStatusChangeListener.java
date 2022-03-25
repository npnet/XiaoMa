package com.xiaoma.audio.listener;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.audio.model.ForegroundAudioApp;

public interface IMusicStatusChangeListener {

    void onPlayStatusChanged(ForegroundAudioApp foregroundAudioApp, MusicInfo musicInfo);

}
