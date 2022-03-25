package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.callback.XmMapNaviManagerCallBack;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.callback.SortChooseCallback;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.HotelSlots;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.HotelBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.result.HotelResult;
import com.xiaoma.assistant.model.result.SpecialResultCallback;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.HotelListAdapter;
import com.xiaoma.assistant.utils.SortUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/8 14:30
 * Desc: 酒店场景
 */
public class IatHotelScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private HotelListAdapter adapter;
    private int pageNo = 1;
    private static final int RANGE = 50;
    private List<HotelBean> hotelBeans;
    private boolean reserveHotel; //预订酒店
    private HotelSlots hotelSlots;
    private ParserLocationParam location;
    private boolean searchPoi;
    private String city;

    public IatHotelScenario(Context context) {
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
                    city = poiBean.getCityName();
                    queryHotel(location, hotelSlots, true, String.valueOf(poiBean.getLatitude()), String.valueOf(poiBean.getLongitude()));
                } else {
                    speakCanNotGetLongitude();
                }
            }
        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        city = "";
        setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
        if ("BOOK".equals(parseResult.getOperation())) {
            // 预订酒店
            reserveHotel = true;
        }

        String slots = parseResult.getSlots();
        if (TextUtils.isEmpty(slots) || slots.equals("{}")) {
            speakUnderstand();
            return;
        }
        hotelSlots = GsonHelper.fromJson(slots, HotelSlots.class);
        if (hotelSlots == null) {
            speakUnderstand();
            return;
        }
        this.location = location;
        if (hotelSlots.getLandmark() != null && !TextUtils.isEmpty(hotelSlots.getLandmark().getOri_loc()) && !"CURRENT_ORI_LOC".equals(hotelSlots.getLandmark().getOri_loc())) {
            // 搜索地址
            String address = hotelSlots.getLandmark().getOri_loc();
            switch (address) {
                case "家":
                case "公司":
                    if (parseResult.getQueryAddress() != null) {
                        queryHotel(location, hotelSlots, true, String.valueOf(parseResult.getQueryAddress().getLatitude()), String.valueOf(parseResult.getQueryAddress().getLongitude()));
                    }
                    return;
                default:
                    searchPoi = true;
                    XmMapNaviManager.getInstance().searchByKey(address);
                    return;
            }
        }

        queryHotel(location, hotelSlots, false, null, null);
    }

    private void queryHotel(ParserLocationParam location, HotelSlots hotelSlots, boolean addressCondition, String lat, String lon) {
        String price = "";
        String starLevel = "";
        boolean starCondition = false;
        String priceCondition = "";

        if (hotelSlots.getLandmark() != null && !TextUtils.isEmpty(hotelSlots.getLandmark().getCity())) {
            city = hotelSlots.getLandmark().getCity();
        }

        LatLng currentPosition = LocationManager.getInstance().getCurrentPosition();
        if (!BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(city) || TextUtils.isEmpty(lat)) {
                boolean searchNearHere = hotelSlots.getLandmark() != null && "CURRENT_ORI_LOC".equals(hotelSlots.getLandmark().getOri_loc());
                if ((currentPosition == null || location == null) && searchNearHere) {
                    speakNoLocation();
                    return;
                }
            }
        }

        if (location != null && TextUtils.isEmpty(city)) {
            city = location.city;
        }
        if (currentPosition != null && TextUtils.isEmpty(lat)) {
            lat = String.valueOf(currentPosition.latitude);
            lon = String.valueOf(currentPosition.longitude);
        }

        if (hotelSlots.getProperty() != null && hotelSlots.getProperty().getHotelLvl() != null && !TextUtils.isEmpty(hotelSlots.getProperty().getHotelLvl().getOffset())) {
            //TODO 星级
            starCondition = true;
            String star = hotelSlots.getProperty().getHotelLvl().getOffset();
            String newStar = changeToChinese(star);
            starLevel = newStar + "星级";
        }

        if (hotelSlots.getPrice() != null) {
            if ("MIN".equals(hotelSlots.getPrice().getLeftClosure())) {
                priceCondition = hotelSlots.getPrice().getRightClosure() + "元以内";
                price = "0-" + hotelSlots.getPrice().getRightClosure();
            } else {
                if ("MAX".equals(hotelSlots.getPrice().getRightClosure())) {
                    priceCondition = hotelSlots.getPrice().getLeftClosure() + "元以上";
                    price = hotelSlots.getPrice().getLeftClosure() + "-99999";
                } else { //XX元左右
                    priceCondition = hotelSlots.getPrice().getLeftClosure() + "元左右";
                    int closure = (int) Float.parseFloat(hotelSlots.getPrice().getLeftClosure());
                    price = (closure >= RANGE ? closure - RANGE : closure) + "-" + (closure <= 99999 - RANGE ? closure + RANGE : closure);
                }
            }
        }

        String hotelName = "";
        if (hotelSlots.getEndLoc() != null && (!TextUtils.isEmpty(hotelSlots.getEndLoc().getOri_loc()))) {
            hotelName = hotelSlots.getEndLoc().getOri_loc();
        }

        String[] date = TimeUtils.getCurrAndNextDate().split(" ");
        String checkIn = date[0];
        String checkOut = date[1];
        String sortCode = "";
        String sortType = "";
        String distance = "";

        final boolean finalAddressCondition = addressCondition;
        final boolean finalStarCondition = starCondition;
        final String finalPriceCondition = priceCondition;
        final String finalStarLevel = starLevel;
        final boolean finalReserveHotel = reserveHotel;

        RequestManager.newSingleton().searchHotel(starLevel, hotelName, city, pageNo, BaseMultiPageAdapter.PAGE_SIZE, checkIn, checkOut, sortCode, sortType, lat, lon, distance, price, new SpecialResultCallback<HotelResult>() {

            @Override
            public void onSuccess(HotelResult result) {
                List<HotelBean> data = result.getData();
//                hotelBeans = new ArrayList<>(data);
                hotelBeans = data;
                if (!ListUtils.isEmpty(data)) {
                    adapter = new HotelListAdapter(context, data);
                    adapter.setOnMultiPageItemClickListener(IatHotelScenario.this);
                    showMultiPageData(adapter, getTTSContent(finalAddressCondition, finalStarCondition, finalPriceCondition, data.size(), finalStarLevel, finalReserveHotel));
                } else {
                    XmTtsManager.getInstance().stopSpeaking();
                    addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                hotelBeans = null;
                if (!NetworkUtils.isConnected(context)) {
                    addFeedbackAndSpeak(getString(R.string.network_errors));
                } else {
                    if (code == 30011) {
                        addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                        return;
                    }
                    addFeedbackAndSpeak(getString(R.string.error_data_response));
                }
            }
        });
    }

    private String changeToChinese(String star) {
        String newStar = "";
        switch (star) {
            case "1":
                newStar = "一";
                break;
            case "2":
                newStar = "二";
                break;
            case "3":
                newStar = "三";
                break;
            case "4":
                newStar = "四";
                break;
            case "5":
                newStar = "五";
                break;
            default:
                newStar = star;
                break;
        }
        return newStar;
    }

    private String getTTSContent(boolean addressCondition, boolean starCondition, String priceCondition, int size, String starLevel, boolean reserveHotel) {
        if (reserveHotel) {
            return StringUtil.format(context.getString(R.string.find_hotel_with_book_intention), size);
        } else if (starCondition && TextUtils.isEmpty(priceCondition)) {
            return StringUtil.format(context.getString(R.string.find_hotel_with_star_condition), size, starLevel);
        } else if ((starCondition || addressCondition) && !TextUtils.isEmpty(priceCondition)) {
            return StringUtil.format(context.getString(R.string.find_hotel_with_condition), size);
        } else if (!TextUtils.isEmpty(priceCondition)) {
            return StringUtil.format(context.getString(R.string.find_hotel_with_price_condition), size, priceCondition);
        } else {
            return StringUtil.format(context.getString(R.string.resturant_search_result_tips), size);
        }
    }

    @Override
    public void onChoose(String voiceText) {
        final boolean isFirstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean isLastPage = assistantManager.getMultiPageView().isLastPage();
        switchChooseAction(voiceText, new SortChooseCallback() {
            @Override
            public void previousPageAction() {
                if (isFirstPage) {
                    KLog.d("It is first page");
                } else {
                    assistantManager.getMultiPageView().setPage(-1);
                }
            }

            @Override
            public void nextPageAction() {
                if (isLastPage) {
                    KLog.d("It is last page");
                } else {
                    assistantManager.getMultiPageView().setPage(1);
                }
            }

            @Override
            public void chooseItemAction(int action) {
                KLog.d("choose item");
                onItemClick(action);
            }

            @Override
            public void lastAction() {
                KLog.d(adapter.getCurrentList().get(adapter.getCurrentList().size() - 1));
                KLog.d("choose the last one");
            }

            @Override
            public void cancelChooseAction() {
//                stopListening();
//                assistantManager.hideMultiPageView();
//                startListening();
                assistantManager.closeAssistant();
            }

            @Override
            public void errorChooseActon() {
                KLog.d("choose error");
            }

            @Override
            public void assignPageAction(int page) {
                assistantManager.getMultiPageView().setPageForIndex(page);
            }

            @Override
            public void confirm() {
                super.confirm();
                onItemClick(0);
            }

            @Override
            public void cancel() {
                super.cancel();
                stopListening();
                assistantManager.hideMultiPageView();
                startListening();
            }

            @Override
            public void sortByPrice() {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByPrice(list, true);
                adapter.setData(list);
            }

            @Override
            public void sortByDistance() {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByDistance(list, true);
                adapter.setData(list);
            }

            @Override
            public void sortByScore() {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByScore(list, false);
                adapter.setData(list);
            }

            @Override
            public void sortByStarLevel() {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByStarLevel(list, false, context);
                adapter.setData(list);
            }

            @Override
            public void filterByStarLevel(String text) {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>();
                for (HotelBean bean : hotelBeans) {
                    if (text.contains(bean.getStarName())) {
                        if (text.contains("准") && !bean.getStarName().contains("准")) {
                            continue;
                        }
                        list.add(bean);
                    }
                }
                adapter.setData(list);
            }

            @Override
            public void filterByPrice(boolean isMinPrice) {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByPrice(list, true);
                List<HotelBean> tempList = new ArrayList<>();
                if (isMinPrice) {
                    tempList.add(list.get(0));
                } else {
                    tempList.add(list.get(list.size() - 1));
                }
                adapter.setData(tempList);
            }

            @Override
            public void filterByHighScore() {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByScore(list, false);
                List<HotelBean> tempList = new ArrayList<>();
                tempList.add(list.get(0));
                adapter.setData(tempList);
            }

            @Override
            public void filterByDistance(boolean isMinDistance) {
                if (ListUtils.isEmpty(hotelBeans)) return;
                List<HotelBean> list = new ArrayList<>(hotelBeans);
                SortUtils.sortHotelByDistance(list, true);
                List<HotelBean> tempList = new ArrayList<>();
                if (isMinDistance) {
                    tempList.add(list.get(0));
                } else {
                    tempList.add(list.get(list.size() - 1));
                }
                adapter.setData(tempList);
            }
        });
    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }

    @Override
    protected String getSrSceneStksCmd() {
        if (assistantManager == null) {
            return "";
        }
        if (assistantManager.getMultiPageView() == null) {
            return "";
        }
        if (adapter == null) {
            return "";
        }
        List<HotelBean> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("hotel");
        stksCmd.setNliScene("hotel");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.name");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            if (!TextUtils.isEmpty(list.get(i).getHotelName())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("name");
                String name = list.get(i).getHotelName().replace("^_^", "");
                stksCmdDimension.setVal(name);
                dimensions.add(stksCmdDimension);
            }
            addDefaultCmdByNumber(i, size, dimensions);
            if (!ListUtils.isEmpty(dimensions))
                stksCmdNliScene.setDimension(dimensions);
            stksCmdNliScenes.add(stksCmdNliScene);
        }
        if (list.size() == 1) {
            addConfirmCmd(stksCmdNliScenes, search);
        } else {
            stksCmdNliScenes.addAll(getDefaultCmd(size));
        }
        addSortByPriceCmd(stksCmdNliScenes, search);
        addSortByDistanceCmd(stksCmdNliScenes, search);
        addSortByStarLevelCmd(stksCmdNliScenes, search);
        addSortByScoreCmd(stksCmdNliScenes, search);
        addFilterByStarLevelCmd(stksCmdNliScenes, search);
        addFilterByMinPriceCmd(stksCmdNliScenes, search);
        addFilterByMaxPriceCmd(stksCmdNliScenes, search);
        addFilterByMaxScoreCmd(stksCmdNliScenes, search);
        addFilterByMinDistanceCmd(stksCmdNliScenes, search);
        addFilterByMaxDistanceCmd(stksCmdNliScenes, search);
        stksCmd.setNliFieldSearch(search);
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
    }

    @Override
    public void onItemClick(int position) {
        //  预订/导航
        HotelBean hotelBean = adapter.getAllList().get(position);
       /* if (reserveHotel) {
            reserveHotel(hotelBean);
        } else {
            XmMapNaviManager.getInstance().startNaviToPoi(hotelBean.getHotelName(), hotelBean.getAddress(), ConvertUtils.stringToDouble(hotelBean.getLon()), ConvertUtils.stringToDouble(hotelBean.getLat()));
        }*/
        XmMapNaviManager.getInstance().startNaviToPoi(hotelBean.getHotelName(), hotelBean.getAddress(), ConvertUtils.stringToDouble(hotelBean.getLon()), ConvertUtils.stringToDouble(hotelBean.getLat()));
        reserveHotel = false;
        closeVoicePopup();
    }

    private void reserveHotel(HotelBean hotelBean) {
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.HotelConstants.HOTEL_ID_TAG, hotelBean.getHotelId());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_NAME_TAG, hotelBean.getHotelName());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_ADDRESS_TAG, hotelBean.getAddress());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_LAT_TAG, hotelBean.getLat());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_LON_TAG, hotelBean.getLon());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_PHONE_TAG, hotelBean.getTelephone());
        bundle.putString(CenterConstants.HotelConstants.HOTEL_ICON_URL_TAG, !ListUtils.isEmpty(hotelBean.getImages()) ? hotelBean.getImages().get(0).getImageUrl() : "");
        LaunchUtils.launchAppWithData(context, CenterConstants.LAUNCHER, "com.xiaoma.launcher.travel.hotel.ui.SelectDateActivity", bundle);
    }

}
