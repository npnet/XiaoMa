package com.xiaoma.music.common.audiosource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.util.ArraySet;

import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.music.common.constant.PlayerBroadcast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.Set;

import static com.xiaoma.music.common.audiosource.AudioSource.BLUETOOTH_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.ONLINE_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.USB_MUSIC;

/**
 * Created by LKF on 2018-12-21 0021.
 * 音源管理器,记录当前音源类型,统一管理全局播放逻辑
 */
public class AudioSourceManager {
    private static final String TAG = AudioSourceManager.class.getSimpleName();
    public static final String KEY_AUDIO_SOURCE = AudioSourceManager.class.getSimpleName();
    private Context mContext;

    private static class InstanceHolder {
        private static AudioSourceManager sInstance = new AudioSourceManager();
    }

    public static AudioSourceManager getInstance() {
        return InstanceHolder.sInstance;
    }

    private AudioSourceManager() {
    }

    private final Set<AudioSourceListener> mAudioSourceListeners = new ArraySet<>();
    @AudioSource
    private int mCurrAudioSource = AudioSource.NONE;
    private PlayerProxy mCurrPlayer;
    private final BroadcastReceiver mPlayerCmdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PlayerProxy player = mCurrPlayer;
            if (player == null) {
                KLog.e(TAG, String.format("PlayCmdReceiver # onReceive( intent: %s ) PlayerProxy is null, ignore", intent));
                return;
            }
            KLog.i(TAG, String.format("PlayCmdReceiver # onReceive( intent: %s )", intent));
            String action = intent.getAction();
            if (PlayerBroadcast.Action.PLAYER_CONTROL.equals(action)) {
                String cmd = intent.getStringExtra(PlayerBroadcast.Extra.CONTROL_CMD);
                if (PlayerBroadcast.Command.PLAY.equals(cmd)) {
                    player.continuePlay();
                } else if (PlayerBroadcast.Command.PAUSE.equals(cmd)) {
                    player.pause();
                }
            }
        }
    };

    public void init(Context context) {
        // 全局播放指令监听
        try {
            mContext = context;
            context.registerReceiver(mPlayerCmdReceiver, new IntentFilter(PlayerBroadcast.Action.PLAYER_CONTROL));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void switchAudioSource(@AudioSource int currAudioSource) {
        int preAudioSource = getCurrAudioSource();
        if (preAudioSource == currAudioSource) {
            KLog.w(TAG, String.format("switchAudioSource( pre: %s, curr: %s ) The same AudioSource", preAudioSource, currAudioSource));
            return;
        }
        KLog.i(TAG, String.format("switchAudioSource( pre: %s, curr: %s )", preAudioSource, currAudioSource));
        dispatchAudioSourceChanged(preAudioSource, currAudioSource);
        setCurrAudioSource(currAudioSource);
    }


    public void switchAudioSource(@AudioSource int currAudioSource, PlayerProxy playerProxy) {
        mCurrPlayer = playerProxy;
        int preAudioSource = getCurrAudioSource();
        if (preAudioSource == currAudioSource) {
            KLog.w(TAG, String.format("switchAudioSource( pre: %s, curr: %s ) The same AudioSource", preAudioSource, currAudioSource));
            return;
        }
        KLog.i(TAG, String.format("switchAudioSource( pre: %s, curr: %s )", preAudioSource, currAudioSource));
        dispatchAudioSourceChanged(preAudioSource, currAudioSource);
        setCurrAudioSource(currAudioSource);
    }

    private void setCurrAudioSource(@AudioSource int audioSource) {
        mCurrAudioSource = audioSource;
        TPUtils.put(mContext, AudioSourceManager.KEY_AUDIO_SOURCE, mCurrAudioSource);
    }

    public int getCurrAudioSource() {
        return mCurrAudioSource;
    }

    public void addAudioSourceListener(AudioSourceListener listener) {
        if (listener != null)
            mAudioSourceListeners.add(listener);
    }

    public void removeAudioSourceListener(AudioSourceListener listener) {
        if (listener != null)
            mAudioSourceListeners.remove(listener);
    }

    private void dispatchAudioSourceChanged(@AudioSource int preAudioSource, @AudioSource int currAudioSource) {
        Set<AudioSourceListener> listeners = new ArraySet<>(mAudioSourceListeners);
        for (AudioSourceListener listener : listeners) {
            listener.onAudioSourceSwitch(preAudioSource, currAudioSource);
        }
        setPlayerType(currAudioSource);
    }

    private void setPlayerType(@AudioSource int currAudioSource) {
        switch (currAudioSource) {
            case ONLINE_MUSIC:
                SourceUtils.setPlaySrouce(SourceType.NET_MUSIC);
                break;
            case BLUETOOTH_MUSIC:
                SourceUtils.setPlaySrouce(SourceType.BT_MUSIC);
                break;
            case USB_MUSIC:
                SourceUtils.setPlaySrouce(SourceType.USB_MUSIC);
                break;
        }
    }

    public void removeCurrAudioSource() {
        if (mCurrPlayer != null) {
            mCurrPlayer.pause();
        }
        switchAudioSource(AudioSource.NONE, null);
    }


    public interface PlayerProxy {
        void continuePlay();

        void pause();
    }
}