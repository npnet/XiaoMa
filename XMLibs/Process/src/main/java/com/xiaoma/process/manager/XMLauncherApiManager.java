package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.launcher.ILauncherAidlInterface;
import com.xiaoma.aidl.launcher.ILauncherNotifyAidlInterface;
import com.xiaoma.aidl.model.User;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IVoiceNotifyListener;

import java.util.ArrayList;
import java.util.List;

public class XMLauncherApiManager extends BaseApiManager<ILauncherAidlInterface> {

    private List<IVoiceNotifyListener> mVoiceNotifyListeners = new ArrayList<>();
    private ILauncherNotifyAidlInterface mILauncherNotifyAidlInterface = new ILauncherNotifyAidlInterface.Stub() {
        @Override
        public boolean notifyVoiceCmd(String cmdId) throws RemoteException {
            return XMLauncherApiManager.this.notifyVoiceCmd(cmdId);
        }

        @Override
        public boolean notifyVoiceCmdWithValue(String cmdId, String content) throws RemoteException {
            return XMLauncherApiManager.this.notifyVoiceCmdWithValue(cmdId, content);
        }

        @Override
        public void notifyVoiceViewShowing(boolean isShowing) throws RemoteException {
            XMLauncherApiManager.this.notifyVoiceViewShowing(isShowing);
        }
    };

    public boolean notifyVoiceCmd(String cmdId) {
        boolean flag = false;
        for (IVoiceNotifyListener voiceNotifyListener : mVoiceNotifyListeners) {
            flag = voiceNotifyListener.notifyVoiceCmd(cmdId);
        }
        return flag;
    }

    public boolean notifyVoiceCmdWithValue(String cmdId, String content) {
        boolean flag = false;
        for (IVoiceNotifyListener voiceNotifyListener : mVoiceNotifyListeners) {
            flag = voiceNotifyListener.notifyVoiceCmdWithValue(cmdId, content);
        }
        return flag;
    }

    public void notifyVoiceViewShowing(boolean isShowing) {
        for (IVoiceNotifyListener voiceNotifyListener : mVoiceNotifyListeners) {
            voiceNotifyListener.notifyVoiceViewShowing(isShowing);
        }
    }

    public void addVoiceNotifyListener(IVoiceNotifyListener listen){
        if (listen == null || mVoiceNotifyListeners.contains(listen)) {
            return;
        }
        mVoiceNotifyListeners.add(listen);
    }

    public void removeVoiceNotifyListener(IVoiceNotifyListener listen){
        if (listen == null || !mVoiceNotifyListeners.contains(listen)) {
            return;
        }
        mVoiceNotifyListeners.remove(listen);
    }

    XMLauncherApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService(){
        return bindServiceConnected();
    }

    @Override
    public void unBindService() {
        unRegisterNotifyListener();
        super.unBindService();
    }

    private boolean bindServiceConnected(){
        if(aidlServiceBind == null){
            aidlServiceBind = new BaseAidlServiceBindManager<ILauncherAidlInterface>(context, XMApiConstants.VOICE_SERVICE_CONNECT_ACTION, XMApiConstants.LAUNCHER, this){
                @Override
                public ILauncherAidlInterface initServiceByIBinder(IBinder service) {
                    return ILauncherAidlInterface.Stub.asInterface(service);
                }
            };
        }
        if(!aidlServiceBind.isConnectedRemoteServer()) {
            return aidlServiceBind.connectRemoteService();
        }else{
            return true;
        }
    }

    @Override
    public void onConnected() {
        registerNotifyListener();
    }


    protected boolean registerNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                return aidlServiceBind.getServerInterface().registerNotifyListener(mILauncherNotifyAidlInterface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    protected boolean unRegisterNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                return aidlServiceBind.getServerInterface().unRegisterNotifyListener(mILauncherNotifyAidlInterface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeXiaoMaVoice(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().closeXiaoMaVoice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showXiaoMaVoice(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().showXiaoMaVoice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected User getCurrentXmUser(){
        try {
            if(isAidlServiceBindSuccess()){
                //暂时注释
                //return aidlServiceBind.getServerInterface().getCurrentXmUser();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerVoiceCmd(String cmdContent, String cmdId){
        try {
            if(isAidlServiceBindSuccess()){
                return aidlServiceBind.getServerInterface().registerVoiceCmd(cmdContent, cmdId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unRegisterVoiceCmd(String cmdId){
        try {
            if(isAidlServiceBindSuccess()){
                return aidlServiceBind.getServerInterface().unRegisterVoiceCmd(cmdId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
