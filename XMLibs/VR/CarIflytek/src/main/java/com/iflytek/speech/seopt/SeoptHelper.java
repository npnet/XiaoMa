package com.iflytek.speech.seopt;

import android.text.TextUtils;
import android.util.Log;

import com.iflytek.speech.NativeHandle;
import com.iflytek.speech.libissseopt;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.model.ConfigType;
import com.xiaoma.vr.model.SeoptType;

/**
 * @author: iSun
 * @date: 2019/3/18 0018
 * 定向识别
 */
public class SeoptHelper implements VoiceConfigManager.IVoiceConfigChange {
    private static final String TAG = "LxWakeupMultipleHelper";
    private ISeoptListener listener;
    private NativeHandle phISSSeopt = null;
    private static SeoptHelper instance;
    private String curSeoptOrientation;
    private String autoSeopOrientation = null;
    private boolean tempCloseSeopt = false;//临时禁用定向识别


    public static SeoptHelper getInstance() {
        if (instance == null) {
            synchronized (SeoptHelper.class) {
                if (instance == null) {
                    instance = new SeoptHelper();
                }
            }
        }
        return instance;
    }

    private SeoptHelper() {
        Log.d(TAG, " SeoptHelper: first init:" + hashCode());
        int err = 0;
        phISSSeopt = new NativeHandle();
        String mSeoptResPath = VrConfig.IFLY_TEK_RES + "/seopt/";
        err = libissseopt.create(phISSSeopt, mSeoptResPath);
        if (err != 0) {
            Log.e(TAG, " initSeopt erroe: " + err);
        }
        boolean openSeopt = VoiceConfigManager.getInstance().isOpenSeopt();
        if (openSeopt && VoiceConfigManager.getInstance().getmSeopt() != SeoptType.AUTO) {
            Log.d(TAG, " SeoptHelper: setMode  ISS_SEOPT_PARAM_WORK_MODE_VALUE_MAB_VAD_ONLY");
            setOnlyMode();
        } else {
            Log.d(TAG, " SeoptHelper: setMode  ISS_SEOPT_PARAM_VALUE_MAE_FAKE");
            setParam(phISSSeopt, libissseopt.ISS_SEOPT_PARAM_WORK_MODE, libissseopt.ISS_SEOPT_PARAM_VALUE_MAE_FAKE);
        }
        VoiceConfigManager.getInstance().registerListener(this);
    }

    private String getOrientationForSeoptType(String orientation) {
        if (SeoptType.RIGHT == VoiceConfigManager.getInstance().getmSeopt()) {
            orientation = libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_RIGTHT;
        } else if (SeoptType.LEFT == VoiceConfigManager.getInstance().getmSeopt()) {
            orientation = libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_LEFT;
        } else if (SeoptType.AUTO == VoiceConfigManager.getInstance().getmSeopt()) {
            orientation = autoSeopOrientation;
        }
        return orientation;
    }

    private void reSetSeopt() {
        String result = null;
        SeoptType seoptType = VoiceConfigManager.getInstance().getmSeopt();
        if (seoptType == SeoptType.CLOSE) {
            result = null;
        } else if (seoptType == SeoptType.LEFT) {
            result = libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_LEFT;
        } else if (seoptType == SeoptType.RIGHT) {
            result = libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_RIGTHT;
        } else if (seoptType == SeoptType.AUTO) {
            result = autoSeopOrientation;
        }
        if (result != curSeoptOrientation) {
            Log.e(TAG, " reSetSeopt : " + result);
            initSeopt();
        }

    }


    public synchronized SeoptHelper initSeopt() {
        Log.d(TAG, " SeoptHelper: reInit");
        tempCloseSeopt = false;
        int err = 0;
        String mSeoptResPath = VrConfig.IFLY_TEK_RES + "/seopt/";
        err = libissseopt.create(phISSSeopt, mSeoptResPath);
        if (err != 0) {
            Log.e(TAG, " initSeopt erroe: " + err);
        }
        boolean openSeopt = VoiceConfigManager.getInstance().isOpenSeopt();
        if (openSeopt) {
            Log.d(TAG, " SeoptHelper: setMode  ISS_SEOPT_PARAM_WORK_MODE_VALUE_MAB_VAD_ONLY");
            setOnlyMode();
        } else {
            Log.d(TAG, " SeoptHelper: setMode  ISS_SEOPT_PARAM_VALUE_MAE_FAKE");
            setParam(phISSSeopt, libissseopt.ISS_SEOPT_PARAM_WORK_MODE, libissseopt.ISS_SEOPT_PARAM_VALUE_MAE_FAKE);
        }
        return this;
    }

