package com.xiaoma.instructiondistribute.listener;

import com.xiaoma.instructiondistribute.contract.AudioSourceType;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/9
 */
public interface IAudioSourceTypeListener {

    void onAudioSourceTypeGranted(AudioSourceType type);
}
