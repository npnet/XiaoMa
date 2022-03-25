package com.xiaoma.utils.constant;

import android.os.Environment;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:语音助手静态变量
 */
public class VrConstants {
    //识别的iat文件，pcm格式
    public static final String PCM_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIat.pcm";
    //识别的iat文件，wav格式
    //public static final String WAV_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIat.wav";
    //识别的pcm文件,录音拷贝
    public static final String PCM_RECORD_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIatRecord.pcm";

    //tts缓存地址
    public static final String TTS_CACHE_DIR = Environment.getExternalStorageDirectory() + "/msc/tts_cache";

    public static final String WELCOME_TYPE_KEY = "key_welcome_type";
    //提示音
    public static final String WELCOME_TYPE_MEDIA = "welcome_media";
    //提示文本
    public static final String WELCOME_TYPE_TEXT = "welcome_text";
    //文本值对应KEY
    public static final String WELCOME_TYPE_TEXT_KEY = "key_welcome_text";

    public static class Actions {
        //数据库命中？？？
        public static final String NOTIFY_ACCESS_DATABASE = "com.xiaoma.NOTIFY_ACCESS_DATABASE";
        //TTS开始播报
        public static final String TTS_SPEAKING_BEGIN = "com.xiaoma.TTS_SPEAKING_BEGIN";
        //TTS异常
        public static final String TTS_SPEAKING_ERROR = "com.xiaoma.TTS_SPEAKING_ERROR";
        public static final String TTS_SPEAKING_STATUS = "com.xiaoma.TTS_SPEAKING_STATUS";
        //TTS播报完成
        public static final String TTS_SPEAKING_COMPLETED = "com.xiaoma.TTS_SPEAKING_COMPLETED";
        //TTS开始播报
        public static final String TTS_SEQUENCE_SPEAKING_BEGIN = "com.xiaoma.TTS_SEQUENCE_SPEAKING_BEGIN";

