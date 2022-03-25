package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.model.parser.HistoryToDayBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vr.model.ConversationItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/2/22 0022
 * 历史今天
 */
class IatHistoryTodayScenario extends IatScenario {
    public IatHistoryTodayScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String data = parseResult.getData();
        JSONObject jsonObject = null;
        try {
            String result;
            jsonObject = new JSONObject(data);
            if (jsonObject.has("result")) {
                result = jsonObject.getString("result");
                List<HistoryToDayBean> historyToDayBeans = GsonHelper.fromJsonToList(result, HistoryToDayBean[].class);
                if (!ListUtils.isEmpty(historyToDayBeans)) {
                    speakContent(historyToDayBeans.get(0).description);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
