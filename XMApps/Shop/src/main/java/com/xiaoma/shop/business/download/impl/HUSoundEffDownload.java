package com.xiaoma.shop.business.download.impl;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.common.constant.ResourceType;

/**
 * Created by LKF on 2019-6-28 0028.
 * 音响音效下载类
 */
public class HUSoundEffDownload extends SoundEffDownload {
    private static HUSoundEffDownload sInstance = new HUSoundEffDownload(ResourceType.VEHICLE_SOUND);

    public static HUSoundEffDownload getInstance() {
        return sInstance;
    }

    private HUSoundEffDownload(@ResourceType  int resType) {
        super(resType);
    }

    @Override
    protected String getDownloadDir() {
        return ConfigManager.FileConfig.getHUSoundEffDownloadFolder().getPath();
    }
}
