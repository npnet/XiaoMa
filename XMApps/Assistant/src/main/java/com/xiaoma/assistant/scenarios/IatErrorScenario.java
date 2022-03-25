package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.vrfactory.tts.XmTtsManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/31
 * Desc：异常错误场景
 */
public class IatErrorScenario extends IatScenario {


    public IatErrorScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        XmTtsManager.getInstance().stopSpeaking();
        if (!TextUtils.isEmpty(parseResult.getService())) {
            addFeedbackAndSpeak(context.getString(R.string.nonsupport_order_right_now));
        } else {
            int speakRandom = AssistantUtils.getUnStandWord();
            String unStandSpeak = getString(AssistantUtils.mUnStands_Speak[speakRandom]);
            String unStand = getString(AssistantUtils.mUnStands[speakRandom]);
            if (assistantManager.isShowing()){
                speakAndFeedListening(unStandSpeak, unStand);
            }
        }
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
