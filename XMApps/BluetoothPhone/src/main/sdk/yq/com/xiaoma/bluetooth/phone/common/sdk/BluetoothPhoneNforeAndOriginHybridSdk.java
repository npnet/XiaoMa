package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.nforetek.bt.aidl.INfCallbackPbap;
import com.nforetek.bt.aidl.INfCommandHfp;
import com.nforetek.bt.aidl.INfCommandPbap;
import com.nforetek.bt.aidl.NfPbapContact;
import com.nforetek.bt.res.NfDef;
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
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.common.utils.PinyinUtils;
import com.xiaoma.bluetooth.phone.common.utils.StringFormatUtils;
import com.xiaoma.bluetooth.phone.contacts.model.PinyinComparator;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/5/20
 * @Desc: 安富和安卓原生接口混用
 */
public class BluetoothPhoneNforeAndOriginHybridSdk implements IBlueToothPhoneManager {

    private static final String TAG = "BluetoothPhoneNforeAndOriginHybridSdk";
    public static BluetoothPhoneNforeAndOriginHybridSdk btHybridSdk;
    private final Context context;
    private final BluetoothAdapterManager btAdapterManager;
    public List<ContactBean> contactBeans = new ArrayList<>();
    public List<ContactBean> missCalls = new ArrayList<>();
    public List<ContactBean> receiveCalls = new ArrayList<>();
    public List<ContactBean> dialCalls = new ArrayList<>();
    public List<ContactBean> callLogs = new ArrayList<>();
    public List<ContactBean> allCallHistory = new ArrayList<>();
    public List<NfPbapContact> nfPbapContacts = new ArrayList<>();
    public boolean isImmediateNotify = false;
    private INfCommandHfp iNfCommandHfp;
    private boolean taskIsFree = true;
    private BlueToothPhoneManagerFactory.PhoneType curDownloadType = null;
    private INfCommandPbap iNfCommandPbap;
    private int pull_phone_param = NfDef.PBAP_PROPERTY_MASK_FN |
            NfDef.PBAP_PROPERTY_MASK_N |
            NfDef.PBAP_PROPERTY_MASK_TEL |
            NfDef.PBAP_PROPERTY_MASK_VERSION |
            NfDef.PBAP_PROPERTY_MASK_PHOTO;
    private Queue<Integer> tasKQueue = new ArrayDeque<>();
    private PullContactbookCallback callback;
    private boolean isPbapReady = false;
    private boolean isSimContact = false;
    // 通话历史最后一个进行下载的项目
    private BlueToothPhoneManagerFactory.PhoneType lastType;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KLog.d("hzx", "INfCommandHfp 连接成功");
            if (name.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_HFP))) {
                iNfCommandHfp = INfCommandHfp.Stub.asInterface(service);
            } else if (name.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_PBAP))) {
                KLog.d("hzx", "INfCommandPbap 连接成功");
                iNfCommandPbap = INfCommandPbap.Stub.asInterface(service);
                try {
                    iNfCommandPbap.registerPbapCallback(iNfCallbackPbap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_HFP))) {
                iNfCommandHfp = null;
            } else if (name.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_PBAP))) {
                try {
                    iNfCommandPbap.unregisterPbapCallback(iNfCallbackPbap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                iNfCommandPbap = null;
                isSimContact = false;
            }
        }
    };
    private String lastBtAddress;
    private INfCallbackPbap.Stub iNfCallbackPbap = new INfCallbackPbap.Stub() {
        @Override
        public void onPbapServiceReady() throws RemoteException {
            KLog.d("hzx", "onPbapServiceReady");
            isPbapReady = true;
            if (taskIsFree) {
                runTask();
            }
        }

        @Override
        public void onPbapStateChanged(String s, int i, int i1, int i2, int i3) throws RemoteException {
            Log.d("hzx", "address: " + s + ", preState: " + i + ", curState: " + i1 + ", reason: " + i2 + ", count: " + i3);
            lastBtAddress = s;
            if (i2 == NfDef.REASON_DOWNLOAD_FAILED) {
                if (callback != null) callback.onPullFailed();
                return;
            } else if (i2 == NfDef.REASON_DOWNLOAD_TIMEOUT) {
                if (callback != null) callback.onTimeOut();
                return;
            } else if (i2 == NfDef.REASON_DOWNLOAD_USER_REJECT) {
                if (callback != null) callback.onUnauthoried();
                return;
            }
            if (i == NfDef.STATE_DOWNLOADING && i1 == NfDef.STATE_READY && i2 == NfDef.REASON_DOWNLOAD_FULL_CONTENT_COMPLETED) {
                KLog.d("hzx", "下载完成: " + curDownloadType);
                taskIsFree = true;
                notifyDataChange();
                runTask();
            } else if ((i == NfDef.STATE_DOWNLOADING && i1 == NfDef.STATE_DOWNLOADING) ||
                    (i1 == NfDef.STATE_READY && i2 == NfDef.STATE_DOWNLOADING)) {
                KLog.d("hzx", "下载中....");
                taskIsFree = false;
            } else if (i == NfDef.STATE_NOT_INITIALIZED || i1 == NfDef.STATE_READY) {
                KLog.d("hzx", "PBAP NOT INITIALIZED");
                if (callback != null) callback.unInitialized();
                taskIsFree = true;
                runTask();
            }
        }

        @Override
        public void retPbapDownloadedContact(NfPbapContact nfPbapContact) throws RemoteException {
            String numString = "";
            if (nfPbapContact.getNumberArray().length > 0) {
                for (String num : nfPbapContact.getNumberArray()) {
                    numString += "," + num;
                }
            }
//            KLog.d("hzx", "nfPhapContact: 名字: " + nfPbapContact.getFirstName() + nfPbapContact.getMiddleName() + nfPbapContact.getLastName() + ", 号码: " + numString + " ,类型: " + nfPbapContact.getStorageType());
            nfPbapContacts.add(nfPbapContact);
        }

        @Override
        public void retPbapDownloadedCallLog(String s, String s1, String s2, String s3, String s4, int i, String s5) throws RemoteException {
//            KLog.d("hzx", "retPbapDownloadedCallLog: " + s + ", " + s1 + ", " + s2 + "," + s3 + ", " + s4 + ", " + ", " + i + ", " + s5);
            if (!TextUtils.equals(s, lastBtAddress)) {
                cleanList();
            }
            ContactBean contactBean = convertToContactBean(s1, s2, s3, s4, i, s5);
            addBeanByType(contactBean);
        }

        @Override
        public void onPbapDownloadNotify(String s, int i, int i1, int i2) throws RemoteException {
//            KLog.d("hzx", "onPbapDownloadNotify: " + s + ", " + i + ", " + i1 + ", " + i2);
        }

        @Override
        public void retPbapDatabaseQueryNameByNumber(String s, String s1, String s2, boolean b) throws RemoteException {

        }

        @Override
        public void retPbapDatabaseQueryNameByPartialNumber(String s, String s1, String[] strings, String[] strings1, boolean b) throws RemoteException {

        }

        @Override
        public void retPbapDatabaseAvailable(String s) throws RemoteException {

        }

        @Override
        public void retPbapDeleteDatabaseByAddressCompleted(String s, boolean b) throws RemoteException {

        }

        @Override
        public void retPbapCleanDatabaseCompleted(boolean b) throws RemoteException {

        }
    };

    private BluetoothPhoneNforeAndOriginHybridSdk() {
        context = BlueToothPhone.getContext();
        btAdapterManager = BluetoothAdapterManager.getInstance();
        initInfBluetooth();
        initInfPbap();
        registerReceiver();
    }

    public static IBlueToothPhoneManager getInstance() {
        if (btHybridSdk == null) {
            btHybridSdk = new BluetoothPhoneNforeAndOriginHybridSdk();
        }
        return btHybridSdk;
    }

    private void addBeanByType(ContactBean contactBean) {
        if (contactBean == null) return;
        switch (curDownloadType) {
            case DIAL:
                dialCalls.add(contactBean);
                break;
            case MISS:
                missCalls.add(contactBean);
                break;
            case RECEIVER:
                receiveCalls.add(contactBean);
                break;
            case Logs:
                callLogs.add(contactBean);
                break;
        }
    }

    private void notifyDataChange() {
        if (callback == null) return;
        switch (curDownloadType) {
            case DIAL:
                callback.onPullDialedCallLog(dialCalls);
                break;
            case CONTACT:
                if (isSimContact) {
                    convertToContactBean(nfPbapContacts);
                    isSimContact = false;
                }
                break;
            case MISS:
                callback.onPullMissCallLog(missCalls);
                break;
            case RECEIVER:
                callback.onPullReceivedCallLog(receiveCalls);
                break;
            case Logs:
                callback.onPullCalllog(callLogs);
        }
        KLog.d("hzx", "curDownloadType: " + curDownloadType + ", lastType: " + lastType);
        if (curDownloadType == lastType) {
            KLog.d("hzx", "--------finish pull------: " + System.currentTimeMillis());
            try {
                notifyCallHistoryDataChange();
            } catch (Exception e) {
                KLog.w(TAG, e.getMessage());
            }
        }
    }

    private void notifyCallHistoryDataChange() {
        allCallHistory.clear();
        allCallHistory.addAll(dialCalls);
        allCallHistory.addAll(missCalls);
        allCallHistory.addAll(receiveCalls);
        allCallHistory.addAll(callLogs);
        sortByTime(allCallHistory);
        if (allCallHistory.size() > 20) {
            allCallHistory = allCallHistory.subList(0, 20);
        }
        callback.onPullCallHistory(allCallHistory);
        if (allCallHistory.isEmpty())
            return;
        BluetoothPhoneClient.getInstance().notifySyncSuccess(allCallHistory);
    }

    private void convertToContactBean(final List<NfPbapContact> nfPbapContacts) {
        if (nfPbapContacts.isEmpty()) {
            contactBeans.clear();
            callback.onPullContactFinished(contactBeans);
            return;
        }
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                if (nfPbapContacts.size() != 0) {
                    for (NfPbapContact nfPbapContact : nfPbapContacts) {
                        if (nfPbapContact.getNumberArray() != null && nfPbapContact.getNumberArray().length != 0) {
                            for (int i = 0; i < nfPbapContact.getNumberArray().length; i++) {
                                String num = nfPbapContact.getNumberArray()[i];
                                ContactBean contactBean = new ContactBean();
                                String name = nfPbapContact.getLastName() + nfPbapContact.getFirstName() + nfPbapContact.getMiddleName();
                                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(name.trim())) {
                                    name = BlueToothPhone.getContext().getString(R.string.unknown_contact);
                                }
                                String id = name + num;
                                int[] phoneTypeArray = nfPbapContact.getPhoneTypeArray();
                                int phoneType = -1;
                                if (phoneTypeArray != null && phoneTypeArray.length >= i) {
                                    phoneType = phoneTypeArray[i];
                                }
                                if (TextUtils.isEmpty(num)) continue;
                                contactBean.setPhoneType(phoneType);
                                contactBean.setId(id);
                                contactBean.setName(name);
                                contactBean.setPhoneNum(num);
                                contactBean.setCallType(BluetoothPhoneConstants.CONTACT);
                                contactBean.setIcon(nfPbapContact.getPhotoByteArray());
                                String pinYin = PinyinUtils.getPinYin(name).trim();
                                // 处理零宽空格(iphone上钉钉相关的联系人)
                                String replace = pinYin.replaceAll("\u200B", "");
                                String firstPinYin = replace.substring(0, 1).toUpperCase();
                                contactBean.setPinYin(replace);
                                if (firstPinYin.matches("[A-Z]")) {
                                    contactBean.setFirstPinYin(firstPinYin);
                                } else {
                                    contactBean.setFirstPinYin("#");
                                }
                                contactBeans.add(contactBean);
                            }
                        }

                    }
                    try {
                        Collections.sort(contactBeans, new PinyinComparator());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onPullContactFinished(contactBeans);
                        }
                    });
                }
            }
        });
    }

    private void cleanList() {
        contactBeans.clear();
        missCalls.clear();
        receiveCalls.clear();
        dialCalls.clear();
    }

    private ContactBean convertToContactBean(String firstName, String middleName, String lastName, String number, int type, String timestamp) {
        int callType = -1;
        switch (type) {
            case NfDef.PBAP_STORAGE_RECEIVED_CALLS:
                callType = BluetoothPhoneConstants.INCOMING_CALL;
                break;
            case NfDef.PBAP_STORAGE_DIALED_CALLS:
                callType = BluetoothPhoneConstants.OUTGOING_CALL;
                break;
            case NfDef.PBAP_STORAGE_MISSED_CALLS:
                callType = BluetoothPhoneConstants.MISSING_CALL;
                break;
        }
        String name = lastName + firstName + middleName;
        if (TextUtils.isEmpty(name)) {
            name = BlueToothPhone.getContext().getString(R.string.unknown_contact);
        }
        String time = StringFormatUtils.getTimeFromString(timestamp);
        String date = StringFormatUtils.getDateFromString(timestamp);
        String filterTimeStamp = StringFormatUtils.getTimeStampString(timestamp);
        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(timestamp)) {
            ContactBean contactBean = new ContactBean();
            contactBean.setName(name);
            contactBean.setCallType(callType);
            contactBean.setTimeStamp(filterTimeStamp);
            contactBean.setTime(time);
            contactBean.setId(name + number);
            contactBean.setDate(date);
            contactBean.setPhoneNum(number);
            return contactBean;
        }
        return null;
    }

    private void initInfPbap() {
        Intent intent = new Intent(NfDef.CLASS_SERVICE_PBAP);
        intent.setComponent(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_PBAP));
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
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

    private void initInfBluetooth() {
        Intent intent = new Intent(NfDef.CLASS_SERVICE_HFP);
        intent.setComponent(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_HFP));
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
    public boolean sendDTMF(Character code) {
        if (iNfCommandHfp != null) {
            try {
                iNfCommandHfp.reqHfpSendDtmf(String.valueOf(code));
            } catch (RemoteException e) {
                e.printStackTrace();
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

    /**
     * @param operate 0表示接听,1表示保持并接听,2表示挂断并接听
     */
    @Override
    public void answerCallByNForeApi(int operate) {
        if (shouldOperation()) {
            if (iNfCommandHfp != null) {
                try {
                    iNfCommandHfp.reqHfpAnswerCall(operate);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
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
        try {
            boolean isAlreadyMute = iNfCommandHfp.isHfpMicMute();
            iNfCommandHfp.muteHfpMic(!isAlreadyMute);
            return !isAlreadyMute;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isBtStateOk() {
        return BluetoothAdapterManager.getInstance().isBtStateOk();
    }

    @Override
    public int missCallNum() {
        int count = 0;
        List<ContactBean> callHistory = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getCallHistory();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        if (callHistory != null && callHistory.size() != 0) {
            for (ContactBean bean : callHistory) {
                if (bean.getCallType() == BluetoothPhoneConstants.MISSING_CALL && StringFormatUtils.getDate(bean.getTimeStamp()).substring(0, 10).equals(today)) {
                    count++;
                }
            }
        }
        return count;
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

    public void downloadAll() {
        Log.d("hzx", "---------start pull---------: " + System.currentTimeMillis());
        BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice == null) {
            KLog.d("hzx", "connectDevice is null");
            return;
        }
        tasKQueue.add(NfDef.PBAP_STORAGE_PHONE_MEMORY);
        tasKQueue.add(NfDef.PBAP_STORAGE_SIM);
        tasKQueue.add(NfDef.PBAP_STORAGE_MISSED_CALLS);
        tasKQueue.add(NfDef.PBAP_STORAGE_RECEIVED_CALLS);
        tasKQueue.add(NfDef.PBAP_STORAGE_DIALED_CALLS);
        lastType = BlueToothPhoneManagerFactory.PhoneType.DIAL;
//        tasKQueue.add(NfDef.PBAP_STORAGE_CALL_LOGS);
        runTask();
    }

    /**
     * 此方法用于在通话结束后拉取指定类型的通话记录,因此会把拉取到的值和之前拉取到的其他通话类型的一起返回
     *
     * @param type
     */
    @Override
    public void downloadByType(BlueToothPhoneManagerFactory.PhoneType type) {
        switch (type) {
            case CONTACT:
                tasKQueue.add(NfDef.PBAP_STORAGE_PHONE_MEMORY);
                tasKQueue.add(NfDef.PBAP_STORAGE_SIM);
                break;
            case RECEIVER:
                tasKQueue.add(NfDef.PBAP_STORAGE_RECEIVED_CALLS);
                break;
            case MISS:
                tasKQueue.add(NfDef.PBAP_STORAGE_MISSED_CALLS);
                break;
            case Logs:
                tasKQueue.add(NfDef.PBAP_STORAGE_CALL_LOGS);
                break;
            case DIAL:
                tasKQueue.add(NfDef.PBAP_STORAGE_DIALED_CALLS);
                break;
            case History:
                tasKQueue.add(NfDef.PBAP_STORAGE_MISSED_CALLS);
                tasKQueue.add(NfDef.PBAP_STORAGE_RECEIVED_CALLS);
                tasKQueue.add(NfDef.PBAP_STORAGE_DIALED_CALLS);
                break;
        }
        if (type == BlueToothPhoneManagerFactory.PhoneType.History) {
            lastType = BlueToothPhoneManagerFactory.PhoneType.DIAL;
        } else {
            lastType = type;
        }
        runTask();
    }

    private void runTask() {
        if (taskIsFree && !tasKQueue.isEmpty() && isPbapReady) {
            int poll = tasKQueue.poll();
            curDownloadType(poll);
            try {
                BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
                if (iNfCommandPbap != null && connectedDevice != null) {
                    if (curDownloadType != BlueToothPhoneManagerFactory.PhoneType.CONTACT) {
                        iNfCommandPbap.reqPbapDownloadRange(connectedDevice.getAddress(), poll, pull_phone_param, 0, 20);
                    } else {
                        iNfCommandPbap.reqPbapDownload(connectedDevice.getAddress(), poll, pull_phone_param);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.d("hzx", "task finish");
        }
    }

    private void curDownloadType(Integer poll) {
        switch (poll) {
            case NfDef.PBAP_STORAGE_PHONE_MEMORY:
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.CONTACT;
                contactBeans.clear();
                nfPbapContacts.clear();
                break;
            case NfDef.PBAP_STORAGE_SIM:
                isSimContact = true;
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.CONTACT;
                break;
            case NfDef.PBAP_STORAGE_MISSED_CALLS:
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.MISS;
                missCalls.clear();
                break;
            case NfDef.PBAP_STORAGE_RECEIVED_CALLS:
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.RECEIVER;
                receiveCalls.clear();
                break;
            case NfDef.PBAP_STORAGE_DIALED_CALLS:
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.DIAL;
                dialCalls.clear();
                break;
            case NfDef.PBAP_STORAGE_CALL_LOGS:
                curDownloadType = BlueToothPhoneManagerFactory.PhoneType.Logs;
                callLogs.clear();
                break;
        }
    }

    private void sortByTime(List<ContactBean> list) {
        Collections.sort(list, new Comparator<ContactBean>() {
            @Override
            public int compare(ContactBean o1, ContactBean o2) {
                long time1;
                long time2;
                if (TextUtils.isEmpty(o1.getTimeStamp())) {
                    time1 = 0;
                } else {
                    time1 = Long.parseLong(o1.getTimeStamp());
                }
                if (TextUtils.isEmpty(o2.getTimeStamp())) {
                    time2 = 0;
                } else {
                    time2 = Long.parseLong(o2.getTimeStamp());
                }
                return (int) (time2 - time1);
            }
        });
    }

    @Override
    public void setPullResultCallback(PullContactbookCallback callback) {
        this.callback = callback;
    }

    @Override
    public void pauseHfpRender() {
        try {
            iNfCommandHfp.pauseHfpRender();
            Log.d(TAG, "pauseHfpRender: ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startHfpRender() {
        try {
            iNfCommandHfp.startHfpRender();
            Log.d(TAG, "startHfpRender: ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopDownload(String macAddress) {
        try {
            if (iNfCommandPbap != null) {
                iNfCommandPbap.reqPbapDownloadInterrupt(macAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        /*BluetoothA2dpSink a2DPSink = BluetoothAdapterManager.getInstance().getA2DPSink();
        BluetoothHeadsetClient btHeadsetClient = BluetoothAdapterManager.getInstance().getBtHeadsetClient();
        if (a2DPSink == null || btHeadsetClient == null) {
            return false;
        }
        List<BluetoothDevice> connectedA2dpDevices = a2DPSink.getConnectedDevices();
        List<BluetoothDevice> connectedHfpDevices = btHeadsetClient.getConnectedDevices();
        if (!connectedA2dpDevices.isEmpty() && !connectedHfpDevices.isEmpty()) {
            boolean isA2DPConnected = TextUtils.equals(connectedA2dpDevices.get(0).getAddress(),device.getAddress());
            boolean isHfpConnected = TextUtils.equals(connectedHfpDevices.get(0).getAddress(),device.getAddress());
            return isA2DPConnected && isHfpConnected;
        }*/

        BluetoothHeadsetClient btHeadsetClient = BluetoothAdapterManager.getInstance().getBtHeadsetClient();
        if (btHeadsetClient == null) {
            return false;
        }
        List<BluetoothDevice> connectedHfpDevices = btHeadsetClient.getConnectedDevices();
        if (!connectedHfpDevices.isEmpty()) {
            boolean isHfpConnected = TextUtils.equals(connectedHfpDevices.get(0).getAddress(), device.getAddress());
            return isHfpConnected;
        }
        return false;
    }

    @Override
    public void cleanTask() {
        if (tasKQueue != null) {
            tasKQueue.clear();
            curDownloadType = null;
        }
    }

    @Override
    public boolean isDeviceDisconnected(BluetoothDevice device) {
        BluetoothA2dpSink a2DPSink = BluetoothAdapterManager.getInstance().getA2DPSink();
        BluetoothHeadsetClient btHeadsetClient = BluetoothAdapterManager.getInstance().getBtHeadsetClient();
        if (a2DPSink == null || btHeadsetClient == null) {
            return false;
        }
        List<BluetoothDevice> connectedA2dpDevices = a2DPSink.getConnectedDevices();
        List<BluetoothDevice> connectedHfpDevices = btHeadsetClient.getConnectedDevices();
        if (connectedA2dpDevices.isEmpty()) {
            if (connectedHfpDevices.isEmpty()) {
                return true;
            } else {
                return !TextUtils.equals(connectedHfpDevices.get(0).getAddress(), device.getAddress());
            }
        }
        if (connectedHfpDevices.isEmpty()) {
            return !TextUtils.equals(connectedA2dpDevices.get(0).getAddress(), device.getAddress());
        }

        boolean isA2APConnected = !TextUtils.equals(connectedA2dpDevices.get(0).getAddress(), device.getAddress());
        boolean isHfpConnected = !TextUtils.equals(connectedHfpDevices.get(0).getAddress(), device.getAddress());
        return isA2APConnected && isHfpConnected;

    }

    @Override
    public boolean isHfpDisconnected() {
        BluetoothHeadsetClient btHeadsetClient = BluetoothAdapterManager.getInstance().getBtHeadsetClient();
        if (btHeadsetClient == null) {
            return true;
        }
        List<BluetoothDevice> connectedDevices = btHeadsetClient.getConnectedDevices();
        if (connectedDevices.isEmpty()) {
            return true;
        }
        return false;
    }
}
