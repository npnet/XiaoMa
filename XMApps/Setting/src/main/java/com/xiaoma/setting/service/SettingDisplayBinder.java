package com.xiaoma.setting.service;

import android.content.Context;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingDisplayAidlInterface;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LaiLai on 2018/12/5 0005.
 */

public class SettingDisplayBinder extends ISettingDisplayAidlInterface.Stub {

    private Context mContext;
    private IVendorExtension mSdkManager;

    public SettingDisplayBinder(Context context){
        mContext = context;
        mSdkManager = XmCarFactory.getCarVendorExtensionManager();
    }

    //获取当前显示模式
    @Override
    public int getDisplayMode() throws RemoteException {
        KLog.d("ljb", "getDisplayMode");
        return mSdkManager.getDisplayMode();
    }

    //设置屏幕显示模式
    @Override
    public void setDisplayMode(int mode) throws RemoteException {
        KLog.d("ljb", "setDisplayMode,mode=" + mode);
        mSdkManager.setDisplayMode(mode);
    }

    //获取屏幕亮度
    @Override
    public int getDisplayLevel() throws RemoteException {
        KLog.d("ljb", "getDisplayLevel");
        return mSdkManager.getDisplayLevel();
    }

    //设置屏幕亮度
    @Override
    public void setDisplayLevel(int level) throws RemoteException {
        KLog.d("ljb", "setDisplayLevel,level=" + level);
        mSdkManager.setDisplayLevel(level);
    }

    //获取按键亮度
    @Override
    public int getKeyBoardLevel() throws RemoteException {
        KLog.d("ljb", "getKeyBoardLevel");
        return mSdkManager.getKeyBoardLevel();
    }

    //设置按键亮度
    @Override
    public void setKeyBoardLevel(int value) throws RemoteException {
        KLog.d("ljb", "setKeyBoardLevel,value="+value);
        mSdkManager.setKeyBoardLevel(value);
    }

    //获取屏保状态
    @Override
    public boolean getBanVideoStatus() throws RemoteException {
        KLog.d("ljb", "getBanVideoStatus");
        return mSdkManager.getBanVideoStatus();
    }

    //设置屏保状态
    @Override
    public void setBanVideoStatus(boolean status) throws RemoteException {
        KLog.d("ljb", "setBanVideoStatus,status="+status);
        mSdkManager.setBanVideoStatus(status);
    }

    //关闭屏幕
    @Override
    public void closeScreen() throws RemoteException {
        KLog.d("ljb", "closeScreen");
        mSdkManager.closeScreen();
    }


}
