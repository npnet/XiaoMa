package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.xting.IXTingPlayerAidlInterface;
import com.xiaoma.aidl.xting.IXTingPlayerNotifyAidlInterface;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IPlayInfoChangeListener;

import java.util.ArrayList;
import java.util.List;

public class XMXTingApiManager extends BaseApiManager<IXTingPlayerAidlInterface> {

    private List<IPlayInfoChangeListener> playInfoChangeListens = new ArrayList<>();
    private IXTingPlayerNotifyAidlInterface iPlayNotifyInterface = new IXTingPlayerNotifyAidlInterface.Stub() {
        @Override
        public void playMusicName(MusicInfo musicInfo) throws RemoteException {
            notifyPlayStatusChanged(musicInfo);
        }
    };

    XMXTingApiManager(Context context) {
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
            aidlServiceBind = new BaseAidlServiceBindManager<IXTingPlayerAidlInterface>(context, XMApiConstants.PLAY_SERVICE_CONNECT_ACTION, XMApiConstants.XTING, this){
                @Override
                public IXTingPlayerAidlInterface initServiceByIBinder(IBinder service) {
                    return IXTingPlayerAidlInterface.Stub.asInterface(service);
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
        getCurrPlayingMusicName();
    }

    private void unRegisterNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unRegisterStatusNotify(iPlayNotifyInterface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().registerStatusNotify(iPlayNotifyInterface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrPlayingMusicName() {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().getCurrPlayingMusicName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPreMusic(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().playPreMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNextMusic(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().playNextMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchPlay(boolean play){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().switchPlay(play);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //切换播放模式
    public void setPlayMode(int mode) {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().setPlayMode(mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlayModeRandom() {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().setPlayModeRandom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //订阅/取消订阅当前节目
    public void subscribeProgram() {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().subscribeProgram();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unSubscribeProgram() {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unSubscribeProgram();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyPlayStatusChanged(MusicInfo musicInfo) {
        for (IPlayInfoChangeListener listener : playInfoChangeListens) {
            listener.onPlayStatusChange(musicInfo);
        }
    }

    public void addPlayInfoChangeListen(IPlayInfoChangeListener listen){
        if (listen == null || playInfoChangeListens.contains(listen)) {
            return;
        }
        playInfoChangeListens.add(listen);
    }

    public void removePlayInfoChangeListen(IPlayInfoChangeListener listen){
        if (listen == null || !playInfoChangeListens.contains(listen)) {
            return;
        }
        playInfoChangeListens.remove(listen);
    }

}
