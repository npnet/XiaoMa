package com.xiaoma.process.listener;

import com.xiaoma.aidl.model.MessageInfo;

public interface IMsgChangeListener {

    void unReadMessageCountChange(MessageInfo messageInfo);

}
