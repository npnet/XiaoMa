package com.xiaoma.launcher.favorites;

import android.content.Context;

/**
 * 小马对外tts接口,接口空方法体编译使用，不需要打包class到aar包中，宿主apk中存在TtsManager.class实现类
 * Created by Thomas on 2019/4/16 0016
 */

public class TtsManager {

    /**
     * 初始化tts
     * @param context
     */
    public void init(Context context) {

    }

    /**
     * 播报tts文本，设置监听器
     * @param text
     * @param listener
     */
    public void startSpeaking(String text, OnTtsListener listener) {

    }

    /**
     * 可变速播报tts文本，设置监听器
     *
     * @param text
     * @param speed
     */
    public void startSpeaking(String text,int speed, OnTtsListener listener) {

    }

    /**
     * 停止播报tts文本
     */
    public void stopSpeaking() {

    }

    /**
     * 移除tts监听
     */
    public void removeListeners() {

    }

    /**
     * 释放tts
     */
    public void destroy() {

    }

    /**
     * 判断当前是否在播报
     */
    public boolean isSpeaking(){
        return false;
    }

    /**
     * tts监听器
     */
    public interface OnTtsListener {
        void onBegin();

        void onCompleted();

        void onError(int code);
    }

}
