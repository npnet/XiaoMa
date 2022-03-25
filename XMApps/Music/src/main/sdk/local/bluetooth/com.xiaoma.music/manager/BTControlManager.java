package com.xiaoma.music.manager;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.autotracker.model.PlayType;
import com.xiaoma.music.R;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.AudioFocusHelper;
import com.xiaoma.music.common.manager.UploadPlayManager;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.XMBluetoothDevice;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.media.session.PlaybackState.STATE_BUFFERING;
import static android.media.session.PlaybackState.STATE_ERROR;
import static android.media.session.PlaybackState.STATE_NONE;
import static android.media.session.PlaybackState.STATE_PAUSED;
import static android.media.session.PlaybackState.STATE_PLAYING;
import static android.media.session.PlaybackState.STATE_STOPPED;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class BTControlManager implements IBTMusic, BluetoothReceiver.BluetoothStateCallback {
    public static final String PACKAGE_BLUETOOTH = "com.android.bluetooth";
    private static final String TAG = "BTControlManager";
    public static final String CLASS_BROWSER_SERVICE = "com.android.bluetooth.a2dpsink.mbs.A2dpMediaBrowserService";
    private BluetoothA2dpSink mA2dpSinkService;
    private BluetoothHeadsetClient mHeadsetService;
    private BluetoothAvrcpController mAvrcpController;
    private static Set<OnBTConnectStateChangeListener> stateChangeListeners = new HashSet<>();
    private static Set<OnBTMusicChangeListener> musicInfoChangeListeners = new HashSet<>();
    private MediaPlaybackModel mMediaPlaybackModel;
    private Context mContext;
    private int currentState = -1;
    @PlayStatus
    private int currentPlayStatus = PlayStatus.INIT;
    private BTMusic mCurrBTMusic;
    private AudioFocusHelper mAudioFocusHelper;
    private BluetoothA2dpSinkExt bluetoothA2dpSinkExt;
    private boolean isBluetoothA2dpSinkExtConnected;
    private int lastPlayState = STATE_NONE;


    public static BTControlManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final BTControlManager instance = new BTControlManager();
    }


    @Override
    public void onBTSinkDisconnected() {
        for (OnBTConnectStateChangeListener stateChangeListener : stateChangeListeners) {
            if (stateChangeListener != null) {
                stateChangeListener.onBTSinkDisconnected();
            }
        }
        pauseRender();
        resetBtMusic();
    }

    @Override
    public void onBTSinkConnected() {
        for (OnBTConnectStateChangeListener stateChangeListener : stateChangeListeners) {
            if (stateChangeListener != null) {
                stateChangeListener.onBTSinkConnected();
            }
        }
        resetBtMusic();
    }

    @Override
    public void onBTConnected() {
        for (OnBTConnectStateChangeListener stateChangeListener : stateChangeListeners) {
            if (stateChangeListener != null) {
                stateChangeListener.onBTConnect();
            }
        }
    }

    @Override
    public void onBTDisconnected() {
        if (mA2dpSinkService != null) {
            List<BluetoothDevice> connectedDevices = mA2dpSinkService.getConnectedDevices();
            if (!ListUtils.isEmpty(connectedDevices)) {
                return;
            }
        }
        if (mHeadsetService != null) {
            List<BluetoothDevice> connectedDevices = mHeadsetService.getConnectedDevices();
            if (!ListUtils.isEmpty(connectedDevices)) {
                for (OnBTConnectStateChangeListener stateChangeListener : stateChangeListeners) {
                    if (stateChangeListener != null) {
                        stateChangeListener.onBTSinkDisconnected();
                    }
                }
                return;
            }
        }
        for (OnBTConnectStateChangeListener stateChangeListener : stateChangeListeners) {
            if (stateChangeListener != null) {
                stateChangeListener.onBTDisconnect();
            }
        }
        resetBtMusic();
    }

    private void resetBtMusic() {
        mCurrBTMusic = null;
        currentPlayStatus = PlayStatus.INIT;
        currentState = -1;
    }

    public BTControlManager() {
    }

    private final AudioSourceManager.PlayerProxy mAudioSourcePlayerProxy = new AudioSourceManager.PlayerProxy() {
        @Override
        public void continuePlay() {
            doSwitchPlay(true, true);
        }

        @Override
        public void pause() {
            doSwitchPlay(false, true);
        }
    };

    private void switchBtMusicAudioSource() {
        AudioSourceManager.getInstance().switchAudioSource(AudioSource.BLUETOOTH_MUSIC, mAudioSourcePlayerProxy);
        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
    }

    @Override
    public void init(final Context context) {
        mContext = context.getApplicationContext();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getProfileProxy(context, mA2dpSinkServiceListener, BluetoothProfile.A2DP_SINK);
        bluetoothAdapter.getProfileProxy(context, mAvrcpServiceListener, BluetoothProfile.AVRCP_CONTROLLER);
        bluetoothAdapter.getProfileProxy(context, mHeadsetServiceListener, BluetoothProfile.HEADSET_CLIENT);
        initMediaSession(context);
        startMediaSession();
        mAudioFocusHelper = new AudioFocusHelper(mContext, new AudioFocusHelper.PlayerProxy() {
            @Override
            public boolean isPlaying() {
                return BTControlManager.this.isPlaying();
            }

            @Override
            public void continuePlayWithoutFocus() {
                BTControlManager.this.doSwitchPlay(true, false);
            }

            @Override
            public void pauseWithoutFocus() {
                BTControlManager.this.doSwitchPlay(false, false);
            }

            @Override
            public void setVolumeScale(float scale) {
                // TODO:
            }
        });
        bluetoothA2dpSinkExt = new BluetoothA2dpSinkExt(context);
        bluetoothA2dpSinkExt.init(new ServiceListener() {
            @Override
            public void onConnectedState(boolean st) {
                KLog.d("hzx", "bluetoothA2dpSinkExt 连接状态: " + st);
                isBluetoothA2dpSinkExtConnected = st;
            }
        });
    }


    public void startRender() {
        if (isBluetoothA2dpSinkExtConnected && bluetoothA2dpSinkExt != null) {
            bluetoothA2dpSinkExt.startRender();
        }
    }

    public void pauseRender() {
        if (isBluetoothA2dpSinkExtConnected && bluetoothA2dpSinkExt != null) {
            bluetoothA2dpSinkExt.pauseRender();
        }
    }

    @Override
    public void initMediaSession(Context context) {
        ComponentName componentName = new ComponentName(PACKAGE_BLUETOOTH, CLASS_BROWSER_SERVICE);
        MediaManager.getInstance(context).setMediaClientComponent(componentName);
        mMediaPlaybackModel = new MediaPlaybackModel(context, null /* browserExtras */);
        mMediaPlaybackModel.addListener(mListener);
    }

    @Override
    public void requestBluetoothAudioFocus() {
        if (mA2dpSinkService != null) {
            List<BluetoothDevice> connectedDevices = mA2dpSinkService.getConnectedDevices();
            if (!ListUtils.isEmpty(connectedDevices)) {
//                mA2dpSinkService.requestA2dpAudioFocus(connectedDevices.get(0));
            }
        }
    }

    @Override
    public void startMediaSession() {
        if (mMediaPlaybackModel != null)
            mMediaPlaybackModel.start();
    }

    @Override
    public void stopMediaSession() {
        if (mMediaPlaybackModel != null)
            mMediaPlaybackModel.stop();
    }

    public BTMusic getCurrBTMusic() {
        if (mCurrBTMusic != null) {
            return mCurrBTMusic;
        }
        if (mMediaPlaybackModel != null) {
            MediaMetadata metadata = mMediaPlaybackModel.getMetadata();
            mCurrBTMusic = convertBtMusic(metadata);
        }
        return mCurrBTMusic;
    }

    private BTMusic emptyMusic() {
        String artist = mContext.getString(R.string.unknow_artist);
        String title = mContext.getString(R.string.unknow_song);
        return new BTMusic(title, artist, 0, "");
    }

    @PlayStatus
    @Override
    public int getBtPlayStatus() {
        return currentPlayStatus;
    }

    private final MediaPlaybackModel.Listener mListener = new MediaPlaybackModel.Listener() {

        @Override
        public void onMediaAppChanged(@Nullable ComponentName currentName, @Nullable ComponentName newName) {
            KLog.d("MrMine", "onMediaAppChanged: " + newName);
        }

        @Override
        public void onMediaAppStatusMessageChanged(@Nullable String message) {
            KLog.d("MrMine", "onMediaAppStatusMessageChanged: " + message);
        }

        @Override
        public void onMediaConnected() {
        }

        @Override
        public void onMediaConnectionSuspended() {
        }

        @Override
        public void onMediaConnectionFailed(CharSequence failedMediaClientName) {
            KLog.d("MrMine", "onMediaConnectionFailed: " + "");
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState playbackState) {
            onMusicStateChange(playbackState);
            for (OnBTMusicChangeListener musicInfoChangeListener : musicInfoChangeListeners) {
                if (musicInfoChangeListener != null) {
                    musicInfoChangeListener.currentBtMusic(getCurrBTMusic());
                }
            }
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metaData) {
            mCurrBTMusic = convertBtMusic(metaData);
            KLog.e(mCurrBTMusic.toString());
//            for (OnBTMusicChangeListener musicInfoChangeListener : musicInfoChangeListeners) {
//                if (musicInfoChangeListener != null) {
//                    musicInfoChangeListener.currentBtMusic(getCurrBTMusic());
//                }
//            }
            UploadPlayManager.getInstance().uploadBtPlayInfo(getCurrBTMusic());
        }

        @Override
        public void onQueueChanged(List<MediaSession.QueueItem> queue) {

        }

        @Override
        public void onSessionDestroyed(CharSequence destroyedMediaClientName) {
            KLog.d("MrMine", "onSessionDestroyed: " + "");
        }
    };

    private BTMusic convertBtMusic(@Nullable MediaMetadata metaData) {
        if (metaData == null) {
            return emptyMusic();
        }
        String artist = mContext.getString(R.string.unknow_artist);
        String title = TextUtils.isEmpty(metaData.getString(MediaMetadata.METADATA_KEY_TITLE))
                ? mContext.getString(R.string.unknow_song) : metaData.getString(MediaMetadata.METADATA_KEY_TITLE);
        String tempArtist = metaData.getString(MediaMetadata.METADATA_KEY_ARTIST);
        KLog.d("MrMine", "convertBtMusic: " + metaData.getString(MediaMetadata.METADATA_KEY_TITLE));
        if (!TextUtils.isEmpty(tempArtist)) {
            artist = tempArtist;//歌曲开始时传一次，后面不会传了
            if (artist.equals(mContext.getString(R.string.unknow_bt))) {
                artist = mContext.getString(R.string.unknow_artist);
            }
        } else {
            if (mCurrBTMusic != null && title.equals(mCurrBTMusic.getTitle())) {
                artist = mCurrBTMusic.getArtist();
            }
        }
        long duration = metaData.getLong(MediaMetadata.METADATA_KEY_DURATION);
        String album = metaData.getString(MediaMetadata.METADATA_KEY_ALBUM);//歌曲开始时是专辑名，后面就变成传歌词了
        if (duration == -1 && TextUtils.isEmpty(album) && TextUtils.isEmpty(metaData.getString(MediaMetadata.METADATA_KEY_TITLE))) {
            title = mContext.getString(R.string.have_no_song);
        }
        return new BTMusic(title, artist, duration, album);
    }

    @Override
    public void registerBluetoothMusicReceiver(Context context) {
        BluetoothReceiver receiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, intentFilter);
        receiver.setBluetoothStateCallback(this);
    }

    private void onMusicStateChange(PlaybackState playbackState) {
        if (playbackState == null) {
            return;
        }
        int playState = playbackState.getState();
        if (lastPlayState != playState) {
            KLog.d("onPlaybackStateChanged：" + playState);
            lastPlayState = playState;
            if (playState == STATE_PLAYING) {
                boolean success = mAudioFocusHelper.requestBluetoothMusicAudioFocus(AudioSource.BLUETOOTH_MUSIC);
                if (success) {
                    AudioShareManager.getInstance().shareBtAudioTypeChanged();
                    switchBtMusicAudioSource();
                } else {
                    KLog.e(TAG, "request audio focus failed");
                    ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchPlay(false);
                            pauseRender();
                        }
                    }, 3000);
                    return;
                }
            }
        }
        if (playState != currentState) {
            currentState = playState;
            switch (playState) {
                case STATE_NONE:
                    currentPlayStatus = PlayStatus.INIT;
                    break;
                case STATE_BUFFERING:
                    currentPlayStatus = PlayStatus.BUFFERING;
                    break;
                case STATE_PLAYING:
                    currentPlayStatus = PlayStatus.PLAYING;
                    dispatchPlayState();
                    break;
                case STATE_PAUSED:
                    currentPlayStatus = PlayStatus.PAUSE;
                    dispatchPauseState();
                    break;
                case STATE_STOPPED:
                    currentPlayStatus = PlayStatus.STOP;
                    dispatchStopState();
                    break;
                case STATE_ERROR:
                    currentPlayStatus = PlayStatus.FAILED;
                    dispatchStopState();
                    break;
            }
        }

        KLog.d("MrMine", "playState: " + playState + "  currentPlayStatus:" + currentPlayStatus);
    }

    private void dispatchStopState() {
        for (OnBTMusicChangeListener musicInfoChangeListener : musicInfoChangeListeners) {
            if (musicInfoChangeListener != null) {
                musicInfoChangeListener.onPlayStop();
            }
        }
    }

    private void dispatchPauseState() {
        for (OnBTMusicChangeListener musicInfoChangeListener : musicInfoChangeListeners) {
            if (musicInfoChangeListener != null) {
                musicInfoChangeListener.onPause();
            }
        }
    }

    private void dispatchPlayState() {
        for (OnBTMusicChangeListener musicInfoChangeListener : musicInfoChangeListeners) {
            if (musicInfoChangeListener != null) {
                musicInfoChangeListener.onPlay();
            }
        }
    }

    @Override
    public void addBtStateChangeListener(OnBTConnectStateChangeListener listener) {
        if (stateChangeListeners != null) {
            stateChangeListeners.add(listener);
        }
    }

    @Override
    public void removeBtStateChangeListener(OnBTConnectStateChangeListener listener) {
        if (stateChangeListeners != null) {
            stateChangeListeners.remove(listener);
        }
    }

    @Override
    public void addBtMusicInfoChangeListener(OnBTMusicChangeListener listener) {
        if (musicInfoChangeListeners != null) {
            musicInfoChangeListeners.add(listener);
        }
    }

    @Override
    public void removeBtMusicInfoChangeListener(OnBTMusicChangeListener listener) {
        if (musicInfoChangeListeners != null) {
            musicInfoChangeListeners.remove(listener);
        }
    }

    @Override
    public List<XMBluetoothDevice> getBTConnectDevice() {
        List<XMBluetoothDevice> xmBluetoothDevices = new ArrayList<>();
        if (mA2dpSinkService == null) {
            return xmBluetoothDevices;
        }
        List<BluetoothDevice> devices = mA2dpSinkService.getConnectedDevices();
        if (!ListUtils.isEmpty(devices)) {
            for (BluetoothDevice device : devices) {
                if (device == null) {
                    continue;
                }
                xmBluetoothDevices.add(new XMBluetoothDevice(device));
            }
        }
        return xmBluetoothDevices;
    }

    @Override
    public void switchPlay(boolean play) {
        RemoteIatManager.getInstance().uploadPlayState(play, AppType.MUSIC);
        doSwitchPlay(play, true);
    }

    private void doSwitchPlay(boolean play, boolean handleAudioFocus) {
        if (mMediaPlaybackModel != null) {
            MediaController.TransportControls transportControls =
                    mMediaPlaybackModel.getTransportControls();
            if (transportControls != null) {
                if (play) {
                    if (handleAudioFocus) {
                        boolean success = mAudioFocusHelper.requestBluetoothMusicAudioFocus(AudioSource.BLUETOOTH_MUSIC);
                        if (!success) {
                            KLog.e(TAG, "request audio focus failed");
                            return;
                        }
                    }
                    AudioShareManager.getInstance().shareBtAudioTypeChanged();
                    switchBtMusicAudioSource();
                    transportControls.play();
                    UploadPlayManager.getInstance().initPlayTime();
                } else {
                    KLog.d("BTControlManager", "doSwitchPlay: pause");
                    transportControls.pause();
                    if (handleAudioFocus) {
                        mAudioFocusHelper.abandonBtAudioFocus();
                    }
                    UploadPlayManager.getInstance().uploadPlayTime(PlayType.BLUETOOTHMUSIC.getPlayType());
                }
            }
        }
    }

    private void handlePlaybackStateForPlay(PlaybackState playbackState,
                                            MediaController.TransportControls transportControls) {
        if (playbackState == null) {
            return;
        }
        switch (playbackState.getState()) {
            case PlaybackState.STATE_PLAYING:
            case PlaybackState.STATE_BUFFERING:
                long actions = playbackState.getActions();
                if ((actions & PlaybackState.ACTION_PAUSE) != 0) {
                    transportControls.pause();
                    UploadPlayManager.getInstance().uploadPlayTime(PlayType.BLUETOOTHMUSIC.getPlayType());
                } else if ((actions & PlaybackState.ACTION_STOP) != 0) {
                    transportControls.stop();
                    UploadPlayManager.getInstance().uploadPlayTime(PlayType.BLUETOOTHMUSIC.getPlayType());
                }
                mAudioFocusHelper.abandonBtAudioFocus();
                break;
            default:
                boolean success = mAudioFocusHelper.requestBluetoothMusicAudioFocus(AudioSource.BLUETOOTH_MUSIC);
                if (!success) {
                    KLog.e(TAG, "request audio focus failed");
                    return;
                }
                AudioShareManager.getInstance().shareBtAudioTypeChanged();
                switchBtMusicAudioSource();
                transportControls.play();
                UploadPlayManager.getInstance().initPlayTime();
        }
    }

    @Override
    public void switchPlay() {
        if (mMediaPlaybackModel != null) {
            MediaController.TransportControls transportControls =
                    mMediaPlaybackModel.getTransportControls();
            if (transportControls != null)
                handlePlaybackStateForPlay(mMediaPlaybackModel.getPlaybackState(),
                        transportControls);
        }
    }

    @Override
    public void nextMusic() {
        if (mMediaPlaybackModel == null) {
            return;
        }
        MediaController.TransportControls transportControls =
                mMediaPlaybackModel.getTransportControls();
        if (transportControls != null)
            transportControls.skipToNext();
        UploadPlayManager.getInstance().initPlayTime();
    }

    @Override
    public void preMusic() {
        if (mMediaPlaybackModel == null) {
            return;
        }
        MediaController.TransportControls transportControls =
                mMediaPlaybackModel.getTransportControls();
        if (transportControls != null)
            transportControls.skipToPrevious();
        UploadPlayManager.getInstance().initPlayTime();
    }

    @Override
    public void seek(long seek) {
        if (mMediaPlaybackModel == null) {
            return;
        }
        MediaController.TransportControls transportControls =
                mMediaPlaybackModel.getTransportControls();
        if (transportControls != null)
            transportControls.seekTo(seek);
    }

    @Override
    public boolean isPlaying(XMBluetoothDevice device) {
        if (mA2dpSinkService == null) {
            return false;
        }
        return mA2dpSinkService.isA2dpPlaying(device.getSDKBean());
    }

    @Override
    public boolean isPlaying() {
        if (currentPlayStatus == PlayStatus.BUFFERING
                || currentPlayStatus == PlayStatus.PLAYING) {
            return true;
        }
        if (mA2dpSinkService != null) {
            List<BluetoothDevice> connectedDevices = mA2dpSinkService.getConnectedDevices();
            if (!ListUtils.isEmpty(connectedDevices)) {
                return mA2dpSinkService.isA2dpPlaying(connectedDevices.get(0));
            }
        }
        return false;
    }

    private BluetoothProfile.ServiceListener mAvrcpServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.AVRCP_CONTROLLER) {
                mAvrcpController = (BluetoothAvrcpController) proxy;
                KLog.d(TAG, "AvrcpControllerService connected" + mAvrcpController);
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.AVRCP_CONTROLLER) {
                KLog.d(TAG, "AvrcpControllerService disconnected");
                mAvrcpController = null;
            }
        }
    };

    private BluetoothProfile.ServiceListener mHeadsetServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET_CLIENT) {
                mHeadsetService = (BluetoothHeadsetClient) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET_CLIENT) {
                mHeadsetService = null;
            }
        }
    };


    private BluetoothProfile.ServiceListener mA2dpSinkServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP_SINK) {
                mA2dpSinkService = (BluetoothA2dpSink) proxy;
                KLog.d(TAG, "mA2dpSinkService connected" + mA2dpSinkService);
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP_SINK) {
                KLog.d(TAG, "mA2dpSinkService disconnected");
                mA2dpSinkService = null;
            }
        }
    };

    public boolean isAudioFocusLossTransient() {
        return mAudioFocusHelper.isAudioFocusLossTransient();
    }

}
