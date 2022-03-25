package com.xiaoma.vrfactory.iat;

import android.content.Context;

import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.openiflytek.iat.OpenIatManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IIatManager;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.OnShortSrListener;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.vr.model.NaviState;
import com.xiaoma.vr.recorder.IBufferListener;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.vrfactory.IAssistantFocusListener;
import com.xiaoma.vrfactory.AssistantMicFocusManager;
import com.xiaoma.vrfactory.ivw.XmIvwManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Local Iat manager
 */

public class XmIatManager implements IBufferListener, IAssistantFocusListener {

    private static XmIatManager instance;
    private IIatManager iatManager;
    //是否长语音转文字
    private boolean isIatTalk = false;
    private boolean listening = false;
    private Context mContext;
    //预防长录音过程中程序异常导致状态出错
    private Runnable talkRunnable = new Runnable() {
        @Override
        public void run() {
            if (isIatTalk) {
                isIatTalk = false;
                VrAidlManager.getInstance().isTalkShow(false);
            }
        }
    };


    private XmIatManager() {
        iatManager = IatManagerFactory.getIatManager();
    }

    public synchronized static XmIatManager getInstance() {
        if (null == instance) {
            instance = new XmIatManager();
        }

        return instance;
    }


    public void init(Context context) {
        iatManager.init(context);
        mContext = context;
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            OpenIatManager.getInstance().init(context);
            VrAidlManager.getInstance().setBufferListener(this);
        }
        AssistantMicFocusManager.getInstance().addListener(this);
    }

    public boolean getInitState() {
        return iatManager.getInitState();
    }

    public void setOnIatListener(OnIatListener onIatListener) {
        iatManager.setOnIatListener(onIatListener);
        KLog.d("XmIatManager setOnIatListener");
    }

    public void stopListening() {
        if (isIatTalk) {
            isIatTalk = false;
            VrAidlManager.getInstance().isTalkShow(false);
            OpenIatManager.getInstance().stopListening();
            OpenIatManager.getInstance().onStop(null);
            ThreadDispatcher.getDispatcher().remove(talkRunnable);
        } else {
            iatManager.stopListening();
        }
        XmIvwManager.getInstance().startWakeup();
        KLog.d("XmIatManager stopListening");
    }


    public void startListeningNormal() {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            onMicError();
            return;
        }
        isIatTalk = false;
        XmIvwManager.getInstance().stopWakeup();
        iatManager.startListeningNormal();
        KLog.d("XmIatManager startListeningNormal");
    }


    public void startListeningRecord() {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            onMicError();
            return;
        }
        XmIvwManager.getInstance().stopWakeup();
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            isIatTalk = true;
            VrAidlManager.getInstance().isTalkShow(true);
            VrAidlManager.getInstance().startIatRecordInternal();
            OpenIatManager.getInstance().startListeningRecord();
            OpenIatManager.getInstance().onStart(null);
            ThreadDispatcher.getDispatcher().remove(talkRunnable);
            ThreadDispatcher.getDispatcher().postDelayed(talkRunnable, 65000);
        } else {
            iatManager.startListeningRecord();
        }
        KLog.d("XmIatManager startListeningRecord");
    }

    public void upWakeupOrientation() {
        // TODO: 2019/8/8 0008 定向识别优化方案 待确定
//        VrAidlManager.getInstance().upWakeupOrientation();
    }

    private void onMicError() {
        XMToast.showToast(mContext, "当前录音通道已被占用，暂不可使用");
    }

    /**
     * 设置讯飞坐标
     *
     * @param lat
     * @param lon
     */
    public void setLocalInfo(double lat, double lon) {
        VrAidlManager.getInstance().setLocationInfo(lat, lon);
    }


    public void startListeningRecord(int startTimeOut, int endTimeOut) {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            onMicError();
            return;
        }
        XmIvwManager.getInstance().stopWakeup();
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            isIatTalk = true;
            VrAidlManager.getInstance().isTalkShow(true);
            VrAidlManager.getInstance().startIatRecordInternal();
            OpenIatManager.getInstance().startListeningRecord(startTimeOut, endTimeOut);
            OpenIatManager.getInstance().onStart(null);
            ThreadDispatcher.getDispatcher().remove(talkRunnable);
            ThreadDispatcher.getDispatcher().postDelayed(talkRunnable, 65000);
        } else {
            iatManager.startListeningRecord(startTimeOut, endTimeOut);
        }
        KLog.d("XmIatManager startListeningRecord");
    }


    public void startListeningForChoose() {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            onMicError();
            return;
        }
        isIatTalk = false;
        XmIvwManager.getInstance().stopWakeup();
        iatManager.startListeningForChoose();
        KLog.d("XmIatManager startListeningForChoose");
    }


    public void startListeningForChoose(String stkCmd) {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            onMicError();
            return;
        }
        isIatTalk = false;
        XmIvwManager.getInstance().stopWakeup();
        iatManager.startListeningForChoose(stkCmd);
        KLog.d("XmIatManager startListeningForChoose");
    }


    public void cancelListening() {
        if (isIatTalk) {
            isIatTalk = false;
            OpenIatManager.getInstance().stopListening();
        }
        iatManager.cancelListening();
        XmIvwManager.getInstance().startWakeup();
        KLog.d("XmIatManager cancelListening");
    }

    public boolean isIatTalk() {
        return isIatTalk;
    }


    public void release() {
        if (isIatTalk) {
            isIatTalk = false;
            OpenIatManager.getInstance().stopListening();
        }
        iatManager.release();
        KLog.d("XmIatManager release");
    }


    public void destroy() {
        if (isIatTalk) {
            isIatTalk = false;
            OpenIatManager.getInstance().stopListening();
        }
        iatManager.destroy();
        KLog.d("XmIatManager destroy");
    }


    public void upLoadContact(boolean isPhoneContact, String contacts) {
        iatManager.upLoadContact(isPhoneContact, contacts);
        KLog.d("XmIatManager upLoadContact");
    }

    public void uploadAppState(boolean isForeground, @AppType String appType) {
        iatManager.uploadAppState(isForeground, appType);
        KLog.d("XmIatManager uploadAppState");
    }

    public void uploadPlayState(boolean isPlaying, @AppType String appType) {
        iatManager.uploadPlayState(isPlaying, appType);
        KLog.d("XmIatManager uploadPlayState");
    }

    public void uploadNaviState(@NaviState String naviState) {
        iatManager.uploadNaviState(naviState);
        KLog.d("XmIatManager uploadNaviState");
    }

    @Override
    public void onBuffer(byte[] buffer) {
        OpenIatManager.getInstance().onBuffer(null, buffer, 0, buffer.length);
    }

    public void setOnShortSrListener(OnShortSrListener listener) {
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            VrAidlManager.getInstance().setOnShortSrListener(listener);
        }
    }


    @Override
    public void onMicFocusChange(int var1) {
        if (var1 == XmMicManager.MICFOCUS_LOSS) {
            //焦点丢失 逻辑已经在XmIvwManager中处理
            if (iatManager != null) {
                OpenIatManager.getInstance().onMicError();
                iatManager.cancelListening();
            }
        } else if (var1 == XmMicManager.MICFOCUS_GAIN) {
            // 焦点恢复
        }
    }


}
