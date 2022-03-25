package com.xiaoma.process.listener;

import com.xiaoma.aidl.model.MusicInfo;

public interface IPlayInfoChangeListener {

    void onPlayStatusChange(MusicInfo musicInfo);

}