    private void setOnlyMode() {
        String orientation = "";
        setParam(phISSSeopt, libissseopt.ISS_SEOPT_PARAM_WORK_MODE, libissseopt.ISS_SEOPT_PARAM_WORK_MODE_VALUE_MAB_VAD_ONLY);
        //oneshot模式下，根据设置中的定向识别方向来设置seopt方向
        orientation = getOrientationForSeoptType(orientation);
        Log.d(TAG, " SeoptHelper: orientation " + orientation);
        if (orientation != null && !TextUtils.isEmpty(orientation)) {
            int code = setParam(phISSSeopt, libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX, orientation);
            Log.d(TAG, " SeoptHelper: orientation set " + orientation);
            if (code == 0) {
                curSeoptOrientation = orientation;
            }
        }
    }


    public void setOrientation(boolean isMainWakeup) {
        Log.d(TAG, " upWakeupOrientation : " + isMainWakeup);
        tempCloseSeopt = false;
        autoSeopOrientation = isMainWakeup ? libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_LEFT : libissseopt.ISS_SEOPT_PARAM_BEAM_INDEX_VALUE_RIGTHT;
        if (VoiceConfigManager.getInstance().getmSeopt() != SeoptType.CLOSE) {
            //自动模式下初始化
            if (VoiceConfigManager.getInstance().getmSeopt() == SeoptType.AUTO) {
                initSeopt();
            }
        }
    }

    public void setTempCloseSeopt(boolean isClose) {
        tempCloseSeopt = isClose;
    }




    public int setParam(NativeHandle nativeHandle, String param, String szParamValue) {
        int code = libissseopt.setParam(nativeHandle, param, szParamValue);
        if (code != 0) {
            Log.e(TAG, "setParam error ,param: " + param + " value:" + szParamValue);
        }
        return code;
    }

    public void setSeoptListener(ISeoptListener listener) {
        this.listener = listener;
    }

    private int process(NativeHandle phISSSeopt, byte[] bufIn, int samplesIn, byte[] bufOut, int[] paramOut) {
        int code = libissseopt.process(phISSSeopt, bufIn, samplesIn, bufOut, paramOut);
        if (code != 0) {
            Log.e(TAG, " seopt process error: " + code);
        }
        return code;
    }


    public void seoptToSrByHardEnc(byte[] bufferLeft, byte[] bufferRight) {
        //提前处理 防止for循环多次判断
        SeoptType seoptType = VoiceConfigManager.getInstance().getmSeopt();
        if (seoptType == SeoptType.CLOSE || (seoptType == SeoptType.AUTO && TextUtils.isEmpty(curSeoptOrientation)) || tempCloseSeopt) {
            //关闭定向识别或者自动模式下且没有唤醒设置过方向或临时关闭定向识别
            if (tempCloseSeopt && !TextUtils.isEmpty(curSeoptOrientation)) {
                curSeoptOrientation = null;
                KLog.e(TAG, " seoptToSrByHardEnc : temp close seopt");
                setParam(phISSSeopt, libissseopt.ISS_SEOPT_PARAM_WORK_MODE, libissseopt.ISS_SEOPT_PARAM_VALUE_MAE_FAKE);
            }
            //处理音频
            seoptToSrMaeFake(bufferLeft, bufferRight);
        } else {
            //打开定向识别，而curSeoptOrientation=null 则说明上次临时关闭需恢复
            boolean openSeopt = VoiceConfigManager.getInstance().isOpenSeopt();
            if (openSeopt && TextUtils.isEmpty(curSeoptOrientation)) {
                Log.d(TAG, "  SeoptHelper: setMode  ISS_SEOPT_PARAM_WORK_MODE_VALUE_MAB_VAD_ONLY,restore");
                setOnlyMode();
            }
            //处理音频
            seoptToSrVadOnly(bufferLeft, bufferRight);
        }
    }

