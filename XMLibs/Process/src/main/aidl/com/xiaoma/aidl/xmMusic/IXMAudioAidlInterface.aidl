package com.xiaoma.aidl.xmMusic;

import com.xiaoma.aidl.xmMusic.IXMAudioStatusNotifyAidlInterface;
import com.xiaoma.aidl.model.AudioVTO;

interface IXMAudioAidlInterface {

    void registerAudioStatusReceiver(IXMAudioStatusNotifyAidlInterface notifyAidlInterface);

    void unRegisterAudioStatusReceiver(IXMAudioStatusNotifyAidlInterface notifyAidlInterface);

    void searchAndPlayAudio(in AudioVTO bean);

    void continueAudio();

    void pauseAudio();

    void stopAudio();

}
