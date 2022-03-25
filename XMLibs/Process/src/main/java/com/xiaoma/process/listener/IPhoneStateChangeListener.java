package com.xiaoma.process.listener;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;

import java.util.List;

/**
 * Created by qiuboxiang on 2019-01-07 10:32
 */
public interface IPhoneStateChangeListener {

    /**
     * 通话状态回调
     *
     * @param beanList 通话对象
     * @param states   通话状态 {@link com.xiaoma.aidl.model.State}
     */
    void onPhoneStateChanged(List<ContactBean> beanList, State[] states);

}
