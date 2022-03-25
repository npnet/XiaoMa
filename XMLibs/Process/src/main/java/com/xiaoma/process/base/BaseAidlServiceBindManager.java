package com.xiaoma.process.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import com.xiaoma.process.listener.IRemoteServiceConnectedListener;
import com.xiaoma.utils.log.KLog;

public abstract class BaseAidlServiceBindManager<T extends IInterface> {

    private Context mContext;
    private String serviceAction;
    private T mServiceInterface;
    private IRemoteServiceConnectedListener connectedListen;
    private volatile boolean isConnectedRemoteServer = false;
    private String servicePackageName;
    private static final String SOURCE_NAME = "app";

    protected BaseAidlServiceBindManager(Context context, String serviceAction, String servicePackageName,
                                         IRemoteServiceConnectedListener connectedListen) {
        this.mContext = context.getApplicationContext();
        this.serviceAction = serviceAction;
        this.servicePackageName = servicePackageName;
        this.connectedListen = connectedListen;
    }

    public synchronized boolean connectRemoteService() {
        Intent intent = new Intent();
        intent.setAction(serviceAction);
        intent.setPackage(servicePackageName);
        intent.putExtra(SOURCE_NAME, mContext.getPackageName());
        return mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public synchronized void unBindService(){
        try {
            mContext.unbindService(conn);
            isConnectedRemoteServer = false;
            mServiceInterface = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public T getServerInterface(){
        return mServiceInterface;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KLog.d("ljb", "onServiceConnected");
            isConnectedRemoteServer = true;
            mServiceInterface = initServiceByIBinder(service);
            //绑定死亡监听
            try {
                if(mServiceInterface != null) {
                    mServiceInterface.asBinder().linkToDeath(mDeathRecipient, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(connectedListen != null) {
                connectedListen.onConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KLog.d("ljb", "onServiceDisconnected");
            isConnectedRemoteServer = false;
            if(connectedListen != null) {
                connectedListen.onDisConnected();
            }
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            isConnectedRemoteServer = false;
            //取消死亡监听
            if(mServiceInterface != null) {
                mServiceInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
                //释放资源
                mServiceInterface = null;
            }
            //重新连接服务
            connectRemoteService();
            if(connectedListen != null) {
                connectedListen.onConnectedDeath();
            }
        }
    };

    public boolean isConnectedRemoteServer(){
        return isConnectedRemoteServer;
    }

    protected abstract T initServiceByIBinder(IBinder service);

}
