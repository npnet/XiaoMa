package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.center.logic.CenterConstants;

/**
 * Created by qiuboxiang on 2019/6/20 16:34
 * Desc:
 */
public class IBCallAndPhoneStateManager {

    private static IBCallAndPhoneStateManager instance;
    private Context context;
    private boolean isIBCallBusyState;
    private boolean isPhoneBusyState;

    public static IBCallAndPhoneStateManager getInstance() {
        if (instance == null) {
            synchronized (IBCallAndPhoneStateManager.class) {
                if (instance == null) {
                    instance = new IBCallAndPhoneStateManager();
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
        filter.addAction(CenterConstants.INCOMING_CALL);
        filter.addAction(CenterConstants.IN_A_CALL);
        filter.addAction(CenterConstants.END_OF_CALL);
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
                case CenterConstants.IN_A_IBCALL:
                    isIBCallBusyState = true;
                    break;
                case CenterConstants.END_OF_IBCALL:
                    isIBCallBusyState = false;
                    break;
                case CenterConstants.INCOMING_CALL:
                    isPhoneBusyState = false;//来电
                    break;
                case CenterConstants.IN_A_CALL:
                    isPhoneBusyState = true;
                    break;
                case CenterConstants.END_OF_CALL:
                    isPhoneBusyState = false;
                    break;
            }
        }
    };

    public boolean isBusyState() {
        return isIBCallBusyState || isPhoneBusyState;
    }

    public void setIBCallBusyState(boolean busyState) {
        isIBCallBusyState = busyState;
    }

    public void setPhoneBusyState(boolean busyState) {
        isPhoneBusyState = busyState;
    }

}
