package com.xiaoma.launcher.common.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherIBCallManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;

/**
 * @author taojin
 * @date 2019/4/24
 */
public class LauncherBluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = LauncherBluetoothReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KLog.d(TAG, action);
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int btAdapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            int btAdapterPreState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            if (btAdapterPreState == BluetoothAdapter.STATE_TURNING_ON) {

                if (btAdapterState == BluetoothAdapter.STATE_ON) {
                    EventBus.getDefault().post(LauncherConstants.BlUETOOTH_STATUS, LauncherConstants.BlUETOOTH_TURN_ON_SUCCESS);
                } else if (btAdapterState == BluetoothAdapter.STATE_OFF) {
                    EventBus.getDefault().post(LauncherConstants.BlUETOOTH_STATUS, LauncherConstants.BlUETOOTH_TURN_ON_FAILED);
                }
            } else if (btAdapterPreState == BluetoothAdapter.STATE_TURNING_OFF) {
                if (btAdapterState == BluetoothAdapter.STATE_OFF) {
                    EventBus.getDefault().post(LauncherConstants.BlUETOOTH_STATUS, LauncherConstants.BlUETOOTH_TURN_OFF_SUCCESS);
                } else if (btAdapterState == BluetoothAdapter.STATE_ON) {
                    EventBus.getDefault().post(LauncherConstants.BlUETOOTH_STATUS, LauncherConstants.BlUETOOTH_TURN_OFF_FAILED);
                }

            }
        } else if (LauncherConstants.PERIOD_ACTION.equals(action)) {
            TPUtils.put(context, LauncherConstants.IS_NEED_PERIOD, intent.getBooleanExtra(LauncherConstants.EXTRA_PERIOD_DATA, false));
            EventBus.getDefault().post(intent.getBooleanExtra(LauncherConstants.EXTRA_PERIOD_DATA, false), LauncherConstants.PERIOD_STATE);
        } else if (CenterConstants.IN_A_IBCALL.equals(action)) {
            LauncherIBCallManager.setIsIBCall(true);
        } else if (CenterConstants.END_OF_IBCALL.equals(action)) {
            LauncherIBCallManager.setIsIBCall(false);
        }
    }

}
