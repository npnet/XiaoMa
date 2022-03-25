package com.iflytek.speech;

import com.iflytek.speech.sr.ISRListener;
import android.util.Log;

public class libisssr {
	private static final String tag = "libisssr";
	
	static {
		System.loadLibrary("ichip-jni");
		System.loadLibrary("iFlyPResBuild");
		System.loadLibrary("iFlyNLI");
		System.loadLibrary("cataIndex");
		System.loadLibrary("cata");
		System.loadLibrary("issauth");
		System.loadLibrary("SpWord");
        System.loadLibrary("kernel");
        System.loadLibrary("w_ivw");
        System.loadLibrary("w_ivwgram");
        System.loadLibrary("namext");
        System.loadLibrary("aiui");
		System.loadLibrary("isr-jni");
		System.loadLibrary("hardinfo");
		System.loadLibrary("lesl");
	}

	//language
	final public static int ISS_SR_ACOUS_LANG_VALUE_MANDARIN = 0; //mandarin
	final public static int ISS_SR_ACOUS_LANG_VALUE_ENGLISH = 1; //english
	final public static int ISS_SR_ACOUS_LANG_VALUE_CANTONESE = 2; //cantonese

	//sr scene
	final public static String ISS_SR_SCENE_ALL = "all";                    ///< All scene
	final public static String ISS_SR_SCENE_POI = "poi";                    ///< POI navigation scene
	final public static String ISS_SR_SCENE_CONTACTS = "contacts";                ///< Contacts scene
	final public static String ISS_SR_SCENE_SELECT = "select";                ///< Select scene
	final public static String ISS_SR_SCENE_CONFIRM = "confirm";                ///< Confirm scene
	final public static String ISS_SR_SCENE_ANSWER_CALL = "answer_call";            ///< The scene of answering a call
	final public static String ISS_SR_SCENE_CMDLIST_WITHALL = "cmdlist_withall";        ///< Abandon

	final public static String ISS_SR_SCENE_SELECTLIST_POI = "selectlist_poi";        ///< only win32 and android, manadarin support
	final public static String ISS_SR_SCENE_SELECTLIST_CONTACTS = "selectlist_contacts";    ///< not support yet

	final public static String ISS_SR_SCENE_MUSIC = "music";                ///< Misic scene

	//sr mode
	final public static int ISS_SR_MODE_CLOUD_REC = 0;              ///< Pure network recognition
	final public static int ISS_SR_MODE_LOCAL_REC = 1;              ///< Pure local recognition
	final public static int ISS_SR_MODE_MIX_REC = 2;              ///< Cloud and terminal mixed recognition

	//sr parameter , parameter value
	final public static String ISS_SR_PARAM_IAT_EXTEND_PARAMS = "iatextendparams";
	final public static String ISS_SR_PARAM_SPEECH_TAIL = "speechtail";

	final public static String ISS_SR_PARAM_LONGTITUDE = "longitude";
	final public static String ISS_SR_PARAM_LATITUDE = "latitude";
	final public static String ISS_SR_PARAM_CITY = "city";
	final public static String ISS_SR_PARAM_WAP_PROXY = "wap_proxy";
	final public static String ISS_SR_PARAM_NET_SUBTYPE = "net_subtype";
	final public static String ISS_SR_PARAM_THEME = "theme";
	final public static String ISS_SR_PARAM_ATTACHPARAMS = "attachparams";
	final public static String ISS_SR_PARAM_SCENE = "scene";

	final public static String ISS_SR_PARAM_TRACE_LEVEL = "tracelevel";
	final public static String ISS_SR_PARAM_TRACE_LEVEL_VALUE_NONE = "none";
	final public static String ISS_SR_PARAM_TRACE_LEVEL_VALUE_ERROR = "error";
	final public static String ISS_SR_PARAM_TRACE_LEVEL_VALUE_INFOR = "infor";
	final public static String ISS_SR_PARAM_TRACE_LEVEL_VALUE_DEBUG = "debug";

	final public static String ISS_SR_PARAM_DOUL_MIC_MAE_DENOISE = "DoulMicMaeDeNoise";
	final public static String ISS_SR_PARAM_SINGLE_MIC_DENOISE = "SingleMicDeNoise";
	final public static String ISS_SR_PARAM_BARGE_IN = "BargeIn";
	final public static String ISS_SR_PARAM_VALUE_ON = "on";                  ///< On
	final public static String ISS_SR_PARAM_VALUE_OFF = "off";                 ///< Off
	final public static String ISS_SR_PARAM_TMP_LOG_DIR = "TmpLogDir";              ///< Set tmp log directory for debugging

