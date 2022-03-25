package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;

/**
 * @author: iSun
 * @date: 2019/5/5 0005
 */
class IatHelperScenario extends IatScenario {
    public IatHelperScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        AssistantManager.getInstance().showHelper();
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
