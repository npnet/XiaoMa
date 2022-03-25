package com.xiaoma.vr.tts;

/**
 * Created by ZYao.
 * Date ：2019/6/6 0006
 */
public interface OnSequenceTtsListener {
    void onCurrentTtsBegin(String content);

    void onCurrentTtsCompleted(String content);

    void onCurrentTtsError(String content);

    void onTtsEnd();
}
