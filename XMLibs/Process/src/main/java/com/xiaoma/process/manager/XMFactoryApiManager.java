package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.factory.IFactoryAidlInterface;
import com.xiaoma.aidl.factory.IFactoryNotifyAidlInterface;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.INotifyMsgChangeListener;

import java.util.ArrayList;
import java.util.List;

public class XMFactoryApiManager extends BaseApiManager<IFactoryAidlInterface> {

    private List<INotifyMsgChangeListener> notifyMsgChangeListens = new ArrayList<>();
    private IFactoryNotifyAidlInterface proxyStatusNotifyAidlImple = new IFactoryNotifyAidlInterface.Stub() {
        @Override
        public void refreshUnReadNotifyMessageCount(int unReadTotalMessageCount) throws RemoteException {
            notifyMessageCountChanged(unReadTotalMessageCount);
        }
    };

    XMFactoryApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService() {
        return bindServiceConnected();
    }

    @Override
    public void unBindService() {
        unRegisterNotifyListener();
        super.unBindService();
    }

    private boolean bindServiceConnected() {
        if (aidlServiceBind == null) {
            aidlServiceBind = new BaseAidlServiceBindManager<IFactoryAidlInterface>(context, XMApiConstants.FACTORY_STATUS_SERVICE_CONNECT_ACTION, XMApiConstants.FACTORY, this) {
                @Override
                public IFactoryAidlInterface initServiceByIBinder(IBinder service) {
                    return IFactoryAidlInterface.Stub.asInterface(service);
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
        registerNotifyListener();
        searchUnReadNotifyMessageCount();
    }

    private void notifyMessageCountChanged(int unReadTotalMessageCount) {
        for (INotifyMsgChangeListener listen : notifyMsgChangeListens) {
            listen.unReadNotifyMessageCount(unReadTotalMessageCount);
        }
    }

    public void addFANotifyMsgListen(INotifyMsgChangeListener listen) {
        if (listen == null || notifyMsgChangeListens.contains(listen)) {
            return;
        }
        notifyMsgChangeListens.add(listen);
    }

    public void removeFANotifyMsgListen(INotifyMsgChangeListener listen) {
        if (listen == null || !notifyMsgChangeListens.contains(listen)) {
            return;
        }
        notifyMsgChangeListens.remove(listen);
    }

    public void searchUnReadNotifyMessageCount() {
        try {
            if (isAidlServiceBindSuccess())
                aidlServiceBind.getServerInterface().searchUnReadMessageCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unRegisterNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unRegisterStatusNotify(proxyStatusNotifyAidlImple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().registerStatusNotify(proxyStatusNotifyAidlImple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
