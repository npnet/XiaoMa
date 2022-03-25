package com.xiaoma.shop.business.download.impl;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.common.constant.ResourceType;

/**
 * Created by LKF on 2019-6-28 0028.
 * 仪表音效下载类
 */
public class LCDSoundEffDownload extends SoundEffDownload {
    private static LCDSoundEffDownload sInstance = new LCDSoundEffDownload(ResourceType.INSTRUMENT_SOUND);

    public static LCDSoundEffDownload getInstance() {
        return sInstance;
    }

    private LCDSoundEffDownload(@ResourceType int resType) {
        super(resType);
    }

    @Override
    protected String getDownloadDir() {
        return ConfigManager.FileConfig.getLCDSoundEffDownloadFolder().getPath();
    }
}