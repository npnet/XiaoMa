package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.RadioApiManager;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.XMAlbum;
import com.xiaoma.assistant.model.XMSearchAlbumList;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.ProgramSlots;
import com.xiaoma.assistant.ui.adapter.AudioAdapter;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.ProgramAdapter;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiuboxiang on 2019/2/22 14:46
 * Desc: 在线节目场景
 */
public class IatProgramScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private ProgramAdapter adapter;
    private AudioAdapter audioAdapter;
    private int pageNum = 1;
    private int mQueryCondition;
    private ProgramSlots slotsObject;

    public IatProgramScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        mQueryCondition = QueryCondition.NONE;
        this.parseResult = parseResult;
        String slots = parseResult.getSlots();
        if (TextUtils.isEmpty(slots) || slots.equals("{}")) {
            speakUnderstand();
            return;
        }
        slotsObject = GsonHelper.fromJson(slots, ProgramSlots.class);
        if (slotsObject != null) {

            //节目搜索
            String data = "";
            String programName = "";
            if (StringUtil.isNotEmpty(slotsObject.presenter)) {
                mQueryCondition = QueryCondition.PRESENTER;
                data = slotsObject.presenter;
            }
            if (StringUtil.isNotEmpty(slotsObject.famous)) {
                mQueryCondition = QueryCondition.FAMOUS;
                data = slotsObject.famous;
                slotsObject.presenter = slotsObject.famous;
            }
            if (StringUtil.isNotEmpty(slotsObject.program)) {
                mQueryCondition = isPresenterOrFamousCondition() ? QueryCondition.PRESENTER_AND_NAME : QueryCondition.NAME;
                data += slotsObject.program;
                programName = slotsObject.program;
            }
            if (StringUtil.isNotEmpty(slotsObject.tags)) {
                mQueryCondition = isPresenterOrFamousCondition() ? QueryCondition.PRESENTER_AND_TYPE : QueryCondition.TYPE;
                data += slotsObject.tags;
            }
            if (slotsObject.chapter != null) {
                if (TextUtils.isEmpty(slotsObject.program)) {
                    speakContent(context.getString(R.string.please_point_out_program_name));
                    return;
                }
                mQueryCondition = QueryCondition.NAME_AND_INDEX;
                ProgramSlots.ChapterBean chapter = slotsObject.chapter;
                if ("MAX".equals(chapter.ref)) {
                    slotsObject.index = "最新一集";
                } else if ("ZERO".equals(chapter.ref)) {
                    slotsObject.index = "第" + chapter.offset + "集";
                }
                data += slotsObject.index;
            }
            data = dealRadio(data);

            if (StringUtil.isNotEmpty(slotsObject.insType)) {
                handleInsType(slotsObject.insType, parseResult.getText());

            } else if (StringUtil.isNotEmpty(slotsObject.mediaSource) && "收藏".equals(slotsObject.mediaSource)) {
                handleCollection(parseResult.getOperation());

            } else {
               /* if (parseResult.getOperation().equals("QUERY")) {

                } else if (parseResult.getOperation().equals("PLAY")) {

                }*/
                handleRadio(data, programName);
            }
        } else {
            speakUnderstand();
        }
    }

    interface QueryCondition {
        int NONE = 0;
        int NAME = 1;
        int PRESENTER = 2;
        int TYPE = 3;
        int PRESENTER_AND_TYPE = 4;
        int PRESENTER_AND_NAME = 5;
        int NAME_AND_INDEX = 6;
        int FAMOUS = 7;
    }

    private boolean isPresenterOrFamousCondition() {
        return mQueryCondition == QueryCondition.PRESENTER || mQueryCondition == QueryCondition.FAMOUS;
    }

    private String getCanNotFindTTSContent() {
        switch (mQueryCondition) {
            case QueryCondition.PRESENTER:
                return context.getString(R.string.can_not_find_program_with_presenter_condition);
            case QueryCondition.FAMOUS:
                return context.getString(R.string.can_not_find_program_with_famous_condition);
            case QueryCondition.TYPE:
                return context.getString(R.string.can_not_find_program_with_type_condition);
            case QueryCondition.PRESENTER_AND_NAME:
                return StringUtil.format(context.getString(R.string.can_not_find_program_with_multi_condition), slotsObject.presenter, slotsObject.program);
            case QueryCondition.PRESENTER_AND_TYPE:
                return StringUtil.format(context.getString(R.string.can_not_find_program_with_multi_condition), slotsObject.presenter, slotsObject.tags);
            default:
                return context.getString(R.string.can_not_find_program);
        }
    }

    private String getFindOneResultTTSContent(String name) {
        switch (mQueryCondition) {
            case QueryCondition.PRESENTER:
            case QueryCondition.FAMOUS:
                return StringUtil.format(context.getString(R.string.play_program_with_presenter_condition), slotsObject.presenter);
            case QueryCondition.TYPE:
                return StringUtil.format(context.getString(R.string.play_program_with_type_condition), slotsObject.tags);
            case QueryCondition.PRESENTER_AND_NAME:
                return StringUtil.format(context.getString(R.string.play_program_with_multi_condition), slotsObject.presenter, name);
            case QueryCondition.PRESENTER_AND_TYPE:
                return StringUtil.format(context.getString(R.string.play_program_with_multi_condition), slotsObject.presenter, slotsObject.tags);
            case QueryCondition.NAME_AND_INDEX:
                return StringUtil.format(context.getString(R.string.play_program_with_term_condition), name, slotsObject.index);
            default:
                return StringUtil.format(context.getString(R.string.play_program), name);
        }
    }

    private String getFindMultiResultTTSContent(String name, int size) {
        switch (mQueryCondition) {
            case QueryCondition.PRESENTER:
            case QueryCondition.FAMOUS:
                return StringUtil.format(context.getString(R.string.find_program_with_presenter_condition), slotsObject.presenter, size);
            case QueryCondition.TYPE:
                return StringUtil.format(context.getString(R.string.find_program), size);
            case QueryCondition.PRESENTER_AND_NAME:
                return StringUtil.format(context.getString(R.string.find_program_with_multi_condition), size, slotsObject.presenter, name);
            case QueryCondition.PRESENTER_AND_TYPE:
                return StringUtil.format(context.getString(R.string.find_program_with_multi_condition), size, slotsObject.presenter, slotsObject.tags);
            case QueryCondition.NAME_AND_INDEX:
                return StringUtil.format(context.getString(R.string.find_program_result), size);
            default:
                return StringUtil.format(context.getString(R.string.find_program), size);
        }
    }

    private void handleCollection(String operation) {
        switch (operation) {
            case "OPEN":
                // 打开收藏列表
                openRadioStationCollectionList();
                break;
            case "PLAY":
                // 播放我的收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                RadioApiManager.getInstance().playProgramCollection();
                break;
        }
    }

    private void handleInsType(String insType, String text) {
        switch (insType) {
            case "COLLECT":
                // 收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION_PROGRAM);
                RadioApiManager.getInstance().collectProgram();
                break;
            case "CANCEL_COLLECT":
                // 取消收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                RadioApiManager.getInstance().cancelCollectProgram();
                break;
            case "PAST":
                // 上一个
                setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
                RadioApiManager.getInstance().preProgram();
                break;
            case "DISLIKE":
            case "NEXT":
                // 下一个
                setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
                RadioApiManager.getInstance().nextProgram();
                break;
            case "PAUSE":
                // 暂停播放
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
                RadioApiManager.getInstance().pauseProgram("");
                break;
            case "PLAY":
                if (text.contains("播放")) {
                    // 继续播放
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
                    RadioApiManager.getInstance().continuePlayProgram();
                } else {
                    // 收听节目
                    playProgram();
                }
                break;
          /*  case "OPEN_LIST":
                // 打开播放列表

                break;
            case "CLOSE_LIST":
                // 关闭播放列表

                break;*/
            case "ORDER":
                // 顺序播放
                RadioApiManager.getInstance().switchPlayModeToOrder();
                break;
            case "RANDOM":
                // 随机播放
                RadioApiManager.getInstance().switchPlayModeToRandom();
                break;
            case "CYCLE":
                // 单曲循环
                RadioApiManager.getInstance().switchPlayModeToCycle();
                break;
            case "LOOP":
                // 循环播放
                RadioApiManager.getInstance().switchPlayModeToLoop();
                break;
        }
    }

    private String dealRadio(String text) {
        text = text.replaceFirst(getString(R.string.xfparser_key_luojisiwei), getString(R.string.xfparser_key_luojisiwei1));
        text = text.replaceFirst(getString(R.string.xfparser_key_xiaosongqitan), getString(R.string.xfparser_key_xiaosongqitan1));
        if (needDealRadio(text)) {
            String word = text.replaceFirst(getString(R.string.xfparser_key_xting_p1), "");
            word = word.replaceFirst(getString(R.string.xfparser_key_xting_p2), "");
            word = word.replaceFirst(getString(R.string.xfparser_key_xting_p3), "");
            word = word.replaceFirst(getString(R.string.xfparser_key_xting_p4), "");
            return word;
        }
        return text;
    }

    private boolean needDealRadio(String text) {
        return text.startsWith(getString(R.string.xfparser_key_xting_p1)) || text.startsWith(getString(R.string.xfparser_key_xting_p2))
                || text.startsWith(getString(R.string.xfparser_key_xting_p3)) || text.startsWith(getString(R.string.xfparser_key_xting_p4));
    }

    private void handleRadio(String data, final String programName) {

        if (data == null || data.isEmpty()) {
            playProgram();
            closeVoicePopup();
        } else {
            setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
            Map<String, String> map = new HashMap<>();
            String searchContent = wrapSearchContent(data);
            map.put(DTransferConstants.SEARCH_KEY, searchContent);
            map.put(DTransferConstants.PAGE, String.valueOf(pageNum));
            map.put(DTransferConstants.PAGE_SIZE, String.valueOf(BaseMultiPageAdapter.PAGE_SIZE));
            CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
                @Override
                public void onSuccess(SearchAlbumList searchAlbumList) {
                    KLog.d("XMLY data:" + searchAlbumList);
                    if (searchAlbumList != null && !ListUtils.isEmpty(searchAlbumList.getAlbums())) {
                        XMSearchAlbumList xmSearchAlbumList = new XMSearchAlbumList(searchAlbumList);
                        List<XMAlbum> list = xmSearchAlbumList.getAlbums();
                        if (ListUtils.isEmpty(list)) {
                            addFeedbackAndSpeak(getCanNotFindTTSContent(), new WrapperSynthesizerListener() {
                                @Override
                                public void onCompleted() {
                                    startListening();
                                }
                            });
                        } else if (list.size() == 1) {
                            XMAlbum album = list.get(0);
                            RadioApiManager.getInstance().playAlbum(album.getId(), getFindOneResultTTSContent(programName));
                        } else {
                            audioAdapter = null;
                            adapter = new ProgramAdapter(context, list);
                            adapter.setOnMultiPageItemClickListener(IatProgramScenario.this);
                            showMultiPageData(adapter, getFindMultiResultTTSContent(programName, list.size()));
                        }
                    } else {
                        addFeedbackAndSpeak(getCanNotFindTTSContent(), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                startListening();
                            }
                        });
                    }
                }

                @Override
                public void onError(int i, String s) {
                    speakUnderstand();
                }
            });
        }

    }

    private String wrapSearchContent(String info) {
        if (TextUtils.isEmpty(info)) {
            return "";
        }
        if (context == null) {
            return info;
        }
        String[] endRadios = context.getResources().getStringArray(R.array.end_radio);
        if (endRadios == null || endRadios.length <= 0) {
            return info;
        }
        for (int i = 0; i < endRadios.length; i++) {
            boolean b = info.endsWith(endRadios[i]);
            if (b) {
                info = info.replace(endRadios[i], "");
                break;
            }
        }
        return info;
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
        return true;
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
        List<XMAlbum> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("program");
        stksCmd.setNliScene("program");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.album");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            if (!TextUtils.isEmpty(list.get(i).getAlbumTitle())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("album");
                String albumName = replaceSpeaicalSign(list.get(i).getAlbumTitle());
                stksCmdDimension.setVal(albumName);
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

    private String replaceSpeaicalSign(String text) {
        return text.replace("^_^", "")
                .replace("【", "")
                .replace("】", "")
                .replace("[", "")
                .replace("]", "")
                .replace("《", "")
                .replace("》", "")
                .replace("：", "")
                .replace("·", "")
                .replace("\"", "")
                .replace("“", "")
                .replace("”", "")
                .replace("～", "")
                .replace("丨", "")
                .replace("+", "")
                .replace("-", "")
                .replace(" ", "")
                .replace("（", "")
                .replace("）", "");

    }

    @Override
    public void onItemClick(int position) {
        closeVoicePopup();
        if (adapter != null) {
            RadioApiManager.getInstance().playAlbum(adapter.getCurrentList().get(position).getId(), "");
        } else {
            RadioApiManager.getInstance().playProgramByID(audioAdapter.getCurrentList().get(position).getUniqueId(), position);
        }
    }

    public void playProgram() {
        RadioApiManager.getInstance().playProgram(playProgramCallback);
    }

    private IClientCallback playProgramCallback = new IClientCallback.Stub() {
        @Override
        public void callback(final Response response) throws RemoteException {
            UIUtils.runOnMain(new Runnable() {
                @Override
                public void run() {
                    Bundle extra = response.getExtra();
                    boolean isLastSource = extra.getBoolean(AudioConstants.BundleKey.AUDIO_DATA_SOURCE);
                    if (isLastSource) {
                        RadioApiManager.getInstance().closeAfterSpeak(context.getString(R.string.ok));
                    } else {
                        final List<AudioInfo> list = extra.getParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST);
                        if (ListUtils.isEmpty(list)) {
                            addFeedbackAndSpeak(context.getString(R.string.can_not_find_program));
                        } else if (list.size() == 1) {
                            AudioInfo info = list.get(0);
                            RadioApiManager.getInstance().closeAfterSpeak(StringUtil.format(context.getString(R.string.play_program), info.getTitle()));
                        } else {
                            adapter = null;
                            audioAdapter = new AudioAdapter(context, list);
                            audioAdapter.setOnMultiPageItemClickListener(IatProgramScenario.this);
                            showMultiPageData(audioAdapter, StringUtil.format(context.getString(R.string.find_program), list.size()));
                        }
                    }
                }
            });
        }
    };

}

