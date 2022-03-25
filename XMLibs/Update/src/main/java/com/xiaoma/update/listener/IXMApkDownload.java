package com.xiaoma.update.listener;

import java.io.File;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/9/17
 *  description :
 * </pre>
 */
public interface IXMApkDownload<T, LISTENER> {

    void startDownload(T t, LISTENER listener);

    void cancelDownload(String tag, boolean deleteFileTF);

    void deleteApkFile(File apkFile);
}
