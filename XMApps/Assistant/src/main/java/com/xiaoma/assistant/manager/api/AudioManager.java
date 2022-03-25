package com.xiaoma.assistant.manager.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.bluetooth.BluetoothA2dpSinkExt;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/19 18:02
 * Desc:
 */
public class AudioManager {

    private int audioType = AudioConstants.AudioTypes.NONE; //当前音源类型
    private static final String TAG = "QBX";
    private Context context;
    private int mUsbState;
    private int mBluetoothState;
    private static SparseArray<String> mUsbStatusArray = new SparseArray<>();
    private static SparseArray<String> mBluetoothStatusArray = new SparseArray<>();
    private static AudioManager mInstance;
    private int usbMusicPlayState = AudioConstants.AudioStatus.STOPPED;
    private int kuwoMusicPlayState = AudioConstants.AudioStatus.STOPPED;
    private int bluetoothMusicPlayState = AudioConstants.AudioStatus.STOPPED;
    private int localFMPlayState = AudioConstants.AudioStatus.STOPPED;
    private int onlineFMPlayState = AudioConstants.AudioStatus.STOPPED;
    private BluetoothA2dpSinkExt bluetoothA2dpSinkExt;
    private boolean isMusicPlaying;

    private AudioManager() {
        registerUsbState();

    }

    private void registerUsbState() {
        UsbDetector.getInstance().addUsbDetectListener(new UsbDetector.UsbDetectListener() {

            @Override
            public void noUsbMounted() {
                mUsbState = AudioConstants.ConnectStatus.USB_NOT_MOUNTED;
            }

            @Override
            public void inserted() {

            }

            @Override
            public void mounted(List<String> mountPaths) {
                mUsbState = AudioConstants.ConnectStatus.USB_MOUNTED;
            }

            @Override
            public void mountError() {
                mUsbState = AudioConstants.ConnectStatus.USB_UNSUPPORT;
            }

            @Override
            public void removed() {
                mUsbState = AudioConstants.ConnectStatus.USB_REMOVE;
            }
        });
        UsbDetector.getInstance().init(AppHolder.getInstance().getAppContext());
    }

