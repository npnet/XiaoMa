package com.xiaoma.assistant.manager.api;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.StringUtil;
import java.util.Random;


/**
 * Created by qiuboxiang on 2019/2/27 16:14
 * Desc:
 */
public class MusicApiManager extends ApiManager {

    private static final String TAG = "QBX 【" + MusicApiManager.class.getSimpleName() + "】";
    private static MusicApiManager mInstance;
    public static final int USB_PORT = AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
    public static final int KUWO_PORT = AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
    public static final int BLUETOOTH_PORT = AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
    //随机动作
    public static int[] mAction = new int[]{AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM1
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM2
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM3};

    private MusicApiManager() {
        // TODO: 2019/5/27 0027 待修复，暂时延迟处理
        /*ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMusicAudioType(new onGetIntResultListener() {
                    @Override
                    public void onSuccess(int result) {
                        AudioManager.getInstance().setAudioType(result);
                    }
                });
            }
        }, 3000);*/
    }

    public static MusicApiManager getInstance() {
        if (mInstance == null) {
            synchronized (MusicApiManager.class) {
                if (mInstance == null) {
                    mInstance = new MusicApiManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.MUSIC, CenterConstants.MUSIC_PORT);
    }

    @Override
    public void onClientIn(SourceInfo source) {
        int port = source.getPort();
        if (port != USB_PORT && port != KUWO_PORT && port != BLUETOOTH_PORT) {
            return;
        }
        AudioManager.getInstance().connectAudioClient(this, port);
    }

    public void getMusicAudioType(onGetIntResultListener listener) {
        request(AudioConstants.Action.GET_AUDIO_SOURCE_TYPE, null, getIntResultClientCallback(listener));
    }

    public void haveOnlineMusicPlayRecord(final onGetBooleanResultListener listener) {
        Log.d(TAG, "haveOnlineMusicPlayRecord: ");
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.HAVE_HISTORY);
        request(KUWO_PORT, AudioConstants.Action.SEARCH, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                boolean haveHistory = extra.getBoolean(AudioConstants.BundleKey.HAVE_HISTORY);
                if (haveHistory) {
                    listener.onTrue();
                } else {
                    listener.onFalse();
                }
            }
        });
    }

    private boolean isUsbMusic() {
        return AudioManager.getInstance().isUsbMusicType();
    }

    public boolean isOnlineMusic() {
        return AudioManager.getInstance().isKuwoMusicType();
    }

    private boolean noMusicSource() {
        /*int audioType = AudioManager.getInstance().getAudioType();
        boolean noMusicSource =
                audioType == AudioConstants.AudioTypes.NONE
                        || audioType == AudioConstants.AudioTypes.XTING_NET_FM
                        || audioType == AudioConstants.AudioTypes.XTING_LOCAL_FM
                        || audioType == AudioConstants.AudioTypes.XTING_NET_RADIO;
        if (noMusicSource) {
            addFeedbackAndSpeak(R.string.nonsupport_operation);
        }
        return noMusicSource;*/
        return false;
    }

    public void playLastAudioSource() {
        Log.d(TAG, "playLastAudioSource: ");
        setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC_SOURCE);
        getMusicAudioType(new onGetIntResultListener() {
            @Override
            public void onSuccess(int audioType) {
                Log.d("QBX", "getMusicAudioType:" + audioType);
                if (audioType == AudioConstants.AudioTypes.NONE || AudioManager.getInstance().isDeviceDisconnected()) {
                    searchMusicByRankingListType("酷我热歌榜", context.getString(R.string.play_kuwo_hot_song_list));
                } else {
                    String musicAudioType = getMusicAudioTypeString(audioType);
                    if (!TextUtils.isEmpty(musicAudioType)) {
                        play(getString(R.string.play_music_with_type, musicAudioType), audioType);
                    } else {
                        play("", audioType);
                    }
                }
            }
        });
    }

    private String getMusicAudioTypeString(int musicAudioType) {
        switch (musicAudioType) {
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                return context.getString(R.string.bluetooth);
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                return context.getString(R.string.usb);
            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
                return context.getString(R.string.online);
            default:
                return "";
        }
    }

    private void ttsFindAlbum(@Nullable final String album, @Nullable final String singer) {
        closeAfterSpeakAndContinueToPlay(TextUtils.isEmpty(album) ? StringUtil.format(context.getString(R.string.play_album_by_songer), singer) : StringUtil.format(context.getString(R.string.play_album), album));
    }

    public void searchMusicByAlbum(@Nullable final String album, @Nullable final String singer) {
        if (isUsbMusic()) {
            searchMusicByAlbum(AudioConstants.AudioTypes.MUSIC_LOCAL_USB, album, singer, new onGetBooleanResultListener() {
                @Override
                public void onTrue() {
                    ttsFindAlbum(album, singer);
                }

                @Override
                public void onFalse() {
                    addFeedbackAndSpeak(R.string.can_not_find_album);
                }
            });
        } else {
            searchOnlineMusicByAlbum(album, singer);
        }
    }

    private void searchOnlineMusicByAlbum(@Nullable final String album, @Nullable final String singer) {
        setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC);
        if (!checkNet()) return;
        searchMusicByAlbum(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, album, singer, new onGetBooleanResultListener() {
            @Override
            public void onTrue() {
                ttsFindAlbum(album, singer);
            }

            @Override
            public void onFalse() {
                addFeedbackAndSpeak(R.string.can_not_find_album);
            }
        });
    }

    private void searchMusicByAlbum(int audioSourceType, @Nullable String album, @Nullable String singer, final onGetBooleanResultListener listener) {
        Log.d(TAG, "searchMusicByAlbum: ");
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.AUDIO_TYPE, audioSourceType);
        bundle.putString(AudioConstants.BundleKey.ALBUM, TextUtils.isEmpty(album) ? "" : album);
        bundle.putString(AudioConstants.BundleKey.SINGER, TextUtils.isEmpty(singer) ? "" : singer);
        request(AudioConstants.Action.SEARCH_MUSIC_BY_ALBUM, bundle, getBooleanResultClientCallback(listener));
    }

    public void searchOnlineMusicByMusicType(@NonNull final String musicType, @Nullable String singer) {
        Log.d(TAG, "searchOnlineMusicByMusicType: ");
        setRobAction(getAction());
        Bundle bundle = new Bundle();
        bundle.putString(AudioConstants.BundleKey.MUSIC_TYPE, musicType);
        bundle.putString(AudioConstants.BundleKey.SINGER, TextUtils.isEmpty(singer) ? "" : singer);
        requestWithCheckNet(AudioConstants.Action.SEARCH_MUSIC_BY_MUSIC_TYPE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {
                    closeAfterSpeakAndContinueToPlay(getString(R.string.play_song_type, musicType));
                } else {
                    addFeedbackAndSpeak(R.string.can_not_find_song_type);
                }
            }
        });
    }

    public void searchMusicByNameAndSinger(@Nullable final String name, @Nullable final String singer) {
        if (!TextUtils.isEmpty(singer)) {
            setRobAction(getAction());
        } else {
            setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC);
        }
        if (isUsbMusic()) {
            searchMusicByNameAndSinger(AudioConstants.AudioTypes.MUSIC_LOCAL_USB, name, singer, new onGetBooleanResultListener() {
                @Override
                public void onTrue() {
                    ttsFindMusicAndSinger(name, singer, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                }

                @Override
                public void onFalse() {
                    ttsCanNotFindMusicAndSinger(singer);
                }
            });
        } else {
            searchOnlineMusicByNameAndSinger(name, singer);
        }
    }

    private void searchOnlineMusicByNameAndSinger(@Nullable final String name, @Nullable final String singer) {
        if (!checkNet()) return;
        searchMusicByNameAndSinger(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, name, singer, new onGetBooleanResultListener() {
            @Override
            public void onTrue() {
                ttsFindMusicAndSinger(name, singer, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
            }

            @Override
            public void onFalse() {
                ttsCanNotFindMusicAndSinger(singer);
            }
        });
    }

    private void ttsCanNotFindMusicAndSinger(String singer) {
        if (!TextUtils.isEmpty(singer)) {
            addFeedbackAndSpeak(R.string.can_not_find_singer);
        } else {
            addFeedbackAndSpeak(R.string.can_not_find_song);
        }
    }

    private void ttsFindMusicAndSinger(String name, String singer, int audioType) {
        String content;
        if (TextUtils.isEmpty(name)) {
            content = getString(R.string.play_song_by_singer, singer);
        } else if (TextUtils.isEmpty(singer)) {
            content = getString(R.string.play_song_by_name, name);
        } else {
            content = getString(R.string.play_song_by_name_and_singer, singer, name);
        }
        closeAfterSpeakAndContinueToPlay(content, audioType);
        //jumpToMusic();
    }

    public void dislikeSomeonesSong(String singerName) {
        Log.d("QBX", "dislikeSomeonesSong: ");
        speakContent(context.getString(R.string.close_this_song), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                closeAssistant();
                if (isUsbMusic()) {
                    dislikeSomeonesSong(singerName, AudioConstants.AudioTypes.MUSIC_LOCAL_USB, new SimpleGetBooleanResultListener() {
                        @Override
                        public void onFalse() {
                            pause();
                        }
                    });
                } else {
                    dislikeSomeonesSongBySeachOnline(singerName);
                }
            }
        });
    }

    private void dislikeSomeonesSongBySeachOnline(String singerName) {
        dislikeSomeonesSong(singerName, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, new SimpleGetBooleanResultListener() {
            @Override
            public void onFalse() {
                pause();
            }
        });
    }

    private void dislikeSomeonesSong(String singerName, int audioSourceType, onGetBooleanResultListener listener) {
        AssistantManager.getInstance().closeAssistant();
        if (listener == null) {
            listener = new SimpleGetBooleanResultListener();
        }
        searchMusicByNameAndSinger(false, audioSourceType, "", singerName, listener);
    }

    private void searchMusicByNameAndSinger(int audioSourceType, @Nullable String name, @Nullable String singer, final onGetBooleanResultListener listener) {
        searchMusicByNameAndSinger(true, audioSourceType, name, singer, listener);
    }

    private void searchMusicByNameAndSinger(boolean like, int audioSourceType, @Nullable String name, @Nullable String singer, final onGetBooleanResultListener listener) {
        Log.d(TAG, "searchMusicByNameAndSinger: like=" + like);
        Bundle bundle = new Bundle();
        bundle.putBoolean(AudioConstants.BundleKey.WANT, like);
        bundle.putInt(AudioConstants.BundleKey.AUDIO_TYPE, audioSourceType);
        bundle.putString(AudioConstants.BundleKey.SONG, TextUtils.isEmpty(name) ? "" : name);
        bundle.putString(AudioConstants.BundleKey.SINGER, TextUtils.isEmpty(singer) ? "" : singer);
        request(AudioConstants.Action.SEARCH_MUSIC_BY_NAME_AND_SINGER, bundle, getBooleanResultClientCallback(listener));
    }

    public void searchMusicBySingerAndChorus(final String singer, final String chorus, @Nullable final String name) {
        setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC);
        if (isUsbMusic()) {
            searchMusicBySingerAndChorus(AudioConstants.AudioTypes.MUSIC_LOCAL_USB, singer, chorus, name, new onGetStringResultListener() {
                @Override
                public void onSuccess(String result) {
                    ttsFindSingerAndChorus(result);
                }

                @Override
                public void onFailed() {
                    addFeedbackAndSpeak(R.string.can_not_find_song);
                }
            });
        } else {
            searchOnlineMusicBySingerAndChorus(singer, chorus, name);
        }
    }

    private void ttsFindSingerAndChorus(String result) {
        closeAfterSpeakAndContinueToPlay(getString(R.string.play_song_by_name, result));
    }

    private void searchOnlineMusicBySingerAndChorus(String singer, String chorus, @Nullable String name) {
        if (!checkNet()) return;
        searchMusicBySingerAndChorus(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, singer, chorus, name, new onGetStringResultListener() {
            @Override
            public void onSuccess(String result) {
                ttsFindSingerAndChorus(result);
            }

            @Override
            public void onFailed() {
                addFeedbackAndSpeak(R.string.can_not_find_song);
            }
        });
    }

    private void searchMusicBySingerAndChorus(int audioSourceType, String singer, String chorus, @Nullable String name, final onGetStringResultListener listener) {
        Log.d(TAG, "searchMusicBySingerAndChorus: ");
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.AUDIO_TYPE, audioSourceType);
        bundle.putString(AudioConstants.BundleKey.SINGER, singer);
        bundle.putString(AudioConstants.BundleKey.CHORUS, chorus);
        bundle.putString(AudioConstants.BundleKey.SONG, TextUtils.isEmpty(name) ? "" : name);
        request(AudioConstants.Action.SEARCH_MUSIC_BY_SINGER_AND_CHORUS, bundle, getStringResultClientCallback(listener));
    }

    public void searchMusicByRankingListType(final String musicListType, final String ttsContent) {
        Log.d(TAG, "searchMusicByRankingListType: ");
        Bundle bundle = new Bundle();
        bundle.putString(AudioConstants.BundleKey.RANKING_LIST_TYPE, TextUtils.isEmpty(musicListType) ? "" : musicListType);
        requestWithCheckNet(AudioConstants.Action.SEARCH_MUSIC_BY_RANKING_LIST_TYPE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {
                    if (!TextUtils.isEmpty(ttsContent)) {
                        closeAfterSpeakAndContinueToPlay(ttsContent);
                    } else {
                        musicListType.replaceAll("榜", "");
                        closeAfterSpeakAndContinueToPlay(getString(R.string.play_ranking_list_type, musicListType));
                    }
                } else {
                    addFeedbackAndSpeak(R.string.can_not_find_song_type);
                }
            }
        });
    }

    public void playMusicBySource(String audioSourceType) {
        Log.d(TAG, "playMusicBySource: ");
        int audioType;
        if ("蓝牙".equals(audioSourceType)) {
            final OpenAppUtils.PendingHandle handleBluetooth = OpenAppUtils.playBlutoothMusic();
            if ((handleBluetooth == null)) {
                addFeedbackAndSpeakMultiTone(context.getString(R.string.no_bluetooth), context.getString(R.string.no_bluetooth_speak), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                    }
                });
                return;
            } else {
                speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                        handleBluetooth.jumpBlutoothMusic(context);
                        play(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                    }
                });
            }
            return;
        } else if ("USB".equalsIgnoreCase(audioSourceType) || "u盘".equalsIgnoreCase(audioSourceType)) {
            final OpenAppUtils.PendingHandle handleUsb = OpenAppUtils.playUsbMusic();
            if ((handleUsb == null)) {
                addFeedbackAndSpeakMultiTone(context.getString(R.string.no_usb), context.getString(R.string.no_usb_speak), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                    }
                });
                return;
            } else {
                speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        AssistantManager.getInstance().closeAssistant();
                        handleUsb.jumpUsbMusic(context);
                        play(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    }
                });
            }
            return;
        } else if ("网络".equals(audioSourceType)) {
            speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    NodeUtils.jumpTo(context, CenterConstants.MUSIC,
                            "com.xiaoma.music.MainActivity",
                            NodeConst.MUSIC.MAIN_ACTIVITY
                                    + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                                    + "/" + NodeConst.MUSIC.ONLINE_FRAGMENT);
                    AssistantManager.getInstance().closeAssistant();
                    play(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
            });
            return;
        } else if ("收藏".equals(audioSourceType)) {
            MusicApiManager.getInstance().playCollectionList();
            return;
        } else {
            AssistantManager.getInstance().addFeedBackConversation(getString(R.string.no_device));
            AssistantManager.getInstance().speakContent(getString(R.string.no_device_speak));
            return;
        }
