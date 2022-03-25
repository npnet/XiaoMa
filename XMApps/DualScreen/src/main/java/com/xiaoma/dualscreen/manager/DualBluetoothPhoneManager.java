package com.xiaoma.dualscreen.manager;

import android.content.Context;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.dualscreen.listener.DualSimpleBluetoothStateListener;
import com.xiaoma.dualscreen.listener.ICallHistorySyncListener;
import com.xiaoma.dualscreen.model.ContactModel;
import com.xiaoma.process.listener.IPhoneStateChangeListener;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙电话状态管理
 * Created by Lai on 2019/5/16 20:04
 * Desc:
 */
public class DualBluetoothPhoneManager extends DualSimpleBluetoothStateListener implements IPhoneStateChangeListener, ICallHistorySyncListener {

    private static DualBluetoothPhoneManager mInstance;

    private PhoneStateUIListener mPhoneStateUIListener;

    private DualBluetoothPhoneManager() {
    }

    public static DualBluetoothPhoneManager getInstance() {
        if (mInstance == null) {
            synchronized (DualBluetoothPhoneManager.class) {
                if (mInstance == null) {
                    mInstance = new DualBluetoothPhoneManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        DualBluetoothPhoneApiManager.getInstance().addOnPhoneStateChangeListener(this);
        DualBluetoothPhoneApiManager.getInstance().addOnCallHistoryChangeListener(this);
        DualBluetoothStateManager.getInstance(context).addListener(this);
    }

    public void setPhoneStateUIListener(PhoneStateUIListener phoneStateUIListener) {
        this.mPhoneStateUIListener = phoneStateUIListener;
    }

    @Override
    public void onPhoneStateChanged(List<ContactBean> beanList, State[] states) {
        KLog.d("Dual onPhoneStateChanged" + states[0] + ";" + states[1]);
        ContactBean contactBean = beanList.get(0);
        ContactBean otherBean = beanList.get(1);
        KLog.d("contactBean" + contactBean + ";otherBean" + otherBean);
        if (otherBean == null) {
            State contactState = states[0];
            if (contactState == State.INCOMING) {
                if (contactBean != null) {
                    handleIncomingPhone(contactBean);
                }
            } else if (contactState == State.CALL) {
                if (contactBean != null) {
                    handleCallingOutPhoneState(contactBean);
                }
            } else if (contactState == State.ACTIVE) {
                if (contactBean != null) {
                    handleCallingPhone(contactBean);
                }
            } else if (contactState == State.IDLE) {
                handleIDLEPhoneState(contactBean);
            }
        } else {
            KLog.d("第三方通话来了");
            ContactModel contactModel1 = new ContactModel(beanList.get(0), states[0]);
            ContactModel contactModel2 = new ContactModel(beanList.get(1), states[1]);
            List<ContactModel> contactModelList = new ArrayList<>();
            contactModelList.add(contactModel1);
            contactModelList.add(contactModel2);
            handleOtherPhoneIncoming(contactModelList);
        }
    }

    private void handleIncomingPhone(ContactBean bean) {
        KLog.d( "handleIncomingPhone" + bean.getPhoneNum());
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onConferIncomingPhone(bean);
        }
    }

    public void clearIncomingPhoneState() {
        KLog.d( "clearIncomingPhoneState");
    }

    public void handleCallingPhone(ContactBean bean) {
        KLog.d( "handleCallingPhone");
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onConferCallingPhone(bean);
        }
    }

    public void handleIDLEPhoneState(ContactBean contactBean) {
        KLog.d( "handleIDLEPhoneState");
        mPhoneStateUIListener.onConferClearPhoneState(contactBean);
    }

    public void handleCallingOutPhoneState(ContactBean contactBean) {
        KLog.d( "handleCallingOutPhoneState");
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onConferCallOutPhone(contactBean);
        }
    }

    public void handleOtherPhoneIncoming(List<ContactModel> contactModelList) {
        KLog.d( "handleOtherPhoneState");
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onConferOtherPhoneIncoming(contactModelList);
        }
    }

    @Override
    public void onBlueToothDisConnected() {
        clearIncomingPhoneState();
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onBlueToothDisconnected();
        }
    }

    @Override
    public void onHfpDisConnected() {
        clearIncomingPhoneState();
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onHfpDisconnected();
        }
    }

    @Override
    public void onBlueToothDisabled() {
        clearIncomingPhoneState();
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onBlueToothDisabled();
        }
    }

    @Override
    public void onBlueToothConnected() {
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onBlueToothConnected();
        }
    }

    @Override
    public void onHfpConnected() {
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onHfpConnected();
        }
    }

    @Override
    public void onPbapConnected() {
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onPbapConnected();
        }
    }

    @Override
    public void onPbapDisconnected() {
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onPbapDisconnected();
        }
    }

    @Override
    public void onHistorySync(List<ContactBean> contactBeanList) {
        if (mPhoneStateUIListener != null) {
            mPhoneStateUIListener.onCallHistorySync(contactBeanList);
        }
    }


    public interface PhoneStateUIListener {

        void onConferIncomingPhone(ContactBean bean);

        void onConferClearPhoneState(ContactBean bean);

        void onConferCallingPhone(ContactBean bean);

        void onConferCallOutPhone(ContactBean bean);

        void onConferOtherPhoneIncoming(List<ContactModel> contactModelList);

        void onBlueToothConnected();

        void onHfpConnected();

        void onBlueToothDisconnected();

        void onBlueToothDisabled();

        void onHfpDisconnected();

        void onPbapConnected();

        void onPbapDisconnected();

        void onCallHistorySync(List<ContactBean> contactBeanList);
    }
}
