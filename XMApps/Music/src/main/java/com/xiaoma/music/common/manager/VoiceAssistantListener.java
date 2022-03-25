package com.xiaoma.music.common.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.center.logic.CenterConstants;

/**
 * Created by qiuboxiang on 2019/8/24 0:41
 * Desc:
 */
public class VoiceAssistantListener {

    private static VoiceAssistantListener mInstance;
    private Context context;
    private boolean isVoiceAssistantShowing;

    public static VoiceAssistantListener getInstance() {
        if (mInstance == null) {
            synchronized (VoiceAssistantListener.class) {
                if (mInstance == null) {
                    mInstance = new VoiceAssistantListener();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        this.context = context;
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG);
        filter.addAction(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG);
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG:
                    setVoiceAssistantShowing(true);
                    break;
                case CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG:
                    setVoiceAssistantShowing(false);
                    break;
            }
        }
    };

    public boolean isVoiceAssistantShowing() {
        if (isVoiceAssistantShowing){
            setVoiceAssistantShowing(false);
            return true;
        }
        return false;
    }

    public void setVoiceAssistantShowing(boolean voiceAssistantShowing) {
        isVoiceAssistantShowing = voiceAssistantShowing;
    }

}
