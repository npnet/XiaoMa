package com.xiaoma.vr.model;//package com.xiaoma.vr.model;
//
//import android.text.TextUtils;
//
//import com.xiaoma.base.constant.Constants;
//import com.xiaoma.utils.json.GsonHelper;
//
//import java.io.Serializable;
//
///**
// * User:Created by Terence
// * IDE: Android Studio
// * Date:2018/6/11
// */
//public class PushMessage implements Serializable {
//    private String id;
//    private String action;
//    private String operation;
//    private String service;
//    private String text;
//    private String data;
//    private Object attachment;
//
//    public String getAction() {
//        return action;
//    }
//
//    public void setAction(String action) {
//        this.action = action;
//    }
//
//    public String getOperation() {
//        return operation;
//    }
//
//    public void setOperation(String operation) {
//        this.operation = operation;
//    }
//
//    public String getService() {
//        return service;
//    }
//
//    public void setService(String service) {
//        this.service = service;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public String getData() {
//        return data;
//    }
//
//    public void setData(String data) {
//        this.data = data;
//    }
//
//    public Object getAttachment() {
//        return attachment;
//    }
//
//    public void setAttachment(Object attachment) {
//        this.attachment = attachment;
//    }
//
//    public ConversationItem toConversationItem(int conversationType) {
//        ConversationItem conversationItem = new ConversationItem();
//        conversationItem.setId(id);
//        conversationItem.setAction(action);
//        conversationItem.setOperation(operation);
//        conversationItem.setService(service);
//        conversationItem.setText(text);
//        conversationItem.setData(data);
//        conversationItem.setAttachment(attachment);
//        conversationItem.setConversationType(conversationType);
//        return conversationItem;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String toJson() {
//        return GsonHelper.toJson(this);
//    }
//
//    public static PushMessage fromJson(String json) {
//        PushMessage pushMessage = GsonHelper.fromJson(json, PushMessage.class);
//        if (TextUtils.isEmpty(pushMessage.data)) {
//            pushMessage.action = Constants.VoiceParserType.ACTION_ERROR;
//        }
//        return pushMessage;
//    }
//}
