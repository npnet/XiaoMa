package com.xiaoma.aidl.xting;

import com.xiaoma.aidl.xting.IXTingPlayerNotifyAidlInterface;

interface IXTingPlayerAidlInterface {

    boolean registerStatusNotify(IXTingPlayerNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterStatusNotify(IXTingPlayerNotifyAidlInterface notifyAidlInterface);

     //正在播放的歌曲
    void getCurrPlayingMusicName();

    //播放前一首
    void playPreMusic();

    //播放下一首
    void playNextMusic();

    //切换播放状态
    void switchPlay(boolean play);

    //切换播放模式
    void setPlayMode(int mode);

    void setPlayModeRandom();

    //订阅/取消订阅当前节目
    void subscribeProgram();

    void unSubscribeProgram();

}
