package com.xiaoma.instructiondistribute.xkan.service;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.instructiondistribute.xkan.service
 *  @file_name:      XkanService
 *  @author:         Rookie
 *  @create_time:    2019/7/18 17:16
 *  @description：   TODO             */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbConnectStateListener;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbStatus;
import com.xiaoma.instructiondistribute.xkan.common.util.ImageUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;

import org.simple.eventbus.EventBus;

import java.util.List;

public class XkanService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        UsbMediaDataManager.getInstance().syncUsbConnectState(this, mUsbConnectStateListener);
    }

    private UsbConnectStateListener mUsbConnectStateListener = new UsbConnectStateListener() {
        @Override
        public void onConnection(UsbStatus status, List<String> mountPaths) {
            switch (status) {
                case NO_USB_MOUNTED:
                    KLog.d("usbtest", "NO_USB_MOUNTED");
                    Toast.makeText(XkanService.this, "无usb挂载", Toast.LENGTH_SHORT).show();
                    //没有usb加载 就根据是否是车机来判断
                    if (!ConfigManager.ApkConfig.isCarPlatform()) {
                        UsbDetector.getInstance().setRemoveState(false);
                        isUsbStateMountedSuccess(FileUtils.getSDPath() + "/usb");
                    } else {
                        isUsbStateMountedFail();
                    }

                    break;
                case INSERTED:
                    Toast.makeText(XkanService.this, "usb插入", Toast.LENGTH_SHORT).show();
                    break;
                case MOUNTED:
                    KLog.d("usbtest", "MOUNTED");
                    Toast.makeText(XkanService.this, "usb挂载 : " + mountPaths.get(0), Toast.LENGTH_SHORT).show();
                    isUsbStateMountedSuccess(mountPaths.get(0));
                    break;
                case MOUNT_ERROR:
                    KLog.d("usbtest", "MOUNT_ERROR");
                    Toast.makeText(XkanService.this, "usb挂载出错", Toast.LENGTH_SHORT).show();
                    isUsbStateMountedFail();
                    break;
                case REMOVED:
                    KLog.d("usbtest", "REMOVED");
                    Toast.makeText(XkanService.this, "usb移除", Toast.LENGTH_SHORT).show();
                    isUsbStateMountedFail();
                    break;
                default:
                    break;
            }
        }
    };

    private void isUsbStateMountedSuccess(final String path) {
        //清除glide缓存
        ImageUtils.clearCacheDiskSelf(this);
        ImageUtils.clearCacheMemory(this);
        UsbMediaDataManager.getInstance().fetchUsbMediaData(path, new UsbMediaSearchListener() {
            @Override
            public void onUsbMediaSearchFinished() {
                if (UsbDetector.getInstance().isRemoveState()) {
                    isUsbStateMountedFail();
                } else {
                    UsbMediaDataManager.getInstance().setCurrPath(path);
                    UsbMediaDataManager.getInstance().addPathToList(path);
                    UsbMediaDataManager.getInstance().addFileListByPath(path);
                    EventBus.getDefault().post(XkanConstants.XKAN_EVENT, XkanConstants.UPDATE_DATA);
                }
            }
        });
    }

    private void isUsbStateMountedFail() {
        //USB移除发送通知到相应的视频图片查看页面
        EventBus.getDefault().post(XkanConstants.XKAN_USB_REMOVE, XkanConstants.RELEASE_MEDIAINFO);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUsbConnectStateListener = null;
        UsbMediaDataManager.getInstance().removeConnectListener();
    }
}
