package com.xiaoma.assistant.model.parser;

import android.text.TextUtils;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc:离线语义解析
 */
public class LxParseResult extends ParseResult {

    public static LxParseResult fromJson(String input) {
        LxParseResult parseResult = new LxParseResult();
        parseResult.setInputResult(input);
        try {
            JSONObject jsonObject = new JSONObject(input);
            parseResult.setRc(jsonObject.optInt("rc", 4));
            parseResult.setOperation(jsonObject.optString("operation"));
            if (jsonObject.has("semantic")) {
                JSONObject semantic = jsonObject.optJSONObject("semantic");
                Semantic semantic1 = new Semantic();
                if (semantic != null && semantic.has("slots")) {
                    semantic1.setSlots(semantic.optString("slots"));
                }
                parseResult.setSemantic(semantic1);
            }
            String answer = jsonObject.optString("answer");
            parseResult.setService(jsonObject.optString("service"));
            parseResult.setText(jsonObject.optString("text"));
            parseResult.setWebPage(jsonObject.optString("webPage"));
            parseResult.setAnswer(answer);
            parseResult.setAnswerText(parseAnswerText(answer));
            if (jsonObject.has("moreResults")) {
                parseResult.setMoreResults(jsonObject.optString("moreResults"));
            }
            if (jsonObject.has("data")) {
                parseResult.setData(jsonObject.optString("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parseResult == null) {
            parseResult = new LxParseResult();
        }
        return parseResult;
    }

    public LxParseResult() {
    }

    public LxParseResult(String slots) {
        setSemantic(new Semantic());
        getSemantic().setSlots(slots);
    }

    private static String parseAnswerText(String answer) {
        try {
            JSONObject json = new JSONObject(answer);
            return json.getString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isRoutAction() {
        /*return ("map".equals(service) && !"POSITION".equals(operation)) || "restaurant".equals(service) ||
                "lbs".equals(service) || "hotel".equals(service) ||
                "publicBus".equals(service) || "station".equals(service);*/
        return "mapU".equals(service) &&
                ("QUERY".equals(operation)
                        || "BOOK".equals(operation)
                        || "VIEW_TRANS".equals(operation)
                        || "VIEW_TRANS_2D_HEAD_UP".equals(operation)
                        || "VIEW_TRANS_2D_NORTH_UP".equals(operation)
                        || "VIEW_TRANS_3D_HEAD_UP".equals(operation)
                        || "CANCEL_MAP".equals(operation)
                        || "CLOSE_MAP".equals(operation)
                        || "CONFIRM".equals(operation)
                        || "OPEN".equals(operation)
                        || "NAVI_INFO".equals(operation)
                        || "USR_POI_QUERY".equals(operation)
                        || "ZOOM_IN".equals(operation)
                        || "ZOOM_OUT".equals(operation)
                        || "ROUTE_PLAN".equals(operation)
                        || "POS_RANK".equals(operation)
                        || "COLLECT".equals(operation)
                        || "NEXT".equals(operation)
                        || "PASS_AWAY".equals(operation)
                        || "OPEN_TRAFFIC_INFO".equals(operation)
                        || "CLOSE_TRAFFIC_INFO".equals(operation)
                        || ("CLOSE".equals(operation) && !XmMapNaviManager.getInstance().isNaviForeground())
                        || "ALONG_SEARCH".equals(operation));
    }

    public boolean isMapAction() {
        return "mapU".equals(service) && ("LOCATE_ROAD".equals(operation) || "LOCATE".equals(operation));
    }

    public boolean isMusicAction() {
        if (!TextUtils.isEmpty(getSlots())) {
            try {
                JSONObject slot = new JSONObject(getSlots());
                return "musicX".equals(service) || ("INSTRUCTION".equals(operation) && "REPLAY_ANSWER".equals(slot.getString("insType")));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return "musicX".equals(service);
        }
    }

    public boolean isStartMusicAction() {
        return "music".equals(service) && "PLAY".equals(operation) && TextUtils.isEmpty(getSlots());
    }

    public boolean isStartLocalMusicAction() {
        return "music".equals(service) && "PLAY".equals(operation) && !TextUtils.isEmpty(getSlots());
    }

    public boolean isWeatherAction() {
        return "weather".equals(service) || "pm25".equalsIgnoreCase(service);
    }

    public boolean isFlightAction() {
        return "flight".equals(service);
    }

    public boolean isConstellationAction() {
        return "constellation".equals(service);
    }

    public boolean isTextAnswerAction() {
        return !"datetime".equals(service) && ("ANSWER".equals(operation) || "EXCHANGE_BY_ALLINFO".equals(operation));
    }

    public boolean isRadioAction() {
        return "radio".equals(service) || "variety".equals(service);
    }

    public boolean isProgramAction() {
        return "internetRadio".equals(service);
    }

    public boolean isFmAction() {
        try {
            JSONObject slot = new JSONObject(getSlots());
            return "radio".equals(service) && ("fm".equals(slot.getString("waveband")) || "am".equals(slot.getString("waveband")));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isScheduleAction() {
        return "scheduleX".equals(service);
    }

    public boolean isCreateScheduleAction() {
        return "schedule".equals(service) && "CREATE".equals(operation);
    }

    public boolean isQueryScheduleAction() {
        return "schedule".equals(service) && "VIEW".equals(operation);
    }

    public boolean isAppControl() {
        return "app".equals(service) && ("LAUNCH".equals(operation) || "EXIT".equals(operation) || "QUERY".equals(operation));
    }

    public boolean isLaunchRadioAction() {
        return "radio".equals(service) && "LAUNCH".equals(operation);
    }

    public boolean isQueryPictureAction() {
        return "app".equals(service) && "QUERY".equals(operation);
    }

    public boolean isVehicleAction() {
        return "vehicleInfo".equals(service) && "QUERY".equals(operation);
    }

    public boolean isAppAction() {
        return "app".equals(service);
    }

    public boolean isExitAppAction() {
        return "app".equals(service) && "EXIT".equals(operation);
    }

    public boolean isPhoneCallAction() {
        return "telephone".equals(service);
    }

    public boolean isQueryContacts() {
        return "contacts".equals(service);
    }

    public boolean isCarFaultAction() {
        return "fault".equals(service) && "FAULT_ON".equals(operation);
    }

    public boolean isTranslateAction() {
        return "translation".equals(service);
    }

    public boolean isGasOnlineAction() {
        return ("gasoline".equals(service) && "GAS".equals(operation) || "oilprice".equals(service));
    }

    public boolean isStockAction() {
        return "stock".equals(service);
    }

    public boolean isTrainQueryAction() {
        return "train".equals(service);
    }

    public boolean isWebsiteAction() {
        return "website".equals(service) && "OPEN".equals(operation);
    }

    public boolean isWebSearchAction() {
        return "websearch".equals(service) && "QUERY".equals(operation);
    }

    public boolean isDateTimeQueryAction() {
        return "datetimeX".equals(service);
    }

    public boolean isQueryViolationAction() {
        return "queryviolation".equals(service) && "QUERYVIOLATION".equals(operation);
    }

    public boolean isTrafficAction() {
        return "traffic".equals(service) || "TRAFFIC".equals(operation) || "QUERY_TRAFFIC_INFO".equals(operation);
    }

    public boolean isNearbyCarAction() {
        return "nearby".equalsIgnoreCase(service) && "SEARCHCAR".equalsIgnoreCase(operation);
    }

    public boolean isParkSearchAction() {
        return "park".equalsIgnoreCase(service) && "PARK".equalsIgnoreCase(operation);
    }

    public boolean isLimitAction() {
        return ("carNumber".equalsIgnoreCase(service)) || ("life".equalsIgnoreCase(service)
                && "LIMIT".equalsIgnoreCase(operation));
    }

    public boolean isConstactSearchAction() {
        return "contacts".equals(service);
    }

    //车载版空调控控制解析
    public boolean isAirControlAction() {
        return "airControl".equals(service) && ("SET".equals(operation)
                || "INSTRUCTION".equals(operation)
                || "CLOSE".equals(operation) || "OPEN".equals(operation))
                || "heater_smartHome".equals(service);
    }

    public boolean isInstructionAction() {
        return "INSTRUCTION".equals(operation) || ("mapU".equals(service) && "DISPLAY_MODE_AUTO".equals(operation));
    }


    public boolean isHistoryToday() {
//        return "LEIQIAO.historyToday".equals(service);
        return "historyToday".equals(service);
    }

    public boolean isVideo() {
        return "video".equals(service) && "INSTRUCTION".equals(operation);
    }


    public boolean isSmartHome() {
        return "smartHome".equals(service) && "INSTRUCTION".equals(operation);
    }

    public boolean isAllSmartHome() {
        return "all_smartHome".equals(service) && "DEFAULT".equals(operation);
    }

    public boolean isFlow() {
        return "dataTransfer".equals(service);
    }

    public boolean isAirControlOpenAction() {
        return "airControl".equals(service) && "OPEN".equals(operation);
    }

    public boolean isAirControlCloseAction() {
        return "airControl".equals(service) && "CLOSE".equals(operation);
    }

    public boolean isAirControlSetAction() {
        return "airControl".equals(service) && "SET".equals(operation);
    }

    public boolean isCarControlSetAction() {
        return "carControl".equals(service);
    }

    public boolean isCmdAciton() {
        return "cmd".equalsIgnoreCase(service);
    }

    public boolean isHelpAciton() {
        return "help".equals(service) && "LAUNCH".equals(operation);
    }

    public boolean isItemSelectAciton() {
        return "itemSelect".equalsIgnoreCase(service) && "SELECT".equalsIgnoreCase(operation);
    }

    public boolean isCmdTimeAction() {
        if (isCmdAciton()) {
            String slot = getSlots();
            try {
                JSONObject slots = new JSONObject(slot);
                if (slots != null && slots.has("name")) {
                    String name = slots.getString("name");
                    if ("hour".equals(name) || "day".equals(name)) { //日期
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean isChangeWakeupAction() {
        if (isCmdAciton()) {
            String slot = getSlots();
            try {
                JSONObject slots = new JSONObject(slot);
                if (slots != null && slots.has("category")) {
                    String name = slots.getString("category");
                    if ("唤醒词修改".equals(name)) { //唤醒词
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (matchWakeUpAction(getText())) {
            return true;
        }
        return false;
    }

    public boolean isMessageAciton() {
        return "message".equals(service) || "weixin".equals(service);
    }

    public boolean isSureXfOfflineParser(String voiceContent) {
        return (isMusicAction() && !isStartMusicAction()) || isRoutAction() || isMapAction()
                || isPhoneCallAction() || (isRadioAction() && !"人物选择".equals(voiceContent));
    }

    public boolean isPoiLbsQuery() {
        return "lbs".equalsIgnoreCase(service) || "hotel".equalsIgnoreCase(service)
                || "restaurant".equalsIgnoreCase(service);
    }

    public boolean isOilPriceQuery() {
        return "oilprice".equalsIgnoreCase(service);
    }


    public boolean isPictureAction() {
        return "picture".equals(service);
    }


    public boolean isUserOfflineParserResult() {
        return !"answer".equals(service)
                && !"schedule".equals(service)
                && !"baike".equals(service)
                && !"joke".equals(service)
                && !"chat".equals(service)
                && !"rescue".equals(service)
                && !"restaurant".equals(service)
                && !"pattern".equals(service)
                && !"websearch".equals(service)
                && !"poetry".equals(service)
                && !"cinemas".equals(service)
                && !"novel".equals(service)
                && !"lottery".equals(service)
                && !"riddle".equals(service)
                && !"itemSelect".equals(service)
                && !"calc".equals(service)
                && !"forex".equals(service)
                && !"iFlytekQA".equals(service)
                && !"length".equals(service)
                && !"photoView".equals(service)
                && !"stock".equals(service)
                && !isQueryName();
    }

    private boolean matchWakeUpAction(String word) {
        if (TextUtils.isEmpty(word))
            return false;
        boolean isMatch = false;
        if (Pattern.matches(".*给你起个名字叫.*", word) || Pattern.matches(".*给你取个名字叫.*", word)) {
            isMatch = true;
        }

        return isMatch;
    }


    public boolean isQueryName() {
        return "personalName".equals(service) && "QUERY".equals(operation);
    }
}
