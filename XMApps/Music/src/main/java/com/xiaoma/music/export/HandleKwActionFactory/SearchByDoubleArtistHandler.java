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
public class SearchByDoubleArtistHandler extends BaseRequestInterceptHandler {
    public SearchByDoubleArtistHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {
        String name = data.getString(AudioConstants.BundleKey.SONG);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        String chorus = data.getString(AudioConstants.BundleKey.CHORUS);
        String newArtist = artist + chorus;
        if (!TextUtils.isEmpty(name)) {
            searchNameAndArtistFromKw(client, action, name, newArtist);
        } else {
            searchArtistFromKw(client, action, newArtist);
        }
    }

    private void searchArtistFromUsb(AssistantClient client, int action, String artist) {
        SearchMusicManager.getInstance().searchArtistFromUsb(artist, new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                playUsbMusic(usbMusics, client, action);
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    searchArtistFromKw(client, action, artist);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }
        });
    }

    private void searchArtistFromKw(AssistantClient client, int action, String artist) {
        SearchMusicManager.getInstance().searchArtistFromKw(artist, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> musicList) {
                if (!ListUtils.isEmpty(musicList)) {
                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                    OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(artist, KwPlayInfoManager.AlbumType.ASSISTANT);
                    String musicName = musicList.get(0).getName();
                    callbackMusicNameSuccess(musicName,action);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void searchNameAndArtistFromUsb(AssistantClient client, int action, String name, String artist) {
        SearchMusicManager.getInstance().searchNameAndArtistFromUsb(name, artist, new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                playUsbMusic(usbMusics, client, action);
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    searchNameAndArtistFromKw(client, action, name, artist);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }
        });
    }

    private void searchNameAndArtistFromKw(AssistantClient client, int action, String name, String artist) {
        SearchMusicManager.getInstance().searchNameAndArtistFromKw(name, artist, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> musicList) {
                if (!ListUtils.isEmpty(musicList)) {
                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                    OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                    String musicName = musicList.get(0).getName();
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(musicName, KwPlayInfoManager.AlbumType.ASSISTANT);
                    callbackMusicNameSuccess(musicName,action);
                }
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void callbackMusicNameSuccess(String musicName, int action) {
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putInt(AudioConstants.BundleKey.ACTION, action);
            callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, AudioConstants.AudioResponseCode.SUCCESS);
            callbackData.putString(AudioConstants.BundleKey.RESULT, musicName);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

    private void playUsbMusic(List<UsbMusic> usbMusics, AssistantClient client, int action) {
        UsbMusicFactory.getUsbPlayerProxy().play(usbMusics.get(0));
        UsbMusicFactory.getUsbPlayerListProxy().addUsbMusicList(usbMusics);
        callbackMusicNameSuccess(usbMusics.get(0).getName(),action);
    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {
        String name = data.getString(AudioConstants.BundleKey.SONG);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        String chorus = data.getString(AudioConstants.BundleKey.CHORUS);
        String newArtist = artist + chorus;
        if (!TextUtils.isEmpty(name)) {
            searchNameAndArtistFromUsb(client, action, name, newArtist);
        } else {
            searchArtistFromUsb(client, action, newArtist);
        }
    }
}
