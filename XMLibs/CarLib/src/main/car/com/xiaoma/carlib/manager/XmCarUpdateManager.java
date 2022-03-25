package com.xiaoma.carlib.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.excelfore.hmiagent.OtaInstallState;
import com.excelfore.hmiagent.OtaUpdateReq;
import com.fsl.android.OtaBinderPool;
import com.fsl.android.TboxBinderPool;
import com.fsl.android.ota.OtaBinder;
import com.fsl.android.ota.inter.IBaseInterface;
import com.fsl.android.ota.inter.IVersionInterface;
import com.fsl.android.ota.other.CustomAction;
import com.fsl.android.serverota.OtaConstants;
import com.fsl.android.serverota.helper.FullOtaClient;
import com.fsl.android.serverota.listener.FullOtaInterface;
import com.xiaoma.thread.ThreadDispatcher;


public class XmCarUpdateManager implements IUpdate {

    private static final String TAG = XmCarUpdateManager.class.getSimpleName();
    private static XmCarUpdateManager instance;
    private final String[] mEcuName = new String[]{
            "GW",
            "EMS",
            "TCU",
            "EGSM",
            "ACM",
            "ACU",
            "ESP",
            "EPS",
            "BCM",
            "DDCU",
            "PDCU",
            "RLDCU",
            "RRDCU",
            "PLG",
            "AC",
            "DSCU",
            "BLE",
            "IFC",
            "APA",
            "PDC",
            "AVS",
            "LCDA",
            "LCDAS",
            "ADB",
            "DSM",
            "IC",
            "HU",
            "3D",
            "TBOX"
    };
    private final String[] mPolicyName = new String[]{
            "车辆处于设防状态",
            "电压>XX",
            "SOC>XX",
            "非远程模式",
            "Gear position is parking",
            "EPB is locked"
    };
    private OtaBinder mBinder;
    private FullOtaClient mFullOtaClient;
    private Context mContext;
    private OnConferStateListener mOnConferStateListener;
    private boolean isInit = false;
    private volatile boolean isAsrRelease = false;//ota升级释放语音资源，只允许一次;
    private volatile boolean isRegisterAsrRelease = false;
    private OnVersionListener versionListener;
    private FullOtaInterface mFullOtaInterface = new FullOtaInterface() {

        @Override
        public void onUpdateRequest(final OtaUpdateReq info) throws RemoteException {
            Log.e(TAG, "onUpdateRequest,info=" + info + ",getPackageName=" + info.getPackageName() + ";getUpdateTime=" + info.getUpdateTime());
            if (mOnConferStateListener == null) {
                return;
            }
            try {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        mOnConferStateListener.onUpdateRequest(info.getPackageName(), info.getUpdateTime());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInstallUpdate(final OtaInstallState info) throws RemoteException {
            Log.e(TAG, "onInstallUpdate,info=" + info + ";info.getState()=" + info.getState());
            if (mOnConferStateListener == null) {
                return;
            }
            try {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        switch (info.getState()) {
                            case 0:  //未开始
                                break;
                            case 1:  //安装中
                                Log.e(TAG, "onInstalling,getInstallingEcu=" + info.getInstallingEcu());
                                mOnConferStateListener.onInstalling(mEcuName[info.getInstallingEcu() - 1]);
                                break;
                            case 2:  //安装成功
                                Log.e(TAG, "onInstallSuccess,getInstallingEcu=" + info.getInstallingEcu());
                                mOnConferStateListener.onInstallSuccess(mEcuName[info.getInstallingEcu() - 1]);
                                break;
                            case 3:  //安装条件不满足
                                Log.e(TAG, "onInstallFailed,getPolicy=" + info.getPolicy());
                                mOnConferStateListener.onInstallFailed(getPolicyContent(info.getPolicy()));
                                break;
                            case 4:  //安装失败
                                if (canUseSystem(info.getInstallingEcu())) {
                                    Log.e(TAG, "onInstallErrorCanUseSystem,getInstallingEcu=" + info.getInstallingEcu());
                                    mOnConferStateListener.onInstallErrorCanUseSystem(mEcuName[info.getInstallingEcu() - 1]);
                                } else {
                                    Log.e(TAG, "onInstallErrorNotUseSystem,getInstallingEcu=" + info.getInstallingEcu());
                                    mOnConferStateListener.onInstallErrorNotUseSystem(mEcuName[info.getInstallingEcu() - 1]);
                                }
                                break;
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getAction());
            boolean ac = intent.getAction().equals("com.xiaoma.carlib.manager.destroyXFResult");
            boolean pack = "com.xiaoma.assistant".equals(intent.getPackage());
            if (ac & pack) {
                if (mBinder != null && !isAsrRelease) {
                    mBinder.asrReleaseResult(CustomAction.AsrResult.RELEASE_SUCCESS);
                    isAsrRelease = true;
                    Log.e("destroy log", " asrReleaseResult SUCCESS");
                } else {
                    Log.e("destroy log", " asrReleaseResult Error");
                }
            }
        }
    };

    private XmCarUpdateManager() {

    }

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

    public void setOnConferStateListener(OnConferStateListener onConferStateListener) {
        this.mOnConferStateListener = onConferStateListener;
    }

    private boolean canUseSystem(int installingEcu) {
        if (installingEcu == 1) {
            return true;
        } else if (installingEcu == 15) {
            return true;
        } else if (installingEcu >= 18 && installingEcu <= 29) {
            return true;
        } else {
            return false;
        }
    }

    private String getPolicyContent(int[] policy) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < policy.length; i++) {
            if (policy[i] == 1) {
                if (i < mPolicyName.length) {
                    stringBuilder.append(mPolicyName[i]);
                    stringBuilder.append(";");
                }
            }
        }
        return stringBuilder.toString();
    }

    public void init(Context context) {
        if (isInit) {
            return;
        }
        isInit = true;

        mContext = context;

        IntentFilter intentFilter = new IntentFilter("com.xiaoma.carlib.manager.destroyXFResult");
        context.registerReceiver(mBroadcastReceiver, intentFilter);

        initLocal();
        initTbox();

    }

    public void confirmUpdate() {
        Log.e(TAG, "confirmUpdate");
        try {
            if (mFullOtaClient != null) {
                mFullOtaClient.updateConfirm(OtaConstants.UpdateConfirm.Confirm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelUpdate() {
        Log.e(TAG, "cancelUpdate");
        try {
            if (mFullOtaClient != null) {
                mFullOtaClient.updateConfirm(OtaConstants.UpdateConfirm.cancel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OtaInstallState getInstallState() {
        if (mFullOtaClient != null) {
            return mFullOtaClient.getInstallState();
        }
        return null;
    }

    public void releaseXF() {
        Intent intent = new Intent();
        intent.setAction("com.xiaoma.cariflytek.destroyXF");
        intent.setPackage(mContext.getPackageName());
        intent.addFlags(0x01000000);
        mContext.sendBroadcast(intent);
    }

    private void initLocal() {
        final OtaBinderPool binderPool = OtaBinderPool.getInstance(mContext);
        binderPool.setConnListener(new OtaBinderPool.ServiceConnListener() {
            @Override
            public void onResult(int paramInt) {
                if (paramInt == OtaBinderPool.BINDER_SUCCEED) {
                    if(mContext!=null&&mContext.getPackageName().equals("com.xiaoma.setting")){
                        try {
                            Log.e(TAG, "bind ota success");
                            IBinder otaBinder = binderPool.queryClient(OtaBinderPool.FULL_SERVER_OTA);
                            mFullOtaClient = new FullOtaClient(otaBinder);
                            mFullOtaClient.registFullOtaListener(mFullOtaInterface);
                            Log.e(TAG, "ota register done");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    IBinder binder = binderPool.queryClient(OtaBinderPool.LOCAL);
                    mBinder = new OtaBinder(binder);
                    mBinder.registVersionListener(new IVersionInterface() {
                        @Override
                        public void onVersion(int type, String version) throws RemoteException {
                            Log.e(TAG, "type = " + type + " version = " + version);
                            if (versionListener != null) {
                                versionListener.onVersionInfo(type, version);
                            }
                        }
                    });
                    mBinder.getVersion(CustomAction.VERSION_TYPE.SN);
                    registerDestroyXF();
                } else {
                    Log.e(TAG, "bind ota fail");
                }
            }
        });
        binderPool.prepare();
    }

    private void registerDestroyXF() {
        if (mBinder != null && mContext != null && "com.xiaoma.assistant".equals(mContext.getPackageName()) && !isRegisterAsrRelease) {
            isRegisterAsrRelease = true;
            mBinder.registerBaseListener(new IBaseInterface() {
                @Override
                public void requestAsrRelease() throws RemoteException {
                    super.requestAsrRelease();
                    Log.e("destroy log", " requestAsrRelease ");
                    releaseXF();
                }
            });
        }
    }

    private void initTbox() {
        XmTboxBinderPoolManager instance = XmTboxBinderPoolManager.getInstance();
        instance.init(mContext);
        instance.registerTboxbinderPoolConnectedListener(new XmTboxBinderPoolManager.OnTboxBinderPoolConnectionListener() {
            @Override
            public void onConnected(TboxBinderPool tboxBinderPool) {
            }

            @Override
            public void onDisconnected(TboxBinderPool tboxBinderPool) {
            }
        });
        instance.prepare();
    }

    public void getVersionInfo() {
        if (mBinder != null) {
            mBinder.getVersion(CustomAction.VERSION_TYPE.OS_LOCAL_VERSION);
            mBinder.getVersion(CustomAction.VERSION_TYPE.MCU_LOCAL_VERSION);
        }
    }

    public void setVersionListener(OnVersionListener listener) {
        this.versionListener = listener;
    }

    public interface OnConferStateListener {

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
