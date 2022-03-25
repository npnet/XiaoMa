package com.xiaoma.process.manager;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.club.IClubStatusAidlInterface;
import com.xiaoma.aidl.club.IClubStatusNotifyAidlInterface;
import com.xiaoma.aidl.model.MessageInfo;
import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IMsgChangeListener;
import com.xiaoma.process.listener.IPlayInfoChangeListener;

import java.util.ArrayList;
import java.util.List;

public class XMClubApiManager extends BaseApiManager<IClubStatusAidlInterface> {

    private List<IMsgChangeListener> msgChangeListens = new ArrayList<>();
    private List<IPlayInfoChangeListener> playStatusChangeListens = new ArrayList<>();
    private IClubStatusNotifyAidlInterface proxyStatusNotifyAidlImpl = new IClubStatusNotifyAidlInterface.Stub() {
        @Override
        public void refreshIMUnReadMessageCount(MessageInfo messageInfo) throws RemoteException {
            notifyMsgChanged(messageInfo);
        }

        @Override
        public void playMusicName(MusicInfo musicInfo) throws RemoteException {
            notifyPlayStatusChanged(musicInfo);
        }
    };

    XMClubApiManager(Context context) {
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
            aidlServiceBind = new BaseAidlServiceBindManager<IClubStatusAidlInterface>(context, XMApiConstants.CLUB_STATUS_SERVICE_CONNECT_ACTION, XMApiConstants.CLUB, this){
                @Override
                public IClubStatusAidlInterface initServiceByIBinder(IBinder service) {
                    return IClubStatusAidlInterface.Stub.asInterface(service);
                }
            };
        }
        if(!aidlServiceBind.isConnectedRemoteServer()) {
            return aidlServiceBind.connectRemoteService();
        } else {
            return true;
        }
    }

    @Override
    public void onConnected() {
        registerNotifyListener();
        searchClubUnReadMessageCount();
        getPlayMusicStatus();
    }

    private void notifyMsgChanged(MessageInfo messageInfo) {
        for (IMsgChangeListener listen : msgChangeListens) {
            listen.unReadMessageCountChange(messageInfo);
        }
    }

    private void notifyPlayStatusChanged(MusicInfo musicInfo) {
        for (IPlayInfoChangeListener listener : playStatusChangeListens) {
            listener.onPlayStatusChange(musicInfo);
        }
    }

    public void addMsgChangeListener(IMsgChangeListener listen){
        if (listen == null || msgChangeListens.contains(listen)) {
            return;
        }
        msgChangeListens.add(listen);
    }

    public void removeMsgChangeListener(IMsgChangeListener listen){
        if (listen == null || !msgChangeListens.contains(listen)) {
            return;
        }
        msgChangeListens.remove(listen);
    }

    public void addPlayStatusListener(IPlayInfoChangeListener listen){
        if (listen == null || playStatusChangeListens.contains(listen)) {
            return;
        }
        playStatusChangeListens.add(listen);
    }

    public void removePlayStatusListener(IPlayInfoChangeListener listen){
        if (listen == null || !playStatusChangeListens.contains(listen)) {
            return;
        }
        playStatusChangeListens.remove(listen);
    }

    public void searchClubUnReadMessageCount(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().searchUnReadMessageCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlayMusicStatus() {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().getCurrPlayingMusicName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPre() {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().playPreMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNext() {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().playNextMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().switchPlay(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            sendBroadcastToKlNotResumePlay();
            if (isAidlServiceBindSuccess()) {
                aidlServiceBind.getServerInterface().switchPlay(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcastToKlNotResumePlay() {
        if(context != null){
            Intent intent = new Intent("com.kaolafm.mediaplayer.resumePlay");
            intent.putExtra("resume_play", false);
            context.sendBroadcast(intent);
        }
    }

    private void unRegisterNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unRegisterStatusNotify(proxyStatusNotifyAidlImpl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNotifyListener(){
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().registerStatusNotify(proxyStatusNotifyAidlImpl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
