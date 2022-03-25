package com.xiaoma.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConfigType;
import com.xiaoma.vr.model.SeoptType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2019/3/16 0016
 * 语音相关配置业务，如须修改配置请前往VrConfig.java
 */
public class VoiceConfigManager {
    private static final String TAG = VoiceConfigManager.class.getSimpleName();
    private boolean isSeoptDebugMode = false;//开启调试模式后debug包将不会读取本地Seopt配置
    private SeoptType mSeopt = VrConfig.defSeoptMode;
    private List<IVoiceConfigChange> list = new CopyOnWriteArrayList<>();
    private boolean voiceFocusSwitch = true;

    private static VoiceConfigManager instance;

    private Context mContext;

    public static final String KEY_CONFIG_TYPE = "KEY_CONFIG_TYPE";   //处理类型

    public static final String KEY_SEOPT_TYPE = "KEY_SEOPT_TYPE";   //定向识别key

    public static final String CONFIG_CHANGE_ACTION = "CONFIG_CHANGE_ACTION";  //广播action

    public static final String VOICE_WAKEUP_WORD = VrConfig.WAKE_UP_WORD_KEY;   //唤醒词
    public static final String VOICE_REPLY_WORD = "voice_reply_word";   //回复语
    public static final String VOICE_REPLY_DEFAULT_WORD = "voice_reply_default_word";   //回复语
    public static final String IS_VOICE_WAKEUP_ON = "is_voice_wakeup_on";  //语音唤醒开关
    public static final String IS_VOICE_WITHOUT_WAKE = "is_voice_without_wake";  //语音免唤醒
    public static final String KEY_DATA = "key_data";  //通讯词语

    public static final int SEOPT_CLOSE = 1;
    public static final int SEOPT_LEFT = 2;
    public static final int SEOPT_RIGHT = 3;
    public static final int SEOPT_AUTO = 4;

    public static final int CONFIG_KEYWORD = 1;//唤醒词修改
    public static final int CONFIG_SWITCH = 2;//唤醒开关修改
    public static final int CONFIG_SEOPT = 3;//定向识别修改
    public static final int CONFIG_REPLY = 4; //回复语修改
    public static final int CONFIG_WITHOUT_WAKE = 5; //免唤醒修改
    private static String uid;

    public static VoiceConfigManager getInstance() {
        if (instance == null) {
            synchronized (VoiceConfigManager.class) {
                if (instance == null) {
                    instance = new VoiceConfigManager();
                }
            }
        }
        return instance;
    }

    private VoiceConfigManager() {
    }


    public boolean isOpenSeopt() {
        return mSeopt != SeoptType.CLOSE;
    }

    public SeoptType getmSeopt() {
        return mSeopt;
    }

    public boolean isDefaultLeftAudio() {
        return VrConfig.isDefaultLeftAudio;
    }

    public void init(Context context) {
        //初始化 同步设置-语音设置逻辑
        if (mContext == null) {
            mContext = context.getApplicationContext();
            IntentFilter intentFilter = new IntentFilter(CONFIG_CHANGE_ACTION);
            mContext.registerReceiver(mReceiver, intentFilter);
            initSeoptType(XmProperties.build(uid).get(KEY_SEOPT_TYPE, mSeopt.getValue()));
            initVoiceFocusSwitch();
        }
    }

    public boolean isStereo() {
        //打开定向识别时自动切换为双通道
        return isOpenSeopt();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public interface IVoiceConfigChange {
        void onConfigChange(ConfigType type);
    }

    public void registerListener(IVoiceConfigChange configChange) {
        if (configChange != null && !list.contains(configChange)) {
            list.add(configChange);
        }
    }

    public void unregisterListener(IVoiceConfigChange configChange) {
        list.remove(configChange);
    }

    private void dispatchConfigChange(ConfigType type) {
        for (IVoiceConfigChange iVoiceConfigChange : list) {
            iVoiceConfigChange.onConfigChange(type);
        }
    }

    //更新唤醒词
    private void initWakeupWord(String word) {
        KLog.d("ljb", "更新唤醒词 IvwConfig initWakeupWord");
        if (word == null) {
            return;
        }
        dispatchConfigChange(ConfigType.KEYWORD);
    }

    //更新唤醒开关
    private void initWakeupSwitch(boolean data) {
        KLog.d("ljb", "更新唤醒开关 IvwConfig initWakeupSwitch:" + data);
        dispatchConfigChange(ConfigType.WAKEUP_SWITCH);
    }


    //更新定向识别值
    private void initSeoptType(int seoptType) {
        KLog.d("ljb", "更新定向识别值 IvwConfig initSeoptType:" + seoptType);
        switch (seoptType) {
            case SEOPT_CLOSE:
                mSeopt = SeoptType.CLOSE;
                break;
            case SEOPT_LEFT:
                mSeopt = SeoptType.LEFT;
                break;
            case SEOPT_RIGHT:
                mSeopt = SeoptType.RIGHT;
                break;
            case SEOPT_AUTO:
                mSeopt = SeoptType.AUTO;
                break;
        }
        dispatchConfigChange(ConfigType.SEOPT);
    }

    //更新回复语
    private void initReplyWord(String word) {
        KLog.d("ljb", "更新回复语 IvwConfig initReplyWord:" + word);
    }

    public boolean isVoiceFocusSwitch() {
        return voiceFocusSwitch;
    }


    public void initVoiceFocusSwitch() {
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                voiceFocusSwitch = true;
                dispatchConfigChange(ConfigType.VOICE_SWITCH);
            }
        }, new IntentFilter(VrConstants.Actions.WAKE_ON));
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                voiceFocusSwitch = false;
                dispatchConfigChange(ConfigType.VOICE_SWITCH);
            }
        }, new IntentFilter(VrConstants.Actions.WAKE_OFF));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int configType = intent.getIntExtra(KEY_CONFIG_TYPE, CONFIG_KEYWORD);
            switch (configType) {
                case CONFIG_KEYWORD:
                    initWakeupWord(intent.getStringExtra(KEY_DATA));
                    break;
                case CONFIG_SWITCH:
                    initWakeupSwitch(intent.getBooleanExtra(KEY_DATA, true));
                    break;
                case CONFIG_SEOPT:
                    initSeoptType(intent.getIntExtra(KEY_DATA, SEOPT_CLOSE));
                    break;
                case CONFIG_REPLY:
                    initReplyWord(intent.getStringExtra(KEY_DATA));
                    break;
            }
        }
    };


    public SeoptType getLocalSeoptType() {
        SeoptType seoptType = SeoptType.CLOSE;
        int i = XmProperties.build(uid).get(KEY_SEOPT_TYPE, -1);
        switch (i) {
            case SEOPT_CLOSE:
                seoptType = SeoptType.CLOSE;
                break;
            case SEOPT_LEFT:
                seoptType = SeoptType.LEFT;
                break;
            case SEOPT_RIGHT:
                seoptType = SeoptType.RIGHT;
                break;
            case SEOPT_AUTO:
                seoptType = SeoptType.AUTO;
                break;
        }
        Log.d(TAG, " WakeUpLog getLocalSeoptType:" + seoptType);
        return seoptType;
    }

}
