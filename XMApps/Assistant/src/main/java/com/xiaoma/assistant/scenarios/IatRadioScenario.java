package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.api.RadioApiManager;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.RadioSlots;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.RadioListAdapter;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：在线电台场景
 */
public class IatRadioScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private RadioListAdapter adapter;
    private static final String RADIO_STATION = "广播电台";

    public IatRadioScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        String slots = parseResult.getSlots();
        if (TextUtils.isEmpty(slots) || slots.equals("{}")) {
            if ("COLLECT".equals(parseResult.getOperation())) {
                // 打开收藏列表
                openRadioStationCollectionList();
                return;
            } else {
                speakUnderstand();
                return;
            }
        }
        RadioSlots slotsObject = GsonHelper.fromJson(slots, RadioSlots.class);
        if (slotsObject != null) {
            if (StringUtil.isNotEmpty(slotsObject.insType)) {
                handleInsType(slotsObject.insType);
            } else if (StringUtil.isNotEmpty(slotsObject.category)) {
                switch (slotsObject.category) {
                    case "在线":
                    case "网络":
                        IatProgramScenario iatProgramScenario = ScenarioDispatcher.getInstance().getIatProgramScenario(context);
                        AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatProgramScenario);
                        iatProgramScenario.playProgram();
                        break;
                    case "本地":
                        RadioApiManager.getInstance().playLocalRadioStation();
                        break;
                    default:
                        RadioApiManager.getInstance().playRadioStationByType(slotsObject.category);
                        break;
                }
            } else if (StringUtil.isNotEmpty(slotsObject.name)) {
                setRobAction(AssistantConstants.RobActionKey.SWITCH_RADIO_STATION);
                RadioApiManager.getInstance().playRadioStationByName(slotsObject.name);
            } else if (StringUtil.isNotEmpty(slotsObject.mediaSource) && "收藏".equals(slotsObject.mediaSource)) {
                //播放我的收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                RadioApiManager.getInstance().playRadioStationCollection();
            } else if (StringUtil.isNotEmpty(slotsObject.code)) {
                double freq = Double.valueOf(slotsObject.code);
                if (TextUtils.isEmpty(slotsObject.waveband) || slotsObject.waveband.equalsIgnoreCase("fm")) {
                    freq *= 1000;
                }
                RadioApiManager.getInstance().playRadioStationByFrequency("fm", (int) freq);
            } else if (slotsObject.location != null) {
                if (!TextUtils.isEmpty(slotsObject.location.provinceAddr)) {
                    String searchRadioStation = slotsObject.location.provinceAddr + RADIO_STATION;
                    RadioApiManager.getInstance().playRadioStationByName(searchRadioStation);
                } else {
                    speakUnderstand();
                }
            }
        } else {
            speakUnderstand();
        }
    }

    private void handleInsType(String insType) {
        switch (insType) {
            case "COLLECT":
                // 收藏电台
                setRobAction(AssistantConstants.RobActionKey.COLLECTION_PROGRAM);
                RadioApiManager.getInstance().collectRadioStation();
                break;
            case "CANCEL_COLLECT":
                // 取消收藏电台
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                RadioApiManager.getInstance().cancelCollectRadioStation();
                break;
            case "PAST":
                // 上一台
                setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
                RadioApiManager.getInstance().preRadioStation();
                break;
            case "DISLIKE":
                setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
                speakContent(context.getString(R.string.switch_radio_station), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                        RadioApiManager.getInstance().nextRadioStation(context.getString(R.string.switch_radio_station));
                    }
                });
                break;
            case "NEXT":
                // 下一台
                setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
                if (parseResult.getText() != null && parseResult.getText().contains("不想听这个电台")) {
                    speakContent(context.getString(R.string.switch_radio_station), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            AssistantManager.getInstance().closeAssistant();
                            RadioApiManager.getInstance().nextRadioStation(context.getString(R.string.switch_radio_station));
                        }
                    });
                } else {
                    RadioApiManager.getInstance().nextRadioStation("");
                }
                break;
            case "CLOSE":
                //不想听广播了
                setRobAction(36);
                RadioApiManager.getInstance().closeRadioStation(context.getString(R.string.close_this_song));
                break;
            case "PAUSE":
                // 暂停播放
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
                RadioApiManager.getInstance().pauseRadioStation("");
                break;
            case "PLAY":
                // 继续播放
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
                RadioApiManager.getInstance().playLocalRadioStation();
                break;
           /* case "OPEN_LIST":
                //TODO 打开播放列表

                break;
            case "CLOSE_LIST":
                //TODO 关闭播放列表

                break;*/
            case "OPEN":
                // 打开电台
                RadioApiManager.getInstance().playFM();
                break;
            case "SEARCH":
                // 跳转电台频道搜索界面
                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
                if (OpenAppUtils.openApp(context, CenterConstants.XTING)) {
                    NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.MainActivity",
                            NodeConst.Xting.ACT_MAIN + "/" + NodeConst.Xting.FGT_HOME + "/" + NodeConst.Xting.FGT_LOCAL + "/" + NodeConst.Xting.FGT_LOCAL_SEARCH);
                } else {
                    closeAfterSpeak(getString(R.string.please_install_xting_first));
                }

//                RadioApiManager.getInstance().scanRadioStation();
                return;
            case "BROADCAST_RADIO":
            case "SEARCH_BROADCAST":
                // 获取当前播放电台的信息：频率、电台名称、节目名称
                RadioApiManager.getInstance().getRadioStationInfo();
                break;
            case "BROADCAST_SINGER":
                // 这首歌谁唱的 听歌识曲
                recognizeSong();
                return;
            case "BROADCAST_SONG":
                // 这是什么歌 听歌识曲
                recognizeSong();
                return;
        }
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
                KLog.d("open radio action");
                closeVoicePopup();
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
        List<Album> radios = adapter.getCurrentList();
        if (ListUtils.isEmpty(radios)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("radio");
        stksCmd.setNliScene("radio");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.album");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = radios.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            if (!TextUtils.isEmpty(radios.get(i).getAlbumTitle())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("album");
                String albumName = radios.get(i).getAlbumTitle().replace("^_^", "");
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(context, "播放电台", Toast.LENGTH_SHORT).show();
        closeVoicePopup();
    }
}
