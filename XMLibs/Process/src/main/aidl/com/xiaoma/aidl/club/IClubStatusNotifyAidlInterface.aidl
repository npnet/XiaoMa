package com.xiaoma.aidl.club;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MessageInfo;

interface IClubStatusNotifyAidlInterface {

    //如果所有未读消息来自同一个会话，则设置conversationId，若来自多个会话则传null
    void refreshIMUnReadMessageCount(inout MessageInfo messageInfo);

    //service主动通知Client当前播放的音乐名称
    void playMusicName(inout MusicInfo musicInfo);

}
