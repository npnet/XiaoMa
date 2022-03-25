package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.export.model.RankInfo;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.search.manager.ResultCallBack;
import com.xiaoma.music.search.manager.SearchMusicManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.BillboardInfo;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class SearchByMusicNameHandler extends BaseRequestInterceptHandler {

    public SearchByMusicNameHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {
        String name = data.getString(AudioConstants.BundleKey.SONG);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        boolean want = data.getBoolean(AudioConstants.BundleKey.WANT, true);
        if (!want && !TextUtils.isEmpty(artist)) {
            playBillboard(client, action, artist);
            return;
        }
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(artist)) {
            searchNameAndArtistFromKw(client, action, name, artist);
        } else if (!TextUtils.isEmpty(name) && TextUtils.isEmpty(artist)) {
            searchByNameFromKw(client, action, name);
        } else if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(artist)) {
            searchArtistFromKw(client, action, artist);
        } else {
            client.dispatcherFailedCallback(action, clientCallback);
        }
    }

    private void playBillboard(AssistantClient client, int action, String name) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                RequestManager.getInstance().searchMusicByKeyWord(KUWO_BILLBOARD_TYPE, "酷我新歌榜", 0, 30, new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        XMResult<RankInfo> result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<RankInfo>>() {
                        }.getType());
                        if (result == null || !result.isSuccess()) {
                            client.dispatcherFailedCallback(action, clientCallback);
                            return;
                        }
                        RankInfo rankInfo = result.getData();
                        BillboardInfo billboardInfo = new BillboardInfo();
                        billboardInfo.setId(String.valueOf(rankInfo.getRankId()));
                        billboardInfo.setName(rankInfo.getRankName());
                        billboardInfo.setDigest(String.valueOf(rankInfo.getDigest()));
                        XMBillboardInfo xmBillboardInfo = new XMBillboardInfo(billboardInfo);
                        OnlineMusicFactory.getKWAudioFetch().fetchBillboardMusic(xmBillboardInfo, 0, 30, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                            @Override
                            public void onFetchSuccess(List<XMMusic> musicList) {
                                for (XMMusic xmMusic : musicList) {
                                    if (!name.equals(xmMusic.getArtist())) {
                                        OnlineMusicFactory.getKWPlayer().play(xmMusic);
                                        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                                        KwPlayInfoManager.getInstance().setCurrentPlayInfo(xmBillboardInfo.getSDKBean().getName()
                                                + xmBillboardInfo.getSDKBean().getId(), KwPlayInfoManager.AlbumType.ASSISTANT);
                                        client.dispatcherSuccessCallback(action, clientCallback);
                                        break;
                                    }
                                }


                            }

                            @Override
                            public void onFetchFailed(String msg) {
                                client.dispatcherFailedCallback(action, clientCallback);
                            }
                        });
                    }
                });
            }
        });
    }

    private void searchArtistFromKw(AssistantClient client, int action, String artist) {
        SearchMusicManager.getInstance().searchArtistFromKw(artist, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> musicList) {
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                KwPlayInfoManager.getInstance().setCurrentPlayInfo(artist, KwPlayInfoManager.AlbumType.ASSISTANT);
                client.dispatcherSuccessCallback(action, clientCallback);
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void searchByNameFromKw(AssistantClient client, int action, String name) {
        SearchMusicManager.getInstance().searchByNameFromKw(name, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> musicList) {
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                KwPlayInfoManager.getInstance().setCurrentPlayInfo(name, KwPlayInfoManager.AlbumType.ASSISTANT);
                client.dispatcherSuccessCallback(action, clientCallback);
            }

            @Override
            public void onFailed(String msg) {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        });
    }

    private void searchNameAndArtistFromKw(AssistantClient client, int action, String name, String artist) {
        SearchMusicManager.getInstance().searchNameAndArtistFromKw(name, artist, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> musicList) {
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                KwPlayInfoManager.getInstance().setCurrentPlayInfo(name + artist, KwPlayInfoManager.AlbumType.ASSISTANT);
                client.dispatcherSuccessCallback(action, clientCallback);
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

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {
        String name = data.getString(AudioConstants.BundleKey.SONG);
        String artist = data.getString(AudioConstants.BundleKey.SINGER);
        boolean want = data.getBoolean(AudioConstants.BundleKey.WANT, true);
        if (!want) { //不想听某人的歌
            searchExcludeArtistFromUsb(client, action, artist);
        } else {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(artist)) {
                searchNameAndArtistFromUsb(client, action, name, artist);
            } else if (!TextUtils.isEmpty(name) && TextUtils.isEmpty(artist)) {
                searchByNameFromUsb(client, action, name);
            } else if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(artist)) {
                searchArtistFromUsb(client, action, artist);
            } else {
                client.dispatcherFailedCallback(action, clientCallback);
            }
        }
    }

    private void searchExcludeArtistFromUsb(AssistantClient client, int action, String artist) {
        SearchMusicManager.getInstance().searchAllFromUsb(new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                ArrayList<UsbMusic> list = new ArrayList<>();
                for (UsbMusic usbMusic : usbMusics) {
                    if (!TextUtils.isEmpty(usbMusic.getArtist()) && !TextUtils.equals(usbMusic.getArtist(), artist)) {
                        list.add(usbMusic);
                    }
                }
                if (!ListUtils.isEmpty(list)) {
                    playUsbMusic(list, client, action);
                } else {
                    if (NetworkUtils.isConnected(mContext)) {
                        playBillboard(client, action, artist);
                    } else {
                        client.dispatcherFailedCallback(action, clientCallback);
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    playBillboard(client, action, artist);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
            }
        });
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

    private void searchByNameFromUsb(AssistantClient client, int action, String name) {
        SearchMusicManager.getInstance().searchByNameFromUsb(name, new ResultCallBack<List<UsbMusic>>() {
            @Override
            public void onSuccess(List<UsbMusic> usbMusics) {
                playUsbMusic(usbMusics, client, action);
            }

            @Override
            public void onFailed(String msg) {
                if (NetworkUtils.isConnected(mContext)) {
                    searchByNameFromKw(client, action, name);
                } else {
                    client.dispatcherFailedCallback(action, clientCallback);
                }
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
}
