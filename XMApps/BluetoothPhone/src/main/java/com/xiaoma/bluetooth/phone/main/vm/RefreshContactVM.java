package com.xiaoma.bluetooth.phone.main.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.vcard.VCardEntry;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.sdk.BluetoothPhoneClient;
import com.xiaoma.bluetooth.phone.common.utils.PinyinUtils;
import com.xiaoma.bluetooth.phone.common.utils.StringFormatUtils;
import com.xiaoma.bluetooth.phone.contacts.model.PinyinComparator;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.utils.log.KLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/10
 */
public class RefreshContactVM extends BaseViewModel {

    private MutableLiveData<List<ContactBean>> mContactItems;
    private MutableLiveData<List<ContactBean>> phoneCallHistoryItems;
    private BluetoothPbapClient bluetoothPbapClient;
    private boolean is_pull_contact = false;
    private PullDataCallback callback;
    private int curType = BluetoothPhoneConstants.CONTACT;
    private List<ContactBean> historyCallList = new ArrayList<>();
    private MutableLiveData<int[]> menu = new MutableLiveData<>();
    private Handler contactHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE:
                    if (msg.obj != null) {
                        List<VCardEntry> items = (List<VCardEntry>) msg.obj;
                        if (curType == BluetoothPhoneConstants.CONTACT) {
                            historyCallList.clear();
                            curType = BluetoothPhoneConstants.INCOMING_CALL;
                            mContactItems.setValue(convertToContactBean(items, true, curType));
                            KLog.d("hzx", "通讯录拉取完成,开始拉取来电: " + timeToString(System.currentTimeMillis()));
                            pullCallHistory(BluetoothPbapClient.ICH_PATH, BluetoothPhoneConstants.INCOMING_CALL);
                        } else if (curType == BluetoothPhoneConstants.INCOMING_CALL) {
                            historyCallList.addAll(convertToContactBean(items, false, curType));
                            KLog.d("hzx", "来电拉取完成,开始拉取去电: " + timeToString(System.currentTimeMillis()));
                            pullCallHistory(BluetoothPbapClient.OCH_PATH, BluetoothPhoneConstants.OUTGOING_CALL);
                        } else if (curType == BluetoothPhoneConstants.OUTGOING_CALL) {
                            historyCallList.addAll(convertToContactBean(items, false, curType));
                            KLog.d("hzx", "去电拉取完成,开始拉取未接电话: " + timeToString(System.currentTimeMillis()));
                            pullCallHistory(BluetoothPbapClient.MCH_PATH, BluetoothPhoneConstants.MISSING_CALL);
                        } else if (curType == BluetoothPhoneConstants.MISSING_CALL) {
                            KLog.d("hzx", "去电拉取完成: " + timeToString(System.currentTimeMillis()));
                            historyCallList.addAll(convertToContactBean(items, false, curType));
                            sortByTime();
                            phoneCallHistoryItems.setValue(historyCallList);
                            KLog.d("hzx", "电话拉取完成,排序完成: " + timeToString(System.currentTimeMillis()));
                            is_pull_contact = false;
                            bluetoothPbapClient.disconnect();
                            BluetoothPhoneClient.getInstance().notifySyncSuccess(historyCallList);
                            if (callback != null) {
                                callback.pullDataSuccess();
                            }
                        }
                    }
                    break;
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR:
                    bluetoothPbapClient.disconnect();
                    is_pull_contact = false;
                    if (callback != null) {
                        callback.pullDataError();
                    }
                    break;
            }
            return true;
        }
    });

    public RefreshContactVM(@NonNull Application application) {
        super(application);
    }

    private void sortByTime() {
        Collections.sort(historyCallList, new Comparator<ContactBean>() {
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

    private void pullCallHistory(String ichPath, int historyCallType) {
        curType = historyCallType;
        bluetoothPbapClient.pullPhoneBook(ichPath);
    }

    private List<ContactBean> convertToContactBean(List<VCardEntry> items, boolean sort, int curType) {
        List<ContactBean> temp = new ArrayList<>();
        for (VCardEntry vcardEntry : items) {
            String name = getName(vcardEntry);
            String callDatetime;
            String time;
            String timeStamp;
            if (TextUtils.isEmpty(vcardEntry.getCallDatetime())) {
                callDatetime = null;
                time = null;
                timeStamp = null;
            } else {
                callDatetime = StringFormatUtils.getDateFromString(vcardEntry.getCallDatetime());
                time = StringFormatUtils.getTimeFromString(vcardEntry.getCallDatetime());
                timeStamp = StringFormatUtils.getTimeStampString(vcardEntry.getCallDatetime());
                KLog.d("timeStamp: " + timeStamp);
            }

//            boolean isPrefer = TextUtils.equals("Starred in Android", vcardEntry.getMember());
            List<VCardEntry.PhotoData> photoList = vcardEntry.getPhotoList();
            byte[] photoByte = null;
            if (photoList != null && photoList.size() >= 1) {
                photoByte = photoList.get(0).getBytes();
            }
            List<VCardEntry.PhoneData> phoneList = vcardEntry.getPhoneList();
            if (phoneList == null || phoneList.size() == 0) continue;
            for (VCardEntry.PhoneData phoneData : phoneList) {
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(name.trim())) {
                    name = BlueToothPhone.getContext().getString(R.string.unknown_contact);
                }
                ContactBean tempBean = new ContactBean();
                tempBean.setPhoneNum(phoneData.getNumber().replace("-", ""));
                tempBean.setName(name);
                tempBean.setId(name + phoneData.getNumber());
                tempBean.setDate(callDatetime);
//                tempBean.setCollected(isPrefer);
                tempBean.setCallType(curType);
                tempBean.setTime(time);
                tempBean.setTimeStamp(timeStamp);
//                tempBean.setPhoneType(phoneData.getPhoneType());

                String pinYin = PinyinUtils.getPinYin(name);
                String firstPinYin = pinYin.substring(0, 1).toUpperCase();
                tempBean.setPinYin(pinYin);
                if (firstPinYin.matches("[A-Z]")) {
                    tempBean.setFirstPinYin(firstPinYin);
                } else {
                    tempBean.setFirstPinYin("#");
                }
                tempBean.setIcon(photoByte);
                temp.add(tempBean);
            }
            if (sort) {
                Collections.sort(temp, new PinyinComparator());
            }
        }
        return temp;
    }

    private String getName(VCardEntry vcardEntry) {
        String name = null;
        VCardEntry.NameData nameData = vcardEntry.getNameData();
        if (!TextUtils.isEmpty(nameData.getFamily())) {
            name = nameData.getFamily();
        }
        if (!TextUtils.isEmpty(nameData.getMiddle())) {
            if (TextUtils.isEmpty(name)) {
                name = nameData.getMiddle();
            } else {
                name += nameData.getMiddle();
            }
        }
        if (!TextUtils.isEmpty(nameData.getGiven())) {
            if (TextUtils.isEmpty(name)) {
                name = nameData.getGiven();
            } else {
                name += nameData.getGiven();
            }
        }
        return name;
    }

    public MutableLiveData<List<ContactBean>> getContact() {
        if (mContactItems == null) {
            mContactItems = new MutableLiveData<>();
        }
        return mContactItems;
    }

    public MutableLiveData<List<ContactBean>> getPhoneCallHistory() {
        if (phoneCallHistoryItems == null) {
            phoneCallHistoryItems = new MutableLiveData<>();
        }
        return phoneCallHistoryItems;
    }

    public void refreshContact(BluetoothDevice device) {
        readConnectedDeviceInfo(device);
//        mockContact();
    }

    /*-----测试代码-------*/
    /*private void mockContact() {
        List<ContactBean> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ContactBean contactBean = new ContactBean();
            if (i % 3 == 0) {
                contactBean.setName("Roger Federer" + i);
                contactBean.setPhoneNum("137777777" + i);
            } else if (i % 3 == 1) {
                contactBean.setName("Novac Djokovic" + i);
                contactBean.setPhoneNum("1388888888" + i);
            } else if (i % 3 == 2) {
                contactBean.setName("Rafa Nadal" + i);
                contactBean.setPhoneNum("1399999999" + i);
            }
            contactBean.setDate("2019 01-24 15:" + i);
            items.add(contactBean);
        }
        phoneCallHistoryItems.setValue(items);
    }*/

    private void readConnectedDeviceInfo(BluetoothDevice device) {
        if (is_pull_contact) return;
        is_pull_contact = true;
        bluetoothPbapClient = new BluetoothPbapClient(device, contactHandler);
        curType = BluetoothPhoneConstants.CONTACT;
        KLog.d("hzx", "开始拉取通讯录: " + timeToString(System.currentTimeMillis()));
        bluetoothPbapClient.pullPhoneBook(BluetoothPbapClient.PB_PATH);
    }

    private String timeToString(long timeStamp) {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String format = sf.format(new Date(timeStamp));
        return format;
    }

    public void setOnPullDataCallback(PullDataCallback callback) {
        this.callback = callback;
    }

    public void cleanData() {
//        getPhoneCallHistory().setValue(new ArrayList<ContactBean>());
//        getContact().setValue(new ArrayList<ContactBean>());
    }

    public Handler getHandler() {
        return contactHandler;
    }

    public MutableLiveData<int[]> getMenu(Context context) {
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.bluetooth_menu);
        int[] items = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            items[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        menu.setValue(items);
        return menu;
    }


    public interface PullDataCallback {
        void pullDataSuccess();

        void pullDataError();
    }
}
