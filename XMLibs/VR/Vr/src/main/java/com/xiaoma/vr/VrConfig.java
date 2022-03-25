package com.xiaoma.vr;

import android.media.AudioManager;
import android.os.Environment;

import com.xiaoma.vr.model.SeoptType;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Vr config
 */

public class VrConfig {

    public static int MvwSceneOneshot = 193;//oneshot场景 128+64+1
    public static int MvwSceneNormal = 65;//64+1
    private static VrConfig instance;

    public enum VrSource {
        OpenIFlyTek,
        LxIFlyTek,
        Speech
    }

    /**
     * 通用语音参数设置
     */
    public static String defaultWakeupWord = "你好友幂";
    //引擎设置
    public static VrSource mainVrSource = VrSource.LxIFlyTek;
    //是否使用小马后台语义解析
    public static boolean useXmNlu = true;
    //是否保存唤醒文件
    public static boolean saveIvwFile = false;
    //是否保存识别的文件
    public static boolean saveIatFile = false;
    //唤醒词的key
    public static final String WAKE_UP_WORD_KEY = "wakeup_key";
    //唤醒拼接词
    public static final String WAKE_UP_APPEND_WORD = "你好";
    //录音文件保存路径
    public static final String VW_PCM_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIvw.pcm";
    public static final String PCM_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIat.pcm";
    public static final String WAV_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newIat.wav";
    public static final String TTS_CACHE_DIR = Environment.getExternalStorageDirectory() + "/msc/tts_cache";
    public static final String RECORD_PCM_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newRecorder.pcm";
    public static final String RECORD_WAV_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/newRecorder.wav";

    public static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    public static final int TTS_STREAM_TYPE = AudioManager.STREAM_NOTIFICATION;//TTS焦点 叠加音
    public static final int IAT_STREAM_TYPE = AudioManager.STREAM_NOTIFICATION;//识别时焦点处理 停止音乐播放

    /**
     * 讯飞开放平台语音引擎参数设置
     */
    public static final String APP_ID = "582029d1";
    public static final String SP_IVW_THRESHOLD = "ivw_threshold";
    public static final String SP_ENABLE_IVW = "enableIvw";
    public static final String SP_ENABLE_POWER_ON_BROADCAST = "sp_enable_power_on_broadcast";
    public static final String XF_DEFAULT_SERVER_URL = "server_url=http://bj.voicecloud.cn/index.htm";
    //唤醒阈值
    public static final int DEFAULT_THRESH = -5;

    /**
     * 车载平台版科大讯飞语音引擎设置
     */
    //序列化号码
    public static final String SERIAL_NUMBER = "XMALIX-HT44VV-KHEA6Y";
    //测试用IMEI，pad没有该机器码，请各自使用的人对应的设置自己的机器码
    public static final String TEST_IMEI_CODE = "32349588998755";
    //资源路径
    public static final String IFLY_TEK_RES = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iflytek/res";
    //可见即可说功能开关
    public static boolean useSayCan = true;
    //是否有深度定制唤醒词
    public static boolean hasCustomizeWord = true;
    //是否打开短期免唤醒
    public static boolean useShortTimeSr = false;
    //短期免唤醒时长
    public static int shortTimeSrSession = 30 * 1000;


    /**
     * 思必驰语音引擎参数设置
     */
    public static final String KDDD = "582029d1";

    //定向识别开关 定向识别需配合双通道及多实例唤醒 需要车机+打开IsUsedSeopt
    public static SeoptType defSeoptMode = SeoptType.CLOSE;
    public static boolean isOneShot = true;//oneshot开关
    public static boolean isDefaultLeftAudio = true;//CLOSE状态下默送入识别音频方向
    public static int ONESHOT_MVW_SCENE = 128;//自定义oneshot场景
    public static int RINGBUFFER_SIZE = 16 * 1024 * 2 * 30;//ringbuffer大小

}
