package com.xiaoma.instructiondistribute.listener;

import android.os.Bundle;
import android.util.Log;

/**
 * Author: loren
 * Date: 2019/8/18
 */
public class IEOLResponseCallback {
    public static final String TAG = IEOLResponseCallback.class.getSimpleName();

    public static final String DEF_NONE = "none";

    private void logResult(Bundle bundle) {
        String result = DEF_NONE;
        if (bundle != null) {
            result = bundle.toString();
        }
        Log.d(TAG, String.format("[%1$s]=>%2$s",
                new Exception().getStackTrace()[1].getMethodName(),
                result));
    }

    public void onFactoryReset(Bundle bundle) {
        logResult(bundle);
    }

    public void onEQConfiguration(Bundle bundle) {
        logResult(bundle);
    }

    public void onBluetoothTestMode(Bundle bundle) {
        logResult(bundle);
    }

    public void onBluetoothStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onBluetoothAddress(Bundle bundle) {
        logResult(bundle);
    }

    public void onBluetoothModuleVersion(Bundle bundle) {
        logResult(bundle);
    }

    public void onBTPairModeStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onBTAudioPlayPauseStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onBTAudioSkipTrackStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onBTIncomingCallActiveStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onClearBTPairedList(Bundle bundle) {
        logResult(bundle);
    }

    public void onBTPairedWithDevice(Bundle bundle) {
        logResult(bundle);
    }

    public void onAudioSourceSelect(Bundle bundle) {
        logResult(bundle);
    }

    public void onFaderLevel(Bundle bundle) {
        logResult(bundle);
    }

    public void onBalanceLevel(Bundle bundle) {
        logResult(bundle);
    }

    public void onMuteStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onSpeedVolumeStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onEQSettingStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onBastListenPositionStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onSoundFieldStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onArkamys3DStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onVolumeLevel(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBAudioPlayPause(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBAudioPlayMode(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBAudioSkipTrack(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBDesiredFileAndTime(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBCurrentStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBPictureOperation(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBPictureDisplayStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBPictureSkipStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBVideoPlayPauseStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onUSBVideoSkip(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerCurrentStatus(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerFrequency(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerBand(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerFavorite(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerSeek(Bundle bundle) {
        logResult(bundle);
    }

    public void onTunerAutoStore(Bundle bundle) {
        logResult(bundle);
    }

    public void onTFTIlluminationOnOff(Bundle bundle) {
        logResult(bundle);
    }

    public void onTFTDisplayPattern(Bundle bundle) {
        logResult(bundle);
    }

    public void onTestScreenIllumination(Bundle bundle) {
        logResult(bundle);
    }

    public void onTestMFDIllumination(Bundle bundle) {
        logResult(bundle);
    }

    public void onLCDLVDSOutputOnOff(Bundle bundle) {
        logResult(bundle);
    }

    public void onIPKLVDSOutputOnOff(Bundle bundle) {
        logResult(bundle);
    }

    public void onDiagnosisSessionCommand(Bundle bundle) {
        logResult(bundle);
    }
}
