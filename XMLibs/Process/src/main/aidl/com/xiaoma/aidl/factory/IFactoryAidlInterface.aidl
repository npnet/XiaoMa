package com.xiaoma.aidl.factory;

import com.xiaoma.aidl.factory.IFactoryNotifyAidlInterface;

interface IFactoryAidlInterface {

    void searchUnReadMessageCount();

    boolean registerStatusNotify(IFactoryNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterStatusNotify(IFactoryNotifyAidlInterface notifyAidlInterface);

}
