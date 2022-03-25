package com.xiaoma.xting.sdk;

import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/23
 */
public interface LocalFMStatusListener {
    void onRadioOpen();

    void onRadioClose(XMRadioStation xmRadioStation);

    void onTuningAM(int frequency);

    void onTuningFM(int frequency);

    void onScanStart();

    void onNewStation(XMRadioStation station);

    void onScanAllResult(List<XMRadioStation> stations);

    void onBandChanged(BandType band);

    void onCancel();

    void onError(int code, String msg);
}
