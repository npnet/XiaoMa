package com.xiaoma.cariflytek.iat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.speech.ISSErrors;
import com.iflytek.speech.libisssr;
import com.iflytek.sr.IIsrListener;
import com.iflytek.sr.SrSession;
import com.xiaoma.cariflytek.R;
import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.cariflytek.ivw.LxWakeupMultipleHelper;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.constants.ErrorCode;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IatType;
import com.xiaoma.vr.model.AppState;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.vr.model.ContactList;
import com.xiaoma.vr.model.RadioType;
import com.xiaoma.vr.model.RealTimeRegWord;
import com.xiaoma.vr.model.StateData;
import com.xiaoma.vr.model.WeChatContactList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:离线讯飞识别和oneshot等功能
 */

public class LxIatManager {
    private boolean isOpenSeopt;
    private static final String TAG = LxIatManager.class.getSimpleName();
    private Context context;
    private Handler mHandler;
    private static volatile LxIatManager instance;
    private int lastVolume = 0;
    //识别会话
    private SrSession mSrInstance;
    //识别类型
    private IatType iatType = IatType.Normal;
    //是否初始化成功
    private boolean isInitSuccess;
    //是否会话成功使用
    private boolean initIatSuccess;
    //识别资源路径
    private String res = VrConfig.IFLY_TEK_RES + "/sr/";
    //可见即可说文本
    private String srSceneStkCmd;
    //一个识别会话是否完成
    private boolean isIatFinished = true;
    //是否是oneshot
    private boolean isOneShot = false;
    private StringBuilder builder = new StringBuilder();
    //拼接的内容结果
    private String pgsString;
    //执行拼接的内容
    private String appendString = "";
    //识别文件
    private FileOutputStream mPcmOutput;
    //当前是否在识别过程中
    private boolean isSrListening = false;
    //是否有音频输入
    private boolean isHasVoiceInput = false;
    //短期免唤醒检测线程
    private Runnable stopSrRunnable = new Runnable() {
        @Override
        public void run() {
            KLog.d("检测当前是否有音频输入----" + isHasVoiceInput);
            if (!isHasVoiceInput) {
                endSRAudioData();
                VrAidlServiceManager.getInstance().onShortTimeSrChange(false);
            }
        }
    };
    //手机联系人
    private ContactList phoneContactList;
    //微信联系人
    private WeChatContactList weChatContactList;
    //应用状态
    private AppState appState = new AppState();

    private IIsrListener isrListener = new IIsrListener() {
        @Override
        public void onSrMsgProc(long uMsg, long wParam, String lParam) {
            if (uMsg == SrSession.ISS_SR_MSG_SpeechStart) {
                isIatFinished = false;
                KLog.d(TAG, "===IIsrListener msg speechStart, uMsg = " + uMsg + ", wParam = " + wParam + ", lParam = " + lParam);
            } else if (uMsg == SrSession.ISS_SR_MSG_SpeechEnd || uMsg == SrSession.ISS_SR_MSG_SpeechTimeOut
                    || uMsg == SrSession.ISS_SR_MSG_ResponseTimeout) {
                KLog.d(TAG, "===IIsrListener msg speechEnd, uMsg = " + uMsg + ", wParam = " + wParam + ", lParam = " + lParam);
                isIatFinished = uMsg != SrSession.ISS_SR_MSG_SpeechEnd;
            } else if (uMsg == SrSession.ISS_SR_MSG_Result || uMsg == SrSession.ISS_SR_MSG_Error) {
                KLog.d(TAG, "===IIsrListener msg result, uMsg = " + uMsg + ", wParam = " + wParam + ", lParam = " + lParam);
                isIatFinished = true;
                if (isOneShot)
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            startOneShotIat(true);
                        }
                    });

