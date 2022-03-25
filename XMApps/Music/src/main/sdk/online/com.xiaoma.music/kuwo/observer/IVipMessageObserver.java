package com.xiaoma.music.kuwo.observer;

import java.util.List;

import cn.kuwo.base.bean.VipUserInfo;
import cn.kuwo.core.observers.IVipMgrObserver;

/**
 * Author: loren
 * Date: 2019/7/8 0008
 */

public class IVipMessageObserver implements IVipMgrObserver {
    @Override
    public void IVipMgrObserver_OnStateChanged() {

    }

    @Override
    public void IVipMgrObserver_OnLoaded(List<VipUserInfo> list) {

    }

    @Override
    public void IVIPMgrObserver_OnLoadFaild(int i, String s) {

    }
}
