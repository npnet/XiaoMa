package com.xiaoma.xting.sdk;

import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioExternTuner;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioMetadata;
import android.hardware.radio.RadioTuner;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioMetadata;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/22
 */
@SuppressLint("WrongConstant")
public class YqSDK implements LocalFM {
    private static final String TAG = YqSDK.class.getSimpleName() + "_TAG";
    private static final int DEFAULT_BAND_TYPE = RadioManager.BAND_FM;
    private static YqSDK instance;

    private boolean isInited = false;
    private boolean isConnected = false;
    private int currentRadioBand = DEFAULT_BAND_TYPE;   // 波段类型(AM/FM)
    private StatusDispatcher statusDispatcher = StatusDispatcher.getInstance();

    private Context appContext;

    // 电台管理器相关
    private boolean isSupportFM = false;
    private boolean isSupportAM = false;
    private RadioManager radioManager;
    private RadioManager.FmBandDescriptor fmBandDescriptor;
    private RadioManager.FmBandConfig fmBandConfig;
    private RadioManager.AmBandDescriptor amBandDescriptor;
    private RadioManager.AmBandConfig amBandConfig;
    private List<RadioManager.ModuleProperties> modules = new ArrayList<>();
    private RadioTuner radioTuner;
    private RadioTuner.Callback radioTunerCallback = new RadioTunerCallback();

    // 扩展↓
    private RadioExternTuner radioExternTuner;
    private RadioExternTuner.Callback radioExternalTunerCallback = new RadioExternalCallback();
    // 扩展↑

