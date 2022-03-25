package com.xiaoma.instructiondistribute.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.audiopolicy.AudioPolicy;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.xiaoma.instructiondistribute.contract.AudioSourceType;
import com.xiaoma.instructiondistribute.listener.IAudioSourceTypeListener;

import java.util.Objects;

/**
 * <des>
 * 音频 管理
 *
 * @author YangGang
 * @date 2019/7/9
 */
public class EOLAudioFocusManager {

    public static final String TAG = "EOL";

    public static final String KEY_AUDIO_SOURCE = "audioSource.eol.key";

    /**********数据来源于Music应用,切勿随便修改***********/
    public static final String ARG_AUDIO_SOURCE_MUSIC = "audio_focus_source_key";
    public static final int MUSIC_TYPE_USB = 2;
    public static final int MUSIC_TYPE_BT = 3;
    /********************/

    /**********数据来源于Xting应用,切勿随便修改***********/
    public static final String ARG_AUDIO_SOURCE_RADIO = "audio_source";
    public static final String ATTR_AUDIO_HIMALAYAN = "HIMALAYAN";
    public static final String ATTR_AUDIO_KOALA = "KOALA";
    public static final String ATTR_AUDIO_YQ = "YQ_RADIO";

    /********************/

    private static EOLAudioFocusManager sAudioFocusManager;
    private Context mAppContext;
    private AudioManager mAudioManager;

    private AudioSourceType mCurSourceType;
    private AudioFocusRequest mCurAudioRequest;
    private AudioFocusChangedListener mAudioFocusChangeListener;

    private IAudioSourceTypeListener mSourceTypeListener;

    public static EOLAudioFocusManager newSingleton(Context context) {
        if (sAudioFocusManager == null) {
            synchronized (EOLAudioFocusManager.class) {
                if (sAudioFocusManager == null) {
                    sAudioFocusManager = new EOLAudioFocusManager(context);
                }
            }
        }
        return sAudioFocusManager;
    }

    private EOLAudioFocusManager(Context context) {
        mCurSourceType = AudioSourceType.DEFAULT;

        mAppContext = context.getApplicationContext();
        mAudioManager = Objects.requireNonNull((AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE));

        listenFocusChange();
    }


    /**
     * 已经有焦点被获取的时候,直接调用这个方法获取当前的焦点类型
     *
     * @return {@link AudioSourceType}
     */
    public AudioSourceType getCurSourceType() {
        return mCurSourceType;
    }

