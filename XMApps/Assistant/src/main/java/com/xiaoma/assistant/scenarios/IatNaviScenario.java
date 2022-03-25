package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.Interceptor.interceptor.DestinationInterceptor;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleXmMapNaviManagerCallBack;
import com.xiaoma.assistant.callback.SortChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.QueryHomeAndCompanyManager;
import com.xiaoma.assistant.model.LocationSlots;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.RouteListAdapter;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.assistant.utils.SortUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：导航场景
 */
public class IatNaviScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener, QueryHomeAndCompanyManager.onGetResultListener {
    private static final int CURRENT_POI_NAVI = 0;
    private static final int ALONG_POI_NAVI = 1;
    private static final int NEARBY_POI_NAVI = 2;
    private static final int START_NAVI_WITH_VIAPOINT = 3;
    private static final int START_NAVI_WITH_VIAPOINT_SECOND_STEP = 4;
    private static final int DEFAULT_STATUS = -1;
    private String voiceContent;
    private long dialogSession;
    private ParserLocationParam locationParam;
    private int currentPoiNavi = DEFAULT_STATUS;
    private String poi;
    private List<PoiBean> poiBeanList;
    private RouteListAdapter adapter;
    private boolean searchAboutMap;
    private OperationType operationType = OperationType.None;
    private int ttsContentId = -1;
    private String nearAddress;
    private LocationSlots slotsBean;
    private boolean alongSearch;
    private LatLng destinationLatLng;
    private String viaLoc;

