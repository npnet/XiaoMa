package com.xiaoma.aidl.club;

import com.xiaoma.aidl.club.IClubStatusNotifyAidlInterface;

interface IClubStatusAidlInterface {

    boolean registerStatusNotify(IClubStatusNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterStatusNotify(IClubStatusNotifyAidlInterface notifyAidlInterface);

    //正在播放的歌曲
    void getCurrPlayingMusicName();

    //播放前一首
    void playPreMusic();

    //播放下一首
    void playNextMusic();

    //切换播放状态
    void switchPlay(boolean play);

    //同步消息数
    void searchUnReadMessageCount();

}