    /**
     * [WARN] 请求焦点 如果是再应用内进行管理,就调用这个,否则,请不要调用!!!!
     *
     * @param sourceType {@link AudioSourceType}
     */
    public boolean requestAudioFocus(AudioSourceType sourceType) {
        boolean focusChanged = initialFocusChanged(sourceType);
        if (!focusChanged) {
            return true;
        }

        int rlt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mCurAudioRequest == null) {
                mCurAudioRequest = createRequest(createAttrs(sourceType));
            }
            rlt = mAudioManager.requestAudioFocus(mCurAudioRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mAudioFocusChangeListener, sourceType.getStream(), AudioManager.AUDIOFOCUS_GAIN);
        }

        Log.d(TAG, "<requestAudioFocus> Focus Granted State = " + AudioManager.AUDIOFOCUS_REQUEST_GRANTED + " [" + rlt + "]");
        return rlt == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * 放弃焦点
     */
    public void abandonAudioFocus() {
        int rlt = Integer.MIN_VALUE;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mCurAudioRequest != null) {
                    rlt = mAudioManager.abandonAudioFocusRequest(mCurAudioRequest);
                }
            } else {
                if (mAudioFocusChangeListener != null) {
                    rlt = mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "<abandonAudioFocus> Focus Abandon State = " + AudioManager.AUDIOFOCUS_REQUEST_FAILED + " [" + rlt + "]");
            //默认显示 undefined mode 音源
            mCurSourceType = AudioSourceType.UNKNOWN_MODE;
            mCurAudioRequest = null;
            mAudioFocusChangeListener = null;
        }
    }

    /**
     * 调用获取某种音源的焦点的时候,使用这个监听获取当前焦点
     *
     * @param listener
     * @see EOLAudioFocusManager#requestAudioFocus(AudioSourceType) 获取焦点方法
     */
    public void setOnAudioSourceTypeListener(IAudioSourceTypeListener listener) {
        if (!AudioSourceType.DEFAULT.equals(mCurSourceType)) {
            listener.onAudioSourceTypeGranted(mCurSourceType);
        }
        mSourceTypeListener = listener;
    }

    private boolean initialFocusChanged(AudioSourceType sourceType) {
        Log.d(TAG, "<initialFocusChanged> AudioSourceType [new : " + sourceType + "] vs [cur : " + mCurSourceType + "]");
        if (mAudioFocusChangeListener == null || !mAudioFocusChangeListener.mAudioSourceType.equals(sourceType)) {
            abandonAudioFocus();
            mAudioFocusChangeListener = new AudioFocusChangedListener(sourceType);
            return true;
        }
        return false;
    }

    private void dispatchAudioSourceGranted(AudioSourceType audioSourceType) {
        if (mSourceTypeListener != null) {
            mSourceTypeListener.onAudioSourceTypeGranted(audioSourceType);
        }
    }

    /**
     * 监听音源焦点变化
     */
    private void listenFocusChange() {
        mAudioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        AudioPolicy.Builder builder = new AudioPolicy.Builder(mAppContext);
        builder.setAudioPolicyFocusListener(new AudioPolicy.AudioPolicyFocusListener() {
            @Override
            public void onAudioFocusGrant(AudioFocusInfo afi, int requestResult) {
                super.onAudioFocusGrant(afi, requestResult);
                if (afi != null) {
                    String packageName = afi.getPackageName();
                    AudioAttributes attributes = afi.getAttributes();
                    Log.d(TAG, "<onAudioFocusGrant>[" + packageName + "_" + attributes.getBundle() + "]");
                    Bundle bundle = attributes.getBundle();
                    if (DistributeConstants.PKG_INSTRCTION_DISTRIBUTE.equals(packageName)) { //如果是在本应用中进行焦点管理
                        handleAudioFocusInternal(bundle);
                    } else {
                        //下发到指定APP进行焦点管理
                        if (DistributeConstants.MUSIC.equals(packageName)) { //音乐部分的
                            handleMusic(bundle);
                        } else if (DistributeConstants.XTING.equals(packageName)) { //电台部分的
                            handleRadio(bundle);
                        }
                    }
                }
            }

            private void handleRadio(Bundle bundle) {
                if (bundle != null) {
                    String audioSource = bundle.getString(ARG_AUDIO_SOURCE_RADIO);
                    if (ATTR_AUDIO_YQ.equals(audioSource)) {
                        //由于FM 和AM是公用同一个音频源,所以这里用FM表示为本地电台
                        dispatchAudioSourceGranted(AudioSourceType.FM);
                    } else {
                        dispatchAudioSourceGranted(AudioSourceType.UNKNOWN_MODE);
                    }
                }
            }

            private void handleMusic(Bundle bundle) {
                if (bundle != null) {
                    int audioType = bundle.getInt(ARG_AUDIO_SOURCE_MUSIC);
                    if (audioType == MUSIC_TYPE_USB) {
                        mCurSourceType = AudioSourceType.USB_AUDIO;
                    } else if (audioType == MUSIC_TYPE_BT) {
                        mCurSourceType = AudioSourceType.BT_Audio;
                    } else {
                        mCurSourceType = AudioSourceType.UNKNOWN_MODE;
                    }

                    dispatchAudioSourceGranted(mCurSourceType);
                    Log.d(TAG, "<onAudioFocusGrant>(Music)[" + mCurSourceType.name() + "]");
                }

            }

            private void handleAudioFocusInternal(Bundle bundle) {
                if (bundle != null) {
                    mCurSourceType = (AudioSourceType) bundle.getSerializable(KEY_AUDIO_SOURCE);
                    dispatchAudioSourceGranted(mCurSourceType);
                    Log.d(TAG, "<onAudioFocusGrant>(Instruction)[" + mCurSourceType.name() + "]");
                }
            }

            @Override
            public void onAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) {
                super.onAudioFocusLoss(afi, wasNotified);
                if (afi != null) {
                    Log.d(TAG, "<onAudioFocusLoss> [" + afi.getPackageName() + "]");
                } else {
                    Log.d(TAG, "<onAudioFocusLoss> [null]");
                }
            }
        });
        if (mAudioManager != null) {
            mAudioManager.registerAudioPolicy(builder.build());
        }
    }

    private AudioAttributes createAttrs(AudioSourceType audioSource) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_AUDIO_SOURCE, audioSource);
        switch (audioSource) {
            case AM:
            case FM:
                bundle.putString(ARG_AUDIO_SOURCE_RADIO, ATTR_AUDIO_YQ);
                break;
            case USB_AUDIO:
                bundle.putInt(ARG_AUDIO_SOURCE_MUSIC, MUSIC_TYPE_USB);
                break;
            case BT_Audio:
                bundle.putInt(ARG_AUDIO_SOURCE_MUSIC, MUSIC_TYPE_BT);
                break;
            case USB_VIDEO:
//                bundle.putString(ARG_AUDIO_SOURCE_RADIO, ATTR_AUDIO_YQ);
                break;
        }
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(audioSource.getStream())
                .addBundle(bundle)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private AudioFocusRequest createRequest(AudioAttributes attrs) {
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attrs)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(mAudioFocusChangeListener)
                .build();
    }

    private class AudioFocusChangedListener implements AudioManager.OnAudioFocusChangeListener {

        AudioSourceType mAudioSourceType;

        public AudioFocusChangedListener(AudioSourceType type) {
            mAudioSourceType = type;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "<onAudioFocusChange> [  " + mAudioSourceType.name() + " ~ " + focusChange + "]");
        }
    }
}
