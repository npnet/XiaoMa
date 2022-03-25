package com.xiaoma.shop.business.download;

import android.support.annotation.Nullable;

/**
 * Created by LKF on 2019-6-26 0026.
 */
public interface DownloadListener {
    void onDownloadStatus(@Nullable DownloadStatus downloadStatus);
}
