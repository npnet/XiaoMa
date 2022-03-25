package com.xiaoma.instructiondistribute.utils.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nforetek.bt.aidl.INfCommandBluetooth;
import com.nforetek.bt.res.NfDef;

public class INfCommandBluetoothSdk {
    private static INfCommandBluetoothSdk INSTANCE;
    private INfCommandBluetooth iNfCommandBluetooth;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iNfCommandBluetooth = INfCommandBluetooth.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_BLUETOOTH)))
                iNfCommandBluetooth = null;
        }
    };

    private INfCommandBluetoothSdk() {
    }

    public static INfCommandBluetoothSdk getInstance() {
        if (INSTANCE == null) {
            synchronized (INfCommandBluetoothSdk.class) {
                if (INSTANCE == null) {
                    INSTANCE = new INfCommandBluetoothSdk();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        Intent intent = new Intent(NfDef.CLASS_SERVICE_BLUETOOTH);
        intent.setComponent(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_BLUETOOTH));
        boolean bindResult = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.d("INfCommandBluetooth", "init: bindresult :" + bindResult);
    }

    public String getNfServiceModuleName() {
        if (iNfCommandBluetooth != null) {
            try {
                return iNfCommandBluetooth.getNfServiceVersionName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public boolean reqPairBtWithMacAddress(String macAddress) {
        if (iNfCommandBluetooth != null) {
            try {
                return iNfCommandBluetooth.reqBtPair(macAddress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
