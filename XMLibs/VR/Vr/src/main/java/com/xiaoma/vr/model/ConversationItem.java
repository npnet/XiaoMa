package com.xiaoma.vr.model;

import com.xiaoma.vr.utils.VrConstants;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * Desc:语音助手会话model
 */
public class ConversationItem {

    public static int CONVERSATION_INPUT = 0;
    public static int CONVERSATION_RESPONSE = 1;

    private String id;
    private String operation;
    private String service;
    private String text;
    private String data;
    private String unStandWord;
    private Object attachment;
    private int action;
    private int conversationType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public boolean isMessageReceived() {
        return getConversationType() == CONVERSATION_RESPONSE;
    }

    public String getUnStandWord() {
        return unStandWord;
    }

    public void setUnStandWord(String unStandWord) {
        this.unStandWord = unStandWord;
    }

    public static ConversationItem createReceiveMsg(String data) {
        ConversationItem conversationItem = new ConversationItem();
        conversationItem.setAction(VrConstants.ConversationType.OTHER);
        conversationItem.setConversationType(CONVERSATION_RESPONSE);
        conversationItem.setData(data);
        return conversationItem;
    }

    public static ConversationItem createSendMsg(String data) {
        ConversationItem conversationItem = new ConversationItem();
        conversationItem.setConversationType(CONVERSATION_INPUT);
        conversationItem.setData(data);
        return conversationItem;
    }


}
