package com.xiaoma.club.msg.hyphenate;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.msg.chat.constant.ChatMsgEventTag;
import com.xiaoma.club.msg.chat.model.MessageStatusResult;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;

import static com.xiaoma.club.common.util.LogUtil.logE;
import static com.xiaoma.club.common.util.LogUtil.logI;

/**
 * Created by LKF on 2018-12-29 0029.
 * 环信消息状态基础回调
 * 注意: 环信的回调是在子线程里的
 */
public class MessageStatusCallback implements EMCallBack {
    private static final String TAG = "MessageStatusCallback";
    private final EMMessage mMessage;

    public MessageStatusCallback(EMMessage message) {
        if (message == null) {
            throw new NullPointerException("< message > cannot be null !");
        }
        mMessage = message;
    }

    @Override
    public void onSuccess() {
        logI(TAG, "onSuccess()");
        mMessage.setStatus(EMMessage.Status.SUCCESS);
        mMessage.setProgress(100);
        dispatchMessageChanged();
    }

    @Override
    public void onError(int code, String message) {
        logE(TAG, StringUtil.format("onError( code: %d, message: %s )", code, message));
        mMessage.setStatus(EMMessage.Status.FAIL);
        mMessage.setProgress(0);
        dispatchMessageChanged(code, message);
    }

    @Override
    public void onProgress(int progress, String status) {
        logI(TAG, StringUtil.format("MsgStatus #onProgress -> [ progress: %d, status: %s ]", progress, status));
        dispatchMessageChanged();
    }

    private void dispatchMessageChanged() {
        dispatchMessageChanged(0, "");
    }

    private void dispatchMessageChanged(int code, String message) {
        // 注意:此处调用的update不会触发环信的消息变化回调,需要自行同步该变化行为
        EMClient.getInstance().chatManager().updateMessage(mMessage);
        EventBus.getDefault().post(new MessageStatusResult(mMessage, code, message), ChatMsgEventTag.SEND_MESSAGE_STATUS_CHANGED);
    }
}
