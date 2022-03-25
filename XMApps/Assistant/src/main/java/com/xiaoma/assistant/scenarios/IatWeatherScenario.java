package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.WeatherInfoV2;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserDate;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.WeatherParserResult;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/17
 * Desc：天气场景
 */
public class IatWeatherScenario extends IatScenario {

    private final String CITY = "city";
    private final String DATE = "date";
    public static final String CURRENT_CITY = "CURRENT_CITY";
    public static final String CURRENT_DAY = "CURRENT_DAY";
    public static final String CURRENT_DATE = "CURRENT_DATE";
    private final String RESULT_CODE = "resultCode";
    private final String RESULT_MESSAGE = "resultMessage";
    private final String QUERY = "QUERY";
    private final String WEATHER = "weather";
    private static final String RESULT_SUCCESS = "1";
    private WeatherParserResult result;
    private boolean mCondition1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public IatWeatherScenario(Context context) {
        super(context);
    }


    @Override
    public void init() {
        //初始化数据
    }


    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        KLog.d("zhs", "IatWeatherScenarioV2  onParser start---" + System.currentTimeMillis());
        this.parseResult = parseResult;
        String text = parseResult.getText();
        String result = parseResult.getSlots();
        if (!TextUtils.isEmpty(text) && isContains(text, R.string.car_control_air_control_key)) {
            addFeedbackAndSpeak(context.getString(R.string.error_weather_search));
            return;
        }
        Slots slots = GsonHelper.fromJson(result, Slots.class);
        if (slots == null) {
            speakUnderstand();
            return;
        }
        mCondition1 = slots.getDatetime() != null && !TextUtils.isEmpty(slots.getDatetime().getDateOrig()) && slots.getDatetime().getDateOrig().contains("未来");
        boolean condition2 = !TextUtils.isEmpty(slots.getSubfocus()) &&
                ("伞".equals(slots.getSubfocus())
                        || "洗车指数".equals(slots.getSubfocus())
                        || "雨".equals(slots.getSubfocus())
                        || "风".equals(slots.getSubfocus())
                        || "穿衣指数".equals(slots.getSubfocus())
                        || "pm25".equals(slots.getSubfocus()));
        if (!TextUtils.isEmpty(parseResult.getAnswerText()) && !TextUtils.isEmpty(result) && condition2) {
            addFeedbackAndSpeak(parseResult.getAnswerText().replaceAll("\"", ""));
            return;
        }
        ParserLocation parserLocation = slots.getLocation() == null
                ? new ParserLocation() : slots.getLocation();
        ParserDate parserDate = slots.getDatetime();
        String city = "";
        String area = "";
        String date = "";
        String province = "";
        String parserCity = parserLocation.getCity();
        String parserArea = parserLocation.getArea();
        String parserProvince = parserLocation.getProvince();
        String parserDateTime = parserDate.getDate();
        String parserEndDateTime = parserDate.getendDate();
        if (!TextUtils.isEmpty(parserCity) || !TextUtils.isEmpty(parserLocation.getCityAddr())) {
            String cityAddr = parserLocation.getCityAddr();
            city = parserLocation.getCity();
            if (!TextUtils.isEmpty(cityAddr)) {
                city = cityAddr;
            }
        } else if (!TextUtils.isEmpty(parserArea)) {
            String areaAddr = parserLocation.getAreaAddr();
            area = parserArea;
            if (!TextUtils.isEmpty(areaAddr)) {
                area = areaAddr;
            }
        } else if (!TextUtils.isEmpty(parserProvince)) {
            province = parserProvince;
        }
        if (!TextUtils.isEmpty(parserDateTime)) {
            date = parserDateTime;
        }
        if (CURRENT_DAY.equals(date) || CURRENT_DATE.equals(date)) {
            date = "";
        }
//        else {
//            long daysOfTwo = getDaysOfTwo(dateFormat.format(new Date()), date);
//            if (!(daysOfTwo >= 0 && daysOfTwo <= 4)) {
//                addFeedbackAndSpeak(getString(R.string.sorry_only_query_five_day_weather));
//                return;
//            }
//        }

        //定位失效，默认模拟定位点  只在debug下生效
        if (BuildConfig.DEBUG) {
            if (location == null || TextUtils.isEmpty(location.city)) {
                location = new ParserLocationParam(LocationManager.DebugLocation.location, LocationManager.DebugLocation.city);
            }
        }

