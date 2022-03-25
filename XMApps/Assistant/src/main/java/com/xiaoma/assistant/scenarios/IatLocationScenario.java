package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.Interceptor.interceptor.NavigationInterceptor;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.mapadapter.listener.OnGeocodeSearchListener;
import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.manager.GeocodeSearch;
import com.xiaoma.mapadapter.manager.PoiSearch;
import com.xiaoma.mapadapter.model.CoordinateType;
import com.xiaoma.mapadapter.model.LatLonPoint;
import com.xiaoma.mapadapter.model.PoiInfo;
import com.xiaoma.mapadapter.model.PoiResult;
import com.xiaoma.mapadapter.model.QueryOption;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;
import com.xiaoma.mapadapter.model.RegeocodeResultInfo;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONObject;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：定位场景
 */
public class IatLocationScenario extends IatScenario {

    private ParserLocationParam locationParam;
    private boolean searchPoi;

    public IatLocationScenario(Context context) {
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
                    XmMapNaviManager.getInstance().showPoiDetail(poiBean.getLongitude(), poiBean.getLatitude());
                    InterceptorManager.getInstance().setCurrentInterceptor(
                            new NavigationInterceptor(searchKey, poiBean.getAddress(), poiBean.getLongitude(), poiBean.getLatitude()),
                            StringUtil.format(context.getString(R.string.do_you_want_to_navigate), searchKey, poiBean.getAddress()));
                } else {
                    speakCanNotGetLongitude();
                }
            }

        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam locationParam, long session) {
        this.locationParam = locationParam;
        this.parseResult = parseResult;
        doParserNew();
    }

    private void doParserNew() {
        String slotsText = parseResult.getSlots();
        ParserLocation parserLocation = new ParserLocation();
        try {
            JSONObject jsonObject = new JSONObject(slotsText);
            if (jsonObject.has("endLoc")) {
                String endLoc = jsonObject.getString("endLoc");
                JSONObject endLocObject = new JSONObject(endLoc);
                if (endLocObject.has("ori_loc")) {
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_LOCATION);
                    String poi = endLocObject.getString("ori_loc");
                    parserLocation.setPoi(poi);
                    if (poi.equals("CURRENT_ORI_LOC")) {
                        parserLocation.setPoi("CURRENT_POI");
                        XmMapNaviManager.getInstance().showCarPosition();
                        PoiBean carPosition = XmMapNaviManager.getInstance().getCarPosition();
                        if (carPosition != null) {
                            //显示当前位置，提前关闭窗口再播报
                            closeVoicePopup();
                            XmMapNaviManager.getInstance().switchToLauncher();
                            assistantManager.speakContent(context.getString(R.string.current_position_is, carPosition.getName()));
                        } else {
                            speakNoLocation();
                        }
                    } else {
                        KLog.d("MrMine", "doParserNew: " + "findLocation");
                        searchPoi(poi);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            findMyLocation(parserLocation);
        }
    }

    private void searchPoi(String searchKey) {
        searchPoi = true;
        XmMapNaviManager.getInstance().searchByKey(searchKey);
    }

    private void doParserOld() {
        String slotsText = parseResult.getSlots();
        Slots slots = Slots.fromJson(slotsText);
        if (slots == null || slots.getLocation() == null) {
            addFeedbackAndSpeak(getString(R.string.error_cannot_location));
            return;
        }
        ParserLocation location = slots.getLocation();
        final String poi = location.getPoi();
        if (TextUtils.isEmpty(poi)) {
            constitutePOI(location);//湖南衡山在哪里 宝安区在哪里 没有POI处理
            findLocation(location);
            return;
        }
        if (!poi.equals("CURRENT_POI")) {//例如查询世界之窗在哪
            findLocation(location);
            return;
        }
        //我在哪
        if (locationParam == null) {
            addFeedbackAndSpeak(getString(R.string.error_cannot_location));
            return;
        }
        findMyLocation(location);
    }


    /**
     * poi 构造
     *
     * @param location 位置
     */
    private void constitutePOI(ParserLocation location) {
        if (TextUtils.isEmpty(location.getCity()) && !TextUtils.isEmpty(location.getArea())) {
            location.setCity(location.getArea());
        }
        if (!TextUtils.isEmpty(location.getAreaAddr())) {
            location.setPoi(location.getAreaAddr());
        } else if (!TextUtils.isEmpty(location.getArea())) {
            location.setPoi(location.getArea());
        } else if (!TextUtils.isEmpty(location.getCityAddr())) {
            location.setPoi(location.getCityAddr());
        } else if (!TextUtils.isEmpty(location.getProvince())) {
            location.setPoi(location.getProvince());
        } else if (!TextUtils.isEmpty(location.getProvinceAddr())) {
            location.setPoi(location.getProvinceAddr());
        }
    }

    private void findLocation(final ParserLocation location) {
        String poiName = location.getPoi();
        String city = location.getCity();
        String cityAddr = location.getCityAddr();
        String areaAddr = location.getAreaAddr();
        if (TextUtils.isEmpty(city)) {
            city = "";
        }
        if (city.equals("CURRENT_CITY") && locationParam != null) {
            city = locationParam.city;
        }
        if (city == null) {
            addFeedbackAndSpeak(getString(R.string.position_search_error));
            return;
        }
        if (TextUtils.isEmpty(poiName)) {
            if (!TextUtils.isEmpty(city)) {
                poiName = city;
            } else {
                if (!TextUtils.isEmpty(cityAddr)) {
                    poiName = cityAddr;
                } else {
                    if (!TextUtils.isEmpty(areaAddr)) {
                        poiName = areaAddr;
                    }
                }
            }
        }
        QueryOption query = new QueryOption(poiName, "", city);
        query.setPageSize(1);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(context);
        poiSearch.setQueryOption(query);
        poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                if (rCode != 1000 || poiResult == null || poiResult.getPoiInfoList() == null ||
                        poiResult.getPoiInfoList().isEmpty()) {
                    speakUnderstand();
                    return;
                }
                LatLonPoint point = poiResult.getPoiInfoList().get(0).getLatLonPoint();
                String poiLocation = String.format("%f,%f", point.getLongitude(), point.getLatitude());
                String address = poiResult.getPoiInfoList().get(0).getAddress();
                if (TextUtils.isEmpty(address)) {
                    address = poiResult.getPoiInfoList().get(0).getSnippet();
                }
                if (!address.contains(poiResult.getPoiInfoList().get(0).getCityName())) {
                    address = poiResult.getPoiInfoList().get(0).getCityName() + address;
                }
                location.setLocation(poiLocation);
                location.setIS_CURRENT_POI(false);
                location.setPoi(address);
                location.setAreaAddr(address);
                speakContent(StringUtil.format(context.getString(R.string.get_current_position), address));
                //创建一个conversation并展示
                ConversationItem conversationItem = parseResult.createReceiveConversation(
                        VrConstants.ConversationType.LOCATION, location);
                addConversationToList(conversationItem);
            }

        });
        poiSearch.doPoiSearch();
    }

    private void findMyLocation(final ParserLocation location) {
        try {
            LatLonPoint point = getCurrentLatLonPoint();
            GeocodeSearch geocoderSearch = new GeocodeSearch(context);
            geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResultInfo regeocodeResult, int rCode) {
                    if (rCode != 1000) {
                        speakUnderstand();
                        return;
                    }
                    String poi;
                    List<PoiInfo> poiInfoList = regeocodeResult.getPoiInfoList();
                    if (poiInfoList != null && poiInfoList.size() > 0) {
                        poi = regeocodeResult.getPoiInfoList().get(0).getAddress();
                    } else {
                        poi = regeocodeResult.getFormatAddress();
                    }
                    location.setPoi(poi);
                    // 当前位置查询
                    /*if (!TextUtils.isEmpty(poi)) {
                        LocalControlManager.getInstance().setSceneActionAndDuration(15, poi.length());
                    }*/
                    location.setAreaAddr(regeocodeResult.getFormatAddress());
                    location.setLocation(locationParam.location);
                    location.setIS_CURRENT_POI(true);
                    speakContent(StringUtil.format(context.getString(R.string.get_current_position), regeocodeResult.getFormatAddress()));
                    //创建一个conversation并展示
                    ConversationItem conversationItem = parseResult.createReceiveConversation(
                            VrConstants.ConversationType.LOCATION, location);
                    addConversationToList(conversationItem);
                }

            });
            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQueryOption query = new RegeocodeQueryOption(point, 100, CoordinateType.GD);
            geocoderSearch.getFromLocationAsyn(query);
        } catch (Exception e) {
            speakUnderstand();
        }
    }

    private LatLonPoint getCurrentLatLonPoint() {
        String[] splits = this.locationParam.location.split(",");
        return new LatLonPoint(Double.parseDouble(splits[1]), Double.parseDouble(splits[0]));
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

        private ParserLocation location;

        public ParserLocation getLocation() {
            return location;
        }

        public void setLocation(ParserLocation location) {
            this.location = location;
        }

        public static Slots fromJson(String result) {
            Slots slots = GsonHelper.fromJson(result, Slots.class);
            return slots;
        }
    }
}
