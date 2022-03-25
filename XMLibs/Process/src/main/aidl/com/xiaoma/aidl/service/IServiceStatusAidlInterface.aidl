package com.xiaoma.aidl.service;

import com.xiaoma.aidl.service.IServiceStatusNotifyAidlInterface;

interface IServiceStatusAidlInterface {

    void searchUnReadMessageCount();

    boolean registerStatusNotify(IServiceStatusNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterStatusNotify(IServiceStatusNotifyAidlInterface notifyAidlInterface);

}
