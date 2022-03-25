package com.xiaoma.bluetooth.phone.main.service_bt;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTConnectReceiver extends BroadcastReceiver {
    private static final String TAG = "BTConnectReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, String.format("BTConnectReceiver: onReceive: action: %s", action));
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action) ||
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
//            Log.i(TAG, "BTConnectReceiver: onReceive: start PhoneBookService");
            context.startService(new Intent(context, PhoneBookService.class));
        }
    }
}
