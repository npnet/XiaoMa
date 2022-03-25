package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.TrainBean;
import com.xiaoma.assistant.model.TrainDetailResponse;
import com.xiaoma.assistant.model.TrainResult;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.Train;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.TrainListAdapter;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.view.TrainDetailView;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：火车场景
 */
public class IatTrainScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    TrainListAdapter adapter;
    private int currentPage;
    private int totalPage;

    public IatTrainScenario(Context context) {
        super(context);
    }


    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        if (checkTrainCity(parseResult.getInputResult())) {
            parserTrain(parseResult.getInputResult());
        } else {
            String jsonString = parseResult.getInputResult();
            if (!TextUtils.isEmpty(jsonString)) {
                Train train = GsonHelper.fromJson(jsonString, Train.class);
                if (TextUtils.isEmpty(parsTrainEnd(train))) {
//                    speakContent(context.getString(R.string.not_found_train_ticket));
                    showSearchError(getString(R.string.no_train));
                    return;
                }
            }
            showSearchError(context.getString(R.string.query_train_error));
        }
    }

    private boolean checkTrainCity(String jsonString) {
        boolean checkCity = false;
        if (!TextUtils.isEmpty(jsonString)) {
            Train train = GsonHelper.fromJson(jsonString, Train.class);
            if (train != null && !TextUtils.isEmpty(parsTrainStart(train))
                    && !TextUtils.isEmpty(parsTrainEnd(train))) {
                checkCity = true;
            }
            if (!checkCity && train != null && train.semantic != null && train.semantic.slots != null && !TextUtils.isEmpty(train.semantic.slots.code)) {
                checkCity = true;
            } else {
                if (BuildConfig.DEBUG) {
                    //在debug包下，默认通过校验，用于模拟定位信息
                    checkCity = true;
                }
            }
        }
        return checkCity;
    }

    private String parsTrainStart(Train train) {
        String startAreaAddress = "";
        String startCityAddress = "";
        if (train.semantic != null) {
            if (train.semantic.slots != null) {
                if (train.semantic.slots.startLoc != null) {
                    startAreaAddress = train.semantic.slots.startLoc.areaAddr;
                    startCityAddress = train.semantic.slots.startLoc.cityAddr;
                }
            }
        }
        String startCity = "";
        if (!TextUtils.isEmpty(startAreaAddress)) {
            startCity = startAreaAddress;
        }
        if (!TextUtils.isEmpty(startCityAddress)) {
            startCity = startCityAddress;
        }
        if (TextUtils.isEmpty(startCity)) {
            if (BuildConfig.DEBUG) {
                startCity = LocationManager.getInstance().getDebugLocationInfo().getCity();
            } else {
                startCity = LocationManager.getInstance().getCurrentCity();
            }
        }
        return startCity;

    }


    private String parsTrainEnd(Train train) {
        String endAreaAddress = "";
        String endCityAddress = "";
        if (train.semantic != null) {
            if (train.semantic.slots != null) {
                if (train.semantic.slots.endLoc != null) {
                    endAreaAddress = train.semantic.slots.endLoc.areaAddr;
                    endCityAddress = train.semantic.slots.endLoc.cityAddr;
                }
            }
        }
        String endCity = "";
        if (!TextUtils.isEmpty(endAreaAddress)) {
            endCity = endAreaAddress;
        }
        if (!TextUtils.isEmpty(endCityAddress)) {
            endCity = endCityAddress;
        }
        return endCity;

    }


    private void parserTrain(String trainJson) {
        try {
            final Train train = GsonHelper.fromJson(trainJson, Train.class);
            if (train == null || train.semantic == null || train.semantic.slots == null) {
                speakUnderstand();
                return;
            }
            String trainNum = train.semantic.slots.code;
            String startType = "";
            String startAreaAddr = "";
            String startCityAddr = "";
            String startAreaAddrDetail = "";
            String startCityAddrDetail = "";
            String startPoi = "";
            if (train.semantic.slots.startLoc != null) {
                startType = train.semantic.slots.startLoc.type;
                startAreaAddr = train.semantic.slots.startLoc.area;
                startCityAddr = train.semantic.slots.startLoc.city;
                startCityAddrDetail = train.semantic.slots.startLoc.cityAddr;
                startAreaAddrDetail = train.semantic.slots.startLoc.areaAddr;
                startPoi = train.semantic.slots.startLoc.poi;
            }
            String endType = "";
            String endAreaAddr = "";
            String endCityAddr = "";
            String endCityAddrDetail = "";
            String endAreaAddrDetail = "";
            String endPoi = "";
            if (train.semantic.slots.endLoc != null) {
                endType = train.semantic.slots.endLoc.type;
                endAreaAddr = train.semantic.slots.endLoc.area;
                endCityAddr = train.semantic.slots.endLoc.city;
                endCityAddrDetail = train.semantic.slots.endLoc.cityAddr;
                endAreaAddrDetail = train.semantic.slots.endLoc.areaAddr;
                endPoi = train.semantic.slots.endLoc.poi;
            }

            String startCity = "";
            if ("LOC_POI".equals(startType)) {
                if (!TextUtils.isEmpty(startPoi) && !"CURRENT_POI".equals(startPoi)) {
                    startCity = startPoi;
                }
            }

            if (TextUtils.isEmpty(startCity)) {
                if (!TextUtils.isEmpty(startAreaAddr)) {
                    startCity = startAreaAddr;
                }
                if (!TextUtils.isEmpty(startAreaAddrDetail)) {
                    startCity = startAreaAddrDetail;
                }
                if (!TextUtils.isEmpty(startCityAddr)) {
                    startCity = startCityAddr;
                }
                if (!TextUtils.isEmpty(startCityAddrDetail)) {
                    startCity = startCityAddrDetail;
                }
                if ("CURRENT_CITY".equals(startCity) || TextUtils.isEmpty(startCity)) {
                    startCity = LocationManager.getInstance().getCurrentCity();
                }
            }

            //定位失效，默认模拟定位点  只在debug下生效
            if (TextUtils.isEmpty(startCity) && BuildConfig.DEBUG) {
                startCity = LocationManager.DebugLocation.city;
            }


            String endCity = "";
            if ("LOC_POI".equals(endType)) {
                if (!TextUtils.isEmpty(endPoi) && !"CURRENT_POI".equals(endPoi)) {
                    endCity = endPoi;
                }
            }

            if (TextUtils.isEmpty(endCity)) {
                if (!TextUtils.isEmpty(endAreaAddr)) {
                    endCity = endAreaAddr;
                }
                if (!TextUtils.isEmpty(endAreaAddrDetail)) {
                    endCity = endAreaAddrDetail;
                }
                if (!TextUtils.isEmpty(endCityAddr)) {
                    endCity = endCityAddr;
                }
                if (!TextUtils.isEmpty(endCityAddrDetail)) {
                    endCity = endCityAddrDetail;
                }
                if ("CURRENT_CITY".equals(endCity) || TextUtils.isEmpty(endCity)) {
                    endCity = LocationManager.getInstance().getCurrentCity();
                }
            }

            String date = "";
            if (train.semantic.slots.startDate != null) {
                date = train.semantic.slots.startDate.date;
            }
            String category = null;
            String no_train_hint = getString(R.string.no_train);
            if (train.semantic.slots.category != null) {
                switch (train.semantic.slots.category) {
                    case "高铁":
                        category = "G";
                        no_train_hint = getString(R.string.no_G_train);
                        break;
                    case "动车":
                        category = "D";
                        no_train_hint = getString(R.string.no_D_train);
                        break;
                    case "特快":
                        category = "T";
                        break;
                    case "快速列车":
                    case "快速":
                        category = "K";
                        break;
                    case "直达列车":
                        category = "Z";
                        break;
                }
            }

            if (TextUtils.isEmpty(trainNum)) {
                queryTrainByDestination(startCity, endCity, date, category, no_train_hint);
            } else {
                TrainBean trainBean = new TrainBean();
                trainBean.train_no = trainNum;
                trainBean.start_station = startCity;
                trainBean.end_station = endCity;
                queryTrainByName(trainBean);
                //抱歉，暂不支持车次查询
//                showSearchError(context.getString(R.string.no_search_train_num));
            }
            uploadTrainInfo(startCity, endCity);
        } catch (Exception e) {
            e.printStackTrace();
            showSearchError();
        }
    }


    private void uploadTrainInfo(String startCity, String endCity) {
        Map<String, Object> option = new HashMap<>();
        option.put("startCity", startCity);
        option.put("endCity", endCity);
        // TODO: 2018/10/30 上报
        //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_TRAIN, option);
    }

    /**
     * 查询两地之间的高铁火车
     *
     * @param startCity
     * @param endCity
     */
    private void queryTrainByDestination(String startCity, String endCity, String date, String category, final String hint) {
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().searchTrain(startCity, endCity, date, category, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                List<TrainBean> trainInfos;
                TrainResult trainResult = GsonHelper.fromJson(result, TrainResult.class);
                if (trainResult != null) {
                    if (trainResult.resultCode == 1) {
                        if (trainResult.data != null) {
                            trainInfos = trainResult.data.list;
                            if (trainInfos != null && trainInfos.size() != 0) {
                                adapter = new TrainListAdapter(context, trainInfos);
                                adapter.setOnMultiPageItemClickListener(IatTrainScenario.this);
                                showMultiPageData(adapter, context.getString(R.string.search_result_tips));
                            } else {
                                showSearchError(hint);
                            }
                        } else {
                            showSearchError(hint);
                        }
                    } else {
//                        showSearchError(trainResult.resultMessage);
                        showSearchError(hint);
                    }
                } else {
                    showSearchError(hint);
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                KLog.e("---onFailure");
                showSearchError(getString(R.string.network_errors));
            }
        });
    }


    /**
     * 查询具体的动车信息
     *
     * @param trainBean
     */
    public void queryTrainByName(final TrainBean trainBean) {
        AssistantManager.getInstance().showMultiPageView(false);
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().searchTrainByName(trainBean.train_no, trainBean.start_station,
                trainBean.end_station, new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        AssistantManager.getInstance().showLoadingView(false);
                        String result = response.body();
                        if (!TextUtils.isEmpty(result)) {
                            TrainDetailResponse trainDetailResponse = GsonHelper.fromJson(result, TrainDetailResponse.class);
                            if (trainDetailResponse != null && trainDetailResponse.isSuccess() && trainDetailResponse.getData() != null &&
                                    trainDetailResponse.getData().station_list != null) {
                                trainDetailResponse.getData().price_list = trainBean.price_list;
                                assistantManager.getMultiPageView().setDetailPage(
                                        Constants.MultiplePageDetailType.TRAIN_DEATAIL, trainDetailResponse.getData());
                                assistantManager.startListeningForChoose(getSrSceneStksCmd());
                            } else {
                                //showSearchError();
                                XMToast.showToast(context, R.string.search_error_and_try_later);
                            }
                        } else {
                            //showSearchError();
                            XMToast.showToast(context, R.string.search_error_and_try_later);
                        }
                        AssistantManager.getInstance().showMultiPageView(true);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        AssistantManager.getInstance().showLoadingView(false);
                        AssistantManager.getInstance().showMultiPageView(true);
                        super.onError(response);
                    }
                });
    }


    @Override
    public void onChoose(String voiceText) {
        final boolean isFirstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean isLastPage = assistantManager.getMultiPageView().isLastPage();
        final boolean isDetailPage = assistantManager.getMultiPageView().isDetailPage();
        final TrainDetailView view = (TrainDetailView) assistantManager.getMultiPageView().getDetailView();
        switchChooseAction(voiceText, new IChooseCallback() {
            @Override
            public void previousPageAction() {
                if (isDetailPage) {
                    KLog.d("Detail page's pre page");
                    view.setCurPage(-1);
                } else {
                    if (isFirstPage) {
                        KLog.d("It is in first page");
                    } else {
                        assistantManager.getMultiPageView().setPage(-1);
                    }
                }
            }

            @Override
            public void nextPageAction() {
                if (isDetailPage) {
                    KLog.d("Detail page's last page");
                    view.setCurPage(1);
                } else {
                    if (isLastPage) {
                        KLog.d("It is in last page");
                    } else {
                        assistantManager.getMultiPageView().setPage(1);
                    }
                }

            }

            @Override
            public void chooseItemAction(int action) {
                if (!isDetailPage) {
                    KLog.d("open train detail");
                    queryTrainByName(adapter.getCurrentList().get(action));
                }
            }

            @Override
            public void lastAction() {
                if (!isDetailPage) {
                    KLog.d(adapter.getCurrentList().get(adapter.getCurrentList().size() - 1));
                    KLog.d("choose last one");
                }
            }

            @Override
            public void cancelChooseAction() {
                if (adapter != null) {
                    assistantManager.showMultiPageView(adapter);
                }
                if (isDetailPage) {
                    assistantManager.getMultiPageView().hideDetailPage();
                    assistantManager.startListeningForChoose(getSrSceneStksCmd());
                } else {
//                    stopListening();
//                    assistantManager.hideMultiPageView();
//                    startListening();
                    assistantManager.closeAssistant();
                }

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
        if (assistantManager.getMultiPageView().isDetailPage()) {
            StksCmd stksCmd = new StksCmd();
            stksCmd.setType("train");
            stksCmd.setNliScene("train");
            ArrayList<String> search = new ArrayList<>();
            search.add("semantic.slots.item");
            search.add("semantic.slots.default");
            stksCmd.setNliFieldSearch(search);
            ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
            stksCmdNliScenes.addAll(getDefaultCmd(0));
            stksCmd.setList(stksCmdNliScenes);
            String result = GsonHelper.toJson(stksCmd);
            KLog.json(result);
            return result;
        } else {
            List data = adapter.getCurrentList();
            if (ListUtils.isEmpty(data)) {
                return "";
            }
            StksCmd stksCmd = new StksCmd();
            stksCmd.setType("train");
            stksCmd.setNliScene("train");
            ArrayList<String> search = new ArrayList<>();
            search.add("semantic.slots.train");
            search.add("semantic.slots.item");
            search.add("semantic.slots.default");
            stksCmd.setNliFieldSearch(search);
            ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
            int size = data.size();
            for (int i = 0; i < size; i++) {
                StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
                stksCmdNliScene.setId(i + 1);
                ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
                TrainBean bean = (TrainBean) data.get(i);
                if (!TextUtils.isEmpty(bean.train_no)) {
                    StksCmdDimension stksCmdDimension = new StksCmdDimension();
                    stksCmdDimension.setField("train");
                    stksCmdDimension.setVal(bean.train_no);
                    dimensions.add(stksCmdDimension);
                }
                addDefaultCmdByNumber(i, size, dimensions);
                if (!ListUtils.isEmpty(dimensions)) stksCmdNliScene.setDimension(dimensions);
                stksCmdNliScenes.add(stksCmdNliScene);
            }
            stksCmdNliScenes.addAll(getDefaultCmd(size));
            stksCmd.setList(stksCmdNliScenes);
            String result = GsonHelper.toJson(stksCmd);
            KLog.json(result);
            return result;
        }

    }

    @Override
    public void onItemClick(int position) {
        queryTrainByName(adapter.getCurrentList().get(position));
        adapter.setSelectPosition(position);
    }
}
