package com.xiaoma.club.msg.hyphenate;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by LKF on 2018/10/10 0010.
 */
public class SimpleMessageListener implements EMMessageListener {
    /**
     * 收到消息
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {

    }

    /**
     * 收到透传消息
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    /**
     * 收到已读回执
     */
    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    /**
     * 收到已送达回执
     */
    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    /**
     * 消息被撤回
     */
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    /**
     * 消息状态变动
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }
}
