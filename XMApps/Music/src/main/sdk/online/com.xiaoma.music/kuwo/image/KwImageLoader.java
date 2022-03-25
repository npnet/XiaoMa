package com.xiaoma.music.kuwo.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;

import java.io.InputStream;

import cn.kuwo.base.bean.Music;
import cn.kuwo.mod.quku.QukuRequestState;
import cn.kuwo.open.KwApi;
import cn.kuwo.open.OnImageFetchListener;

/**
 * Created by LKF on 2018/10/24 0024.
 */
public class KwImageLoader extends BaseGlideUrlLoader<KwImage> {
    private String mUrl;

    private KwImageLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<KwImage, GlideUrl> modelCache) {
        super(concreteLoader, modelCache);
    }

    @Override
    protected String getUrl(KwImage image, int width, int height, Options options) {
        final Object lock = new Object();
        synchronized (lock) {
            KwApi.fetchImage(image.getMusic(), new OnImageFetchListener() {
                @Override
                public void onFetched(QukuRequestState state, String msg, String url) {
                    synchronized (lock) {
                        if (url == null) {
                            url = "url is empty";
                        }
                        mUrl = url;
                        lock.notify();
                    }
                }
            }, IKuwoConstant.getImageSize(image.getImageSize()));
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mUrl;
    }

    @Override
    public boolean handles(@NonNull KwImage image) {
        final Music music = image.getMusic();
        return music != null && music.rid != 0;
    }

    public static class Factory implements ModelLoaderFactory<KwImage, InputStream> {
        private static ModelCache<KwImage, GlideUrl> sUrlCache = new ModelCache<>();

        @NonNull
        @Override
        public ModelLoader<KwImage, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new KwImageLoader(multiFactory.build(GlideUrl.class, InputStream.class), sUrlCache);
        }

        @Override
        public void teardown() {

        }
    }
}