        public static final String TTS_SEQUENCE_SPEAKING_END = "com.xiaoma.TTS_SEQUENCE_SPEAKING_END";
        //开启TTS播报
        public static final String START_SPEAKING = "com.xiaoma.START_SPEAKING";
        //开启TTS连续播报
        public static final String START_SEQUENCE_SPEAKING = "com.xiaoma.START_SEQUENCE_SPEAKING";
        //开启第三方应用TTS播报
        public static final String START_SPEAKING_BY_THIRD = "com.xiaoma.START_SPEAKING_BY_THIRD";
        //设置语音播报的角色
        public static final String SET_VOICE_TYPE = "com.xiaoma.SET_VOICE_TYPE";
        //停止TTS播报
        public static final String STOP_SPEAKING = "com.xiaoma.STOP_SPEAKING";
        //TTS试听角色
        public static final String AUDITION_VOICE_TYPE = "com.xiaoma.AUDITION_VOICE_TYPE";
        //移除TTS播报监听
        public static final String REMOVE_TTS_SPEAKING_LISTENER = "com.xiaoma.REMOVE_TTS_SPEAKING_LISTENER";
        //启动唤醒
        public static final String START_IVW = "com.xiaoma.START_IVW";
        //停止录音
        public static final String STOP_RECORDER = "com.xiaoma.STOP_RECORDER";
        //启动录音
        public static final String START_RECORDER = "com.xiaoma.START_RECORDER";
        //设置唤醒的阈值
        public static final String SET_IVW_THRESHOLD = "com.xiaoma.SET_IVW_THRESHOLD";
        //初始化唤醒
        public static final String INIT_IVW = "com.xiaoma.INIT_IVW";
        //停止唤醒
        public static final String STOP_IVW = "com.xiaoma.STOP_IVW";
        //启动识别
        public static final String START_IAT = "com.xiaoma.START_IAT";
        //启动语音选择页面（二级页面）
        public static final String START_IAT_FOR_CHOOSE = "com.xiaoma.START_IAT_FOR_CHOOSE";
        //启动长语音识别
        public static final String START_IAT_RECORD = "com.xiaoma.START_IAT_RECORD";
        //停止长语音识别
        public static final String STOP_IAT = "com.xiaoma.STOP_IAT";
        //唤醒回调
        public static final String ON_IVW_WAKEUP = "com.xiaoma.ON_IVW_WAKEUP";
        //上传联系人到字典
        public static final String UPLOAD_CONTACT_IAT = "com.xiaoma.UPLOAD_CONTACT_IAT";
        //上传应用前后台状态
        public static final String UPLOAD_APP_STATE = "com.xiaoma.UPLOAD_APP_STATE";
        //上传播放状态
        public static final String UPLOAD_PLAY_STATE = "com.xiaoma.UPLOAD_PLAY_STATE";
        //上传导航状态
        public static final String UPLOAD_NAVI_STATE = "com.xiaoma.UPLOAD_NAVI_STATE";
        //免唤醒词回调？？、
        public static final String ON_IVW_WAKEUP_CMD = "com.xiaoma.ON_IVW_WAKEUP_CMD";
        //识别完成回调
        public static final String ON_IAT_COMPLETE = "com.xiaoma.ON_IAT_COMPLETE";
        //识别音量大小回调
        public static final String ON_IAT_VOLUME_CHANGE = "com.xiaoma.ON_IAT_VOLUME_CHANGE";
        //识别异常回调
        public static final String ON_IAT_ERROR = "com.xiaoma.ON_IAT_ERROR";
        //识别结果回调
        public static final String ON_IAT_RESULT = "com.xiaoma.ON_IAT_RESULT";
        //录音文件完成回调
        public static final String ON_IAT_WAV_FILE_COMPLETE = "com.xiaoma.ON_IAT_WAV_FILE_COMPLETE";
        //语音助手dialog显示通知
        public static final String ON_VOICE_DIALOG_SHOW = "com.xiaoma.ON_VOICE_DIALOG_SHOW";
        //语音助手dialog关闭通知
        public static final String ON_VOICE_DIALOG_DISMISSED = "com.xiaoma.ON_VOICE_DIALOG_DISMISSED";
        //初始化识别引擎
        public static final String INIT_IAT = "com.xiaoma.INIT_IAT";
        //注册唤醒词
        public static final String REGISTER_WAKEUP_WORDS = "com.xiaoma.REGISTER_WAKEUP_WORDS";
        //TBox？？？
        public static final String TBOX_VR_START = "com.xiaoma.TBOX_VR_START";
        //TBox？？？
        public static final String TBOX_VR_START_ONLY = "com.xiaoma.TBOX_VR_START_ONLY";
        //TBox？？？
        public static final String TBOX_VIDEO_OFF = "com.xiaoma.TBOX_VIDEO_OFF";
        //反注册唤醒词
        public static final String UNREGISTER_WAKEUP_WORDS = "com.xiaoma.UNREGISTER_WAKEUP_WORDS";
        //唤醒词命中？？？
        public static final String WAKEUP_WORD_HITTED = "com.xiaoma.WAKEUP_WORD_HITTED";
        //释放焦点与恢复
        public static final String WAKE_OFF = "com.xiaoma.WAKE_OFF";
        public static final String WAKE_ON = "com.xiaoma.WAKE_ON";

        //关闭语音弹窗
        public static final String ASSISTANT_DIALOG_CLOSE = "com.xiaoma.assistant.dialog.close";


        //tts角色变化
        public static final String TTS_CHANGE = "com.xiaoma.voice.tts";

        public static final String SHOW_ASSSISTANT_DIALOG = "com.xiaoma.voice.show_dialog";

        //唤醒词更改
        public static final String WAKEUP_CHANGE = "com.xiaoma.wakeup_change";

        //开启识别或唤醒
        public static String VOICE_RECORDING = "com.xiaoma.voice.recording";
    }


