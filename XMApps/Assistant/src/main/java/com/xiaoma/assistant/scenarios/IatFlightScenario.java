package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.FlightV2;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.Flight;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.FlightListAdapter;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
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
 * Desc：航班场景
 */
public class IatFlightScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private FlightListAdapter adapter;
    private String CODE_OK = "1";
    private boolean canNotGetLocation;

    public IatFlightScenario(Context context) {
        super(context);
    }


    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        canNotGetLocation = false;
        String text = parseResult.getText();
        String result = parseResult.getSlots();
        if (!isCorrect(result)) {
            if (checkFlightCity(parseResult.getInputResult())) {
                parserFlight(parseResult.getInputResult());
            } else {
                if (canNotGetLocation) {
                    speakNoLocation();
                } else {
                    speakContent(context.getString(R.string.query_flight_error));
                }
            }
        } else {
            speakContent(context.getString(R.string.query_flight_error));
        }
    }

    private boolean isCorrect(String slots) {
        boolean isCorrect = true;
        if (!TextUtils.isEmpty(slots)) {
            isCorrect = false;
        }
        return isCorrect;
    }

    private boolean checkFlightCity(String jsonString) {
        boolean checkCity = false;
        if (!TextUtils.isEmpty(jsonString)) {
            Flight flight = GsonHelper.fromJson(jsonString, Flight.class);
            if (flight != null && !TextUtils.isEmpty(parsFlightStart(flight))) {
                checkCity = true;
            }
        }
        return checkCity;
    }

    private String parsFlightStart(Flight flight) {
        String startAreaAddress = "";
        String startCityAddress = "";
        if (flight.semantic.slots.startLoc != null) {
            startAreaAddress = flight.semantic.slots.startLoc.areaAddr;
            startCityAddress = flight.semantic.slots.startLoc.cityAddr;
        }

        String startCity = "";
        if (!TextUtils.isEmpty(startAreaAddress)) {
            startCity = startAreaAddress;
        }
        if (!TextUtils.isEmpty(startCityAddress)) {
            startCity = startCityAddress;
        }
        if (TextUtils.isEmpty(startCity)) {
            startCity = LocationManager.getInstance().getCurrentCity();
            if (TextUtils.isEmpty(startCity)) {
                if (BuildConfig.DEBUG) {
                    startCity = LocationManager.getInstance().getDebugLocationInfo().getCity();
                } else {
                    canNotGetLocation = true;
                }
            }
        }

        return startCity;
    }


    private void parserFlight(String flightJson) {
        Flight flight = GsonHelper.fromJson(flightJson, Flight.class);
        // 这里需要每个都需要判空
        if (flight == null || flight.semantic == null || flight.semantic.slots == null) {
            return;
        }

        String flightNo = flight.semantic.slots.flightNo;

        String startAreaAddr = "";
        String startCityAddr = "";
        String startCityAddrDetail = "";
        String startAreaAddrDetail = "";
        if (flight.semantic.slots.startLoc != null) {
            startAreaAddr = flight.semantic.slots.startLoc.area;
            startCityAddr = flight.semantic.slots.startLoc.city;
            startCityAddrDetail = flight.semantic.slots.startLoc.cityAddr;
            startAreaAddrDetail = flight.semantic.slots.startLoc.areaAddr;
        }
        String endAreaAddr = "";
        String endCityAddr = "";
        String endCityAddrDetail = "";
        String endAreaAddrDetail = "";
        if (flight.semantic.slots.endLoc != null) {
            endAreaAddr = flight.semantic.slots.endLoc.area;
            endCityAddr = flight.semantic.slots.endLoc.city;
            endCityAddrDetail = flight.semantic.slots.endLoc.cityAddr;
            endAreaAddrDetail = flight.semantic.slots.endLoc.areaAddr;
        }
        String dateTime = "";
        if (flight.semantic.slots.startDate != null) {
            dateTime = flight.semantic.slots.startDate.date;
        }
        String startCity = "";
        String endCity = "";
        String date = "";
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
        if (TextUtils.isEmpty(startCity) || "CURRENT_CITY".equals(startCity)) {
            startCity = LocationManager.getInstance().getCurrentCity();
        }

        //定位失效，默认模拟定位点  只在debug下生效
        if (BuildConfig.DEBUG && TextUtils.isEmpty(startCity)) {
            startCity = LocationManager.DebugLocation.city;
        }

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
        if (TextUtils.isEmpty(endCity) || "CURRENT_CITY".equals(endCity)) {
            endCity = LocationManager.getInstance().getCurrentCity();
        }

        if (!TextUtils.isEmpty(dateTime)) {
            date = dateTime;
        }

        if (TextUtils.isEmpty(flightNo)) {
            queryFlightByDestination(startCity, endCity, date);
        } else {
//                queryFlightByNumber(flightNo, date);
            //抱歉，暂不支持航次查询
            showSearchError(context.getString(R.string.no_search_flight_num));
        }
        uploadFlightInfo(startCity, endCity, date);
    }

    private void uploadFlightInfo(String startCity, String endCity, String date) {
        Map<String, Object> option = new HashMap<>();
        option.put("startCity", startCity);
        option.put("endCity", endCity);
        option.put("date", date);
        // TODO: 2018/10/30 打点
        //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_FLIGHT, option);
    }


    /**
     * 查询一个城市到另外一个城市的航班信息
     *
     * @param startCity
     * @param endCity
     * @param date
     */
    private void queryFlightByDestination(String startCity, final String endCity, final String date) {
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().searchFlight(startCity, endCity, date, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                FlightV2 flightV2 = GsonHelper.fromJson(result, FlightV2.class);
                if (flightV2 == null) {
                    showSearchError(getString(R.string.no_flight));
                    return;
                }

                if (CODE_OK.equals(flightV2.resultCode)) {
                    if (flightV2.data != null && flightV2.data.list != null) {
                        List<FlightV2.DataBean.ListBean> flightList = flightV2.data.list;
                        adapter = new FlightListAdapter(context, flightList);
                        adapter.setOnMultiPageItemClickListener(IatFlightScenario.this);
                        String tips;
                        if (flightList.size() == 0) {
                            if (TextUtils.isEmpty(date)) {
                                tips = getString(R.string.no_flight);
                            } else {
                                tips = getString(R.string.no_flight);
                            }
                            continueListening(tips);
                            return;
                        } else {
                            tips = String.format(getString(R.string.flight_result_speak), endCity, flightList.size());
                        }
                        showMultiPageData(adapter, tips);
                        setSearchResultOperate();
                    } else {
                        continueListening(getString(R.string.no_flight));
                    }
                } else {
                    /*if (!TextUtils.isEmpty(flightV2.resultMessage)) {
                        showSearchError(flightV2.resultMessage);
                    } else {
                        showSearchError(getString(R.string.no_flight));
                    }*/
                    String tips;
                    if (TextUtils.isEmpty(date)) {
                        tips = getString(R.string.no_flight);
                    } else {
                        tips = getString(R.string.no_flight);
                    }
                    continueListening(tips);
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                showSearchError(getString(R.string.network_errors));
            }
        });
    }


    private void continueListening(String content) {
        addFeedbackAndSpeak(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                startListening();
            }
        });
    }


    private void setSearchResultOperate() {
        TextView searchResultOperate = assistantManager.getSearchResultOperate();
        String prefix = context.getString(R.string.please_choose_or_cancel);
        String content = context.getString(R.string.search_result_page_choose);
        SpannableString spannableString = new SpannableString(prefix + content);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.white)), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), prefix.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchResultOperate.setText(spannableString);
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
                KLog.d("choose flight");
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
        return true;
    }

    @Override
    public void onEnd() {

    }

    protected void showSearchError() {
        String errorText = getString(R.string.no_data_response);
        addFeedBackConversation(errorText);
        speakContent(errorText);
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
//        if(assistantManager.getMultiPageView().isDetailPage()){
//            StksCmd stksCmd = new StksCmd();
//            stksCmd.setType("flight");
//            stksCmd.setNliScene("flight");
//            ArrayList<String> search = new ArrayList<>();
//            search.add("semantic.slots.item");
//            search.add("semantic.slots.default");
//            stksCmd.setNliFieldSearch(search);
//            ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
//            stksCmdNliScenes.addAll(getDefaultCmd(size));
//            stksCmd.setList(stksCmdNliScenes);
//            String result = GsonHelper.toJson(stksCmd);
//            KLog.json(result);
//            return result;
//        }else{
        List data = adapter.getCurrentList();
        if (ListUtils.isEmpty(data)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("flight");
        stksCmd.setNliScene("flight");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            addDefaultCmdByNumber(i, size, dimensions);
            if (!ListUtils.isEmpty(dimensions)) stksCmdNliScene.setDimension(dimensions);
            stksCmdNliScenes.add(stksCmdNliScene);
        }
        stksCmdNliScenes.addAll(getDefaultCmd(size));
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
//        }
    }

    @Override
    public void onItemClick(int position) {
//        Toast.makeText(context, "航班", Toast.LENGTH_SHORT).show();
//        closeVoicePopup();
        adapter.setSelectPosition(position);
    }
}
