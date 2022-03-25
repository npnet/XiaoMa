package com.xiaoma.assistant.model.parser;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.xiaoma.vr.model.ConversationItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：讯飞解析返回结果
 */
public class ParseResult {
    protected int rc = 4;
    protected String text;
    protected String service;
    protected String operation;
    protected Semantic semantic;
    protected String answer;
    protected String webPage;
    //后面两个参数离线时才使用
    protected String moreResults;
    protected String data;
    protected String answerText;
    protected QueryAddress queryAddress;
    protected boolean isOpenSemantic;

    @Expose(serialize = false)
    private String inputResult;

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Semantic getSemantic() {
        return semantic;
    }

    public void setSemantic(Semantic semantic) {
        this.semantic = semantic;
    }

    public String getSlots() {
        if (getSemantic() == null) {
            return "";
        }
        return getSemantic().getSlots();
    }

    public String getMoreResults() {
        return moreResults;
    }

    public void setMoreResults(String moreResults) {
        this.moreResults = moreResults;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isRoutAction() {
        return (service.equals("map") && !"POSITION".equals(operation))|| service.equals("restaurant") ||
                service.equals("lbs") || service.equals("hotel") ||
                service.equals("publicBus") || service.equals("station");
    }

    public boolean isMapAction() {
        return service.equals("map") && operation.equals("POSITION");
    }

    public boolean isMusicAction() {
        return service.equals("music") && (operation.equals("PLAY") || operation.equals("SEARCH"));
    }

    public boolean isOpenSemantic() {
        return isOpenSemantic;
    }

    public void setOpenSemantic(boolean openSemantic) {
        isOpenSemantic = openSemantic;
    }

    public static ParseResult fromJson(String input) {
        ParseResult parseResult = new ParseResult();
        parseResult.setInputResult(input);
        try {
            JSONObject jsonObject = new JSONObject(input);
            parseResult.setRc(jsonObject.optInt("rc", 4));
            parseResult.setOperation(jsonObject.optString("operation"));
            if (jsonObject.has("semantic")) {
                JSONObject semantic = jsonObject.optJSONObject("semantic");
                Semantic semantic1 = new Semantic();
                semantic1.setSlots(semantic.optString("slots"));
                parseResult.setSemantic(semantic1);
            }
            String answer = jsonObject.optString("answer");
            parseResult.setService(jsonObject.optString("service"));
            parseResult.setText(jsonObject.optString("text"));
            parseResult.setWebPage(jsonObject.optString("webPage"));
            parseResult.setAnswer(answer);
            if (jsonObject.has("moreResults")){
                parseResult.setMoreResults(jsonObject.optString("moreResults"));
            }
            if (jsonObject.has("data")){
                parseResult.setData(jsonObject.optString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (parseResult == null) {
            parseResult = new ParseResult();
        }
        return parseResult;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public boolean isWeatherAction() {
        return service.equals("weather") && operation.equals("QUERY");
    }

    public boolean isFlightAction() {
        return service.equals("flight") && operation.equals("QUERY");
    }

    public boolean isConstellationAction() {
        return service.equals("life") && operation.equals("CONSTELLATION");
    }

    public boolean isTextAnswerAction() {
        return !service.equals("datetime") && operation.equals("ANSWER");
    }

    public boolean isRadioAction() {
        return service.equals("radio");
    }

    public boolean isFmAction(){
        return false;
    }


    public boolean isPhoneCallAction() {
        return service.equals("telephone") && operation.equals("CALL");
    }

    public boolean isCarFaultAction() {
        return service.equals("fault") && operation.equals("FAULT_ON");
    }

    public boolean isTranslateAction() {
        return service.equals("translation") && operation.equals("TRANSLATION");
    }

    public boolean isGasOnlineAction() {
        return service.equals("gasoline") && operation.equals("GAS");
    }

    public boolean isStockAction() {
        return service.equals("stock") && operation.equals("QUERY");
    }

    public boolean isTrainQueryAction() {
        return service.equals("train") && operation.equals("QUERY");
    }

    public boolean isWebsiteAction() {
        return service.equals("website") && operation.equals("OPEN");
    }

    public boolean isWebSearchAction() {
        return service.equals("websearch") && operation.equals("QUERY");
    }

    public boolean isDateTimeQueryAction() {
        return service.equals("datetime") && operation.equals("ANSWER");
    }

    public boolean isQueryViolationAction() {
        return "queryviolation".equals(service) && "QUERYVIOLATION".equals(operation);
    }

    public boolean isTrafficAction() {
        return "traffic".equals(service) && "TRAFFIC".equals(operation);
    }

    public boolean isNearbyCarAction() {
        return "nearby".equalsIgnoreCase(service) && "SEARCHCAR".equalsIgnoreCase(operation);
    }

    public boolean isNewsAction() {
        return "news".equals(service);
    }


    public boolean isParkSearchAction() {
        return "park".equalsIgnoreCase(service) && "PARK".equalsIgnoreCase(operation);
    }

    public boolean isLimitAction() {
        return "life".equalsIgnoreCase(service) && "LIMIT".equalsIgnoreCase(operation);
    }

    public boolean isScheduleAction() {
        return "schedule".equalsIgnoreCase(service) && "CREATE".equalsIgnoreCase(operation);
    }

    public boolean isImageSearchAction() {
        return "picture".equalsIgnoreCase(service) && "QUERY".equalsIgnoreCase(operation);
    }

    public boolean isImageQueryAction() {
        return "photoView".equalsIgnoreCase(service) && "QUERY".equalsIgnoreCase(operation);
    }

    public boolean isTravelSearchAction() {
        return "travel".equalsIgnoreCase(service) && "SEARCHTRAVEL".equalsIgnoreCase(operation);
    }

    public boolean isRestaurantSearchAction() {
        return "cate".equalsIgnoreCase(service) && "SEARCH".equalsIgnoreCase(operation);
    }

    public boolean isConstactSearchAction(){
        return service.equals("contacts");
    }


    public boolean isCmdAciton(){
        return false;
    }

    public boolean isCmdTimeAction(){
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


    private boolean matchWakeUpAction(String word) {
        if (TextUtils.isEmpty(word))
            return false;
        boolean isMatch = false;
        if (Pattern.matches(".*给你起个名字叫.*", word) || Pattern.matches(".*给你取个名字叫.*", word)) {
            isMatch = true;
        }

        return isMatch;
    }


    public boolean isTempControlAction() {
        return "aircondition".equalsIgnoreCase(service) && "AIR_TEMP".equalsIgnoreCase(operation);
    }

    public boolean isWindControlAction() {
        return "aircondition".equalsIgnoreCase(service) && "WIND_POWER".equalsIgnoreCase(operation);
    }


    //车载版空调控控制解析
    public boolean isAirControlAction() {
        return service.equals("airControl") && operation.equals("SET");
    }


    public boolean isHotel(){
        return service.equals("hotel");
    }

    public boolean isBusStation(){
        return service.equals("publicBus");
    }

    public boolean isStation(){
        return service.equals("station");
    }

    public boolean isRestaurant(){
        return service.equals("restaurant");
    }


    public String getInputResult() {
        return inputResult;
    }

    public void setInputResult(String inputResult) {
        this.inputResult = inputResult;
    }

    public static class Semantic {
        private String slots;

        public String getSlots() {
            return slots;
        }

        public void setSlots(String slots) {
            this.slots = slots;
        }
    }


    public String toJson(){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private ConversationItem toConversationItem(int action,Object attachment, int conversationType) {
        ConversationItem conversationItem = new ConversationItem();
        //conversationItem.setId(id);
        conversationItem.setAction(action);
        conversationItem.setOperation(operation);
        conversationItem.setService(service);
        conversationItem.setText(text);
        //conversationItem.setData(data);
        conversationItem.setAttachment(attachment);
        conversationItem.setConversationType(conversationType);
        return conversationItem;
    }


    public ConversationItem createSendConversation(int action, Object attachment){
        return toConversationItem(action,attachment,ConversationItem.CONVERSATION_INPUT);
    }

    public ConversationItem createReceiveConversation(int action, Object attachment){
        return toConversationItem(action,attachment,ConversationItem.CONVERSATION_RESPONSE);
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public QueryAddress getQueryAddress() {
        return queryAddress;
    }

    public void setQueryAddress(QueryAddress addressBean) {
        this.queryAddress = addressBean;
    }

    public static class QueryAddress{
        double longitude;
        double latitude;
        String address;

        public QueryAddress(double longitude, double latitude, String address) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.address = address;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

    public static class  DataBean{

        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * fuzzy_score : 1
             * id :
             * name : 王浩
             * phoneNumber :
             * score : 1
             * source : personal
             * type :
             */

            private String id;
            private String name;
            private String phoneNumber;
            private String source;
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

}
