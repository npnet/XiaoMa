package com.xiaoma.aidl.bluetoothphone;

import com.xiaoma.aidl.model.ContactBean;

/**
 * Created by qiuboxiang on 2019-01-04 14:22
 */
interface IBluetoothPhoneNotifyAidlInterface {

    /**
     * 通话状态回调
     *
     * @param beanList 通话对象
     * @param states   通话状态 {@link com.xiaoma.aidl.model.State}
     */
    void onPhoneStateChanged(in List<ContactBean> beanList, in int[] states);
}
