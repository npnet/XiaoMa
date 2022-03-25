package com.xiaoma.xting.sdk;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import com.acrcloud.rec.ACRCloudClient;
import com.acrcloud.rec.ACRCloudConfig;
import com.acrcloud.rec.IACRCloudRadioMetadataListener;
import com.acrcloud.rec.utils.ACRCloudLogger;
import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.sdk.listener.OnRadioResultListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * wutao
 * 2018.12.5
 */
public class AcrSDK implements IAcrRadio {
    private static final String TAG = AcrSDK.class.getSimpleName();
    private ACRCloudClient mClient;

    public AcrSDK() {
    }

    @Override
    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        Context appContext = context.getApplicationContext();
        ACRCloudConfig config = new ACRCloudConfig();
        config.context = appContext;

        // Please create project in "http://console.acrcloud.cn/service/avr".
        config.host = AcrConfig.HOST;
        config.accessKey = AcrConfig.ACCESS_KEY;
        config.accessSecret = AcrConfig.ACCESS_SECRET;

        // auto recognize access key
        config.hostAuto = "";
        config.accessKeyAuto = "";
        config.accessSecretAuto = "";

        config.recorderConfig.rate = 8000;
        config.recorderConfig.channels = AudioFormat.CHANNEL_IN_DEFAULT;
        config.recorderConfig.source = MediaRecorder.AudioSource.MIC;
        config.recorderConfig.reservedRecordBufferMS = 0; // 关闭录制
        config.recorderConfig.isVolumeCallback = false;

        mClient = new ACRCloudClient();
        mClient.initWithConfig(config);
        ACRCloudLogger.setLog(true);
    }

    public void requestRadioMetadata(final int type, String lat, String lon, final int frequency, final OnRadioResultListener listener) {
        ACRCloudConfig.RadioType radioType = ACRCloudConfig.RadioType.FM;
        List<String> freqList = new ArrayList<>();
        if (type == XtingConstants.FMAM.TYPE_FM) {
            freqList.add(frequency / 10.0 + "");
        } else if (type == XtingConstants.FMAM.TYPE_AM) {
            radioType = ACRCloudConfig.RadioType.AM;
            //AM需要传入整型
            freqList.add(frequency + "");
        }
        if (mClient == null) {
            init(AppHolder.getInstance().getAppContext());
        }
        mClient.requestRadioMetadataAsyn(lat, lon, freqList, radioType, new IACRCloudRadioMetadataListener() {
            @Override
            public void onRadioMetadataResult(String s) {
                Log.i(TAG, "onRadioMetadataResult: " + s);
                listener.onRadioResult(s);
            }
        });
    }

    public void requestBatchRadioMetadata(final int type, String lat, String lon, String frequencys, final OnRadioResultListener listener) {
        ACRCloudConfig.RadioType radioType;
        List<String> freqList = new ArrayList<>();
        if (type == XtingConstants.FMAM.TYPE_FM) {
            radioType = ACRCloudConfig.RadioType.FM;
        } else {
            radioType = ACRCloudConfig.RadioType.AM;
        }
        if (!TextUtils.isEmpty(frequencys)) {
            String[] freqs = frequencys.split("\\|");
            freqList = Arrays.asList(freqs);
        }
        if (mClient == null) {
            init(AppHolder.getInstance().getAppContext());
        }
        mClient.requestRadioMetadataAsyn(lat, lon, freqList, radioType, new IACRCloudRadioMetadataListener() {
            @Override
            public void onRadioMetadataResult(String s) {
                Log.d(TAG, "onRadioMetadataResult: " + s);
                listener.onRadioResult(s);
            }
        });
    }

    public void release() {
        if (mClient != null) {
            mClient.release();
            mClient = null;
        }
    }
}
