package com.xiaoma.music.common.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by ZYao.
 * Date ：2019/8/14 0014
 */
public class AssistantShowManager {
    public static final String SHOW_VOICE_ASSISTANT_DIALOG = "show_voice_assistant_dialog";
    public static final String DISMISS_VOICE_ASSISTANT_DIALOG = "dismiss_voice_assistant_dialog";
    private boolean show = false;

    public static AssistantShowManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final AssistantShowManager instance = new AssistantShowManager();
    }

    public boolean isShow() {
        return show;
    }

    public void registerVrDialogReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SHOW_VOICE_ASSISTANT_DIALOG);
        intentFilter.addAction(DISMISS_VOICE_ASSISTANT_DIALOG);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (SHOW_VOICE_ASSISTANT_DIALOG.equals(action)) {
                    show = true;
                } else if (DISMISS_VOICE_ASSISTANT_DIALOG.equals(action)) {
                    show = false;
                }
            }
        }, intentFilter);
    }
}
