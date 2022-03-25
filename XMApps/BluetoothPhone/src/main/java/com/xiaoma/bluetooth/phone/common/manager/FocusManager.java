package com.xiaoma.bluetooth.phone.common.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.center.logic.CenterConstants;

/**
 * Created by qiuboxiang on 2019/6/11 15:59
 * Desc:
 */
public class FocusManager implements PhoneStateManager.OnCallStateListener {

    private static final String TAG = "QBX " + FocusManager.class.getSimpleName();
    private static FocusManager instance;
    private Context context;
    private boolean hasMicFocus;

    public static FocusManager getInstance() {
        if (instance == null) {
            synchronized (FocusManager.class) {
                if (instance == null) {
                    instance = new FocusManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        XmMicManager.getInstance().init(context);
        PhoneStateManager.getInstance(context).addOnCallStateListener(this);
    }

    @Override
    public void onCall() {
        ContactBean activeBean = PhoneStateManager.getInstance(context).getPhoneStates().getCurrentActiveBean();
        if (activeBean.getBeforeState() == State.CALL.getValue()) {
            if (IBCallStateManager.getInstance().isBusyState()) {
                BlueToothPhoneManagerFactory.getInstance().terminateCall();
            } else {
                requestAudioFocus();
                requestMicFocus();
                context.sendBroadcast(new Intent(CenterConstants.IN_A_CALL));
            }
        } else if (activeBean.getBeforeState() == State.INCOMING.getValue()) {
            BlueToothPhoneManagerFactory.getInstance().pauseHfpRender();
            context.sendBroadcast(new Intent(CenterConstants.INCOMING_CALL));
        }
    }

    @Override
    public void onActive() {
        ContactBean activeBean = PhoneStateManager.getInstance(context).getPhoneStates().getCurrentActiveBean();
        if (activeBean.getBeforeState() == State.INCOMING.getValue()) {
            BlueToothPhoneManagerFactory.getInstance().startHfpRender();
        }
        if (activeBean.getBeforeState() != State.CALL.getValue()) {
            context.sendBroadcast(new Intent(CenterConstants.IN_A_CALL));
        }
        if (!IBCallStateManager.getInstance().isBusyState()) {
            requestAudioFocus();
        }
        requestMicFocus();
    }

    @Override
    public void onIdle() {
        try {
            BlueToothPhoneManagerFactory.getInstance().startHfpRender();
            context.sendBroadcast(new Intent(CenterConstants.END_OF_CALL));
        } catch (Exception e) {
            e.printStackTrace();
        }
        abandonAudioFocus();
        abandonMicFocus();
        CommonAudioFocusManager.getInstance().abandonAudioFocus();
    }

    private void requestMicFocus() {
        if (hasMicFocus) {
            return;
        }
        hasMicFocus = XmMicManager.getInstance().requestMicFocus(onMicFocusChangeListener, XmMicManager.MIC_LEVEL_CALL, XmMicManager.FLAG_NONE);
        Log.d("hzx", "requestMicFocus result:  " + hasMicFocus);
        if (!hasMicFocus) {
            Log.d("QBX", context.getString(R.string.answer_failed));
            Toast.makeText(context, context.getString(R.string.answer_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void abandonMicFocus() {
        if (!hasMicFocus) {
            return;
        }
        Log.d("hzx", "abandonMicFocus");
        hasMicFocus = !XmMicManager.getInstance().abandonMicFocus(onMicFocusChangeListener);
    }

    void requestAudioFocus() {
        AudioFocusManager.getInstance().requestAudioFocus();
    }

    private void abandonAudioFocus() {
        AudioFocusManager.getInstance().abandonAudioFocus();
    }

    private XmMicManager.OnMicFocusChangeListener onMicFocusChangeListener = new XmMicManager.OnMicFocusChangeListener() {
        @Override
        public void onMicFocusChange(int focusChange) {
            switch (focusChange) {
                case XmMicManager.MICFOCUS_GAIN:
                    Log.d(TAG, "BluetoothPhone MICFOCUS_GAIN");
                    hasMicFocus = true;
                    break;
                case XmMicManager.MICFOCUS_LOSS:
                    Log.d(TAG, "BluetoothPhone MICFOCUS_LOSS");
                    hasMicFocus = false;
                    break;
            }
        }
    };

}
