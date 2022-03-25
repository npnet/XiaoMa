package com.xiaoma.club.msg.chat.model;

import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2018/10/18 0018.
 */
public class HxChatParam {
    private String hxChatId;
    private boolean isGroupChat;

    public HxChatParam(String hxChatId, boolean isGroupChat) {
        this.hxChatId = hxChatId;
        this.isGroupChat = isGroupChat;
    }

    public String getHxChatId() {
        return hxChatId;
    }

    public void setHxChatId(String hxChatId) {
        this.hxChatId = hxChatId;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    @Override
    public String toString() {
        return StringUtil.format("[ hxChatId: %s, isGroupChat: %b ]", hxChatId, isGroupChat);
    }
}