    public IatNaviScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {
        XmMapNaviManager.getInstance().setXmMapNaviManagerCallBack(new SimpleXmMapNaviManagerCallBack() {
            @Override
            public void onSearchResult(String searchKey, int errorCode, final List<PoiBean> searchResults) {
                Log.d("QBX", "POI SEARCH CALLBACK: ");
                if (!hasSearchedBefore()) return;
                if (currentPoiNavi == CURRENT_POI_NAVI) {
                    if (errorCode == 0) {
                        if (!ListUtils.isEmpty(searchResults)) {
                            final int size = searchResults.size();
                            if (size == 1) {
                                PoiBean poiBean = searchResults.get(0);
                                speakContent(context.getString(R.string.will_navi_to, poiBean.getName()), new WrapperSynthesizerListener() {
                                    @Override
                                    public void onCompleted() {
                                        closeVoicePopup();
                                        XmMapNaviManager.getInstance().startNaviToPoi(poiBean.getName(),
                                                poiBean.getAddress(), poiBean.getLongitude(), poiBean.getLatitude());
                                    }
                                });
                            } else if (size > 1) {
                                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter(searchResults);
                                        showMultiPageData(adapter, context.getString(R.string.more_navi_result, size));
                                    }
                                });
                            }
                        } else {
                            canNotFoundAddress();
                        }
                    } else {
                        canNotFoundAddress();
                    }
                    currentPoiNavi = DEFAULT_STATUS;
                } else if (currentPoiNavi == START_NAVI_WITH_VIAPOINT) {
                    Log.d("QBX", "onSearchResult: START_NAVI_WITH_VIAPOINT");
                    if (errorCode == 0) {
                        if (!ListUtils.isEmpty(searchResults)) {
                            int size = searchResults.size();
                            if (size == 1) {
                                PoiBean poiBean = searchResults.get(0);
                                destinationLatLng = new LatLng(poiBean.getLatitude(), poiBean.getLongitude());
                                currentPoiNavi = START_NAVI_WITH_VIAPOINT_SECOND_STEP;
                                searchPoi(viaLoc);
                            } else {
                                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter(searchResults);
                                        showMultiPageData(adapter, context.getString(R.string.more_navi_result, size));
                                    }
                                });
                                return;
                            }
                        } else {
//                            AssistantManager.getInstance().showLoadingView(false);
                            canNotFoundAddress();
                            currentPoiNavi = DEFAULT_STATUS;
                        }
                    } else {
//                        AssistantManager.getInstance().showLoadingView(false);
                        canNotFoundAddress();
                        currentPoiNavi = DEFAULT_STATUS;
                    }
                    viaLoc = null;
                } else if (currentPoiNavi == START_NAVI_WITH_VIAPOINT_SECOND_STEP) {
                    Log.d("QBX", "onSearchResult: START_NAVI_WITH_VIAPOINT_SECOND_STEP");
//                    AssistantManager.getInstance().showLoadingView(false);
                    if (errorCode == 0) {
                        if (!ListUtils.isEmpty(searchResults)) {
                            int size = searchResults.size();
                            if (size == 1) {
                                PoiBean poiBean = searchResults.get(0);
                                XmMapNaviManager.getInstance().startNaviWithViaPoint(destinationLatLng.getLongitude(), destinationLatLng.getLatitude(),
                                        poiBean.getLongitude(), poiBean.getLatitude());
                                closeVoicePopup();
                            } else {
                                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter(searchResults);
                                        showMultiPageData(adapter, context.getString(R.string.more_navi_result, size));
                                    }
                                });
                                return;
                            }
                        } else {
                            canNotFoundAddress();
                        }
                    } else {
                        canNotFoundAddress();
                    }
                    currentPoiNavi = DEFAULT_STATUS;
                } else if (currentPoiNavi == ALONG_POI_NAVI) {
                    if (errorCode == 0) {
                        if (!ListUtils.isEmpty(searchResults)) {
                            PoiBean poiBean = searchResults.get(0);
                            XmMapNaviManager.getInstance().addViaPoint(poiBean.getName(), poiBean.getAddress(),
                                    poiBean.getLongitude(), poiBean.getLatitude());
                            closeVoicePopup();
                        } else {
                            canNotFoundAddress();
                        }
                    } else {
                        canNotFoundAddress();
                    }
                    currentPoiNavi = DEFAULT_STATUS;
                } else if (currentPoiNavi == NEARBY_POI_NAVI) {
                    if (!ListUtils.isEmpty(searchResults)) {
                        PoiBean poiBean = searchResults.get(0);
                        if (searchNearByKey(poi, poiBean.getLongitude(), poiBean.getLatitude()) == -1) {
                            addFeedbackAndSpeak(getString(R.string.map_not_init));
                        }
                    }
                    currentPoiNavi = DEFAULT_STATUS;
                } else {
                    canNotFoundAddress();
                }
            }

            @Override
            public void onSearchNearResult(String searchKey, double lon, double lat, int errorCode, final List<PoiBean> searchResults) {
                Log.d("QBX", "NEAR SEARCH CALLBACK: ");
                if (!hasSearchedBefore()) return;
                if (!ListUtils.isEmpty(searchResults)) {
                    final int size = searchResults.size();
                    if (size == 1) {
                        PoiBean bean = searchResults.get(0);
                        speakContent(context.getString(R.string.will_navi_to, bean.getName()), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                closeVoicePopup();
                                XmMapNaviManager.getInstance().startNaviToPoi(bean.getName(), bean.getAddress(), bean.getLongitude(), bean.getLatitude());
                            }
                        });
                    } else {
                        poiBeanList = searchResults;
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter(searchResults);
                                String text;
                                if (ttsContentId != -1) {
                                    if (!TextUtils.isEmpty(nearAddress)) {
                                        text = context.getString(ttsContentId, nearAddress, size);
                                        nearAddress = null;
                                    } else {
                                        text = context.getString(ttsContentId, size);
                                    }
                                    ttsContentId = -1;
                                } else {
                                    text = context.getString(R.string.more_navi_result_near, size);
                                }
                                showMultiPageData(adapter, text);
                            }
                        });
                    }
                } else {
                    canNotFoundAddress();
                }
            }

            @Override
            public void onSearchByRouteResult(String searchKey, int errorCode, final List<PoiBean> searchResults) {
                Log.d("QBX", "SEARCH BY ROUTE CALLBACK: ");
                if (!hasSearchedBefore()) return;
                if (!ListUtils.isEmpty(searchResults)) {
                    final int size = searchResults.size();
                    if (size == 1) {
                        PoiBean bean = searchResults.get(0);
                        XmMapNaviManager.getInstance().addViaPoint(bean.getName(), bean.getAddress(), bean.getLongitude(), bean.getLatitude());
                        closeVoicePopup();
                    } else {
                        poiBeanList = searchResults;
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter(searchResults);
                                showMultiPageData(adapter, context.getString(R.string.more_navi_result_near, size));
                            }
                        });
                    }
                } else {
                    canNotFoundAddress();
                }
            }
        });
    }

    private void setAdapter(List<PoiBean> list) {
        if (adapter == null) {
            adapter = new RouteListAdapter(context, list);
            adapter.setOnMultiPageItemClickListener(IatNaviScenario.this);
        } else {
            adapter.setData(list);
        }
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        this.voiceContent = voiceJson;
        this.locationParam = location;
        this.dialogSession = session;
        currentPoiNavi = DEFAULT_STATUS;
        alongSearch = false;
        searchAboutMap = false;


        //返回导航
        if ("返回导航".equals(parseResult.getText())) {
            XmTtsManager.getInstance().startSpeakingByAssistant("好的", new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    AssistantManager.getInstance().closeAssistant();
                    XmMapNaviManager.getInstance().switchToLauncher();
                }
            });
            return;
        }

        String slots = parseResult.getSlots();
        if (TextUtils.isEmpty(slots) || slots.equals("{}")) {
            setRobAction(15);
            InterceptorManager.getInstance().setCurrentInterceptor(new DestinationInterceptor(), context.getString(R.string.where_do_you_want_to_navigate));
            return;
        }
        LocationSlots slotsBean = GsonHelper.fromJson(slots, LocationSlots.class);
        if (slotsBean == null) {
            speakUnderstand();
            return;
        }
        String address = null;
        if ("USR_POI_QUERY".equals(parseResult.getOperation())) {
            if (slotsBean.getLandmark() != null && !TextUtils.isEmpty(slotsBean.getLandmark().getOri_loc())) {
                address = slotsBean.getLandmark().getOri_loc();
            } else if ("QUERY_TRAFFIC_INFO".equals(slotsBean.getInsType()) && slotsBean.getEndLoc() != null && !TextUtils.isEmpty(slotsBean.getEndLoc().getOri_loc())) {
                address = slotsBean.getEndLoc().getOri_loc();
            }
        }
        if (address != null) {
            switch (address) {
                case "家":
                    this.slotsBean = slotsBean;
                    operationType = OperationType.ContinueParsing;
                    speakContent(context.getString(R.string.please_check_road_condition), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            AssistantManager.getInstance().closeAssistant();
                            QueryHomeAndCompanyManager.getInstance().queryHome(IatNaviScenario.this);
                        }
                    });

                    return;
                case "公司":
                    this.slotsBean = slotsBean;
                    operationType = OperationType.ContinueParsing;
                    speakContent(context.getString(R.string.please_check_road_condition), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            AssistantManager.getInstance().closeAssistant();
                            QueryHomeAndCompanyManager.getInstance().queryCompany(IatNaviScenario.this);
                        }
                    });
                    return;
                default:
                    break;
            }
        }

        continueParsing(slotsBean);
    }

    private void continueParsing(LocationSlots slotsBean) {
        if (TextUtils.equals(parseResult.getOperation(), "NEXT")) {
            searchFrontage();
            return;
        } else if (TextUtils.equals(parseResult.getOperation(), "NAVI_INFO") && StringUtil.isNotEmpty(slotsBean.getNaviInfo())) {
            switch (slotsBean.getNaviInfo()) {
                case "VIA_INFO":
                    //查看全局路线
                    openOverView();
                    break;
                case "CANCEL_INFO":
                    //退出全局路线
                    closeOverView();
                    break;
                case "TIME_REMAIN":
                case "DISTANCE_REMAIN":
                    if (XmMapNaviManager.getInstance().isNaviing()) {
                        int remainingTime = XmMapNaviManager.getInstance().getRemainingTime();
                        int remainingDistance = XmMapNaviManager.getInstance().getRemainingDistance();
                        int minutes = remainingTime / 60;
                        speakAndClose(context.getString(R.string.text_last_time, remainingDistance / 1000, CommonUtils.getFormattedTime(context, minutes)));
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        return;
                    } else {
                        speakAndClose(context.getString(R.string.not_navi_state));
                        return;
                    }
                case "FORWARD":
                    searchFrontage();
                    break;
            }
        }
        if (TextUtils.equals(parseResult.getOperation(), "POS_RANK") && slotsBean.getPosRank() != null
                && StringUtil.isNotEmpty(slotsBean.getPosRank().getOffset())) {
            String offset = slotsBean.getPosRank().getOffset();
            chooseRoute(Integer.parseInt(offset) - 1);
        }
        if (TextUtils.equals(parseResult.getOperation(), "ROUTE_PLAN") && StringUtil.isNotEmpty(slotsBean.getRouteCondition())) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
            if (XmMapNaviManager.getInstance().isNaviing()) {
                speakContent(context.getString(R.string.route_plan_success), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        closeVoicePopup();
                        switch (slotsBean.getRouteCondition()) {
                            case "AVOID_ROUND":
                                XmMapNaviManager.getInstance().setRouteAvoidType(AssistantConstants.RoutAvoidType.AVOID_ROUND);
                                break;
                            case "FASTEST":
                            case "SHORTEST":
                                XmMapNaviManager.getInstance().setRouteAvoidType(AssistantConstants.RoutAvoidType.FASTEST);
                                break;
                            case "CHEAPEST":
                                XmMapNaviManager.getInstance().setRouteAvoidType(AssistantConstants.RoutAvoidType.CHEAPEST);
                                break;
                            case "NOT_HIGH_FIRST":
                                XmMapNaviManager.getInstance().setRouteAvoidType(AssistantConstants.RoutAvoidType.NOT_HIGH_FIRST);
                                break;
                            case "HIGH_FIRST":
                                XmMapNaviManager.getInstance().setRouteAvoidType(AssistantConstants.RoutAvoidType.HIGH_FIRST);
                                break;
                        }
                    }
                });
            } else {
                speakAndClose(context.getString(R.string.not_navi_state));
            }
        }
        if (StringUtil.isNotEmpty(slotsBean.getInsType()) && !"ALONG_SEARCH".equals(slotsBean.getInsType())) {
            handleInsType(slotsBean);

        } else if (slotsBean.getEndLoc() != null) {
            if (TextUtils.isEmpty(slotsBean.getEndLoc().getTopic()) || TextUtils.isEmpty(slotsBean.getEndLoc().getOri_loc())) {
                speakUnderstand();
                return;
            }
            String poi = slotsBean.getEndLoc().getOri_loc();
            if (!matchCollections(poi)) {
                handleTopic(slotsBean);
            }

        } else if (slotsBean.getViaLoc() != null) {
            //途经某地
            if (TextUtils.isEmpty(slotsBean.getViaLoc().getOri_loc())) {
                speakUnderstand();
                return;
            }
            String point = slotsBean.getViaLoc().getOri_loc();
            currentPoiNavi = ALONG_POI_NAVI;
            searchPoi(point);
        }
    }

    private void handleTopic(LocationSlots slotsBean) {
        IatScenario iatScenario;
        switch (slotsBean.getEndLoc().getTopic()) {
            case "hotel":
                iatScenario = ScenarioDispatcher.getInstance().getIatHotelScenario(context);
                AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatScenario);
                iatScenario.onParser(voiceContent, parseResult, locationParam, dialogSession);
                break;
            case "restaurant":
                iatScenario = ScenarioDispatcher.getInstance().getIatRestaurantScenario(context);
                AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatScenario);
                iatScenario.onParser(voiceContent, parseResult, locationParam, dialogSession);
                break;
