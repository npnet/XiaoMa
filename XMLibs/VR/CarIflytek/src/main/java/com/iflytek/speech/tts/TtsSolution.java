package com.iflytek.speech.tts;

import android.util.Log;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class TtsSolution implements ITTSService {
    private static final String tag = "TtsSolution";

    private TtsSolution() {
        Log.d("TtsSolution", "new TtsSolution");
    }

    public static TtsSolution getInstance() {
        return TtsSolution.HolderClass.instance;
    }

    public TtsPlayerInst createTtsPlayerInst(String strResDir) {
        return new TtsSolution.TtsPlayerInstIm(strResDir);
    }

    private class TtsPlayerInstIm implements TtsPlayerInst {
        private final TtsPlayer mTtsPlayer;

        public TtsPlayerInstIm(String strResDir) {
            this.mTtsPlayer = new TtsPlayer(strResDir);
        }

        public int sessionBegin(int streamType) {
            return this.mTtsPlayer.Init(streamType);
        }

        public int setParam(int nParam, int nValue) {
            return this.mTtsPlayer.SetParam(nParam, nValue);
        }

        public int startSpeak(String text, ITTSListener ttsListener,String id) {
            this.mTtsPlayer.setListener(ttsListener);
            return this.mTtsPlayer.Start(text,id);
        }

        public int pauseSpeak() {
            return this.mTtsPlayer.Pause();
        }

        public int resumeSpeak() {
            return this.mTtsPlayer.Resume();
        }

        public int stopSpeak() {
            return this.mTtsPlayer.Stop();
        }

        public int sessionStop() {
            return this.mTtsPlayer.Release();
        }

        public int setParamEx(int nParam, String strValue) {
            return this.mTtsPlayer.SetParamEx(nParam, strValue);
        }

        public int sessionInitState() {
            return this.mTtsPlayer.GetInitState();
        }
    }

    private static class HolderClass {
        private static final TtsSolution instance = new TtsSolution();

        private HolderClass() {
        }
    }
}

