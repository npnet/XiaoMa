package com.xiaoma.shop.business.tts;

import com.xiaoma.cariflytek.tts.XttsWork;
import com.xiaoma.shop.business.download.BaseDownload;
import com.xiaoma.shop.business.model.SkusBean;

/**
 * Created by LKF on 2019-7-1 0001.
 */
public class TTSDownload extends BaseDownload<SkusBean> {
    private static final TTSDownload sInstance = new TTSDownload();

    public static TTSDownload getInstance() {
        return sInstance;
    }

    private TTSDownload() {
    }

    @Override
    public String getDownloadDir() {
        return XttsWork.getResZipPath();
    }

    @Override
    public String getDownloadUrl(SkusBean model) {
        return model != null ? model.getTtsResDownloadUrl() : "";
    }

    @Override
    protected String getModelId(SkusBean model) {
        return String.valueOf(model != null ? model.getId() : "");
    }
}
