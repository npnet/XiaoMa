package com.xiaoma.shop.business.download;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.xiaoma.component.AppHolder;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.MD5Utils;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LKF on 2019-6-26 0026.
 * 下载业务抽象类
 */
public abstract class BaseDownload<T> {
    @SuppressLint("StaticFieldLeak")
    private static final Context APP_CONTEXT = AppHolder.getInstance().getAppContext();
    protected final String TAG = getClass().getSimpleName();
    private final Set<DownloadListener> mDownloadListeners = new ArraySet<>();
    private final Map<String, DownloadStatus> mDownloadStatusMap = new ConcurrentHashMap<>();

    abstract protected String getDownloadDir();

    abstract protected String getDownloadUrl(T model);

    abstract protected String getModelId(T model);

    public Map<String, DownloadStatus> getmDownloadStatusMap() {
        return mDownloadStatusMap;
    }

    public String getDownloadFileName(T model) {
        String url = getDownloadUrl(model);
        String fileName = MD5Utils.getMD5String(url);
        if (TextUtils.isEmpty(fileName)) {
            fileName = getModelId(model);
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis());
        }
        int postfixIdx = url.lastIndexOf(".");
        if (postfixIdx >= 0) {
            fileName += (url.substring(postfixIdx));
        }
        return fileName;
    }

    /**
     * 开始下载
     */
    public boolean start(final T model) {
        if (model == null)
            return false;
        String url = getDownloadUrl(model);
        if (!URLUtil.isValidUrl(url))
            return false;
        if (!isDownloading(model)) {
            XmHttp.getDefault().getFile(url, null, url, new DownFileCallback(model));
        }
        return true;
    }

    /**
     * 移除下载,如果下载已完成,会删除已下载的文件
     */
    @SafeVarargs
    public final int remove(T... models) {
        int count = 0;
        for (final T model : models) {
            String url = getDownloadUrl(model);
            if (TextUtils.isEmpty(url))
                continue;
            // 取消下载
            XmHttp.getDefault().cancelTag(url);
            // 删除已下载的文件
            DownloadStatus status = getDownloadStatusInternal(model);
            if (status != null) {
                File downFile;
                if (!TextUtils.isEmpty(status.downFilePath)
                        && (downFile = new File(status.downFilePath)).exists()) {
                    FileUtils.delete(downFile);
                }
                ++count;
            }
            mDownloadStatusMap.remove(url);
        }
        return count;
    }

    /**
     * 获取下载状态
     *
     * @return {@link DownloadManager}  常量定义为 STATUS_*
     */
    @Nullable
    public DownloadStatus getDownloadStatus(T model) {
        return getDownloadStatusInternal(model);
    }

    public boolean isDownloadSuccess(T model) {
        DownloadStatus downloadStatus = getDownloadStatusInternal(model);
        return downloadStatus != null
                && DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status
                && !TextUtils.isEmpty(downloadStatus.downFilePath)
                && new File(downloadStatus.downFilePath).exists();
    }

    public File getDownloadFile(T model) {
        File downFile = null;
        DownloadStatus downloadStatus = getDownloadStatusInternal(model);
        if (downloadStatus != null
                && DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status
                && !TextUtils.isEmpty(downloadStatus.downFilePath)) {
            downFile = new File(downloadStatus.downFilePath);
            if (!downFile.exists())
                downFile = null;
        }
        return downFile;
    }

    public boolean isDownloading(T model) {
        DownloadStatus downloadStatus = getDownloadStatusInternal(model);
        return downloadStatus != null
                && DownloadManager.STATUS_RUNNING == downloadStatus.status;
    }

    /**
     * 监听下载变化
     */
    public void addDownloadListener(final DownloadListener listener) {
        Log.i(TAG, "addDownloadListener: " + listener);
        if (listener == null)
            return;
        mDownloadListeners.add(listener);
    }

    public void removeDownloadListener(final DownloadListener listener) {
        Log.i(TAG, "removeDownloadListener: " + listener);
        if (listener == null)
            return;
        mDownloadListeners.remove(listener);
    }

    @Nullable
    private DownloadStatus getDownloadStatusInternal(T model) {
        if (model == null)
            return null;
        String url = getDownloadUrl(model);
        if (TextUtils.isEmpty(url))
            return null;
        DownloadStatus downloading = mDownloadStatusMap.get(url);
        if (downloading != null)
            return new DownloadStatus(downloading);
        File downFile = new File(getDownloadDir(), getDownloadFileName(model));
        if (downFile.exists()) {
            DownloadStatus downloadStatus = new DownloadStatus();
            downloadStatus.downFilePath = downFile.getPath();
            downloadStatus.downUrl = url;
            downloadStatus.status = DownloadManager.STATUS_SUCCESSFUL;
            downloadStatus.currentLength = downloadStatus.totalLength = downFile.length();
            return downloadStatus;
        }
        return null;
    }

    public static String getDownloadStatusStr(int status) {
        String statusStr;
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusStr = "FAILED";
                break;
            case DownloadManager.STATUS_PAUSED:
                statusStr = "PAUSED";
                break;
            case DownloadManager.STATUS_PENDING:
                statusStr = "PENDING:";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusStr = "RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusStr = "SUCCESSFUL";
                break;
            default:
                statusStr = "UNKNOWN";
                break;
        }
        return statusStr;
    }

    private class DownFileCallback extends FileCallback {
        private T model;
        private final Map<String, ServiceConnection> mDownloadingConn = new ArrayMap<>();

        DownFileCallback(T model) {
            this.model = model;
        }

        @Override
        public void onStart() {
            DownloadStatus downloadStatus = genDownloadStatus();
            downloadStatus.status = DownloadManager.STATUS_RUNNING;
            downloadStatus.currentLength = 0;
            downloadStatus.totalLength = -1;

            Set<DownloadListener> listeners = new ArraySet<>(mDownloadListeners);
            for (final DownloadListener listener : listeners) {
                listener.onDownloadStatus(new DownloadStatus(downloadStatus));
            }

            String id = getModelId(model);
            if (!mDownloadingConn.containsKey(id)) {
                ServiceConnection conn = new ConnImpl();
                APP_CONTEXT.bindService(new Intent(APP_CONTEXT, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
                mDownloadingConn.put(id, conn);
            }

            Log.i(TAG, String.format("onStart: { url: %s }", downloadStatus.downUrl));
        }

        @Override
        public void onProgress(long currLength, long totalLength) {
            DownloadStatus downloadStatus = genDownloadStatus();
            long lastProgress = downloadStatus.currentLength * 100 / downloadStatus.totalLength;
            downloadStatus.status = DownloadManager.STATUS_RUNNING;
            downloadStatus.currentLength = currLength;
            downloadStatus.totalLength = totalLength;
            long curProgress = currLength * 100 / totalLength;
            // 下载进度百分比变化了才回调,避免回调太频繁导致卡顿;下载完成100%也直接回调
            if (lastProgress != curProgress || currLength == totalLength) {
                Set<DownloadListener> listeners = new ArraySet<>(mDownloadListeners);
                for (final DownloadListener listener : listeners) {
                    listener.onDownloadStatus(new DownloadStatus(downloadStatus));
                }
            }
            Log.i(TAG, String.format("onProgress: { currLength: %s, totalLength: %s }", currLength, totalLength));
        }

        @Override
        public void onError(Response<File> response) {
            DownloadStatus downloadStatus = genDownloadStatus();
            downloadStatus.status = DownloadManager.STATUS_FAILED;

            Set<DownloadListener> listeners = new ArraySet<>(mDownloadListeners);
            for (final DownloadListener listener : listeners) {
                listener.onDownloadStatus(new DownloadStatus(downloadStatus));
            }
            Log.e(TAG, String.format("onError: { code: %s, url: %s }", response.code(), downloadStatus.downUrl));
        }

        @Override
        public void onSuccess(Response<File> response) {
            DownloadStatus downloadStatus = genDownloadStatus();
            File bodyFile = response.body();
            File cpyToFile = new File(getDownloadDir(), getDownloadFileName(model));
            FileUtils.delete(cpyToFile);
            if (FileUtils.copy(bodyFile, cpyToFile)) {
                downloadStatus.status = DownloadManager.STATUS_SUCCESSFUL;
                downloadStatus.downFilePath = cpyToFile.getPath();

                Set<DownloadListener> listeners = new ArraySet<>(mDownloadListeners);
                for (final DownloadListener listener : listeners) {
                    listener.onDownloadStatus(new DownloadStatus(downloadStatus));
                }
                Log.i(TAG, String.format("onSuccess: { downFile: %s, url: %s }", cpyToFile, downloadStatus.downUrl));
            } else {
                Log.e(TAG, String.format("onSuccess: COPY ERROR! { from: %s, to: %s, url: %s }", bodyFile, cpyToFile, downloadStatus.downUrl));
                onError(response);
            }
        }

        @Override
        public void onFinish() {
            Log.i(TAG, String.format("onFinish: { url: %s }", getDownloadUrl(model)));
            String id = getModelId(model);
            ServiceConnection conn = mDownloadingConn.remove(id);
            if (conn != null) {
                APP_CONTEXT.unbindService(conn);
            }
            mDownloadStatusMap.remove(getDownloadUrl(model));
        }

        private DownloadStatus genDownloadStatus() {
            String url = getDownloadUrl(model);
            DownloadStatus downloadStatus = mDownloadStatusMap.get(url);
            if (downloadStatus == null) {
                downloadStatus = new DownloadStatus();
                downloadStatus.downUrl = url;
                mDownloadStatusMap.put(url, downloadStatus);
            }
            return downloadStatus;
        }
    }

    private static class ConnImpl implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ConnImpl", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ConnImpl", "onServiceDisconnected");
        }
    }
}
