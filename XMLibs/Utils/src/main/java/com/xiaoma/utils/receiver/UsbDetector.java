package com.xiaoma.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;

import com.xiaoma.config.ConfigConstants;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Thomas on 2018/11/14 0014
 * usb state manager
 */

public class UsbDetector extends BroadcastReceiver {

    private static final String TAG = "UsbDetector";
    private HashSet<Integer> mHashSet = new HashSet<>();
    private boolean isRemoveState = false;
    private static CopyOnWriteArraySet<UsbDetectListener> mUsbDetectListeners = new CopyOnWriteArraySet<>();
    private Context mContext;
    private boolean isHandInsert = false;

    private UsbDetector() {
    }

    private static class UsbDetectorHolder {
        static final UsbDetector INSTANCE = new UsbDetector();
    }

    public boolean isHandInsert() {
        return isHandInsert;
    }

    public void setRemoveState(boolean removeState) {
        isRemoveState = removeState;
    }

    public static UsbDetector getInstance() {
        return UsbDetectorHolder.INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ConfigConstants.STORAGE_ACTION_VOLUME_STATE_CHANGED);
        context.registerReceiver(UsbDetector.getInstance(), filter);
    }

    public void addUsbDetectListener(UsbDetectListener listener) {
        if (listener != null && !mUsbDetectListeners.contains(listener)) {
            mUsbDetectListeners.add(listener);
        }
    }

    public void removeUsbDetectListener(UsbDetectListener listener) {
        if (listener != null && mUsbDetectListeners.contains(listener)) {
            mUsbDetectListeners.remove(listener);
        }
    }

    //主动判断USB插入、挂载或者移除的状态
    public void syncUsbConnectState(final Context context, UsbDetectListener listener) {
        if (listener == null) {
            return;
        }
        addUsbDetectListener(listener);
        syncUsbConnectState(context);
    }

    public void syncUsbConnectState(Context context) {
        //注册广播
        initHashSet();
        boolean isMassStorageIn = false;
        try {
            //check usb device inserted or not
            UsbManager obj_UsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            Collection<UsbDevice> usbDeviceList = obj_UsbManager.getDeviceList().values();
            Log.i(TAG, "UsbDeviceList size : " + usbDeviceList.size());
            for (UsbDevice usbDevice : usbDeviceList) {
                if (findIOSDeviceByVIDPID(usbDevice)) {
                    Log.i(TAG, "----Find IOS existing----");
                    continue;
                }
                if (findAndroidDeviceByInterface(usbDevice) && findAndroidDeviceByVID(usbDevice)) {
                    Log.i(TAG, "----Find Android existing----");
                    continue;
                }
                if (usbDevice.getVendorId() == 0x0525) {
                    Log.i(TAG, "----Find T-Box existing----");
                    continue;
                }
                if (findMassStorageByInterface(usbDevice)) {
                    Log.i(TAG, "----Find Mass Storage existing----");
                    isMassStorageIn = true;
                    continue;
                }
                if (findUsbHubByInterface(usbDevice)) {
                    Log.i(TAG, "----Find USB Hub existing----");
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isMassStorageIn) {
            if (mUsbDetectListeners.size() > 0) {
                isRemoveState = true;
                for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                    usbDetectListener.noUsbMounted();
                }
                return;
            }

        }
        try {
            //check device mounted or not
            List<String> mountpaths = new ArrayList<>();
            StorageManager obj_StorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class class_StorageManager = StorageManager.class;
            Method method_getVolumes = class_StorageManager.getMethod("getVolumes");
            List<Object> getVolumes = (List<Object>) method_getVolumes.invoke(obj_StorageManager);
            for (Object obj_VolumeInfo : getVolumes) {
                Class class_VolumeInfo = obj_VolumeInfo.getClass();
                Method method_getType = class_VolumeInfo.getMethod("getType");
                int getType = (int) method_getType.invoke(obj_VolumeInfo);
                Method method_getState = class_VolumeInfo.getMethod("getState");
                int getState = (int) method_getState.invoke(obj_VolumeInfo);
                Method method_getPath = class_VolumeInfo.getMethod("getPath");
                File getPath = (File) method_getPath.invoke(obj_VolumeInfo);
                Method method_getDisk = class_VolumeInfo.getMethod("getDisk");
                Object obj_DiskInfo = method_getDisk.invoke(obj_VolumeInfo);
                if (getType != 0 || obj_DiskInfo == null
                        || getState != 2 || getPath == null) {
                    continue;
                }
                mountpaths.add(getPath.toString());
            }
            if (mountpaths.size() <= 0) {
                Log.e(TAG, "mountPaths size is null");
                mountedError();
                return;
            }

            //usb exist and mounted
            if (mUsbDetectListeners.size() > 0) {
                isRemoveState = false;
                Log.i(TAG, "usb mounted");
                isHandInsert = false;
                for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                    usbDetectListener.mounted(mountpaths);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //监听系统广播：判断USB插入、挂载或者移除的状态
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            onInserted(context, intent);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            onRemoved(intent);
        } else if (ConfigConstants.STORAGE_ACTION_VOLUME_STATE_CHANGED.equals(action)) {
            onMounted(context, intent);
        }
    }

    public boolean isRemoveState() {
        return isRemoveState;
    }

    private void onInserted(Context context, Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null
                || !findMassStorageByInterface(usbDevice)
                || (findAndroidDeviceByInterface(usbDevice) && findAndroidDeviceByVID(usbDevice))
                || findIOSDeviceByVIDPID(usbDevice)
                || findUsbHubByInterface(usbDevice)
                || findUnsupportedDeviceByPID(usbDevice)) {
            return;
        }
        Log.i(TAG, "usb insert");
        if (mUsbDetectListeners.size() > 0) {
            isRemoveState = false;
            for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                usbDetectListener.inserted();
            }
        }
    }

    private void onRemoved(Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null
                || !findMassStorageByInterface(usbDevice)
                || (findAndroidDeviceByInterface(usbDevice) && findAndroidDeviceByVID(usbDevice))
                || findIOSDeviceByVIDPID(usbDevice)
                || findUsbHubByInterface(usbDevice)
                || findUnsupportedDeviceByPID(usbDevice)) {
            return;
        }
        Log.i(TAG, "usb remove");
        if (mUsbDetectListeners.size() > 0) {
            isRemoveState = true;
            for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                usbDetectListener.removed();
            }
        }
    }

    private void onMounted(final Context context, Intent intent) {
        //VolumeInfo.ACTION_VOLUME_STATE_CHANGED == "android.os.storage.action.VOLUME_STATE_CHANGED"
        //VolumeInfo.EXTRA_VOLUME_STATE == "android.os.storage.extra.VOLUME_STATE"
        //VolumeInfo.STATE_CHECKING = 1;
        //VolumeInfo.STATE_MOUNTED = 2;
        //VolumeInfo.STATE_MOUNTED_READ_ONLY = 3;
        //VolumeInfo.STATE_FORMATTING = 4;
        //VolumeInfo.STATE_EJECTING = 5;
        //VolumeInfo.STATE_UNMOUNTABLE = 6;
        //VolumeInfo.STATE_REMOVED = 7;
        //VolumeInfo.STATE_BAD_REMOVAL = 8;
        //VolumeInfo.STATE_UNMOUNTED = 0;
        final int usbState = intent.getIntExtra("android.os.storage.extra.VOLUME_STATE", 0);
        String deviceName = intent.getStringExtra("android.os.storage.extra.FS_UUID");
        Log.i(TAG, "VOLUME_STATE = " + usbState + ", FS_UUID = " + deviceName);
        if (usbState == VolumeInfo.STATE_UNMOUNTABLE) {
            mountedError();
            return;
        }
        if (usbState != VolumeInfo.STATE_MOUNTED) {
            return;
        }
        try {
            StorageManager obj_StorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class class_StorageManager = StorageManager.class;
            Method method_getVolumes = class_StorageManager.getMethod("getVolumes");
            List<Object> getVolumes = (List<Object>) method_getVolumes.invoke(obj_StorageManager);
            if (getVolumes == null) {
                mountedError();
                return;
            }
            List<String> mountPaths = new ArrayList<>();
            for (Object obj_VolumeInfo : getVolumes) {
                Class class_VolumeInfo = obj_VolumeInfo.getClass();
                Method method_getType = class_VolumeInfo.getMethod("getType");
                int getType = (int) method_getType.invoke(obj_VolumeInfo);
                Method method_getState = class_VolumeInfo.getMethod("getState");
                int getState = (int) method_getState.invoke(obj_VolumeInfo);
                Method method_getPath = class_VolumeInfo.getMethod("getPath");
                File getPath = (File) method_getPath.invoke(obj_VolumeInfo);
                Method method_getDisk = class_VolumeInfo.getMethod("getDisk");
                Object obj_DiskInfo = method_getDisk.invoke(obj_VolumeInfo);
                Log.i(TAG, "detected path = " + getPath.toString() + ", usbState = " + usbState);
                if (getType != 0 || obj_DiskInfo == null
                        || getState != 2 || getPath == null) {
                    continue;
                }

                Log.i(TAG, "usb mounted useful path = " + getPath.toString() + ", usbState = " + usbState);
                mountPaths.add(getPath.toString());
            }
            if (mountPaths.size() <= 0) {
                Log.i(TAG, "mountPaths is null");
                mountedError();
                return;
            }
            Log.i(TAG, "usb mounted");
            if (mUsbDetectListeners.size() > 0) {
                isRemoveState = false;
                isHandInsert = true;
                for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                    usbDetectListener.mounted(mountPaths);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mountedError();
        }
    }

    private void mountedError() {
        if (mUsbDetectListeners.size() > 0) {
            isRemoveState = true;
            for (UsbDetectListener usbDetectListener : mUsbDetectListeners) {
                usbDetectListener.mountError();
            }
        }
    }

    private boolean findMassStorageByInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            Log.i(TAG, "    findMassStorageByInterface :: getInterface(i) : \n    "
                    + device.getInterface(i).toString());
            if (device.getInterface(i).getInterfaceClass() == 255) {
                Log.i(TAG, "findMassStorageByInterface : false");
                return false;
            }
            if (device.getInterface(i).getInterfaceClass() == 0x08) {
                Log.d(TAG, "findMassStorageByInterface : true");
                return true;
            }
        }
        Log.i(TAG, "findMassStorageByInterface : false");
        return false;
    }

    private boolean findUsbHubByInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            Log.i(TAG, "findUsbHubByInterface :: getInterface(i) : \n"
                    + device.getInterface(i).toString());
            if (device.getInterface(i).getInterfaceClass() == 0x09) {
                Log.d(TAG, "findUsbHubByInterface : true");
                return true;
            }
        }
        Log.i(TAG, "findUsbHubByInterface : false");
        return false;
    }

    private boolean findUnsupportedDeviceByPID(UsbDevice device) {
        class VPID {
            int vid;
            int pid;

            VPID(int v, int p) {
                this.vid = v;
                this.pid = p;
            }
        }
        HashSet<VPID> unsupportedPIDs = new HashSet<VPID>();
        unsupportedPIDs.add(new VPID(0x0b95, 0x1790));
        unsupportedPIDs.add(new VPID(0x046d, 0x08c9));
        unsupportedPIDs.add(new VPID(0x045e, 0x0719));
        unsupportedPIDs.add(new VPID(0x045e, 0x0084));
        unsupportedPIDs.add(new VPID(0x1a0a, 0x0201));
        if (unsupportedPIDs.contains(new VPID(device.getVendorId(), device.getProductId()))) {
            Log.d(TAG, "findUnsupportedDeviceByPID : true");
            return true;
        }
        Log.d(TAG, "findUnsupportedDeviceByPID : true");
        return false;
    }

    /**
     * 通过interface接口判断是否是android设备
     *
     * @param device
     * @return
     */
    private boolean findAndroidDeviceByInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            Log.i(TAG, "    findAndroidDeviceByInterface :: getInterface(i) : \n    "
                    + device.getInterface(i).toString());
            if (device.getInterface(i).getInterfaceClass() == 255
                    && device.getInterface(i).getInterfaceSubclass() == 66
                    && device.getInterface(i).getInterfaceProtocol() == 1) {
                Log.d(TAG, "findAndroidDeviceByInterface : true");
                return true;
            }
        }
        Log.i(TAG, "findAndroidDeviceByInterface : false");
        return false;
    }

    /**
     * 通过VID接口判断是否是android设备
     *
     * @param device
     * @return
     */
    private boolean findAndroidDeviceByVID(UsbDevice device) {
        if (mHashSet.contains(device.getVendorId())) {
            Log.d(TAG, "findAndroidDeviceByVID : true");
            return true;
        }
        Log.d(TAG, "findAndroidDeviceByVID : false");
        return false;
    }

    /**
     * 通过VID、PID接口判断是否是IOS设备
     *
     * @param device
     * @return
     */
    private boolean findIOSDeviceByVIDPID(UsbDevice device) {
        if ((device.getVendorId() == 0x05ac) && ((device.getProductId() & 0xff00) == 0x1200)) {
            Log.d(TAG, "findIOSDeviceByVIDPID : true");
            return true;
        }
        Log.d(TAG, "findIOSDeviceByVIDPID : false");
        return false;
    }

    /**
     * 添加android生产厂商的VID到hashset
     */
    public void initHashSet() {
        Log.d(TAG, "initHashSet");
        mHashSet.add(0x18d1);
        mHashSet.add(0x8087);
        mHashSet.add(0x0bb4);
        mHashSet.add(0x04e8);
        mHashSet.add(0x22b8);
        mHashSet.add(0x1004);
        mHashSet.add(0x12D1);
        mHashSet.add(0x0502);
        mHashSet.add(0x0FCE);
        mHashSet.add(0x0489);
        mHashSet.add(0x413c);
        mHashSet.add(0x0955);
        mHashSet.add(0x091E);
        mHashSet.add(0x04dd);
        mHashSet.add(0x19D2);
        mHashSet.add(0x0482);
        mHashSet.add(0x10A9);
        mHashSet.add(0x05c6);
        mHashSet.add(0x2257);
        mHashSet.add(0x0409);
        mHashSet.add(0x04DA);
        mHashSet.add(0x0930);
        mHashSet.add(0x1F53);
        mHashSet.add(0x2116);
        mHashSet.add(0x0b05);
        mHashSet.add(0x0471);
        mHashSet.add(0x0451);
        mHashSet.add(0x0F1C);
        mHashSet.add(0x0414);
        mHashSet.add(0x2420);
        mHashSet.add(0x1219);
        mHashSet.add(0x1BBB);
        mHashSet.add(0x2006);
        mHashSet.add(0x17EF);
        mHashSet.add(0xE040);
        mHashSet.add(0x24E3);
        mHashSet.add(0x1D4D);
        mHashSet.add(0x0E79);
        mHashSet.add(0x1662);
        mHashSet.add(0x04C5);
        mHashSet.add(0x25E3);
        mHashSet.add(0x0408);
        mHashSet.add(0x2314);
        mHashSet.add(0x054C);
        mHashSet.add(0x1949);
        mHashSet.add(0x1EBF);
        mHashSet.add(0x2237);
        mHashSet.add(0x2340);
        mHashSet.add(0x16D5);
        mHashSet.add(0x19A5);
        mHashSet.add(0x22D9);
        mHashSet.add(0x2717);
        mHashSet.add(0x19D1);
        mHashSet.add(0x2836);
        mHashSet.add(0x201E);
        mHashSet.add(0x109b);
        mHashSet.add(0x0e8d);
        mHashSet.add(0x2080);
        mHashSet.add(0x1D45);
        mHashSet.add(0x03fc);
        mHashSet.add(0x1f3a);
        mHashSet.add(0x2a47);
        mHashSet.add(0x2ae5);
        mHashSet.add(0x271D);
        mHashSet.add(0x2b0e);
        mHashSet.add(0x2a45);
        mHashSet.add(0x2a70);
        mHashSet.add(0x2207);
        mHashSet.add(0x29a9);
        mHashSet.add(0x1782);
        mHashSet.add(0x9bb5);
        mHashSet.add(0x2970);
        mHashSet.add(0x05e0);
        mHashSet.add(0x2b4c);
    }

    public interface UsbDetectListener {

        void noUsbMounted();

        void inserted();

        void mounted(List<String> mountPaths);

        void mountError();

        void removed();
    }

}
