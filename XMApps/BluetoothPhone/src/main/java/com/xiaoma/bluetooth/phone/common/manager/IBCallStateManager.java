package com.xiaoma.bluetooth.phone.common.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaoma.center.logic.CenterConstants;

/**
 * Created by qiuboxiang on 2019/6/20 16:34
 * Desc:
 */
public class IBCallStateManager {

    private static IBCallStateManager instance;
    private Context context;
    private boolean isBusyState;

    public static IBCallStateManager getInstance() {
        if (instance == null) {
            synchronized (IBCallStateManager.class) {
                if (instance == null) {
                    instance = new IBCallStateManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.IN_A_IBCALL);
        filter.addAction(CenterConstants.END_OF_IBCALL);
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action==null){
                return;
            }
            switch (action) {
                case CenterConstants.IN_A_IBCALL:
                    isBusyState = true;
                    break;
                case CenterConstants.END_OF_IBCALL:
                    isBusyState = false;
                    if (PhoneStateManager.getInstance(context).isCallState()){
                        FocusManager.getInstance().requestAudioFocus();
                        PhoneStateManager.getInstance(context).showMainActivity();
                    }
                    break;
            }
        }
    };

    public boolean isBusyState() {
        return isBusyState;
    }

    public void setBusyState(boolean busyState) {
        isBusyState = busyState;
    }

}
