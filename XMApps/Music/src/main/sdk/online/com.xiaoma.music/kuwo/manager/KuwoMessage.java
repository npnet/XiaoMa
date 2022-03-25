package com.xiaoma.music.kuwo.manager;

import cn.kuwo.core.messagemgr.IObserverBase;
import cn.kuwo.core.messagemgr.MessageID;
import cn.kuwo.core.messagemgr.MessageManager;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/8/31
 *  description :
 * </pre>
 */
public class KuwoMessage {

    private MessageManager mKuwoMessage;

    public KuwoMessage() {
        mKuwoMessage = MessageManager.getInstance();
    }

    public void attachMsg(MessageID messageID, IObserverBase observerBase) {
        mKuwoMessage.attachMessage(messageID, observerBase);
    }

    public void detachMsg(MessageID messageID, IObserverBase observerBase) {
        mKuwoMessage.detachMessage(messageID, observerBase);
    }
}
