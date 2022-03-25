package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;

/**
 * @author: iSun
 * @date: 2019/1/21 0021
 * 影片场景
 */
public class IatMovieScenario extends IatScenario {
    public IatMovieScenario(Context context) {
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
