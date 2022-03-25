package com.xiaoma.launcher.common.image;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.xiaoma.player.AudioInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by ZYao.
 * Date ：2018/12/18 0018
 */
class UsbImageFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "UsbImageFetcher";
    private AudioInfo music;

    UsbImageFetcher(AudioInfo music) {
        this.music = music;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        MediaMetadataRetriever retriever = null;
        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(music.getUsbMusicPath());
            final byte[] embeddedPicture = retriever.getEmbeddedPicture();
            callback.onDataReady(new ByteArrayInputStream(embeddedPicture));
        } catch (Exception e) {
            callback.onLoadFailed(e);
            e.printStackTrace();
        } finally {
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void cleanup() {
        // Do nothing.
    }

    @Override
    public void cancel() {
        // Do nothing.
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
