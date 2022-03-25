package com.xiaoma.aidl.maplauncher;

import com.xiaoma.aidl.model.MusicInfo;

interface IMusicNotifyAidlInterface {

    /**
     * Service 通知当前正在播放的音乐信息
     */
    void playingInfo(inout MusicInfo musicInfo);

}
