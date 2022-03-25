package com.iflytek.tts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iflytek.speech.ISSErrors;
import com.iflytek.speech.NativeHandle;
import com.iflytek.speech.libisstts;
import com.iflytek.speech.tts.ITTSListener;
import com.iflytek.speech.tts.ITTSService;
import com.iflytek.speech.tts.TtsPlayerInst;
import com.iflytek.speech.tts.TtsSolution;
import com.xiaoma.utils.constant.VrConstants;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class TtsSession {
    private static int cnt = 0;
    private String tag;
    private static String mResDir = null;
    private ITtsListener mTtsListener;
    private TtsPlayerInst mItts;
    private static ITtsInitListener mTtsInitListener = null;
    private Context mContext;
    private static ITTSService mTTSService;
    private ITTSListener mTtsAidlListener;
    private static Object lock = new Object();
    private final NativeHandle mNativeHandle;
    private com.iflytek.speech.ITtsListener mVoidTtsListener;
    private boolean isSpeaking;

    public TtsSession(Context context, ITtsInitListener iTtsInitListener, String resDir) {
        this.tag = "TtsSession_" + cnt;
        this.mTtsListener = null;
        this.mItts = null;
        this.mContext = null;
        this.mTtsAidlListener = new ITTSListener() {
            public void onTTSPlayBegin(String ttsID) {
                upTtsSpeakingStatus(true);
                if (TtsSession.this.mTtsListener != null) {
                    TtsSession.this.mTtsListener.onPlayBegin(ttsID);
                }

            }

            public void onTTSPlayCompleted(String ttsID) {
                upTtsSpeakingStatus(false);
                if (TtsSession.this.mTtsListener != null) {
                    TtsSession.this.mTtsListener.onPlayCompleted(ttsID);
                }

            }

            public void onTTSPlayInterrupted(String ttsID) {
                upTtsSpeakingStatus(false);
                if (TtsSession.this.mTtsListener != null) {
                    TtsSession.this.mTtsListener.onPlayInterrupted(ttsID);
                }

            }

            public void onTTSProgressReturn(int nTextIndex, int nTextLen) {
                if (TtsSession.this.mTtsListener != null) {
                    TtsSession.this.mTtsListener.onProgressReturn(nTextIndex, nTextLen);
                }

            }
        };
        this.mNativeHandle = new NativeHandle();
        this.mVoidTtsListener = new com.iflytek.speech.ITtsListener() {
            public void onDataReady() {
            }

            public void onProgress(int nTextIndex, int nTextLen) {
            }
        };
        Object var4 = lock;
        synchronized (lock) {
            Log.d(this.tag, "new TtsSession()");
            ++cnt;
            this.mContext = context;
            mTtsInitListener = iTtsInitListener;
            mResDir = resDir;
            this.initService();
        }
    }

    private void upTtsSpeakingStatus(boolean b) {
        if (isSpeaking != b) {
            isSpeaking = b;
            sendSpeakingStatus();
        }
    }

    public void sendSpeakingStatus() {
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.TTS_SPEAKING_STATUS);
            intent.putExtra(VrConstants.ActionExtras.TTS_STATUS, isSpeaking);
            mContext.sendBroadcast(intent);
        }
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    private void castInitState(boolean s, int e) {
        if (mTtsInitListener != null) {
            Log.d(this.tag, "castInitState(" + s + ", " + e + ")");
            mTtsInitListener.onTtsInited(s, e);
        }

    }

    public synchronized void initService() {
        Log.d(this.tag, "initService");
        if (this.mContext == null) {
            Log.d(this.tag, "initService: mContext == null.");
            (new Thread(new OnTtsInitedRunnable(false, 10106))).start();
        } else if (null == mResDir) {
            Log.d(this.tag, "initService: mResDir == null.");
            (new Thread(new OnTtsInitedRunnable(false, 10106))).start();
        } else {
            mTTSService = TtsSolution.getInstance();
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
            int initResRet = this.mItts.sessionInitState();
            if (0 != initResRet) {
                Log.d(this.tag, "initRes is failed. ret = " + initResRet);
                (new Thread(new OnTtsInitedRunnable(false, initResRet))).start();
            } else {
                (new Thread(new OnTtsInitedRunnable(true, 0))).start();
            }
        }
    }

    public int sessionStart(ITtsListener ttsListener, int audioType) {
        Object var3 = lock;
        synchronized (lock) {
            Log.d(this.tag, "sessionStart");
            if (mTTSService == null) {
                mTTSService = TtsSolution.getInstance();
            }

            this.mTtsListener = ttsListener;
            if (this.mItts == null) {
                this.mItts = mTTSService.createTtsPlayerInst(mResDir);
            }

            return this.mItts.sessionBegin(audioType);
        }
    }

    public int setParam(int id, int value) {
        Log.d(this.tag, "setParam");
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (this.mItts == null) {
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
        }

        return this.mItts.setParam(id, value);
    }

    public int setParamEx(int id, String strValue) {
        Log.d(this.tag, "setParamEx");
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (this.mItts == null) {
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
        }

        return this.mItts.setParamEx(id, strValue);
    }

    public int startSpeak(String text, String id) {
        int result = -1;
        Log.d("XmTtsManager", String.format("startSpeaking TtsSession:%s", text));
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (text != null && text.length() != 0) {
            if (this.mItts == null) {
                this.mItts = mTTSService.createTtsPlayerInst(mResDir);
            }
            result = this.mItts.startSpeak(text, this.mTtsAidlListener, id);
        } else {
            result = 10106;
        }
        if (result != ISSErrors.ISS_SUCCESS) {
            Log.e("XmTtsManager", "start speak error:" + result);
        }
        return result;

    }

    public int pauseSpeak() {
        Log.d(this.tag, "pauseSpeak");
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (this.mItts == null) {
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
        }

        return this.mItts.pauseSpeak();
    }

    public int resumeSpeak() {
        Log.d(this.tag, "resumeSpeak");
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (this.mItts == null) {
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
        }

        return this.mItts.resumeSpeak();
    }

    public int stopSpeak() {
        Log.d(this.tag, "stopSpeak");
        if (mTTSService == null) {
            mTTSService = TtsSolution.getInstance();
        }

        if (this.mItts == null) {
            this.mItts = mTTSService.createTtsPlayerInst(mResDir);
        }

        return this.mItts.stopSpeak();
    }

    public int sessionStop() {
        Object var1 = lock;
        synchronized (lock) {
            Log.d(this.tag, "sessionStop");
            if (mTTSService == null) {
                mTTSService = TtsSolution.getInstance();
            }

            if (this.mItts == null) {
                this.mItts = mTTSService.createTtsPlayerInst(mResDir);
            }

            return this.mItts.sessionStop();
        }
    }

    public int startSynthToGetPcm(String text) {
        if (this.mNativeHandle.native_point == 0L) {
            libisstts.initRes(mResDir, 1);
            if (this.mNativeHandle.err_ret != 0) {
                return this.mNativeHandle.err_ret;
            }

            libisstts.create(this.mNativeHandle, this.mVoidTtsListener);
            if (this.mNativeHandle.err_ret != 0) {
                return this.mNativeHandle.err_ret;
            }
        }

        libisstts.start(this.mNativeHandle, text);
        return 0;
    }

    public int setParamSynthToGetPcm(int id, int value) {
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.setParam(this.mNativeHandle, id, value);
            return this.mNativeHandle.err_ret;
        }
    }

    public int stopSynthToGetPcm() {
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.stop(this.mNativeHandle);
            return this.mNativeHandle.err_ret;
        }
    }

    public int destroySynthToGetPcm() {
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.destroy(this.mNativeHandle);
            this.mNativeHandle.native_point = 0L;
            int tmp = this.mNativeHandle.err_ret;
            this.mNativeHandle.err_ret = 0;
            return tmp;
        }
    }

    public int getAudioData(byte[] audioBuffer, int nBytes, int[] outBytes) {
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.getAudioData(this.mNativeHandle, audioBuffer, nBytes, outBytes);
            return this.mNativeHandle.err_ret;
        }
    }

    private class OnTtsInitedRunnable implements Runnable {
        public boolean mBoolInitState;
        public int mIntErrorCode;

        public OnTtsInitedRunnable(boolean s, int e) {
            this.mBoolInitState = s;
            this.mIntErrorCode = e;
        }

        public void run() {
            synchronized (TtsSession.lock) {
                try {
                    Thread.currentThread();
                    Thread.sleep(5L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }

                TtsSession.this.castInitState(this.mBoolInitState, this.mIntErrorCode);
            }
        }
    }

}
