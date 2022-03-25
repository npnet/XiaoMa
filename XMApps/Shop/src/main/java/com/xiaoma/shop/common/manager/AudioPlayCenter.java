package com.xiaoma.shop.common.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/24
 * @Describe: 音频播放中心类
 */

public class AudioPlayCenter {

    private List<OnAudioPlayStatusListener> mOnAudioPlayStatusListeners;

    public AudioPlayCenter() {
        mOnAudioPlayStatusListeners = new ArrayList<>();
    }

    public void registerAudioPlayChangeListener(OnAudioPlayStatusListener onAudioPlayStatusListener) {
        if (onAudioPlayStatusListener != null && !mOnAudioPlayStatusListeners.contains(onAudioPlayStatusListener)) {
            mOnAudioPlayStatusListeners.add(onAudioPlayStatusListener);
        }
    }

    public void unRegisterAudioPlayChangeListener(OnAudioPlayStatusListener onAudioPlayStatusListener) {
        if (onAudioPlayStatusListener != null && mOnAudioPlayStatusListeners.contains(onAudioPlayStatusListener)) {
            mOnAudioPlayStatusListeners.remove(onAudioPlayStatusListener);
        }
    }

    public void dispatchAudioPlayStatusChange() {
        for (OnAudioPlayStatusListener onAudioPlayStatusListener : mOnAudioPlayStatusListeners) {
            onAudioPlayStatusListener.change();
        }
    }

    public static AudioPlayCenter getInstance() {
        return Hodler.sAudioPlayCenter;
    }

    private static class Hodler {
        private static AudioPlayCenter sAudioPlayCenter = new AudioPlayCenter();
    }

    public interface OnAudioPlayStatusListener {

        void change();
    }
}
