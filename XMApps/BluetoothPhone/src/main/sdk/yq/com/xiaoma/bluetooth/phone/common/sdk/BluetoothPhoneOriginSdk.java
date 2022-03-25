package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/5/20
 * @Desc: 纯安卓原生接口
 */
public class BluetoothPhoneOriginSdk implements IBlueToothPhoneManager {
    private static final String TAG = "BluetoothPhoneOriginSdk";
    public static BluetoothPhoneOriginSdk bluetoothPhoneOriginSdk;
    private final Context context;
    private final BluetoothAdapterManager btAdapterManager;

    private BluetoothPhoneOriginSdk() {
        btAdapterManager = BluetoothAdapterManager.getInstance();
        context = BlueToothPhone.getContext();
        registerReceiver();
    }

    public static IBlueToothPhoneManager getInstance() {
        if (bluetoothPhoneOriginSdk == null) {
            bluetoothPhoneOriginSdk = new BluetoothPhoneOriginSdk();
        }
        return bluetoothPhoneOriginSdk;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED");
        filter.addAction("android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
        context.registerReceiver(BluetoothReceiverManager.getInstance(), filter);
    }

    @Override
    public boolean dial(String phoneNum) {
        Map<String, BluetoothDevice> hfpDeviceMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = BluetoothReceiverManager.getInstance().getHfpDeviceMap().keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "dial success");
                String key = iter.next();
                btAdapterManager.getBtHeadsetClient().dial(hfpDeviceMap.get(key), phoneNum);
                return true;
            }
        }
        KLog.d(TAG, "dial fail");
        return false;
    }

    @Override
    public boolean sendDTMF(Character digit) {
        Map<String, BluetoothDevice> hfpMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = hfpMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "sendDTMF success");
                char c = digit;
                byte code = (byte) c;
                String key = iter.next();
                btAdapterManager.getBtHeadsetClient().sendDTMF(hfpMap.get(key), code);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean acceptCall() {
        Map<String, BluetoothDevice> hfpMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = hfpMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "acceptCall success");
                String key = iter.next();
                BluePhoneState states = PhoneStateManager.getInstance(context).getPhoneStates();
                State state = states.getState(1);
                if (state != null && state != State.IDLE) {
                    btAdapterManager.getBtHeadsetClient().acceptCall(hfpMap.get(key), BluetoothHeadsetClient.CALL_ACCEPT_HOLD);
                } else {
                    btAdapterManager.getBtHeadsetClient().acceptCall(hfpMap.get(key), BluetoothHeadsetClient.CALL_ACCEPT_NONE);
                }
                return true;
            }
        }
        KLog.d(TAG, "acceptCall fail");
        return false;
    }

    @Override
    public boolean rejectCall() {
        Map<String, BluetoothDevice> hfpMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = hfpMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "rejectCall success");
                String key = iter.next();
                btAdapterManager.getBtHeadsetClient().rejectCall(hfpMap.get(key));
                return true;
            }
        }
        KLog.d(TAG, "rejectCall fail");
        return false;
    }

    @Override
    public boolean terminateCall() {
        Map<String, BluetoothDevice> hfpMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = hfpMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "terminateCall success");
                String key = iter.next();
                btAdapterManager.getBtHeadsetClient().terminateCall(hfpMap.get(key), null);
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
    public boolean holdCall() {
        Map<String, BluetoothDevice> mHfpDeviceMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "holdCall success");
                String key = iter.next();
                btAdapterManager.getBtHeadsetClient().holdCall(mHfpDeviceMap.get(key));
                return true;
            }
        }
        KLog.d(TAG, "holdCall fail");
        return false;
    }

    @Override
    public boolean connectAudio() {
        Map<String, BluetoothDevice> mHfpDeviceMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "connectAudio success");
                String key = iter.next();
                return btAdapterManager.getBtHeadsetClient().connectAudio(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "connectAudio fail");
        return false;
    }

    @Override
    public boolean disconnectAudio() {
        Map<String, BluetoothDevice> mHfpDeviceMap = getHfpMap();
        if (shouldOperation()) {
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "disconnectAudio success");
                String key = iter.next();
                return btAdapterManager.getBtHeadsetClient().disconnectAudio(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "disconnectAudio fail");
        return false;
    }

    @Override
    public List<ContactBean> getAllContact() {
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
    public String getDialBackNumber() {
        KLog.d(TAG, "getDialBackNumber");
        return getFirstNumByType(BluetoothPhoneConstants.INCOMING_CALL);
    }

    @Override
    public String getRedialNumber() {
        KLog.d(TAG, "getRedialNumber");
        return getFirstNumByType(BluetoothPhoneConstants.OUTGOING_CALL);
    }

    @Override
    public int getAudioState() {
        if (shouldOperation()) {
            Map<String, BluetoothDevice> mHfpDeviceMap = getHfpMap();
            Iterator<String> iter = mHfpDeviceMap.keySet().iterator();
            while (iter.hasNext()) {
                KLog.d(TAG, "getAudioState success");
                String key = iter.next();
                return btAdapterManager.getBtHeadsetClient().getAudioState(mHfpDeviceMap.get(key));
            }
        }
        KLog.d(TAG, "getAudioState fail");
        return BluetoothHeadsetClient.STATE_AUDIO_DISCONNECTED;
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(BluetoothReceiverManager.getInstance());
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
        Map<String, BluetoothDevice> hfpDeviceMap = BluetoothReceiverManager.getInstance().getHfpDeviceMap();
        return BluetoothAdapterManager.getInstance().getBluetoothStateWithoutTTS() && btAdapterManager.getBtHeadsetClient() != null && !hfpDeviceMap.isEmpty();
    }

    public boolean mutePhone() {
        // TODO 注意，原生的这种实现目前在新协议栈中是无效的；
        AudioManager  audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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

    private boolean isBtStateOk() {
        return BluetoothAdapterManager.getInstance().isBtStateOk();
    }

    private boolean shouldOperation() {
        Map<String, BluetoothDevice> hfpDeviceMap = BluetoothReceiverManager.getInstance().getHfpDeviceMap();
        return isBtStateOk() && btAdapterManager.getBtHeadsetClient() != null && !hfpDeviceMap.isEmpty();
    }

    private Map<String, BluetoothDevice> getHfpMap() {
        return BluetoothReceiverManager.getInstance().getHfpDeviceMap();
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
}
