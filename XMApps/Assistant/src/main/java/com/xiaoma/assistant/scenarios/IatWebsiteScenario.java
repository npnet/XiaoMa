package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：网页搜索
 */
public class IatWebsiteScenario extends IatScenario {


    public IatWebsiteScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String url = "";
        try {
            JSONObject jsonObject = new JSONObject(parseResult.getSlots());
            url = jsonObject.optString("url", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int speakRandom = AssistantUtils.getUnStandWord();
        String welcomeSpeak = getString(AssistantUtils.mUnStands_Speak[speakRandom]);
        String welcome = getString(AssistantUtils.mUnStands[speakRandom]);
        addFeedBackConversation(welcome);
        XmTtsManager.getInstance().stopSpeaking();
        speakContent(welcomeSpeak);
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
