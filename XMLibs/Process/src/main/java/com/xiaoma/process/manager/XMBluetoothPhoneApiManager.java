package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaoma.aidl.bluetoothphone.IBluetoothPhoneAidlInterface;
import com.xiaoma.aidl.bluetoothphone.IBluetoothPhoneNotifyAidlInterface;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IPhoneStateChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by qiuboxiang on 2018/12/19 15:39
 */
public class XMBluetoothPhoneApiManager extends BaseApiManager<IBluetoothPhoneAidlInterface> {

    private List<IPhoneStateChangeListener> stateChangeListenrList = new CopyOnWriteArrayList<>();
    private IBluetoothPhoneNotifyAidlInterface.Stub proxyNotifyAidlImple = new IBluetoothPhoneNotifyAidlInterface.Stub() {

        @Override
        public void onPhoneStateChanged(List<ContactBean> beanList, int[] states) throws RemoteException {
            State[] stateArray = new State[states.length];
            for (int i = 0; i < states.length; i++) {
                stateArray[i] = State.getState(states[i]);
            }
            notifyStateChanged(beanList, stateArray);
        }
    };

    XMBluetoothPhoneApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService() {
        return bindServiceConnected();
    }

    private boolean bindServiceConnected() {
        if (aidlServiceBind == null) {
            aidlServiceBind = new BaseAidlServiceBindManager<IBluetoothPhoneAidlInterface>(context, XMApiConstants.BLUETOOTH_PHONE_SERVICE_CONNECT_ACTION, XMApiConstants.BLUETOOTH_PHONE, this) {
                @Override
                public IBluetoothPhoneAidlInterface initServiceByIBinder(IBinder service) {
                    return IBluetoothPhoneAidlInterface.Stub.asInterface(service);
                }
            };
        }
        if (!aidlServiceBind.isConnectedRemoteServer()) {
            return aidlServiceBind.connectRemoteService();
        } else {
            return true;
        }
    }


    @Override
    public void onConnected() {
        registerPhoneStateCallback(proxyNotifyAidlImple);
    }

    @Override
    public void unBindService() {
        unregisterPhoneStateCallback(proxyNotifyAidlImple);
        super.unBindService();
    }

    private void notifyStateChanged(List<ContactBean> beanList, State[] states) {
        for (IPhoneStateChangeListener listen : stateChangeListenrList) {
            List<ContactBean> tempList = new ArrayList<>();
            for (int i = 0; i < beanList.size(); i++) {
                tempList.add(beanList.get(i) != null ? (ContactBean) beanList.get(i).clone() : null);
            }
            listen.onPhoneStateChanged(tempList, states.clone());
        }
    }

    public void addPhoneStateChangeListener(IPhoneStateChangeListener listener) {
        if (listener == null || stateChangeListenrList.contains(listener)) {
            return;
        }
        stateChangeListenrList.add(listener);
    }

    public void removePhoneStateChangeListener(IPhoneStateChangeListener listener) {
        if (listener == null || !stateChangeListenrList.contains(listener)) {
            return;
        }
        stateChangeListenrList.remove(listener);
    }

    /**
     * 拨号
     *
     * @param phoneNum
     */
    public boolean dial(String phoneNum) {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().dial(phoneNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 双音多频，发送给被叫号码的用户信号
     *
     * @param code
     */
    public boolean sendDTMF(char code) {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().sendDTMF(code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 接听
     */
    public boolean acceptCall() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().acceptCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 拒接
     */
    public boolean rejectCall() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().rejectCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 结束当前通话
     */
    public boolean terminateCall() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().terminateCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 三方通话时切换通话对象
     */
    public boolean holdCall() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().holdCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 连接蓝牙音频(车机接听)
     */
    public boolean connectAudio() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().connectAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 断开蓝牙音频（手机接听）
     */
    public boolean disconnectAudio() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().disconnectAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取所有联系人
     *
     * @return
     */
    public List<ContactBean> getAllContact() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().getAllContact();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 回拨
     */
    public boolean dialBack() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().dialBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重拨
     */
    public boolean redial() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().redial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注册通话状态回调
     */
    public void registerPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback) {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().registerPhoneStateCallback(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除注册通话状态回调
     */
    public void unregisterPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback) {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().unregisterPhoneStateCallback(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 蓝牙是否已连接
     */
    public boolean isBluetoothConnected() {
        try {
            if (isAidlServiceBindSuccess()) {
                return aidlServiceBind.getServerInterface().isBluetoothConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
