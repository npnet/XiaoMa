package com.xiaoma.club.msg.conversation.util;

import android.content.Context;
import android.support.annotation.Size;
import android.support.annotation.StringRes;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.R;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LKF on 2019-1-23 0023.
 */
public class ConversationUtil {
    private static final String NEW_MSG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getNewMsg(Context context, EMConversation conversation) {
        String newMsg = "";
        if (context != null && conversation != null) {
            final EMMessage message = conversation.getLastMessage();
            if (message != null) {
                newMsg = getMsgContent(context, message);
            }

        }
        return newMsg;
    }

    public static String getMsgContent(Context context, EMMessage message) {
        String newMsg = "";
        switch (message.getType()) {
            case TXT:
                final String type = message.getStringAttribute(MessageKey.MSG_TYPE, "");
                if (MessageType.FACE.equalsIgnoreCase(type)) {
                    newMsg = context.getString(R.string.msg_face);
                } else if (MessageType.SHARE.equalsIgnoreCase(type)) {
                    newMsg = context.getString(R.string.msg_share);
                } else if (MessageType.RED_PACKET.equalsIgnoreCase(type)) {
                    if (message.getBooleanAttribute(MessageKey.RedPacket.IS_LOCATION, false)) {
                        newMsg = context.getString(R.string.msg_red_packet_location);
                    } else {
                        newMsg = context.getString(R.string.msg_red_packet_normal);
                    }
                } else {
                    newMsg = message.getBody().toString().replace("txt:", "");
                }
                break;
            case IMAGE:
                break;
            case VIDEO:
                break;
            case LOCATION:
                newMsg = context.getString(R.string.msg_location);
                break;
            case VOICE:
                newMsg = context.getString(R.string.msg_voice);
                break;
            case FILE:
                break;
            case CMD:
                break;
        }
        return newMsg;
    }


    public static String getMessageDate(Context context, long msgTime) {
        context = context.getApplicationContext();
        final Date today = new Date();
        final Date msgDate = new Date(msgTime);
        final Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        final Date yesterday = c.getTime();

        final @StringRes int dateFormatRes;
        if (msgDate.getYear() == yesterday.getYear()
                && msgDate.getMonth() == yesterday.getMonth()
                && msgDate.getDate() == yesterday.getDate()) {
            // 昨天
            dateFormatRes = R.string.msg_time_format_yesterday;
        } else {
            if (msgDate.getYear() == today.getYear()) {
                // 相同年份
                if (msgDate.getMonth() == today.getMonth()
                        && msgDate.getDate() == today.getDate()) {
                    // 同一天
                    dateFormatRes = R.string.msg_time_format_today;
                } else {
                    // 不在同一天
                    dateFormatRes = R.string.msg_time_format_same_year;
                }
            } else {
                // 不同年份
                dateFormatRes = R.string.msg_time_format_different_year;
            }
        }
        return new SimpleDateFormat(context.getString(dateFormatRes), Locale.US).format(msgTime);
    }


    // 获得日期的年月日字段
    @Size(3)
    private static int[] getTimeFields(long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(time);
        final int[] timeFields = new int[3];
        timeFields[0] = calendar.get(Calendar.YEAR);
        timeFields[1] = calendar.get(Calendar.MONTH);
        timeFields[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return timeFields;
    }

    public static EMConversation getConversation(String hxChatId, EMConversation.EMConversationType type) {
        final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(hxChatId, type, true);// 最后一个参数表示会话不存在则自动创建
        if (conversation != null && conversation.getLastMessage() == null) {
            // 记录会话的创建时间
            conversation.setExtField(String.valueOf(System.currentTimeMillis()));
        }
        return conversation;
    }

    public static long getConversationCreateTime(EMConversation conversation) {
        try {
            return Long.parseLong(conversation.getExtField());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
