package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.SortChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.handler.AssistantWordHandler;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.model.BisMvwSTKS;
import com.xiaoma.assistant.model.ConfirmWord;
import com.xiaoma.assistant.model.OptionWord;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.assistant.view.MultiPageView;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.model.SeoptType;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vrfactory.ivw.XmIvwManager;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/31
 * Desc：
 */
public abstract class IatScenario {
    protected IAssistantManager assistantManager = AssistantManager.getInstance();
    private AssistantWordHandler wordHandler;
    protected LxParseResult parseResult;
    protected Context context;
    protected boolean executeSkill;
    protected ExecResult execResult;
    protected SkillCallback skillCallback;
    protected String[] okCloseAnswers = {"关掉了", "好嘞", "诺"};
    //随机动作
    public static int[] mAction = new int[]{AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM1
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM2
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM3};

    public IatScenario(Context context) {
        this.context = context;
        init();
    }

    public abstract void init();

    public abstract void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session);

    public abstract void onChoose(String voiceText);

    public abstract boolean isIntercept();

    public abstract void onEnd();


    protected void addFeedBackConversation(String text) {
        assistantManager.addFeedBackConversation(text);
    }

    protected void addFeedbackAndSpeak(String text) {
        assistantManager.addFeedBackConversation(text);
        speakContent(text);
    }

    protected void addFeedbackAndSpeak(String text, WrapperSynthesizerListener listener) {
        assistantManager.addFeedBackConversation(text);
        speakContent(text, listener);
    }


    protected void addConversationToList(ConversationItem item) {
        if (item != null) {
            assistantManager.addItemToConversationList(item);
        }
    }

    protected boolean showMultiPageData(BaseMultiPageAdapter adapter) {
        assistantManager.showMultiPageView(adapter);
        assistantManager.startListeningForChoose(getSrSceneStksCmd());
        //todo
        return true;
    }

    public SeoptType getLocalSeoptType() {
        return AssistantManager.getInstance().getLocalSeoptType();
    }


    //二级页面播报
    protected boolean showMultiPageData(BaseMultiPageAdapter adapter, String tips) {
        assistantManager.showMultiPageView(adapter);
        assistantManager.startListeningForChoose(getSrSceneStksCmd());
        if (!TextUtils.isEmpty(tips)) {
            speakContent(tips, new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    //                    assistantManager.startListeningForChoose(getSrSceneStksCmd());
                }

                @Override
                public void onBegin() {

                }
            });
        } else {
            assistantManager.startListeningForChoose(getSrSceneStksCmd());
        }
        //todo
        return true;
    }

    protected boolean showContactPageData(BaseMultiPageAdapter adapter) {
        assistantManager.showContactView(adapter);
        assistantManager.startListeningForChoose(getSrSceneStksCmd());
        return true;
    }

    protected boolean displayMusicRecognitionView(boolean show) {
        assistantManager.displayMusicRecognitionView(show);
        //TODO 音乐识别
        return true;
    }

    protected void speakUnderstand() {
        if (AssistantManager.getInstance().isShowing()) {
            assistantManager.speakUnderstand();
        }
    }

    public void speakNoLocation() {
        addFeedbackAndSpeak(getString(R.string.error_cannot_location));
    }

    public void canNotFoundAddress() {
        speakThenListening(context.getString(R.string.can_not_find_address));
    }

    protected void speakCanNotGetLongitude() {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                addFeedbackAndSpeak(context.getString(R.string.can_not_get_lon_and_lay));
            }
        });
    }

    protected void speakContent(String text) {
        if (AssistantManager.getInstance().isShowing()) {
            assistantManager.speakContent(text);
        }
    }

    protected void speakContent(String text, WrapperSynthesizerListener listener) {
        if (assistantManager.isShowing()) {
            speakContentWithoutCheckShowing(text, listener);
        }
    }

    private void speakContentWithoutCheckShowing(String text, WrapperSynthesizerListener listener) {
        assistantManager.speakContent(text, listener);
        assistantManager.addFeedBackConversation(text);
    }

    public void closeAfterSpeak(int id) {
        closeAfterSpeak(context.getString(id));
    }

    public void closeAfterSpeak(String content) {
        addFeedbackAndSpeak(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }

    protected void addFeedbackAndSpeakMultiTone(final String showtext, final String speaktext, OnTtsListener onIatListener) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                AssistantManager.getInstance().addFeedBackConversation(showtext);
                AssistantManager.getInstance().speakContent(speaktext, onIatListener);
            }
        });
    }

    void speakThenListening(String content) {
        assistantManager.addFeedBackConversation(content);
        assistantManager.speakThenListening(content);
    }

    void speakAndFeedListening(String speak, String feed) {
        assistantManager.addFeedBackConversation(feed);
        assistantManager.speakThenListening(speak);
    }

    void speakMultiToneListening(String speakText, String showText) {
        assistantManager.addFeedBackConversation(showText);
        assistantManager.speakThenListening(speakText);
    }

    public boolean handleBack() {
        return false;
    }

    protected void closeVoicePopup() {
        assistantManager.closeAssistant();
    }

    protected void startListening() {
        assistantManager.startListening(false);
    }

    protected void stopListening() {
        assistantManager.stopListening();
    }

    protected void speakSearchEmptyTips() {
        addFeedBackConversation(getString(R.string.search_music_empty_tips));
        speakContent(getString(R.string.search_music_empty_tips), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                startListening();
            }
        });
    }

    protected void showSearchError() {
        String errorText = getString(R.string.search_error_empty);
        addFeedBackConversation(errorText);
        speakContent(errorText);

    }

    protected void showSearchError(int errorRes) {
        String errorText = getString(errorRes);
        showSearchError(errorText);
    }

    protected void showSearchError(String errorText) {
        XmTtsManager.getInstance().stopSpeaking();
        addFeedBackConversation(errorText);
        speakContent(errorText);
    }


    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected boolean isContains(String voiceContent, int resId) {
        String texts = getString(resId);
        String[] splits = texts.split("\\|");
        if (splits.length == 0) {
            return false;
        }
        for (String split : splits) {
            if (split.isEmpty()) {
                continue;
            }
            if (voiceContent.contains(split)) {
                return true;
            }
        }
        return false;
    }


    protected void switchChooseAction(String voiceText, IChooseCallback callback) {
        Log.d("QBX", "onChoose: ");
        if (callback == null) {
            return;
        }
        if (wordHandler == null) {
            wordHandler = new AssistantWordHandler(context);
        }

        int action = wordHandler.recognizeOptionIndex(voiceText);
        if (action == AssistantWordHandler.RECOGNIZE_RESULT_LAST) {
            callback.lastAction();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_PREVIOUS) {
            callback.previousPageAction();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_NEXT) {
            callback.nextPageAction();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_ERROR) {
            callback.errorChooseActon();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_CANCEL) {
            callback.cancelChooseAction();
        } else if (action >= AssistantWordHandler.RECOGNIZE_RESULT_INDEX_START) {
            callback.assignPageAction(getPageForAction(action));
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_INDEX_LAST) {
            callback.assignPageAction(Integer.MAX_VALUE);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_PRICE && callback instanceof SortChooseCallback) {
            Log.d("QBX", "sortByPrice");
            ((SortChooseCallback) callback).sortByPrice();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_DISTANCE && callback instanceof SortChooseCallback) {
            Log.d("QBX", "sortByDistance");
            ((SortChooseCallback) callback).sortByDistance();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_STAR_LEVEL && callback instanceof SortChooseCallback) {
            Log.d("QBX", "sortByStarLevel");
            ((SortChooseCallback) callback).sortByStarLevel();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_SCORE && callback instanceof SortChooseCallback) {
            Log.d("QBX", "sortByScore");
            ((SortChooseCallback) callback).sortByScore();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_STAR_LEVEL && callback instanceof SortChooseCallback) {
            Log.d("QBX", "filterByStarLevel");
            BisMvwSTKS stks = GsonHelper.fromJson(voiceText, BisMvwSTKS.class);
            if (stks != null && !TextUtils.isEmpty(stks.getBisMvwSTKS())) {
                if (stks.getSelectList() != null && stks.getSelectList().getList() != null &&
                        stks.getSelectList().getList().size() > 0) {
                    String text = stks.getText();
                    ((SortChooseCallback) callback).filterByStarLevel(text);
                } else {
                    ((SortChooseCallback) callback).filterByStarLevel(voiceText);
                }
            } else {
                ((SortChooseCallback) callback).filterByStarLevel(voiceText);
            }
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_PRICE_MIN && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).filterByPrice(true);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_PRICE_MAX && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).filterByPrice(false);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_SCORE_MAX && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).filterByHighScore();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MAX && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).filterByDistance(false);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MIN && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).filterByDistance(true);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_HEAD_NUMBER && callback instanceof SortChooseCallback) {
            BisMvwSTKS stks = GsonHelper.fromJson(voiceText, BisMvwSTKS.class);
            String text = stks.getText();
            if (TextUtils.isEmpty(text)) return;
            String headNumber = text.replace(context.getString(R.string.at_the_beginning), "");
            ((SortChooseCallback) callback).filterByHeadNumber(headNumber);
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_CONFIRM_OPERATION && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).confirm();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_CANCEL_OPERATION && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).cancel();
        } else if (action == AssistantWordHandler.RECOGNIZE_RESULT_CORRECT && callback instanceof SortChooseCallback) {
            ((SortChooseCallback) callback).correct();
        } else {
            if (assistantManager.getMultiPageView() != null
                    && assistantManager.getMultiPageView().getAdapter() != null
                    && !ListUtils.isEmpty(assistantManager.getMultiPageView().getAdapter().getCurrentList())
                    && assistantManager.getMultiPageView().getAdapter().getCurrentList().size() > action) {
                assistantManager.getMultiPageView().getAdapter().setSelectPosition(action);
            }
            callback.chooseItemAction(action);
        }
    }

    private int getPageForAction(int action) {
        int result = 1;
        result = action - AssistantWordHandler.RECOGNIZE_RESULT_INDEX_START + 1;
        return result;
    }


    protected void addDefaultCmdByNumber(int index, int size, ArrayList<StksCmdDimension> dimensions) {
        /*String number = AssistantUtils.numberToChinese(index + 1);
        //因为OptionWord.json中下标是从0开始，在所有的模糊匹配下标都有加1，因此此处对比OptionWord.json也加1
        String[] defaultCmd = new String[]{"个", "条", "首", "项", "页"};
        StksCmdDimension stksCmdDimension = new StksCmdDimension();
        stksCmdDimension.setField("item");
        StringBuilder cmd = new StringBuilder();
        for (int i = 0; i < defaultCmd.length; i++) {
            if (!TextUtils.isEmpty(number) && i < defaultCmd.length - 1) {
                cmd.append("第" + number + defaultCmd[i]);
                cmd.append(",");
            }
            if (index == size - 1) {
                cmd.append("最后一" + defaultCmd[i]);
                cmd.append(",");
            }
        }
        stksCmdDimension.setVal(cmd.toString());
        stksCmdDimension.setSpword(true);
        if (!TextUtils.isEmpty(stksCmdDimension.getVal())) dimensions.add(stksCmdDimension);*/

        //减少可见即可说匹配
        String number = AssistantUtils.numberToChinese(index + 1);
        //因为OptionWord.json中下标是从0开始，在所有的模糊匹配下标都有加1，因此此处对比OptionWord.json也加1
        StksCmdDimension stksCmdDimension = new StksCmdDimension();
        stksCmdDimension.setField("item");
        StringBuilder cmd = new StringBuilder();
        if (!TextUtils.isEmpty(number)) {
            cmd.append("第" + number + "个");
            cmd.append(",");
            cmd.append("第" + number + "条");
        }
        if (index == size - 1) {
            cmd.append(",");
            cmd.append("最后一个");
            cmd.append(",");
            cmd.append("最后一条");
        }
        stksCmdDimension.setVal(cmd.toString());
        stksCmdDimension.setSpword(true);
        if (!TextUtils.isEmpty(stksCmdDimension.getVal())) dimensions.add(stksCmdDimension);

    }


    protected List<StksCmdNliScene> getDefaultCmd(int size) {
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        //因为OptionWord.json中下标是从0开始，在所有的模糊匹配下标都有加1，因此此处对比OptionWord.json也加1
        //        String[][] defaultCmd = new String[][]
        //                {context.getResources().getStringArray(R.array.next_page),
        //                        context.getResources().getStringArray(R.array.previous_page),
        //                        context.getResources().getStringArray(R.array.cancel_action)};
        if (wordHandler == null) {
            wordHandler = new AssistantWordHandler(context);
        }
        String field = "default";
        List<OptionWord> wordList = wordHandler.getWordList();
        for (int i = 0; i < wordList.size(); i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(wordList.get(i).getNumber() + 1);
            ArrayList<StksCmdDimension> stksCmdDimensions = new ArrayList<>();
            StringBuilder cmd = new StringBuilder();
            cmd.append(wordList.get(i).getKeyWord());
            if (i == wordList.size() - 1) {
                List<String> wakeUpCmd = XmIvwManager.getInstance().getWakeUpWord();
                if (!ListUtils.isEmpty(wakeUpCmd)) {
                    for (String wakeUp : wakeUpCmd) {
                        cmd.append(",");
                        cmd.append(wakeUp);
                    }
                }
            }
            StksCmdDimension stksCmdDimension = new StksCmdDimension();
            stksCmdDimension.setField(field);
            stksCmdDimension.setVal(cmd.toString());
            stksCmdDimension.setSpword(true);
            stksCmdDimensions.add(stksCmdDimension);
            stksCmdNliScene.setDimension(stksCmdDimensions);
            stksCmdNliScenes.add(stksCmdNliScene);
        }
        if (size != 0) {
            addPageCmd(stksCmdNliScenes, field, size);
        }

        return stksCmdNliScenes;
    }

    private StksCmdNliScene getCmd(int id, String field, String... values) {
        ArrayList<StksCmdDimension> stksCmdDimensions = new ArrayList<>();
        for (String value : values) {
            StksCmdDimension stksCmdDimension = new StksCmdDimension();
            stksCmdDimension.setField(field);
            stksCmdDimension.setVal(value);
            stksCmdDimensions.add(stksCmdDimension);
        }
        StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
        stksCmdNliScene.setDimension(stksCmdDimensions);
        stksCmdNliScene.setId(id + 1);
        return stksCmdNliScene;
    }

    private void addCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search, int id, String field, String... values) {
        stksCmdNliScenes.add(getCmd(id, field, values));
        search.add("semantic.slots." + field);
    }

    void addSortByPriceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_PRICE, "sort_by_price", context.getString(R.string.sort_by_price), context.getString(R.string.cheap));
    }

    void addSortByDistanceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_DISTANCE, "sort_by_distance", context.getString(R.string.sort_by_distance), context.getString(R.string.near));
    }

    void addSortByStarLevelCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_STAR_LEVEL, "sort_by_star_level", context.getString(R.string.sort_by_star_level), context.getString(R.string.sort_by_star_level2));
    }

    void addSortByScoreCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_SORT_BY_SCORE, "sort_by_score", context.getString(R.string.sort_by_estimate), context.getString(R.string.sort_by_score), context.getString(R.string.highly_rated));
    }

    void addFilterByStarLevelCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_STAR_LEVEL, "filter_by_star_level", context.getResources().getStringArray(R.array.star_level));
    }

    void addFilterByMinPriceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_PRICE_MIN, "filter_by_price_min", context.getString(R.string.filter_price_min));
    }

    void addFilterByMaxPriceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_PRICE_MAX, "filter_by_price_max", context.getString(R.string.filter_price_max));
    }

    void addFilterByMaxScoreCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_SCORE_MAX, "filter_by_score_max", context.getString(R.string.filter_score_max), context.getString(R.string.filter_score_max2));
    }

    void addFilterByMinDistanceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MIN, "filter_by_distance_min", context.getString(R.string.filter_distance_min));
    }

    void addFilterByMaxDistanceCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MAX, "filter_by_distance_max", context.getString(R.string.filter_distance_max));
    }

    private void addPageCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, String field, int size) {
        int pageSize = MultiPageView.getTotalPage(size);
        for (int i = 0; i < pageSize; i++) {
            int id = AssistantWordHandler.RECOGNIZE_RESULT_INDEX_START + i;
            String number = AssistantUtils.numberToChinese(i + 1);
            String text = ("第" + number + "页");
            String text2 = ("第" + number + "夜");
            stksCmdNliScenes.add(getCmd(id, field, text, text2));
        }
    }

    void addFilterByHeadNumberCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search, List<ContactBean> contactList) {
        List<String> headNumList = new ArrayList<>();
        List<String> orderList = new ArrayList<>();
        for (ContactBean contactBean : contactList) {
            if (contactBean == null || TextUtils.isEmpty(contactBean.getPhoneNum()) || contactBean.getPhoneNum().length() < 3) {
                continue;
            }
            String headNum = contactBean.getPhoneNum().substring(0, 3);
            if (!headNumList.contains(headNum)) {
                headNumList.add(headNum);
                orderList.add(StringUtil.format(context.getString(R.string.head_number), headNum));
            }
        }
        String[] array = orderList.toArray(new String[0]);
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_FILTER_BY_HEAD_NUMBER, "filter_by_head_number", array);
    }

    void addConfirmCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search, ConfirmWord... words) {
        List<String> confirmWordList = new ArrayList<>();
        List<String> cancelWordList = new ArrayList<>();
        try {
            String text = AssetUtils.getTextFromAsset(InterceptorManager.getInstance().getContext(), "config/ConfirmWord.json");
            JSONObject jsonObject = new JSONObject(text);
            String options = jsonObject.getString("options");
            List<ConfirmWord> wordList = GsonHelper.fromJson(options, new TypeToken<List<ConfirmWord>>() {
            }.getType());
            wordList.addAll(Arrays.asList(words));
            for (ConfirmWord word : wordList) {
                if (word.isConfirm()) {
                    confirmWordList.add(word.getKeyWord());
                } else {
                    cancelWordList.add(word.getKeyWord());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_CONFIRM_OPERATION, "confirm_operation", confirmWordList.toArray(new String[0]));
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_CANCEL_OPERATION, "cancel_operation", cancelWordList.toArray(new String[0]));
    }

    void addCorrectCmd(ArrayList<StksCmdNliScene> stksCmdNliScenes, ArrayList<String> search) {
        addCmd(stksCmdNliScenes, search, AssistantWordHandler.RECOGNIZE_RESULT_CORRECT, "correct", context.getString(R.string.correct), context.getString(R.string.error_recovery));
    }

    protected String getSrSceneStksCmd() {
        return "";
    }

    public void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }

    /**
     * 听歌识曲
     */
    public void recognizeSong() {
        Log.d("QBX", "recognizeSong: ");
        setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC);
        speakContentWithoutCheckShowing(context.getString(R.string.wait_few_seconds), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                closeVoicePopup();
                LaunchUtils.launchAppOnlyNewTask(context, CenterConstants.LAUNCHER, "com.xiaoma.launcher.recmusic.ui.MusicRecDialogActivity", null);
            }
        });
    }

    /**
     * 打开日历
     */
    void openCalendar(String date) {
        speakContent(context.getString(R.string.result_ok), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                closeVoicePopup();
                Bundle bundle = new Bundle();
                bundle.putString(CenterConstants.DATE, date);
                LaunchUtils.launchAppOnlyNewTask(context, CenterConstants.LAUNCHER, "com.xiaoma.launcher.main.ui.CalendarActivity", bundle);
            }
        });
    }

    /**
     * 打开语音训练
     *
     * @param isOpen
     */
    void openVrPractice(boolean isOpen) {
        if (isOpen) {
            setRobAction(AssistantConstants.RobActionKey.HANG_UP);
            speakContent(context.getString(R.string.result_ok), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    NodeUtils.jumpTo(context, CenterConstants.LAUNCHER, "com.xiaoma.vrpractice.ui.MainActivity", NodeConst.LAUNCHER.VOICE_TRAINING);
                    closeVoicePopup();
                }
            });
        } else {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Constants.CLOSE_APP + "VR_PRACTICE");
            context.sendBroadcast(intent);
            assistantManager.speakThenClose(okCloseAnswers[new Random().nextInt(3)]);
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
        }

    }

    void openRadioStationCollectionList() {
        Log.d("QBX", "openRadioStationCollectionList: ");
        setRobAction(AssistantConstants.RobActionKey.COLLECTION);
        if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.XTING)) {
            NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.MainActivity",
                    NodeConst.Xting.ACT_MAIN
                            + "/" + NodeConst.Xting.FGT_HOME
                            + "/" + NodeConst.Xting.FGT_MY
                            + "/" + NodeConst.Xting.FGT_MY_COLLECT);
        } else {
            closeAfterSpeak(R.string.please_install_xting_first);
        }
    }

    public void setSkillData(ExecResult execResult, SkillCallback skillCallback) {
        executeSkill = true;
        this.execResult = execResult;
        this.skillCallback = skillCallback;
    }

    public void executeSkillSucceeded() {
        if (!executeSkill) return;
        executeSkill = false;
        execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
        skillCallback.onExec(execResult);
    }

    /**
     * 随机动作
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
    }
}
