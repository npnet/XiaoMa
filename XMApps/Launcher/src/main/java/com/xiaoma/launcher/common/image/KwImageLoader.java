package com.xiaoma.launcher.common.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.xiaoma.launcher.player.callback.ImageResultCallBack;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.utils.log.KLog;

import java.io.InputStream;

/**
 * Created by LKF on 2018/10/24 0024.
 */
public class KwImageLoader extends BaseGlideUrlLoader<AudioInfo> {
    private String mUrl;

    protected KwImageLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<AudioInfo, GlideUrl> modelCache) {
        super(concreteLoader, modelCache);
    }

    @Override
    protected String getUrl(AudioInfo audioInfo, int width, int height, Options options) {
        final Object lock = new Object();
        synchronized (lock) {
            KLog.d("kw image" + " getUrl");
            PlayerConnectHelper.getInstance().fetchKwImage(audioInfo, new ImageResultCallBack() {
                @Override
                public void onFetchImageSuccess(String url) {
                    synchronized (lock) {
                        if (url == null) {
                            url = "url is empty";
                        }
                        KLog.d("kw image" + url);
                        mUrl = url;
                        lock.notify();
                    }
                }
            });
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mUrl;
    }

    @Override
    public boolean handles(@NonNull AudioInfo audioInfo) {
        return audioInfo.getUniqueId() != 0;
    }

    public static class Factory implements ModelLoaderFactory<AudioInfo, InputStream> {
        private static ModelCache<AudioInfo, GlideUrl> sUrlCache = new ModelCache<>();

        @NonNull
        @Override
        public ModelLoader<AudioInfo, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new KwImageLoader(multiFactory.build(GlideUrl.class, InputStream.class), sUrlCache);
        }

        @Override
        public void teardown() {
        }
    }
}