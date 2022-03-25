package com.xiaoma.music.export.manager;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.client.BluetoothClient;
import com.xiaoma.music.export.client.KwClient;
import com.xiaoma.music.export.client.UsbClient;
import com.xiaoma.music.export.receiver.ExportMusicReceiver;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.receiver.UsbDetector;

/**
 * Created by ZYao.
 * Date ：2019/1/30 0030
 */
public class AudioShareManager {
    public static final String PLAY_MUSIC_ACTION = "com.xiaoma.PLAY_ANTI_FATIGUE_MUSIC";
    private Context mContext;
    private AbsAudioClient mUsbClient, mKwClient, mBluetoothClient;
    private AssistantClient mAssistantClient;

    private AudioShareManager() {
    }

    public static AudioShareManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final AudioShareManager instance = new AudioShareManager();
    }

    public void init(final Context context) {
        this.mContext = context;
        initCenterService(context);
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onPrepare(String depend) {
                Log.d("music", "onPrepare initCenterService " + Center.getInstance().isConnected());
                if (!Center.getInstance().isConnected()) {
                    initCenterService(mContext);
                }
            }

            @Override
            public void onConnected() {
                super.onConnected();
                Log.d("music", "onConnected shareInitMusicInfo");
                AudioShareManager.getInstance().shareInitMusicInfo();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY_MUSIC_ACTION);
        context.registerReceiver(new ExportMusicReceiver(), intentFilter);
    }

    private void initCenterService(Context context) {
        Center.getInstance().init(context);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                registerClient(mContext);
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                    @Override
                    public boolean queueIdle() {
                        UsbDetector.getInstance().syncUsbConnectState(mContext.getApplicationContext());
                        return false;
                    }
                });
            }
        });
    }


    private void registerClient(Context context) {
        Center.getInstance().register(mKwClient = KwClient.newSingleton(context));
        Center.getInstance().register(mUsbClient = UsbClient.newSingleton(context));
        Center.getInstance().register(mBluetoothClient = BluetoothClient.newSingleton(context));
        Center.getInstance().register(mAssistantClient = AssistantClient.newSingleton(context));
    }

    public void shareInitMusicInfo() {
        if (mKwClient != null) {
            mKwClient.shareInitMusicInfo();
        }
    }


    public void shareOnlineAudioFavorite(boolean isFavorite) {
        if (mKwClient != null) {
            mKwClient.shareAudioFavorite(isFavorite);
        }
    }

    //如果success后会进行播放则需要在fetchFailed的时候进行shareAudioErrorStateOnNet
    public void shareAudioErrorStateOnNet() {
        if (mKwClient != null) {
            mKwClient.shareAudioState(AudioConstants.AudioStatus.ERROR, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        }
    }

    public void shareKwAudioDataSourceChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.KUWO);
        }
    }

    public void shareKwRadioDataSourceChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.KUWO_RADIO);
        }
    }

    public void shareLocalAudioDataSourceChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
        }
    }

    public void shareKwAudioTypeChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioTypeToLauncher(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        }
    }

    public void shareUsbAudioTypeChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioTypeToLauncher(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
        }
    }

    public void shareBtAudioTypeChanged() {
        if (mKwClient != null) {
            mKwClient.shareAudioTypeToLauncher(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
        }
    }


    public void shareKwPlayMode(@AudioConstants.AudioPlayMode int playMode) {
        if (mKwClient != null) {
            mKwClient.shareAudioMode(playMode);
        }
    }

    public void shareUsbPlayMode(@AudioConstants.AudioPlayMode int playMode) {
        if (mUsbClient != null) {
            mUsbClient.shareAudioMode(playMode);
        }
    }

}
