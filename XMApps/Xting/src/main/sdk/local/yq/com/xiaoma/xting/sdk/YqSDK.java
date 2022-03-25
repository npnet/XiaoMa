package com.xiaoma.xting.sdk;

import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioExternTuner;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioMetadata;
import android.hardware.radio.RadioTuner;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.xting.common.AccOffOnSignal;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.local.ui.ManualFragment;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioMetadata;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    // private boolean isConnected = false;
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

    // Radio开关状态标志位
    private boolean isOpen = false;
    private CarAudioHandler carAudioHandler;

    private boolean isScanning = false;
    private RadioExternTuner radioExternTuner;

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
        XmCarEventDispatcher.getInstance().registerEvent(AccOffOnSignal.getInstance());
        return initInternal();
    }

    @Override
    public boolean isInited() {
        return isInited;
    }

    @Override
    public boolean isConnected() {
        if (carAudioHandler == null) {
            return false;
        }
        return carAudioHandler.isConnected();
    }

    @Override
    public boolean isHasAudioFocus() {
        // return hasAudioFocus;
        return AudioFocusManager.getInstance().getAudioType() == 3;
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
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        try {
            if (!isRadioOpen()) {
                openRadio();
            }
            if (!isSupportFM()) {
                return false;
            }
            if (radioTuner == null) {
                return false;
            }
            XMRadioStation currentStation = getCurrentStation();
            if (currentStation != null && currentStation.getChannel() == frequency) {
                return true;
            }
            if (currentRadioBand != RadioManager.BAND_FM) {
                switchBand(BandType.FM);
            }
            int band = RadioManager.BAND_FM;
            ProgramSelector selector = ProgramSelector.createAmFmSelector(band, frequency);
            radioTuner.tune(selector);
            statusDispatcher.onTuningFM(frequency);
            Log.d(TAG, "tuneFM: " + frequency);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "tuneFM error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean tuneAM(int frequency) {
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        try {
            if (!isRadioOpen()) {
                openRadio();
            }
            if (!isSupportAM()) {
                return false;
            }
            if (radioTuner == null) {
                return false;
            }
            XMRadioStation currentStation = getCurrentStation();
            if (currentStation != null && currentStation.getChannel() == frequency) {
                return true;
            }

            if (currentRadioBand != RadioManager.BAND_AM) {
                switchBand(BandType.AM);
            }
            int band = RadioManager.BAND_AM;
            ProgramSelector selector = ProgramSelector.createAmFmSelector(band, frequency);
            radioTuner.tune(selector);
            statusDispatcher.onTuningAM(frequency);
            Log.d(TAG, "tuneAM: " + frequency);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "tuneAM error:\n" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scanAll(BandType bandType) {
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        if (!isRadioOpen()) {
            openRadio();
        }
        if (getCurrentBand() != bandType) {
            switchBand(bandType);
        }
        if (radioExternTuner != null) {
            radioExternTuner.autoP();
            isScanning = true;
            Log.d(TAG, "autoP");
        }
        return true;
    }

    @Override
    public boolean cancel() {
        int result = -1;
        if (radioTuner != null) {
            result = radioTuner.cancel();
            StatusDispatcher.getInstance().setInterruptByCancel(true);
            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StatusDispatcher.getInstance().setInterruptByCancel(false);
                }
            }, 500);
            statusDispatcher.onCancel();
            isScanning = false;
            Log.d(TAG, "cancel");
        }
        return result == RadioManager.STATUS_OK;
    }

    @Override
    public boolean scanDown() {
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        try {
            if (!isRadioOpen()) {
                openRadio();
            }
            // 显式向下搜台
            if (radioTuner == null) {
                return false;
            }
            int status = radioTuner.scan(RadioTuner.DIRECTION_DOWN, true);
            Log.d(TAG, "scanDown: ");
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
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        try {
            if (!isRadioOpen()) {
                openRadio();
            }
            // 显式向上搜台
            if (radioTuner == null) {
                return false;
            }
            int status = radioTuner.scan(RadioTuner.DIRECTION_UP, true);
            Log.d(TAG, "scanUp: ");
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
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        Log.d(TAG, "stepNext: ");
        if (!isRadioOpen()) {
            openRadio();
        }
        return step(RadioTuner.DIRECTION_UP);
    }

    @Override
    public boolean stepPrevious() {
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        Log.d(TAG, "stepPrevious: ");
        if (!isRadioOpen()) {
            openRadio();
        }
        return step(RadioTuner.DIRECTION_DOWN);
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
        return new XMRadioStation(channel, subChannel, currentRadioBand, xmMetadata);
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
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        if (isRadioOpen()) {
            return true;
        }
        if (carAudioHandler != null) {
            carAudioHandler.setMuted(false);
        }
        int status = openRadioBandInternal(currentRadioBand); // 打开收音机的时候
        Log.d(TAG, "openRadio: " + (status == RadioManager.STATUS_OK));
        return status == RadioManager.STATUS_OK;
    }

    @Override
    public void closeRadio() {
        closeInternal();
        AudioFocusManager.getInstance().abandonFMFocus();
        Log.d(TAG, "closeRadio: ");
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
    public boolean switchBand(BandType band) {
        if (!AudioFocusManager.getInstance().requestFMFocus()) return false;
        if (band == null) {
            return false;
        }
        if (!isRadioOpen()) {
            openRadio();
        }
        Log.d(TAG, "switchBand: " + band);
        switch (band) {
            case AM:
                currentRadioBand = RadioManager.BAND_AM;
                changeBand(RadioManager.BAND_AM);// 切换到AM的时候
                break;
            case FM:
                currentRadioBand = RadioManager.BAND_FM;
                changeBand(RadioManager.BAND_FM); //
                break;
        }
        return true;
    }

    @Override
    public void getInternalRecord() {
        // 未找到接口
    }

    @Override
    public boolean setMuted(boolean muted) {
        if (carAudioHandler == null) return false;
        if (radioExternTuner == null) return false;
        Log.d(TAG, "setMuted: " + muted);
        boolean carAudioB = carAudioHandler.setMuted(muted);
        int i = radioTuner.setMute(muted);
        boolean radioTunerB = i == RadioManager.STATUS_OK;
        return carAudioB && radioTunerB;
    }

    private void changeBand(int band) {
        switch (band) {
            case RadioManager.BAND_AM:
            case RadioManager.BAND_AM_HD:
                if (radioExternTuner != null) {
                    KLog.e(TAG, "change to am");
                    radioExternTuner.changeBand(RadioExternTuner.AM_BAND);
                }
                break;
            case RadioManager.BAND_FM:
            case RadioManager.BAND_FM_HD:
                if (radioExternTuner != null) {
                    KLog.e(TAG, "change to fm");
                    radioExternTuner.changeBand(RadioExternTuner.FM_BAND);
                }
                break;
        }
        statusDispatcher.onBandChanged(getCurrentBand());
    }

    private boolean initInternal() {
        try {
//            if (appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
//                car = Car.createCar(appContext, carConn);
//                car.connect();
//            }
            carAudioHandler = new CarAudioHandler(appContext);
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
            if (modules.size() == 0) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * listModules size == 0");
                return false;
            }
            // 只取第一个 Module Properties 即可
            RadioManager.ModuleProperties moduleProperties = modules.get(0);
            if (moduleProperties == null) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * module properties is null");
                return false;
            }
            RadioManager.BandDescriptor[] bands = moduleProperties.getBands();
            if (bands == null || bands.length <= 0) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * bands is null or empty");
                return false;
            }


            // 只有两个band(AM/FM)
            for (RadioManager.BandDescriptor descriptor : bands) {
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
            RadioManager.BandConfig bandConfig = getRadioConfig(radioBand);
            if (bandConfig == null) {
                return RadioManager.STATUS_ERROR;
            }

            currentRadioBand = radioBand;
            if (radioTuner == null) {
                int moduleId = modules.get(0).getId();
                boolean withAudio = true;
                radioTuner = radioManager.openTuner(
                        moduleId,
                        bandConfig,
                        withAudio,
                        radioTunerCallback,
                        null);
                radioTuner.setMute(false);
                radioExternTuner = radioManager.openExternTuner(new RadioExternTuner.Callback() {
                    @Override
                    public void onProgramListUpdated(int type, List<Long> data) {
                        super.onProgramListUpdated(type, data);
                        isScanning = false;
                        if (data == null) {
                            KLog.e(TAG, "onProgramListUpdated null");
                            return;
                        }
                        KLog.e(TAG, "onProgramListUpdated" + data.size());
                        final List<XMRadioStation> list = new ArrayList<>();
                        for (Long datum : data) {
                            if (datum == null) {
                                continue;
                            }
                            XMRadioMetadata metadata = new XMRadioMetadata(String.valueOf(datum), "", "");
                            XMRadioStation radioStation = new XMRadioStation(datum.intValue(), 0, type, metadata);
                            list.add(radioStation);
                        }
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                statusDispatcher.onScanAllResult(list);
                            }
                        });
                    }
                });
                radioExternTuner.changeBand(currentRadioBand == RadioManager.BAND_FM ? RadioExternTuner.FM_BAND : RadioExternTuner.AM_BAND);
                statusDispatcher.onRadioOpen();
            }
            statusDispatcher.onBandChanged(getCurrentBand());
            isOpen = true;
            return RadioManager.STATUS_OK;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "openRadioBandInternal band:" + radioBand + " error:\n" + e.getMessage());
            return RadioManager.STATUS_BAD_VALUE;
        }
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    private void openInternal() {

    }

    private void closeInternal() {
        try {
            //get last radio
            XMRadioStation currentStation = getCurrentStation();
            if (radioTuner != null) {
                radioTuner.setMute(true);
                radioTuner.close();
                radioExternTuner = null;
                radioTuner = null;
            }
            isOpen = false;
            statusDispatcher.onRadioClose(currentStation);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "closeInternal error:\n" + e.getMessage());
        }
    }

    public void closeRadioWithoutFocus() {
        try {
            XMRadioStation currentStation = getCurrentStation();
            if (radioTuner != null) {
                radioTuner.setMute(true);
                radioTuner.close();
                radioExternTuner = null;
                radioTuner = null;
            }
            isOpen = false;
            statusDispatcher.onRadioClose(currentStation);
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
            long value = info.getSelector().getPrimaryId().getValue();
            // 电台频道
            int channel = info.getChannel();
            // 电台子频道
            int subChannel = info.getSubChannel();
            XMRadioMetadata xmMetadata = null;
            RadioMetadata metadata = info.getMetadata();
            if (metadata != null) {
                // 频道信息(Hz)
                String rds = metadata.getString(RadioMetadata.METADATA_KEY_RDS_PS);
                // 节目艺术家
                String artist = metadata.getString(RadioMetadata.METADATA_KEY_ARTIST);
                // 节目名称
                String title = metadata.getString(RadioMetadata.METADATA_KEY_TITLE);
                xmMetadata = new XMRadioMetadata(String.valueOf(value), artist, title);
            }
            XMRadioStation station = new XMRadioStation(channel, subChannel, currentRadioBand, xmMetadata);
            if (isOpen) {
                statusDispatcher.onNewStation(station);
            }
        }

        @Override
        public void onConfigurationChanged(RadioManager.BandConfig config) {
            if (config == null) {
                return;
            }
            //这里在 调频到FM后关闭再打开同时切换到AM时回调的type却为FM有问题，所以先注释了
//            currentRadioBand = config.getType();
        }

        @Override
        public void onControlChanged(boolean control) {
            if (!control) {
                YqSDK.getInstance().closeInternal();
            } else {
                if (radioTuner == null) {
                    YqSDK.getInstance().openRadioBandInternal(currentRadioBand); // 重新打开的时候
                }
            }
        }

        @Override
        public void onError(int status) {
            KLog.e(TAG, "error message " + status);
            if (status == RadioTuner.ERROR_HARDWARE_FAILURE
                    || status == RadioTuner.ERROR_SERVER_DIED) {
                if (radioTuner != null) {
                    radioTuner.close();
                    radioTuner = null;
                    radioExternTuner = null;
                }
            }
        }


    }

    private static class StatusDispatcher implements LocalFMStatusListener {
        private static StatusDispatcher instance;
        private boolean isInterruptByCancel;
        private CopyOnWriteArrayList<LocalFMStatusListener> listeners = new CopyOnWriteArrayList<>();

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
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
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
            });
        }

        @Override
        public void onRadioClose(final XMRadioStation xmRadioStation) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        if (listener == null) {
                            continue;
                        }
                        listener.onRadioClose(xmRadioStation);
                    }
                }
            });
        }

        @Override
        public void onTuningAM(final int frequency) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
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
            });
        }

        @Override
        public void onTuningFM(final int frequency) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
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
            });
        }

        @Override
        public void onScanStart() {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
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
            });
        }

        @Override
        public void onNewStation(final XMRadioStation station) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        if (listener == null || (isInterruptByCancel
                                && !(listener instanceof ManualFragment.FMListener))) {
                            continue;
                        }
                        try {
                            listener.onNewStation(station);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onScanAllResult(final List<XMRadioStation> stations) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        if (listener == null) {
                            continue;
                        }
                        try {
                            listener.onScanAllResult(stations);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onBandChanged(final BandType band) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
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
            });
        }

        @Override
        public void onCancel() {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        if (listener == null) {
                            continue;
                        }
                        try {
                            listener.onCancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onError(final int code, final String msg) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        if (listener == null) {
                            continue;
                        }
                        try {
                            listener.onError(code, msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public void setInterruptByCancel(boolean isInterruptByCancel) {
            this.isInterruptByCancel = isInterruptByCancel;
        }
    }

}
