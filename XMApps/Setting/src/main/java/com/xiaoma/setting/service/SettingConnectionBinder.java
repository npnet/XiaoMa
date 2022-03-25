package com.xiaoma.setting.service;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingConnectionAidlInterface;
import com.xiaoma.aidl.setting.ISettingNotifyAidlInterface;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.setting.bluetooth.service.BluetoothServiceManager;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LaiLai on 2018/12/5 0005.
 */

public class SettingConnectionBinder extends ISettingConnectionAidlInterface.Stub {

    private Context mContext;
    private RemoteCallbackList<ISettingNotifyAidlInterface> mRemoteCallbackList = new RemoteCallbackList<>();
    private IVendorExtension mSdkManager;

    public SettingConnectionBinder(Context context){
        mContext = context;
        mSdkManager = XmCarFactory.getCarVendorExtensionManager();
    }

    @Override
    public void registerStatusNotify(ISettingNotifyAidlInterface notifyAidlInterface) throws RemoteException {
        KLog.d("ljb", "registerStatusNotify");
        if (notifyAidlInterface != null) {
            mRemoteCallbackList.register(notifyAidlInterface);
        }
        KLog.d("ljb", "registerStatusNotify getRegisteredCallbackCount: " + mRemoteCallbackList.getRegisteredCallbackCount());
    }

    @Override
    public void unRegisterStatusNotify(ISettingNotifyAidlInterface notifyAidlInterface) throws RemoteException {
        KLog.d("ljb", "unRegisterStatusNotify");
        if (notifyAidlInterface != null) {
            mRemoteCallbackList.unregister(notifyAidlInterface);
        }
    }

    //获取wifi连接状态
    @Override
    public boolean getWifiConnection() throws RemoteException {
        KLog.d("ljb", "getWifiConnection");
        return true;
    }

    //连接wifi
    @Override
    public void connectWifi() throws RemoteException {
        KLog.d("ljb", "connectWifi");
    }

    //打开wifi
    @Override
    public boolean openWifi() throws RemoteException {
        //todo
        return true;
    }

    //关闭wifi
    @Override
    public boolean closeWifi() throws RemoteException {
        KLog.d("ljb", "closeWifi");
        return true;
    }

    //获取蓝牙状态
    @Override
    public boolean getBlueToothStatus() throws RemoteException {
        KLog.d("ljb", "getBlueToothStatus");
        return false;
    }

    //连接蓝牙
    @Override
    public void connectBlueTooth() throws RemoteException {
        KLog.d("ljb", "connectBlueTooth");
    }

    //打开蓝牙
    @Override
    public boolean openBlueTooch() throws RemoteException {
        KLog.d("ljb", "openBlueTooch");
        return BluetoothServiceManager.getInstance().getBluetoothAdapter().enable();
    }

    //关闭蓝牙
    @Override
    public boolean closeBlueTooch() throws RemoteException {
        KLog.d("ljb", "closeBlueTooch");
        return BluetoothServiceManager.getInstance().getBluetoothAdapter().disable();
    }

    //获取热点状态
    @Override
    public boolean getHotspotStatus() throws RemoteException {
        KLog.d("ljb", "getHotspotStatus");
        return false;
    }

    //连接热点
    @Override
    public void connectHotspot() throws RemoteException {
        KLog.d("ljb", "connectHotspot");
    }

    //打开热点
    @Override
    public boolean openHotspot() throws RemoteException {
        KLog.d("ljb", "openHotspot");
        return true;
    }

    //关闭热点
    @Override
    public boolean closeHotspot() throws RemoteException {
        KLog.d("ljb", "closeHotspot");
        return true;
    }


    //设置上网方式
    @Override
    public void setInternetType(int type) throws RemoteException {
        KLog.d("ljb", "setInternetType");
    }

    /*
    //回调wifi连接结果
    public void onConferWifiConnect(String wifiName) {
        KLog.d("ljb", "onConferwifiConnect getRegisteredCallbackCount:" + mRemoteCallbackList.getRegisteredCallbackCount());
        //测试用
        synchronized (mRemoteCallbackList) {
            mRemoteCallbackList.beginBroadcast();
            int N = mRemoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < N; i++){
                try {
                    if (mRemoteCallbackList.getBroadcastItem(i) != null){
                        mRemoteCallbackList.getBroadcastItem(i).onConferConnectWifi(wifiName, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mRemoteCallbackList.unregister(mRemoteCallbackList.getBroadcastItem(i));
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }

    //回调蓝牙连接
    public void onConferConnectBlueTooch(String blueToochName) {
        //测试用
        synchronized (mRemoteCallbackList) {
            mRemoteCallbackList.beginBroadcast();
            int N = mRemoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < N; i++){
                try {
                    if (mRemoteCallbackList.getBroadcastItem(i) != null){
                        mRemoteCallbackList.getBroadcastItem(i).onConferConnectBlueTooch(blueToochName, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mRemoteCallbackList.unregister(mRemoteCallbackList.getBroadcastItem(i));
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }

    //回调热点连接
    public void onConferConnectHotspot(String hotspotName) {
        //测试用
        synchronized (mRemoteCallbackList) {
            mRemoteCallbackList.beginBroadcast();
            int N = mRemoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < N; i++){
                try {
                    if (mRemoteCallbackList.getBroadcastItem(i) != null){
                        mRemoteCallbackList.getBroadcastItem(i).onConferConnectHotspot(hotspotName, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mRemoteCallbackList.unregister(mRemoteCallbackList.getBroadcastItem(i));
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }*/

}
