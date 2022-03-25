package com.xiaoma.app.usb;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import com.xiaoma.config.ConfigConstants;
import com.xiaoma.utils.receiver.UsbDetector;

import java.util.List;

/**
 * Created by Thomas on 2018/11/14 0014
 * usb 数据 状态 管理
 */

public class UsbMediaDataManager {

    private static final String TAG = "UsbMediaDataManager";

    private Context context;
    private static UsbMediaDataManager sUsbMediaDataManager;
    private UsbStatus mCurrentUsbStatus = UsbStatus.REMOVED;

    //usb根路径
    private String rootPath;
    private UsbConnectStateListener listener;

    private UsbMediaDataManager() {
    }

    public static synchronized UsbMediaDataManager getInstance() {
        if (sUsbMediaDataManager == null) {
            sUsbMediaDataManager = new UsbMediaDataManager();
        }
        return sUsbMediaDataManager;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        initUsbReceiver();
    }

    public UsbStatus getCurrentUsbStatus() {
        return mCurrentUsbStatus;
    }

    private void initUsbReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(ConfigConstants.STORAGE_ACTION_VOLUME_STATE_CHANGED);
        context.registerReceiver(UsbDetector.getInstance(), intentFilter);
    }

    private UsbDetector.UsbDetectListener mUsbDetectListener = new UsbDetector.UsbDetectListener() {
        @Override
        public void noUsbMounted() {
            mCurrentUsbStatus = UsbStatus.NO_USB_MOUNTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.NO_USB_MOUNTED, null);
            }
        }

        @Override
        public void inserted() {
            mCurrentUsbStatus = UsbStatus.INSERTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.INSERTED, null);
            }
        }

        @Override
        public void mounted(List<String> mountPaths) {
            mCurrentUsbStatus = UsbStatus.MOUNTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.MOUNTED, mountPaths);
            }
        }

        @Override
        public void mountError() {
            mCurrentUsbStatus = UsbStatus.MOUNT_ERROR;
            if (listener != null) {
                listener.onConnection(UsbStatus.MOUNT_ERROR, null);
            }
        }

        @Override
        public void removed() {
            mCurrentUsbStatus = UsbStatus.REMOVED;
            if (listener != null) {
                listener.onConnection(UsbStatus.REMOVED, null);
            }
        }
    };

    public void syncUsbConnectState(Context context, final UsbConnectStateListener listener) {
        this.listener = listener;
        UsbDetector.getInstance().syncUsbConnectState(context, mUsbDetectListener);
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
