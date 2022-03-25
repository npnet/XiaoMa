package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.HoroscopeBean;
import com.xiaoma.assistant.model.parser.ConstellationXfParser;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：星座场景
 */
public class IatConstellationScenario extends IatScenario {


    public IatConstellationScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        String slots = parseResult.getSlots();
        try {
            if (!TextUtils.isEmpty(slots)) {
                //queryConstellation(slots);
                queryConstellationV2(slots);
            } else {
                speakContent(getString(R.string.error_data_response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            speakContent(getString(R.string.error_data_response));
        }
    }


    private void queryConstellationV2(String slots) {
        SlotsV2 slotsV2 = GsonHelper.fromJson(slots, SlotsV2.class);
        if (slotsV2 == null) {
            speakUnderstand();
            return;
        }

        String dateType = slotsV2.dateType;
        String name = slotsV2.name;
        String dateTime = "";
        if (TextUtils.isEmpty(dateType) && slotsV2.datetime != null) {
            dateTime = slotsV2.datetime.date;
        }
        searchConstellation(name, dateType, dateTime);
    }


    private void queryConstellation(final String text) {
        ConstellationXfParser parse = GsonHelper.fromJson(text, ConstellationXfParser.class);
        if (parse != null) {
            String userId = LoginManager.getInstance().getLoginUserId();
            Slots slots = GsonHelper.fromJson(parseResult.getSlots(), Slots.class);
            String constellation = "";
            String type = "custom";
            String dateTime = "";
            if (slots != null && slots.dateTime != null) {
                if (!TextUtils.isEmpty(slots.Name)) {
                    constellation = slots.Name;
                }
                dateTime = slots.dateTime.dateOrig;
                if (TextUtils.isEmpty(dateTime)) {
                    dateTime = slots.dateTime.date;
                }

                if ("今天".equals(dateTime)) {
                    type = "today";
                } else if ("明天".equals(dateTime)) {
                    type = "tomorrow";
                } else if ("本周".equals(dateTime)) {
                    type = "week";
                } else if ("下一周".equals(dateTime)) {
                    type = "nextweek";
                } else if ("本月".equals(dateTime)) {
                    type = "month";
                } else if ("今年".equals(dateTime)) {
                    type = "year";
                } else if (isValidDate(slots.dateTime.date)) {
                    dateTime = slots.dateTime.date;
                    type = "custom";
                } else {
                    speakContent(getString(R.string.error_date_constellation));
                    return;
                }
            } else {
                String time = parse.when.time;
                if ("T1".equals(time)) {
                    type = "today";
                } else if ("T2".equals(time)) {
                    type = "tomorrow";
                } else if ("W1".equals(time)) {
                    type = "week";
                } else if ("W2".equals(time)) {
                    type = "nextweek";
                } else if ("M1".equals(time)) {
                    type = "month";
                } else if ("Y1".equals(time)) {
                    type = "year";
                } else if ("S".equals(time)) {
                    int year = parse.when.date.year;
                    int month = parse.when.date.month;
                    int day = parse.when.date.day;
                    if (year == 0) {
                        dateTime = month + "-" + day;
                    } else {
                        dateTime = year + "-" + month + "-" + day;
                    }
                    type = "custom";
                } else {
                    speakContent(getString(R.string.error_date_constellation));
                    return;
                }
            }

            searchConstellation(constellation, type, dateTime);
        } else {
            speakUnderstand();
        }
    }


    private void searchConstellation(String constellation, String type, String dateTime) {
        Map<String, Object> option = new HashMap<>();
        option.put("constellation", constellation);
        option.put("date", type);
        String userId = LoginManager.getInstance().getLoginUserId();
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().searchConstellation(userId, constellation, type, dateTime, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                if (!TextUtils.isEmpty(result)) {
                    XMResult baseResult = GsonHelper.fromJson(result, XMResult.class);
                    if (baseResult != null && baseResult.isSuccess()) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String data = jsonObject.getString("data");
                            HoroscopeBean bean = GsonHelper.fromJson(data, HoroscopeBean.class);
                            speakContent(bean.getLuck());
                            //创建一个conversation并展示
                          /*  ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.CONSTELLATION, bean);*/
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.OTHER,bean.getLuck());
                            addConversationToList(conversationItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                            speakContent(getString(R.string.error_data_response));
                        }
                    } else {
                        if (baseResult != null && !TextUtils.isEmpty(baseResult.getResultMessage())) {
                            speakContent(baseResult.getResultMessage());
                        } else {
                            speakContent(getString(R.string.error_data_response));
                        }
                    }
                } else {
                    speakContent(getString(R.string.error_data_response));
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                if (!NetworkUtils.isConnected(context)) {
                    speakContent(getString(R.string.network_errors));
                } else {
                    speakContent(getString(R.string.error_data_response));
                }
            }
        });
    }


    private boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
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

    static class Slots {

        private String Name;
        private String Type;

        private SlotDateTime dateTime;
    }

    static class SlotDateTime {
        String current_date;
        String date;
        String dateOrig;
        String type;
        String endDate;
    }


    private class SlotsV2 {
        public String dateType;
        public String name;

        public SlotDateTime datetime;
    }
}