/*                case "parking":
            iatScenario = ScenarioDispatcher.getInstance().getIatParkScenario(context);
            AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatScenario);
            iatScenario.onParser(voiceContent, parseResult, locationParam, dialogSession);
            break;*/
            case "cinema":
                iatScenario = ScenarioDispatcher.getInstance().getIatCinemaScenario(context);
                AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatScenario);
                iatScenario.onParser(voiceContent, parseResult, locationParam, dialogSession);
                break;
            case "others":
            case "scenic_spot":
            case "parking":
            default:
                if ("景点".equals(slotsBean.getEndLoc().getOri_loc())) {
                    iatScenario = ScenarioDispatcher.getInstance().getIatRestaurantScenario(context);
                    AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatScenario);
                    iatScenario.onParser(voiceContent, parseResult, locationParam, dialogSession);
                } else { //导航
                    if (locationParam == null && !BuildConfig.DEBUG) {
                        speakNoLocation();
                        return;
                    }
                    navi(slotsBean);
                }
                break;
        }
    }

    private void handleInsType(LocationSlots slotsBean) {
        switch (slotsBean.getInsType()) {
            case "OPEN":
                speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        XmMapNaviManager.getInstance().switchToLauncher();
                        closeVoicePopup();
                    }
                });
                break;
            case "CLOSE":
                ScenarioDispatcher.getInstance().getIatInstructionScenario(context).closeAssistant();
                break;
            case "VIEW_TRANS_2D_HEAD_UP":
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                closeVoicePopup();
                break;
            case "VIEW_TRANS_2D_NORTH_UP":
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                closeVoicePopup();
                break;
            case "VIEW_TRANS_3D_HEAD_UP":
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_3D_HEAD_UP);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                closeVoicePopup();
                break;
            case "VIEW_TRANS_HEAD_UP":
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                closeVoicePopup();
                break;
            case "VIEW_TRANS":
                switchShowState();
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                closeVoicePopup();
                break;
            case "CLOSE_MAP":
            case "CANCEL_MAP":
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_STOP_NAVI);
                XmMapNaviManager.getInstance().cancelNavi();
                closeAfterSpeak(context.getString(R.string.navigation_is_over));
                break;
            case "COLLECT":
                LocationInfo currentLocation = LocationManager.getInstance().getCurrentLocation();
                if (currentLocation == null) {
                    if (BuildConfig.DEBUG) {
                        currentLocation = LocationManager.getInstance().getDebugLocationInfo();
                    } else {
                        speakNoLocation();
                        return;
                    }
                }
                PoiBean poiBean = new PoiBean();
                poiBean.setAddress(currentLocation.getAddress());
                poiBean.setLatitude(currentLocation.getLatitude());
                poiBean.setLongitude(currentLocation.getLongitude());
                XmMapNaviManager.getInstance().addCollection(poiBean);
                speakAndClose(context.getString(R.string.collect_poi_success, currentLocation.getAddress()));
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_COLLECT_POI);
                break;
            case "CONFIRM":
                if (!XmMapNaviManager.getInstance().isPathPlanSuccess()) {
                    speakAndClose(context.getString(R.string.not_route_planning_state));
                    return;
                }
                if (!XmMapNaviManager.getInstance().isNaviForeground()) {
                    speakAndClose(context.getString(R.string.not_map_page));
                    return;
                }
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SELECT_LINE);
                XmMapNaviManager.getInstance().startNavi();
                closeVoicePopup();
                break;
            case "ZOOM_IN":
                if (XmMapNaviManager.getInstance().isNaviForeground()) {
                    XmMapNaviManager.getInstance().setMapZoomIn();
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    closeVoicePopup();
                } else {
                    speakAndClose(context.getString(R.string.not_map_page));
                }
                break;
            case "ZOOM_OUT":
                if (XmMapNaviManager.getInstance().isNaviForeground()) {
                    XmMapNaviManager.getInstance().setMapZoomOut();
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    closeVoicePopup();
                } else {
                    speakAndClose(context.getString(R.string.not_map_page));
                }
                break;
            case "OPEN_TRAFFIC_INFO":
                XmMapNaviManager.getInstance().showTmc(true);
                speakAndClose(context.getString(R.string.open_traffic_info));
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                break;
            case "CLOSE_TRAFFIC_INFO":
                XmMapNaviManager.getInstance().showTmc(false);
                speakAndClose(context.getString(R.string.close_traffic_info));
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                break;
            case "QUERY_TRAFFIC_INFO":
                if (slotsBean.getEndLoc() != null && !TextUtils.isEmpty(slotsBean.getEndLoc().getOri_loc())) {
                    switch (slotsBean.getEndLoc().getOri_loc()) {
                        case "家":      // 查询家附近路况
                        case "公司":    // 查询公司附近路况
                            if (parseResult.getQueryAddress() != null) {
                                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
                                XmMapNaviManager.getInstance().showTmcForPoi(parseResult.getQueryAddress().getLongitude(), parseResult.getQueryAddress().getLatitude());
                                closeVoicePopup();
                                speakContent(context.getString(R.string.please_check_road_condition));
                            }
                            break;
                    }
                }
                break;
        }
    }

    public void chooseRoute(int offset) {
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SELECT_LINE);
        if (!XmMapNaviManager.getInstance().isPathPlanSuccess()) {
            speakAndClose(context.getString(R.string.not_route_planning_state));
            return;
        }
        if (!XmMapNaviManager.getInstance().isNaviForeground()) {
            speakAndClose(context.getString(R.string.not_map_page));
            return;
        }
        speakContent(context.getString(R.string.result_ok),new WrapperSynthesizerListener(){
            @Override
            public void onCompleted() {
                closeVoicePopup();
                XmMapNaviManager.getInstance().chooseRoutePlan(offset);
                XmMapNaviManager.getInstance().startNavi();
            }
        });
    }

    private void searchFrontage() {
        if (XmMapNaviManager.getInstance().isNaviing()) {
            XmMapNaviManager.getInstance().naviBroadcast();
            closeVoicePopup();
        } else {
            speakAndClose(context.getString(R.string.not_navi_state));
        }
    }

    private void closeOverView() {
        if (!XmMapNaviManager.getInstance().isNaviForeground()) {
            speakAndClose(context.getString(R.string.not_map_page));
            return;
        }

        if (!XmMapNaviManager.getInstance().isNaviing()) {
            speakAndClose(context.getString(R.string.not_navi_state));
            return;
        }
        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
        if (!isOverView) {
            closeVoicePopup();
            return;
        }
        XmMapNaviManager.getInstance().switchRouteOverview();
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CANCEL_INFO);
        closeVoicePopup();
    }

    private void openOverView() {
        if (!XmMapNaviManager.getInstance().isNaviForeground()) {
            speakAndClose(context.getString(R.string.not_map_page));
            return;
        }

        if (!XmMapNaviManager.getInstance().isNaviing()) {
            speakAndClose(context.getString(R.string.not_navi_state));
            return;
        }
        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
        if (isOverView) {
            closeVoicePopup();
            return;
        }
        XmMapNaviManager.getInstance().switchRouteOverview();
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
        closeVoicePopup();
    }

    private void switchShowState() {
        int showState = XmMapNaviManager.getInstance().getNaviShowState();
        switch (showState) {
            case AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP:
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP);
                break;
            case AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP:
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_3D_HEAD_UP);
                break;
            case AssistantConstants.NaviShowState.VIEW_TRANS_3D_HEAD_UP:
            case AssistantConstants.NaviShowState.VIEW_TRANS_AR:
                XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                break;
            default:
                break;
        }
    }

    private void navi(LocationSlots slotsBean) {
        //        assistantManager.closeAssistant();
        String poi = slotsBean.getEndLoc().getOri_loc();
        if ("USR_POI_QUERY".equals(parseResult.getOperation())) {
            switch (slotsBean.getEndLoc().getOri_loc()) {
                case "家":
                    QueryHomeAndCompanyManager.getInstance().queryHome(new QueryHomeAndCompanyManager.SimpleGetResultListener() {
                        @Override
                        public void onGetHomeResult(double longitude, double latitude, String address) {
                            if (isResultInvalid(longitude, CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME)) {
                                return;
                            }
                            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                            speakContent(context.getString(R.string.will_navi_to_home), new WrapperSynthesizerListener() {
                                @Override
                                public void onCompleted() {
                                    closeVoicePopup();
                                    XmMapNaviManager.getInstance().startNaviToHome();
                                }
                            });
                        }
                    });
                    break;
                case "公司":
                    QueryHomeAndCompanyManager.getInstance().queryCompany(new QueryHomeAndCompanyManager.SimpleGetResultListener() {
                        @Override
                        public void onGetHomeResult(double longitude, double latitude, String address) {
                            if (isResultInvalid(longitude, CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY)) {
                                return;
                            }
                            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                            speakContent(context.getString(R.string.will_navi_to_company), new WrapperSynthesizerListener() {
                                @Override
                                public void onCompleted() {
                                    closeVoicePopup();
                                    XmMapNaviManager.getInstance().startNaviToCompany();
                                }
                            });
                        }
                    });
                    break;
                default:
                    // 查找家或者公司附近的xx（poi类别）
                    if (slotsBean.getLandmark() != null && !TextUtils.isEmpty(slotsBean.getLandmark().getOri_loc())) {
                        switch (slotsBean.getLandmark().getOri_loc()) {
                            case "家":
                            case "公司":
                                if (parseResult.getQueryAddress() != null) {
                                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SEARCH);
                                    if (searchNearByKey(slotsBean.getEndLoc().getOri_loc(), parseResult.getQueryAddress().getLongitude(), parseResult.getQueryAddress().getLatitude()) == -1) {
                                        addFeedbackAndSpeak(getString(R.string.map_not_init));
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        } else if (StringUtil.isNotEmpty(slotsBean.getEndLoc().getXm_cat())) {
            String routePoint = slotsBean.getEndLoc().getPoi();
            currentPoiNavi = ALONG_POI_NAVI;
            searchPoi(routePoint);
        } else if (StringUtil.isNotEmpty(slotsBean.getInsType()) && "ALONG_SEARCH".equals(slotsBean.getInsType())) {
            if (XmMapNaviManager.getInstance().isNaviing()) {
                alongSearch = true;
                searchByRoute(poi);
            } else {
                speakAndClose(context.getString(R.string.not_navi_state));
            }
        } else if (slotsBean.getLandmark() != null) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SEARCH);
            LocationSlots.Landmark landmark = slotsBean.getLandmark();
            String ori_loc = landmark.getOri_loc();
            if (!TextUtils.isEmpty(ori_loc)) {
                if (ori_loc.equals("CURRENT_ORI_LOC")) {
                    KLog.d("MrMine", "navi: " + "附近的poi搜索");
                    LocationInfo currentLocation = LocationManager.getInstance().getCurrentLocation();
                    if (currentLocation == null) {
                        if (BuildConfig.DEBUG) {
                            currentLocation = LocationManager.getInstance().getDebugLocationInfo();
                        } else {
                            speakNoLocation();
                            return;
                        }
                    }
                    if (searchNearByKey(poi, currentLocation.getLongitude(), currentLocation.getLatitude()) == -1) {
                        addFeedbackAndSpeak(getString(R.string.map_not_init));
                    }
                } else {
                    KLog.d("MrMine", "navi: " + "XXX附近的poi搜索");
                    this.poi = poi;
                    currentPoiNavi = NEARBY_POI_NAVI;
                    searchPoi(ori_loc);
                }
            }
        } else {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_LOCATION);
            if (!matchCollections(poi)) {
                if (slotsBean.getViaLoc() != null && !TextUtils.isEmpty(slotsBean.getViaLoc().getOri_loc())) {
                    viaLoc = slotsBean.getViaLoc().getOri_loc();
                    currentPoiNavi = START_NAVI_WITH_VIAPOINT;
//                    AssistantManager.getInstance().showLoadingView(true);
                } else {
                    currentPoiNavi = CURRENT_POI_NAVI;
                }
                searchPoi(poi);
            }
        }
    }

    private boolean matchCollections(String poi) {
        final List<PoiBean> matchPoi = new ArrayList<>();
        matchCollectionPoiBean(poi, matchPoi);
        if (!ListUtils.isEmpty(matchPoi)) {
            final int size = matchPoi.size();
            if (size == 1) {
                PoiBean poiBean = matchPoi.get(0);
                speakContent(context.getString(R.string.will_navi_to, poiBean.getName()), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        closeVoicePopup();
                        XmMapNaviManager.getInstance().startNaviToPoi(poiBean.getName(),
                                poiBean.getAddress(), poiBean.getLongitude(), poiBean.getLatitude());
                    }
                });
            } else if (size > 1) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(matchPoi);
                        showMultiPageData(adapter, context.getString(R.string.more_navi_result, size));
                    }
                });
            }
            return true;
        } else {
            return false;
        }
    }

    public void navi(String destination) {
        currentPoiNavi = CURRENT_POI_NAVI;
        searchPoi(destination);
    }

    private boolean hasSearchedBefore() {
        if (searchAboutMap) {
            searchAboutMap = false;
            return true;
        } else {
            return false;
        }
    }

    private void searchPoi(String searchKey) {
        searchAboutMap = true;
        XmMapNaviManager.getInstance().searchByKey(searchKey);
    }

    public int searchNearByKey(String searchKey, double lon, double lat) {
        searchAboutMap = true;
        return XmMapNaviManager.getInstance().searchNearByKey(searchKey, lon, lat);

    }

    private int searchByRoute(String searchKey) {
        searchAboutMap = true;
        return XmMapNaviManager.getInstance().searchByRoute(searchKey);
    }

    private void matchCollectionPoiBean(String poi, List<PoiBean> matchPoi) {
        List<PoiBean> collections = XmMapNaviManager.getInstance().getCollections();
        if (!ListUtils.isEmpty(collections)) {
            for (PoiBean poiBean : collections) {
                if (poiBean == null) {
                    continue;
                }
                String name = poiBean.getName();
                String address = poiBean.getAddress();
                if (name.contains(poi) || address.contains(poi)) {
                    matchPoi.add(poiBean);
                    break;
                }
            }
        }
    }

    private void speakAndClose(String text) {
        addFeedbackAndSpeak(text, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                assistantManager.closeAssistant();
            }
        });
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
        List<PoiBean> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("navi");
        stksCmd.setNliScene("navi");
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
        addSortByDistanceCmd(stksCmdNliScenes, search);
        addFilterByMinDistanceCmd(stksCmdNliScenes, search);
        addFilterByMaxDistanceCmd(stksCmdNliScenes, search);
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
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
            public void sortByDistance() {
                if (ListUtils.isEmpty(poiBeanList)) return;
                List<PoiBean> list = new ArrayList<>(poiBeanList);
                SortUtils.sortPoiResultByDistance(list, true);
                adapter.setData(list);
            }

            @Override
            public void filterByDistance(boolean isMinDistance) {
                if (ListUtils.isEmpty(poiBeanList)) return;
                List<PoiBean> list = new ArrayList<>(poiBeanList);
                SortUtils.sortPoiResultByDistance(list, true);
                List<PoiBean> tempList = new ArrayList<>();
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
    public void onItemClick(int position) {
        PoiBean bean = adapter.getAllList().get(position);
        if (currentPoiNavi == START_NAVI_WITH_VIAPOINT) {
            destinationLatLng = new LatLng(bean.getLatitude(), bean.getLongitude());
            currentPoiNavi = START_NAVI_WITH_VIAPOINT_SECOND_STEP;
            searchPoi(viaLoc);
            return;
        } else if (currentPoiNavi == START_NAVI_WITH_VIAPOINT_SECOND_STEP) {
            speakContent(String.format(getString(R.string.will_navi_to_position), position + 1), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                    XmMapNaviManager.getInstance().startNaviWithViaPoint(destinationLatLng.getLongitude(), destinationLatLng.getLatitude(), bean.getLongitude(), bean.getLatitude());
                }
            });
            return;
        }
        if (alongSearch) {
            XmMapNaviManager.getInstance().addViaPoint(bean.getName(), bean.getAddress(), bean.getLongitude(), bean.getLatitude());
            closeVoicePopup();
        } else {
            speakContent(String.format(getString(R.string.will_navi_to_position), position + 1), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                    XmMapNaviManager.getInstance().startNaviToPoi(bean.getName(), bean.getAddress(), bean.getLongitude(), bean.getLatitude());
                }
            });
        }
    }

    enum OperationType {
        ContinueParsing,
        None
    }

    @Override
    public void onGetHomeResult(double longitude, double latitude, String address) {
        if (operationType == OperationType.None) {
            return;
        }
        if (isResultInvalid(longitude, CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME)) {
            operationType = OperationType.None;
            return;
        }
        ttsContentId = R.string.found_several_result_near_somewhere;
        nearAddress = context.getString(R.string.home);
        performOperations(longitude, latitude, address);
    }

    @Override
    public void onGetCompanyResult(double longitude, double latitude, String address) {
        if (operationType == OperationType.None) {
            return;
        }
        if (isResultInvalid(longitude, CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY)) {
            operationType = OperationType.None;
            return;
        }
        ttsContentId = R.string.found_several_result_near_somewhere;
        nearAddress = context.getString(R.string.company);
        performOperations(longitude, latitude, address);
    }

    private boolean isResultInvalid(double longitude, String queryType) {
        if (longitude == -1) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SET_HOME);
            int errorStringId;
            switch (queryType) {
                case CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME:
                    errorStringId = R.string.please_setup_home_address;
                    break;
                case CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY:
                    errorStringId = R.string.please_setup_company_address;
                    break;
                default:
                    return true;
            }
            speakContent(context.getString(errorStringId), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                    switch (queryType) {
                        case CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME:
                            XmMapNaviManager.getInstance().showHomeSettingPage();
                            break;
                        case CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY:
                            XmMapNaviManager.getInstance().showCompanySettingPage();
                            break;
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void performOperations(double longitude, double latitude, String address) {
        if (operationType == OperationType.ContinueParsing) {
            operationType = OperationType.None;
            parseResult.setQueryAddress(new ParseResult.QueryAddress(longitude, latitude, address));
            continueParsing(slotsBean);
        }
    }

}
