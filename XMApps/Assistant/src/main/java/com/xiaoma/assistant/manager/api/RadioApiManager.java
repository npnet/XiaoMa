package com.xiaoma.assistant.manager.api;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import java.util.List;
import java.util.Random;

/**
 * Created by qiuboxiang on 2019/3/4 11:40
 * Desc:
 */
public class RadioApiManager extends ApiManager {

    private static final String TAG = "QBX 【" + RadioApiManager.class.getSimpleName() + "】";
    private static RadioApiManager mInstance;
    private int[] finishWord = new int[]{R.string.ok, R.string.no_problem, R.string.here};
    public static final int API_PORT = CenterConstants.XTING_PORT;
    public static final int STATE_CALLBACK_PORT = AudioConstants.AudioTypes.XTING;

    private RadioApiManager() {
    }

    public static RadioApiManager getInstance() {
        if (mInstance == null) {
            synchronized (RadioApiManager.class) {
                if (mInstance == null) {
                    mInstance = new RadioApiManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.XTING, API_PORT);
    }

    @Override
    public void init() {
        AudioManager.getInstance().connectAudioClient(this, STATE_CALLBACK_PORT);
        getPlayState();
    }

    @Override
    public void onClientIn(SourceInfo source) {
        int port = source.getPort();
        if (port != STATE_CALLBACK_PORT) {
            return;
        }
        init();
    }

    /**
     * 获取播放状态
     */
    private void getPlayState() {
        Log.d(TAG, "getPlayState");
        requestWithoutTTS(CenterConstants.XtingThirdAction.GET_PLAY_STATE, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {

            }
        });
    }

    /**
     * 播放电台收藏列表
     */
    public void playRadioStationCollection() {
        Log.d(TAG, "playRadioStationCollection");
        request(CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_COLLECTION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.let_us_listen_to_the_collected_radio_station);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 收藏电台
     */
    public void collectRadioStation() {
        if (!isRadioAudioType()) {
            return;
        }
        if (!AudioManager.getInstance().isLocalFMType()) {
            collectProgram();
            return;
        }
        Log.d(TAG, "collectRadioStation");
        request(CenterConstants.XtingThirdAction.COLLECT_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.collected);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 取消收藏电台
     */
    public void cancelCollectRadioStation() {
        if (!isRadioAudioType()) {
            return;
        }
        if (!AudioManager.getInstance().isLocalFMType()) {
            cancelCollectProgram();
            return;
        }
        Log.d(TAG, "cancelCollectRadioStation");
        request(CenterConstants.XtingThirdAction.CANCEL_COLLECT_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.canceled_collect);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 上一电台
     * 如果当前音源不是本地或在线电台，则不做任何操作，并返回false
     */
    public void preRadioStation() {
        if (!isRadioAudioType()) {
            return;
        }
        if (!AudioManager.getInstance().isLocalFMType()) {
            preProgram();
            return;
        }
        Log.d(TAG, "preRadioStation: ");
        request(CenterConstants.XtingThirdAction.PRE_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 下一电台
     */
    public void nextRadioStation(final String ttsContent) {
        if (!isRadioAudioType()) {
            return;
        }

        if (!AudioManager.getInstance().isLocalFMType()) {
            nextProgram();
            return;
        }

        Log.d(TAG, "nextRadioStation: ");
        request(CenterConstants.XtingThirdAction.NEXT_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
            }
        });
    }

    /**
     * 暂停播放电台
     */
    public void pauseRadioStation(final String ttsContent) {
        if (!isRadioAudioType()) {
            return;
        }
        if (!AudioManager.getInstance().isLocalFMType()) {
            pauseProgram("");
            return;
        }
        Log.d(TAG, "pauseRadioStation: ");
        request(CenterConstants.XtingThirdAction.PAUSE_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                closeDialogAndTTS(success, ttsContent);
            }
        });
    }

    /**
     * 继续播放电台
     */
    public void continueToPlayRadioStation() {
        if (!isRadioAudioType()) {
            return;
        }
        if (!AudioManager.getInstance().isLocalFMType()) {
            continuePlayProgram();
            return;
        }
        playLocalRadioStation();
    }

    /**
     * 扫描全部电台
     */
    public void scanRadioStation() {
        request(CenterConstants.XtingThirdAction.SCAN_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 获取当前电台信息：电台频率、电台名称、节目名称
     */
    public void getRadioStationInfo() {
        Log.d(TAG, "getRadioStationInfo: ");
        request(CenterConstants.XtingThirdAction.GET_RADIO_STATION_INFO, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    String programName = extra.getString(CenterConstants.XtingThirdBundleKey.PROGRAM_NAME);//节目名称
                    String radioStationName = extra.getString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_NAME);//电台名称
                    double radioStationFrequency = extra.getDouble(CenterConstants.XtingThirdBundleKey.RADIO_STATION_FREQUENCY);//电台频率
                    String text = !TextUtils.isEmpty(radioStationName) ? radioStationFrequency + radioStationName : String.valueOf(radioStationFrequency);
                    closeAfterSpeak(getString(R.string.this_is, text));
                } else {
                    addFeedbackAndSpeak(R.string.have_not_included_this_radio);
                }
            }
        });
    }

