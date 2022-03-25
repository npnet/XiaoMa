package com.xiaoma.xting.sdk;

import android.content.Context;
import android.support.annotation.Nullable;

import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

/**
 * @author youthyJ
 * @date 2018/9/29
 */
public interface LocalFM {
    boolean init(Context context);

    boolean isInited();

    boolean isConnected();

    boolean isHasAudioFocus();

    boolean isRadioOpen();

    boolean isSupportFM();

    boolean isSupportAM();

    boolean addLocalFMStatusListener(LocalFMStatusListener l);

    boolean removeLocalFMStatusListener(LocalFMStatusListener l);

    boolean tuneFM(int frequency);

    boolean tuneAM(int frequency);

    boolean scanAll(BandType band);

    boolean cancel();

    boolean scanDown();

    boolean scanUp();

    boolean stepNext();

    boolean stepPrevious();

    @Nullable
    XMRadioStation getCurrentStation();

    boolean openRadio();

    void closeRadio();

    @Nullable
    BandType getCurrentBand();

    /**
     * 在车机中SDK中， 调用SwitchBand会自动回到切换目标类型的上一个频率，并在onNewStation中回调之前的频率
     * 如果在调用switchBand 之后立马调用TuneFM/AM onNewStation也只会被调用一次，回调的是新调频的频率
     *
     * @param band
     */
    boolean switchBand(BandType band);

    void getInternalRecord();

    boolean setMuted(boolean muted);

    boolean isScanning();
}
