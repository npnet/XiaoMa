package com.xiaoma.wechat.callback;

import com.xiaoma.wechat.model.WeChatContact;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/23 10:43
 * Desc:
 */
public class SimpleWeChatCallback implements WeChatCallback{

    @Override
    public void onContactChanged(String cookie, List<WeChatContact> contacts) {

    }

    @Override
    public void onSendMessageResult(String messageId, boolean success) {

    }

    @Override
    public void onLoginStateChanged(boolean logined) {

    }

    @Override
    public void onPageVisibilityChanged(boolean mainVisiable, boolean contactVisiable, boolean conversionViaiable) {

    }

    @Override
    public void onKeyEventResult(int keyEvent, boolean success) {

    }

}
