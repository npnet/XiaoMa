package com.xiaoma.carlib.callback;

/**
 * Created by qiuboxiang on 2019/4/9 19:44
 * Desc:
 */
public interface onGetIntArrayResultListener {
    void onSoundEffectsGetResult(Integer[] result);

    void onArkamys3dEffectsGetResult(int result);

    void onSubwooferGetValue(boolean value);

    void onSoundEffectPositionGetResult(Integer[] result);
}