	final public static int  ISS_SR_PARAM_WORK_MODE_NOTSET = 0;
    final public static int  ISS_SR_PARAM_WORK_MODE_NORMAL = 1;
    final public static int  ISS_SR_PARAM_WORK_MODE_SEOPT = 2;
	
	final public static int ISS_SR_PARAM_LOG_LEVEL = (0X00000506);
	final public static String ISS_SR_VOLUME_LOG_LEVEL_ALL = "-1";    // all info
	final public static String ISS_SR_VOLUME_LOG_LEVEL_NONE = "0";    // none
	final public static String ISS_SR_VOLUME_LOG_LEVEL_CRIT = "1";    // critical info
	final public static String ISS_SR_VOLUME_LOG_LEVEL_ERROR = "2";    // error info
	final public static String ISS_SR_VOLUME_LOG_LEVEL_WARNING = "4";    // warnint info

	final public static int ISS_SR_PARAM_LOG_OUTPUT = (0X00000507);
	final public static String ISS_SR_VOLUME_LOG_OUTPUT_NONE = ("0");    // none
	final public static String ISS_SR_VOLUME_LOG_OUTPUT_FILE = ("1");    // file
	final public static String ISS_SR_VOLUME_LOG_OUTPUT_CONSOLE = ("2");    // console except for android
	final public static String ISS_SR_VOLUME_LOG_OUTPUT_DEBUGGER = ("4");    // debugger
	final public static String ISS_SR_VOLUME_LOG_OUTPUT_MSGBOX = ("8");    // message box
	
	final public static int ISS_SR_UPLOAD_TO_LOCAL_AND_CLOUD = 0;
	final public static int ISS_SR_UPLOAD_TO_CLOUD = 1;
	final public static int ISS_SR_UPLOAD_TO_LOCAL = 2;

	
	// Log FileName
	final public static int ISS_SR_PARAM_LOG_FILE_NAME = (0X00000508);

	final public static String ISS_SR_PARAM_NETWORK_STATUS = "network_status";			///< network status, on or off, for mix sr
	final public static String ISS_SR_PARAM_SEOPT_MODE = "seopt_mode";
	final public static String ISS_SR_ENABLE_CONTINUOUS_MODEL = "EnableContinuousModel";
	
	final public static String ISS_SR_PARAM_PGS = "pgs";
	
	
	//message
	final public static int ISS_SR_MSG_InitStatus = 20000;
	final public static int ISS_SR_MSG_UpLoadDictToLocalStatus = 20001;
	final public static int ISS_SR_MSG_UpLoadDictToCloudStatus = 20002;
	final public static int ISS_SR_MSG_VolumeLevel = 20003;
	final public static int ISS_SR_MSG_SpeechStart = 20005;
	final public static int ISS_SR_MSG_SpeechEnd = 20007;
	final public static int ISS_SR_MSG_Error = 20008;
	final public static int ISS_SR_MSG_Result = 20009;
	final public static int ISS_SR_MSG_CloudInitStatus = 20013;
	final public static int ISS_SR_MSG_WaitingForCloudResult = 20018;
	final public static int ISS_SR_MSG_WaitingForLocalResult = 20021;
	final public static int ISS_SR_MSG_UpLoadDataToCloudStatus = 20050;
    final public static int ISS_SR_MSG_CloudResult = 20051;
    final public static int ISS_SR_MSG_LocalResult = 20052;
    final public static int ISS_SR_MSG_QuerySyncStatusResult = 20053;
	final public static int ISS_SR_MSG_AIUIClientSTATE = 20054;
	final public static int SR_MSG_SRResult = 20055;
	final public static int SR_MSG_TPPResult = 20056;
	final public static int ISS_SR_MSG_TransResult = 20057;


	public static native int setMachineCode(String code);
	
	public static native int setSerialNumber(String serialNumber);

	public static native int getActiveKey(String resDir);

	public static native int activate(String resDir);

	public static native int create(String resDir, ISRListener iSRListener);
	
	public static native int createEx(int iAcousLang, String resDir, ISRListener iSRListener);

	public static native int start(String szScene, int iMode, String szCmd);

	public static native int uploadDict(String szList, int iUpLoadMode);

    public static native int uploadData(String szData, int iUpLoadMode);

    public static native int querySyncStatusResult(String szSid);
	
	public static native int resetSession();
	
	public static native int clearResult();

	public static native int setParam(String szParam, String szParamValue);

	public static native int appendAudioData(byte[] audioBuffer, int nNumOfByte);

	public static native int endAudioData();

	public static native int stop();

	public static native int destroy();
	
	public static native String mspSearch(String szText, String szExternParam);
	public static native String localNli(String szText, String szScene);
    public static native int setMvwKeyWords(int nMvwScene, String szKeyWords);

}
