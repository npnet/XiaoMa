package com.xiaoma.xting.common.playerSource.control;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.sdk.LocalFMFactory;

import java.util.Objects;

public class AudioFocusManager {
    private static final String TAG = AudioFocusManager.class.getSimpleName();

    public static final String ARG_AUDIO_SOURCE = "audio_source";
    public static final String ATTR_AUDIO_HIMALAYAN = "HIMALAYAN";
    public static final String ATTR_AUDIO_KOALA = "KOALA";
    public static final String ATTR_AUDIO_YQ = "YQ_RADIO";
    private static AudioFocusManager instance;
    private final AudioManager mAudioManager;
    private AudioFocusRequest mXmlyFocusReq = null;
    private AudioFocusRequest mKoalaFocusReq = null;
    private AudioFocusRequest mFMFocusReq = null;
    private int mFocusAudioType;//当前获取焦点的音源，0-无音源，1-xmly，2-koala，3-fm
    private int mRestoreAudioType;//暂时失去焦点的音源，0-无音源，1-xmly，2-koala，3-fm
    private boolean isLossByOther;
    private IPlayerControl playerControl;
    private final AudioAttributes koalaAttr;
    private final AudioAttributes himalayaAttr;
    private final AudioAttributes yqAttr;

    private final AudioManager.OnAudioFocusChangeListener mXmlyFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //获得焦点
                    mFocusAudioType = 1;
                    if (mRestoreAudioType == 1 && playerControl != null) {
                        playerControl.play();
                    }
                    Log.d(TAG, "onAudioFocusChange: xmly-GAIN");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 0;
//                    playerControl.pause();
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.HIMALAYAN).pause();
                    Log.d(TAG, "onAudioFocusChange: xmly-LOSS");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 1;
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.HIMALAYAN).pause(false);
                    Log.d(TAG, "onAudioFocusChange: xmly-LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    //暂时失去焦点，低音量
//                    mFocusAudioType = 0;
//                    mRestoreAudioType = 1;
//                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.HIMALAYAN).pause(false);
//                    Log.d(TAG, "onAudioFocusChange: xmly-LOSS_CAN_DUCK");
                    break;
            }
        }
    };
    private final AudioManager.OnAudioFocusChangeListener mKoalaFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //获得焦点
                    mFocusAudioType = 2;
                    if (mRestoreAudioType == 2 && playerControl != null) {
                        playerControl.play();
                    }
                    Log.d(TAG, "onAudioFocusChange: Koala-GAIN");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 0;
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.KOALA).pause();
                    Log.d(TAG, "onAudioFocusChange: Koala-LOSS");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 2;
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.KOALA).pause(false);
                    Log.d(TAG, "onAudioFocusChange: Koala-LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //暂时失去焦点，低音量
//                    mFocusAudioType = 0;
//                    mRestoreAudioType = 2;
//                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.KOALA).pause(false);
//                    Log.d(TAG, "onAudioFocusChange: Koala-LOSS_CAN_DUCK");
                    break;
            }

        }
    };
    private final AudioManager.OnAudioFocusChangeListener mFMFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //获得焦点
                    mFocusAudioType = 3;
                    LocalFMFactory.getSDK().setMuted(false);
                    if (!LocalFMFactory.getSDK().isRadioOpen()) {
                        LocalFMFactory.getSDK().openRadio();
                    }
                    Log.d(TAG, "onAudioFocusChange: FM-GAIN");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 0;
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.RADIO_YQ).exitPlayer();
                    abandonFMFocus();
                    Log.d(TAG, "onAudioFocusChange: FM-LOSS");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去焦点
                    mFocusAudioType = 0;
                    mRestoreAudioType = 3;
                    LocalFMFactory.getSDK().setMuted(true);
                    LocalFMFactory.getSDK().cancel();
                    Log.d(TAG, "onAudioFocusChange: FM-LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //暂时失去焦点，低音量
//                    mFocusAudioType = 0;
//                    mRestoreAudioType = 3;
//                    LocalFMFactory.getSDK().setMuted(true);
                    LocalFMFactory.getSDK().cancel();
//                    Log.d(TAG, "onAudioFocusChange: FM-LOSS_CAN_DUCK");
//                    break;
                default:
            }
        }
    };

    public static AudioFocusManager getInstance() {
        if (instance == null) {
            synchronized (AudioFocusManager.class) {
                if (instance == null) {
                    instance = new AudioFocusManager(AppHolder.getInstance().getAppContext());
                }
            }
        }
        return instance;
    }

    public int getAudioType() {
        return mFocusAudioType;
    }

    public boolean requestKoalaFocus() {
        if (mAudioManager == null) return false;
        //发现存在未获取焦点，是因为没有走进这里导致，所以注释掉
//        if (mFocusAudioType == 2) return true;
        int result;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = mAudioManager.requestAudioFocus(mKoalaFocusReq);
        } else {
            result = mAudioManager.requestAudioFocus(mKoalaFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        //faile AUDIOFOCUS_REQUEST_FAILED
        //不处理 AUDIOFOCUS_REQUEST_DELAYED
        boolean b = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        mFocusAudioType = (b ? 2 : 0);
        Log.d(TAG, "requestKoalaFocus: " + b);
        if (!b) {
            XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.can_not_gain_focus);
        }
        return b;
    }

    public void abandonKoalaFocus() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mKoalaFocusReq);
        } else {
            mAudioManager.abandonAudioFocus(mKoalaFocusChangeListener, koalaAttr);
        }
    }

    public boolean requestXmlyFocus() {
        if (mAudioManager == null) return false;
//        if (mFocusAudioType == 1) return true;
        int result;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = mAudioManager.requestAudioFocus(mXmlyFocusReq);
        } else {
            result = mAudioManager.requestAudioFocus(mXmlyFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        //faile AUDIOFOCUS_REQUEST_FAILED
        //不处理 AUDIOFOCUS_REQUEST_DELAYED
        boolean b = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        Log.d(TAG, "requestXmlyFocus: " + b);
        mFocusAudioType = (b ? 1 : 0);
        if (!b) {
            XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.can_not_gain_focus);
        }
        return b;
    }

    public void abandonXmlyFocus() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mXmlyFocusReq);
        } else {
            mAudioManager.abandonAudioFocus(mXmlyFocusChangeListener, himalayaAttr);
        }
    }

    public boolean requestFMFocus() {
        if (mAudioManager == null) return false;
        //        if (mFocusAudioType == 3) return true;
        int result;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = mAudioManager.requestAudioFocus(mFMFocusReq);
        } else {
            result = mAudioManager.requestAudioFocus(mFMFocusChangeListener,
                    AudioManager.STREAM_RADIO, AudioManager.AUDIOFOCUS_GAIN);
        }
        //faile AUDIOFOCUS_REQUEST_FAILED
        //不处理 AUDIOFOCUS_REQUEST_DELAYED
        boolean b = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        Log.d(TAG, "requestFMFocus: " + b);
        if (!b) {
            XMToast.toastException(AppHolder.getInstance().getAppContext(), R.string.can_not_gain_focus);
        }
        mFocusAudioType = (b ? 3 : 0);
        return b;
    }

    public void abandonFMFocus() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mFMFocusReq);
        } else {
            mAudioManager.abandonAudioFocus(mFMFocusChangeListener, yqAttr);
        }
    }

    public void setPlayerControl(IPlayerControl playerControl) {
        if (playerControl != null) {
            Log.d(TAG, "setPlayerControl: " + playerControl.getClass().getSimpleName());
        }
        this.playerControl = playerControl;
    }

    public int getTempAudioFocusLoss() {
        return mRestoreAudioType;
    }

    private AudioFocusManager(Context context) {
        // 获取系统的音频管理器
        mAudioManager = Objects.requireNonNull((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));

        // 申请音频焦点的参数
        himalayaAttr = createAudioAttr(ATTR_AUDIO_HIMALAYAN, AudioManager.STREAM_MUSIC);
        koalaAttr = createAudioAttr(ATTR_AUDIO_KOALA, AudioManager.STREAM_MUSIC);
        yqAttr = createAudioAttr(ATTR_AUDIO_YQ, AudioManager.STREAM_RADIO);

        //获取焦点请求
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mXmlyFocusReq = createAudioRequest(himalayaAttr, mXmlyFocusChangeListener);
            mKoalaFocusReq = createAudioRequest(koalaAttr, mKoalaFocusChangeListener);
            mFMFocusReq = createAudioRequest(yqAttr, mFMFocusChangeListener);
        }
    }

    public void setLossByOther(boolean isLossByOther) {
        this.isLossByOther = isLossByOther;
    }

    public boolean getLossByOther() {
        return isLossByOther;
    }

    private AudioAttributes createAudioAttr(String audioSource, int streamType) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AUDIO_SOURCE, audioSource);
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(streamType)
                .addBundle(bundle)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private AudioFocusRequest createAudioRequest(AudioAttributes audioAttributes, AudioManager.OnAudioFocusChangeListener listener) {
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(listener)
                .build();
    }
}