    public static AudioManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager();
                }
            }
        }
        return mInstance;
    }

    static {
        mUsbStatusArray.put(AudioConstants.ConnectStatus.USB_MOUNTED, "USB_MOUNTED");
        mUsbStatusArray.put(AudioConstants.ConnectStatus.USB_NOT_MOUNTED, "USB_NOT_MOUNTED");
        mUsbStatusArray.put(AudioConstants.ConnectStatus.USB_REMOVE, "USB_REMOVE");
        mUsbStatusArray.put(AudioConstants.ConnectStatus.USB_SCAN_FINISH, "USB_SCAN_FINISH");
        mUsbStatusArray.put(AudioConstants.ConnectStatus.USB_UNSUPPORT, "USB_UNSUPPORT");

        mBluetoothStatusArray.put(AudioConstants.ConnectStatus.BLUETOOTH_CONNECTED, "BLUETOOTH_CONNECTED");
        mBluetoothStatusArray.put(AudioConstants.ConnectStatus.BLUETOOTH_DISCONNECTED, "BLUETOOTH_DISCONNECTED");
        mBluetoothStatusArray.put(AudioConstants.ConnectStatus.BLUETOOTH_SINK_CONNECTED, "BLUETOOTH_SINK_CONNECTED");
        mBluetoothStatusArray.put(AudioConstants.ConnectStatus.BLUETOOTH_SINK_DISCONNECTED, "BLUETOOTH_SINK_DISCONNECTED");
    }

    public int getUsbState() {
        return mUsbState;
    }

    public int getBluetoothState() {
        return mBluetoothState;
    }

    public int getAudioType() {
        printAudioType("getAudioType", audioType);
        return audioType;
    }

    public void setAudioType(int audioType) {
        this.audioType = audioType;
    }

    class Callback extends IClientCallback.Stub {

        private int port;

        public Callback(int port) {
            this.port = port;
        }

        @Override
        public void callback(Response response) throws RemoteException {
            Bundle extra = response.getExtra();
            extra.setClassLoader(ProgressInfo.class.getClassLoader());
            String action = extra.getString(AudioConstants.BundleKey.ACTION);
            switch (action) {
                case AudioConstants.BundleKey.AUDIO_PROGRESS:
                    if (port != RadioApiManager.STATE_CALLBACK_PORT) {
                        ProgressInfo progressInfo = extra.getParcelable(action);
                        audioType = progressInfo.getAudioType();
                        updatePlayState(AudioConstants.AudioStatus.PLAYING, audioType);
                    }
                    break;
                case AudioConstants.BundleKey.AUDIO_STATE:
                    int playState = extra.getInt(action);
                    updatePlayState(playState);
                    break;
                case AudioConstants.BundleKey.AUDIO_INFO:
                    AudioInfo mAudioInfo = extra.getParcelable(AudioConstants.BundleKey.AUDIO_INFO);
                    if (mAudioInfo != null) {
                        int currentAudioType = mAudioInfo.getAudioType();
                        if ((currentAudioType == AudioConstants.AudioTypes.XTING_NET_FM || currentAudioType == AudioConstants.AudioTypes.XTING_KOALA_ALBUM) && mAudioInfo.isHistory()) {
                            return;
                        }
                        if (audioType != currentAudioType) {
                            printAudioType("callback", currentAudioType);
                        }
                        audioType = currentAudioType;
                    }
                    break;
                case AudioConstants.BundleKey.AUDIO_TYPE:
                    int currentAudioType = extra.getInt(action);
                    if (currentAudioType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO || currentAudioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB || currentAudioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
                        if (audioType != currentAudioType) {
                            printAudioType("callback from shareAudioTypeToLauncher", currentAudioType);
                        }
                        audioType = currentAudioType;
                    }
                    break;
                case AudioConstants.BundleKey.AUDIO_FAVORITE:
                    boolean audioFavorite = extra.getBoolean(action);
//                    Log.d(TAG, "callback: favorite: " + audioFavorite);
                    break;
                case AudioConstants.BundleKey.AUDIO_PLAYMODE:
                    int mCurrentPlayMode = extra.getInt(action);
//                    Log.d(TAG, "callback: playMode: " + mCurrentPlayMode);
                    break;
                case AudioConstants.BundleKey.AUDIO_DATA_SOURCE:
                    int dataSource = extra.getInt(action);
//                    Log.d(TAG, "callback: dataSource: " + dataSource);
                    break;
                case AudioConstants.BundleKey.CONNECT_STATE:
                    int state = extra.getInt(action);
//                    Log.d(TAG, "callback: connectState: " + state);
                    String stateText;
                    if ((stateText = mUsbStatusArray.get(state)) != null) {
                        mUsbState = state;
                        Log.d(TAG, "callback:  " + StringUtil.format(context.getString(R.string.usb_connect_state), stateText));
                    } else if ((stateText = mBluetoothStatusArray.get(state)) != null) {
                        mBluetoothState = state;
                        Log.d(TAG, "callback: " + StringUtil.format(context.getString(R.string.bluetooth_connect_state), stateText));
                    }
                    break;
                default:
                    break;
            }
        }

        private void updatePlayState(int playState) {
            switch (port) {
                case MusicApiManager.USB_PORT:
                    if (playState == AudioConstants.AudioStatus.PLAYING) {
                        audioType = AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
                    }
                    updatePlayState(playState, port);
                    break;
                case MusicApiManager.KUWO_PORT:
                    if (playState == AudioConstants.AudioStatus.PLAYING) {
                        audioType = AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
                    }
                    updatePlayState(playState, port);
                    break;
                case MusicApiManager.BLUETOOTH_PORT:
                    if (playState == AudioConstants.AudioStatus.PLAYING) {
                        audioType = AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
                    }
                    updatePlayState(playState, port);
                    break;
               /* case RadioApiManager.STATE_CALLBACK_PORT:
                    if (playState == AudioConstants.AudioStatus.LOADING) {
                        return;
                    }
                    if (isLocalFMType()) {
                        localFMPlayState = playState;
                        Log.d(TAG, "localFMPlayState: " + getPlayStateText(playState));
                    } else if (isOnlineFMType()) {
                        onlineFMPlayState = playState;
                        Log.d(TAG, "onlineFMPlayState: " + getPlayStateText(playState));
                    }
                    break;*/
            }
        }

        private void updatePlayState(int playState, int currentAudioType) {
            switch (currentAudioType) {
                case MusicApiManager.USB_PORT:
                    if (usbMusicPlayState != playState) {
                        Log.d(TAG, "updatePlayState: " + getPlayStateText(playState));
                    }
                    usbMusicPlayState = playState;
                    break;
                case MusicApiManager.KUWO_PORT:
                    if (kuwoMusicPlayState != playState) {
                        Log.d(TAG, "kuwoMusicPlayState: " + getPlayStateText(playState));
                    }
                    kuwoMusicPlayState = playState;
                    break;
                case MusicApiManager.BLUETOOTH_PORT:
                    if (bluetoothMusicPlayState != playState) {
                        Log.d(TAG, "bluetoothMusicPlayState: " + getPlayStateText(playState));
                    }
                    bluetoothMusicPlayState = playState;
                    break;
                /*case AudioConstants.AudioTypes.XTING_LOCAL_FM:
                    localFMPlayState = playState;
                    Log.d(TAG, "localFMPlayState: " + getPlayStateText(playState));
                    break;
                case AudioConstants.AudioTypes.XTING_NET_FM:
                case AudioConstants.AudioTypes.XTING_NET_RADIO:
                case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
                    onlineFMPlayState = playState;
                    Log.d(TAG, "onlineFMPlayState: " + getPlayStateText(playState));
                    break;*/
            }
        }
    }

    private String getPlayStateText(int playState) {
        switch (playState) {
            case AudioConstants.AudioStatus.PLAYING:
                return "PLAYING";
            case AudioConstants.AudioStatus.PAUSING:
                return "PAUSING";
            case AudioConstants.AudioStatus.LOADING:
                return "LOADING";
            case AudioConstants.AudioStatus.STOPPED:
                return "STOPPED";
            case AudioConstants.AudioStatus.EXIT:
                return "EXIT";
            case AudioConstants.AudioStatus.ERROR:
                return "ERROR";
            default:
                return "";
        }
    }

    public boolean isUsbMusicType() {
        return audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
    }

    public boolean isKuwoMusicType() {
        return audioType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
    }

    public boolean isBluetoothMusicType() {
        return audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
    }

    public boolean isUsbMusicPlaying() {
        return isUsbMusicType() && isMusicPlaying;
    }

    public boolean isKuwoMusicPlaying() {
        return isKuwoMusicType() && isMusicPlaying;
    }

    public boolean isBluetoothMusicPlaying() {
        return isBluetoothMusicType() && isMusicPlaying;
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public boolean isMusicPause() {
        return !isMusicPlaying && isMusicType();
    }

    public boolean isMusicType() {
        return (isKuwoMusicType() || isUsbMusicType() || isBluetoothMusicType());
    }

    public boolean isOnlineFMPlaying() {
        return isPlaying(onlineFMPlayState);
    }

    public boolean isLocalFMPlaying() {
        return isPlaying(localFMPlayState);
    }

    public boolean isFMPlaying() {
        return isOnlineFMPlaying() || isLocalFMPlaying();
    }

    public boolean isMediaPlaying() {
        return isUsbMusicPlaying() || isKuwoMusicPlaying() || isBluetoothMusicPlaying() || isOnlineFMPlaying() || isLocalFMPlaying();
    }

    public boolean playerPauseMediaPre() {
        boolean result = false;
        if (isMusicPause() || isMusicType()) {
            result = true;
            MusicApiManager.getInstance().past();
        } else if (isLocalFMPause() || isLocalFMType()) {
            result = true;
            RadioApiManager.getInstance().preRadioStation();
        } else if (isOnlineFMPause() || isOnlineFMType()) {
            result = true;
            RadioApiManager.getInstance().preProgram();
        }
        return result;
    }

    public boolean playerPauseMediaNext() {
        boolean result = false;
        if (isMusicPause() || isMusicType()) {
            result = true;
            MusicApiManager.getInstance().next(null);
        } else if (isLocalFMPause() || isLocalFMType()) {
            result = true;
            RadioApiManager.getInstance().nextRadioStation("");
        } else if (isOnlineFMPause() || isOnlineFMType()) {
            result = true;
            RadioApiManager.getInstance().nextProgram();
        }
        return result;
    }

    private boolean isOnlineFMPause() {
        return isOnlineFMType() && onlineFMPlayState == AudioConstants.AudioStatus.PAUSING;
    }

    private boolean isLocalFMPause() {
        return isLocalFMType() && localFMPlayState == AudioConstants.AudioStatus.PAUSING;
    }

    private boolean isPlaying(int state) {
        return state == AudioConstants.AudioStatus.PLAYING;
    }

    public boolean isUsbConnected() {
        return mUsbState == AudioConstants.ConnectStatus.USB_MOUNTED || mUsbState == AudioConstants.ConnectStatus.USB_SCAN_FINISH;
    }

    public boolean isBluetoothConnected() {
        return bluetoothA2dpSinkExt.isConnected();
    }

    public boolean isDeviceDisconnected() {
        return (isUsbMusicType() && !isUsbConnected()) || (isBluetoothMusicType() && !isBluetoothConnected());
    }

    public boolean haveMediaPlayRecord() {
        return audioType != AudioConstants.AudioTypes.NONE;
    }

    public boolean isLocalFMType() {
        return audioType == AudioConstants.AudioTypes.XTING_LOCAL_FM;
    }

    public boolean isOnlineFMType() {
        return audioType == AudioConstants.AudioTypes.XTING_NET_FM || audioType == AudioConstants.AudioTypes.XTING_NET_RADIO || audioType == AudioConstants.AudioTypes.XTING_KOALA_ALBUM;
    }

    private void printAudioType(String text, int audioType) {
        switch (audioType) {
            case AudioConstants.AudioTypes.NONE:
                Log.d(TAG, text + " AudioConstants.AudioTypes.NONE");
                break;
            case AudioConstants.AudioTypes.XTING_NET_FM:
                Log.d(TAG, text + " AudioConstants.AudioTypes.XTING_NET_FM");
                break;
            case AudioConstants.AudioTypes.XTING_LOCAL_FM:
                Log.d(TAG, text + " AudioConstants.AudioTypes.XTING_LOCAL_FM");
                break;
            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
                Log.d(TAG, text + " AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO");
                break;
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                Log.d(TAG, text + " AudioConstants.AudioTypes.MUSIC_LOCAL_USB");
                break;
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                Log.d(TAG, text + " AudioConstants.AudioTypes.MUSIC_LOCAL_BT");
                break;
            case AudioConstants.AudioTypes.XTING_NET_RADIO:
                Log.d(TAG, text + " AudioConstants.AudioTypes.XTING_NET_RADIO");
                break;
            case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
                Log.d(TAG, text + " AudioConstants.AudioTypes.XTING_KOALA_ALBUM");
                break;
        }
    }

    public void initAudioType() {
        SourceType playType = SourceUtils.getPlayType();
        if (playType != SourceType.NONE) {
            setAudioState(playType, true, true);
        } else {
            setAudioState(SourceUtils.getPauseType(), false, true);
        }
    }

    private void setAudioState(SourceType audioType, boolean isPlaying, boolean upload) {
        int audioState = isPlaying ? AudioConstants.AudioStatus.PLAYING : AudioConstants.AudioStatus.STOPPED;
        switch (audioType) {
            case AM:
            case FM:
                this.audioType = AudioConstants.AudioTypes.XTING_LOCAL_FM;
                localFMPlayState = audioState;
                if (isPlaying && upload) {
                    RemoteIatManager.getInstance().uploadPlayState(isPlaying, AppType.RADIO);
                }
                break;
            case NET_FM:
                this.audioType = AudioConstants.AudioTypes.XTING_NET_FM;
                onlineFMPlayState = audioState;
                if (isPlaying && upload) {
                    RemoteIatManager.getInstance().uploadPlayState(isPlaying, AppType.INTERNET_RADIO);
                }
                break;
            case NET_MUSIC:
                this.audioType = AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
                kuwoMusicPlayState = audioState;
                isMusicPlaying = isPlaying;
                if (isPlaying && upload) {
                    uploadMusicPlayState(isPlaying);
                }
                break;
            case BT_MUSIC:
                this.audioType = AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
                bluetoothMusicPlayState = audioState;
                isMusicPlaying = isPlaying;
                if (isPlaying && upload) {
                    uploadMusicPlayState(isPlaying);
                }
                break;
            case USB_MUSIC:
                this.audioType = AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
                usbMusicPlayState = audioState;
                isMusicPlaying = isPlaying;
                if (isPlaying && upload) {
                    uploadMusicPlayState(isPlaying);
                }
                break;
        }
    }

    private void uploadMusicPlayState(boolean isPlaying) {
        RemoteIatManager.getInstance().uploadPlayState(isPlaying, AppType.MUSIC);
    }

    public void connectAudioClient(ApiManager apiManager, int port) {
        apiManager.connect(port, AudioConstants.Action.PLAYER_CONNECT, null, new Callback(port));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(VrConstants.Actions.UPLOAD_PLAY_STATE)) {
                boolean isPlaying = intent.getBooleanExtra(VrConstants.ActionExtras.PLAY_STATE, false);
                String appType = intent.getStringExtra(VrConstants.ActionExtras.APP_TYPE);
                switch (appType) {
                    case AppType.RADIO:
                        setAudioState(SourceType.FM, isPlaying, false);
                        break;
                    case AppType.INTERNET_RADIO:
                        setAudioState(SourceType.NET_FM, isPlaying, false);
                        break;
                    case AppType.MUSIC:
                        isMusicPlaying = isPlaying;
                        break;
                }
            }
        }
    };

    public void init(Context context) {
        Log.d(TAG, "AudioManager init =》 connectAudioClient");
        this.context = context;
        connectAudioClient(MusicApiManager.getInstance(), MusicApiManager.USB_PORT);
        connectAudioClient(MusicApiManager.getInstance(), MusicApiManager.KUWO_PORT);
        connectAudioClient(MusicApiManager.getInstance(), MusicApiManager.BLUETOOTH_PORT);
        connectAudioClient(RadioApiManager.getInstance(), RadioApiManager.STATE_CALLBACK_PORT);
        registerReceiver();
        bluetoothA2dpSinkExt = new BluetoothA2dpSinkExt(context);
        bluetoothA2dpSinkExt.init();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VrConstants.Actions.UPLOAD_PLAY_STATE);
        context.registerReceiver(receiver, filter);
    }

}
