package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.TrafficInfo;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：路况场景
 */
public class IatTrafficScenario extends IatScenario {

    private boolean searchPoi;

    public IatTrafficScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {
        XmMapNaviManager.getInstance().setXmMapNaviManagerCallBack(new SimpleXmMapNaviManagerCallBack() {
            @Override
            public void onSearchResult(String searchKey, int errorCode, List<PoiBean> searchResults) {
                if (searchPoi) {
                    searchPoi = false;
                } else {
                    return;
                }
                if (!ListUtils.isEmpty(searchResults)) {
                    PoiBean poiBean = searchResults.get(0);
                    showTmcForPoi(poiBean.getLongitude(), poiBean.getLatitude());
                } else {
                    speakCanNotGetLongitude();
                }
            }
        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        if (location == null && !BuildConfig.DEBUG) {
            speakContent(getString(R.string.position_search_error));
            return;
        }
        String city = location != null ? location.city : LocationManager.DebugLocation.city;
        String street = "";

        String slotJson = parseResult.getSlots();
        if (TextUtils.isEmpty(slotJson) || slotJson.equals("{}")) {
            speakUnderstand();
            return;
        } else {
            TrafficSlot slot = GsonHelper.fromJson(slotJson, TrafficSlot.class);
            if (!TextUtils.isEmpty(slot.insType)) {
                if ("QUERY_TRAFFIC_INFO".equals(slot.insType)) {
                    if (slot.endLoc != null) {
                        String endAddress = slot.endLoc.ori_loc;
                        if ("CURRENT_ORI_LOC".equals(slot.startLoc.ori_loc)) {
                            // 查询endAddress的路况
                            searchPoi(endAddress);
                        } else {
                            String startAddress = slot.startLoc.ori_loc;
                            //TODO 查询从startAddress到endAddress的路况 暂缺四维接口
                            addFeedbackAndSpeak(context.getString(R.string.car_control_un_enable));
                        }

                    } else if (slot.keyword != null) {
                        // 查询slot.keyword的路况
                        searchPoi(slot.keyword.ori_loc);
//                        queryTraffic(city, slot.keyword.ori_loc);
                    } else {
                        // 查询前方路况
                        final LocationInfo currentLocation = LocationManager.getInstance().getCurrentLocation();
                        double lon = currentLocation != null ? currentLocation.getLongitude() : LocationManager.DebugLocation.longitude;
                        double lat = currentLocation != null ? currentLocation.getLatitude() : LocationManager.DebugLocation.latitude;
                        showTmcForPoi(lon, lat);
                    }
                }
            }
        }



     /*
        IatLimitScenario.Slots slots = GsonHelper.fromJson(parseResult.getSlots(),IatLimitScenario.Slots.class);
        if (slots == null || slots.getLocation() == null){
            //parserCannotHandler(parseResult.getText(),callback);
            speakUnderstand();
            return;
        }

        if (!TextUtils.isEmpty(slots.getLocation().getStreet())){
            street = slots.getLocation().getStreet();
        } else if (!TextUtils.isEmpty(slots.getLocation().getPoi())){
            street = slots.getLocation().getPoi();
        } else if (!TextUtils.isEmpty(slots.getLocation().getArea())) {
            street = slots.getLocation().getArea();
        } else if (!TextUtils.isEmpty(slots.getLocation().getCity())) {
            street = slots.getLocation().getCity();
        } else {
            //parserCannotHandler(parseResult.getText(),callback);
            speakUnderstand();
            return;
        }

        if (!TextUtils.isEmpty(slots.getLocation().getCityAddr())){
            city = slots.getLocation().getCityAddr();
        } else if (!TextUtils.isEmpty(slots.getLocation().getCity())){
            city = slots.getLocation().getCity();
        }

        if("CURRENT_CITY".equals(city) && location != null){
            city = location.city;
        }*/

//        queryTraffic(city, street);
    }

    private void searchPoi(String address) {
        searchPoi = true;
        XmMapNaviManager.getInstance().searchByKey(address);
    }

    private void showTmcForPoi(double lon, double lat) {
        XmMapNaviManager.getInstance().showTmcForPoi(lon, lat);
        speakContent(context.getString(R.string.please_check_road_condition),new WrapperSynthesizerListener(){
            @Override
            public void onCompleted() {
                closeVoicePopup();
            }

            @Override
            public void onError(int code) {
                closeVoicePopup();
            }
        });
    }

    private void speakAndClose(String text) {
        speakContent(text, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                assistantManager.closeAssistant();
            }
        });
    }

    private void queryTraffic(String city, String street) {
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().fetchTrafficInfo(city, street, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                TrafficInfo trafficInfo = GsonHelper.fromJson(result, TrafficInfo.class);
                if (trafficInfo == null) {
                    showError(getString(R.string.query_traffic_error));
                    return;
                }
                if (!trafficInfo.isSuccess()) {
//                        showError(trafficInfo.resultMessage);
                    showError(getString(R.string.query_traffic_error));
                    return;
                }
                if (trafficInfo.getData() == null) {
                    showError(getString(R.string.query_traffic_error));
                    return;
                }
                String speakText = StringUtil.isNotEmpty(trafficInfo.getData().getDescription()) ?
                        trafficInfo.getData().getDescription() : "";
                addFeedBackConversation(context.getString(R.string.traffic_road_result_is));
                XmTtsManager.getInstance().stopSpeaking();
                speakContent(speakText);
                //创建一个conversation并展示
                ConversationItem conversationItem = parseResult.createReceiveConversation(
                        VrConstants.ConversationType.TRAFFIC, trafficInfo.getData().getDescription());
                addConversationToList(conversationItem);
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                String errorText = getString(R.string.network_errors);
                showError(errorText, true);
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
        showError(errorText, false);
    }


    private void showError(String errorText, boolean isNetworkError) {
        if (TextUtils.isEmpty(errorText)) {
            isNetworkError = true;
            errorText = getString(R.string.no_data_response);
        }
        addFeedBackConversation(errorText);
        speakContent(errorText, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
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

    class TrafficSlot {

        /**
         * endLoc : {"ori_loc":"深南大","topic":"others"}
         * insType : QUERY_TRAFFIC_INFO
         * startLoc : {"ori_loc":"CURRENT_ORI_LOC"}
         */

        public EndLocBean endLoc;
        public String insType;
        public StartLocBean startLoc;
        public KeyWord keyword;

        class EndLocBean {
            public String ori_loc;
            public String topic;
        }

        class StartLocBean {
            public String ori_loc;
        }

        class KeyWord {
            public String ori_loc;
        }
    }
}
