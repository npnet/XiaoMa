package com.xiaoma.process.base;

import android.content.Context;
import android.os.IInterface;

import com.xiaoma.process.listener.IRemoteServiceBind;
import com.xiaoma.process.listener.IRemoteServiceConnectedListener;

public abstract class BaseApiManager<T extends IInterface> implements IRemoteServiceConnectedListener, IRemoteServiceBind {

    protected BaseAidlServiceBindManager<T> aidlServiceBind;
    protected Context context;

    @Override
    public void unBindService() {
        if(aidlServiceBind != null) {
            aidlServiceBind.unBindService();
        }
    }

    @Override
    public void onDisConnected() {
        //...
    }

    @Override
    public void onConnectedDeath() {
        //...
    }

    protected boolean isAidlServiceBindSuccess() {
        return aidlServiceBind != null && aidlServiceBind.getServerInterface() != null && aidlServiceBind.isConnectedRemoteServer();
    }

}
