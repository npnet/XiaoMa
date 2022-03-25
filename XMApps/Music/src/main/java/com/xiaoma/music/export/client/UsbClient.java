package com.xiaoma.music.export.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.callback.UsbFileSearchListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.VoiceAssistantListener;
import com.xiaoma.music.common.util.UsbMusicRecordManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.manager.IUsbPlayTipsListener;
import com.xiaoma.music.manager.UsbPlayerListProxy;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.PlayMode;
import com.xiaoma.music.utils.UsbScanManager;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ZYao.
 * Date ：2019/1/30 0030
 */
public class UsbClient extends AbsAudioClient {

    private static UsbClient sClient;
    private ClientCallback mCallback;
    private boolean isNext;

    public static UsbClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (UsbClient.class) {
                if (sClient == null) {
                    sClient = new UsbClient(context);
                }
            }
        }
        return sClient;
    }

    public UsbClient(Context context) {
        super(context, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
        setUsbAudioInfoAndStatusListener();
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        super.onConnect(action, data, callback);
        Log.d("QBX", "onConnect: UsbClient");
    }

    @Override
    public boolean shareAudioInfo(AudioInfo audioInfo) {
        return super.shareAudioInfo(audioInfo);
    }

    @Override
    protected void onFavorite() {

    }

    @Override
    protected void onPrevious(ClientCallback callback) {
        mCallback = callback;
        isNext = false;
        UsbMusicFactory.getUsbPlayerListProxy().playPre();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onNext(ClientCallback callback) {
        mCallback = callback;
        isNext = true;
        UsbMusicFactory.getUsbPlayerListProxy().playNext();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onPlay(Bundle data, ClientCallback callback) {
        RemoteIatManager.getInstance().uploadPlayState(true, AppType.MUSIC);
        AudioCategoryBean bean = null;
        if (data != null) {
            data.setClassLoader(AudioCategoryBean.class.getClassLoader());
            bean = data.getParcelable(AudioConstants.BundleKey.EXTRA);
            if (bean == null) {
                return;
            }
            int playAction = bean.getAction();
            String path = bean.getUsbPath();
            if (data.containsKey(AudioConstants.BundleKey.MusicType)) {
                //双屏发来的播放信号
                int subType = data.getInt(AudioConstants.BundleKey.MusicType);
                if (subType == AudioConstants.MusicType.USB) {
                    //播放USB音乐
                    if (playAction == AudioConstants.PlayAction.DEFAULT) {
                        if (UsbScanManager.getInstance().getUsbMusicList().size() > 0) {
                            UsbMusicFactory.getUsbPlayerProxy().continuePlayOrPlayFirst();
                            shareAudioResult(true, callback);
                        } else {
                            shareConnectState(AudioConstants.ConnectStatus.USB_SCAN_FINISH_WITH_NO_MUSIC);
                        }
                    }
                }
                return;
            }
            if (playAction == AudioConstants.PlayAction.DEFAULT) {
                UsbMusicFactory.getUsbPlayerProxy().switchPlay(true);
                shareAudioResult(true, callback);
            } else if (playAction == AudioConstants.PlayAction.PLAY_LIST_AT_INDEX) {
                playUsbMusicByPath(path);
                shareAudioResult(true, callback);
            } else if (playAction == AudioConstants.PlayAction.PLAY_ALBUM_AT_INDEX) {
                playUsbMusicByPath(path);
                shareAudioResult(true, callback);
            }
        }
    }

    @Override
    protected void isPlaying(ClientCallback callback) {
        boolean isPlaying = UsbMusicFactory.getUsbPlayerProxy().isPlaying();
        Bundle bundle = new Bundle();
        bundle.putBoolean("status", isPlaying);
        callback.setData(bundle);
        callback.callback();
    }

    @Override
    protected void onPause(ClientCallback callback) {
        RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
        UsbMusicFactory.getUsbPlayerProxy().switchPlay(false);
        shareAudioResult(true, callback);
    }

    @Override
    protected void seek(Bundle data, ClientCallback callback) {
        int type = data.getInt("type");
        Bundle bundle = new Bundle();
        if (type == 1) { // set
            long seconds = data.getLong("seconds");
            long duration = UsbMusicFactory.getUsbPlayerProxy().getDuration();
            if (duration < seconds) { // 下一曲
                UsbMusicFactory.getUsbPlayerListProxy().playNext();
            } else {
                UsbMusicFactory.getUsbPlayerProxy().seekToPos(seconds);
            }
            bundle.putBoolean("result", true);
        } else { // get
            long curPosition = UsbMusicFactory.getUsbPlayerProxy().getCurPosition();
            bundle.putLong("seconds", curPosition);
        }
        callback.setData(bundle);
        callback.callback();
    }

    @Override
    protected void swithPlayMode(Bundle data, ClientCallback callback) {
        int mode = data.getInt(AudioConstants.BundleKey.AUDIO_PLAYMODE);
        shareAudioResult(true, callback);
        UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(mode);
//        switch (mode) {
//            case 1: // 随机
//                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.RANDOM);
//                break;
//            case 2: // 顺序
//                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_ORDER);
//                break;
//            case 3: // 单曲
//                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.SINGLE_LOOP);
//                break;
//            case 4: // 列表
//                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_LOOP);
//                break;
//        }
//        callback.setData(data);
//        callback.callback();
    }

    @Override
    protected void getCurrentPlayMode(ClientCallback callback) {
        int playMode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();
        Bundle bundle = new Bundle();
        switch (playMode) {
            case PlayMode.RANDOM: // 随机
                bundle.putInt("mode", 1);
                break;
            case PlayMode.LIST_ORDER: // 顺序
                bundle.putInt("mode", 2);
                break;
            case PlayMode.SINGLE_LOOP: // 单曲
                bundle.putInt("mode", 3);
                break;
            case PlayMode.LIST_LOOP: // 列表
                bundle.putInt("mode", 4);
                break;
        }
        callback.setData(bundle);
        callback.callback();
    }

    private void playUsbMusicByPath(String path) {
        UsbMusic usbMusic = UsbScanManager.getInstance().getUsbMusicByPath(path);
        if (usbMusic != null) {
            UsbMusicFactory.getUsbPlayerProxy().play(usbMusic);
        }
    }

    @Override
    protected void onPause() {
        RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
        UsbMusicFactory.getUsbPlayerProxy().switchPlay(false);
    }

    @Override
    protected void onSearchRequest(Bundle data, ClientCallback callback) {
        int searchAction = data.getInt(AudioConstants.BundleKey.SEARCH_ACTION);
        if (searchAction == AudioConstants.SearchAction.CURRENT) {
            List<UsbMusic> usbMusicList = UsbMusicFactory.getUsbPlayerListProxy().getUsbMusicList();
            if (!ListUtils.isEmpty(usbMusicList)) {
                ArrayList<AudioInfo> audioInfoList = new ArrayList<>();
                for (UsbMusic usbMusic : usbMusicList) {
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(usbMusic.getName());
                    audioInfo.setSubTitle(usbMusic.getArtist());
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    audioInfo.setCover(usbMusic.getCoverUrl());
                    audioInfo.setUsbMusicPath(usbMusic.getPath());
                    audioInfoList.add(audioInfo);
                }
                shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, audioInfoList, new int[]{0, 1, 100}, 0, callback);
            } else {
                List<UsbMusic> usbMusics = UsbScanManager.getInstance().getUsbMusicList();
                ArrayList<AudioInfo> audioInfoList = new ArrayList<>();
                for (UsbMusic usbMusic : usbMusics) {
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(usbMusic.getName());
                    audioInfo.setSubTitle(usbMusic.getArtist());
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    audioInfo.setCover(usbMusic.getCoverUrl());
                    audioInfo.setUsbMusicPath(usbMusic.getPath());
                    audioInfoList.add(audioInfo);
                }
                shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, audioInfoList, new int[]{0, 1, 100}, 0, callback);
            }
        } else if (searchAction == AudioConstants.SearchAction.SEARCH_RESULT) {
            final List<UsbMusic> usbMusicList = UsbScanManager.getInstance().getUsbMusicList();
            if (!ListUtils.isEmpty(usbMusicList)) {
                findUsbMusicList(usbMusicList);
                UsbMusicFactory.getUsbPlayerListProxy().addUsbMusicList(usbMusicList);
                shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, null, null, 0, callback);
            } else {
                shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0, callback);
            }
        }
    }

    @Override
    protected void onOtherRequest(int action, Bundle data, ClientCallback callback) {
        super.onOtherRequest(action, data, callback);
        if (action == AudioConstants.Action.LAUNCHER_USB_HISTORY) {
            playUsbMusic();
        }
    }

    private void playUsbMusic() {
        final List<UsbMusic> musicList = UsbScanManager.getInstance().getUsbMusicList();
        ThreadDispatcher.getDispatcher().postHighPriority(() -> {
            final String TAG = "UsbRestore";
            if (musicList == null || musicList.isEmpty()) {
                Log.e(TAG, String.format("Play list: %s", musicList));
                return;
            }
            UsbMusic willPlay = null;
            long startPos = 0;
            UsbMusic record = UsbMusicRecordManager.getInstance().getUsbMusicFromRecord();
            if (record != null) {
                for (UsbMusic usbMusic : musicList) {
                    if (Objects.equals(record.getPath(), usbMusic.getPath())) {
                        willPlay = usbMusic;
                        startPos = UsbMusicRecordManager.getInstance().getCurrentPositionIfExists();
                        break;
                    }
                }
            }
            if (willPlay == null) {
                willPlay = musicList.get(0);
            }
            UsbPlayerListProxy.getInstance().replaceUsbMusicList(musicList);
            final UsbMusic finalWillPlay = willPlay;
            final long finalStartPos = startPos;
            ThreadDispatcher.getDispatcher().postOnMain(() -> {
                boolean playSuccess = UsbMusicFactory.getUsbPlayerProxy().play(finalWillPlay);
                if (finalStartPos > 0) {
                    UsbMusicFactory.getUsbPlayerProxy().seekToPos(finalStartPos);
                    UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(UsbMusicRecordManager.getInstance().getPlayMode());
                }
                if (!AppObserver.getInstance().isForeground() && playSuccess) {
//                        PlayerActivity.launch(getApplication());
                }
            });
            Log.i(TAG, String.format("name: %s, startPos: %s", willPlay.getName(), startPos));
        });
    }

    @Override
    protected void onConnect() {

    }

    private void setUsbAudioInfoAndStatusListener() {
        UsbMusicFactory.getUsbPlayerProxy().addMusicChangeListener(new OnUsbMusicChangedListener() {
            @Override
            public void onBuffering(UsbMusic music) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    AudioShareManager.getInstance().shareLocalAudioDataSourceChanged();
                    if (music == null) {
                        return;
                    }
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(music.getName());
                    audioInfo.setUsbMusicPath(music.getPath());
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    audioInfo.setCover(music.getCoverUrl());
                    audioInfo.setSubTitle(music.getArtist());
                    shareAudioInfo(audioInfo);
                    shareAudioState(AudioConstants.AudioStatus.PLAYING, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    shareAudioMode(UsbMusicFactory.getUsbPlayerListProxy().getPlayMode());
                }
            }

            @Override
            public void onPlay(UsbMusic music) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    AudioShareManager.getInstance().shareLocalAudioDataSourceChanged();
                    if (music == null) {
                        return;
                    }
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(music.getName());
                    audioInfo.setUsbMusicPath(music.getPath());
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    audioInfo.setCover(music.getCoverUrl());
                    audioInfo.setSubTitle(music.getArtist());
                    shareAudioInfo(audioInfo);
                    shareAudioState(AudioConstants.AudioStatus.PLAYING, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    shareAudioMode(UsbMusicFactory.getUsbPlayerListProxy().getPlayMode());
                    RemoteIatManager.getInstance().uploadPlayState(true, AppType.MUSIC);
                    SourceUtils.setSourceStatus(SourceType.USB_MUSIC, true);
                }
            }

            @Override
            public void onPause() {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.PAUSING, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                }
                if (!VoiceAssistantListener.getInstance().isVoiceAssistantShowing()) {
                    RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                }
                SourceUtils.setSourceStatus(SourceType.USB_MUSIC, false);

            }

            @Override
            public void onProgressChange(long progressInMs, long totalInMs) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    ProgressInfo progressInfo = new ProgressInfo();
                    progressInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                    progressInfo.setTotal(totalInMs);
                    progressInfo.setCurrent(progressInMs);
                    progressInfo.setPercent(progressInMs * 1f / totalInMs);
                    shareAudioProgress(progressInfo);
                }
            }

            @Override
            public void onPlayFailed(int errorCode) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.USB_MUSIC, false);
            }

            @Override
            public void onPlayStop() {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.USB_MUSIC, false);
            }

            @Override
            public void onCompletion() {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.USB_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.USB_MUSIC, false);
            }
        });
        UsbDetector.getInstance().addUsbDetectListener(new UsbDetector.UsbDetectListener() {
            @Override
            public void noUsbMounted() {
                //usb未挂载
                shareConnectState(AudioConstants.ConnectStatus.USB_NOT_MOUNTED);
            }

            @Override
            public void inserted() {

            }

            @Override
            public void mounted(List<String> mountPaths) {
                shareConnectState(AudioConstants.ConnectStatus.USB_MOUNTED);
            }

            @Override
            public void mountError() {
                //挂载错误 不支持的设备
                shareConnectState(AudioConstants.ConnectStatus.USB_UNSUPPORT);
            }

            @Override
            public void removed() {
                shareConnectState(AudioConstants.ConnectStatus.USB_REMOVE);
            }
        });

        UsbScanManager.getInstance().addUsbScanListener(new UsbFileSearchListener() {
            @Override
            public void onUsbMusicScanFinished(ArrayList<UsbMusic> musicList) {
                if (UsbDetector.getInstance().isHandInsert()) {
                    playUsbMusic();
                } else {
                    shareConnectState(AudioConstants.ConnectStatus.USB_SCAN_FINISH_AUTO);
                }
                shareConnectState(AudioConstants.ConnectStatus.USB_SCAN_FINISH);
                if (musicList == null || musicList.size() == 0) {
                    shareConnectState(AudioConstants.ConnectStatus.USB_SCAN_FINISH_WITH_NO_MUSIC);
                }
            }

            @Override
            public void onUsbMusicAnalyticFinished(ArrayList<UsbMusic> musicList) {
            }
        });
        UsbMusicFactory.getUsbPlayerListProxy().addUsbPlayTipsListener(new IUsbPlayTipsListener() {
            @Override
            public void onFirstTips() {
                if (!isNext) {
                    shareAudioControlCode(AudioConstants.AudioControlCode.TOP, mCallback);
                }
            }

            @Override
            public void onLastTips() {
                if (isNext) {
                    shareAudioControlCode(AudioConstants.AudioControlCode.BOTTOM, mCallback);
                }
            }
        });
    }


    private void findUsbMusicList(List<UsbMusic> usbMusics) {
        if (UsbMusicFactory.getUsbPlayerProxy().isPlaying()) {
            //如果usb已经在播放，无需处理
            return;
        }

        UsbMusic usbMusicRecord = UsbMusicRecordManager.getInstance().getUsbMusicFromRecord();
        if (usbMusicRecord == null || usbMusics == null || usbMusics.size() <= 0) {
            //usb记录不存在，无需处理
            KLog.d("Usb memory file not content.");
            return;
        }

        String recordName = usbMusicRecord.getName();
        String recordPath = usbMusicRecord.getPath();
        int playMode = UsbMusicRecordManager.getInstance().getPlayMode();

        boolean recordFlag = false;
        for (int index = 0; index < usbMusics.size(); index++) {
            UsbMusic usbMusic = usbMusics.get(index);
            //文件名与路径相同 说明usb音乐记录存在
            if (recordName.equalsIgnoreCase(usbMusic.getName()) && recordPath.equalsIgnoreCase(usbMusic.getPath())) {
                UsbMusicFactory.getUsbPlayerProxy().play(usbMusic);
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(playMode);
                recordFlag = true;
                break;
            }
        }

        if (!recordFlag) {
            UsbMusicFactory.getUsbPlayerProxy().play(usbMusics.get(0));
        }
    }
}
