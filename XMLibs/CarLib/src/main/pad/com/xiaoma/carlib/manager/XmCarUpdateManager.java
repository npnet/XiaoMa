package com.xiaoma.carlib.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

//import com.fsl.android.tbox.client.UpgradeClient;


public class XmCarUpdateManager implements IUpdate {

    private static final String TAG = XmCarUpdateManager.class.getSimpleName();
//    private UpgradeClient mUpdateClient;
    private static XmCarUpdateManager instance;

    private OnConferStateListener mOnConferStateListener;

    public void setOnConferStateListener(OnConferStateListener onConferStateListener){
        this.mOnConferStateListener = onConferStateListener;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            mUpdateClient = new UpdateClient(service);
//            mUpdateClient.registerSystemCallback(mISystemCallback);
//            mUpdateClient.registerTboxCallback(mITboxCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

//    private ISystemCallback.Stub mISystemCallback = new ISystemCallback.Stub() {
//        //音响总成检测到新版本
//        @Override
//        public void onNewVersion(SystemInfo systemInfo) throws RemoteException {
//
//        }
//
//        //音响总成升级结果（包括解压、校验、安装、拷贝等）
//        @Override
//        public void onResult(int i, int i1) throws RemoteException {
//
//        }
//
//        //音响总成升级进度（包括解压、校验、安装、拷贝等）
//        @Override
//        public void onProgress(int i, int i1) throws RemoteException {
//
//        }
//    };
//
//    private ITboxCallback.Stub mITboxCallback = new ITboxCallback.Stub() {
//        //T-box检测到新版本
//        @Override
//        public void onNewVersion(TboxInfo tboxInfo) throws RemoteException {
//
//        }
//
//        //T-box 升级结果（包括下载、升级）
//        @Override
//        public void onResult(int i, int i1) throws RemoteException {
//
//        }
//
//        //T-box 升级进度（包括下载、升级）
//        @Override
//        public void onProgress(int i, int i1) throws RemoteException {
//
//        }
//    };

    public static XmCarUpdateManager getInstance() {
        if (instance == null) {
            synchronized (XmCarUpdateManager.class) {
                if (instance == null) {
                    instance = new XmCarUpdateManager();
                }
            }
        }
        return instance;
    }

    private XmCarUpdateManager() {

    }

    public void init(Context context) {
//        Intent service = new Intent(UpdateManager.SERVICE_ACTION_UPDATE);
//        service.setPackage(UpdateManager.SERVICE_PACKAGE_NAME);
//        context.bindService(service, conn, Context.BIND_AUTO_CREATE);
    }

    //获取当前系统信息
    public String getCurrentVersion(int type) {
//        if (mUpdateClient == null) {
//            return "";
//        }
//        return mUpdateClient.getCurrentVersion(type);
        return "";
    }

    //请求音响软件升级
    public void requestSystemUpdate() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.requestSystemUpdate();
    }

    //请求T-box下载
    public void requestTboxDownload() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.requestTboxDownload();
    }

    //请求T-box升级
    public void requestTboxUpdate() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.requestTboxUpdate();
    }

    //恢复出厂设置
    public void restoreFactory() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.restoreFactory();
    }

    //音响软件升级检测
    public void systemUpdateCheck() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.systemUpdateCheck();
    }

    //T-box软件升级检测
    public void tboxUpdateCheck() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.tboxUpdateCheck();
    }

    public void unResisterCallBack() {
//        if (mUpdateClient == null) {
//            return;
//        }
//        mUpdateClient.unRegisterSystemCallback(mISystemCallback);
//        mUpdateClient.unRegisterTboxCallback(mITboxCallback);
    }

    public void confirmUpdate() {
    }

    public void cancelUpdate() {
    }

    public void setVersionListener(OnVersionListener versionListener){}

    public void getVersionInfo(){}

    public interface OnConferStateListener{

        void onUpdateRequest(String packageName, int updateTime);

        void onInstallSuccess(String ecuName);

        void onInstallErrorCanUseSystem(String ecuName);

        void onInstallErrorNotUseSystem(String ecuName);

        void onInstallFailed(String content);

        void onInstalling(String ecuName);

    }

    public interface OnVersionListener {
        void onVersionInfo(int type, String version);
    }
}
