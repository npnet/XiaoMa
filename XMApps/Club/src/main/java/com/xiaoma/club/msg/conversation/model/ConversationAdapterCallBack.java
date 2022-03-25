package com.xiaoma.club.msg.conversation.model;

/**
 * Author: loren
 * Date: 2019/1/30 0030
 */

public interface ConversationAdapterCallBack {
    void delete(String chatId,String groupName);

    void onFetchModel(String chatId);
}
