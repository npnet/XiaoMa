package com.xiaoma.instructiondistribute.utils.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nforetek.bt.aidl.INfCommandA2dp;
import com.nforetek.bt.aidl.UiCommand;
import com.nforetek.bt.res.NfDef;
import com.xiaoma.instructiondistribute.listener.ServiceListener;

import java.lang.reflect.Method;

/**
 * Created by hamm on 19-5-10.
 */

public class BluetoothA2dpSinkExt {
    private String TAG = "Nf_BluetoothA2dpSinkExt";

    private INfCommandA2dp mCommandA2dp;
    private Context mContext;
    private ServiceListener mServiceListener;
    private UiCommand mUiCommand;


    private void notifyServiceConnected(boolean state) {
        if (mServiceListener != null) {
            mServiceListener.onConnectedState(state);
        }
    }

    public BluetoothA2dpSinkExt(Context context) {
        mContext = context;
    }

    public void init(ServiceListener listener) {
        Log.v(TAG, "bindA2dpService");
        mServiceListener = listener;
        Intent intent = new Intent();
        //intent.setPackage(NfDef.PACKAGE_NAME);
        //intent.setAction(NfDef.CLASS_SERVICE_A2DP);
        intent.setComponent(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP));
        mContext.bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);
        Intent intent2 = new Intent("com.nforetek.bt.START_UI_SERVICE");
        intent2.setPackage("com.nforetek.bt.demo.service");
        mContext.bindService(intent2, this.mUiConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean setBtPairWithDevice(String macAddress) {
        if (mUiCommand != null) {
            try {
               return mUiCommand.reqBtPair(macAddress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getBtModuleVersion() {
        if (mUiCommand != null) {
            try {
                return mUiCommand.getNfServiceVersionName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void deInit() {
        Log.i(TAG, "start unbind service");
        mServiceListener = null;
        mContext.unbindService(mConnection);
        mContext.unbindService(mUiConnection);
        Log.i(TAG, "end unbind service");
    }

    private ServiceConnection mUiConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "ready onServiceConnected");

            Log.v(TAG, "Piggy Check className : " + className);

            if (className.getPackageName().equals("com.nforetek.bt.demo.service")) {
                Log.i(TAG, "ComponentName(com.nforetek.bt.demo.service");
                mUiCommand = UiCommand.Stub.asInterface(service);
                if (mUiCommand == null) {
                    Log.i(TAG, "mUiCommand is null !!");
                    return;
                }
                dumpClassMethod(mUiCommand.getClass());

                notifyServiceConnected(true);
                try {
//                        mUiCommand.reqAvrcp13GetElementAttributesPlaying();
//                        mUiCommand.reqAvrcpUpdateSongStatus();
                    if (mUiCommand.isAvrcpConnected()) {
                        Log.d("hzx", "Avrcp connected");
                        mUiCommand.reqAvrcp13GetElementAttributesPlaying();
                        mUiCommand.reqAvrcpUpdateSongStatus();
                        mUiCommand.reqAvrcp13GetCapabilitiesSupportEvent();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            Log.i(TAG, "end onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "ready onServiceDisconnected: " + className);
            if (className.getPackageName().equals("com.nforetek.bt.demo.service")) {
                mUiCommand = null;
            }
            Log.i(TAG, "end onServiceDisconnected");
            notifyServiceConnected(false);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "ready onServiceConnected");

            Log.v(TAG, "Piggy Check className : " + className);

            Log.i(TAG, "IBinder service: " + service.hashCode());
            try {
                Log.v(TAG, "Piggy Check service : " + service.getInterfaceDescriptor());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

            if (className.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP))) {
                Log.i(TAG, "ComponentName(" + NfDef.CLASS_SERVICE_A2DP + ")");
                mCommandA2dp = INfCommandA2dp.Stub.asInterface(service);
                if (mCommandA2dp == null) {
                    Log.i(TAG, "mCommandA2dp is null !!");
                    return;
                }
                dumpClassMethod(mCommandA2dp.getClass());
                notifyServiceConnected(true);
            }

            Log.i(TAG, "end onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "ready onServiceDisconnected: " + className);
            if (className.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP))) {
                mCommandA2dp = null;
            }
            Log.i(TAG, "end onServiceDisconnected");
            notifyServiceConnected(false);
        }
    };

    /*
     *  For UI command API
     *
     */
    private void dumpClassMethod(Class c) {
        for (Method method : c.getDeclaredMethods()) {
            Log.i(TAG, "Method name: " + method.getName());
        }
    }
}
