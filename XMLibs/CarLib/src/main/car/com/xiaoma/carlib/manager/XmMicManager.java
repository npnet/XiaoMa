package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.mic.MicManager;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;


import com.xiaoma.utils.log.KLog;


/**
 * @author: iSun
 * @date: 2019/6/11 0011
 */
public class XmMicManager extends BaseCarManager<MicManager> {
    private static XmMicManager instance;
    private static final String SERVICE_NAME = Car.MIC_SERVICE;
    private MicManager micManager;
    private boolean isEnableMicManager = true;//是否开启Mic管理
    public static final String TAG = XmMicManager.class.getSimpleName();
    public static final int RESULT_FAILED = MicManager.MICFOCUS_REQUEST_FAILED;//失败
    public static final int RESULT_DELAYED = MicManager.MICFOCUS_REQUEST_DELAYED;//等待回调
    public static final int RESULT_SUCCESS = MicManager.MICFOCUS_REQUEST_GRANTED;//成功

    public static final int MIC_LEVEL_VOICE = MicManager.MIC_VR_LEVEL;//语音识别
    public static final int MIC_LEVEL_CALL = MicManager.MIC_COMMON_CALL_LEVEL;//蓝牙电话
    public static final int MIC_LEVEL_APP = MicManager.MIC_COMMON_LEVEL;// 其他APP

    public static final int FLAG_NONE = MicManager.MICFOCUS_FLAG_NONE;
    public static final int FLAG_DELAY_OK = MicManager.MICFOCUS_FLAG_DELAY_OK;

    public static final int MICFOCUS_GAIN = MicManager.MICFOCUS_GAIN;//焦点恢复
    public static final int MICFOCUS_LOSS = MicManager.MICFOCUS_LOSS;//焦点被抢占

    private boolean hasMicFocus = false;
    private int micLevel = MIC_LEVEL_APP;
    private int micFocusFlag = FLAG_NONE;


    private XmMicManager() {
        super(SERVICE_NAME);
    }

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

    @Override
    public void onCarServiceConnected(IBinder binder) {
        // TODO 需要更新车机系统和android.jar
        try {
            micManager = getManager();
            micManager.setContext(mContext);
        } catch (Exception e) {
            Log.e(TAG, " init error,micManager is null" + (micManager == null));
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    @Override
    public void onCarServiceConnected() {

    }

    public void init(Context context) {
        Log.e("xm", " mic manager init");
        super.init(context);
        this.mContext = context;
        CarServiceConnManager.getInstance(mContext).addCallBack(this);
    }


    public boolean requestMicFocus(OnMicFocusChangeListener l) {
        if (mContext != null) {
            Log.e(TAG, "  request Mic Focus package:%s " + mContext.getPackageName());
        }
        if (!isEnableMicManager) {
            return true;
        }
        if (micManager == null) {
            Log.e(TAG, " micManager not initialized");
            return false;
        }
        int i = 0;
        try {
            i = micManager.requestMicFocus(l.listener, micLevel, micFocusFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hasMicFocus = i == RESULT_SUCCESS;
        if (mContext != null) {
            Log.e(TAG, String.format("request Mic Focus package:%s result:%s  retCode:%s", mContext.getPackageName(), hasMicFocus, i));
        }
        return hasMicFocus;
    }

    public boolean requestMicFocus(OnMicFocusChangeListener l, int micLevel, int flag) {
        if (mContext != null) {
            Log.e(TAG, String.format("request Mic Focus package:%s ", mContext.getPackageName()));
        }
        if (!isEnableMicManager) {
            return true;
        }
        if (micManager == null) {
            Log.e(TAG, " micManager not initialized");
            return false;
        }
        int i = micManager.requestMicFocus(l.listener, micLevel, flag);
        hasMicFocus = i == RESULT_SUCCESS;
        if (mContext != null) {
            Log.e(TAG, String.format("request Mic Focus package:%s result:%s  retCode:%s", mContext.getPackageName(), hasMicFocus, i));
        }
        return hasMicFocus;
    }

    public boolean abandonMicFocus(OnMicFocusChangeListener l) {
        if (mContext != null) {
            Log.e(TAG, String.format("abandonMicFocus Mic Focus package:%s", mContext.getPackageName()));
        }
        if (!isEnableMicManager) {
            return true;
        }
        if (micManager == null) {
            return false;
        }
        int i = micManager.abandonMicFocus(l.listener);
        hasMicFocus = false;
        if (mContext != null) {
            Log.e(TAG, String.format("abandonMicFocus Mic Focus package:%s retCode:%s", mContext.getPackageName(), i));
        }
        return i == RESULT_SUCCESS;
    }

    public abstract static class OnMicFocusChangeListener {
        MicManager.OnMicFocusChangeListener listener = new MicManager.OnMicFocusChangeListener() {
            @Override
            public void onMicFocusChange(int i) {
                switch (i) {
                    case MICFOCUS_LOSS://MIC被抢占
                        Log.d(TAG, " mic MICFOCUS_LOSS");
                        break;
                    case MICFOCUS_GAIN://MIC被上个应用释放
                        Log.d(TAG, " mic MICFOCUS_GAIN");
                        break;
                }
                Log.e(TAG, String.format("onMicFocusChange package:%s", i));
                OnMicFocusChangeListener.this.onMicFocusChange(i);
            }
        };

        public abstract void onMicFocusChange(int var1);
    }

}
