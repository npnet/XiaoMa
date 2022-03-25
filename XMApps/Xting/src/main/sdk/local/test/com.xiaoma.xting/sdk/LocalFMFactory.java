package com.xiaoma.xting.sdk;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.xiaoma.utils.log.KLog;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioMetadata;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author youthyJ
 * @date 2018/10/22
 */
public class LocalFMFactory {
    public static final String TAG = LocalFMFactory.class.getSimpleName();

    private LocalFMFactory() throws Exception {
        throw new Exception();
    }

    private static final int BAND_FM = XtingConstants.FMAM.TYPE_FM;
    private static final int BAND_AM = XtingConstants.FMAM.TYPE_AM;

    private static int currentBandType = BAND_FM;
    private static XMRadioStation currentStation = createTestModel();

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static AudioManager mAudioManager;
    private static AudioManager.OnAudioFocusChangeListener mFocusChangeListener;

    private static CopyOnWriteArrayList<LocalFMStatusListener> listeners = new CopyOnWriteArrayList<>();

    public static LocalFM testSDK = new LocalFM() {
        // Radio开关状态标志位
        private boolean isOpen;

        @Override
        public boolean init(Context context) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        //Pause playback
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            break;
                        //Resume playback
                        case AudioManager.AUDIOFOCUS_GAIN:
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            break;
                        //Stop playback
                        case AudioManager.AUDIOFOCUS_LOSS:
                            closeRadio();
                            break;
                        default:
                    }
                }
            };
            return true;
        }

        @Override
        public boolean isInited() {
            return true;
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public boolean isHasAudioFocus() {
            return true;
        }

        @Override
        public boolean isRadioOpen() {
            return isOpen;
        }

        @Override
        public boolean isSupportFM() {
            return true;
        }

        @Override
        public boolean isSupportAM() {
            return true;
        }

        @Override
        public boolean addLocalFMStatusListener(LocalFMStatusListener l) {
            if (l == null) {
                return false;
            }
            if (listeners.contains(l)) {
                return false;
            }
            return listeners.add(l);
        }

        @Override
        public boolean removeLocalFMStatusListener(LocalFMStatusListener l) {
            return listeners.remove(l);
        }

        @Override
        public boolean tuneFM(final int frequency) {
            KLog.d(TAG, "tuneFM:" + frequency);
            currentStation.setChannel(frequency);
            currentBandType = BAND_FM;
            currentStation.setRadioBand(BAND_FM);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KLog.d("kaka", "tuneFM: " + frequency);
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onTuningFM(frequency);
                    }
                }
            }, 50);
            return true;
        }

        @Override
        public boolean tuneAM(final int frequency) {
            KLog.d(TAG, "tuneAM:" + frequency);
            currentStation.setChannel(frequency);
            currentBandType = BAND_AM;
            currentStation.setRadioBand(BAND_AM);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onTuningAM(frequency);
                    }
                }
            }, 50);
            return false;
        }

        @Override
        public boolean scanAll(BandType band) {
            currentBandType = (band == BandType.FM ? XtingConstants.FMAM.TYPE_FM : XtingConstants.FMAM.TYPE_AM);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ArrayList<XMRadioStation> stations = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        stations.add(createTestModel());
                    }

                    for (LocalFMStatusListener listener : listeners) {
                        listener.onScanAllResult(stations);
                    }
                }
            }, 8000);
            return false;
        }

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public boolean scanDown() {
            KLog.d(TAG, "scanDown");
            handler.removeCallbacksAndMessages(null);
            for (LocalFMStatusListener listener : listeners) {
                listener.onScanStart();
            }
            final int stepCount;
            final int lucky;
            if (currentBandType == XtingConstants.FMAM.TYPE_FM) {
                stepCount = Math.round(currentStation.getChannel() / 100f - XtingConstants.FMAM.getFMStart() / 100f);
            } else {
                stepCount = Math.round((currentStation.getChannel() - XtingConstants.FMAM.getAMStart()) / 9f);
            }

            if (stepCount == 0) {
                lucky = 0;
            } else if (stepCount > 0 && stepCount <= 20) {
                lucky = new Random().nextInt(stepCount);
            } else {
                lucky = new Random().nextInt(20);
            }
            handler.postDelayed(new Runnable() {
                private int n = 0;

                @Override
                public void run() {
                    XMRadioStation station = stepAction(currentStation, false);
                    currentStation.setChannel(station.getChannel());
                    if (n != lucky) {
                        handler.postDelayed(this, 50);
                        n++;
                    }
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onNewStation(station);
                    }
                }
            }, 50);
            return true;
        }

        @Override
        public boolean scanUp() {
            KLog.d(TAG, "scanUp");
            handler.removeCallbacksAndMessages(null);
            for (LocalFMStatusListener listener : listeners) {
                listener.onScanStart();
            }
            final int stepCount;
            final int lucky;
            if (currentBandType == XtingConstants.FMAM.TYPE_FM) {
                stepCount = Math.round(XtingConstants.FMAM.getFMEnd() / 100f - currentStation.getChannel() / 100f);
            } else {
                stepCount = Math.round((XtingConstants.FMAM.getAMEnd() - currentStation.getChannel()) / 9f);
            }

            if (stepCount == 0) {
                lucky = 0;
            } else if (stepCount > 0 && stepCount <= 20) {
                lucky = new Random().nextInt(stepCount);
            } else {
                lucky = new Random().nextInt(20);
            }

            handler.postDelayed(new Runnable() {
                private int n = 0;

                @Override
                public void run() {
                    XMRadioStation station = stepAction(currentStation, true);
                    currentStation.setChannel(station.getChannel());
                    if (n != lucky) {
                        handler.postDelayed(this, 50);
                        ++n;
                    }
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onNewStation(station);
                    }
                }
            }, 50);
            return true;
        }

        @Override
        public boolean stepNext() {
            KLog.d(TAG, "stepNext");
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentStation = stepAction(currentStation, true);
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onNewStation(currentStation);
                    }
                }
            }, 50);
            return true;
        }

        @Override
        public boolean stepPrevious() {
            KLog.d(TAG, "stepPrevious");
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentStation = stepAction(currentStation, false);
                    for (LocalFMStatusListener listener : listeners) {
                        listener.onNewStation(currentStation);
                    }
                }
            }, 50);
            return true;
        }

        @Override
        public XMRadioStation getCurrentStation() {
            return currentStation;
        }

        @Override
        public boolean openRadio() {
            KLog.d(TAG, "openRadio");
            requestFocus();
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            for (LocalFMStatusListener listener : listeners) {
                listener.onRadioOpen();
            }
            isOpen = true;
            return true;
        }

        @Override
        public void closeRadio() {
            KLog.d(TAG, "closeRadio");
            abandonFocus();
            isOpen = false;
            for (LocalFMStatusListener listener : listeners) {
                listener.onRadioClose(currentStation);
            }
        }

        @Override
        public BandType getCurrentBand() {
            if (currentBandType == BAND_FM) {
                return BandType.FM;
            }
            return BandType.AM;
        }

        @Override
        public boolean switchBand(BandType band) {
            KLog.d(TAG, "switchBand: " + (band == BandType.FM ? "FM" : "AM"));
            if (band == null) {
                return false;
            }
            switch (band) {
                case AM:
                    if (currentBandType != BAND_AM) {
                        currentBandType = BAND_AM;
                        currentStation = createTestModel();
                        for (LocalFMStatusListener listener : listeners) {
                            listener.onBandChanged(band);
                            listener.onNewStation(currentStation);
                        }
                    }
                    break;
                case FM:
                    if (currentBandType != BAND_FM) {
                        currentBandType = BAND_FM;
                        currentStation = createTestModel();
                        for (LocalFMStatusListener listener : listeners) {
                            listener.onBandChanged(band);
                            listener.onNewStation(currentStation);
                        }
                    }
                    break;
            }
            return true;
        }

        @Override
        public void getInternalRecord() {

        }

        @Override
        public boolean setMuted(boolean muted) {
            return false;
        }
    };

    public static LocalFM getSDK() {
        return testSDK;
    }

    private static XMRadioStation createTestModel() {
        Random random = new Random();
        int id;
        if (currentBandType == BAND_FM) {
            id = (random.nextInt(210) + 870) * 100;
        } else {
            id = random.nextInt(1098) + 522;
        }
        XMRadioMetadata metadata = new XMRadioMetadata(
                "[source " + id + "]",
                "[artist " + id + "]",
                "[title " + id + "]");
        XMRadioStation xmRadioStation = new XMRadioStation(id, id, currentBandType, metadata);
        return xmRadioStation;
    }

    private static XMRadioStation stepAction(XMRadioStation currentStation, boolean add) {
        int channel = currentStation.getChannel();
        int radioBand = currentStation.getRadioBand();
        XMRadioMetadata metadata = currentStation.getMetadata();
        if (add) {
            if (currentStation.getRadioBand() == BAND_FM) {
                return new XMRadioStation(channel + 100, channel, radioBand, metadata);
            } else {
                return new XMRadioStation(channel + 9, channel, radioBand, metadata);
            }
        } else {
            if (currentStation.getRadioBand() == BAND_FM) {
                return new XMRadioStation(channel - 100, channel, radioBand, metadata);
            } else {
                return new XMRadioStation(channel - 9, channel, radioBand, metadata);
            }
        }
    }

    private static boolean requestFocus() {
        if (mFocusChangeListener != null && mFocusChangeListener != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    mAudioManager.requestAudioFocus(mFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN);
        }
        return false;
    }

    private static boolean abandonFocus() {
        if (mFocusChangeListener != null && mFocusChangeListener != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    mAudioManager.abandonAudioFocus(mFocusChangeListener);
        }
        return false;
    }
}
