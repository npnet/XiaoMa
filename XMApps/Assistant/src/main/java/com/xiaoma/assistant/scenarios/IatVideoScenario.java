package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: iSun
 * @date: 2019/2/25 0025
 * 视频场景
 */
class IatVideoScenario extends IatScenario {
    public IatVideoScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String slots = parseResult.getSlots();
        try {
            if (!TextUtils.isEmpty(slots)) {
                JSONObject jsonObject = new JSONObject(slots);
                if (jsonObject.has("insType")) {
                    String type = jsonObject.getString("insType");
                    controlVideo(type);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void controlVideo(String type) {
        if ("OPEN".equals(type)) {
            //打开视频
        } else if ("CLOSE".equals(type)) {
            //关闭视频
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