        if (city.equals(CURRENT_CITY)) {
            if (location == null || TextUtils.isEmpty(location.city)) {
                speakNoLocation();
                return;
            }
            if (!TextUtils.isEmpty(location.city)) {
                city = location.city;
            }
        }
        if (TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)) {
            city = area;
        } else if (TextUtils.isEmpty(city) && TextUtils.isEmpty(area) && !TextUtils.isEmpty(province)) {
            addFeedbackAndSpeak(context.getString(R.string.error_weather_search));
            return;
        }
        KLog.d("zhs", "IatWeatherScenarioV2  onParser end---" + System.currentTimeMillis());
        queryWeather(city, date, text);
    }

    private void queryWeather(String city, String date, final String text) {
        KLog.d("zhs", "IatWeatherScenarioV2  query start---" + System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put(CITY, city);
        option.put(DATE, date);
        //EventAgent.getInstance().onEvent(com.xiaoma.base.constant.Constants.XMEventKey.Assistant.VOICE_SEARCH_WEATHER, option);
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().queryCityWeather(city, date, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
//                executeSkillSucceeded();
                String result = response.body();
                KLog.d("zhs", "IatWeatherScenarioV2  query end---" + System.currentTimeMillis());
                if (StringUtil.isEmpty(result)) {
                    showSearchError();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString(RESULT_CODE);
                    String resultMessage = jsonObject.getString(RESULT_MESSAGE);
                    WeatherInfoV2 weatherInfoV2 = GsonHelper.fromJson(result, WeatherInfoV2.class);
                    if (weatherInfoV2 != null) {
                        if (!ListUtils.isEmpty(weatherInfoV2.result)) {
                            WeatherInfoV2.ResultBean resultBean = weatherInfoV2.result.get(0);
                            if (resultBean != null && StringUtil.isNotEmpty(resultBean.now)) {
                                long daysOfTwo = getDaysOfTwo(resultBean.now, date);
                                if (!(daysOfTwo >= 0 && daysOfTwo <= 4)) {
                                    addFeedbackAndSpeak(getString(R.string.sorry_only_query_five_day_weather));
                                    return;
                                }
                            }
                        }
                    }
                    if (RESULT_SUCCESS.equals(resultCode) && weatherInfoV2 != null) {
                        if (mCondition1) {
                            speakContent(text + getString(R.string.as_follows), new WrapperSynthesizerListener() {
                                @Override
                                public void onCompleted() {
                                    super.onCompleted();
                                    executeSkillSucceeded();
                                }

                                @Override
                                public void onError(int code) {
                                    super.onError(code);
                                    executeSkillSucceeded();
                                }
                            });
                            //创建一个conversation并展示
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.WEATHER, weatherInfoV2);
                            addConversationToList(conversationItem);
                        } else {
                            addFeedBackConversation("天气查询结果如下：");
                            speakContent(weatherInfoV2.makeSpeakingWord(context), new WrapperSynthesizerListener() {
                                @Override
                                public void onCompleted() {
                                    super.onCompleted();
                                    executeSkillSucceeded();
                                }

                                @Override
                                public void onError(int code) {
                                    super.onError(code);
                                    executeSkillSucceeded();
                                }
                            });
                            //创建一个conversation并展示
                            ConversationItem conversationItem = parseResult.createReceiveConversation(
                                    VrConstants.ConversationType.WEATHER, weatherInfoV2);
                            addConversationToList(conversationItem);
                        }
                    } else {
                        KLog.e(resultMessage);
                        addFeedbackAndSpeak(context.getString(R.string.sorry_only_query_five_day_weather));
                    }
                } catch (Exception e) {
                    addFeedbackAndSpeak(context.getString(R.string.sorry_only_query_five_day_weather));
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                executeSkillSucceeded();
                super.onError(response);
                if (!NetworkUtils.isConnected(context)) {
                    addFeedbackAndSpeak(context.getString(R.string.network_errors));
                } else {
                    addFeedbackAndSpeak(context.getString(R.string.sorry_only_query_five_day_weather));
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

    private long getDaysOfTwo(String str1, String str2) {
        long days = 0;
        try {
            Date one = dateFormat.parse(str1);
            Date two = dateFormat.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff = time2 - time1;
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static class Slots {
        public ParserLocation location;
        public ParserDate datetime;
        public String subfocus;

        public ParserLocation getLocation() {
            return location == null ? new ParserLocation() : location;
        }

        public void setLocation(ParserLocation location) {
            this.location = location;
        }

        public ParserDate getDatetime() {
            return datetime == null ? new ParserDate() : datetime;
        }

        public void setDatetime(ParserDate datetime) {
            this.datetime = datetime;
        }

        public String getSubfocus() {
            return subfocus;
        }

        public void setSubfocus(String subfocus) {
            this.subfocus = subfocus;
        }
    }
}
