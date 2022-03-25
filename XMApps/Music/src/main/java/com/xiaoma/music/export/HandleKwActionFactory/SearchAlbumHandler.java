package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.search.manager.ResultCallBack;
import com.xiaoma.music.search.manager.SearchMusicManager;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class SearchAlbumHandler extends BaseRequestInterceptHandler {

    public SearchAlbumHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {
        String album = data.getString(AudioConstants.BundleKey.ALBUM);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        if (!TextUtils.isEmpty(artist)) {
            fetchAndPlayKwAlbum(artist, album, client, action);
        } else {
            fetchAndPlayKwAlbum(album, client, action);
        }
    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {
        String album = data.getString(AudioConstants.BundleKey.ALBUM);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        if (!TextUtils.isEmpty(artist)) {
            searchArtistAndAlbumFromUsb(client, action, album, artist);
        } else {
            searchAlbumFromUsb(client, action, album);
        }
    }

    private void searchAlbumFromUsb(AssistantClient client, int action, String album) {
        SearchMusicManager.getInstance().searchAlbumFromUsb(album, new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                playUsbMusic(usbMusics, client, action);
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    fetchAndPlayKwAlbum(album, client, action);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }
        });
    }

    private void fetchAndPlayKwAlbum(String album, AssistantClient client, int action) {
        SearchMusicManager.getInstance().searchAlbumFromKw(album, new ResultCallBack<List<XMAlbumInfo>>() {
            @Override
            public void onSuccess(List<XMAlbumInfo> albumInfoList) {
                playAlbum(albumInfoList, client, action);
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void playUsbMusic(List<UsbMusic> usbMusics, AssistantClient client, int action) {
        UsbMusicFactory.getUsbPlayerProxy().play(usbMusics.get(0));
        UsbMusicFactory.getUsbPlayerListProxy().addUsbMusicList(usbMusics);
        client.dispatcherSuccessCallback(action, clientCallback);
    }

    private void searchArtistAndAlbumFromUsb(AssistantClient client, int action, String album, String artist) {
        SearchMusicManager.getInstance().searchArtistAndAlbumFromUsb(artist, album, new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                playUsbMusic(usbMusics, client, action);
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    fetchAndPlayKwAlbum(artist, album, client, action);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }
        });
    }

    private void fetchAndPlayKwAlbum(String artist, String album, AssistantClient client, int action) {
        SearchMusicManager.getInstance().searchArtistAndAlbumFromKw(artist, album, new ResultCallBack<List<XMAlbumInfo>>() {
            @Override
            public void onSuccess(List<XMAlbumInfo> albumInfoList) {
                playAlbum(albumInfoList, client, action);
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void playAlbum(List<XMAlbumInfo> albumInfoList, AssistantClient client, int action) {
        if (ListUtils.isEmpty(albumInfoList)) {
            client.dispatcherFailedCallback(action, clientCallback);
            return;
        }
        final XMAlbumInfo albumInfo = albumInfoList.get(0);
        OnlineMusicFactory.getKWAudioFetch().fetchAlbumMusic(albumInfo, 0, 300,
                new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> musicList) {
                        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                        OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                        KwPlayInfoManager.getInstance().setCurrentPlayInfo(albumInfo.getArtistID()
                                + albumInfo.getArtist(), KwPlayInfoManager.AlbumType.ASSISTANT);
                        client.dispatcherSuccessCallback(action, clientCallback);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        client.dispatcherFailedCallback(action, clientCallback);
                    }
                });
    }
}
