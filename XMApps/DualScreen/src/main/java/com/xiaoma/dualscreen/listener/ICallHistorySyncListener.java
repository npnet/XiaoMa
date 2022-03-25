package com.xiaoma.dualscreen.listener;

import com.xiaoma.aidl.model.ContactBean;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/7/18 0018
 */
public interface ICallHistorySyncListener {

    void onHistorySync(List<ContactBean> contactBeanList);

}
