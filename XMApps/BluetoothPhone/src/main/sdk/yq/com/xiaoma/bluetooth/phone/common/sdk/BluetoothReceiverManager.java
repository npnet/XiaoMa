package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHeadsetClientCall;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothPhoneDbManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.screentool.ScreenControlUtil;

import org.simple.eventbus.EventBus;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/5/20
 * @Desc:
 */
public class BluetoothReceiverManager extends BroadcastReceiver {
    private static String TAG = "BluetoothReceiverManager";
    private static BluetoothReceiverManager btReceiverManager;
    private final Context mContext;
    private Map<String, BluetoothDevice> mHfpDeviceMap = new HashMap<>();

    private BluetoothReceiverManager() {
        mContext = BlueToothPhone.getContext();

    }

    public static BluetoothReceiverManager getInstance() {
        if (btReceiverManager == null) {
            btReceiverManager = new BluetoothReceiverManager();
        }
        return btReceiverManager;
    }

    public Map<String, BluetoothDevice> getHfpDeviceMap() {
        return mHfpDeviceMap;
    }

    public void setDeviceData(boolean isClear, BluetoothDevice device) {
        if (isClear) {
            mHfpDeviceMap.clear();
        }
        mHfpDeviceMap.put(device.getAddress(), device);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KLog.i(TAG, "action = " + action);

        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {         //??????
            String nihao = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            KLog.i(TAG, "incoming = " + nihao);
        }

        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {                   //??????
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            KLog.i(TAG, "call OUT:" + phoneNumber);
        }


        //????????????????????????
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (btState) {
                case BluetoothAdapter.STATE_OFF:
                    mHfpDeviceMap.clear();
                    PhoneStateManager.getInstance(mContext).setMacAddress(null);
                    break;
                case BluetoothAdapter.STATE_ON:
                    KLog.i(TAG, mContext.getString(R.string.bluetooth_state_on));
                    BluetoothAdapterManager.getInstance().connectProfile();
                    break;
                default:
                    break;
            }
        }
        //??????????????????????????????
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

            BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            if (state == BluetoothDevice.BOND_BONDED) {

            }
            if (state == BluetoothDevice.BOND_NONE) {

            }

        } else if (action.equals("android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED")) {
            KLog.i(TAG, mContext.getString(R.string.pbap_state_changed));
        }


        //HFP??????????????????
        else if (action.equals(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED)) {
            int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            KLog.i(TAG, "extra_state = " + String.valueOf(extra_state));

            /**
             * ????????????HFP??????????????????
             * ???????????????HFP????????????????????????????????????????????????
             */
            if (extra_state == BluetoothProfile.STATE_CONNECTED) {
               BluetoothAdapterManager.getInstance().connectProfile();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String macAddress = device.getAddress();
                PhoneStateManager.getInstance(mContext).setMacAddress(macAddress);
                mHfpDeviceMap.clear();
                mHfpDeviceMap.put(macAddress, device);
                KLog.i(TAG, mContext.getString(R.string.hfp_connect_success));
            }
            if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String macAddress = device.getAddress();
                mHfpDeviceMap.remove(macAddress);
                String currentMacAddress = BluetoothPhoneDbManager.getInstance().getMacAddress();
                if (!TextUtils.isEmpty(currentMacAddress) && currentMacAddress.equals(macAddress)) {
                    PhoneStateManager.getInstance(mContext).setMacAddress(null);
                }
            }
        }

        //HFP?????? AG???Call??????
        else if (action.equals(BluetoothHeadsetClient.ACTION_CALL_CHANGED)) {
            Bundle bundle = intent.getExtras();
            Object obj = bundle.get(BluetoothHeadsetClient.EXTRA_CALL);
            if (obj != null) {
                BluetoothHeadsetClientCall btHfpCall = (BluetoothHeadsetClientCall) obj;
                BluetoothDevice remoteDevice = btHfpCall.getDevice();
                long elapsedTime = btHfpCall.getCreationElapsedMilli(); //?????? ???????????????boot?????????????????????????????????????????????
                int phoneState = btHfpCall.getState();
                String phoneNum = btHfpCall.getNumber();

                int state;
                long callStartTime = 0;
                switch (phoneState) {
                    case BluetoothHeadsetClientCall.CALL_STATE_ACTIVE://???????????? ????????????
                        PhoneStateManager.getInstance(mContext).getPhoneStates().beginTimeKeeping(elapsedTime, phoneNum);
                        state = State.ACTIVE.getValue();
                        callStartTime = elapsedTime;
                        Log.d(TAG, "CALL_STATE_ACTIVE   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_DIALING://?????? ????????????
                        sendCloseNaviBarBCast();
                        state = State.CALL.getValue();
                        callStartTime = elapsedTime;

                        Log.d(TAG, "CALL_STATE_DIALING   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_ALERTING://?????? ????????????
                        sendCloseNaviBarBCast();
                        state = State.CALL.getValue();
                        callStartTime = elapsedTime;
                        Log.d(TAG, "CALL_STATE_ALERTING   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_INCOMING://??????????????????
                        sendCloseNaviBarBCast();
                        state = State.INCOMING.getValue();
                        callStartTime = elapsedTime;
                        sendTurnOnScreenBroadCast();
                        Log.d(TAG, "CALL_STATE_INCOMING   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_WAITING://?????????????????????
                        sendCloseNaviBarBCast();
                        state = State.INCOMING.getValue();
                        callStartTime = elapsedTime;
                        Log.d(TAG, "CALL_STATE_WAITING   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_TERMINATED://?????? ????????????????????? ?????????????????? IDLE
                    default:
                        state = State.IDLE.getValue();
                        Log.d(TAG, "CALL_STATE_TERMINATED   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_HELD://???????????????
                        state = State.KEEP.getValue();
                        Log.d(TAG, "CALL_STATE_HELD   " + btHfpCall.getNumber());
                        break;

                    case BluetoothHeadsetClientCall.CALL_STATE_HELD_BY_RESPONSE_AND_HOLD:
                        //TODO
                        state = State.KEEP.getValue();
                        Log.d(TAG, "CALL_STATE_HELD_BY_RESPONSE_AND_HOLD   " + btHfpCall.getNumber());
                        break;
                }

                int deviceId = btHfpCall.getId();
                boolean isMulti = btHfpCall.isMultiParty();
                boolean isOut = btHfpCall.isOutgoing();

                KLog.i(TAG, "device = " + remoteDevice + "  deviceId = " + deviceId + "  phoneState = " +
                        phoneState + "  phoneNum = " + phoneNum + "  isMultiParty = " +
                        isMulti + "  isOutgoing = " + isOut + "   elapsedTime = " + elapsedTime);

                ContactBean bean = new ContactBean();
                bean.setPhoneNum(phoneNum);

                boolean isKeepState = false;
                BluePhoneState phoneStates = PhoneStateManager.getInstance(context).getPhoneStates();
                int index = phoneStates.getIndex(phoneNum);
                if (index != -1) {
                    int[] states = phoneStates.getStates();
                    if (states[index] == State.KEEP.getValue()) {
                        Log.d(TAG, "isKeepState: "+phoneNum);
                        isKeepState = true;
                    }
                }

                if (callStartTime != 0 && !isKeepState) {
                    bean.setCallStartTime(callStartTime);
                } else {
                    bean.setElapsedTime(elapsedTime);
                }
                ScreenControlUtil.sendTurnOnScreenBroadCast(mContext);
                PhoneStateManager.getInstance(context).dispatchState(state, bean);
            }
        }

        //HFP?????? ??????????????????????????????
        if (action.equals(BluetoothHeadsetClient.ACTION_AUDIO_STATE_CHANGED)) {
            //            extra_state??????
            //                    * extra_state = 0  audio disconnected    ??????????????????
            //                    * extra_state = 2  audio connected       ??????????????????
            int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            KLog.i(TAG, "extra_state = " + String.valueOf(state));
            EventBus.getDefault().post(state == BluetoothHeadsetClient.STATE_AUDIO_CONNECTED, EventBusTags.AUDIO_STATE_CHANGED);
        }

        //HFP?????? HFP/AG???EVENT????????????
        if (action.equals(BluetoothHeadsetClient.ACTION_AG_EVENT)) {

        }
    }

    private void sendCloseNaviBarBCast() {
        Intent intent = new Intent();
        intent.setAction(ConfigConstants.NAVIBARWINDOW_CLOSE_ACTION);
        mContext.sendBroadcast(intent);
    }

    private void sendTurnOnScreenBroadCast() {
        Intent intent = new Intent();
        intent.setAction(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION);
        mContext.sendBroadcast(intent);
    }
}
