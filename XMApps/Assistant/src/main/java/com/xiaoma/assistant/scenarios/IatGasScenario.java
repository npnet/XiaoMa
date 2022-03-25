package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.GasPriceBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：油价查询场景
 */
public class IatGasScenario extends IatScenario {


    public IatGasScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String data = "";
        String dateJson = "";
        this.parseResult = parseResult;
        try {
            Slots slots = GsonHelper.fromJson(parseResult.getSlots(), Slots.class);
            if (slots != null) {

                SlotLocation locationSlot = slots.location;
                if (!TextUtils.isEmpty(locationSlot.city)) {
                    data = locationSlot.city;
                } else {
                    data = locationSlot.cityAddr;
                }


                SlotDateTime dateTime = slots.datetime;
                if (dateTime != null) {
                    JSONObject dateObject = new JSONObject();
                    String date = dateTime.date;
                    dateObject.put("flag", "1");
                    dateObject.put("time", date);

                    dateJson = dateObject.toString();
//                    String[] split = date.split("-");
//                    dateObject.put("month", split[1]);
//                    dateObject.put("year", split[0]);
//                    dateObject.put("day", split[2]);
//
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("date", dateObject);
//                    jsonObject.put("time", date);
//                    dateJson = jsonObject.toString();
                }
//                if (date != null) {
//                    dateObject.put("month", date.month);
//                    dateObject.put("year", date.year);
//                    dateObject.put("day", date.day);
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("date", dateObject);
//                    jsonObject.put("time", dateTime.time);
//                    dateJson = jsonObject.toString();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(data)) {
            LocationInfo currentLocation = LocationManager.getInstance().getCurrentLocation();
            if (currentLocation != null) {
                if (TextUtils.isEmpty(currentLocation.getProvince())) {
                    data = currentLocation.getProvince();
                } else {
                    data = currentLocation.getCity();
                }
            }
        }

        if (TextUtils.isEmpty(data)) {
            String tips = getString(R.string.gasoline_error_cannot_location);
            speakContent(tips);
            return;
        }

        Map<String, Object> option = new HashMap<>();
        option.put("city", data);
        //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_OIL, option);
        queryGasOnline(data, dateJson);
    }

    private void queryGasOnline(final String text, String when) {
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().queryOil(text, when, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        String resultCode = jsonObject.getString("resultCode");
                        String resultMessage = jsonObject.getString("resultMessage");
                        if ("1".equals(resultCode)) {
                            String data = jsonObject.getString("data");
                            //显示和控制UI
                            GasPriceBean gasPriceBean = GsonHelper.fromJson(data, GasPriceBean.class);
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.GAS, gasPriceBean);
                            addConversationToList(conversationItem);
                            speakContent(context.getString(R.string.query_hint_price));
                        } else {
                            if (!TextUtils.isEmpty(resultMessage)) {
                                speakContent(resultMessage);
                            } else {
                                speakContent(getString(R.string.error_data_response));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        speakContent(getString(R.string.error_data_response));
                    }
                } else {
                    //parserErrorHandler(context.getString(R.string.error_data_response), text,callback);
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                KLog.d(response.code() + "---" + response.message());
                if (!NetworkUtils.isConnected(context)) {
                    //parserErrorHandler(context.getString(R.string.network_errors), text, callback);
                    speakContent(getString(R.string.error_data_response));
                } else {
                    //parserErrorHandler(context.getString(R.string.error_data_response), text, callback);
                    speakContent(getString(R.string.error_data_response));
                }

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

    //    {
//        "datetime": {
//        "date": "2019-07-31",
//                "dateOrig": "今天",
//                "type": "DT_BASIC"
//    },
//        "location": {
//        "city": "深圳市",
//                "cityAddr": "深圳",
//                "type": "LOC_BASIC"
//    }
//    }
    static class Slots {
        private SlotDateTime datetime;
        private SlotLocation location;
    }

    static class SlotDateTime {
        private String date;
        private String dateOrig;
        private String type;
    }

    static class SlotLocation {
        private String city;
        private String cityAddr;
        private String type;
    }

//    static class Slots {
//        private String province;
//        private String city;
//        private SlotDateTime when;
//    }
//
//    static class SlotDateTime {
//        private String time;
//        private SlotDate date;
//    }
//
//    static class SlotDate {
//        private String year;
//        private String day;
//        private String month;
//    }

}
