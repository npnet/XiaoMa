package com.xiaoma.music.kuwo.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.xiaoma.music.model.UsbMusic;

import java.io.InputStream;

/**
 * Created by ZYao.
 * Date ：2018/12/18 0018
 */
public class UsbImageLoader implements ModelLoader<UsbMusic, InputStream> {
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull UsbMusic music, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(music), new UsbImageFetcher(music));
    }

    @Override
    public boolean handles(@NonNull UsbMusic music) {
        final boolean handle = !TextUtils.isEmpty(music.getPath());
        return handle;
    }

    public static class Factory implements ModelLoaderFactory<UsbMusic, InputStream> {
        @NonNull
        @Override
        public ModelLoader<UsbMusic, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new UsbImageLoader();
        }

        @Override
        public void teardown() {
        }
    }
}
