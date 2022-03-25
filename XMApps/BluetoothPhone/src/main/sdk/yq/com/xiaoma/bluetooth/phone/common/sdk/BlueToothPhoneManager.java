package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHeadsetClientCall;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothPhoneDbManager;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.screentool.ScreenControlUtil;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by qiuboxiang on 2018/12/11
 */
public class BlueToothPhoneManager implements IBlueToothPhoneManager {

    public static final String TAG = "BlueToothPhoneManager";
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothHeadsetClient mBTHeadSetClient = null;
    private Map<String, BluetoothDevice> mHfpDeviceMap = new HashMap<>();
    private BluetoothProfile.ServiceListener mHfpPbaClientProfileService = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            KLog.i(TAG, "onServiceConnected profile:" + profile);
            if (profile == BluetoothProfile.HEADSET_CLIENT) {

                //蓝牙开启且支持该协议，则获取非null协议代理对象
                mBTHeadSetClient = (BluetoothHeadsetClient) proxy;
                if (mBTHeadSetClient != null) {
                    KLog.i(TAG, "BluetoothHeadsetClient agent is not null");
                    /**
                     * 获取已连接HFP远程蓝牙设备列表
                     * 适应场景：蓝牙已开启，HFP已连接成功，之后启动APP
                     */
                    List<BluetoothDevice> deviceList = mBTHeadSetClient.getConnectedDevices();
                    if (!deviceList.isEmpty()) {
                        BluetoothDevice device = deviceList.get(0);
                        String macAddress = device.getAddress();
                        PhoneStateManager.getInstance(mContext).setMacAddress(macAddress);
                        mHfpDeviceMap.clear();
                        mHfpDeviceMap.put(macAddress, device);
                    }
                } else {
                    KLog.i(TAG, "do not support HFP profile");
                }
            }
        }

        public void onServiceDisconnected(int profile) {
            //相关协议断开，自动会回调该方法，置null
            KLog.i(TAG, "onServiceDisconnected profile:" + profile);
            if (profile == BluetoothProfile.HEADSET_CLIENT) {
                mBTHeadSetClient = null;
            }
        }
    };
    private BroadcastReceiver mBTPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KLog.i(TAG, "action = " + action);

            if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {         //来电
                String nihao = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                KLog.i(TAG, "incoming = " + nihao);
            }

            if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {                   //去电
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                KLog.i(TAG, "call OUT:" + phoneNumber);
            }


            //蓝牙开启关闭广播
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (btState) {
                    case BluetoothAdapter.STATE_OFF:
                        mHfpDeviceMap.clear();
                        PhoneStateManager.getInstance(mContext).setMacAddress(null);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        KLog.i(TAG, mContext.getString(R.string.bluetooth_state_on));
                        if (mBluetoothAdapter != null) {
                            mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.HEADSET_CLIENT);
                            mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.PBAP_CLIENT);
                        }
                        break;
                    default:
                        break;
                }
            }
            //蓝牙配对状态改变广播
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


            //HFP协议连接状态
            else if (action.equals(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                KLog.i(TAG, "extra_state = " + String.valueOf(extra_state));

                /**
                 * 动态获取HFP远程蓝牙设备
                 * 适应场景：HFP协议连接建立和连接断开触发的广播
                 */
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    KLog.d("1111", mBluetoothAdapter + "");

                    if (mBTHeadSetClient == null) {
                        if (mBluetoothAdapter != null) {
                            mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.HEADSET_CLIENT);
                            mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.PBAP_CLIENT);
                        }
                    }

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

            //HFP协议 AG侧Call状态
            else if (action.equals(BluetoothHeadsetClient.ACTION_CALL_CHANGED)) {
                Bundle bundle = intent.getExtras();
                Object obj = bundle.get(BluetoothHeadsetClient.EXTRA_CALL);
                if (obj != null) {
                    BluetoothHeadsetClientCall btHfpCall = (BluetoothHeadsetClientCall) obj;
                    BluetoothDevice remoteDevice = btHfpCall.getDevice();
                    long elapsedTime = btHfpCall.getCreationElapsedMilli(); //毫秒 返回自设备boot启动后的毫秒数，包括睡眠时间。
                    int phoneState = btHfpCall.getState();
                    String phoneNum = btHfpCall.getNumber();

                    int state;
                    long callStartTime = 0;
                    switch (phoneState) {
                        case BluetoothHeadsetClientCall.CALL_STATE_ACTIVE://主叫被叫 接听状态
                            PhoneStateManager.getInstance(mContext).getPhoneStates().beginTimeKeeping(elapsedTime, phoneNum);
                            state = State.ACTIVE.getValue();
                            callStartTime = elapsedTime;
                            KLog.d(TAG, "CALL_STATE_ACTIVE   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_DIALING://主叫 拨号状态
                            state = State.CALL.getValue();
                            callStartTime = elapsedTime;
                            KLog.d(TAG, "CALL_STATE_DIALING   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_ALERTING://主叫 响铃状态
                            state = State.CALL.getValue();
                            callStartTime = elapsedTime;
                            KLog.d(TAG, "CALL_STATE_ALERTING   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_INCOMING://被叫响铃状态
                            state = State.INCOMING.getValue();
                            callStartTime = elapsedTime;
                            ScreenControlUtil.sendTurnOnScreenBroadCast(context);
                            KLog.d(TAG, "CALL_STATE_INCOMING   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_WAITING://第三方来电等待
                            state = State.INCOMING.getValue();
                            callStartTime = elapsedTime;
                            KLog.d(TAG, "CALL_STATE_WAITING   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_TERMINATED://主叫 终止或中止状态 被叫中止状态 IDLE
                        default:
                            state = State.IDLE.getValue();
                            KLog.d(TAG, "CALL_STATE_TERMINATED   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_HELD://别人被挂起
                            state = State.KEEP.getValue();
                            KLog.d(TAG, "CALL_STATE_HELD   " + btHfpCall.getNumber());
                            break;

                        case BluetoothHeadsetClientCall.CALL_STATE_HELD_BY_RESPONSE_AND_HOLD:
                            //TODO
                            state = State.KEEP.getValue();
                            KLog.d(TAG, "CALL_STATE_HELD_BY_RESPONSE_AND_HOLD   " + btHfpCall.getNumber());
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
                    PhoneStateManager.getInstance(context).dispatchState(state, bean);
                }
            }

            //HFP协议 音频流接通和断开广播
            if (action.equals(BluetoothHeadsetClient.ACTION_AUDIO_STATE_CHANGED)) {
                //            extra_state解释
                //                    * extra_state = 0  audio disconnected    音频流已断开
                //                    * extra_state = 2  audio connected       音频流已连接
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                KLog.i(TAG, "extra_state = " + String.valueOf(state));
                EventBus.getDefault().post(state == BluetoothHeadsetClient.STATE_AUDIO_CONNECTED, EventBusTags.AUDIO_STATE_CHANGED);
            }

            //HFP协议 HFP/AG侧EVENT事件广播
            if (action.equals(BluetoothHeadsetClient.ACTION_AG_EVENT)) {

            }
        }
    };

    public BlueToothPhoneManager() {
        mContext = BlueToothPhone.getContext();
        registerReceiver();
    }

    public void registerReceiver() {
        Log.d(TAG, "registerReceiver: ");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED");
        filter.addAction("android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
        mContext.registerReceiver(mBTPhoneReceiver, filter);

        if (mBTHeadSetClient == null) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.HEADSET_CLIENT);
                mBluetoothAdapter.getProfileProxy(mContext, mHfpPbaClientProfileService, BluetoothProfile.PBAP_CLIENT);
            }
        }
    }

    @Override
    public boolean dial(String phoneNum) {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "dial success");
                String key = iter.next();
                mBTHeadSetClient.dial(mHfpDeviceMap.get(key), phoneNum);
                return true;
            }
        }
        KLog.d(TAG, "dial fail");
        return false;
    }

    @Override
    public boolean sendDTMF(Character digit) {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "sendDTMF success");
                char c = digit;
                byte code = (byte) c;
                String key = iter.next();
                mBTHeadSetClient.sendDTMF(mHfpDeviceMap.get(key), code);
                return true;
            }
        }
        KLog.d(TAG, "sendDTMF fail");
        return false;
    }

    @Override
    public boolean holdCall() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "holdCall success");
                String key = iter.next();
                mBTHeadSetClient.holdCall(mHfpDeviceMap.get(key));
                return true;
            }
        }
        KLog.d(TAG, "holdCall fail");
        return false;
    }

    @Override
    public boolean acceptCall() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "acceptCall success");
                String key = iter.next();
                BluePhoneState states = PhoneStateManager.getInstance(mContext).getPhoneStates();
                State state = states.getState(1);
                if (state != null && state != State.IDLE) {
                    mBTHeadSetClient.acceptCall(mHfpDeviceMap.get(key), BluetoothHeadsetClient.CALL_ACCEPT_HOLD);
                } else {
                    mBTHeadSetClient.acceptCall(mHfpDeviceMap.get(key), BluetoothHeadsetClient.CALL_ACCEPT_NONE);
                }
                return true;
            }
        }
        KLog.d(TAG, "acceptCall fail");
        return false;
    }

    @Override
    public boolean rejectCall() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "rejectCall success");
                String key = iter.next();
                mBTHeadSetClient.rejectCall(mHfpDeviceMap.get(key));
                return true;
            }
        }
        KLog.d(TAG, "rejectCall fail");
        return false;
    }

    @Override
    public boolean terminateCall() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "terminateCall success");
                String key = iter.next();
                mBTHeadSetClient.terminateCall(mHfpDeviceMap.get(key), null);
                return true;
            }
        }
        KLog.d(TAG, "terminateCall fail");
        return false;
    }

    @Override
    public void answerCallByNForeApi(int operate) {

    }

    @Override
    public int getAudioState() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "getAudioState success");
                String key = iter.next();
                return mBTHeadSetClient.getAudioState(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "getAudioState fail");
        return BluetoothHeadsetClient.STATE_AUDIO_DISCONNECTED;
    }

    @Override
    public boolean connectAudio() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "connectAudio success");
                String key = iter.next();
                return mBTHeadSetClient.connectAudio(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "connectAudio fail");
        return false;
    }

    @Override
    public boolean disconnectAudio() {
        if (isBtStateOk() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "disconnectAudio success");
                String key = iter.next();
                return mBTHeadSetClient.disconnectAudio(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "disconnectAudio fail");
        return false;
    }

    @Override
    public List<ContactBean> getAllContact() {
        KLog.d(TAG, "getAllContact");
        List<ContactBean> allContact = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getAllContact();
        return allContact != null ? allContact : new ArrayList<ContactBean>();
    }

    @Override
    public boolean dialBack() {
        KLog.d(TAG, "dialBack");
        String num = getDialBackNumber();
        if (TextUtils.isEmpty(num)) {
            XMToast.showToast(BlueToothPhone.getContext(), R.string.find_no_first_unanswer_call);
            return false;
        }
        return dial(num);
    }

    @Override
    public String getDialBackNumber() {
        KLog.d(TAG, "getDialBackNumber");
        return getFirstNumByType(BluetoothPhoneConstants.INCOMING_CALL);
    }

    @Override
    public boolean redial() {
        KLog.d(TAG, "redial");
        String num = getRedialNumber();
        if (TextUtils.isEmpty(num)) {
            XMToast.showToast(BlueToothPhone.getContext(), R.string.find_no_first_call_out_record);
            return false;
        }
        return dial(num);
    }

    @Override
    public String getRedialNumber() {
        KLog.d(TAG, "getRedialNumber");
        return getFirstNumByType(BluetoothPhoneConstants.OUTGOING_CALL);
    }

    private String getFirstNumByType(int type) {
        List<ContactBean> callHistory = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getCallHistory();
        if (callHistory == null || callHistory.size() == 0) return null;
        List<ContactBean> temp = new ArrayList<>();
        for (ContactBean contactBean : callHistory) {
            if (contactBean.getCallType() == type) {
                temp.add(contactBean);
            }
        }
        if (temp.size() == 0) return null;
        if (temp.size() == 1) return temp.get(0).getPhoneNum();
        Collections.sort(temp, new Comparator<ContactBean>() {
            @Override
            public int compare(ContactBean o1, ContactBean o2) {
                return (int) (Long.parseLong(o2.getTimeStamp()) - Long.parseLong(o1.getTimeStamp()));
            }
        });
        return temp.get(0).getPhoneNum();
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        if (mBTPhoneReceiver != null) {
            mContext.unregisterReceiver(mBTPhoneReceiver);
        }
    }

    @Override
    public boolean isContactBookSynchronized() {
        return PhoneStateManager.getInstance(BlueToothPhone.getContext()).isPullPhoneBookSuccess();
    }

    @Override
    public void synchronizeContactBook(final PullPhoneBookResultCallback callback) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                PhoneStateManager.getInstance(BlueToothPhone.getContext()).pullPhoneBook(callback);
            }
        });
    }

    @Override
    public List<ContactBean> getCallHistory() {
        List<ContactBean> callHistory = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getCallHistory();
        return callHistory;
    }

    @Override
    public boolean isBluetoothConnected() {
        return BluetoothAdapterManager.getInstance().getBluetoothStateWithoutTTS() && mBTHeadSetClient != null && !mHfpDeviceMap.isEmpty();
    }


    public boolean mutePhone() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        boolean mute;
        if (!audioManager.isMicrophoneMute()) {
            //静音
            mute = true;
        } else {
            //取消静音
            mute = false;
        }
        if (audioManager != null) {
            audioManager.setMicrophoneMute(mute);
        }
        return mute;
    }

    @Override
    public int missCallNum() {
        return 0;
    }

    @Override
    public void downloadByType(BlueToothPhoneManagerFactory.PhoneType phoneType) {

    }

    @Override
    public void downloadAll() {

    }

    @Override
    public void setPullResultCallback(PullContactbookCallback callback) {

    }

    @Override
    public void pauseHfpRender() {

    }

    @Override
    public void startHfpRender() {

    }

    @Override
    public void stopDownload(String macAddress) {

    }

    @Override
    public boolean isDeviceDisconnected(BluetoothDevice device) {
        return false;
    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        return false;
    }

    @Override
    public void cleanTask() {

    }

    @Override
    public boolean isHfpDisconnected() {
        return false;
    }

    public boolean isBtStateOk() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Context mContext = BlueToothPhone.getContext();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT) == BluetoothProfile.STATE_CONNECTED) {
                    return true;
                } else {
                    BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
                    if (connectedDevice != null) {
                        hfpDisConnected();
                    } else {
                        bluetoothDisConnected();
                    }
                    return false;
                }

            } else {
                bluetoothDisabled();
                return false;
            }

        } else {
            bluetoothNonExistent();
            return false;
        }
    }

    public void bluetoothNonExistent() {
        XMToast.showToast(mContext, mContext.getResources().getString(R.string.bluetooth_not_exist));
    }

    public void bluetoothDisabled() {
        XMToast.showToast(mContext, mContext.getResources().getString(R.string.bluetooth_closed));
    }

    public void bluetoothDisConnected() {
        XMToast.showToast(mContext, mContext.getResources().getString(R.string.bluetooth_disconnected));
    }

    public void hfpDisConnected() {
        XMToast.showToast(mContext, mContext.getResources().getString(R.string.hfp_disconnected));
    }

}