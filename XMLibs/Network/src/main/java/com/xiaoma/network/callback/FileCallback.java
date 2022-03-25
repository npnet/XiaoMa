package com.xiaoma.network.callback;

import com.xiaoma.network.model.Response;

import java.io.File;

public abstract class FileCallback implements BaseCallback<File> {
    private String mDestFileDir;
    private String mDestFileName;

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFileName) {
        this(null, destFileName);
    }

    public FileCallback(String destFileDir, String destFileName) {
        mDestFileDir = destFileDir;
        mDestFileName = destFileName;
    }

    /**
     * 请求网络开始前，UI线程
     */
    public void onStart() {}

    /**
     * 上传/下载进度
     *
     * @param currLength  当前读写长度
     * @param totalLength 总长度
     */
    public void onProgress(long currLength, long totalLength) {}

    /**
     * 对返回数据进行操作的回调， UI线程
     */
    public void onSuccess(Response<File> response) {}

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程
     */
    public void onError(Response<File> response) {}

    /**
     * 请求网络结束后
     */
    public void onFinish() {}

    public String getDestFileDir() {
        return mDestFileDir;
    }

    public String getDestFileName() {
        return mDestFileName;
    }
}
