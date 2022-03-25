package com.xiaoma.music.export.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.PlayerBroadcast;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.common.manager.VoiceAssistantListener;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.export.HandleKwActionFactory.BaseRequestInterceptHandler;
import com.xiaoma.music.export.HandleKwActionFactory.MusicRequestActionDispatcher;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.export.manager.MoodLightingManager;
import com.xiaoma.music.export.model.MusicTagInfo;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;
import com.xiaoma.music.player.model.CollectEventBean;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import org.simple.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import cn.kuwo.mod.playcontrol.PlayMode;

/**
 * Created by ZYao.
 * Date ：2019/1/30 0030
 */
public class KwClient extends AbsAudioClient {
    private static KwClient sClient;
    private Context mContext;

    public static KwClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (KwClient.class) {
                if (sClient == null) {
                    sClient = new KwClient(context);
                }
            }
        }
        return sClient;
    }

    public KwClient(Context context) {
        super(context, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        mContext = context.getApplicationContext();
        setOnlineAudioInfoAndStatusListener();
        AudioShareManager.getInstance().shareInitMusicInfo();
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        super.onConnect(action, data, callback);
        Log.d("QBX", "onConnect: KwClient");
    }

    @Override
    public boolean shareAudioInfo(AudioInfo audioInfo) {
        audioInfo.setPlayState(OnlineMusicFactory.getKWPlayer().getStatus() == PlayStatus.PLAYING
                || OnlineMusicFactory.getKWPlayer().getStatus() == PlayStatus.BUFFERING ?
                AudioConstants.AudioStatus.PLAYING : AudioConstants.AudioStatus.PAUSING);
        return super.shareAudioInfo(audioInfo);
    }

    @Override
    protected void onFavorite() {
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        long rid = nowPlayingMusic.getRid();
        XMMusic favoriteMusic = MusicDbManager.getInstance().queryCollectionMusicById(rid);
        if (favoriteMusic != null) {
            MusicDbManager.getInstance().deleteCollectionMusic(favoriteMusic);
        } else {
            MusicDbManager.getInstance().saveCollectionMusic(nowPlayingMusic);
        }
        CollectEventBean bean = new CollectEventBean();
        bean.setFavorite(favoriteMusic != null);
        bean.setMusic(nowPlayingMusic);
        EventBus.getDefault().post(bean, EventBusTags.MUSIC_COLLECTION);
    }

    @Override
    protected void onPrevious(ClientCallback callback) {
        int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        if (PlayMode.MODE_ALL_ORDER == playMode && index == 0 && callback != null) {
            shareAudioControlCode(AudioConstants.AudioControlCode.TOP, callback);
            shareAudioResult(false, callback);
            return;
        }
        OnlineMusicFactory.getKWPlayer().playPre();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onNext(ClientCallback callback) {
        int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
        if (nowPlayingList == null) {
            shareAudioResult(false, callback);
            return;
        }
        int size = nowPlayingList.toList().size();
        if (PlayMode.MODE_ALL_ORDER == playMode && index == size - 1 && callback != null) {
            shareAudioControlCode(AudioConstants.AudioControlCode.BOTTOM, callback);
            shareAudioResult(false, callback);
            return;
        }
        OnlineMusicFactory.getKWPlayer().playNext();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onPlay(Bundle data, ClientCallback callback) {
        AudioCategoryBean bean = null;
        RemoteIatManager.getInstance().uploadPlayState(true, AppType.MUSIC);
        if (data != null) {
            data.setClassLoader(AudioCategoryBean.class.getClassLoader());
            bean = data.getParcelable(AudioConstants.BundleKey.EXTRA);
            if (bean == null) {
                return;
            }
            int playAction = bean.getAction();
            int categoryId = bean.getCategoryId();
            if (data.containsKey(AudioConstants.BundleKey.MusicType)) {
                //双屏发来的播放信号
                int subType = data.getInt(AudioConstants.BundleKey.MusicType);
                if (subType == AudioConstants.MusicType.ONLINE) {
                    //播放在线音乐
                    if (playAction == AudioConstants.PlayAction.DEFAULT) {
                        if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.NONE) {
                            OnlineMusicFactory.getKWPlayer().playBillBroadFirst();
                            shareAudioResult(true, callback);
                        } else {
                            if (OnlineMusicFactory.getKWPlayer().continuePlay()) {
                                shareAudioResult(true, callback);
                            } else {
                                OnlineMusicFactory.getKWPlayer().playBillBroadFirst();
                                shareAudioResult(true, callback);
                            }
                        }
                    }
                }
                return;
            }
            if (playAction == AudioConstants.PlayAction.DEFAULT) {
                final XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
                if (nowPlayingMusic == null) {
                    shareAudioResult(false, callback);
                    return;
                }
                OnlineMusicFactory.getKWPlayer().continuePlay();
                shareAudioResult(true, callback);
            } else if (playAction == AudioConstants.PlayAction.PLAY_LIST_AT_INDEX) {
                playMusicById(categoryId);
            } else if (playAction == AudioConstants.PlayAction.PLAY_REC_MUSIC) {
                playRecMusicById(categoryId);
            } else if (playAction == AudioConstants.PlayAction.PLAY_REC_MUSIC_NAME) {
                playRecMusicById(categoryId);
            } else if (playAction == AudioConstants.PlayAction.PLAY_ALBUM_AT_INDEX) {
                playMusicById(categoryId);
            } else if (playAction == AudioConstants.PlayAction.HISTORY) {
                playMusicById(categoryId);
            } else if (playAction == AudioConstants.PlayAction.PLAY_KW_FAVORITE_LIST) {
                playCollectionMusic(categoryId, callback);
                shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.KUWO);
            }
        }
    }

    private void playRecMusicById(int categoryId) {
        OnlineMusicFactory.getKWAudioFetch().fetchMusicById(categoryId, new PlayAfterSuccessFetchListener<XMMusic>() {
            @Override
            public void onFetchSuccess(XMMusic music) {
                OnlineMusicFactory.getKWPlayer().play(music);
            }

            @Override
            public void onFetchFailed(String msg) {
                XMToast.showToast(mContext, R.string.copyright_songs_cannot_be_played);
            }
        });
    }

    private void playMusicById(int categoryId) {
        OnlineMusicFactory.getKWAudioFetch().fetchMusicById(categoryId, new PlayAfterSuccessFetchListener<XMMusic>() {
            @Override
            public void onFetchSuccess(XMMusic music) {
                OnlineMusicFactory.getKWPlayer().play(music);
                if (music.isPlayFree()) {

                }
            }

            @Override
            public void onFetchFailed(String msg) {

            }
        });
    }

    @Override
    protected void onPause() {
        RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
        OnlineMusicFactory.getKWPlayer().pause();
    }

    @Override
    protected void onSearchRequest(Bundle data, ClientCallback callback) {
        int searchAction = data.getInt(AudioConstants.BundleKey.SEARCH_ACTION);
        BaseRequestInterceptHandler dispatcher = MusicRequestActionDispatcher.getInstance().dispatcher(mContext, searchAction, callback);
        dispatcher.handler(this, data);
    }

    @Override
    protected void onPause(ClientCallback callback) {
        super.onPause(callback);
        onPause();
        shareAudioResult(true, callback);
    }

    @Override
    public void onOtherRequest(int action, Bundle data, ClientCallback callback) {
        super.onOtherRequest(action, data, callback);
        if (action == AudioConstants.Action.Option.COLLECT) {
            collectionMusic(callback);
        } else if (action == AudioConstants.Action.Option.CANCEL_COLLECT) {
            cancelCollection(callback);
        }
    }

    private void collectionMusic(ClientCallback callback) {
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        long rid = nowPlayingMusic.getRid();
        XMMusic favoriteMusic = MusicDbManager.getInstance().queryCollectionMusicById(rid);
        if (favoriteMusic != null) {
            shareAudioResult(false, callback);
        } else {
            shareAudioResult(true, callback);
            MusicDbManager.getInstance().saveCollectionMusic(nowPlayingMusic);
            CollectEventBean bean = new CollectEventBean();
            bean.setFavorite(false);
            bean.setMusic(nowPlayingMusic);
            EventBus.getDefault().post(bean, EventBusTags.MUSIC_COLLECTION);
            AudioShareManager.getInstance().shareOnlineAudioFavorite(true);
        }
    }

    private void playCollectionMusic(int categoryId, ClientCallback callback) {
        final List<XMMusic> musicHistory = MusicDbManager.getInstance().queryCollectionMusic();
        if (ListUtils.isEmpty(musicHistory)) {
            shareAudioResult(false, callback);
            return;
        }
        Collections.reverse(musicHistory);
        int index = 0;
        if (categoryId != 0) {
            for (int i = 0; i < musicHistory.size(); i++) {
                if (musicHistory.get(i).getRid() == categoryId) {
                    index = i;
                }
            }
        }
        OnlineMusicFactory.getKWPlayer().play(musicHistory, index);
        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
        shareAudioResult(true, callback);
        KwPlayInfoManager.getInstance().setCurrentPlayInfo(musicHistory.get(0).getRid() + musicHistory.get(0).getName(),
                KwPlayInfoManager.AlbumType.COLLECTION);
    }

    private void cancelCollection(ClientCallback callback) {
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        long rid = nowPlayingMusic.getRid();
        XMMusic favoriteMusic = MusicDbManager.getInstance().queryCollectionMusicById(rid);
        if (favoriteMusic != null) {
            MusicDbManager.getInstance().deleteCollectionMusic(favoriteMusic);
            shareAudioResult(true, callback);
            CollectEventBean bean = new CollectEventBean();
            bean.setFavorite(true);
            bean.setMusic(nowPlayingMusic);
            EventBus.getDefault().post(bean, EventBusTags.MUSIC_COLLECTION);
            AudioShareManager.getInstance().shareOnlineAudioFavorite(false);
        } else {
            shareAudioResult(false, callback);
        }
    }

    private void switchPlayMode(ClientCallback callback, int mode) {
        OnlineMusicFactory.getKWPlayer().setPlayMode(mode);
        shareAudioResult(true, callback);
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.Action.ONLINE_PLAY_MODE);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }

    @Override
    protected void swithPlayMode(Bundle data, ClientCallback callback) {
        int mode = data.getInt(AudioConstants.BundleKey.AUDIO_PLAYMODE);
        switchPlayMode(callback, mode);
    }

    @Override
    protected void onConnect() {
        AudioShareManager.getInstance().shareInitMusicInfo();
    }

    private void setOnlineAudioInfoAndStatusListener() {
        OnlineMusicFactory.getKWMessage().addPlayStateListener(new OnPlayControlListener() {
            @Override
            public void onPreStart(XMMusic music) {
                if (XMMusic.isEmpty(music)) {
                    return;
                }
                try {
                    if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                        wrapperAudioInfo(music, false);
                        shareAudioFavorite(MusicDbManager.getInstance().queryCollectionMusicById(music.getRid()) != null);
                        shareAudioMode(OnlineMusicFactory.getKWPlayer().getPlayMode());
                        shareAudioState(AudioConstants.AudioStatus.LOADING, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                        startLighting(music);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReadyPlay(XMMusic music) {
                if (XMMusic.isEmpty(music)) {
                    return;
                }
                try {
                    if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                        wrapperAudioInfo(music, false);
                        shareAudioFavorite(MusicDbManager.getInstance().queryCollectionMusicById(music.getRid()) != null);
                        shareAudioMode(OnlineMusicFactory.getKWPlayer().getPlayMode());
                        shareAudioState(AudioConstants.AudioStatus.LOADING, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                        startLighting(music);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBufferStart() {

            }

            @Override
            public void onBufferFinish() {

            }

            @Override
            public void onPlay(XMMusic music) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    wrapperAudioInfo(music, false);
                    shareAudioState(AudioConstants.AudioStatus.PLAYING, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
                RemoteIatManager.getInstance().uploadPlayState(true, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.NET_MUSIC, true);

            }

            @Override
            public void onSeekSuccess(int position) {

            }

            @Override
            public void onPause() {
                stopLighting();
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.PAUSING, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
                if (!VoiceAssistantListener.getInstance().isVoiceAssistantShowing()) {
                    RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                }
                SourceUtils.setSourceStatus(SourceType.NET_MUSIC, false);
            }

            @Override
            public void onProgressChange(long progressInMs, long totalInMs) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    ProgressInfo progressInfo = new ProgressInfo();
                    progressInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                    progressInfo.setTotal(totalInMs);
                    progressInfo.setCurrent(progressInMs);
                    progressInfo.setPercent(progressInMs * 1f / totalInMs);
                    shareAudioProgress(progressInfo);
                }
            }

            @Override
            public void onPlayFailed(int errorCode, String errorMsg) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
                switch (errorCode) {
                    case KuwoPlayControlObserver.ErrorCode.NO_COPY_RIGHT:
                    case KuwoPlayControlObserver.ErrorCode.NEED_VIP:
                    case KuwoPlayControlObserver.ErrorCode.NEED_SING_SONG:
                    case KuwoPlayControlObserver.ErrorCode.NEED_ALBUM:
                    case KuwoPlayControlObserver.ErrorCode.NOT_ENOUGH:
                        XMToast.showToast(mContext, "付费音乐购买后才能播放哦~", true);
                        break;
                    case KuwoPlayControlObserver.ErrorCode.OTHER_PLAY_ERROR:
                        XMToast.toastException(mContext, R.string.net_work_error, true);
                        if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                            shareAudioState(AudioConstants.AudioStatus.ERROR, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                        }
                        break;
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.NET_MUSIC, false);
            }

            @Override
            public void onPlayStop() {
                stopLighting();
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.NET_MUSIC, false);
            }

            @Override
            public void onPlayModeChanged(int playMode) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    shareAudioMode(playMode);
                }
            }

            @Override
            public void onCurrentPlayListChanged() {

            }
        });
    }

    private void stopLighting() {
        int lightId = caseAmbientLightColorToValue(12);
        XmCarFactory.getCarVendorExtensionManager().setAmbientLightColor(lightId);
    }

    private void startLighting(XMMusic music) {
        MusicTagInfo musicTagInfo = MoodLightingManager.getInstance().queryLocalTagById(music.getRid());
        if (musicTagInfo != null) {
            // TODO: 2019/5/8 0008 设置氛围灯
            setCarLightId(musicTagInfo);
            return;
        }
        RequestManager.getInstance().requestMusicTagById(music.getRid(), music.getName(), new ModelCallback<MusicTagInfo>() {
            @Override
            public MusicTagInfo parse(String data) throws Exception {
                XMResult<MusicTagInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<MusicTagInfo>>() {
                }.getType());
                if (result != null && result.isSuccess()) {
                    return result.getData();
                }
                return null;
            }

            @Override
            public void onSuccess(MusicTagInfo model) {
                if (model != null) {
                    MoodLightingManager.getInstance().saveMusicTag(model);
                    setCarLightId(model);
                }
            }

            @Override
            public void onError(int code, String msg) {
            }


        });
    }

    private void setCarLightId(MusicTagInfo musicTagInfo) {
        int lightId = caseAmbientLightColorToValue(musicTagInfo.getTagId());
        XmCarFactory.getCarVendorExtensionManager().setAmbientLightColor(lightId);
    }

    private int caseAmbientLightColorToValue(int index) {
        int value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_1;
        switch (index) {
            case 1:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_1;
                break;
            case 2:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_2;
                break;
            case 3:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_3;
                break;
            case 4:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_4;
                break;
            case 5:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_5;
                break;
            case 6:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_6;
                break;
            case 7:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_7;
                break;
            case 8:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_8;
                break;
            case 9:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_9;
                break;
            case 10:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_10;
                break;
            case 11:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_11;
                break;
            case 12:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_12;
                break;
        }
        return value;
    }

    @Override
    public void shareInitMusicInfo() {
        super.shareInitMusicInfo();
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                final XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
                if (!XMMusic.isEmpty(nowPlayingMusic)) {
                    wrapperAudioInfo(nowPlayingMusic, true);
                }
            }
        }, 2000);
    }

    private void wrapperAudioInfo(final XMMusic music, boolean isInit) {
        final AudioInfo audioInfo = new AudioInfo();
        if (music.getRid() < 0) {
            audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_KUWO_RADIO);
        } else {
            audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        }
        audioInfo.setTitle(music.getName());
        audioInfo.setSubTitle(music.getArtist());
        audioInfo.setUniqueId(music.getRid());
        audioInfo.setHistory(isInit);
        shareAudioInfo(audioInfo);
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                OnlineMusicFactory.getKWAudioFetch().fetchImage(music, new OnAudioFetchListener<String>() {
                    @Override
                    public void onFetchSuccess(String s) {
                        audioInfo.setCover(s);
                        shareAudioInfo(audioInfo);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        audioInfo.setCover("");
                        shareAudioInfo(audioInfo);
                    }
                }, IKuwoConstant.IImageSize.SIZE_120);
            }
        });

    }

}
