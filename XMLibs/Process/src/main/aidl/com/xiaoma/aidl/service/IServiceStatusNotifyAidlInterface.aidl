package com.xiaoma.aidl.service;

import com.xiaoma.aidl.model.MessageInfo;

interface IServiceStatusNotifyAidlInterface {

    //如果所有未读消息来自同一个会话，则设置conversationId，若来自多个会话则传null
    void refreshIMUnReadMessageCount(inout MessageInfo messageInfo);

}
