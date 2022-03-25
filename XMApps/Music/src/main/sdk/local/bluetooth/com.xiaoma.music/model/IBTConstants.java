package com.xiaoma.music.model;

import android.support.annotation.IntDef;
import com.xiaoma.utils.log.KLog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

/**
 * Created by ZYao.
 * Date ：2018/10/24 0024
 */
public class IBTConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IBTConnectState.STATE_DISCONNECTED, IBTConnectState.STATE_CONNECTING,
            IBTConnectState.STATE_CONNECTED, IBTConnectState.STATE_DISCONNECTING})
    public @interface IBTConnectState {
        int STATE_DISCONNECTED = 0;
        int STATE_CONNECTING = 1;
        int STATE_CONNECTED = 2;
        int STATE_DISCONNECTING = 3;
    }

    public static int getBTConnectState(int status) {
        int audioState = -1;
        switch (status) {
            case STATE_DISCONNECTED:
                audioState = IBTConnectState.STATE_DISCONNECTED;
                break;
            case STATE_CONNECTING:
                audioState = IBTConnectState.STATE_CONNECTING;
                break;
            case STATE_CONNECTED:
                audioState = IBTConnectState.STATE_CONNECTED;
                break;
            case STATE_DISCONNECTING:
                audioState = IBTConnectState.STATE_DISCONNECTING;
                break;
            default:
                KLog.d("Error audio status " + status);
                break;
        }
        return audioState;
    }
}
