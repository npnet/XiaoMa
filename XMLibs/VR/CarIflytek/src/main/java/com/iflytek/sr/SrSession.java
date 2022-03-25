package com.iflytek.sr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.speech.libisssr;
import com.iflytek.speech.sr.ISRListener;
import com.iflytek.speech.sr.ISRService;
import com.iflytek.speech.sr.SRSolution;
import com.iflytek.speech.util.NetworkUtil;
import com.xiaoma.cariflytek.iat.LxIatManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VoiceConfigManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class SrSession {
    private static final String TAG = "SrSession";
    private Context mContext = null;
    private ISRService mIsr = null;
    private String sr_session_id = null;
    private IIsrListener isrListener = null;
    private String Imei = "";
    private String mSerialNumber = "";
    private String resDir = null;
    private static SrSession instance = null;
    public static final int ISS_SR_ACOUS_LANG_VALUE_MANDARIN = 0;
    public static final int ISS_SR_ACOUS_LANG_VALUE_ENGLISH = 1;
    public static final int ISS_SR_ACOUS_LANG_VALUE_CANTONESE = 2;
    public static final String ISS_SR_SCENE_ALL = "all";
    public static final String ISS_SR_SCENE_POI = "poi";
    public static final String ISS_SR_SCENE_CONTACTS = "contacts";
    public static final String ISS_SR_SCENE_SELECT = "select";
    public static final String ISS_SR_SCENE_CONFIRM = "confirm";
    public static final String ISS_SR_SCENE_ANSWER_CALL = "answer_call";
    public static final String ISS_SR_SCENE_CMDLIST_WITHALL = "cmdlist_withall";
    public static final String ISS_SR_SCENE_SELECT_MVW = "select_mvw";
    public static final String ISS_SR_SCENE_CONFIRM_MVW = "confirm_mvw";
    public static final String ISS_SR_SCENE_ANSWER_CALL_MVW = "answer_call_mvw";
    public static final String ISS_SR_SCENE_BUILD_GRM_MVW = "build_grm_mvw";
    public static final int ISS_SR_MODE_CLOUD_REC = 0;
    public static final int ISS_SR_MODE_LOCAL_REC = 1;
    public static final int ISS_SR_MODE_MIX_REC = 2;
    public static final int ISS_SR_MODE_LOCAL_CMDLIST = 3;
    public static final int ISS_SR_MODE_LOCAL_NLP = 4;
    public static final String ISS_SR_PARAM_LONGTITUDE = "longitude";
    public static final String ISS_SR_PARAM_LATITUDE = "latitude";
    public static final String ISS_SR_PARAM_SPEECH_TIMEOUT = "speechtimeout";
    public static final String ISS_SR_PARAM_IAT_EXTEND_PARAMS = "iatextendparams";
    public static final String ISS_SR_PARAM_RESPONSE_TIMEOUT = "responsetimeout";
    public static final String ISS_SR_PARAM_SPEECH_TAIL = "speechtail";
    public static final String ISS_SR_PARAM_CITY = "city";
    public static final String ISS_SR_PARAM_WAP_PROXY = "wap_proxy";
    public static final String ISS_SR_PARAM_NET_SUBTYPE = "net_subtype";
    public static final String ISS_SR_MVW_PARAM_AEC = "mvw_enable_aec";
    public static final String ISS_SR_MVW_PARAM_LSA = "mvw_enable_lsa";
    public static final String ISS_SR_PARAM_VALUE_ON = "on";
    public static final String ISS_SR_PARAM_VALUE_OFF = "off";
    public static final String ISS_SR_PARAM_TRACE_LEVEL = "tracelevel";
    public static final String ISS_SR_PARAM_TRACE_LEVEL_VALUE_NONE = "none";
    public static final String ISS_SR_PARAM_TRACE_LEVEL_VALUE_ERROR = "error";
    public static final String ISS_SR_PARAM_TRACE_LEVEL_VALUE_INFOR = "infor";
    public static final String ISS_SR_PARAM_TRACE_LEVEL_VALUE_DEBUG = "debug";
    public static final String ISS_SR_PARAM_TMP_LOG_DIR = "TmpLogDir";
    public static final String ISS_SR_PARAM_ACOUS_LANG = "ent";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_MANDARIN = "automotiveknife16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_ENGLISH = "sms-en16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_CANTONESE = "cantonese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_LMZ = "lmz16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_HENANESE = "henanese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_DONGBEIESE = "dongbeiese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_SHANDONGNESE = "shandongnese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_SHANXINESE = "shanxinese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_HEFEINESE = "hefeinese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_NANCHANGNESE = "nanchangnese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_CHANGSHANESE = "changshanese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_WUHANESE = "wuhanese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_MINNANESE = "minnanese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_SHANGHAINESE = "shanghainese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_TIANJINESE = "tianjinese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_NANJING = "nankinese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_TAIYUANESE = "taiyuanese16k";
    public static final String ISS_SR_PARAM_ACOUS_LANG_VALUE_UYGHUR = "uyghur16k";
    public static final int ISS_SR_MSG_InitStatus = 20000;
    public static final int ISS_SR_MSG_UpLoadDictToLocalStatus = 20001;
    public static final int ISS_SR_MSG_UpLoadDictToCloudStatus = 20002;
    public static final int ISS_SR_MSG_VolumeLevel = 20003;
    public static final int ISS_SR_MSG_ResponseTimeout = 20004;
    public static final int ISS_SR_MSG_SpeechStart = 20005;
    public static final int ISS_SR_MSG_SpeechTimeOut = 20006;
    public static final int ISS_SR_MSG_SpeechEnd = 20007;
    public static final int ISS_SR_MSG_Error = 20008;
    public static final int ISS_SR_MSG_Result = 20009;
    public static final int ISS_SR_MSG_ErrorDecodingAudio = 20011;
    public static final int ISS_SR_MSG_PreResult = 20012;
    public static final int ISS_SR_MSG_CloudInitStatus = 20013;
    public static final int ISS_SR_MSG_WaitingForCloudResult = 20018;
    public static final int ISS_SR_MSG_WaitingForLocalResult = 20021;
    public static final int ISS_SR_MSG_STKS_Result = 20022;
    public static final int ISS_SR_MSG_ONESHOT_MVWResult = 20023;
    public static final int ISS_SR_MSG_UpLoadDataToCloudStatus = 20050;
    public static final int ISS_SR_MSG_CloudResult = 20051;
    public static final int ISS_SR_MSG_LocalResult = 20052;
    public static final int ISS_SR_MSG_QuerySyncStatusResult = 20053;
    public static final int ISS_SR_MSG_AIUIClientSTATE = 20054;
    public static final int SR_MSG_SRResult = 20055;
    public static final int SR_MSG_TPPResult = 20056;
    public static final int ISS_SR_MSG_TransResult = 20057;
    public static final int STATE_UPLOAD_MODE_MIX = 0;
    public static final int STATE_UPLOAD_MODE_CLOUD = 1;
    public static final int STATE_UPLOAD_MODE_LOCAL = 2;

    private ISRListener isrAidlListener = new ISRListener() {
        public void onSRMsgProc_(long uMsg, long wParam, String lParam) {
            //KLog.d("zhs----" + uMsg + "---- wParm " + wParam + "---- lParam  " + lParam);
            if (uMsg == 10121L) {
                SrSession.this.sr_session_id = lParam;
            }

            if (SrSession.this.isrListener != null) {
                SrSession.this.isrListener.onSrMsgProc(uMsg, wParam, lParam);
            }

        }
    };
    private Object lock = new Object();

    private static String trimInvalid(String value) {
        return TextUtils.isEmpty(value) ? null : value.replaceAll("[,\n ]", "|");
    }

    private String getImei() {
        return this.Imei;
    }

    private void setImei(String imei) {
        this.Imei = imei;
    }

    public synchronized int getActiveKey(String resDir) {
        if (resDir != null && !resDir.equals("")) {
            Log.d("SrSession", "getActiveKey");
            return this.mIsr.getActiveKey(resDir);
        } else {
            Log.d("SrSession", "getActiveKey Res is null");
            return 10106;
        }
    }

    public static SrSession getInstance(Context context, IIsrListener srListener, String resDir, String imei) {
        if (context != null && srListener != null && resDir != null) {
            if (instance == null) {
                Class var4 = SrSession.class;
                synchronized (SrSession.class) {
                    if (instance == null) {
                        instance = new SrSession(context, srListener, resDir, imei);
                    }
                }
            }

            return instance;
        } else {
            return null;
        }
    }

    public static SrSession getInstance(Context context, IIsrListener srListener, int iAcousLang, String resDir, String imei, String serialNumber) {
        if (context != null && srListener != null && resDir != null) {
            if (instance == null) {
                Class var6 = SrSession.class;
                synchronized (SrSession.class) {
                    if (instance == null) {
                        instance = new SrSession(context, srListener, iAcousLang, resDir, imei, serialNumber);
                    }
                }
            }

            return instance;
        } else {
            return null;
        }
    }

    public static SrSession getInstance(Context context, IIsrListener srListener, int iAcousLang, String resDir, String imei) {
        if (context != null && srListener != null && resDir != null) {
            if (instance == null) {
                Class var5 = SrSession.class;
                synchronized (SrSession.class) {
                    if (instance == null) {
                        instance = new SrSession(context, srListener, iAcousLang, resDir, imei);
                    }
                }
            }

            return instance;
        } else {
            return null;
        }
    }

    private SrSession(Context mContext, IIsrListener mSrListener, String resDir, String imei) {
        Object var5 = this.lock;
        synchronized (this.lock) {
            Log.d("SrSession", "new SrSession");
            if (mContext != null && mSrListener != null && resDir != null) {
                this.mContext = mContext;
                this.isrListener = mSrListener;
                this.resDir = resDir;
                this.setImei(imei);
                this.initService();
            }
        }
    }

    private SrSession(Context mContext, IIsrListener mSrListener, int iAcousLang, String resDir, String imei, String serialNumber) {
        Object var7 = this.lock;
        synchronized (this.lock) {
            Log.d("SrSession", "new SrSession");
            if (mContext != null && mSrListener != null && resDir != null) {
                this.mContext = mContext;
                this.isrListener = mSrListener;
                this.resDir = resDir;
                this.mSerialNumber = serialNumber;
                this.setImei(imei);
                this.initServiceEx(iAcousLang);
            }
        }
    }

    private SrSession(Context mContext, IIsrListener mSrListener, int iAcousLang, String resDir, String imei) {
        Object var6 = this.lock;
        synchronized (this.lock) {
            Log.d("SrSession", "new SrSession");
            if (mContext != null && mSrListener != null && resDir != null) {
                this.mContext = mContext;
                this.isrListener = mSrListener;
                this.resDir = resDir;
                this.setImei(imei);
                this.initServiceEx(iAcousLang);
            }
        }
    }

    private void castInitState(boolean s, int e) {
        Log.d("SrSession", "castInitState, state=" + s + ", errId=" + e);
        if (this.isrListener != null) {
            this.isrListener.onSrInited(s, e);
        }

    }

    public synchronized int initService() {
        Log.d("SrSession", "sr initService start");
        if (this.mIsr != null) {
            Log.d("SrSession", "Already inited.");
            (new Thread(new SrSession.OnSrInitedRunnable(true, 0))).start();
            return 0;
        } else if (this.mContext == null) {
            Log.d("SrSession", "initService: mContext==null, castInitState(false, ISS_ERROR_INVALID_PARA) and return");
            (new Thread(new SrSession.OnSrInitedRunnable(false, 10106))).start();
            return 10106;
        } else {
            this.mIsr = SRSolution.getInstance(this.Imei);
            if (!this.mSerialNumber.equals("")) {
                Log.d("SrSession", "SerialNumber = " + this.mSerialNumber);
                this.mIsr.setSerialNumber(this.mSerialNumber);
            }

            this.sr_session_id = this.mIsr.create(this.resDir, this.isrAidlListener);
            Log.d("SrSession", "create return id=" + this.sr_session_id);
            (new Thread(new SrSession.OnSrInitedRunnable(true, 0))).start();
            return 0;
        }
    }

    public synchronized int initServiceEx(int iAcousLang) {
        Log.d("SrSession", "sr initServiceEx start");
        if (this.mIsr != null) {
            Log.d("SrSession", "Already inited.");
            (new Thread(new SrSession.OnSrInitedRunnable(true, 0))).start();
            return 0;
        } else if (this.mContext == null) {
            Log.d("SrSession", "initServiceEx: mContext==null, castInitState(false, ISS_ERROR_INVALID_PARA) and return");
            (new Thread(new SrSession.OnSrInitedRunnable(false, 10106))).start();
            return 10106;
        } else {
            this.mIsr = SRSolution.getInstance(this.Imei);
            if (!this.mSerialNumber.equals("")) {
                Log.d("SrSession", "SerialNumber = " + this.mSerialNumber);
                this.mIsr.setSerialNumber(this.mSerialNumber);
            }

            this.sr_session_id = this.mIsr.createEx(iAcousLang, this.resDir, this.isrAidlListener);
            Log.d("SrSession", "createEx return id=" + this.sr_session_id);
            (new Thread(new SrSession.OnSrInitedRunnable(true, 0))).start();
            return 0;
        }
    }

    public synchronized int start(String szScene, int iMode, String szCmd) {
        Log.d("SrSession", "start scene=" + szScene + ", mode=" + iMode + ", cmd=" + szCmd);
        if (this.sr_session_id == null) {
            Log.d("SrSession", "session id is null, return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            if (this.mContext == null) {
                this.mIsr.setParam(this.sr_session_id, "wap_proxy", "none");
            } else {
                ConnectivityManager nmgr = (ConnectivityManager) this.mContext.getSystemService(CONNECTIVITY_SERVICE);
                @SuppressLint("MissingPermission") NetworkInfo active = nmgr.getActiveNetworkInfo();
                if (active == null) {
                    this.mIsr.setParam(this.sr_session_id, "wap_proxy", "none");
                } else {
                    this.mIsr.setParam(this.sr_session_id, "wap_proxy", NetworkUtil.getNetType(active));
                    String subnet = NetworkUtil.getNetSubType(active);
                    this.mIsr.setParam(this.sr_session_id, "net_subtype", trimInvalid(subnet));
                }
                // 定向识别开关 现有定向识别实现方式，关闭时也需打开 具体处理逻辑在SeoptHelp
                this.mIsr.setParam(this.sr_session_id, libisssr.ISS_SR_PARAM_SEOPT_MODE, libisssr.ISS_SR_PARAM_VALUE_ON);
            }

            int errid = this.mIsr.sessionStart(this.sr_session_id, szScene, iMode, szCmd);
            Log.d("SrSession", "start return " + errid);
            return errid;
        }
    }

    public synchronized int uploadDict(String szList, int bOnlyUploadToCloud) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "uploadDict return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.uploadDict(this.sr_session_id, szList, bOnlyUploadToCloud);
            Log.d("SrSession", "uploadDict return " + errid);
            return errid;
        }
    }

    public synchronized int uploadData(String szData, int iUpLoadMode) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "uploadData return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.uploadData(this.sr_session_id, szData, iUpLoadMode);
            Log.d("SrSession", "uploadData return " + errid);
            return errid;
        }
    }

    public synchronized int setParam(String szParam, String szParamValue) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.setParam(this.sr_session_id, szParam, szParamValue);
            Log.d("SrSession", "setParam return " + errid);
            return errid;
        }
    }

    public synchronized int appendAudioData(byte[] audioBuffer) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "session id error");
            return 10000;
        } else {
            return audioBuffer == null ? 10106 : this.mIsr.appendAudioData(this.sr_session_id, audioBuffer, audioBuffer.length);
        }
    }

    public synchronized int appendAudioData(byte[] audioBuffer, int nBufferLength) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "session id error");
            return 10000;
        } else {
            return audioBuffer == null ? 10106 : this.mIsr.appendAudioData(this.sr_session_id, audioBuffer, nBufferLength);
        }
    }

    public synchronized int setMvwKeyWords(int nMvwScene, String szWords) {
        if (this.sr_session_id == null) {
            return 10000;
        } else {
            int errid = this.mIsr.setMvwKeyWords(this.sr_session_id, nMvwScene, szWords);
            Log.d("SrSession", "setMvwKeyWords return " + errid);
            return errid;
        }
    }

    public synchronized int endAudioData() {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "endAudioData return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.endAudioData(this.sr_session_id);
            Log.d("SrSession", "endAudioData return " + errid);
            return errid;
        }
    }

    public synchronized int stop() {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "stop return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.sessionStop(this.sr_session_id);
            Log.d("SrSession", "stop return " + errid);
            return errid;
        }
    }

    public synchronized String mspSearch(String szText, String szExternParam) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "mspSearch return ISS_ERROR_INVALID_CALL");
            return "";
        } else {
            return this.mIsr.mspSearch(this.sr_session_id, szText, szExternParam);
        }
    }

    public synchronized String localNli(String szText, String szScene) {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "localNli return ISS_ERROR_INVALID_CALL");
            return "";
        } else {
            return this.mIsr.localNli(this.sr_session_id, szText, szScene);
        }
    }

    public synchronized int release() {
        if (this.sr_session_id == null) {
            Log.d("SrSession", "release return ISS_ERROR_INVALID_CALL");
            return 10000;
        } else {
            int errid = this.mIsr.destroy(this.sr_session_id);
            this.sr_session_id = null;
            instance = null;
            Log.e("destroy log SrSession", " libisssr destroy " + errid);
            return errid;
        }
    }

    private class OnSrInitedRunnable implements Runnable {
        public boolean mBoolInitState;
        public int mIntErrorCode;

        public OnSrInitedRunnable(boolean s, int e) {
            this.mBoolInitState = s;
            this.mIntErrorCode = e;
        }

        public void run() {
            synchronized (SrSession.this.lock) {
                try {
                    Thread.currentThread();
                    Thread.sleep(5L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }

                SrSession.this.castInitState(this.mBoolInitState, this.mIntErrorCode);
            }
        }
    }

}
