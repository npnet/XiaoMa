package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SimpleBluetoothStateListener;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.process.listener.IPhoneStateChangeListener;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/4/15 20:04
 * Desc:
 */
public class BluetoothPhoneManager extends SimpleBluetoothStateListener implements IPhoneStateChangeListener {

    private static BluetoothPhoneManager mInstance;
    private Context context;
    private String phoneNumber;
    private boolean isTtsCompleted;

    private BluetoothPhoneManager() {
    }

    public static BluetoothPhoneManager getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothPhoneManager.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothPhoneManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        this.context = context;
        BluetoothPhoneApiManager.getInstance().addOnPhoneStateChangeListener(this);
        BluetoothStateManager.getInstance(context).addListener(this);
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.INCOMING_CALL_TTS_COMPLETED);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public void onPhoneStateChanged(List<ContactBean> beanList, State[] states) {
        boolean match = false;
        for (int i = 0; i < states.length; i++) {
            State state = states[i];
            if (beanList.get(i) == null) break;
            String phoneNumber = beanList.get(i).getPhoneNum();

            if (!TextUtils.isEmpty(this.phoneNumber) && this.phoneNumber.equals(phoneNumber)) {
                match = true;
            }

            if (state == State.INCOMING && !phoneNumber.equals(this.phoneNumber)) {
                match = true;
                handleIncomingPhone(beanList.get(i));
            } else if (state == State.ACTIVE && phoneNumber.equals(this.phoneNumber)) {
                clearIncomingPhoneState();
            }
        }

        if (!TextUtils.isEmpty(this.phoneNumber) && !match) {
            clearIncomingPhoneState();
        }
    }

    private void handleIncomingPhone(ContactBean bean) {
        phoneNumber = bean.getPhoneNum();
/*//        InterceptorManager.getInstance().saveInterceptor();
        String target = bean.getName().equals(context.getString(R.string.unknown_contact)) ? bean.getPhoneNum() : bean.getName();
        String text = StringUtil.format(context.getString(R.string.incoming_phon_by_someone), target);
//        InterceptorManager.getInstance().setCurrentInterceptor(new AnswerPhoneInterceptor(),text );
        XmTtsManager.getInstance().startSpeakingByPhone(text);*/
    }

    public void clearIncomingPhoneState() {
//        if (isIncomingState()) {
        phoneNumber = null;
        isTtsCompleted = false;
//            InterceptorManager.getInstance().restoreInterceptor();
//        }
    }

    public boolean isIncomingState() {
        Log.d("QBX", "isIncomingState: " + !TextUtils.isEmpty(phoneNumber) + "  isTtsCompleted:" + isTtsCompleted);
        return !TextUtils.isEmpty(phoneNumber) && isTtsCompleted;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            if (action.equals(CenterConstants.INCOMING_CALL_TTS_COMPLETED)) {
                isTtsCompleted = true;
            }
        }
    };

    @Override
    public void onBlueToothDisConnected() {
        clearIncomingPhoneState();
    }

    @Override
    public void onHfpDisConnected() {
        clearIncomingPhoneState();
    }

    @Override
    public void onBlueToothDisabled() {
        clearIncomingPhoneState();
    }

}
