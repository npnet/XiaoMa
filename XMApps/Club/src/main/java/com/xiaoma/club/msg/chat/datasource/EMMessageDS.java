package com.xiaoma.club.msg.chat.datasource;

import android.arch.paging.ItemKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.conversation.util.ConversationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LKF on 2018-12-27 0027.
 */
public class EMMessageDS extends ItemKeyedDataSource<String, EMMessage> {
    private static final String TAG = "EMMessageDS";
    private static final long MSG_TIME_INTERVAL = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
    private Context mContext;
    private EMConversation mConversation;

    public EMMessageDS(Context context, EMConversation conversation) {
        mContext = context;
        mConversation = conversation;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<EMMessage> callback) {
        List<EMMessage> messages = null;
        if (mConversation != null) {
            messages = mConversation.getAllMessages();
            if (messages != null && !messages.isEmpty()
                    && messages.size() < params.requestedLoadSize) {
                final List<EMMessage> appendMessages = mConversation.loadMoreMsgFromDB(messages.get(0).getMsgId(), params.requestedLoadSize - messages.size());
                if (appendMessages != null) {
                    messages.addAll(0, appendMessages);
                }
            }
        }
        messages = ensureMessages(messages, true);
        LogUtil.logI(TAG, String.format("loadInitial() size: %s", messages.size()));
        callback.onResult(messages);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<EMMessage> callback) {
        List<EMMessage> messages = null;
        if (mConversation != null) {
            messages = mConversation.loadMoreMsgFromDB(params.key, params.requestedLoadSize);
        }
        if (messages == null) {
            messages = new ArrayList<>();
        }
        LogUtil.logI(TAG, String.format("loadBefore() size: %s", messages.size()));
        messages = ensureMessages(messages, true);
        callback.onResult(messages);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<EMMessage> callback) {
        /*List<EMMessage> messages = null;
        EMMessage lastMsg;
        if (mConversation != null &&
                (lastMsg = EMClient.getInstance().chatManager().getMessage(params.key)) != null) {
            // 这里的时间要+1,防止重复加载出lastMsg,导致出现两条一样的数据
            messages = mConversation.searchMsgFromDB(lastMsg.getMsgTime() + 1, params.requestedLoadSize, EMConversation.EMSearchDirection.DOWN);
        }

        if (messages == null) {
            messages = new ArrayList<>();
        }
        LogUtil.logI(TAG, String.format("loadAfter() size: %s", messages.size()));
        messages = ensureMessages(messages, false);
        callback.onResult(messages);*/

        LogUtil.logI(TAG, "loadAfter()");
        // 环信消息不存在往后加载
        callback.onResult(Collections.<EMMessage>emptyList());
    }

    private List<EMMessage> ensureMessages(List<EMMessage> messages, boolean addMsgTimeOnTop) {
        if (messages == null || messages.isEmpty())
            return new ArrayList<>();
        final List<EMMessage> ensureList = new ArrayList<>(messages);
        for (int i = messages.size() - 1; i > 0; i--) {
            final EMMessage pre = messages.get(i - 1);
            final EMMessage curr = messages.get(i);
            if (Math.abs(pre.getMsgTime() - curr.getMsgTime()) > MSG_TIME_INTERVAL) {
                ensureList.add(i, createTimeMsg(curr));
            }
        }
        if (addMsgTimeOnTop) {
            final EMMessage firstMsg = messages.get(0);
            ensureList.add(0, createTimeMsg(firstMsg));
        }
        return ensureList;
    }

    private EMMessage createTimeMsg(EMMessage message) {
        final long msgTime = message.getMsgTime();
        String timeDisplay = ConversationUtil.getMessageDate(mContext, msgTime);
        final EMMessage timeMsg = EMMessage.createTxtSendMessage(timeDisplay, message.getTo());
        timeMsg.setMsgTime(message.getMsgTime());
        timeMsg.setAttribute(MessageKey.MSG_TYPE, MessageType.MSG_TIME);
        timeMsg.setAttribute(MessageKey.HX_MSG_ID, message.getMsgId());
        return timeMsg;
    }

    @NonNull
    @Override
    public String getKey(@NonNull EMMessage item) {
        String hxMsgId = item.getStringAttribute(MessageKey.HX_MSG_ID, null);
        if (TextUtils.isEmpty(hxMsgId)) {
            hxMsgId = item.getMsgId();
        }
        return hxMsgId;
    }
}
