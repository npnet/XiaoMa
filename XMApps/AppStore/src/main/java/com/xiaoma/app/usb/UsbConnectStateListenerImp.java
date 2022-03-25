package com.xiaoma.app.usb;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xiaoma.app.R;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsbConnectStateListenerImp implements UsbConnectStateListener {

    private Context mContext;
    private Dialog mDialog;
    private ArrayList<String> apkPathList;
    //U盘根目录下需要创建同名xm_install文件夹
    private static final String XM_INSTALL_FOLD = "xm_install";

    public UsbConnectStateListenerImp(Context context) {
        this.mContext = context;
        apkPathList = new ArrayList<>();
    }

    @Override
    public void onConnection(UsbStatus status, List<String> mountPaths) {
        switch (status) {
            case NO_USB_MOUNTED:
                KLog.d("usbtest", "NO_USB_MOUNTED");
                break;

            case INSERTED:
                KLog.d("usbtest", "INSERTED");
                break;

            case MOUNTED:
                KLog.d("usbtest", "MOUNTED");
                //获取U盘下,XM_INSTALL_FOLD文件夹下面apk路径
                apkPathList.clear();
                int apkSize = getApkFilePath(mountPaths.get(0) + File.separator + XM_INSTALL_FOLD, apkPathList).size();
                if (apkSize > 0) {
                    mDialog = showInstallDialog();
                }
                break;

            case MOUNT_ERROR:
                KLog.d("usbtest", "MOUNT_ERROR");
                break;

            case REMOVED:
                KLog.d("usbtest", "REMOVED");
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                break;

            default:
                break;
        }
    }

    private Dialog showInstallDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        window.setLayout(500, 300);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.color.window_bg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500, 300);
        dialog.addContentView(LayoutInflater.from(mContext).inflate(R.layout.dialog_usb_insert, null), params);
        dialog.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                XMToast.showToast(mContext, R.string.one_shot_install);
                ApkUtils.silentInstall(apkPathList);
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        return dialog;
    }

    private ArrayList<String> getApkFilePath(String fileAbsolutePath, ArrayList<String> apkPathList) {
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        for (File fileIn : subFile) {
            KLog.d("File:" + fileIn);
            // 判断是否为文件夹
            // TODO: 2019/7/10 md5文件校验
            if (fileIn.isDirectory()) {
                getApkFilePath(fileIn.getAbsolutePath(), apkPathList);

            } else {
                String filename = fileIn.getName();
                // 判断是否为apk文件
                if (filename.trim().toLowerCase().endsWith(".apk")) {
                    apkPathList.add(fileIn.getAbsolutePath());
                }
            }
        }

        return apkPathList;
    }
}
