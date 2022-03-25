package com.xiaoma.launcher.favorites;

import android.content.Context;

import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

/**
 * 小马对外tts接口  四维地图间接性调用 切勿删除此类文件  不然运行时出现class文件找不到异常
 * Created by Thomas on 2019/4/16 0016
 */

public class TtsManager {

    /**
     * 初始化tts
     *
     * @param context
     */
    public void init(Context context) {
        KLog.e("init()");
        EventTtsManager.getInstance().init(context);
    }

    /**
     * 播报tts文本，设置监听器
     *
     * @param text
     * @param listener
     */
    public void startSpeaking(String text, final OnTtsListener listener) {
        KLog.e("startSpeaking() text=" + text + " listener=" + listener);
        boolean ret = EventTtsManager.getInstance().startSpeaking(text, new com.xiaoma.vr.tts.OnTtsListener() {
            @Override
            public void onCompleted() {
                if (listener != null) {
                    listener.onCompleted();
                }
            }

            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onError(int code) {
                if (listener != null) {
                    listener.onError(code);
                }
            }
        });
    }

    /**
     * 可变速播报tts文本，设置监听器
     *
     * @param text
     * @param speed    设置播放语述[0-10]
     * @param listener
     */
    public void startSpeaking(String text, int speed, final OnTtsListener listener) {
        KLog.e("startSpeaking() text=" + text + " speed=" + speed + " listener=" + listener);
        EventTtsManager.getInstance().startSpeaking(text, speed, new com.xiaoma.vr.tts.OnTtsListener() {
            @Override
            public void onCompleted() {
                if (listener != null) {
                    listener.onCompleted();
                }
            }

            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onError(int code) {
                if (listener != null) {
                    listener.onError(code);
                }
            }
        });
    }

    /**
     * 停止播报tts文本
     */
    public void stopSpeaking() {
        KLog.e("stopSpeaking()");
        EventTtsManager.getInstance().stopSpeaking();
    }

    /**
     * 移除tts监听
     */
    public void removeListeners() {
        KLog.e("removeListeners()");
        EventTtsManager.getInstance().removeListeners();
    }

    /**
     * 释放tts
     */
    public void destroy() {
        KLog.e("destroy()");
        EventTtsManager.getInstance().destroy();
    }

    /**
     * 判断当前是否在播报
     */
    public boolean isSpeaking(){
        return EventTtsManager.getInstance().isSpeaking();
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
