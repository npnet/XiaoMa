package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：附近的群场景
 */
public class IatNearbyGroupScenario extends IatScenario {


    public IatNearbyGroupScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {

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
