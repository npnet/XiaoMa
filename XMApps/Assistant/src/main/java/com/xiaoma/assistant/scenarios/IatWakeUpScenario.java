package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：唤醒场景
 */
public class IatWakeUpScenario extends IatScenario {
    private static final String TAG = IatWakeUpScenario.class.getSimpleName();
    private final String[] sPunctuation = {",", ".", "?", ";", "!", "，", "。", "？", "；", "！"};
    private String keyWord1 = context.getString(R.string.wake_up_set1);
    private String keyWord2 = context.getString(R.string.wake_up_set2);
    private String keyWord3 = context.getString(R.string.wake_up_set3);
    private String keyWord4 = context.getString(R.string.wake_up_set4);

    public IatWakeUpScenario(Context context) {
        super(context);
    }


    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String data = "";
        if (parseResult.isChangeWakeupAction()) {
            try {
                if (!TextUtils.isEmpty(parseResult.getSlots())) {
                    JSONObject slots = new JSONObject(parseResult.getSlots());
                    if (slots != null && slots.has("productName")) {
                        data = slots.getString("productName");
                        data = handlerWordByReplace(data);
                        boolean result = VrAidlManager.getInstance().setWakeupWord(data);
                        if (result) {
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.OTHER, getString(R.string.new_wake_up_word_is) + data);
                            addConversationToList(conversationItem);
                            closeAfterSpeak(getString(R.string.new_wake_up_word_is) + data);
                            wakeupWordChange(data);
                        } else {
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.OTHER, getString(R.string.new_wake_up_error));
                            addConversationToList(conversationItem);
                            closeAfterSpeak(getString(R.string.new_wake_up_error));
                        }
//                        doResult(data, parseResult.getText(), Constants.VoiceParserType.ACTION_WAKE_UP, "CONTROL", "wakeup", callback);
//                        speakContent(getString(R.string.new_wake_up_word_is) + data);
                    } else {
                        //doResult("", parseResult.getText(), Constants.VoiceParserType.ACTION_ERROR, "", "", callback);
                        ConversationItem conversationItem = parseResult.createReceiveConversation(
                                VrConstants.ConversationType.OTHER, getString(R.string.not_support_wake_up_word));
                        addConversationToList(conversationItem);
                        closeAfterSpeak(getString(R.string.not_support_wake_up_word));
                    }
                } else {
                    data = handleWakeUpWord(parseResult.getText());
                    data = handlerWordByReplace(data);
                    //doResult(data, parseResult.getText(), Constants.VoiceParserType.ACTION_WAKE_UP, "CONTROL", "wakeup", callback);
                    ConversationItem conversationItem = parseResult.createReceiveConversation(
                            VrConstants.ConversationType.OTHER, getString(R.string.new_wake_up_word_is) + data);
                    addConversationToList(conversationItem);
                    closeAfterSpeak(getString(R.string.new_wake_up_word_is) + data);
                    wakeupWordChange(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //doResult("", parseResult.getText(), Constants.VoiceParserType.ACTION_ERROR, "", "", callback);
                speakUnderstand();
            }
        }
    }

    private void wakeupWordChange(String data) {
        context.sendBroadcast(new Intent(com.xiaoma.utils.constant.VrConstants.Actions.WAKEUP_CHANGE));
    }

    private String handlerWordByReplace(String word) {
        if (TextUtils.isEmpty(word)) return "";
        String result = word;
        for (int i = 0; i < sPunctuation.length; i++) {
            result = result.replace(sPunctuation[i], "");
        }
        return result;
    }

    private String handleWakeUpWord(String voiceContent) {
        String name = "";
        if (voiceContent.contains(keyWord1)) {
            String[] strs = voiceContent.split(keyWord1);
            if (strs != null && strs.length > 0)
                name = strs[strs.length - 1];
        } else if (voiceContent.contains(keyWord2)) {
            String[] strs = voiceContent.split(keyWord2);
            if (strs != null && strs.length > 0)
                name = strs[strs.length - 1];
        } else if (voiceContent.contains(keyWord3)) {
            String[] strs = voiceContent.split(keyWord3);
            if (strs != null && strs.length > 0)
                name = strs[strs.length - 1];
        } else if (voiceContent.contains(keyWord4)) {
            String[] strs = voiceContent.split(keyWord4);
            if (strs != null && strs.length > 0)
                name = strs[strs.length - 1];
        }
        return name;
    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }
}
