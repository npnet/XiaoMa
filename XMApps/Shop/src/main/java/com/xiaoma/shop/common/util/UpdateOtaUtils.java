package com.xiaoma.shop.common.util;

import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.business.download.impl.HUSoundEffDownload;
import com.xiaoma.shop.business.download.impl.LCDSoundEffDownload;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.ZipUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/21
 * @Describe: OTA 升级 工具类
 */

public class UpdateOtaUtils {
    //用于控制单任务升级
    private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void beginExecution() {
        atomicBoolean.compareAndSet(false, true);
    }

    public static void endExecution() {
        atomicBoolean.compareAndSet(true, false);
    }

    public static boolean isExecuting() {
        return atomicBoolean.get();
    }


    public static void pushTheFileToDst(final String srcFilePath,
                                        @ResourceType final int type,
                                        @UpdateResouceType final int updateType,
                                        final OnHandleListener listener) {
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                actualPushTheFileToDst(srcFilePath, type, updateType, listener);
            }
        });
    }

    private static SoundEffDownload getSoundEffDownloader(@ResourceType int type) {
        return ResourceType.VEHICLE_SOUND == type ?
                HUSoundEffDownload.getInstance() : LCDSoundEffDownload.getInstance();
    }

    private static void actualPushTheFileToDst(String srcFilePath,
                                               @ResourceType int type,
                                               @UpdateResouceType int updateType,
                                               OnHandleListener listener) {
        if (!TextUtils.isEmpty(srcFilePath)) {
            File folder = null;
            switch (type) {
                case ResourceType.VEHICLE_SOUND:
                    folder = ConfigManager.FileConfig.getHUSoundEffDownloadFolder();
                    break;
                case ResourceType.INSTRUMENT_SOUND:
                    folder = ConfigManager.FileConfig.getLCDSoundEffDownloadFolder();
                    break;
            }
            String fn = VehicleSoundUtils.getFileName(srcFilePath);
            File file = new File(folder, fn);
            File dstFolder = getDstFolder(updateType);
            if (file != null) {
                try {
                    //delete dst file
                    FileUtils.delete(dstFolder);
                    // copy the file
                    // get file name
                    File toFile = new File(dstFolder + "/" + fn);
                    if (updateType == UpdateResouceType.HU || updateType == UpdateResouceType.ICH) {//HU 和 ICH 需要解压
                        if (ZipUtil.unzipFile(file, dstFolder)) {
                            handleResult(true, listener);
                        }
                    }else{
                        if (FileUtils.copy(file, toFile)) {
                            //push the file
                            handleResult(true, listener);
                        }
                    }
                } catch (Exception e) {
                    // use failure ,the copy file error
                    handleResult(false, listener);
                }
            } else {
                // use failure , the file is null
                handleResult(false, listener);
            }
        } else {
            // use failure, the url is null
            handleResult(false, listener);
        }
    }

    private static void handleResult(final boolean succeed,
                                     final OnHandleListener listener) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (succeed) {
                    listener.onSucceed();
                } else {
                    listener.onFailure();
                }
            }
        });
    }

    private static File getDstFolder(@UpdateResouceType int updateType) {
        File folderFile = null;
        switch (updateType) {
            case UpdateResouceType.THREE_D:
                folderFile = ConfigManager.FileConfig.get3DFolder();
                break;
            case UpdateResouceType.ICH:
                folderFile = ConfigManager.FileConfig.getICHFolder();
                break;
            case UpdateResouceType.ICL:
                folderFile = ConfigManager.FileConfig.getICLFolder();
                break;
            case UpdateResouceType.HU:
                folderFile = ConfigManager.FileConfig.getHUFolder();
                break;
        }
        return folderFile;
    }

    public interface OnHandleListener {
        void onSucceed();

        void onFailure();
    }
}
