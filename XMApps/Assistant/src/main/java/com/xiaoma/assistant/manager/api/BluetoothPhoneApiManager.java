package com.xiaoma.assistant.manager.api;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.process.listener.IPhoneStateChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by qiuboxiang on 2019/2/26 17:33
 * Desc:
 */
public class BluetoothPhoneApiManager extends ApiManager {

    private static final String TAG = "QBX 【" + BluetoothPhoneApiManager.class.getSimpleName() + "】";
    private List<IPhoneStateChangeListener> phoneStateChangeListeners = new CopyOnWriteArrayList<>();

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.BLUETOOTH_PHONE, CenterConstants.BLUETOOTH_PHONE_PORT);
    }

    private static BluetoothPhoneApiManager mInstance;

    private BluetoothPhoneApiManager() {
    }

    public static BluetoothPhoneApiManager getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothPhoneApiManager.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothPhoneApiManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onClientIn(SourceInfo source) {
        int port = source.getPort();
        if (port != CenterConstants.BLUETOOTH_PHONE_PORT) {
            return;
        }
        init();
    }

    @Override
    public void init() {
        registerPhoneStateCallback();
//        isContactBookSynchronized(null, false);
    }

    public void dial(String phoneNum, final String ttsContent) {
        Log.d(TAG, "dial: " + phoneNum);
        if (!TextUtils.isEmpty(ttsContent)) {
            speakContent(ttsContent, new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    AssistantManager.getInstance().closeAssistant();
                    dial(phoneNum);
                }
            });
        } else {
            AssistantManager.getInstance().closeAssistant();
            dial(phoneNum);
        }
    }

    private void dial(String phoneNum) {
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM, phoneNum);
        request(CenterConstants.BluetoothPhoneThirdAction.DIAL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);

            }
        });
    }

    public void acceptCall() {
        Log.d(TAG, "acceptCall: ");
        request(CenterConstants.BluetoothPhoneThirdAction.ACCEPT_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.ACCEPT_CALL_RESULT);
                if (!success) {

                }
            }
        });
    }

    public void rejectCall() {
        Log.d(TAG, "rejectCall: ");
        request(CenterConstants.BluetoothPhoneThirdAction.REJECT_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.REJECT_CALL_RESULT);
                if (!success) {

                }
            }
        });
    }

    public void terminateCall() {
        Log.d(TAG, "terminateCall: ");
        request(CenterConstants.BluetoothPhoneThirdAction.TERMINAL_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.TERMINAL_CALL_RESULT);
                if (!success) {

                }
            }
        });
    }

    public void dialBack() {
        Log.d(TAG, "dialBack: ");
        request(CenterConstants.BluetoothPhoneThirdAction.DIAL_BACK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_CALL_BACK_RESULT);
                if (success) {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    public void redial() {
        Log.d(TAG, "redial: ");
        request(CenterConstants.BluetoothPhoneThirdAction.REDIAL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                AssistantManager.getInstance().closeAssistant();
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.REDIAL_RESULT);
                if (success) {
                    AssistantManager.getInstance().closeAssistant();
                }
            }
        });
    }

    public void getDialBackNumber(final onGetStringResultListener listener) {
        Log.d(TAG, "getDialBackNumber: ");
        request(CenterConstants.BluetoothPhoneThirdAction.GET_DIAL_BACK_NUMBER, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                String number = extra.getString(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (!TextUtils.isEmpty(number)) {
                    listener.onSuccess(number);
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    public void getRedialNumber(final onGetStringResultListener listener) {
        Log.d(TAG, "getRedialNumber: ");
        request(CenterConstants.BluetoothPhoneThirdAction.GET_REDIAL_NUMBER, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                String number = extra.getString(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (!TextUtils.isEmpty(number)) {
                    listener.onSuccess(number);
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    public void getAllContact(IClientCallback callback) {
        Log.d(TAG, "getAllContact: ");
        request(CenterConstants.BluetoothPhoneThirdAction.GET_ALL_CONTACT, null, callback);
    }

    public void synchronizeContactBook(final OnTrueListener listener) {
        synchronizeContactBook(listener, true);
    }

    public void synchronizeContactBook(final OnTrueListener listener, boolean needTTS) {
        Log.d(TAG, "synchronizeContactBook: ");
        addFeedbackAndSpeak(context.getString(R.string.synchronize));
        request(CenterConstants.BluetoothPhoneThirdAction.SYNCHRONIZE_CONTACT_BOOK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (success) {
                    if (needTTS) {
                        String ttsContent = context.getString(R.string.synchronization_complete);
                        closeAfterSpeak(ttsContent);
                    }
                   /* if (listener != null) {
                        speakContent(ttsContent, new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                listener.onTrue();
                            }
                        });
                    } else {
                        closeAfterSpeak(ttsContent);
                    }*/

                } else {
                    if (needTTS) {
                        addFeedbackAndSpeak(R.string.synchronize_failed);
                    }
                }
            }
        });
    }

    public void isContactBookSynchronized(final OnTrueListener listener) {
        isContactBookSynchronized(listener, true);
    }

    public void isContactBookSynchronized(final OnTrueListener listener, boolean needTTS) {
        Log.d(TAG, "isContactBookSynchronized: ");
        request(CenterConstants.BluetoothPhoneThirdAction.IS_CONTACT_BOOK_SYNCHRONIZED, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (success) {
                    if (listener != null) {
                        listener.onTrue();
                    }
                } else {
                    //交互二
                    synchronizeContactBook(listener, needTTS);
                }
            }
        });
    }

    private void registerPhoneStateCallback() {
        requestWithoutTTS(CenterConstants.BluetoothPhoneThirdAction.REGISTER_PHONE_STATE_CALLBACK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                List<ContactBean> beanList = extra.getParcelableArrayList(CenterConstants.BluetoothPhoneThirdBundleKey.CONTACT_BEAN_LIST);
                int[] states = extra.getIntArray(CenterConstants.BluetoothPhoneThirdBundleKey.STATE_ARRAY);
                State[] stateArray = new State[states.length];
                for (int i = 0; i < states.length; i++) {
                    stateArray[i] = State.getState(states[i]);
                }
                notifyPhoneStateChanged(beanList, stateArray);
            }
        });
    }

    public void isBluetoothConnected(final onGetBooleanResultListener listener) {
        Log.d(TAG, "isBluetoothConnected: ");
        requestWithoutTTS(CenterConstants.BluetoothPhoneThirdAction.IS_BLUETOOTH_CONNECTED, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean isBluetoothConnected = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (isBluetoothConnected) {
                    listener.onTrue();
                } else {
                    listener.onFalse();
                }
            }
        });
    }

    public void addOnPhoneStateChangeListener(IPhoneStateChangeListener listener) {
        if (!phoneStateChangeListeners.contains(listener)) {
            phoneStateChangeListeners.add(listener);
        }
    }

    public void removeOnPhoneStateChangeListener(IPhoneStateChangeListener listener) {
        phoneStateChangeListeners.remove(listener);
    }

    private void notifyPhoneStateChanged(List<ContactBean> beanList, State[] states) {
        for (IPhoneStateChangeListener listener : phoneStateChangeListeners) {
            List<ContactBean> tempList = new ArrayList<>();
            for (int i = 0; i < beanList.size(); i++) {
                tempList.add(beanList.get(i) != null ? (ContactBean) beanList.get(i).clone() : null);
            }
            listener.onPhoneStateChanged(tempList, states.clone());
        }
    }

    @Override
    String getAppName() {
        return context.getString(R.string.bluetooth_phone);
    }
}
