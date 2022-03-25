package com.xiaoma.wechat.callback;

import com.xiaoma.wechat.model.WeChatContact;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/22 17:48
 * Desc:
 */
public interface WeChatCallback {

    /**
     * 联系人列表回调
     *
     * @param cookie   获取头像时需要设置的cookie
     * @param contacts 联系人列表
     */
    void onContactChanged(String cookie, List<WeChatContact> contacts);

    /**
     * 发送消息结果回调
     *
     * @param messageId 消息id
     * @param success   是否发送成功
     */
    void onSendMessageResult(String messageId, boolean success);

    /**
     * 登陆状态变化回调
     *
     * @param logined 是否已登录
     */
    void onLoginStateChanged(boolean logined);

    /**
     * 界面可见性变化回调
     *
     * @param mainVisiable       微信界面是否可见
     * @param contactVisiable    联系人是否可见
     * @param conversionViaiable 会话界面是否可见
     */
    void onPageVisibilityChanged(boolean mainVisiable, boolean contactVisiable, boolean conversionViaiable);

    /**
     * 转发按键事件结果回调
     *
     * @param keyEvent 按键事件
     * @param success  是否成功
     */
    void onKeyEventResult(int keyEvent, boolean success);

}
