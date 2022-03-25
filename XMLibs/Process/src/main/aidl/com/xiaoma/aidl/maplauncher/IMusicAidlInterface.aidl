package com.xiaoma.aidl.maplauncher;

import com.xiaoma.aidl.maplauncher.IMusicNotifyAidlInterface;

interface IMusicAidlInterface {

    String getPlayingMusicName();

    void play(String musicName,String singer);

    void pause();

    void stop();

    void registerPlayingInfo(IMusicNotifyAidlInterface notifyAidlInterface);

    void unRegisterPlayingInfo(IMusicNotifyAidlInterface notifyAidlInterface);

}
