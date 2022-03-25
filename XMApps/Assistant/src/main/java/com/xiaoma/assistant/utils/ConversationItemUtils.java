package com.xiaoma.assistant.utils;

import android.content.Context;

import com.xiaoma.assistant.R;
import com.xiaoma.vr.model.ConversationItem;


/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/17
 * Desc：会话内容工具类
 */
public class ConversationItemUtils {

    public static String getSpeakingWord(ConversationItem conversationItem, Context context) {
        if (context == null || conversationItem == null) {
            return "";
        }
        //todo 语音播报内容处理
        return context.getString(R.string.search_result_showing);
    }

    //定义语音3.0播报内容
    public static String getSpeakingWordV2(ConversationItem conversationItem, Context context) {
        if (context == null || conversationItem == null) {
            return "";
        }

        return context.getString(R.string.search_result_showing);
    }

    public static String makeSearchCompleteText(ConversationItem conversationItem, Context context) {
        if (context == null || conversationItem == null) {
            return "";
        }

        return context.getString(R.string.search_result_showing);
    }


}
