package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：日期场景
 */
public class IatDateScenario extends IatScenario {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

    public IatDateScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        doParserNew();
    }


    //3.5套件版的解析
    private void doParserNew() {
        String slotJson = parseResult.getSlots();
        if (TextUtils.isEmpty(slotJson) || slotJson.equals("{}")) {
            speakUnderstand();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(parseResult.getAnswer());
            String text = jsonObject.getString("text");
            if (!TextUtils.isEmpty(text)) {
                XmTtsManager.getInstance().stopSpeaking();
                speakContent(text);
                ConversationItem conversationItem = parseResult.createReceiveConversation(
                        VrConstants.ConversationType.TIME, text);
                addConversationToList(conversationItem);
            } else {
                speakUnderstand();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            speakUnderstand();
        }

//        DateSlot dateSlot = GsonHelper.fromJson(slotJson, DateSlot.class);
//        if (dateSlot == null || dateSlot.getDatetime() == null) {
//            speakUnderstand();
//        }
//        String dateStr = dateSlot.getDatetime().getDate();
//        try {
//            Date date = dateFormat.parse(dateStr);
//            String format = dateFormat2.format(date);
//            String text = dateSlot.getDatetime().getWeekday() != null ? String.format(context.getString(R.string.tts_date_and_weekday), format, dateSlot.getDatetime().getWeekday())
//                    : String.format(context.getString(R.string.tts_date), format);
//            XmTtsManager.getInstance().stopSpeaking();
//            speakContent(text);
//            //创建一个conversation并展示
//            ConversationItem conversationItem = parseResult.createReceiveConversation(
//                    VrConstants.ConversationType.TIME, text);
//            addConversationToList(conversationItem);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            speakUnderstand();
//        }

       /* String answer = parseResult.getAnswer();
        try {
            JSONObject json = new JSONObject(answer);
            String text = json.getString("text");
            if (!TextUtils.isEmpty(text)) {
                addFeedBackConversation(context.getString(R.string.date_search_result_is));
                XmTtsManager.getInstance().stopSpeaking();
                speakContent(text);
                //创建一个conversation并展示
                ConversationItem conversationItem = parseResult.createReceiveConversation(
                        VrConstants.ConversationType.TIME,text);
                addConversationToList(conversationItem);
            } else {
                speakUnderstand();
            }
        } catch (Exception e) {
            e.printStackTrace();
            speakUnderstand();
        }*/
    }


    //3.0套件版的解析
    private void doParserOld() {
        String data = "";
        if (parseResult.isCmdTimeAction()) {
            try {
                JSONObject slots = new JSONObject(parseResult.getSlots());
                if (slots != null && slots.has("name")) {
                    String name = slots.getString("name");
                    if ("hour".equals(name) || "day".equals(name)) { //日期
                        JSONArray jsonArray = new JSONArray(parseResult.getMoreResults());
                        JSONObject result = jsonArray.getJSONObject(0);
                        data = result.getJSONObject("answer").getString("text");
                        String[] split = data.split(" ");
                        if (split[1].contains(getString(R.string.week_str))) {
                            //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_TIME, data);
                        } else {
                            //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_DATE, data);
                        }
                        addFeedBackConversation(context.getString(R.string.date_search_result_is));
                        XmTtsManager.getInstance().stopSpeaking();
                        speakContent(data);
                        //创建一个conversation并展示
                        ConversationItem conversationItem = parseResult.createReceiveConversation(
                                VrConstants.ConversationType.TIME, data);
                        addConversationToList(conversationItem);
                    } else {
                        //doResult("", parseResult.getText(), Constants.VoiceParserType.ACTION_ERROR, "", "", callback);
                        speakUnderstand();
                    }
                } else {
                    speakUnderstand();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                speakUnderstand();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getAnswer());
                data = jsonObject.optString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] split = data.split(" ");
            if (split != null && split.length > 1 && split[1].contains(getString(R.string.week_str))) {
                //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_TIME, data);
            } else {
                //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_DATE, data);
            }
            addFeedBackConversation(context.getString(R.string.date_search_result_is));
            XmTtsManager.getInstance().stopSpeaking();
            speakContent(data);
            //创建一个conversation并展示
            ConversationItem conversationItem = parseResult.createReceiveConversation(
                    VrConstants.ConversationType.TIME, data);
            addConversationToList(conversationItem);
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

    public static class DateSlot {

        /**
         * datetime : {"date":"2019-08-12","dateOrig":"今天","type":"DT_BASIC","weekday":"星期一"}
         */

        private DatetimeBean datetime;

        public DatetimeBean getDatetime() {
            return datetime;
        }

        public void setDatetime(DatetimeBean datetime) {
            this.datetime = datetime;
        }

        public static class DatetimeBean {
            /**
             * date : 2019-08-12
             * dateOrig : 今天
             * type : DT_BASIC
             * weekday : 星期一
             */

            private String date;
            private String dateOrig;
            private String type;
            private String weekday;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getDateOrig() {
                return dateOrig;
            }

            public void setDateOrig(String dateOrig) {
                this.dateOrig = dateOrig;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getWeekday() {
                return weekday;
            }

            public void setWeekday(String weekday) {
                this.weekday = weekday;
            }
        }
    }
}
