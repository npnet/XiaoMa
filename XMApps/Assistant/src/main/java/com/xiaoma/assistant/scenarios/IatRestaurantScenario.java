package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.callback.SortChooseCallback;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.constants.EventConstants;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.LocationSlots;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.RestaurantInfo;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.RestaurantListAdapter;
import com.xiaoma.assistant.utils.SortUtils;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：餐厅/景点场景
 */
public class IatRestaurantScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private RestaurantListAdapter adapter;
    private List<RestaurantInfo> restaurantData;
    private boolean searchPoi;
    private String queryContent;
    private boolean isConditionalQuery;

    public IatRestaurantScenario(Context context) {
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
                    String poi = poiBean.getLatitude() + "," + poiBean.getLongitude();
                    queryRestaurant(poi);
                } else {
                    speakCanNotGetLongitude();
                }
            }

        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        isConditionalQuery = false;
        setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
        String slots = parseResult.getSlots();
        LocationSlots slotsBean = GsonHelper.fromJson(slots, LocationSlots.class);
        if (slotsBean == null) {
            speakUnderstand();
            return;
        }
        LatLng currentPosition = LocationManager.getInstance().getCurrentPosition();

        String poi = "";
        queryContent = "";
        if (slotsBean.getEndLoc() != null) {
            queryContent = slotsBean.getEndLoc().getOri_loc();
        }
        if (!BuildConfig.DEBUG) {
            if (currentPosition == null && slotsBean.getLandmark() != null && "CURRENT_ORI_LOC".equals(slotsBean.getLandmark().getOri_loc())) {
                speakNoLocation();
                return;
            }
        }
        if (currentPosition != null) {
            poi = currentPosition.latitude + "," + currentPosition.longitude;
        }

        if (slotsBean.getLandmark() != null && !TextUtils.isEmpty(slotsBean.getLandmark().getOri_loc()) && !"CURRENT_ORI_LOC".equals(slotsBean.getLandmark().getOri_loc())) {
            isConditionalQuery = true;
            switch (slotsBean.getLandmark().getOri_loc()) {
                case "家":
                case "公司":
                    if (parseResult.getQueryAddress() != null) {
                        poi = parseResult.getQueryAddress().getLatitude() + "," + parseResult.getQueryAddress().getLongitude();
                    }
                    break;
                default:
                    searchPoi = true;
                    XmMapNaviManager.getInstance().searchByKey(slotsBean.getLandmark().getOri_loc());
                    return;
            }
        }

        queryRestaurant(poi);
    }

    private void queryRestaurant(String poi) {
        if (EventConstants.michelinRestaurant.equals(queryContent)) {
            searchMichelinRestaurant(poi);
        } else {
            searchStoreOrFeatureSpot(poi);
        }
    }

    /**
     * 米其林餐厅
     *
     * @param poi
     */
    private void searchMichelinRestaurant(String poi) {
        RequestManager.newSingleton().searchMichelinRestaurant(poi, "", queryContent, new ResultCallback<XMResult<List<RestaurantInfo>>>() {
            @Override
            public void onSuccess(XMResult<List<RestaurantInfo>> result) {
                List<RestaurantInfo> data = result.getData();
                restaurantData = data;
                if (!ListUtils.isEmpty(data)) {
                    adapter = new RestaurantListAdapter(context, data);
                    adapter.setOnMultiPageItemClickListener(IatRestaurantScenario.this);
                    showMultiPageData(adapter, String.format(context.getString(R.string.resturant_search_result_tips), data.size()));
                } else {
                    XmTtsManager.getInstance().stopSpeaking();
                    addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                restaurantData = null;
                if (!NetworkUtils.isConnected(context)) {
                    addFeedbackAndSpeak(getString(R.string.network_errors));
                } else {
                    addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                }
            }
        });
    }

    private void searchStoreOrFeatureSpot(String poi) {
        RequestManager.newSingleton().searchStoreOrFeatureSpot(poi, "", queryContent, new ResultCallback<XMResult<List<RestaurantInfo>>>() {

            @Override
            public void onSuccess(XMResult<List<RestaurantInfo>> result) {
                List<RestaurantInfo> data = result.getData();
                restaurantData = data;
                if (!ListUtils.isEmpty(data)) {
                    adapter = new RestaurantListAdapter(context, data);
                    adapter.setOnMultiPageItemClickListener(IatRestaurantScenario.this);
                    showMultiPageData(adapter, String.format(context.getString(isConditionalQuery(queryContent) ? R.string.resturant_search_result_tips_conditional : R.string.resturant_search_result_tips), data.size()));
                } else {
                    XmTtsManager.getInstance().stopSpeaking();
                    addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                restaurantData = null;
                if (!NetworkUtils.isConnected(context)) {
                    addFeedbackAndSpeak(getString(R.string.network_errors));
                } else {
                    addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                }
            }
        });
    }

    private boolean isConditionalQuery(String queryContent) {
        return !(queryContent.equals("餐馆") || queryContent.equals("餐厅") || queryContent.equals("美食")) && isConditionalQuery;
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
            public void errorChooseActon() {
                KLog.d("choose error");
            }

            @Override
            public void assignPageAction(int page) {
                assistantManager.getMultiPageView().setPageForIndex(page);
            }

            @Override
            public void sortByPrice() {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByPrice(list, true);
                adapter.setData(list);
            }

            @Override
            public void sortByDistance() {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByDistance(list, true);
                adapter.setData(list);
            }

            @Override
            public void sortByScore() {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByScore(list, false);
                adapter.setData(list);
            }

            @Override
            public void filterByPrice(boolean isMinPrice) {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByPrice(list, true);
                List<RestaurantInfo> tempList = new ArrayList<>();
                if (isMinPrice) {
                    tempList.add(list.get(0));
                } else {
                    tempList.add(list.get(list.size() - 1));
                }
                adapter.setData(tempList);
            }

            @Override
            public void filterByHighScore() {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByScore(list, false);
                List<RestaurantInfo> tempList = new ArrayList<>();
                tempList.add(list.get(0));
                adapter.setData(tempList);
            }

            @Override
            public void filterByDistance(boolean isMinDistance) {
                if (ListUtils.isEmpty(restaurantData)) return;
                List<RestaurantInfo> list = new ArrayList<>(restaurantData);
                SortUtils.sortRestaurantByDistance(list, true);
                List<RestaurantInfo> tempList = new ArrayList<>();
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
        List<RestaurantInfo> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("restaurant");
        stksCmd.setNliScene("restaurant");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.name");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            if (!TextUtils.isEmpty(list.get(i).getName())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("name");
                String name = list.get(i).getName();
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
        addSortByScoreCmd(stksCmdNliScenes, search);
        addFilterByMinPriceCmd(stksCmdNliScenes, search);
        addFilterByMaxPriceCmd(stksCmdNliScenes, search);
        addFilterByMaxScoreCmd(stksCmdNliScenes, search);
        addFilterByMinDistanceCmd(stksCmdNliScenes, search);
        addFilterByMaxDistanceCmd(stksCmdNliScenes, search);
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
    }

    @Override
    public void onItemClick(int position) {
        RestaurantInfo info = adapter.getCurrentList().get(position);
        XmMapNaviManager.getInstance().startNaviToPoi(info.getName(), info.getAddress(), ConvertUtils.stringToDouble(info.getLng()), ConvertUtils.stringToDouble(info.getLat()));
        closeVoicePopup();
    }
}
