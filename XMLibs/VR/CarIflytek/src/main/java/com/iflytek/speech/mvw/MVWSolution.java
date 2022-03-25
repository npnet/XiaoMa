package com.iflytek.speech.mvw;

import android.util.Log;

import com.iflytek.speech.NativeHandle;
import com.iflytek.speech.libissmvw;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class MVWSolution implements IMVWService {
    private static final String tag = "MVWSolution";
    private static String ID = null;
    private static MVWSolution instance = null;
    private final NativeHandle mNativeHandle = new NativeHandle();

    public MVWSolution() {
        Log.d("MVWSolution", "new MVWSolution");
    }

    public static MVWSolution getInstance() {
        Log.d("MVWSolution", "getInstance");
        if (instance == null) {
            instance = new MVWSolution();
        }

        return instance;
    }

    public static int setMvwLanguage(int nLangType) {
        Log.d("MVWSolution", "setMvwLanguage");
        int errId = libissmvw.setMvwLanguage(nLangType);
        return errId;
    }

    public static boolean isCouldAppendAudioData() {
        Log.d("MVWSolution", "isCouldAppendAudioData");
        boolean flag = libissmvw.isCouldAppendAudioData();
        return flag;
    }

    public int startMvw(int nMvwScene) {
        Log.d("MVWSolution", "StartMvw");
        libissmvw.stop(this.mNativeHandle);
        int errid = libissmvw.start(this.mNativeHandle, nMvwScene);
        Log.d("MVWSolution", "speechjni MVW -> start return " + errid);
        return errid;
    }

    public int addStartMvwScene(int nMvwScene) {
        Log.d("MVWSolution", "add StartMvw scene");
        int errid = libissmvw.addstartscene(this.mNativeHandle, nMvwScene);
        Log.d("MVWSolution", "speechjni MVW -> start return " + errid);
        return errid;
    }

    public int appendAudioData(byte[] audioBuffer, int BufferLength) {
        return libissmvw.appendAudioData(this.mNativeHandle, audioBuffer, BufferLength);
    }

    public int setThreshold(int nMvwScene, int nMvwId, int threshold) {
        Log.d("MVWSolution", "setThreshold");
        return libissmvw.setThreshold(this.mNativeHandle, nMvwScene, nMvwId, threshold);
    }

    public int setParam(String szParam, String szParamValue) {
        Log.d("MVWSolution", "setParam");
        return libissmvw.setParam(this.mNativeHandle, szParam, szParamValue);
    }

    public int stopMvw() {
        Log.e("destroy xf MVWSolution", "sessionStop");
        int errId = libissmvw.stop(this.mNativeHandle);
        Log.e("destroy xf MVWSolution", ""+errId);
        return errId;
    }

    public int stopMvwScene(int nMvwScene) {
        Log.d("MVWSolution", "sessionStop scene");
        int errId = libissmvw.stopscene(this.mNativeHandle, nMvwScene);
        return errId;
    }

    public int initMvw(String resDir, IMVWListener mMVWListener) {
        Log.d("MVWSolution", "init Mvw");
        if (resDir != null) {
            resDir = resDir.replaceFirst("/$", "");
        }

        int errId = libissmvw.create(this.mNativeHandle, resDir, mMVWListener);
        Log.d("MVWSolution", "create libissmvw return " + errId);
        return errId;
    }

    public int releaseMvw() {
        Log.d("MVWSolution", "release mvw");
        int errId = libissmvw.destroy(this.mNativeHandle);
        Log.e("destroy log MVWSolution", " libissmvw destroy " + errId);
        return errId;
    }

    public int setMvwKeyWords(int nMvwScene, String szWords) {
        Log.d("MVWSolution", "setMvwKeyWords");
        int errId = libissmvw.setMvwKeyWords(this.mNativeHandle, nMvwScene, szWords);
        return errId;
    }

    public int setMvwDefaultKeyWords(int nMvwScene) {
        Log.d("MVWSolution", "setMvwKeyWords");
        int errId = libissmvw.setMvwDefaultKeyWords(this.mNativeHandle, nMvwScene);
        return errId;
    }
}

