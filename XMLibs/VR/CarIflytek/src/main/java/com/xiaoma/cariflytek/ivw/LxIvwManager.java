package com.xiaoma.cariflytek.ivw;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.ivw.BaseWakeup;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.recorder.RecordConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc：离线唤醒管理类
 */

public class LxIvwManager {
    private static volatile LxIvwManager instance = null;
    private Context mContext;
    private BaseWakeup wakeUpInstance = null;
    private FileOutputStream mPcmOutput;
    private boolean isOpenSeopt = false;
    private OnWakeUpListener onWakeUpListener = new OnWakeUpListener() {
        @Override
        public void onWakeUp(int index, String keyWord, boolean isMainDrive) {
            WakeUpInfo wakeUpInfo = new WakeUpInfo();
            wakeUpInfo.setLeft(isMainDrive);
            wakeUpInfo.setnKeyword(keyWord);
            wakeUpInfo.setnMvwScene(index);
            VrAidlServiceManager.getInstance().onWakeUp(wakeUpInfo);
        }

        @Override
        public void onWakeUpCmd(String cmdText) {
            VrAidlServiceManager.getInstance().onWakeUpCmd(cmdText);
        }
    };

    public static LxIvwManager getInstance() {
        if (instance == null) {
            synchronized (LxIvwManager.class) {
                if (instance == null) {
                    instance = new LxIvwManager();
                }
            }
        }
        return instance;
    }


    private LxIvwManager() {
        String uid = VrAidlServiceManager.getInstance().getUid();
        if (wakeUpInstance == null) {
            int seopt = XmProperties.build(uid).get(VoiceConfigManager.KEY_SEOPT_TYPE, VoiceConfigManager.SEOPT_CLOSE);
            if (seopt != VoiceConfigManager.SEOPT_CLOSE) {
                isOpenSeopt = true;
            }
            if (isOpenSeopt) {
                wakeUpInstance = new LxXfWakeupMultiple();
            } else {
                wakeUpInstance = new LxXfWakeup();
            }
        }
    }


    public void init(Context context) {
        this.mContext = context;
        initInstance();
    }

    private void initInstance() {
        if (mContext != null && null != wakeUpInstance) {
            if (null != wakeUpInstance) {
                wakeUpInstance.init(mContext);
                wakeUpInstance.setOnWakeUpListener(onWakeUpListener);
            }
        }
    }


    public void startWakeup() {
        startRecorder();
        if (VrConfig.saveIvwFile) {
            File file = new File(VrConfig.VW_PCM_FILE_PATH);
            if (!file.exists()) {
                //这里不存在时候去创建一下，防止第一次启动的时候因为文件目录不存在，而导致第一次的录音文件异常
                file.mkdirs();
            }
            if (file.exists()) {
                KLog.d("delete pcm file rlt : " + file.delete());
            }
            try {
                mPcmOutput = new FileOutputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void stopWakeup() {
        stopRecorder();
        if (VrConfig.saveIvwFile) {
            try {
                mPcmOutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void startRecorder() {
        wakeUpInstance.startWakeup();
    }


    public void stopRecorder() {
        if (null != wakeUpInstance) {
            wakeUpInstance.stopWakeup();
        }
    }

    private void appendAudioData(byte[] buffer, int start, int byteCount) {
        if (wakeUpInstance != null) {
            wakeUpInstance.appendAudioData(buffer, start, byteCount);
        }
    }


    public void appendAudioData(Bundle buffer) {
        if (wakeUpInstance != null) {
            byte[] buffers = buffer.getByteArray(RecordConstants.BUFFER_ALL);
            byte[] left = buffer.getByteArray(RecordConstants.BUFFER_LEFT);
            byte[] right = buffer.getByteArray(RecordConstants.BUFFER_RIGHT);
            wakeUpInstance.appendAudioData(buffers, left, right);
        }
    }


    public boolean setWakeupWord(String word) {
        if (TextUtils.isEmpty(word) || wakeUpInstance == null) {
            return false;
        } else {
            return wakeUpInstance.setWakeupWord(word);
        }
    }


    public boolean resetWakeupWord() {
        if (wakeUpInstance != null) {
            return wakeUpInstance.resetWakeupWord();
        } else {
            return false;
        }
    }


    public List<String> getWakeupWord() {
        if (wakeUpInstance != null) {
            return wakeUpInstance.getWakeupWord();
        } else {
            return null;
        }
    }


    public boolean registerOneShotWakeupWord(List<String> wakeupWord) {
        if (ListUtils.isEmpty(wakeupWord)) {
            return false;
        }

        return wakeUpInstance.registerOneShotWakeupWord(wakeupWord);
    }

    public boolean unregisterOneShotWakeupWord(List<String> wakeupWord) {
        if (ListUtils.isEmpty(wakeupWord)) {
            return false;
        }
        return wakeUpInstance.unregisterOneShotWakeupWord(wakeupWord);
    }


    public void destroy() {
        if (wakeUpInstance != null) {
            wakeUpInstance.destroy();
        }
    }


    public boolean destroyIvw() {
        return wakeUpInstance != null && wakeUpInstance.destroyIvw();
    }

    public void onConfigChange() {
        if (wakeUpInstance != null) {
            wakeUpInstance.stopWakeup();
            wakeUpInstance.destroyIvw();
            wakeUpInstance = null;
        }
        if (isOpenSeopt) {
            wakeUpInstance = new LxXfWakeupMultiple();
        } else {
            wakeUpInstance = new LxXfWakeup();
        }
        initInstance();
    }

    public void upSeopt(boolean isOpenSeopt) {
        this.isOpenSeopt = isOpenSeopt;
    }


}
