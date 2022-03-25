package com.xiaoma.xting.sdk;

import com.xiaoma.utils.log.KLog;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.List;

/**
 * @author KY
 * @date 11/13/2018
 */
public class LocalFMStatusListenerImpl implements LocalFMStatusListener {

    public static final String TAG = LocalFMStatusListenerImpl.class.getSimpleName();

    @Override
    public void onRadioOpen() {
        KLog.d(TAG, "onRadioOpen");
    }

    @Override
    public void onRadioClose(XMRadioStation xmRadioStation) {
        KLog.d(TAG, "onRadioClose");
    }

    @Override
    public void onTuningAM(int frequency) {
        KLog.d(TAG, "onTuningAM:" + String.valueOf(frequency));
    }

    @Override
    public void onTuningFM(int frequency) {
        KLog.d(TAG, "onTuningFM:" + String.valueOf(frequency));
    }

    @Override
    public void onScanStart() {
        KLog.d(TAG, "onScanStart");
    }

    @Override
    public void onNewStation(XMRadioStation station) {
        KLog.d(TAG, "onNewStation:" + String.valueOf(station));
    }

    @Override
    public void onScanAllResult(List<XMRadioStation> stations) {
        KLog.d(TAG, "onScanAllResult:" + String.valueOf(stations));
    }

    @Override
    public void onBandChanged(BandType band) {
        KLog.d(TAG, "onBandChanged:" + String.valueOf(band));
    }

    @Override
    public void onCancel() {
        KLog.d(TAG, "onCancel:");
    }

    @Override
    public void onError(int code, String msg) {
        KLog.d(TAG, "onError:" + msg);
    }
}