//        play(context.getString(R.string.all_right), audioType);
    }

    public void collect() {
        Log.d(TAG, "collect: ");
        if (isConnectFailed()) {
            closeAfterSpeak(R.string.connect_failed);
            return;
        }
        if (isOnlineMusic()) {
            request(KUWO_PORT, AudioConstants.Action.Option.COLLECT, null, new IClientCallback.Stub() {
                @Override
                public void callback(Response response) {
                    Bundle extra = response.getExtra();
                    boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                    if (success) {
                        closeAfterSpeak(R.string.collected);
                    } else {
                        AssistantManager.getInstance().closeAssistant();
                    }
                }
            });
        } else {
            addFeedbackAndSpeak(R.string.can_not_collect_local_music);
        }
    }

    public void cancelCollect() {
        Log.d(TAG, "cancelCollect: ");
        if (isOnlineMusic()) {
            request(KUWO_PORT, AudioConstants.Action.Option.CANCEL_COLLECT, null, new IClientCallback.Stub() {
                @Override
                public void callback(Response response) {
                    Bundle extra = response.getExtra();
                    boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                    if (success) {
                        closeAfterSpeak(R.string.canceled_collect);
                    } else {
                        AssistantManager.getInstance().closeAssistant();
                    }
                }
            });
        } else {
            AssistantManager.getInstance().closeAssistant();
        }
    }

    public void playCollectionList() {
        Log.d(TAG, "playCollectionList: ");
        Bundle bundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.PLAY_KW_FAVORITE_LIST);
        categoryBean.setCategoryId(0);
        bundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        requestWithCheckNet(KUWO_PORT, AudioConstants.Action.PLAY, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {
                    closeAfterSpeakAndContinueToPlay(context.getString(R.string.play_collection_list));
                } else {
                    speakContent(R.string.empty_collection_list);
                }
            }
        });
    }

    public void next(final String ttsContent) {
        Log.d(TAG, "next: ");
        setRobAction(getAction());
        if (noMusicSource() || isNetErrorAndIsKuwoAudioType() || AudioManager.getInstance().isDeviceDisconnected()) {
            return;
        }
        speakContent(ttsContent, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                closeAssistant();
                request(AudioManager.getInstance().getAudioType(), AudioConstants.Action.Option.NEXT, null, new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) {
                        Bundle extra = response.getExtra();
                        boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
//                closeDialogAndTTSAndContinueToPlay(success, ttsContent, AudioManager.getInstance().getAudioType());
                /*if (success) {
                    closeDialogAndTTS(true, ttsContent);
                } else {
                    closeAfterSpeak(R.string.it_is_the_last_one);
                }*/
                    }
                });
            }
        });
    }

    public void past() {
        Log.d(TAG, "past: ");
        setRobAction(getAction());
        if (noMusicSource() || isNetErrorAndIsKuwoAudioType() || AudioManager.getInstance().isDeviceDisconnected()) {
            return;
        }
        request(AudioManager.getInstance().getAudioType(), AudioConstants.Action.Option.PREVIOUS, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                closeAndContinueToPlay(AudioManager.getInstance().getAudioType());
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                /*if (success) {
                    AssistantManager.getInstance().closeAssistant();
                } else {
                    closeAfterSpeak(R.string.it_is_the_first_one);
                }*/
            }
        });
    }

    public void play() {
        if (noMusicSource()) {
            return;
        }
        play("", AudioManager.getInstance().getAudioType());
    }

    public void play(final int audioType) {
        Log.d(TAG, "play: ");
        if (isNetErrorAndIsKuwoAudioType() || AudioManager.getInstance().isDeviceDisconnected()) {
            return;
        }
        final Bundle bundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.DEFAULT);
        categoryBean.setCategoryId(0);
        bundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        request(audioType, AudioConstants.Action.PLAY, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
            }
        });
    }

    public void play(final String ttsContent, final int audioType) {
        Log.d(TAG, "play: ");
        if (isNetErrorAndIsKuwoAudioType() || AudioManager.getInstance().isDeviceDisconnected()) {
            return;
        }
        final Bundle bundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.DEFAULT);
        categoryBean.setCategoryId(0);
        bundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        switch (audioType) {
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                bundle.putInt(AudioConstants.BundleKey.MusicType, AudioConstants.MusicType.BLUE);
                break;
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                bundle.putInt(AudioConstants.BundleKey.MusicType, AudioConstants.MusicType.USB);
                break;
        }
        if (TextUtils.isEmpty(ttsContent)) {
            AssistantManager.getInstance().closeAssistant();
            request(audioType, AudioConstants.Action.PLAY, bundle, new IClientCallback.Stub() {
                @Override
                public void callback(Response response) {
                }
            });
        } else {
            speakContent(ttsContent, new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    AssistantManager.getInstance().closeAssistant();
                    request(audioType, AudioConstants.Action.PLAY, bundle, new IClientCallback.Stub() {
                        @Override
                        public void callback(Response response) {
                        }
                    });
                }
            });
        }
    }

    public void pause() {
        Log.d(TAG, "pause: ");
        if (noMusicSource() || AudioManager.getInstance().isDeviceDisconnected()) {
            return;
        }
        request(AudioManager.getInstance().getAudioType(), AudioConstants.Action.Option.PAUSE, new Bundle(), new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    public void switchPlayMode(@AudioConstants.KwAudioPlayMode int mode) {
        setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
        Log.d(TAG, "switchPlayMode: ");
        if (noMusicSource()) {
            return;
        }
        int audioType = AudioManager.getInstance().getAudioType();
        //禁止usb切换播放模式
//        if (audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
//            addFeedbackAndSpeak(StringUtil.format(context.getString(R.string.nonsupport_operation_about_something), context.getString(R.string.usb_music)));
//            return;
//        }
        if (audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
            closeAfterSpeak(StringUtil.format(context.getString(R.string.nonsupport_operation_about_something), context.getString(R.string.bluetooth_music)));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.AUDIO_PLAYMODE, mode);
        request(AudioManager.getInstance().getAudioType(), AudioConstants.Action.Option.SWITCH_PLAY_MODE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {
                    closeAfterSpeakAndContinueToPlay(context.getString(R.string.setup_succeeded), AudioManager.getInstance().getAudioType());
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    @Override
    String getAppName() {
        return context.getString(R.string.music);
    }

    private boolean isNetErrorAndIsKuwoAudioType() {
        return !checkNet() && AudioManager.getInstance().getAudioType() == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
    }

    private void closeAfterSpeakAndContinueToPlay(String content, int... audioType) {
        int mAudioType = audioType.length != 0 ? audioType[0] : AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
        AssistantManager.getInstance().addFeedBackConversation(content);
        speakContent(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                closeAndContinueToPlay(mAudioType);
            }
        });
    }

    private void closeDialogAndTTSAndContinueToPlay(boolean success, String ttsContent, int... audioType) {
        int mAudioType = audioType.length != 0 ? audioType[0] : AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
        if (success) {
            if (!TextUtils.isEmpty(ttsContent)) {
                closeAfterSpeakAndContinueToPlay(ttsContent, mAudioType);
            } else {
                AssistantManager.getInstance().closeAssistant();
            }
        } else {
            AssistantManager.getInstance().closeAssistant();
        }
    }

    private void closeAndContinueToPlay(int audioType) {
        AssistantManager.getInstance().closeAssistant();
        play("", audioType);
    }

    /**
     * 随机动作
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
    }

    private void jumpToMusic() {
        ComponentName componentName = new ComponentName("com.xiaoma.music", "com.xiaoma.music.MainActivity");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(componentName);
        context.startActivity(intent);
    }
}
