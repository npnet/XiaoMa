package com.xiaoma.vr.iat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.vr.model.NaviState;


/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/21
 * Desc:Iat manager for the other app by broadcast
 */

public class RemoteIatManager implements IIatManager {
    private Context context;
    private OnIatListener onIatListener;
    private static RemoteIatManager instance = null;
    private boolean isRegistered = false;
    private EventIatReceiver mReceiver;
    private boolean isListener = false;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    KLog.d("RemoteIatManager onAudioFocusChange, " + "focusChange: " + focusChange);
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //在聆听状态下 如果出现丢失将处理异常逻辑
                        if (isListener) {
                            onIatError(IatError.ERROR_MEDIA_FOCUS_LOSS);
                            stopListening();
                        }
                    }
                }
            };

    public synchronized static RemoteIatManager getInstance() {
        if (instance == null) {
            instance = new RemoteIatManager();
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context.getApplicationContext();
        audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        this.context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.INIT_IAT));
        initReceiver(this.context);
    }

    private void initReceiver(Context context) {
        if (isRegistered) {
            return;
        }
        mReceiver = new EventIatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VrConstants.Actions.ON_IAT_COMPLETE);
        intentFilter.addAction(VrConstants.Actions.ON_IAT_ERROR);
        intentFilter.addAction(VrConstants.Actions.ON_IAT_VOLUME_CHANGE);
        intentFilter.addAction(VrConstants.Actions.ON_IAT_RESULT);
        intentFilter.addAction(VrConstants.Actions.ON_IAT_WAV_FILE_COMPLETE);
        context.registerReceiver(mReceiver, intentFilter);

        isRegistered = true;
    }

    @Override
    public void setOnIatListener(OnIatListener onIatListener) {
        this.onIatListener = onIatListener;
    }

    @Override
    public void release() {
        try {
            this.onIatListener = null;
            if (isRegistered && mReceiver != null) {
                context.unregisterReceiver(mReceiver);
                isRegistered = false;
            }
        } catch (Exception e) {
            KLog.e(e.toString());
        }
    }

    @Override
    public void stopListening() {
        if (context != null) {
            abandonAudioFocus();
            context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.STOP_IAT));
        }
    }

    /**
     * @param isCloseSeopt 本次识别是否需要临时关闭定向识别
     */
    public void startListeningNormal(boolean isCloseSeopt) {
        if (context != null) {
            if (requestAudioFocus()) {
                Intent broadCastIntent = getBroadCastIntent(VrConstants.Actions.START_IAT);
                broadCastIntent.putExtra(VrConstants.ActionExtras.SEOPT_CLOSE, isCloseSeopt);
                context.sendBroadcast(broadCastIntent);
                isListener = true;
            } else {
                onIatError(IatError.ERROR_MIC_FOCUS_LOSS);
            }
        }
    }


    @Override
    public void startListeningNormal() {
        if (context != null) {
            if (requestAudioFocus()) {
                context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.START_IAT));
                isListener = true;
            } else {
                onIatError(IatError.ERROR_MIC_FOCUS_LOSS);
            }
        }
    }


    @Override
    public void startListeningRecord() {
        if (context != null) {
            if (requestAudioFocus()) {
                context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.START_IAT_RECORD));
                isListener = true;
            } else {
                onIatError(IatError.ERROR_MIC_FOCUS_LOSS);
            }
        }
    }

    @Override
    public void startListeningRecord(int startTimeOut, int endTimeOut) {
        if (context != null) {
            if (requestAudioFocus()) {
                Intent intent = getBroadCastIntent(VrConstants.Actions.START_IAT_RECORD);
                intent.putExtra(VrConstants.ActionExtras.IAT_END_TIME_OUT, endTimeOut);
                intent.putExtra(VrConstants.ActionExtras.IAT_START_TIME_OUT, startTimeOut);
                context.sendBroadcast(intent);
                isListener = true;
            } else {
                onIatError(IatError.ERROR_MIC_FOCUS_LOSS);
            }
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void startListeningForChoose() {
        if (context != null) {
            if (requestAudioFocus()) {
                context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.START_IAT_FOR_CHOOSE));
                isListener = true;
            } else {
                onIatError(IatError.ERROR_MIC_FOCUS_LOSS);
            }
        }
    }

    @Override
    public void startListeningForChoose(String srSceneStkCmd) {

    }

    @Override
    public void cancelListening() {
        if (context != null) {
            abandonAudioFocus();
            context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.STOP_IAT));
        }
    }

    @Override
    public void destroy() {
        if (context != null) {
            abandonAudioFocus();
            context.sendBroadcast(getBroadCastIntent(VrConstants.Actions.STOP_IAT));
        }
    }

    @Override
    public boolean getInitState() {
        return false;
    }

    @Override
    public void upLoadContact(boolean isPhoneContact, String contacts) {
        if (context != null) {
            Intent intent = getBroadCastIntent(VrConstants.Actions.UPLOAD_CONTACT_IAT);
            intent.putExtra(VrConstants.ActionExtras.IAT_CONTACTS_TYPE_UPLOAD, isPhoneContact);
            intent.putExtra(VrConstants.ActionExtras.CONTACT_LIST, contacts);
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void uploadAppState(boolean isForeground, @AppType String appType) {
        if (context != null) {
            Intent intent = getBroadCastIntent(VrConstants.Actions.UPLOAD_APP_STATE);
            intent.putExtra(VrConstants.ActionExtras.IS_FOREGROUND, isForeground);
            intent.putExtra(VrConstants.ActionExtras.APP_TYPE, appType);
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void uploadPlayState(boolean isPlaying, @AppType String appType) {
        Log.e("AUDIO_STATE", "uploadPlayState: " + isPlaying + " ~ " + appType);
        if (context != null) {
            Intent intent = getBroadCastIntent(VrConstants.Actions.UPLOAD_PLAY_STATE);
            intent.putExtra(VrConstants.ActionExtras.PLAY_STATE, isPlaying);
            intent.putExtra(VrConstants.ActionExtras.APP_TYPE, appType);
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void uploadNaviState(@NaviState String naviState) {
        if (context != null) {
            Intent intent = getBroadCastIntent(VrConstants.Actions.UPLOAD_NAVI_STATE);
            intent.putExtra(VrConstants.ActionExtras.NAVI_STATE, naviState);
            context.sendBroadcast(intent);
        }
    }

    @SuppressLint("WrongConstant")
    private Intent getBroadCastIntent(String action) {
        Intent intent = new Intent(action);
        intent.addFlags(0x01000000);
        return intent;
    }

    public boolean requestAudioFocus() {
        boolean isFocus = false;
        int i = audioManager.requestAudioFocus(onAudioFocusChangeListener, VrConfig.IAT_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        isFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return isFocus;
    }

    public void abandonAudioFocus() {
        isListener = false;
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }

    private class EventIatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case VrConstants.Actions.ON_IAT_COMPLETE:
                    abandonAudioFocus();
                    if (onIatListener != null) {
                        String voiceContent = intent.getStringExtra(VrConstants.ActionExtras.IAT_VOICE_CONTENT);
                        onIatListener.onComplete(voiceContent, "");
                    }
                    break;
                case VrConstants.Actions.ON_IAT_VOLUME_CHANGE:
                    if (onIatListener != null) {
                        int volume = intent.getIntExtra(VrConstants.ActionExtras.IAT_VOLUME, 0);
                        onIatListener.onVolumeChanged(volume);
                    }
                    break;
                case VrConstants.Actions.ON_IAT_ERROR:
                    int code = intent.getIntExtra(VrConstants.ActionExtras.IAT_ERROR_CODE, IatError.ERROR);
                    onIatError(code);
                    break;
                case VrConstants.Actions.ON_IAT_RESULT:
                    if (onIatListener != null) {
                        String voiceText = intent.getStringExtra(VrConstants.ActionExtras.IAT_VOICE_TEXT);
                        boolean isLast = intent.getBooleanExtra(VrConstants.ActionExtras.IAT_VOICE_ISLAST, false);
                        onIatListener.onResult(voiceText, isLast, "");
                    }
                    break;
                case VrConstants.Actions.ON_IAT_WAV_FILE_COMPLETE:
                    if (onIatListener != null) {
                        onIatListener.onWavFileComplete();
                    }
                    break;
            }
        }
    }

    private void onIatError(int errorCode) {
        Log.e("RemoteIatManager", " onIatError:" + errorCode);
        abandonAudioFocus();
        if (onIatListener != null) {
            onIatListener.onError(errorCode);
        }
    }


}
