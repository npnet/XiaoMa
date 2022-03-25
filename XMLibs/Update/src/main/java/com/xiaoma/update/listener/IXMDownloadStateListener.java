package com.xiaoma.update.listener;

import java.io.File;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/9/17
 *  description :
 * </pre>
 */
public interface IXMDownloadStateListener {

    void onDownloadStart();

    void onDownloading(long progress, long total);

    void onDownloadError(String errorMsg);

    void onDownloadFinish(File apkFile);

}
