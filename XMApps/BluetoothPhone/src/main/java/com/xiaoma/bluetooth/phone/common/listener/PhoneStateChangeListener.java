package com.xiaoma.bluetooth.phone.common.listener;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;

/**
 * @author: iSun
 * @date: 2018/11/19 0019
 */
public interface PhoneStateChangeListener {
    void onPhoneStateChange(ContactBean bean, State state);
}
