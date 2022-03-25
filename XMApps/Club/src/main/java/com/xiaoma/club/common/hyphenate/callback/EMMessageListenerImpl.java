package com.xiaoma.club.common.hyphenate.callback;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 消息侦听接口，可以用来侦听消息接受情况，成功发送到对方手机后会有回执，
 * 对方阅读了这条消息也会收到回执。 发送消息过程中，消息的ID会发生改变，
 * 由最初本地生成的一个UUID，变更为服务器端生成的全局唯一的ID，
 * 这个ID在所有使用Hyphenate SDK的 设备上都是唯一的。 应用需要实现此接口来监听消息变更状态.
 */
public class EMMessageListenerImpl implements EMMessageListener {

    private static class EMMessageListenerImplHolder{
        static final EMMessageListenerImpl instance = new EMMessageListenerImpl();
    }

    public static EMMessageListenerImpl getInstance(){
        return EMMessageListenerImplHolder.instance;
    }

    /**
     * 接受消息接口，在接受到文本消息，图片，视频，语音，地理位置，文件这些消息体的时候，会通过此接口通知用户。
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {

    }

    /**
     * 区别于EMMessageListener#onMessageReceived(List), 这个接口只包含命令的消息体，包含命令的消息体通常不对用户展示。
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    /**
     * 接受到消息体的已读回执, 消息的接收方已经阅读此消息。
     * @param messages
     */
    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    /**
     * 收到消息体的发送回执，消息体已经成功发送到对方设备。
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    /**
     * 收到消息体的撤回回调，消息体已经成功撤回。
     * @param messages
     */
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    /**
     * 接受消息发生改变的通知，包括消息ID的改变。消息是改变后的消息。
     * @param message 发生改变的消息
     * @param change
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change ) {

    }
}
