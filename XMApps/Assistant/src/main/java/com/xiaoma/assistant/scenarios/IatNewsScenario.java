package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.handler.AssistantWordHandler;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.NewsInfo;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.RadioSlots;
import com.xiaoma.assistant.model.parser.TodayNewsBean;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.NewsListAdapter;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：新闻场景
 */
public class IatNewsScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {
    private static final int PAGE = 1;
    private NewsListAdapter adapter;
    private Timer timer;
    private String[] newType;
    private boolean ttsNews;


    public IatNewsScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
//        String data = "clear";
//        if (parseResult.isNewsAction()) {
//            data = context.getString(R.string.news_word);
//        }
        String category = getCategory(parseResult.getSlots());
        String speekContent = getSpeekContent(parseResult.getSlots());
        final String keyWord = getKeyWord(parseResult);
        String data = parseResult.getData();
        KLog.d("data : " + data);
        String channelName = "";
        String title = "";
        if (TextUtils.isEmpty(category) && TextUtils.isEmpty(keyWord) && TextUtils.isEmpty(voiceJson)) {
            speakSearchEmptyTips();
            return;
        }
        if (!TextUtils.isEmpty(keyWord)) {
            title = keyWord;
        } else if (!TextUtils.isEmpty(category)) {
            channelName = category;
        }
        final String errorContent;
        if (TextUtils.isEmpty(category) && TextUtils.isEmpty(title)) {
            errorContent = context.getString(R.string.find_no_keyword_news);
        } else {
            errorContent = context.getString(R.string.find_no_specific_news);
        }
        final String finalNewKeys;
        if (speekContent != null && !TextUtils.isEmpty(speekContent)) {
            finalNewKeys = speekContent;
        } else {
            finalNewKeys = channelName;
        }
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().fetchNewsListInfo(channelName, title, PAGE, 20, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                NewsInfo newsInfo = GsonHelper.fromJson(result, NewsInfo.class);
                if (newsInfo == null) {
                    showSearchError(errorContent);
                    return;
                }
                NewsInfo.DataBean data = newsInfo.getData();
                if (data == null) {
                    showSearchError(errorContent);
                    return;
                }
                List<NewsInfo.DataBean.ContentlistBean> newsList = data.getContentlist();
                if (newsList.isEmpty()) {
                    showSearchError(errorContent);
                    return;
                }
                adapter = new NewsListAdapter(context, newsList);
                adapter.setOnMultiPageItemClickListener(IatNewsScenario.this);
                showMultiPageData(adapter);
                if (newsList.size() == 1) {
                    assistantManager.speakContent(newsList.get(0).getDesc(), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            executeSkillSucceeded();
                        }

                        @Override
                        public void onError(int code) {
                            executeSkillSucceeded();
                        }
                    });
                } else {
                    speakContentAndListenerByTime(String.format(context.getString(R.string.find_specific_news), newsList.size(), finalNewKeys), 5000);
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

    private String getSpeekContent(String slots) {
        if (TextUtils.isEmpty(slots)) {
            return null;
        }
        String speek = null;
        RadioSlots slotsObject = GsonHelper.fromJson(slots, RadioSlots.class);
        if (slotsObject != null && !TextUtils.isEmpty(slotsObject.category)) {
            speek = slotsObject.category;
        }
        return speek;
    }

    private void speakContentAndListenerByTime(String speakContent, final int delayTime) {
        ttsNews = true;
        speakContent(speakContent, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                assistantManager.startListeningForChoose(getSrSceneStksCmd());
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!ttsNews) {
                            return;
                        }
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                if (!adapter.getAnimState()) {
                                    adapter.setPlayPostion(0);
                                    continuousPlayback();
                                }
                            }
                        });
                    }
                }, delayTime);
            }

            @Override
            public void onBegin() {

            }
        });
    }

    @Override
    public boolean handleBack() {
        executeSkillSucceeded();
        ttsNews = false;
        return super.handleBack();
    }

    private List<NewsInfo.DataBean.ContentlistBean> convertTodayNewsBeanToContentlistBean(TodayNewsBean todayNewsBean) {
        List<NewsInfo.DataBean.ContentlistBean> convertBeans = new ArrayList<>();
        if (todayNewsBean != null) {
            List<TodayNewsBean.DataBean.ResultBean> result = todayNewsBean.getData().getResult();
            if (result != null || result.size() != 0) {
                for (TodayNewsBean.DataBean.ResultBean resultBean : result) {
                    NewsInfo.DataBean.ContentlistBean contentlistBean = new NewsInfo.DataBean.ContentlistBean();
                    contentlistBean.setTitle(resultBean.getTitle());
                    contentlistBean.setDesc(resultBean.getDescription());
                    contentlistBean.setChannelName(resultBean.getCategory());
                    contentlistBean.setId(resultBean.getId());
                    List<NewsInfo.DataBean.ContentlistBean.ImageurlsBean> imageurlsBeans = new ArrayList<>();
                    NewsInfo.DataBean.ContentlistBean.ImageurlsBean imageurlsBean = new NewsInfo.DataBean.ContentlistBean.ImageurlsBean();
                    imageurlsBean.setUrl(resultBean.getPicUrl());
                    imageurlsBeans.add(imageurlsBean);
                    contentlistBean.setImageurls(imageurlsBeans);
                    convertBeans.add(contentlistBean);
                }
            }
            return convertBeans;
        }
        return convertBeans;
    }

    private String getKeyWord(LxParseResult parseResult) {
        String result = "";
        if (parseResult != null) {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getSlots());
                if (jsonObject.has("keyword")) {
                    result = jsonObject.getString("keyword");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Nullable
    private String getCategory(String slots) {
        if (TextUtils.isEmpty(slots)) {
            return null;
        }
        String category = null;
        RadioSlots slotsObject = GsonHelper.fromJson(slots, RadioSlots.class);
        if (slotsObject != null && !TextUtils.isEmpty(slotsObject.category)) {
            String data = slotsObject.category;
            if (getString(R.string.news_word).equals(data)) {
                category = context.getString(R.string.inland_newest);
            } else if (data.contains(context.getString(R.string.inland_word))) {
                category = context.getString(R.string.inland_newest);
            } else if (data.contains(context.getString(R.string.international_word))) {
                category = context.getString(R.string.international_newest);
            } else {
                category = getNewType(slotsObject.category);
            }
        }
        return category;
    }


    public String getNewType(String key) {
        String type = "";
        if (newType == null) {
            newType = context.getResources().getStringArray(R.array.new_type);
        }
        for (String s : newType) {
            boolean contains = s.contains(key);
            if (contains) {
                type = s;
                break;
            }
        }
        return type;
    }

    @Override
    public void onChoose(String voiceText) {
        if (timer != null) {
            timer.cancel();
        }
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
                adapter.setPlayPostion(action);
                continuousPlayback();
                assistantManager.getMultiPageView().scrollTo(action);
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

    protected void showSearchError() {
        String errorText = getString(R.string.no_data_response);
        addFeedBackConversation(errorText);
        speakContent(errorText, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                startListening();
            }
        });
    }

    protected void speakSearchEmptyTips() {
        addFeedBackConversation(getString(R.string.search_news_empty_tips));
        speakContent(getString(R.string.search_news_empty_tips), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                startListening();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (adapter != null) {
            adapter.setPlayPostion(position);
            continuousPlayback();
        }
    }

    private WrapperSynthesizerListener wrapperSynthesizerListener = new WrapperSynthesizerListener() {
        @Override
        public void onCompleted() {
            super.onCompleted();
            if (adapter != null && assistantManager != null && assistantManager.inMultipleForChooseMode()) {
                if (adapter.getItemCount() > adapter.getPlayPostion() + 1) {
                    adapter.setPlayPostion(adapter.getPlayPostion() + 1);
                    assistantManager.getMultiPageView().scrollTo(adapter.getPlayPostion());
                    continuousPlayback();
                } else {
                    executeSkillSucceeded();
                }
            }
        }

        @Override
        public void onError(int code) {

        }
    };

    private void continuousPlayback() {
        if (adapter != null) {
            assistantManager.stopSpeak();
            String content = adapter.getContent();
            if (content == null || TextUtils.isEmpty(content)) {
                adapter.getDesc();
            }
            speakContent(content, wrapperSynthesizerListener);
        }
    }

    @Override
    protected String getSrSceneStksCmd() {
        if (assistantManager == null) {
            return "";
        }

        if (assistantManager.getMultiPageView() == null) {
            return "";
        }

        List<NewsInfo.DataBean.ContentlistBean> currentList = adapter.getCurrentList();
        if (currentList.isEmpty()) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("news");
        stksCmd.setNliScene("news");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = currentList.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            addDefaultCmdByNumber(i, size, dimensions);
            if (!ListUtils.isEmpty(dimensions)) {
                stksCmdNliScene.setDimension(dimensions);
            }
            stksCmdNliScenes.add(stksCmdNliScene);
        }
        stksCmdNliScenes.addAll(getDefaultCmd(size));
        stksCmdNliScenes.addAll(getExitCmd());
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
    }

    private List<StksCmdNliScene> getExitCmd() {
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
        ArrayList<StksCmdDimension> stksCmdDimensions = new ArrayList<>();
        stksCmdNliScene.setId(AssistantWordHandler.RECOGNIZE_RESULT_CANCEL + 1);
        StksCmdDimension stksCmdDimension = new StksCmdDimension();
        stksCmdDimension.setField("default");
        stksCmdDimension.setVal(context.getString(R.string.exit_cmd));
        stksCmdDimensions.add(stksCmdDimension);
        stksCmdNliScene.setDimension(stksCmdDimensions);
        stksCmdNliScenes.add(stksCmdNliScene);
        return stksCmdNliScenes;
    }
}