    // 音频焦点相关
    private boolean hasAudioFocus;
    // Radio开关状态标志位
    private boolean isOpen;
    private Car car;
    private ServiceConnection carConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnected = true;
            try {
                // The CarAudioManager only needs to be retrieved once.
                if (audioManager == null) {
                    audioManager = (CarAudioManager) car.getCarManager(android.car.Car.AUDIO_SERVICE);
                    audioAttributes = audioManager.getAudioAttributesForCarUsage(CarAudioManager.CAR_AUDIO_USAGE_RADIO);
                }
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
        }
    };
    private CarAudioManager audioManager;
    private AudioAttributes audioAttributes;
    private AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    hasAudioFocus = true;
                    openRadioBandInternal(currentRadioBand);
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    hasAudioFocus = false;
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    hasAudioFocus = false;
                    closeInternal();
                    break;

                default:
                    break;
            }
        }
    };

    public static YqSDK getInstance() {
        if (instance == null) {
            synchronized (YqSDK.class) {
                if (instance == null) {
                    instance = new YqSDK();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        return initInternal();
    }

    @Override
    public boolean isInited() {
        return isInited;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean isHasAudioFocus() {
        return hasAudioFocus;
    }

    @Override
    public boolean isRadioOpen() {
        return isOpen;
    }

    @Override
    public boolean isSupportFM() {
        return isSupportFM;
    }

    @Override
    public boolean isSupportAM() {
        return isSupportAM;
    }

    @Override
    public boolean addLocalFMStatusListener(LocalFMStatusListener l) {
        return StatusDispatcher.getInstance().addListener(l);
    }

    @Override
    public boolean removeLocalFMStatusListener(LocalFMStatusListener l) {
        return StatusDispatcher.getInstance().removeListener(l);
    }

    @Override
    public boolean tuneFM(int frequency) {
        try {
            if (!isSupportFM()) {
                return false;
            }
            if (radioTuner == null) {
                return false;
            }
            switchBand(BandType.FM);
            int band = RadioManager.BAND_FM;
            ProgramSelector selector = ProgramSelector.createAmFmSelector(band, frequency);
            radioTuner.tune(selector);
            statusDispatcher.onTuningFM(frequency);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "tuneFM error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean tuneAM(int frequency) {
        try {
            if (!isSupportAM()) {
                return false;
            }
            if (radioTuner == null) {
                return false;
            }
            switchBand(BandType.AM);
            int band = RadioManager.BAND_AM;
            ProgramSelector selector = ProgramSelector.createAmFmSelector(band, frequency);
            radioTuner.tune(selector);
            statusDispatcher.onTuningAM(frequency);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "tuneAM error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scanDown() {
        try {
            // 显式向下搜台
            if (radioTuner == null) {
                return false;
            }
            int status = radioTuner.scan(RadioTuner.DIRECTION_DOWN, true);
            if (status == RadioManager.STATUS_OK) {
                statusDispatcher.onScanStart();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "scanDown error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scanUp() {
        try {
            // 显式向上搜台
            if (radioTuner == null) {
                return false;
            }
            int status = radioTuner.scan(RadioTuner.DIRECTION_UP, true);
            if (status == RadioManager.STATUS_OK) {
                statusDispatcher.onScanStart();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "scanUp error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean stepNext() {
        return step(RadioTuner.DIRECTION_DOWN);
    }

    @Override
    public boolean stepPrevious() {
        return step(RadioTuner.DIRECTION_UP);
    }

    private boolean step(int direction) {
        try {
            if (radioTuner == null) {
                return false;
            }
            int status = radioTuner.step(direction, true);
            return status == RadioManager.STATUS_OK;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "step" + (direction == RadioTuner.DIRECTION_DOWN ? "down" : "up") + " error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public XMRadioStation getCurrentStation() {
        if (radioTuner == null) {
            return null;
        }
        RadioManager.ProgramInfo[] infos = new RadioManager.ProgramInfo[1];
        int status;
        try {
            status = radioTuner.getProgramInformation(infos);
        } catch (Exception e) {
            Log.e(TAG, "getCurrentStation: \n" + Log.getStackTraceString(e));
            return null;
        }
        if (status != RadioManager.STATUS_OK) {
            return null;
        }
        if (infos == null || infos.length < 1 || infos[0] == null) {
            return null;
        }
        RadioManager.ProgramInfo info = infos[0];

        // 电台频道
        int channel = info.getChannel();
        // 电台子频道
        int subChannel = info.getSubChannel();
        // 是否是有效电台
        boolean isValid = info.isTuned();
        XMRadioMetadata xmMetadata = null;
        RadioMetadata metadata = info.getMetadata();
        if (metadata != null) {
            // 频道信息(Hz)
            String rds = metadata.getString(RadioMetadata.METADATA_KEY_RDS_PS);
            // 节目艺术家
            String artist = metadata.getString(RadioMetadata.METADATA_KEY_ARTIST);
            // 节目名称
            String title = metadata.getString(RadioMetadata.METADATA_KEY_TITLE);
            xmMetadata = new XMRadioMetadata(rds, artist, title);
        }
        return new XMRadioStation(channel, subChannel, currentRadioBand, isValid, xmMetadata);
    }

    private boolean isFMBand() {
        if (!isSupportFM()) {
            return false;
        }
        return currentRadioBand == RadioManager.BAND_FM
                || currentRadioBand == RadioManager.BAND_FM_HD;
    }

    private boolean isAMBand() {
        if (!isSupportAM()) {
            return false;
        }
        return currentRadioBand == RadioManager.BAND_AM
                || currentRadioBand == RadioManager.BAND_AM_HD;
    }

    @Override
    public boolean openRadio() {
        int status = openRadioBandInternal(currentRadioBand);
        return status == RadioManager.STATUS_OK;
    }

    @Override
    public void closeRadio() {
        closeInternal();
    }

    @Nullable
    public BandType getCurrentBand() {
        if (isFMBand()) {
            return BandType.FM;
        }
        if (isAMBand()) {
            return BandType.AM;
        }
        return null;
    }

    @Override
    public void switchBand(BandType band) {
        if (band == null) {
            return;
        }
        switch (band) {
            case AM:
                openRadioBandInternal(RadioManager.BAND_AM);
                break;
            case FM:
                openRadioBandInternal(RadioManager.BAND_FM);
                break;
        }
    }

    @Override
    public void getInternalRecord() {
        // 未找到接口
    }

    private boolean initInternal() {
        try {
            if (appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
                car = Car.createCar(appContext, carConn);
                car.connect();
            }
            radioManager = (RadioManager) appContext.getSystemService(Context.RADIO_SERVICE);
            if (radioManager == null) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * radio manager is null");
                return false;
            }

            int status = radioManager.listModules(modules);
            if (status != RadioManager.STATUS_OK) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * listModules failure");
                return false;
            }

            // 扩展↓
            int externalStatus = radioManager.listExternModules();
            if (externalStatus != RadioManager.STATUS_OK) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * listExternModules failure");
                return false;
            }
            // 扩展↑
            if (modules.size() == 0) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * listModules size == 0");
                return false;
            }
            // 只有两个band(AM/FM)
            for (RadioManager.BandDescriptor descriptor : modules.get(0).getBands()) {
                if (fmBandDescriptor == null && descriptor.isFmBand()) {
                    fmBandDescriptor = (RadioManager.FmBandDescriptor) descriptor;
                    if (fmBandDescriptor != null) {
                        isSupportFM = true;
                    }
                }

                if (amBandDescriptor == null && descriptor.isAmBand()) {
                    amBandDescriptor = (RadioManager.AmBandDescriptor) descriptor;
                    if (amBandDescriptor != null) {
                        isSupportAM = true;
                    }
                }
            }

            if (!isSupportAM() && !isSupportFM()) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * don't support AM and FM");
                return false;
            }

            // 开启立体声, 需要设备支持
            if (isSupportFM()) {
                fmBandConfig = new RadioManager.FmBandConfig.Builder(fmBandDescriptor)
                        .setStereo(true)
                        .build();
            }
            if (isSupportAM()) {
                amBandConfig = new RadioManager.AmBandConfig.Builder(amBandDescriptor)
                        .setStereo(true)
                        .build();
            }

            isInited = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "initInternal error:\n" + e.getMessage());
            return false;
        }
    }

    private int openRadioBandInternal(int radioBand) {
        try {
            int requestStatus = requestAudioFocus();
            if (requestStatus != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return RadioManager.STATUS_ERROR;
            }
            RadioManager.BandConfig bandConfig = getRadioConfig(radioBand);
            if (bandConfig == null) {
                return RadioManager.STATUS_ERROR;
            }

            currentRadioBand = radioBand;
            if (radioTuner != null) {
                radioTuner.setConfiguration(bandConfig);
            } else {
                int moduleId = modules.get(0).getId();
                boolean withAudio = true;
                radioTuner = radioManager.openTuner(
                        moduleId,
                        bandConfig,
                        withAudio,
                        radioTunerCallback,
                        null);
                statusDispatcher.onRadioOpen();
            }
            // 扩展↓
            if (radioExternTuner == null) {
                radioExternTuner = radioManager.openExternTuner(true, radioExternalTunerCallback, null);
            }
            // 扩展↑
            statusDispatcher.onBandChanged(getCurrentBand());
            isOpen = true;
            return RadioManager.STATUS_OK;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "openRadioBandInternal band:" + radioBand + " error:\n" + e.getMessage());
            return RadioManager.STATUS_BAD_VALUE;
        }
    }

    private int requestAudioFocus() {
        int status = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        try {
            status = audioManager.requestAudioFocus(
                    listener,
                    audioAttributes,
                    AudioManager.AUDIOFOCUS_GAIN,
                    0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            hasAudioFocus = true;
        }
        return status;
    }

    private void abandonAudioFocus() {
        if (audioManager == null) {
            return;
        }
        audioManager.abandonAudioFocus(listener, audioAttributes);
    }

    private void openInternal() {

    }

    private void closeInternal() {
        try {
            abandonAudioFocus();
            if (radioTuner != null) {
                radioTuner.close();
                radioTuner = null;
            }
            isOpen = false;
            statusDispatcher.onRadioClose();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "closeInternal error:\n" + e.getMessage());
        }
    }

    @Nullable
    private RadioManager.BandConfig getRadioConfig(int selectedRadioBand) {
        switch (selectedRadioBand) {
            case RadioManager.BAND_AM:
            case RadioManager.BAND_AM_HD:
                return amBandConfig;
            case RadioManager.BAND_FM:
            case RadioManager.BAND_FM_HD:
                return fmBandConfig;
            default:
                return null;
        }
    }

    private class RadioTunerCallback extends RadioTuner.Callback {
        @Override
        public void onProgramInfoChanged(RadioManager.ProgramInfo info) {
            if (info == null) {
                return;
            }
            // 电台频道
            int channel = info.getChannel();
            // 电台子频道
            int subChannel = info.getSubChannel();
            boolean isValid = info.isTuned();
            XMRadioMetadata xmMetadata = null;
            RadioMetadata metadata = info.getMetadata();
            if (metadata != null) {
                // 频道信息(Hz)
                String rds = metadata.getString(RadioMetadata.METADATA_KEY_RDS_PS);
                // 节目艺术家
                String artist = metadata.getString(RadioMetadata.METADATA_KEY_ARTIST);
                // 节目名称
                String title = metadata.getString(RadioMetadata.METADATA_KEY_TITLE);
                xmMetadata = new XMRadioMetadata(rds, artist, title);
            }
            XMRadioStation station = new XMRadioStation(channel, subChannel, currentRadioBand, isValid, xmMetadata);
            statusDispatcher.onNewStation(station);
        }

        @Override
        public void onConfigurationChanged(RadioManager.BandConfig config) {
            if (config == null) {
                return;
            }
            currentRadioBand = config.getType();
        }

        @Override
        public void onControlChanged(boolean control) {
            if (!control) {
                YqSDK.getInstance().closeInternal();
            } else {
                if (radioTuner == null) {
                    YqSDK.getInstance().openRadioBandInternal(currentRadioBand);
                }
            }
        }

        @Override
        public void onError(int status) {
            if (status == RadioTuner.ERROR_HARDWARE_FAILURE
                    || status == RadioTuner.ERROR_SERVER_DIED) {
                if (radioTuner != null) {
                    radioTuner.close();
                    radioTuner = null;
                }
            }
        }


    }

    // 扩展↓
    private class RadioExternalCallback extends RadioExternTuner.Callback {
        @Override
        public void onStereoChanged(int change) {
            Log.d(TAG, "onStereoChanged(); stereo: " + change);
        }

        @Override
        public void onTunerState(int state) {
            Log.d(TAG, "onTunerState(); state: " + state);
        }

        @Override
        public void onSignalqualityChange(int change) {
            Log.d(TAG, "onSignalqualityChange(); change: " + change);
        }
    }
    // 扩展↑
    private static class StatusDispatcher implements LocalFMStatusListener {
        private static StatusDispatcher instance;
        private List<LocalFMStatusListener> listeners = new ArrayList<>();

        public static StatusDispatcher getInstance() {
            if (instance == null) {
                synchronized (StatusDispatcher.class) {
                    if (instance == null) {
                        instance = new StatusDispatcher();
                    }
                }
            }
            return instance;
        }

        private boolean addListener(LocalFMStatusListener l) {
            if (l == null) {
                return false;
            }
            if (listeners.contains(l)) {
                return false;
            }
            return listeners.add(l);
        }

        private boolean removeListener(LocalFMStatusListener l) {
            return listeners.remove(l);
        }

        @Override
        public void onRadioOpen() {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onRadioOpen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onRadioClose() {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                listener.onRadioClose();
            }
        }

        @Override
        public void onTuningAM(int frequency) {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onTuningAM(frequency);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onTuningFM(int frequency) {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onTuningFM(frequency);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onScanStart() {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onScanStart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onNewStation(XMRadioStation station) {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onNewStation(station);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBandChanged(BandType band) {
            for (LocalFMStatusListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                try {
                    listener.onBandChanged(band);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(int code, String msg) {

        }
    }

}
