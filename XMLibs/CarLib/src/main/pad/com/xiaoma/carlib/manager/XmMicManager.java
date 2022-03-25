package com.xiaoma.carlib.manager;

import android.content.Context;


/**
 * @author: iSun
 * @date: 2019/6/11 0011
 */
public class XmMicManager {
    private static XmMicManager instance;
    public static final String TAG = XmMicManager.class.getSimpleName();
    public static final int RESULT_FAILED = 0;//失败
    public static final int RESULT_DELAYED = 0;//等待回调
    public static final int RESULT_SUCCESS = 0;//成功

    public static final int MIC_LEVEL_VOICE = 0;//语音
    public static final int MIC_LEVEL_CALL = 0;//蓝牙电话
    public static final int MIC_LEVEL_APP = 0;// 其他APP

    public static final int FLAG_NONE = 0;
    public static final int FLAG_DELAY_OK = 0;

    public static final int MICFOCUS_GAIN = 1;//焦点恢复
    public static final int MICFOCUS_LOSS = 0;//焦点被抢占


    public static XmMicManager getInstance() {
        if (instance == null) {
            synchronized (XmMicManager.class) {
                if (instance == null) {
                    instance = new XmMicManager();
                }
            }
        }
        return instance;
    }

    private XmMicManager() {

    }

    public void init(Context context) {
    }

    public boolean requestMicFocus(OnMicFocusChangeListener l) {
        return true;
    }

    public boolean requestMicFocus(OnMicFocusChangeListener l, int micLevel, int flag) {
        return true;
    }

    public boolean abandonMicFocus(OnMicFocusChangeListener l) {
        return true;
    }

    public void registerMicFocusListener(OnMicFocusChangeListener l) {

    }

    public void unregisterAudioFocusListener(OnMicFocusChangeListener l) {

    }

    public abstract static class OnMicFocusChangeListener {
        public abstract void onMicFocusChange(int var1);
    }

}
