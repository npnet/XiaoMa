package com.xiaoma.music.export.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.VoiceAssistantListener;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.XMBluetoothDevice;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/1/30 0030
 */
public class BluetoothClient extends AbsAudioClient {

    private static BluetoothClient sClient;

    public static BluetoothClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (BluetoothClient.class) {
                if (sClient == null) {
                    sClient = new BluetoothClient(context);
                }
            }
        }
        return sClient;
    }


    public BluetoothClient(Context context) {
        super(context, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
        setBTAudioInfoAndStatusListener();
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        super.onConnect(action, data, callback);
        Log.d("QBX", "onConnect: BluetoothClient");
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
        BTMusicFactory.getBTMusicControl().preMusic();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onNext(ClientCallback callback) {
        BTMusicFactory.getBTMusicControl().nextMusic();
        shareAudioResult(true, callback);
    }

    @Override
    protected void onPlay(Bundle data, ClientCallback callback) {
        if (data != null) {
            data.setClassLoader(AudioCategoryBean.class.getClassLoader());
            if (data.containsKey(AudioConstants.BundleKey.MusicType)) {
                //双屏发来的播放信号
                int subType = data.getInt(AudioConstants.BundleKey.MusicType);
                if (subType == AudioConstants.MusicType.BLUE) {
                    //播放蓝牙音乐
                    BTMusicFactory.getBTMusicControl().switchPlay(true);
                    shareAudioResult(true, callback);
                }
                return;
            }
            BTMusicFactory.getBTMusicControl().switchPlay(true);
            shareAudioResult(true, callback);
        }
    }

    @Override
    protected void onPause() {
        BTMusicFactory.getBTMusicControl().switchPlay(false);
    }

    @Override
    protected void isPlaying(ClientCallback callback) {
        boolean isPlaying = BTMusicFactory.getBTMusicControl().isPlaying();
        Bundle bundle = new Bundle();
        bundle.putBoolean("status", isPlaying);
        callback.setData(bundle);
        callback.callback();
    }

    @Override
    protected void onPause(ClientCallback callback) {
        BTMusicFactory.getBTMusicControl().switchPlay(false);
        shareAudioResult(true, callback);
    }

    @Override
    protected void onSearchRequest(Bundle data, ClientCallback callback) {
        BTMusic currBTMusic = BTMusicFactory.getBTMusicControl().getCurrBTMusic();
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.setTitle(currBTMusic.getTitle());
        audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
        audioInfo.setPlayState(BTControlManager.getInstance().isPlaying() ? AudioConstants.AudioStatus.PLAYING : AudioConstants.AudioStatus.PAUSING);
        shareAudioInfo(audioInfo);
        BTMusicFactory.getBTMusicControl().switchPlay(true);
        shareAudioResult(true, callback);
    }

    @Override
    protected void onOtherRequest(int action, Bundle data, ClientCallback callback) {
        super.onOtherRequest(action, data, callback);
        if (action == AudioConstants.Action.BT_CONNECT_STATE) {
            List<XMBluetoothDevice> btConnectDevice = BTControlManager.getInstance().getBTConnectDevice();
            shareAudioResult(!ListUtils.isEmpty(btConnectDevice), callback);
        }
    }

    @Override
    protected void onConnect() {

    }

    private void setBTAudioInfoAndStatusListener() {
        BTMusicFactory.getBTMusicControl().addBtMusicInfoChangeListener(new OnBTMusicChangeListener() {
            @Override
            public void currentBtMusic(BTMusic music) {
                KLog.d("MrMine", "currentBtMusic: " + "");
                if (music == null) {
                    return;
                }
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(music.getTitle());
                    audioInfo.setPlayState(BTControlManager.getInstance().getBtPlayStatus() == PlayStatus.PLAYING
                            || BTControlManager.getInstance().getBtPlayStatus() == PlayStatus.BUFFERING ?
                            AudioConstants.AudioStatus.PLAYING : AudioConstants.AudioStatus.PAUSING);
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                    shareAudioInfo(audioInfo);
                }
            }

            @Override
            public void onPlay() {
                KLog.d("MrMine", "onPlay: " + "");
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
                    BTMusic currBTMusic = BTMusicFactory.getBTMusicControl().getCurrBTMusic();
                    AudioInfo audioInfo = new AudioInfo();
                    audioInfo.setTitle(currBTMusic.getTitle());
                    audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                    audioInfo.setPlayState(AudioConstants.AudioStatus.PLAYING);
                    shareAudioInfo(audioInfo);
                    shareAudioState(AudioConstants.AudioStatus.PLAYING, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                }
                RemoteIatManager.getInstance().uploadPlayState(true, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.BT_MUSIC, true);
            }

            @Override
            public void onPause() {
                KLog.d("MrMine", "onPause: " + "");
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.PAUSING, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                }
                if (!VoiceAssistantListener.getInstance().isVoiceAssistantShowing()) {
                    RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                }
                SourceUtils.setSourceStatus(SourceType.BT_MUSIC, false);
            }

            @Override
            public void onProgressChange(long progressInMs, long totalInMs) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    ProgressInfo progressInfo = new ProgressInfo();
                    progressInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                    progressInfo.setTotal(totalInMs);
                    progressInfo.setCurrent(progressInMs);
                    progressInfo.setPercent(totalInMs == 0 ? 0 : progressInMs * 1f / totalInMs);
                    shareAudioProgress(progressInfo);
                }
            }

            @Override
            public void onPlayFailed(int errorCode) {
                KLog.d("MrMine", "onPlayFailed: " + "");
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.BT_MUSIC, false);
            }

            @Override
            public void onPlayStop() {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    shareAudioState(AudioConstants.AudioStatus.STOPPED, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                }
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.MUSIC);
                SourceUtils.setSourceStatus(SourceType.BT_MUSIC, false);
            }
        });
        BTMusicFactory.getBTMusicControl().addBtStateChangeListener(new OnBTConnectStateChangeListener() {
            @Override
            public void onBTConnect() {
                shareConnectState(AudioConstants.ConnectStatus.BLUETOOTH_CONNECTED);
            }

            @Override
            public void onBTDisconnect() {
                shareConnectState(AudioConstants.ConnectStatus.BLUETOOTH_DISCONNECTED);
            }

            @Override
            public void onBTSinkConnected() {
                shareConnectState(AudioConstants.ConnectStatus.BLUETOOTH_SINK_CONNECTED);
            }

            @Override
            public void onBTSinkDisconnected() {
                shareConnectState(AudioConstants.ConnectStatus.BLUETOOTH_SINK_DISCONNECTED);
            }
        });
    }

}
