package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.model.MessageInfo;
import com.xiaoma.aidl.service.IServiceStatusAidlInterface;
import com.xiaoma.aidl.service.IServiceStatusNotifyAidlInterface;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IMsgChangeListener;

import java.util.ArrayList;
import java.util.List;

public class XMServiceApiManager extends BaseApiManager<IServiceStatusAidlInterface> {

    private List<IMsgChangeListener> msgChangeListens = new ArrayList<>();
    private IServiceStatusNotifyAidlInterface proxyStatusNotifyAidlImple = new IServiceStatusNotifyAidlInterface.Stub() {
        @Override
        public void refreshIMUnReadMessageCount(MessageInfo messageInfo) throws RemoteException {
            notifyStatusChanged(messageInfo);
        }

    };

    XMServiceApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService(){
        return this.bindServiceConnected();
    }

    @Override
    public void unBindService() {
        unRegisterNotifyListener();
        super.unBindService();
    }

    private boolean bindServiceConnected(){
        if(aidlServiceBind == null){
            aidlServiceBind = new BaseAidlServiceBindManager<IServiceStatusAidlInterface>(context, XMApiConstants.SERVICE_STATUS_SERVICE_CONNECT_ACTION, XMApiConstants.SERVICE, this) {
                @Override
                public IServiceStatusAidlInterface initServiceByIBinder(IBinder service) {
                    return IServiceStatusAidlInterface.Stub.asInterface(service);
                }
            };
        }
        if(!aidlServiceBind.isConnectedRemoteServer()) {
            return aidlServiceBind.connectRemoteService();
        }else {
            return true;
        }
    }

    @Override
    public void onConnected() {
        registerNotifyListener();
        searchServiceUnReadMessageCount();
    }

    private void notifyStatusChanged(MessageInfo messageInfo){
        for (IMsgChangeListener listen : msgChangeListens) {
            listen.unReadMessageCountChange(messageInfo);
        }
    }

    public void addMsgChangeListen(IMsgChangeListener listen){
        if (listen == null || msgChangeListens.contains(listen)) {
            return;
        }
        msgChangeListens.add(listen);
    }

    public void removeMsgChangeListen(IMsgChangeListener listen){
        if (listen == null || !msgChangeListens.contains(listen)) {
            return;
        }
        msgChangeListens.remove(listen);
    }

    public void searchServiceUnReadMessageCount(){
        try {
            if(isAidlServiceBindSuccess())
                aidlServiceBind.getServerInterface().searchUnReadMessageCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unRegisterStatusNotify(proxyStatusNotifyAidlImple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().registerStatusNotify(proxyStatusNotifyAidlImple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
