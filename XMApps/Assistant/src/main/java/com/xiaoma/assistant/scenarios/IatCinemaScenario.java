package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.CinemaBean;
import com.xiaoma.assistant.model.LocationSlots;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.HotelBean;
import com.xiaoma.assistant.model.result.CinemaResult;
import com.xiaoma.assistant.model.result.SpecialResult;
import com.xiaoma.assistant.model.result.SpecialResultCallback;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.CinemaListAdapter;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/21 0021
 * 电影院场景
 */
public class IatCinemaScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private CinemaListAdapter adapter;
    private int pageNum = 1;
    private String city;
    private boolean searchPoi;

    public IatCinemaScenario(Context context) {
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
                    queryCinema(String.valueOf(poiBean.getLatitude()), String.valueOf(poiBean.getLongitude()));
                } else {
                    speakCanNotGetLongitude();
                }
            }
        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
        String slots = parseResult.getSlots();
        LocationSlots slotsBean = GsonHelper.fromJson(slots, LocationSlots.class);
        if (slotsBean == null) {
            speakUnderstand();
            return;
        }
        LatLng currentPosition = LocationManager.getInstance().getCurrentPosition();
        String lat = "";
        String lon = "";
        city = "";
        if (!BuildConfig.DEBUG) {
            if (currentPosition == null && slotsBean.getLandmark() != null && "CURRENT_ORI_LOC".equals(slotsBean.getLandmark().getOri_loc())) {
                speakNoLocation();
                return;
            }
        }
        if (currentPosition!=null) {
            lat = String.valueOf(currentPosition.latitude);
            lon = String.valueOf(currentPosition.longitude);
        }
        if (location!=null) {
            city = location.city;
        }

        if (slotsBean.getLandmark() != null && !TextUtils.isEmpty(slotsBean.getLandmark().getOri_loc()) && !"CURRENT_ORI_LOC".equals(slotsBean.getLandmark().getOri_loc())) {
            switch (slotsBean.getLandmark().getOri_loc()) {
                case "家":
                case "公司":
                    if (parseResult.getQueryAddress() != null) {
                        lat = String.valueOf(parseResult.getQueryAddress().getLatitude());
                        lon = String.valueOf(parseResult.getQueryAddress().getLongitude());
                    }
                    break;
                default:
                    searchPoi = true;
                    XmMapNaviManager.getInstance().searchByKey(slotsBean.getLandmark().getOri_loc());
                    return;
            }
        }

        queryCinema(lat, lon);
    }

    private void queryCinema(String lat, String lon) {
        String countyName = "";//搜索地址
        String cinemaName = "";//影院名
        String filmName = "";//	电影名
        String dataStyle = "";//电影类型 0全部 1热门 2即将上映

        RequestManager.newSingleton().queryFilm(city, countyName, cinemaName,
                pageNum, BaseMultiPageAdapter.PAGE_SIZE, filmName, dataStyle, lat, lon, new SpecialResultCallback<CinemaResult>() {

                    @Override
                    public void onSuccess(CinemaResult result) {
                        List<CinemaBean> data = result.getData();
                        if (!ListUtils.isEmpty(data)) {
                            adapter = new CinemaListAdapter(context, data);
                            adapter.setOnMultiPageItemClickListener(IatCinemaScenario.this);
                            showMultiPageData(adapter, context.getString(R.string.search_result_tips));
                        } else {
                            XmTtsManager.getInstance().stopSpeaking();
                            addFeedbackAndSpeak(context.getString(R.string.not_search_data));
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        if (!NetworkUtils.isConnected(context)) {
                            addFeedbackAndSpeak(getString(R.string.network_errors));
                        } else {
                            addFeedbackAndSpeak(getString(R.string.error_data_response));
                        }
                    }
                });
    }

    @Override
    public void onChoose(String voiceText) {
        final boolean isFirstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean isLastPage = assistantManager.getMultiPageView().isLastPage();
        switchChooseAction(voiceText, new IChooseCallback() {
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
        List<CinemaBean> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("cinema");
        stksCmd.setNliScene("cinema");
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
            if (!TextUtils.isEmpty(list.get(i).getCinemaName())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("name");
                String name = list.get(i).getCinemaName();
                stksCmdDimension.setVal(name);
                dimensions.add(stksCmdDimension);
            }
            addDefaultCmdByNumber(i, size, dimensions);
            if (!ListUtils.isEmpty(dimensions))
                stksCmdNliScene.setDimension(dimensions);
            stksCmdNliScenes.add(stksCmdNliScene);
        }
        stksCmdNliScenes.addAll(getDefaultCmd(size));
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
    }

    @Override
    public void onItemClick(int position) {
        CinemaBean bean = adapter.getCurrentList().get(position);
        XmMapNaviManager.getInstance().startNaviToPoi(bean.getCinemaName(), bean.getAddress(), ConvertUtils.stringToDouble(bean.getLongitude()), ConvertUtils.stringToDouble(bean.getLatitude()));
        closeVoicePopup();
    }
}
