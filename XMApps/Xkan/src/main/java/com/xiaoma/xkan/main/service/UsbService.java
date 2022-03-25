package com.xiaoma.xkan.main.service;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xkan.main.service
 *  @file_name:      UsbService
 *  @author:         Rookie
 *  @create_time:    2019/8/6 17:31
 *  @description：   TODO             */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.listener.UsbConnectStateListener;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbStatus;

import org.simple.eventbus.EventBus;

import java.util.List;

public class UsbService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UsbMediaDataManager.getInstance().syncUsbConnectState(this, mUsbConnectStateListener);
    }

    private UsbConnectStateListener mUsbConnectStateListener = new UsbConnectStateListener() {
        @Override
        public void onConnection(UsbStatus status, List<String> mountPaths) {
            switch (status) {
                case NO_USB_MOUNTED:
                    KLog.d("usbtest", "NO_USB_MOUNTED");
                    //没有usb加载 就根据是否是车机来判断
                    if (!ConfigManager.ApkConfig.isCarPlatform()) {
                        UsbDetector.getInstance().setRemoveState(false);
                        EventBus.getDefault().post(FileUtils.getSDPath() + "/usb", XkanConstants.USB_MOUNTED);
                    } else {
                        EventBus.getDefault().post("", XkanConstants.USB_NO_MOUNTED);
                    }
                    break;
                case INSERTED:
                    KLog.d("usbtest", "INSERTED");
                    EventBus.getDefault().post("", XkanConstants.USB_INSERT);
                    break;
                case MOUNTED:
                    KLog.d("usbtest", "MOUNTED");
                    EventBus.getDefault().post(mountPaths.get(0), XkanConstants.USB_MOUNTED);
                    break;
                case MOUNT_ERROR:
                    KLog.d("usbtest", "MOUNT_ERROR");
                    EventBus.getDefault().post("", XkanConstants.USB_NO_MOUNTED);
                    break;
                case REMOVED:
                    KLog.d("usbtest", "REMOVED");
                    //u盘拔出后,重置列表页面
                    //并且要关闭对应详情页
                    EventBus.getDefault().post("", XkanConstants.RELEASE_MEDIAINFO);
                    EventBus.getDefault().post("", XkanConstants.USB_NO_MOUNTED);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        mUsbConnectStateListener = null;
        UsbMediaDataManager.getInstance().removeConnectListener();
        super.onDestroy();
    }
}
