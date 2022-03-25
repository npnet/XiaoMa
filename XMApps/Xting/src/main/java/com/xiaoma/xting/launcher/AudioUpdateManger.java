package com.xiaoma.xting.launcher;

import com.xiaoma.xting.sdk.bean.XMPlayableModel;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/23
 */
public class AudioUpdateManger {

    private boolean mFavorited;
    private AudioChangeListener mAudioChangeListener;

    private AudioUpdateManger() {

    }

    public static AudioUpdateManger newSingleton() {
        return Holder.sINSTANCE;
    }

    public void updateFavoriteChanged(boolean favorite) {
        if (mAudioChangeListener != null) {
            mAudioChangeListener.onFavoriteChanged(favorite);
        }
    }

    public void updatePlayListChanged(XMPlayableModel playMode) {
        if (mAudioChangeListener != null) {
            mAudioChangeListener.onPlayListChanged(playMode);
        }
    }

    public void registerOnChangeListener(AudioChangeListener listener) {
        this.mAudioChangeListener = listener;
    }

    public void unRegisterOnChangeListener() {
        this.mAudioChangeListener = null;
    }

    interface Holder {
        AudioUpdateManger sINSTANCE = new AudioUpdateManger();
    }
}