    /**
     * 打开本地电台
     */
    public void playLocalRadioStation() {
        Log.d(TAG, "playLocalRadioStation: ");
        setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
        speakContent( context.getString(R.string.listen_to_the_radio),new WrapperSynthesizerListener(){
            @Override
            public void onCompleted() {
                closeAssistant();
                request(CenterConstants.XtingThirdAction.PLAY_LCOAL_RADIO_STATION, null, new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        Bundle extra = response.getExtra();
                        boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                    }
                });
            }
        });
    }
    //打开本地电台而没有tts播报
    public void playLocalRadioStationNoSpeak(){
        setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
        request(CenterConstants.XtingThirdAction.PLAY_LCOAL_RADIO_STATION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
            }
        });
    }

    /**
     * 收音机切换到AM或FM的相应频率
     */
    public void playRadioStationByFrequency(String amOrFm, int frequency) {
        Log.d(TAG, "playRadioStationByFrequency: ");
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.XtingThirdBundleKey.AM_OR_FM, amOrFm);//值为"fm" 或者"am"
        bundle.putInt(CenterConstants.XtingThirdBundleKey.RADIO_STATION_FREQUENCY, frequency);
        speakContent(getFinishWord(), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                request(CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_FREQUENCY, bundle, new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        Bundle extra = response.getExtra();
                        boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                        if (success) {
                            closeAssistant();
                        } else {
                            addFeedbackAndSpeak(R.string.can_not_find_frequency);
                        }
                    }
                });
            }
        });
    }

    /**
     * 收听指定名称的电台
     */
    public void playRadioStationByName(String name) {
        Log.d(TAG, "playRadioStationByName: ");
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_NAME, name);
        bundle.putBoolean(CenterConstants.XtingThirdBundleKey.RADIO_LOCAL_TTS,true);
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_NAME, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                List<AudioInfo> list = extra.getParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST);
                if (success) {
                    closeAssistant();
//                    closeAfterSpeak(getFinishWord());
                } else {
                    speakThenListening(R.string.can_not_find_radio_station);
                }
            }
        });
    }

    /**
     * 收听指定类型的电台
     * 电台类型：音乐、交通、故事、小说、经济、体育、文艺、资讯、都市、双语、综合、生活、旅游、曲艺、国家台、网络台。
     */
    public void playRadioStationByType(String type) {
        Log.d(TAG, "playRadioStationByType: ");
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_TYPE, type);
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_TYPE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                List<AudioInfo> list = extra.getParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 收听AM
     */
    public void playAM() {
        Log.d(TAG, "playAM: ");
        request(CenterConstants.XtingThirdAction.PLAY_AM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(getFinishWord());
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 收听FM
     */
    public void playFM() {
        Log.d(TAG, "playFM: ");
        request(CenterConstants.XtingThirdAction.PLAY_FM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(getFinishWord());
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 播放节目收藏列表
     */
    public void playProgramCollection() {
        Log.d(TAG, "playProgramCollection: ");
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_PROGRAM_COLLECTION, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.let_us_listen_to_the_collected_radio_station);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 收藏节目
     */
    public void collectProgram() {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            collectRadioStation();
            return;
        }
        Log.d(TAG, "collectProgram: ");
        request(CenterConstants.XtingThirdAction.COLLECT_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.collected);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 取消收藏节目
     */
    public void cancelCollectProgram() {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            cancelCollectRadioStation();
            return;
        }
        Log.d(TAG, "cancelCollectProgram: ");
        request(CenterConstants.XtingThirdAction.CANCEL_COLLECT_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(R.string.canceled_collect);
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 上一节目
     * 如果当前音源不是本地或在线电台，则不做任何操作，并返回false
     */
    public void preProgram() {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            preRadioStation();
            return;
        }
        Log.d(TAG, "preProgram: ");
        requestWithCheckNet(CenterConstants.XtingThirdAction.PRE_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 下一节目
     */
    public void nextProgram() {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            nextRadioStation("");
            return;
        }
        Log.d(TAG, "nextProgram: ");
        requestWithCheckNet(CenterConstants.XtingThirdAction.NEXT_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
            }
        });
    }

    /**
     * 暂停播放节目
     */
    public void pauseProgram(final String ttsContent) {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            pauseRadioStation("");
            return;
        }
        Log.d(TAG, "pauseProgram: ");
        request(CenterConstants.XtingThirdAction.PAUSE_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                closeDialogAndTTS(success, ttsContent);
            }
        });
    }

    /**
     * 继续播放节目
     */
    public void continuePlayProgram() {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            continueToPlayRadioStation();
            return;
        }
        Log.d(TAG, "continuePlayProgram: ");
        requestWithCheckNet(CenterConstants.XtingThirdAction.CONTINUE_PLAY_PROGRAM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 切换节目播放模式为 顺序播放
     */
    public void switchPlayModeToOrder() {
        if (!isRadioAudioType()) {
            return;
        }
        Log.d(TAG, "switchPlayModeToOrder: ");
        request(CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_ORDER, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(context.getString(R.string.setup_succeed));
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 切换节目播放模式为 随机播放
     */
    public void switchPlayModeToRandom() {
        if (!isRadioAudioType()) {
            return;
        }
        Log.d(TAG, "switchPlayModeToRandom: ");
        request(CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_RANDOM, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(context.getString(R.string.setup_succeed));
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 切换节目播放模式为 单曲循环
     */
    public void switchPlayModeToCycle() {
        if (!isRadioAudioType()) {
            return;
        }
        Log.d(TAG, "switchPlayModeToCycle: ");
        request(CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_CYCLE, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(context.getString(R.string.setup_succeed));
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 切换节目播放模式为 列表循环
     */
    public void switchPlayModeToLoop() {
        if (!isRadioAudioType()) {
            return;
        }
        Log.d(TAG, "switchPlayModeToLoop: ");
        request(CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_LOOP, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {
                    closeAfterSpeak(context.getString(R.string.setup_succeed));
                } else {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    /**
     * 打开在线电台：
     * 1.如果有播放记录则直接继续上次内容播放；
     * 2.如果没有播放记录，则播放推荐节目
     */
    public void playProgram(IClientCallback callback) {
        Log.d(TAG, "playProgram: ");
        setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_PROGRAM, null, callback);
    }

    /**
     * 播放指定id的专辑
     */
    public void playAlbum(long id, final String ttsContent) {
        Log.d(TAG, "playAlbum: ");
        Bundle bundle = new Bundle();
        bundle.putLong(CenterConstants.XtingThirdBundleKey.ALBUM_ID, id);
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_ALBUM, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                closeDialogAndTTS(success, ttsContent);
            }
        });
    }

    /**
     * 播放指定id的碎片
     */
    public void playProgramByID(long id, int position) {
        Log.d(TAG, "playProgramByID: ");
        Bundle bundle = new Bundle();
        bundle.putLong(CenterConstants.XtingThirdBundleKey.PROGRAM_ID, id);
        bundle.putInt(CenterConstants.XtingThirdBundleKey.PROGRAM_INDEX, position);
        requestWithCheckNet(CenterConstants.XtingThirdAction.PLAY_PROGRAM_BY_ID, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.XtingThirdBundleKey.RESULT);
                if (success) {

                } else {

                }
            }
        });
    }

    /**
     * 关闭电台
     */
    public void closeRadioStation(String ttsContent) {
        if (!isRadioAudioType()) {
            return;
        }
        if (AudioManager.getInstance().isLocalFMType()) {
            pauseRadioStation(ttsContent);
        } else {
            pauseProgram(ttsContent);
        }
    }

    public void haveLocalRadioPlayRecord(onGetBooleanResultListener listener) {
        Log.d(TAG, "haveLocalRadioPlayRecord");
        request(CenterConstants.XtingThirdAction.GET_HAS_STATIONS, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean haveRecord = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (haveRecord) {
                    listener.onTrue();
                } else {
                    listener.onFalse();
                }
            }
        });
    }

    private boolean isRadioAudioType() {
       /* boolean isRadioAudioType = AudioManager.getInstance().isLocalFMType() || AudioManager.getInstance().isOnlineFMType();
        if (!isRadioAudioType) {
            addFeedbackAndSpeak(R.string.nonsupport_operation);
        }
        return isRadioAudioType;*/
        return true;
    }

    private int getFinishWord() {
        Random random = new Random();
        int index = random.nextInt(finishWord.length);
        return finishWord[index];
    }

    @Override
    String getAppName() {
        return context.getString(R.string.radio_station);
    }
}
