package com.xiaoma.carwxsdk.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carwxbase.utils.CarWXConstants;
import com.xiaoma.carwxsdk.AsrListener;
import com.xiaoma.carwxsdk.ClientListener;
import com.xiaoma.carwxsdk.SpeedChangeListener;
import com.xiaoma.carwxsdk.TtsListener;
import com.xiaoma.carwxsdk.XMCarInterface;
import com.xiaoma.carwxsdk.callback.AsrCallBack;
import com.xiaoma.carwxsdk.callback.CarSpeedChangeCallBack;
import com.xiaoma.carwxsdk.callback.TtsCallBack;
import com.xiaoma.carwxsdk.callback.UploadContactCallBack;

import java.util.List;


class LinkManager {
    private final String TAG = "LinkManager";
    private static final String SERVICE_ACTION = "com.xiaoma.sdk.CARWX";
    private static final String SERVICE_PACKAGE = "com.xiaoma.launcher";
    private static final String EXTRA_KEY = "com.xiaoma.CLIENT_PACKAGE_KEY";

    private static LinkManager instance;

    private XMCarInterface xmCarInterface;
    private Context context;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if (binder == null) {
                return;
            }
            xmCarInterface = XMCarInterface.Stub.asInterface(binder);
            try {
                xmCarInterface.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected: service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: service disconnected");
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied: thread=" + Thread.currentThread().getName());
            if (xmCarInterface == null) return;
            xmCarInterface.asBinder().unlinkToDeath(this, 0);
            xmCarInterface = null;
            linkService();
        }
    };

    static LinkManager getInstance() {
        if (instance == null) {
            synchronized (LinkManager.class) {
                if (instance == null) {
                    instance = new LinkManager();
                }
            }
        }
        return instance;
    }

    boolean isLinked() {
        Log.d(TAG, "isLinked: =" + (xmCarInterface != null));
        return xmCarInterface != null;
    }

    private Intent initIntent() {
        Intent intent = new Intent();
        intent.setAction(SERVICE_ACTION);
        intent.setPackage(SERVICE_PACKAGE);
        intent.putExtra(EXTRA_KEY, context.getPackageName());
        return intent;
    }

    private boolean linkService() {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        if (isLinked()) {
            return true;
        }
        Intent intent = initIntent();
        boolean status = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "linkService: bind Service success:" + status);
        return status;
    }

    boolean init(Context context) {
        Log.d(TAG, "init: context=" + context);
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.context = context;
        return linkService();
    }

    void startNaviByPoi(String name, double lat, double lon) {
        Log.d(TAG, "startNaviByPoi: name=" + name + ",lat=" + lat + ",lon=" + lon);
        if (TextUtils.isEmpty(name)) return;
        if (isLinked()) {
            try {
                xmCarInterface.startNaviByPoi(lat, lon, name);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "startNaviByPoi: meet exception :"+e.getMessage());
            }
        }
    }

    void startNaviByKey(String keyWords) {
        Log.d(TAG, "startNaviByKey: keyWords=" + keyWords);
        if (TextUtils.isEmpty(keyWords)) return;
        if (isLinked()) {
            try {
                xmCarInterface.startNaviByKey(keyWords);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "startNaviByKey: meet exception :"+e.getMessage());
            }
        }
    }

    void stopTTS() {
        Log.d(TAG, "stopTTS: ");
        if (isLinked()) {
            try {
                xmCarInterface.stopTTS();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "stopTTS: meet exception :"+e.getMessage());
            }
        }
    }

    void startTTS(String id, String speakContent) {
        Log.d(TAG, "startTTS: id=" + id + ",speakContent=" + speakContent);
        if (TextUtils.isEmpty(speakContent)) return;
        if (isLinked()) {
            try {
                xmCarInterface.startTTS(id, speakContent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "startTTS: meet exception :"+e.getMessage());
            }
        }
    }

    void setTtsListener(final TtsCallBack ttsCallBack) {
        Log.d(TAG, "setTtsListener: ttsCallBack is null:" + (ttsCallBack == null));
        if (ttsCallBack == null) return;
        if (!isLinked()) {
            ttsCallBack.onFailed(CarWXConstants.CODE_CLIENT_ERROR_SERVICE_UNLINK, CarWXConstants.REMOTE_SERVICE_UNLINK);
            return;
        }
        try {
            xmCarInterface.setTTSListener(new TtsListener.Stub() {
                @Override
                public void onFinish(String id) {
                    if (ttsCallBack != null)
                        ttsCallBack.onFinish(id);
                }

                @Override
                public void onStart(String id) {
                    if (ttsCallBack != null)
                        ttsCallBack.onStart(id);
                }

                @Override
                public void onError(String id, int code) {
                    if (ttsCallBack != null)
                        ttsCallBack.onError(id, code);
                }

                @Override
                public void onProgress(String id, int voice, int process) {
                    if (ttsCallBack != null)
                        ttsCallBack.onProgress(id, voice, process);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (ttsCallBack != null)
                ttsCallBack.onFailed(CarWXConstants.CODE_REMOTE_ERROR, e.getMessage());
            Log.d(TAG, "setTtsListener: meet exception :"+e.getMessage());
        }
    }

    void startRecord( boolean needPunctuation) {
        Log.d(TAG, "startRecord: " + "needPunctuation=" + needPunctuation);
        if (!isLinked()) return;
        try {
            xmCarInterface.startRecord(needPunctuation);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG, "startRecord: meet exception :"+e.getMessage());
        }
    }

    void finishRecord() {
        Log.d(TAG, "finishRecord: ");
        if (!isLinked()) return ;
        try {
            xmCarInterface.finishRecord();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "finishRecord: meet exception :"+e.getMessage());
        }
    }

    void cancelRecord() {
        Log.d(TAG, "cancelRecord: ");
        if (!isLinked()) return;
        try {
            xmCarInterface.cancelRecord();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "cancelRecord: meet exception :"+e.getMessage());
        }
    }

    void setASRListener(final AsrCallBack asrCallBack) {
        Log.d(TAG, "setASRListener: asrCallBack is null:" + (asrCallBack == null));
        if (asrCallBack == null) return;
        if (!isLinked()) {
            asrCallBack.onFailed(CarWXConstants.CODE_CLIENT_ERROR_SERVICE_UNLINK, CarWXConstants.REMOTE_SERVICE_UNLINK);
            return;
        }
        try {
            xmCarInterface.setASRListener(new AsrListener.Stub() {

                @Override
                public void onVolumeChanged(int volume) {
                    asrCallBack.onVolumeChanged(volume);
                }

                @Override
                public void onSRstatus(int i) {
                    asrCallBack.onSRstatus(i);
                }

                @Override
                public void onError(int errorCode) {
                    asrCallBack.onError(errorCode);
                }

                @Override
                public void showSrText(String voiceFilePath, String recordTxt, boolean arg1) throws RemoteException {
                    asrCallBack.showSrText(voiceFilePath, recordTxt, arg1);
                }
            });
        } catch (Exception e) {
            asrCallBack.onFailed(CarWXConstants.CODE_REMOTE_ERROR, e.getMessage());
            Log.d(TAG, "setASRListener: meet exception :"+e.getMessage());
            e.printStackTrace();
        }
    }

    void uploadContact(List<String> contacts, final UploadContactCallBack callback) {
        Log.d(TAG, "uploadContact: callback is null: " + (callback == null));
        if (contacts == null || contacts.isEmpty()) {
            Log.d(TAG, "uploadContact: contacts is null or empty !!!");
            if (callback != null)
                callback.onFailed(CarWXConstants.CODE_CLIENT_ERROR_PARAMS_ILLEGAL, CarWXConstants.PARAMS_ILLEGAL);
            return;
        }
        Log.d(TAG, "uploadContact: contacts's size = " + contacts.size());
        try {
            xmCarInterface.uploadContact(contacts, new ClientListener.Stub() {
                @Override
                public void onSuccess(String result) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailed(int errCode, String errMsg) {
                    if (callback != null) {
                        callback.onFailed(errCode, errMsg);
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null)
                callback.onFailed(CarWXConstants.CODE_REMOTE_ERROR, e.getMessage());
            Log.d(TAG, "uploadContact: meet exception :"+e.getMessage());
            e.printStackTrace();
        }
    }

    void setSpeedChangeListener(final CarSpeedChangeCallBack callback) {
        Log.d(TAG, "setSpeedChangeListener: callback is null:" + (callback == null));
        if (callback == null) return;
        if (!isLinked()) {
            callback.onFailed(CarWXConstants.CODE_CLIENT_ERROR_SERVICE_UNLINK, CarWXConstants.REMOTE_SERVICE_UNLINK);
            return;
        }
        try {
            xmCarInterface.setSpeedChangeListener(new SpeedChangeListener.Stub() {
                @Override
                public void onSpeedChanged(float arg0) throws RemoteException {
                    callback.onSpeedChanged(arg0);
                }
            });
        } catch (Exception e) {
            callback.onFailed(CarWXConstants.CODE_REMOTE_ERROR, e.getMessage());
            Log.d(TAG, "setSpeedChangeListener: meet exception :"+e.getMessage());
            e.printStackTrace();
        }
    }

    String getVin() {
        Log.d(TAG, "getVin: ");
        try {
            if (isLinked())
                return xmCarInterface.getCarVin();
            return "";
        } catch (Exception e) {
            Log.d(TAG, "getVin: meet exception :"+e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    String getSerialNumber() {
        Log.d(TAG, "getSerialNumber: ");
        try {
            if (isLinked())
                return xmCarInterface.getSerialNumber();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getSerialNumber: meet exception :"+e.getMessage());
            return "";
        }
    }
    int getCurrentTheme(){
        try {
            return xmCarInterface.getCurrentTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CarWXConstants.DEFAULT_THEME_ID;
    }
    boolean hasConnectedBluetoothDevice() {
        Log.d(TAG, "hasConnectedBluetoothDevice: ");
        try {
            if (!isLinked()) return false;
            return xmCarInterface.hasConnectedBluetoothDevice();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "hasConnectedBluetoothDevice: meet exception :"+e.getMessage());
            return false;
        }
    }

    void callPhone(String phoneNumber) {
        Log.d(TAG, "callPhone: phoneNumber = " + phoneNumber);
        try {
            if (TextUtils.isEmpty(phoneNumber)) return;
            if (!isLinked()) return;
            xmCarInterface.callPhone(phoneNumber);
        } catch (Exception e) {
            Log.d(TAG, "callPhone: meet exception :"+e.getMessage());
            e.printStackTrace();
        }
    }
}