    public static class ActionExtras {
        public static final String SEOPT_CLOSE = "CLOSE_SEOPT";//定向识别是否临时关闭
        public static final String TTS_STATUS = "TTS_STATUS";//tts播放状态

        public static String VOICE_RECORDING_STATUS = "RECORDING_STATUS";
        //包名
        public static final String PACKAGE_NAME = "PACKAGE_NAME";
        //播报id
        public static final String TTS_ID = "TTS_ID";
        //播报错误码
        public static final String ERROR_CODE = "ERROR_CODE";
        //语音内容
        public static final String VOICE_NAME = "VOICE_NAME";
        //语音参数
        public static final String VOICE_PARAM = "VOICE_PARAM";
        //TTS播报内容
        public static final String SPEAK_CONTENT = "SPEAK_CONTENT";
        //TTS正在播报的内容
        public static final String TTS_SPEAKING_CONTENT = "TTS_SPEAKING_CONTENT";
        //TTS播报的语述
        public static final String TTS_SPEAKING_SPEED = "TTS_SPEAKING_SPEED";
        //唤醒权重
        public static final String IVW_THRESHOLD = "IVW_THRESHOLD";
        //语音识别的内容
        public static final String IAT_VOICE_CONTENT = "IAT_VOICE_CONTENT";
        //语音识别的文本
        public static final String IAT_VOICE_TEXT = "IAT_VOICE_TEXT";
        //语音音量变化
        public static final String IAT_VOLUME = "IAT_VOLUME";
        //是否是最后一个语音输入
        public static final String IAT_VOICE_ISLAST = "IAT_VOICE_ISLAST";
        //唤醒
        public static final String IVW_WAKEUP = "IVW_WAKEUP";
        //唤醒词
        public static final String WAKEUP_WORDS = "WAKEUP_WORDS";
        //唤醒词命中
        public static final String HIT_WAKEUP_WORD = "HIT_WAKEUP_WORD";
        //
        public static final String RELEASE_AUDIOFOCUS_BY_TBOX = "release_audio_focus_by_tbox";
        //联系人上报类型？？
        public static final String IAT_CONTACTS_TYPE_UPLOAD = "iat_contacts_type_upload";
        //联系人列表
        public static final String CONTACT_LIST = "CONTACT_LIST";
        //是否在前台
        public static final String IS_FOREGROUND = "IS_FOREGROUND";
        //播放状态
        public static final String PLAY_STATE = "PLAY_STATE";
        //导航状态
        public static final String NAVI_STATE = "NAVI_STATE";
        //应用类型
        public static final String APP_TYPE = "APP_TYPE";
        //识别超时
        public static final String IAT_END_TIME_OUT = "iat_time_out_end";
        public static final String IAT_START_TIME_OUT = "iat_time_out_start";


        //当前的APP
        public static final String CURRENT_APP = "CURRENT_APP";
        //可见即可说分词内容
        public static final String STKS_FOR_CHOOSE = "STKS_FOR_CHOOSE";
        //识别结果异常码
        public static final String IAT_ERROR_CODE = "IAT_ERROR_CODE";
        //tts名称
        public static final String TTS_NAME = "tts_name";
        //tts资源路径
        public static final String TTS_PATH = "tts_path";
        //tts试听音频路径
        public static final String TTS_AUDIO = "tts_audio";
        //当前tts配置名称
        public static final String TTS_CUR_ROLE = "tts_current_name";
    }

    public static class ActionScreen {
        public static final String TURN_OFF_SCREEN_ACTION = "com.xiaoma.turnoff.screen.action";
        public static final String TURN_ON_SCREEN_ACTION = "com.xiaoma.turnon.screen.action";
        public static final String SHOW_VOICE_ASSISTANT_DIALOG = "show_voice_assistant_dialog";
        public static final String DISMISS_VOICE_ASSISTANT_DIALOG = "dismiss_voice_assistant_dialog";
    }


}
