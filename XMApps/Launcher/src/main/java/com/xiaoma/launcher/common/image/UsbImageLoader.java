package com.xiaoma.launcher.common.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.xiaoma.player.AudioInfo;

import java.io.InputStream;

/**
 * Created by ZYao.
 * Date ：2018/12/18 0018
 */
public class UsbImageLoader implements ModelLoader<AudioInfo, InputStream> {
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull AudioInfo music, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(music), new UsbImageFetcher(music));
    }

    @Override
    public boolean handles(@NonNull AudioInfo music) {
        final boolean handle = !TextUtils.isEmpty(music.getUsbMusicPath());
        return handle;
    }

    public static class Factory implements ModelLoaderFactory<AudioInfo, InputStream> {
        @NonNull
        @Override
        public ModelLoader<AudioInfo, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new UsbImageLoader();
        }

        @Override
        public void teardown() {
        }
    }
}
