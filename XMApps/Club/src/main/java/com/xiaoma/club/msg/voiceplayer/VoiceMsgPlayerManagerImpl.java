package com.xiaoma.club.msg.voiceplayer;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.xiaoma.club.common.util.LogUtil.logE;
import static com.xiaoma.club.common.util.LogUtil.logI;
import static com.xiaoma.club.msg.voiceplayer.VoicePlayError.ERR_AUDIO_FOCUS_OCCUPIED;
import static com.xiaoma.club.msg.voiceplayer.VoicePlayError.ERR_UNKNOWN;

/**
 * Created by LKF on 2018-12-29 0029.
 */
class VoiceMsgPlayerManagerImpl implements IVoiceMsgPlayerManager, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "VMPMImpl";
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private EMMessage mCurrMessage;
    private final Set<VoiceMsgPlayCallback> mCallbacks = new ArraySet<>();

    private final AudioManager.OnAudioFocusChangeListener mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            logI(TAG, "onAudioFocusChange( focusChange: %s )", focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    stopInternal(true);
                    break;
            }
        }
    };

    @Override
    public void init(Context context) {
        if (mPlayer != null) {
            logI(TAG, "init() Has init, return !");
            return;
        }
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        logI(TAG, "init()");
    }

    @Override
    public void release() {
        logI(TAG, "release()");
        stopInternal(true);
        try {
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer = null;
        mCurrMessage = null;
    }

    @Override
    public void play(EMMessage message) {
        if (EMMessage.Type.VOICE != message.getType()) {
            logE(TAG, "play( message: %s ) Not voice msg !!!", message);
            dispatchError(ERR_UNKNOWN, "");
            return;
        }
        final EMMessageBody body = message.getBody();
        if (!(body instanceof EMVoiceMessageBody)) {
            logE(TAG, "play( message: %s ) incorrect msg body: %s", message, body != null ? body.getClass().getName() : "null");
            dispatchError(ERR_UNKNOWN, "");
            return;
        }
        final EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) body;
        // 优先读取本地播放
        final String localPath = voiceBody.getLocalUrl();
        if (new File(localPath).exists()) {
            logI(TAG, "play( message: %s ) localPath: %s", message, localPath);
            playInternal(localPath);
            mCurrMessage = message;
            return;
        }
        // 远程播放
        final String remoteUrl = voiceBody.getRemoteUrl();
        if (!TextUtils.isEmpty(remoteUrl)) {
            logI(TAG, "play( message: %s ) remoteUrl: %s", message, remoteUrl);
            playInternal(remoteUrl);
            // 下载到本地
            try {
                EMClient.getInstance().chatManager().downloadAttachment(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCurrMessage = message;
            return;
        }
        dispatchError(ERR_UNKNOWN, "");
        logE(TAG, "play( message: %s ) Invalid voice body: %s", message, voiceBody);
    }

    private void playInternal(String path) {
        stopInternal(false);
        if (!requestFocus()) {
            dispatchError(ERR_AUDIO_FOCUS_OCCUPIED, "");
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    dispatchPlay();
                }
            });
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            dispatchError(ERR_UNKNOWN, e.getMessage());
        }
    }

    @Override
    public void stop() {
        stopInternal(true);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public EMMessage getCurrMessage() {
        return mCurrMessage;
    }

    @Override
    public void addVoiceMsgPlayCallback(VoiceMsgPlayCallback callback) {
        if (callback != null)
            mCallbacks.add(callback);
    }

    @Override
    public void removeVoiceMsgPlayCallback(VoiceMsgPlayCallback callback) {
        if (callback != null)
            mCallbacks.remove(callback);
    }

    private void stopInternal(boolean abandonFocus) {
        try {
            if (mPlayer.isPlaying())
                mPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispatchStop();
        if (abandonFocus) {
            abandonFocus();
        }
    }

    private boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.requestAudioFocus(mFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    private void abandonFocus() {
        mAudioManager.abandonAudioFocus(mFocusChangeListener);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        logI(TAG, "onCompletion");
        abandonFocus();
        dispatchComplete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        dispatchError(what, String.valueOf(extra));
        return true;
    }

    private void dispatchPlay() {
        final EMMessage message = mCurrMessage;
        logI(TAG, "dispatchPlay() curMsg: %s", message);
        final Set<VoiceMsgPlayCallback> callbacks = new ArraySet<>(mCallbacks);
        for (VoiceMsgPlayCallback callback : callbacks) {
            try {
                callback.onPlay(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatchStop() {
        final EMMessage message = mCurrMessage;
        logI(TAG, "dispatchStop() curMsg: %s", message);
        final Set<VoiceMsgPlayCallback> callbacks = new ArraySet<>(mCallbacks);
        for (VoiceMsgPlayCallback callback : callbacks) {
            try {
                callback.onStop(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatchComplete() {
        final EMMessage message = mCurrMessage;
        logI(TAG, "dispatchComplete() curMsg: %s", message);
        final Set<VoiceMsgPlayCallback> callbacks = new ArraySet<>(mCallbacks);
        for (VoiceMsgPlayCallback callback : callbacks) {
            try {
                callback.onComplete(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatchError(int code, String msg) {
        logE(TAG, "dispatchComplete( code: %s, msg: %s )", code, msg);
        final Set<VoiceMsgPlayCallback> callbacks = new ArraySet<>(mCallbacks);
        for (VoiceMsgPlayCallback callback : callbacks) {
            try {
                callback.onError(code, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}