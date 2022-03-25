package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.api.AudioManager;
import com.xiaoma.assistant.manager.api.MusicApiManager;
import com.xiaoma.assistant.model.MusicDataBean;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.SimpleMusicInfo;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.MusicListAdapter;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：音乐场景
 */
public class IatMusicScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {
    private SimpleMusicInfo info;
    private MusicListAdapter adapter;

    public IatMusicScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        if ("RANDOM_SEARCH".equals(parseResult.getOperation())) {
            MusicApiManager.getInstance().playLastAudioSource();
            return;
        }
        if ("close".equalsIgnoreCase(parseResult.getOperation())) {
            closeMusic();
            return;
        }
        if (TextUtils.isEmpty(parseResult.getSlots())) {
            speakUnderstand();
            return;
        }
        String slotString = parseResult.getSlots().replaceAll("exclude#artist", "exclude_artist").replaceAll("exclude#song", "exclude_song");
        if ("{}".equals(slotString)) {
            MusicApiManager.getInstance().playLastAudioSource();
            return;
        }
        info = SimpleMusicInfo.parseFromJson(slotString);
        if (info == null) {
            speakUnderstand();
            closeVoicePopup();
            return;
        }

        String name = "";
        String singer = "";
        if (!TextUtils.isEmpty(info.getArtist())) {
            singer = info.getArtist();
        }
        if (!TextUtils.isEmpty(info.getSong())) {
            String[] songs = info.getSong().split("\\|");
            if (songs != null && songs.length > 0) {
                name = songs[0];
                info.setSong(name);
            }

            if (handleSong(info.getSong())) {
                return;
            }
        }
        if (StringUtil.isNotEmpty(info.getInsType())) {
            handleInsType(info.getInsType());
        } else if (StringUtil.isNotEmpty(info.getInsType())) {
            handleCollection(parseResult.getOperation());
        } else if (StringUtil.isNotEmpty(info.getExclude_artist())) {
            if (!TextUtils.isEmpty(name)) {
                try {
                    //有没有其他人唱的
                    MusicDataBean dataBean = GsonHelper.fromJson(parseResult.getData(), MusicDataBean.class);
                    String artist = dataBean.getResult().get(0).getSingernames().get(0);
                    MusicApiManager.getInstance().searchMusicByNameAndSinger(name, artist);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 不想听info.getExclude_artist()的歌
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SET_HOME);
            MusicApiManager.getInstance().dislikeSomeonesSong(info.getExclude_artist());

        } else if (StringUtil.isNotEmpty(info.getExclude_song())) {
            if (!TextUtils.isEmpty(singer)) {
                try {
                    //换成他的其他歌曲
                    MusicDataBean dataBean = GsonHelper.fromJson(parseResult.getData(), MusicDataBean.class);
                    String songName = dataBean.getResult().get(0).getSongname();
                    MusicApiManager.getInstance().searchMusicByNameAndSinger(songName, singer);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 不想听info.getExclude_song()
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SET_HOME);
            MusicApiManager.getInstance().next(getString(R.string.close_this_song));

        } else if (!TextUtils.isEmpty(singer) && !TextUtils.isEmpty(info.getMoreArtist())) {
            MusicApiManager.getInstance().searchMusicBySingerAndChorus(singer, info.getMoreArtist(), name);
        } else if (!TextUtils.isEmpty(info.getSourceType()) && "专辑".equals(info.getSourceType())) {
            MusicApiManager.getInstance().searchMusicByAlbum(info.getSource(), singer);
        } else if (!TextUtils.isEmpty(info.getSourceType()) && "榜单".equals(info.getSourceType())) {
            String type = "";
            if (!TextUtils.isEmpty(info.getLang())) {
                type = info.getLang();
            } else if (!TextUtils.isEmpty(info.getTags())) {
                type = info.getTags();
            } else if (!TextUtils.isEmpty(info.getGenre())) {
                type = info.getGenre();
            } else if (!TextUtils.isEmpty(info.getArea())) {
                type = info.getArea();
            } else if (!TextUtils.isEmpty(info.getSong())) {
                type = info.getSong();
            } else if (!TextUtils.isEmpty(info.getSource())) {
                type = info.getSource();
            }
            MusicApiManager.getInstance().searchMusicByRankingListType(type, "");
        } else if (!TextUtils.isEmpty(info.getTags())) {
            MusicApiManager.getInstance().searchOnlineMusicByMusicType(info.getTags(), singer);
        } else if (!TextUtils.isEmpty(info.getGenre())) {
            MusicApiManager.getInstance().searchOnlineMusicByMusicType(info.getGenre(), singer);
        } else if (!TextUtils.isEmpty(info.getLang()) && TextUtils.isEmpty(name)) {
            MusicApiManager.getInstance().searchOnlineMusicByMusicType(info.getLang(), singer);
        } else if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(singer) || !TextUtils.isEmpty(info.getSource())) {
            String searchName = !TextUtils.isEmpty(name) ? name : info.getSource();
            if (!TextUtils.isEmpty(info.getLang()) && info.getLang().equals("英语")) {
                searchName = "英文版" + searchName;
            }
            MusicApiManager.getInstance().searchMusicByNameAndSinger(searchName, singer);
        } else if (!TextUtils.isEmpty(info.getMediaSource())) {
            MusicApiManager.getInstance().playMusicBySource(info.getMediaSource());
        }
    }

