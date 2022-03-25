package com.xiaoma.club.msg.chat.constant;

import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.msg.chat.model.MessageStatusResult;

/**
 * Created by LKF on 2019-1-3 0003.
 * 因为环信端在主动发消息时,不走消息回调,所以我们自己通过EventBus来监听发消息的行为
 */
public interface ChatMsgEventTag {
    /**
     * 发送消息
     * void fun({@link EMMessage} message)
     */
    String SEND_MESSAGE = "sendMessage";

    /**
     * 发送消息的状态变化
     * void fun({@link MessageStatusResult} message)
     */
    String SEND_MESSAGE_STATUS_CHANGED = "sendMessageStatusChanged";

    /**
     * 当前用户撤回已发送的消息
     * void fun({@link EMMessage} message)
     */
    String RECALL_MESSAGE = "recallMessage";

    /**
     * 保存一条消息到本地,仅保存到本地数据库,其他用户收不到.
     * 要注意和"SEND_MESSAGE"区别开来.
     * void fun({@link EMMessage} message)
     */
    String SAVE_MESSAGE = "saveMessage";
}
