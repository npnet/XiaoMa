package com.xiaoma.app.listener;


import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.download.DownloadListener;

/**
 * Created by zhushi.
 * Date: 2018/10/18
 */
public abstract class AppDownloadListener extends DownloadListener {

    public AppDownloadListener(Object tag) {
        super(tag);
    }


    @Override
    public void onError(Progress progress) {
        Throwable throwable = progress.exception;
        if (throwable != null){
            throwable.printStackTrace();
        }
    }


    @Override
    public void onRemove(Progress progress) {

    }

}
