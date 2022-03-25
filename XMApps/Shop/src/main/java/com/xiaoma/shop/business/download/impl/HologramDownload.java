package com.xiaoma.shop.business.download.impl;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.business.download.BaseDownload;
import com.xiaoma.shop.business.model.HoloListModel;

/**
 * Author: loren
 * Date: 2019/8/13
 */
public class HologramDownload extends BaseDownload<HoloListModel> {
    //自定义的重命名
    public static final String FILE_HOLOGRAM_3D = "CustomAsset.zip";

    public static final String DEF_NONE = "none";

    private static HologramDownload sDownload;

    public static HologramDownload newSingleton() {
        if (sDownload == null) {
            synchronized (HologramDownload.class) {
                if (sDownload == null) {
                    sDownload = new HologramDownload();
                }
            }
        }
        return sDownload;
    }

    @Override
    public String getDownloadDir() {
        return ConfigManager.FileConfig.getShopHologramDownlaodFolder().getPath();
    }

    @Override
    protected String getDownloadUrl(HoloListModel model) {
        return model.getCustomImageResourceUrl();
    }

    @Override
    protected String getModelId(HoloListModel model) {
        if (model != null) {
            return String.valueOf(model.getId());
        }
        return "";
    }
}
