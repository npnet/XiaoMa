//package com.xiaoma.club.msg.conversation.model;
//
//import android.content.Context;
//
//import com.hyphenate.chat.EMConversation;
//import com.hyphenate.chat.EMMessage;
//import com.xiaoma.adapter.base.XMBean;
//import com.xiaoma.club.R;
//import com.xiaoma.club.msg.chat.constant.MessageKey;
//import com.xiaoma.club.msg.chat.constant.MessageType;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Author: loren
// * Date: 2019/1/2 0002
// */
//
//public class XMEMConversation extends XMBean<EMConversation> {
//
//    public XMEMConversation(EMConversation emConversation) {
//        super(emConversation);
//    }
//
//    public EMConversation.EMConversationType getChatType() {
//        return getSDKBean().getType();
//    }
//
//    public int getUnReadMsgCount() {
//        return getSDKBean().getUnreadMsgCount();
//    }
//
//    public List<EMMessage> getAllMessages() {
//        return getSDKBean().getAllMessages();
//    }
//
//    public String getId() {
//        return getSDKBean().conversationId();
//    }
//
//    public String getNewMsg(Context context) {
//        String newMsg = "";
//        final EMMessage message = getSDKBean().getLastMessage();
//        if (message != null) {
//            switch (message.getType()) {
//                case TXT:
//                    final String type = message.getStringAttribute(MessageKey.MSG_TYPE, "");
//                    if (MessageType.FACE.equalsIgnoreCase(type)) {
//                        newMsg = context.getString(R.string.msg_face);
//                    }
//                    break;
//                case IMAGE:
//                    break;
//                case VIDEO:
//                    break;
//                case LOCATION:
//                    newMsg = context.getString(R.string.msg_location);
//                    break;
//                case VOICE:
//                    newMsg = context.getString(R.string.msg_voice);
//                    break;
//                case FILE:
//                    break;
//                case CMD:
//                    break;
//            }
//        }
//        return newMsg;
//    }
//
//    public String getNewMsgDate() {
//        final EMMessage message = getSDKBean().getLastMessage();
//        if (message != null) {
//            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getMsgTime()));
//        }
//        return "";
//    }
//
//    public void markAllMessagesAsRead() {
//        getSDKBean().markAllMessagesAsRead();
//    }
//}
