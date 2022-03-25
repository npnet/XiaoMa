package com.xiaoma.vrfactory.ivw;

import android.content.Context;
import android.content.Intent;

import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IAssistantView;
import com.xiaoma.vr.ivw.IIvwManager;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.vr.recorder.RecorderManager;
import com.xiaoma.vrfactory.IAssistantFocusListener;
import com.xiaoma.vrfactory.AssistantMicFocusManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Local Ivw manager
 */

public class XmIvwManager implements IAssistantFocusListener {

    private static XmIvwManager instance;
    private IIvwManager ivwManager;
    private Context mContext;


    private XmIvwManager() {
        ivwManager = IvwManagerFactory.getIvwManager();
    }

    public static XmIvwManager getInstance() {
        if (null == instance) {
            instance = new XmIvwManager();
        }

        return instance;
    }


    public void init(Context context) {
        ivwManager.init(context);
        this.mContext = context;
        AssistantMicFocusManager.getInstance().addListener(this);
    }


    public void setOnWakeUpListener(OnWakeUpListener listener) {
        ivwManager.setOnWakeUpListener(listener);
    }

    public void addAssistantView(IAssistantView view) {
        ivwManager.addAssistantView(view);
    }

    public void release() {
        ivwManager.release();
    }


    public void startWakeup() {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            return;
        }
        ivwManager.startWakeup();
    }


    public void stopWakeup() {
        ivwManager.stopWakeup();
    }


    public void startRecorder() {
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            return;
        }
        ivwManager.startRecorder();
    }


    public void stopRecorder() {
        ivwManager.stopRecorder();
    }


    public void setIvwThreshold(int curThresh) {
        ivwManager.setIvwThreshold(curThresh);
    }


    public void setWakeupInterception(boolean interception) {
        ivwManager.setWakeupInterception(interception);
    }


    public List<String> getWakeUpWord() {
        List<String> wakeUpWord = new ArrayList<>();
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            wakeUpWord = ((VrAidlManager) ivwManager).getWakeupWord();
        }
        return wakeUpWord;
    }


    @Override
    public void onMicFocusChange(int var1) {
        if (var1 == XmMicManager.MICFOCUS_LOSS) {
            //焦点丢失
            notifyUi();
            VrAidlManager.getInstance().destroyAudio();
        } else if (var1 == XmMicManager.MICFOCUS_GAIN) {
            // 焦点恢复
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    startWakeup();
                }
            },800);

        }
    }

    private void notifyUi() {
        if (mContext != null) {
            mContext.sendBroadcast(new Intent(VrConstants.Actions.ASSISTANT_DIALOG_CLOSE));
        }
    }
}