    private void closeMusic() {
        if (AudioManager.getInstance().isMusicPlaying()){
            MusicApiManager.getInstance().pause();
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constants.CLOSE_APP + "com.xiaoma.music");
        context.sendBroadcast(intent);
        assistantManager.speakThenClose(okCloseAnswers[new Random().nextInt(3)]);
    }

    private boolean handleSong(String song) {
        switch (song) {
            case "暂停":
            case "停止":
                MusicApiManager.getInstance().pause();
                return true;
            default:
                break;
        }
        return false;
    }

    private void handleCollection(String operation) {
        switch (operation) {
           /* case "OPEN":
                closeVoicePopup();
                // 打开在线音乐的收藏列表

                break;*/
            case "PLAY":
                // 播放我的收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                MusicApiManager.getInstance().playCollectionList();
                break;
        }
    }

    private void handleInsType(String insType) {
        switch (insType) {
            case "COLLECT":
                // 收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION_PROGRAM);
                MusicApiManager.getInstance().collect();
                break;
            case "CANCEL_COLLECT":
//            case "DISLIKE":
                // 取消收藏
                setRobAction(AssistantConstants.RobActionKey.COLLECTION);
                MusicApiManager.getInstance().cancelCollect();
                break;
            case "DISLIKE":
                // 下一首
                setRobAction(getAction());
                speakContent(context.getString(R.string.change_song), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                        MusicApiManager.getInstance().next("");
                    }
                });

                break;
            case "NEXT":
                // 下一首
                setRobAction(getAction());
                MusicApiManager.getInstance().next("");
                break;
            case "PAST":
                // 上一首
                setRobAction(getAction());
                MusicApiManager.getInstance().past();
                break;
            case "PAUSE":
                // 暂停播放
                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                MusicApiManager.getInstance().pause();
                break;
            case "PLAY":
                // 继续播放
                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                MusicApiManager.getInstance().play();
                break;
            case "ORDER":
                // 顺序播放
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_ORDER);
                break;
            case "RANDOM":
                // 随机播放
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_RANDOM);
                break;
            case "CYCLE":
                // 单曲循环
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_SINGLE_CIRCLE);
                break;
            case "LOOP":
                // 循环播放
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_CIRCLE);
                break;
            case "OPEN_LYRIC":
                if (MusicApiManager.getInstance().isOnlineMusic()) {
                    setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                    if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.MUSIC)) {
                        NodeUtils.jumpTo(context, CenterConstants.MUSIC, "com.xiaoma.music.player.ui.PlayerActivity",
                                NodeConst.MUSIC.PLAYER_ACTIVITY
                                        + "/" + NodeConst.MUSIC.PLAYER_FRAGMENT
                                        + "/" + NodeConst.MUSIC.OPEN_PLAY_LYRIC);
                        closeAfterSpeak(getString(R.string.result_ok));
                    } else {
                        closeAfterSpeak(getString(R.string.please_install_music_app_first));
                    }
                } else {
                    closeAfterSpeak(context.getString(R.string.cant_support_local_music_Lyrics));
                }
                return;
            case "CLOSE_LYRIC"://关闭歌词
                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                closeAfterSpeak(getString(R.string.close_lyrics_speak));
                break;
            case "OPEN_LIST":
                // 打开播放列表
                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.MUSIC)) {
                    NodeUtils.jumpTo(context, CenterConstants.MUSIC, "com.xiaoma.music.player.ui.PlayerActivity",
                            NodeConst.MUSIC.PLAYER_ACTIVITY
                                    + "/" + NodeConst.MUSIC.PLAYER_FRAGMENT
                                    + "/" + NodeConst.MUSIC.OPEN_PLAY_LIST);
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(() -> AssistantManager.getInstance().closeAssistant(), 1000);
                } else {
                    closeAfterSpeak(getString(R.string.please_install_music_app_first));
                }
                return;
            case "CLOSE_LIST":
                // 关闭播放列表
//                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
//                if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.MUSIC, context.getString(R.string.please_install_music_app_first))) {
//                    NodeUtils.jumpTo(context, CenterConstants.MUSIC, "com.xiaoma.music.player.ui.PlayerActivity",
//                            NodeConst.MUSIC.PLAYER_ACTIVITY
//                                    + "/" + NodeConst.MUSIC.PLAYER_FRAGMENT
//                                    + "/" + NodeConst.MUSIC.CLOSE_PLAY_LIST);
//                }
//                closeVoicePopup();
                addFeedbackAndSpeak(context.getString(R.string.no_music_play_list));
                break;
            case "OPEN":
                MusicApiManager.getInstance().playLastAudioSource();
                break;
            case "BROADCAST_SINGER":
            case "BROADCAST_SONG":
                // 听歌识曲
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
        List<BaseQukuItem> radios = adapter.getCurrentList();
        if (ListUtils.isEmpty(radios)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("music");
        stksCmd.setNliScene("music");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.song");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        int size = radios.size();
        for (int i = 0; i < size; i++) {
            StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
            stksCmdNliScene.setId(i + 1);
            ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
            if (!TextUtils.isEmpty(radios.get(i).getName())) {
                StksCmdDimension stksCmdDimension = new StksCmdDimension();
                stksCmdDimension.setField("song");
                String albumName = radios.get(i).getName().replace("^_^", "");
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
        closeVoicePopup();
    }

}
