package com.xiaoma.carpark.plugin.listener;

import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.download.DownloadListener;

import java.io.File;

public class PluginDownloadListener extends DownloadListener {
    public PluginDownloadListener(Object tag) {
        super(tag);
    }

    @Override
    public void onStart(Progress progress) {

    }

    @Override
    public void onProgress(Progress progress) {

    }

    @Override
    public void onError(Progress progress) {
        Throwable throwable = progress.exception;
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {

    }

    @Override
    public void onRemove(Progress progress) {

    }
}
