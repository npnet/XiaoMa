package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.LimitInfo;
import com.xiaoma.assistant.model.parser.DateTimeBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：限行场景
 */
public class IatLimitScenario extends IatScenario {


    public IatLimitScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        parser();
    }


    private void parser() {
        String slots = parseResult.getSlots();
        SlotV2 slotV2 = GsonHelper.fromJson(slots,SlotV2.class);
        String date = slotV2.datetime.getDate();
        String city = slotV2.location.getCity();
        queryLimit(city,date,1);
    }


    private void xmParserOld(ParserLocationParam location){
        String date = "";
        String position = "";
        int queryDay = 1;
        String queryDate = ""; //具体的日期
        String city = "";
        if (location != null) {
            city = location.city;
        }
        if (!TextUtils.isEmpty(city) && city.lastIndexOf(context.getString(R.string.xfparser_key_city)) == city.length() - 1) {
            city = city.substring(0, city.lastIndexOf(context.getString(R.string.xfparser_key_city)));
        }
        String queryCity = city;
        try {
            JSONObject jsonObject = new JSONObject(parseResult.getSlots());
            date = jsonObject.getJSONObject("when").getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(date)) {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getSlots());
                date = jsonObject.getString("when");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(date)) {
            date = "T1";
        }

        boolean dateMatched = false;
        if ("E".equalsIgnoreCase(date)) {
            dateMatched = false;
        } else if ("T1".equalsIgnoreCase(date)) {
            dateMatched = true;
            queryDay = 1;
        } else if ("T2".equalsIgnoreCase(date)) {
            dateMatched = true;
            queryDay = 2;
        } else if ("T3".equalsIgnoreCase(date)) {
            dateMatched = true;
            queryDay = 3;
        } else if ("T4".equalsIgnoreCase(date)) {
            dateMatched = true;
            queryDay = 4;
        } else if (!date.contains(context.getString(R.string.xfparser_key_month))) {
            dateMatched = true;
            queryDay = 1;
        } else {
            dateMatched = true;
            queryDay = -1;
            queryDate = date;
        }

        try {
            JSONObject jsonObject = new JSONObject(parseResult.getSlots());
            position = jsonObject.getJSONObject("where").getString("city");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(position)) {
            position = "HERE";
        }

        if (!"HERE".equalsIgnoreCase(position)) {
            queryCity = position;
        }

        JSONObject jo = new JSONObject();
        try {
            if (dateMatched) {
                jo.put("queryDay", queryDay);
                jo.put("queryCity", queryCity);
                jo.put("queryDate", queryDate);
                Map<String, Object> option = new HashMap<>();
                option.put("city", queryCity);
                option.put("date", queryDay);
                //EventAgent.getInstance().onEvent(com.xiaoma.base.constant.Constants.XMEventKey.Assistant.VOICE_SEARCH_LIMIT, option);
                //doResult(jo.toString(), parseResult.getText(), com.xiaoma.base.constant.Constants.VoiceParserType.ACTION_LIMIT, parseResult.getOperation(), parseResult.getService(), callback);
                queryLimit(queryCity,queryDate,queryDay);
            } else {
                jo.put("queryCity", queryCity);
                //parserDateOutOfBoundsHandler(jo.toString(), parseResult.getText(), callback);
                //todo 日期越界
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //doResult("", parseResult.getText(), com.xiaoma.base.constant.Constants.VoiceParserType.ACTION_ERROR, "", "", callback);
            speakUnderstand();
        }
    }



    private void queryLimit(final String city, String date, int type){
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().fetchLimit(city, type, date, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                LimitInfo limitInfo = GsonHelper.fromJson(result, LimitInfo.class);
                if (limitInfo == null) {
                    showError(getString(R.string.no_data_response));
                    return;
                }
                if (!limitInfo.isSuccess()) {
                    XmTtsManager.getInstance().stopSpeaking();
                    showError(limitInfo.getResultMessage());
                    return;
                }

                //播报的日期，由后台返回
                String date = limitInfo.getFormatDate();

                String speakText;
                if (limitInfo.isXianXing()) {
                    String limitNum = "";
                    if (limitInfo.getXxweihao().size() > 0) {
                        limitNum = context.getString(R.string.limit_number);
                        for (int i = 0; i < limitInfo.getXxweihao().size(); i++) {
                            if (i != limitInfo.getXxweihao().size() - 1) {
                                limitNum += limitInfo.getXxweihao().get(i) + context.getString(R.string.dayton);
                            } else {
                                limitNum += limitInfo.getXxweihao().get(i);
                            }
                        }
                    }
                    speakText = city + date + context.getString(R.string.limit_info) + limitNum;
                } else {
                    speakText = city + date + getString(R.string.unlimited);
                }
                addFeedbackAndSpeak(speakText);
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                showError(getString(R.string.network_errors));
            }
        });
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


    private void showError(String errorText) {
        if (TextUtils.isEmpty(errorText))
            errorText = getString(R.string.no_data_response);
        addFeedBackConversation(errorText);
        speakContent(errorText, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                startListening();
            }
        });
    }


    class Slots {
        private ParserLocation location;

        public ParserLocation getLocation() {
            return location == null ? new ParserLocation() : location;
        }

        public void setLocation(ParserLocation location) {
            this.location = location;
        }
    }


    class SlotV2 {
        DateTimeBean datetime;
        ParserLocation location;
    }
}
