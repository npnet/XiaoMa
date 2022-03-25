package com.xiaoma.aidl.xting;

import com.xiaoma.aidl.model.MusicInfo;

interface IXTingPlayerNotifyAidlInterface {

    //service主动通知Client当前播放的音乐名称
    void playMusicName(inout MusicInfo musicInfo);

}