                isOneShot = false;
            } else if (uMsg == SrSession.ISS_SR_MSG_ONESHOT_MVWResult) {
                isOneShot = true;
                //已更改为采用自定义唤醒词
                WakeUpInfo wakeUpInfo = GsonHelper.fromJson(lParam, WakeUpInfo.class);
                if (wakeUpInfo != null) {
                    //1代表命中oneshot中的唤醒词  32代表命中oneshot场景中的"导航到"、"我想去"、"打电话给"
                    VrAidlServiceManager.getInstance().onWakeUp(wakeUpInfo);
                }
            }
            Message msg = new Message();
            msg.what = 0;
            Bundle b = new Bundle();
            b.putLong("uMsg", uMsg);
            b.putLong("wParam", wParam);
            b.putString("lParam", lParam);
            msg.setData(b);
            msg.setTarget(mHandler);
            msg.sendToTarget();
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onSrInited(boolean state, int errId) {
            if (state) {
                KLog.d(TAG, context.getString(R.string.begin_set_inited));
                AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        int activeKeyId = mSrInstance.getActiveKey(res);
                        if (activeKeyId != ISSErrors.ISS_SUCCESS) {
                            KLog.d(TAG, context.getString(R.string.vr_init_failed_reason) + activeKeyId);
                            reInitLater();
                            isInitSuccess = false;
                        } else {
                            isInitSuccess = true;
                        }
                        return null;
                    }
                };
                asyncTask.execute(null, null, null);
            } else {
                if (errId == ISSErrors.REMOTE_EXCEPTION) {
                    mSrInstance.initService();
                    isInitSuccess = false;
                }
            }
        }
    };

    private void postSrCheck() {
        if (mHandler != null && VrConfig.useShortTimeSr) {
            KLog.d(TAG, "post runnable");
            isHasVoiceInput = false;
            mHandler.postDelayed(stopSrRunnable, VrConfig.shortTimeSrSession);
        }
    }

    private void removeSrCheck() {
        if (mHandler != null && VrConfig.useShortTimeSr) {
            KLog.d(TAG, "remove runnable");
            mHandler.removeCallbacks(stopSrRunnable);
        }
    }

    private void rePostCheck() {
        if (VrConfig.useShortTimeSr) {
            removeSrCheck();
            postSrCheck();
        }
    }


    public static LxIatManager getInstance() {
        if (instance == null) {
            synchronized (LxIatManager.class) {
                if (instance == null) {
                    instance = new LxIatManager();
                }
            }
        }
        return instance;
    }

    private void startChooseModel(String srSceneStkCmd) {
        if (mSrInstance == null) {
            return;
        }
        abandonSrResult();
        if (TextUtils.isEmpty(srSceneStkCmd)) {
            KLog.d(TAG, "===============startSelectIat=================");
            int sessionStartId = mSrInstance.start(SrSession.ISS_SR_SCENE_SELECT, SrSession.ISS_SR_MODE_MIX_REC, null);
            if (sessionStartId != ISSErrors.ISS_SUCCESS) {
                KLog.d(TAG, "start sr failed, ERR_ID=" + sessionStartId);
            }
        } else {
            KLog.d(TAG, "===============startStkIat=================");
            int sessionStartId = mSrInstance.start("stks", SrSession.ISS_SR_MODE_MIX_REC, srSceneStkCmd);
            if (sessionStartId != ISSErrors.ISS_SUCCESS) {
                KLog.d(TAG, "start sr failed, ERR_ID=" + sessionStartId);
            }
        }
    }


    public synchronized void startOneShotIat(boolean force) {
        if (mSrInstance == null || !initIatSuccess) {
            return;
        }
        if (!force && iatType == IatType.OneShot) return;
        if (iatType == IatType.Normal && !isIatFinished) {
            return;
        }
        abandonSrResult();
        setIatType(IatType.OneShot);
        KLog.d(TAG, "===============startOneShotIat=================");
        int sessionStartId = mSrInstance.start("oneshot", SrSession.ISS_SR_MODE_MIX_REC, "{\n"
                + "    \"KeywordsType\": 1,\n"
                + "    \"MvwScene\": 32\n"
                + "}");
        if (sessionStartId != ISSErrors.ISS_SUCCESS) {
            KLog.d(TAG, "start sr failed, ERR_ID=" + sessionStartId + ", initIatSuccess = " + initIatSuccess);
            return;
        } else {
            VrAidlServiceManager.getInstance().onOneShotStateChange(true);
        }
    }


    public void init(final Context mContext) {
        KLog.d(TAG, "init");
        this.context = mContext.getApplicationContext();
        if (mSrInstance != null)
            return;
//        String deviceId = ConfigManager.DeviceConfig.getIMEI(context);
//        if (deviceId == null || deviceId.length() == 0) {
//            deviceId = VrConfig.TEST_IMEI_CODE;
//        }
        String uid = VrAidlServiceManager.getInstance().getUid();
        isOpenSeopt = XmProperties.build(uid).get(VoiceConfigManager.KEY_SEOPT_TYPE, VoiceConfigManager.SEOPT_CLOSE) != VoiceConfigManager.SEOPT_CLOSE;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = ConfigManager.DeviceConfig.getICCID(context);
        if (deviceId == null || deviceId.length() == 0) {
            deviceId = getRandomID();
        }
        KLog.d(TAG, "deviceId is " + deviceId);

        mSrInstance = SrSession.getInstance(context, isrListener, 0, res, deviceId, VrConfig.SERIAL_NUMBER);
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        handleSrMessage(msg);
                        break;
                    case 1:
                        Bundle b = msg.getData();
                        int errId = b.getInt("errId");
                        if (errId == ISSErrors.ISS_ERROR_NO_SPEECH) {
                            KLog.d(TAG, context.getString(R.string.not_check_user_voice));
                        }
                        onRecordState(false);
                        break;
                }
            }
        };
        VrAidlServiceManager.getInstance().initIatSuccess();
        KLog.d(TAG, "init end");
    }

    private String getRandomID() {
        return "d058" + getRandomString(20) + "xiaoma";
    }

    private String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }

    public boolean isInitSuccess() {
        return initIatSuccess;
    }


    private void handleSrMessage(Message msg) {
        Bundle b = msg.getData();
        long uMsg = b.getLong("uMsg");
        long wParam = b.getLong("wParam");
        String lParam = b.getString("lParam");
        if (uMsg == SrSession.ISS_SR_MSG_VolumeLevel) {
            //声纹变化
            initIatSuccess = true;
//            isHasVoiceInput = true;
            int volume = (int) wParam / 50;
            if (volume != lastVolume) {
                //优化声源变化重复回调
                VrAidlServiceManager.getInstance().onVolumeChanged(volume);
                Intent intent = getIntent(VrConstants.Actions.ON_IAT_VOLUME_CHANGE).putExtra(VrConstants.ActionExtras.IAT_VOLUME, volume);
                sendVoiceStatus(intent);
            }
            lastVolume = volume;
        } else if (uMsg == SrSession.SR_MSG_SRResult) {
            //实时转写
            //KLog.d(TAG,context.getString(R.string.received_real_time_transfer_content));
            builder.delete(0, builder.length());
            if (!TextUtils.isEmpty(lParam)) {
                RealTimeRegWord realTimeRegWord = GsonHelper.fromJson(lParam, RealTimeRegWord.class);
                if (realTimeRegWord != null && realTimeRegWord.getText() != null) {
                    //识别文本
                    List<RealTimeRegWord.TextBean.WsBean> ws = realTimeRegWord.getText().getWs();
                    //sn的位置
                    int sn = realTimeRegWord.getText().getSn();
                    if (sn == 1) {
                        appendString = "";
                    } else if (realTimeRegWord.getText().getPgs().equals("apd")) {
                        appendString = pgsString;
                    }
                    builder.append(appendString);
                    if (!ListUtils.isEmpty(ws)) {
                        for (int i = 0; i < ws.size(); i++) {
                            builder.append(ws.get(i).getCw().get(0).getW());
                        }
                        //KLog.d(TAG, context.getString(R.string.processed_real_time_transfer_content_and_returned));
                        pgsString = builder.toString();
                        Intent intent = getIntent(VrConstants.Actions.ON_IAT_RESULT).putExtra(VrConstants.ActionExtras.IAT_VOICE_TEXT, builder.toString())
                                .putExtra(VrConstants.ActionExtras.IAT_VOICE_ISLAST, true);
                        sendVoiceStatus(intent);
                        VrAidlServiceManager.getInstance().onIatResult(pgsString);
                    }
                }
            }
        } else if (uMsg == SrSession.ISS_SR_MSG_ResponseTimeout) {
            //结果响应超时
            KLog.d(TAG, context.getString(R.string.no_voice_in_time_out_line));
            if (iatType == IatType.ForChoose) {
                startClientListeningForChoose();
                return;
            }
            onNoSpeaking();
            sendErrorMsg(ErrorCode.IAT_NO_SPEAK);
            rePostCheck();
            onRecordState(false);
        } else if (uMsg == SrSession.ISS_SR_MSG_SpeechStart) {
            //检测到语音输入
            isHasVoiceInput = true;
            rePostCheck();
            KLog.d(TAG, context.getString(R.string.voice_begin_input));
        } else if (uMsg == SrSession.ISS_SR_MSG_SpeechTimeOut) {
            //语音录入超时
            isHasVoiceInput = false;
            KLog.d(TAG, context.getString(R.string.voice_time_out));
            if (iatType == IatType.ForChoose) {
                startClientListeningForChoose();
                return;
            }
            sendErrorMsg(ErrorCode.IAT_TIME_OUT);
            onRecordState(true);
        } else if (uMsg == SrSession.ISS_SR_MSG_SpeechEnd) {
            //检测到语音输入结点
            isHasVoiceInput = false;
            KLog.d(TAG, context.getString(R.string.voice_end_and_stop_input));
            onRecordState(true);
        } else if (uMsg == SrSession.ISS_SR_MSG_Error) {
            //结果异常
            KLog.d(TAG, context.getString(R.string.voice_engine_error) + msg);
            if (iatType == IatType.ForChoose) {
                startClientListeningForChoose();
                return;
            } else if (iatType == IatType.OneShot) {
                startOneShotIat(true);
                onError(wParam);
                return;
            }
            rePostCheck();
            isHasVoiceInput = false;
            onError(wParam);
            onRecordState(false);
        } else if (uMsg == SrSession.ISS_SR_MSG_Result || uMsg == SrSession.ISS_SR_MSG_STKS_Result) {
            //返回结果
            try {
                KLog.json(TAG, lParam);
                if (iatType == IatType.ForChoose) {
                    startClientListeningForChoose();
                    VrAidlServiceManager.getInstance().onIatResult(lParam);
                    return;
                }
                rePostCheck();
                if (!VrConfig.useShortTimeSr) {
                    VrAidlServiceManager.getInstance().startWakeup();
                }
                VrAidlServiceManager.getInstance().onWaveFileComplete();
                Log.d("QBX", "onIatComplete: " + lParam);
                VrAidlServiceManager.getInstance().onComplete(appendString, lParam);
                Intent intent = getIntent(VrConstants.Actions.ON_IAT_COMPLETE).putExtra(VrConstants.ActionExtras.IAT_VOICE_CONTENT, lParam);
                sendVoiceStatus(intent);
            } catch (Exception e) {
                e.printStackTrace();
                onError(wParam);
            }
        } else if (uMsg == SrSession.ISS_SR_MSG_InitStatus) {
            //初始化状态
            if (wParam == ISSErrors.ISS_SUCCESS) {
                KLog.d(TAG, context.getString(R.string.create_session_successed));
                initIatSuccess = true;
                mSrInstance.setParam(SrSession.ISS_SR_PARAM_TRACE_LEVEL, SrSession.ISS_SR_PARAM_TRACE_LEVEL_VALUE_DEBUG);
//                mSrInstance.setParam(SrSession.ISS_SR_MVW_PARAM_AEC, SrSession.ISS_SR_PARAM_VALUE_ON);
//                mSrInstance.setParam(SrSession.ISS_SR_MVW_PARAM_LSA, SrSession.ISS_SR_PARAM_VALUE_ON);
//                String attachparamsStr = "{\"nlp_params\":{\"interactive_mode\":\"oneShot\"}}";
//                mSrInstance.setParam(libisssr.ISS_SR_PARAM_ATTACHPARAMS, attachparamsStr);
                mSrInstance.setParam(SrSession.ISS_SR_PARAM_SPEECH_TIMEOUT, "15000");
                mSrInstance.setParam("mvwtimeout", "0");
                mSrInstance.setParam(SrSession.ISS_SR_PARAM_RESPONSE_TIMEOUT, "10000");
//                upLoadContact(VrAidlManager.ContactType.Phone);
                uploadHotWords();
//                setSrWakeUpWords();
                startOneShotIat(true);
            } else {
                reInitLater();
                KLog.d(TAG, context.getString(R.string.create_session_failed));
            }
        } else if (uMsg == SrSession.ISS_SR_MSG_UpLoadDictToLocalStatus) {
            //本地字典上传
            if (wParam == ISSErrors.ISS_SUCCESS) {
                KLog.d(TAG, context.getString(R.string.upload_local_word_successed));
            } else if (wParam == ISSErrors.ISS_ERROR_INVALID_JSON_FMT) {
                KLog.d(TAG, context.getString(R.string.json_word_exception));
            } else if (wParam == ISSErrors.ISS_ERROR_INVALID_JSON_INFO) {
                KLog.d(TAG, context.getString(R.string.no_useful_data_in_json));
            }
        } else if (uMsg == SrSession.ISS_SR_MSG_UpLoadDictToCloudStatus) {
            //云端字典上传
            if (wParam == ISSErrors.ISS_SUCCESS) {
                KLog.d(TAG, context.getString(R.string.upload_data_to_cloud_successed));
            } else if (wParam == ISSErrors.ISS_ERROR_INVALID_JSON_FMT) {
                KLog.d(TAG, context.getString(R.string.json_word_exception));
            } else if (wParam == ISSErrors.ISS_ERROR_INVALID_JSON_INFO) {
                KLog.d(TAG, context.getString(R.string.no_useful_data_in_json));
            } else {
                KLog.d(TAG, context.getString(R.string.upload_data_to_cloud_failed) + wParam);
            }
        }
    }


    private void onRecordState(boolean isRecordComplete) {
        VrAidlServiceManager.getInstance().onRecordState(isRecordComplete);
        if (isRecordComplete) {
            if (VrConfig.saveIatFile) {
                try {
                    mPcmOutput.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void onError(long errorCode) {
        sendErrorMsg((int) errorCode);
        VrAidlServiceManager.getInstance().onError((int) errorCode);
    }

    private void sendErrorMsg(int code) {
        Intent intent = getIntent(VrConstants.Actions.ON_IAT_ERROR);
        intent.putExtra(VrConstants.ActionExtras.IAT_ERROR_CODE, code);
        sendVoiceStatus(intent);
    }


    private void sendVoiceStatus(Intent intent) {
        if (VrAidlServiceManager.getInstance().isVrShowing()) {
            return;
        }
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }


    @SuppressLint("WrongConstant")
    public Intent getIntent(String action) {
        Intent intent = new Intent(action);
        intent.addFlags(0x01000000);
        return intent;
    }

    private void onNoSpeaking() {
        VrAidlServiceManager.getInstance().onNoSpeaking();
    }

    private void setIatType(IatType iatType) {
        this.iatType = iatType;
        if (iatType != IatType.OneShot) {
            isOneShot = false;
        }
    }

    private void startIat() {
        LxWakeupMultipleHelper.getInstance().reStart();
        if (mSrInstance == null) {
            return;
        }
        abandonSrResult();
        KLog.d(TAG, "===============startAllIat=================");
        int srMode = getSrMode();
        int sessionStartId = mSrInstance.start(SrSession.ISS_SR_SCENE_ALL, srMode, null);
        if (sessionStartId != ISSErrors.ISS_SUCCESS) {
            KLog.e(TAG, "start sr failed, ERR_ID=" + sessionStartId);
        }

        if (VrConfig.saveIatFile) {
            File file = new File(VrConfig.PCM_FILE_PATH);
            if (!file.exists()) {
                //这里不存在时候去创建一下，防止第一次启动的时候因为文件目录不存在，而导致第一次的录音文件异常
                file.mkdirs();
            }
            if (file.exists()) {
                KLog.d(TAG, "delete pcm file rlt : " + file.delete());
            }
            try {
                mPcmOutput = new FileOutputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocationInfo(double lat, double lon) {
        if (mSrInstance != null) {
            mSrInstance.setParam(SrSession.ISS_SR_PARAM_LATITUDE, lat + "");
            mSrInstance.setParam(SrSession.ISS_SR_PARAM_LONGTITUDE, lon + "");
        }
    }

    private int getSrMode() {
        int srMode = SrSession.ISS_SR_MODE_MIX_REC;
        if (ConfigManager.ApkConfig.isDebug()) {
            //Debug模式下如果SD卡存在sr_mode_local.cfg文件 则为本地识别模式
            //讯飞定制语义云端暂未上线 方便测试切换
            String srModeCfg = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sr_mode_local.cfg";
            try {
                if (new File(srModeCfg).exists()) {
                    srMode = SrSession.ISS_SR_MODE_LOCAL_REC;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "init iat " + srMode);
        }
        return srMode;
    }


    public void startListeningNormal() {
        setIatType(IatType.Normal);
        startIat();
        rePostCheck();
    }


    public void startListeningNormal(int timeOut) {
        if (timeOut <= 0) {
            timeOut = 10000;
        }
        KLog.d(TAG, "startListeningNormal  timeOut: " + timeOut);
        mSrInstance.setParam(SrSession.ISS_SR_PARAM_RESPONSE_TIMEOUT, timeOut + "");
        setIatType(IatType.Normal);
        startIat();
        rePostCheck();
    }


    public void startListeningRecord() {
        setIatType(IatType.Record);
        startIat();
    }


    public void startListeningForChoose() {
        startListeningForChoose(srSceneStkCmd);
    }


    public void startListeningForChoose(String srSceneStkCmd) {
        this.srSceneStkCmd = srSceneStkCmd;
        setIatType(IatType.ForChoose);
        startChooseModel(this.srSceneStkCmd);
    }


    public void startClientListeningForChoose() {
        VrAidlServiceManager.getInstance().onListeningForChoose();
        startListeningForChoose();
    }


    public void cancelListening() {
        KLog.d(TAG, "Iat cancelListening in IatManager");
        setIatType(IatType.Normal);
        //endSRAudioData();
    }


    public void stopListening() {
        KLog.d(TAG, "Iat stopListening in IatManager");
        setIatType(IatType.Normal);
        //endSRAudioData();
    }


    public synchronized void appendAudioData(Bundle buffer) {
        if (mSrInstance == null) return;
        try {
            byte[] buffers = buffer.getByteArray("buffer");
            mSrInstance.appendAudioData(buffers);
            if (VrConfig.saveIatFile) {
                mPcmOutput.write(buffers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int setSrWakeUpWords(final String json) {
        if (TextUtils.isEmpty(json)) return -1;
        int srMvwKeyWordCode = -1;
        if (initIatSuccess) {
            srMvwKeyWordCode = mSrInstance.setMvwKeyWords(32, json);
            startOneShotIat(true);
            KLog.json(json);
        }
        /*if (srMvwKeyWordCode != ISSErrors.ISS_SUCCESS) {
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    setSrWakeUpWords(json);
                }
            }, 3000);
        }*/
        KLog.d(TAG, "set sr wake up code: " + srMvwKeyWordCode);
        return srMvwKeyWordCode;
    }


    private int setSrWakeUpWords() {
        try {
            JSONObject objRoot = new JSONObject();
            JSONArray objArr = new JSONArray();
            List<String> oneshotWords = new ArrayList<>();
            oneshotWords.add(context.getString(R.string.call_for));
            oneshotWords.add(context.getString(R.string.navigate_to));
            oneshotWords.add(context.getString(R.string.want_to_listen));
            oneshotWords.add(context.getString(R.string.like_to_listen));
            for (int nIndex = 0; nIndex < oneshotWords.size(); nIndex++) {
                JSONObject objWord = new JSONObject();
                String word = oneshotWords.get(nIndex);
                objWord.put("KeyWordId", nIndex);
                objWord.put("KeyWord", word);
                objWord.put("DefaultThreshold40", 0);
                objArr.put(objWord);
            }
            objRoot.put("Keywords", objArr);
            objRoot.put("KeywordsType", 1);
            return setSrWakeUpWords(objRoot.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void abandonSrResult() {
        try {
            if (mSrInstance == null) {
                return;
            }
            //需要放弃此次回调结果
            KLog.d(TAG, "endSRAudioData abandonResult");
            mSrInstance.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endSRAudioData() {
        if (mSrInstance == null) {
            return;
        }
        int errId = mSrInstance.endAudioData();
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putInt("errId", errId);
        msg.setData(b);
        msg.setTarget(mHandler);
        msg.sendToTarget();
        KLog.d(TAG, "PcmRecorderSR endSRAudioData!");
    }


    private void uploadHotWords() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                int uploadDict = mSrInstance.uploadDict(AssetUtils.getTextFromAsset(context,
                        "hot_word.json"), 0);
                if (uploadDict != 0) {
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(this, 5000);
                }
                KLog.d(TAG, "uploadHotWords: " + uploadDict);
            }
        });

    }

    void uploadAppState(final boolean isForeground, @AppType String appType) {
        Log.d("QBX", String.format("【uploadAppState】: isForeground=%s, %s", isForeground, appType));
        switch (appType) {
            case AppType.MUSIC:
                appState.setMusicForeground(isForeground);
                break;
            case AppType.RADIO:
                if (RadioType.None.equals(appState.getLastRadioType()) || RadioType.Local.equals(appState.getLastRadioType())) {
                    appState.setRadioForeground(isForeground);
                    if (isForeground) {
                        appState.setInternetRadioForeground(false);
                    }
                } else {
                    appState.setInternetRadioForeground(isForeground);
                    if (isForeground) {
                        appState.setRadioForeground(false);
                    }
                }
                break;
            case AppType.NAVI:
                appState.setNaviForeground(isForeground);
                break;
            case AppType.WEIXIN:
                appState.setWeixinForeground(isForeground);
                break;
        }
        uploadData();
    }

    void uploadPlayState(boolean isPlaying, String appType) {
        Log.d("QBX", String.format("【uploadPlayState】: isPlaying=%s, %s", isPlaying, appType));
        switch (appType) {
            case AppType.MUSIC:
                appState.setMusicPlaying(isPlaying);
                break;
            case AppType.RADIO:
                appState.setRadioPlaying(isPlaying);
                appState.setLastRadioType(RadioType.Local);
                if (isPlaying) {
                    appState.setInternetRadioPlaying(false);
                    if (appState.isInternetRadioForeground()) {
                        appState.setInternetRadioForeground(false);
                        appState.setRadioForeground(true);
                    }
                }
                break;
            case AppType.INTERNET_RADIO:
                if (RadioType.None.equals(appState.getLastRadioType()) && appState.isRadioForeground()) {
                    appState.setRadioForeground(false);
                    appState.setInternetRadioForeground(true);
                }
                appState.setInternetRadioPlaying(isPlaying);
                appState.setLastRadioType(RadioType.Internet);
                if (isPlaying) {
                    appState.setRadioPlaying(false);
                    if (appState.isRadioForeground()) {
                        appState.setRadioForeground(false);
                        appState.setInternetRadioForeground(true);
                    }
                }
                break;
        }
        uploadData();
    }

    void uploadNaviState(String naviState) {
        Log.d("QBX", "【uploadNaviState】: " + naviState);
        appState.setNaviState(naviState);
        uploadData();
    }

    private String getStateData() {
        StateData stateData = new StateData();
        if (appState.isWeixinForeground()) {
            stateData.getUserData().getWeixin__default().setActiveStatus("fg");
            stateData.getUserData().getMapU__navi().setActiveStatus("bg");
            stateData.getUserData().getMusicX__default().setActiveStatus("bg");
            stateData.getUserData().getRadio__default().setActiveStatus("bg");
            stateData.getUserData().getInternetRadio__default().setActiveStatus("bg");
        } else {
            stateData.getUserData().getWeixin__default().setActiveStatus("bg");
            stateData.getUserData().getMapU__navi().setActiveStatus(appState.isNaviForeground() ? "fg" : "bg");
            stateData.getUserData().getMusicX__default().setActiveStatus(appState.isMusicForeground() ? "fg" : "bg");
            stateData.getUserData().getRadio__default().setActiveStatus(appState.isRadioForeground() ? "fg" : "bg");
            stateData.getUserData().getInternetRadio__default().setActiveStatus(appState.isInternetRadioForeground() ? "fg" : "bg");
        }
        stateData.getUserData().getMusicX__default().setSceneStatus(appState.isMusicPlaying() ? "playing" : "paused");
        stateData.getUserData().getRadio__default().setSceneStatus(appState.isRadioPlaying() ? "playing" : "paused");
        stateData.getUserData().getInternetRadio__default().setSceneStatus(appState.isInternetRadioPlaying() ? "playing" : "paused");
        stateData.getUserData().getMapU__navi().setSceneStatus(appState.getNaviState());
        String data = GsonHelper.toJson(stateData).replaceAll("__", "::");
        String formatted = getFormattedString(data);
        Log.d("QBX", "uploadData: ");
        Log.d("QBX", formatted);
        return formatted;
    }

    private String getFormattedString(String data) {
        String formattedStr = "";
        try {
            JSONObject jsonRoot = new JSONObject(data);
            JSONObject jsonUserData = jsonRoot.optJSONObject("UserData");
            jsonRoot.put("UserData", new JSONObject(formatStr(jsonUserData.toString())));
            formattedStr = jsonRoot.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedStr;
    }

    private String formatStr(String str) {
        String newStr = "";
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject newJsonObject = new JSONObject();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject childJson = jsonObject.getJSONObject(key);
                if (key.equals("viewCmd::default") ||
                        childJson.optString("activeStatus").equals("fg") ||
                        childJson.optString("sceneStatus").equals("playing") ||
                        childJson.optString("sceneStatus").equals("navigation")
                ) {
                    newJsonObject.put(key, childJson);
                }
            }
            newStr = newJsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newStr;
    }

    private void uploadData() {
        if (mSrInstance != null) {
            String stateData = getStateData();
            if (!TextUtils.isEmpty(stateData)) {
                int uploadData = mSrInstance.uploadData(stateData, SrSession.STATE_UPLOAD_MODE_MIX);
                KLog.d(TAG, "uploadData: " + uploadData);
            }
        }
    }

    void upLoadContact(final String contactType) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                if (mSrInstance == null) {
                    ThreadDispatcher.getDispatcher().postDelayed(this, 2000);
                    return;
                }
                KLog.d(TAG, "upload contact");
                try {
                    abandonSrResult();
                    String textFromAsset = AssetUtils.getTextFromAsset(context, "hot_word.json");
                    JSONObject jsonObject = new JSONObject(textFromAsset);
                    String grm = jsonObject.getString("grm");
                    String contact = VrAidlServiceManager.getInstance().getAllContacts();
                    contact = handleContactList(contact, contactType);
                    String data = "";
                    if (!TextUtils.isEmpty(contact)) {
                        StringBuilder builder = new StringBuilder();
                        String subStr = grm.substring(1, grm.length() - 1);
                        builder.append("{\"grm\":[");
                        builder.append(subStr);
                        builder.append(",");
                        builder.append(contact);
                        builder.append("]}");
                        data = builder.toString();
                    }
                    if (!TextUtils.isEmpty(data)) {
                        Log.d("QBX", "upLoadContact");
                        int code = mSrInstance.uploadDict(data, 0);
                        KLog.d(TAG, "upload contact result is :" + code);
                    } else {
                        uploadHotWords();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isIatFinished = true;
                    startOneShotIat(true);
                }
            }
        });
    }

    private String handleContactList(String contact, String contactType) {
        switch (contactType) {
            case VrAidlManager.ContactType.Phone:
                ContactList contactList = GsonHelper.fromJson(contact, ContactList.class);
                phoneContactList = (ContactList) contactList.clone();
                break;
            case VrAidlManager.ContactType.WeChat:
                WeChatContactList tempContactList = GsonHelper.fromJson(contact, WeChatContactList.class);
                weChatContactList = (WeChatContactList) tempContactList.clone();
                break;
        }
        if (phoneContactList == null || ListUtils.isEmpty(phoneContactList.dictcontant)) {
            return GsonHelper.toJson(weChatContactList);
        } else if (weChatContactList == null || ListUtils.isEmpty(weChatContactList.dictcontant)) {
            return GsonHelper.toJson(phoneContactList);
        } else {
            return GsonHelper.toJson(phoneContactList) + "," + GsonHelper.toJson(weChatContactList);
        }
    }


    public void reInit(Context context) {
        if (mSrInstance != null) {
            mSrInstance.release();
            mSrInstance = null;
        }

        init(context);
    }


    private void reInitLater() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                reInit(context);
            }
        }, 5000);
    }

    public void upSeopt(boolean isOpenSeopt) {
        this.isOpenSeopt = isOpenSeopt;
        LxWakeupMultipleHelper.getInstance().upSeopt(isOpenSeopt);
    }

    public boolean getOpenSeopt() {
        return isOpenSeopt;
    }


    public void destroy() {
        endSRAudioData();
    }


    public boolean destroySr() {
        if (mSrInstance == null)
            return false;
        mSrInstance.stop();
        mSrInstance.release();
        int result2 = libisssr.stop();
        int result3 = libisssr.destroy();
        mSrInstance = null;
        KLog.e(TAG, "destroy xf sr --->" + result2 + ", " + result3);
        return 0 == result2 && 0 == result3;
    }

}