    public void seoptToSrVadOnly(byte[] bufferLeft, byte[] bufferRight) {
        try {
            int curPosition = 0;
            for (; curPosition + libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 < bufferLeft.length; curPosition += libissseopt.ISS_SEOPT_FRAME_SHIFT * 2) {
                byte[] bufferIn = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 2];
                byte[] bufferOut = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 4];
                int[] sampleOut = new int[2];
                for (int j = 0; j < libissseopt.ISS_SEOPT_FRAME_SHIFT; j++) {
                    bufferIn[j * 4] = bufferLeft[j * 2 + curPosition];
                    bufferIn[j * 4 + 1] = bufferLeft[j * 2 + 1 + curPosition];
                    bufferIn[j * 4 + 2] = bufferRight[j * 2 + curPosition];
                    bufferIn[j * 4 + 3] = bufferRight[j * 2 + 1 + curPosition];
                }
                int code = process(phISSSeopt, bufferIn, libissseopt.ISS_SEOPT_FRAME_SHIFT, bufferOut, sampleOut);
                if (code != 0) {
                    Log.e(TAG, " seoptToSrByHardEnc : 输入错误" + code);
                }
                //在开启定向识别时 第12路送入唤醒 第34路送入识别
                //识别 第34路
                byte[] buffer2Sr = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 2];
                //开放云平台 第1路（左声道）
                byte[] proceBufferLeft = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2];
                //第2路（右声道）
                byte[] proceBufferRight = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2];

                for (int index = 0; index < sampleOut[0]; index++) {
                    buffer2Sr[index * 4] = bufferOut[index * 8 + 4];
                    buffer2Sr[index * 4 + 1] = bufferOut[index * 8 + 5];
                    buffer2Sr[index * 4 + 2] = bufferOut[index * 8 + 6];
                    buffer2Sr[index * 4 + 3] = bufferOut[index * 8 + 7];

                    proceBufferLeft[index * 2] = bufferOut[index * 8 + 0];
                    proceBufferLeft[index * 2 + 1] = bufferOut[index * 8 + 1];

                    proceBufferRight[index * 2] = bufferOut[index * 8 + 2];
                    proceBufferRight[index * 2 + 1] = bufferOut[index * 8 + 3];
                }
                if (listener != null) {
                    listener.onSeopt(buffer2Sr, proceBufferLeft, proceBufferRight);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " seoptToSrByHardEnc error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void seoptToSrMaeFake(byte[] bufferLeft, byte[] bufferRight) {
        try {
            int curPosition = 0;
            for (; curPosition + libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 < bufferLeft.length; curPosition += libissseopt.ISS_SEOPT_FRAME_SHIFT * 2) {
                byte[] bufferIn = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 2];
                byte[] bufferOut = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 4];
                int[] sampleOut = new int[2];
                for (int j = 0; j < libissseopt.ISS_SEOPT_FRAME_SHIFT; j++) {
                    bufferIn[j * 4] = bufferLeft[j * 2 + curPosition];
                    bufferIn[j * 4 + 1] = bufferLeft[j * 2 + 1 + curPosition];
                    bufferIn[j * 4 + 2] = bufferRight[j * 2 + curPosition];
                    bufferIn[j * 4 + 3] = bufferRight[j * 2 + 1 + curPosition];
                }
                int code = process(phISSSeopt, bufferIn, libissseopt.ISS_SEOPT_FRAME_SHIFT, bufferOut, sampleOut);
                if (code != 0) {
                    Log.e(TAG, " seoptToSrByHardEnc : 输入错误" + code);
                }
                //在关闭定向识别时 第3路送入唤醒 第34路送入识别（此处因第4路静音，所以复制第3路）
                //识别 第34路
                byte[] buffer2Sr = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2 * 2];
                //开放云平台 第1路（左声道）
                byte[] proceBufferLeft = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2];
                //第3路（降噪 关闭定向模式下将此路送到唤醒）
                byte[] buffer2Mvw = new byte[libissseopt.ISS_SEOPT_FRAME_SHIFT * 2];

                for (int index = 0; index < sampleOut[0]; index++) {
                    buffer2Sr[index * 4] = bufferOut[index * 8 + 4];
                    buffer2Sr[index * 4 + 1] = bufferOut[index * 8 + 5];
                    buffer2Sr[index * 4 + 2] = bufferOut[index * 8 + 4];
                    buffer2Sr[index * 4 + 3] = bufferOut[index * 8 + 5];

                    proceBufferLeft[index * 2] = bufferOut[index * 8 + 0];
                    proceBufferLeft[index * 2 + 1] = bufferOut[index * 8 + 1];

                    buffer2Mvw[index * 2] = bufferOut[index * 8 + 4];
                    buffer2Mvw[index * 2 + 1] = bufferOut[index * 8 + 5];
                }
                if (listener != null) {
                    listener.onSeopt(buffer2Sr, proceBufferLeft, buffer2Mvw);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " seoptToSrByHardEnc error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigChange(ConfigType type) {
        if (type == ConfigType.SEOPT) {
            reSetSeopt();
        }
    }

    public void upWakeupOrientation() {
        // TODO: 2019/8/8 0008 定向识别优化逻辑，先回调显示界面，开始识别前再设置定向方向，方案待最终待确定
//        setOrientation(LxWakeupMultipleHelper.getInstance().isMainWakeup());
    }

    public interface ISeoptListener {
        /**
         * @param buffer
         * @param leftBuffer
         * @param buffer2Mvw 关闭定向识别时此数据为第3声道数据 可送入唤醒，否则为2声道数据
         */
        void onSeopt(byte[] buffer, byte[] leftBuffer, byte[] buffer2Mvw);
    }

}
