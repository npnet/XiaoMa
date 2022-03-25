package com.xiaoma.process.manager;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.xiaoma.aidl.smart.ISmartNotifyStatusAidlInterface;
import com.xiaoma.aidl.smart.ISmartStatusAidlInterface;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.process.listener.IRemoteServiceStatusListener;

public class XMSmartApiManager extends BaseApiManager<ISmartStatusAidlInterface> {

    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private IRemoteServiceStatusListener iRemoteServiceStatusListen;

    public void setiRemoteServiceStatusListen(IRemoteServiceStatusListener iRemoteServiceStatusListen) {
        this.iRemoteServiceStatusListen = iRemoteServiceStatusListen;
    }

    XMSmartApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService(){
        return bindServiceConnected();
    }

    private boolean bindServiceConnected() {
        if(aidlServiceBind == null){
            aidlServiceBind = new BaseAidlServiceBindManager<ISmartStatusAidlInterface>(context, XMApiConstants.SMART_SERVICE_CONNECT_ACTION, XMApiConstants.SMART_SERVICE, this){
                @Override
                public ISmartStatusAidlInterface initServiceByIBinder(IBinder service) {
                    return ISmartStatusAidlInterface.Stub.asInterface(service);
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
        if (iRemoteServiceStatusListen != null) {
            iRemoteServiceStatusListen.onConnected();
        }
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().registerStatusNotify(notifyStatusAidlInterfaceProxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unBindService() {
        try {
            if(isAidlServiceBindSuccess()){
                aidlServiceBind.getServerInterface().unRegisterStatusNotify(notifyStatusAidlInterfaceProxy);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.unBindService();
    }

    public ISmartStatusAidlInterface getStatusAidlInterface(){
        if(aidlServiceBind != null) {
            return aidlServiceBind.getServerInterface();
        }
        return null;
    }

    @Override
    public void onDisConnected() {
        super.onDisConnected();
        if (iRemoteServiceStatusListen != null) {
            iRemoteServiceStatusListen.onDisConnected();
        }
    }

    @Override
    public void onConnectedDeath() {
        super.onConnectedDeath();
        if (iRemoteServiceStatusListen != null) {
            iRemoteServiceStatusListen.onConnectedDeath();
        }
    }

    private ISmartNotifyStatusAidlInterface notifyStatusAidlInterfaceProxy = new ISmartNotifyStatusAidlInterface.Stub() {

        @Override
        public void notifyDoorStatus(int[] doorStatus) throws RemoteException {
            if (iRemoteServiceStatusListen != null) {
                iRemoteServiceStatusListen.notifyDoorStatus(doorStatus);
            }
        }

        @Override
        public void notifyWindowStatus(int[] windowStatus) throws RemoteException {
            if (iRemoteServiceStatusListen != null) {
                iRemoteServiceStatusListen.notifyWindowStatus(windowStatus);
            }
        }

        @Override
        public void notifyLockStatus(final int[] lockStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyLockStatus(lockStatus);
                    }
                }
            });
        }

        @Override
        public void notifyLightStatus(final int[] lightStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyLightStatus(lightStatus);
                    }
                }
            });
        }

        @Override
        public void notifyPowerChange(final int power) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyPowerChange(power);
                    }
                }
            });
        }

        @Override
        public void notifyOilChange(final int oil) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyOilChange(oil);
                    }
                }
            });
        }

        @Override
        public void notifyMileageChange(final int mileage) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyMileageChange(mileage);
                    }
                }
            });
        }

        @Override
        public void notifyAcStatus(final boolean on) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyAcStatus(on);
                    }
                }
            });
        }

        @Override
        public void notifyAcTemp(final float leftTemp, final float rightTemp) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyAcTemp(leftTemp, rightTemp);
                    }
                }
            });
        }

        @Override
        public void notifyAutoStatus(final boolean on) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyAutoStatus(on);
                    }
                }
            });
        }

        @Override
        public void notifyDualStatus(final boolean on) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyDualStatus(on);
                    }
                }
            });
        }

        @Override
        public void notifySeatWarmStatus(final int[] warmStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifySeatWarmStatus(warmStatus);
                    }
                }
            });
        }

        @Override
        public void notifyWindowWarmStatus(final int[] warmStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyWindowWarmStatus(warmStatus);
                    }
                }
            });
        }

        @Override
        public void notifyWindSpeed(final int speed) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyWindSpeed(speed);
                    }
                }
            });
        }

        @Override
        public void notifyWindModel(final int windowModel) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyWindModel(windowModel);
                    }
                }
            });
        }


        @Override
        public void notifySkyLightStatus(final int skyLightStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null){
                        iRemoteServiceStatusListen.notifySkyLightStatus(skyLightStatus);
                    }
                }
            });
        }

        @Override
        public void notifyLooperStatus(final int looperStatus) throws RemoteException {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (iRemoteServiceStatusListen != null) {
                        iRemoteServiceStatusListen.notifyLooperStatus(looperStatus);
                    }
                }
            });
        }
    };
}
