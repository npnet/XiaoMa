package com.xiaoma.music.search.manager;

import android.text.TextUtils;

import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/8 0008
 */
public class SearchMusicManager implements ISearchMusic {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_COUNT = 200;

    public static SearchMusicManager getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void searchArtistFromUsb(String artist, ResultCallBack<List<UsbMusic>> callBack) {
        List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryUsbMusicByArtist(artist);
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    @Override
    public void searchAllFromUsb(ResultCallBack<List<UsbMusic>> callBack) {
        List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryAllUsbMusic();
        KLog.d("XM_LOG_" + "searchAllFromUsb: "+usbMusicList.size());
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    @Override
    public void searchArtistFromKw(String artist, ResultCallBack<List<XMMusic>> callBack) {
        OnlineMusicFactory.getKWAudioFetch().search(artist, IKuwoConstant.ISearchType.MUSIC, DEFAULT_PAGE, DEFAULT_COUNT,
                new OnAudioFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> xmMusics) {
                        callBack.onSuccess(xmMusics);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        callBack.onFailed(msg);
                    }
                });
    }

    @Override
    public void searchAlbumFromKw(String album, ResultCallBack<List<XMAlbumInfo>> callBack) {
        OnlineMusicFactory.getKWAudioFetch().search(album, IKuwoConstant.ISearchType.ALBUM, DEFAULT_PAGE, DEFAULT_COUNT,
                new OnAudioFetchListener<List<XMAlbumInfo>>() {
                    @Override
                    public void onFetchSuccess(List<XMAlbumInfo> albumInfoList) {
                        callBack.onSuccess(albumInfoList);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        callBack.onFailed(msg);
                    }
                });

    }


    @Override
    public void searchArtistAndAlbumFromKw(String artist, String album, ResultCallBack<List<XMAlbumInfo>> callBack) {
        OnlineMusicFactory.getKWAudioFetch().search(artist, IKuwoConstant.ISearchType.ALBUM, DEFAULT_PAGE, DEFAULT_COUNT,
                new OnAudioFetchListener<List<XMAlbumInfo>>() {
                    @Override
                    public void onFetchSuccess(List<XMAlbumInfo> albumInfoList) {
                        List<XMAlbumInfo> listInfos = new ArrayList<>();
                        for (XMAlbumInfo albumInfo : albumInfoList) {
                            if (albumInfo == null || albumInfo.getSDKBean() == null) {
                                continue;
                            }
                            if (!TextUtils.isEmpty(album)) {
                                if (albumInfo.getSDKBean().getName().equals(album)) {
                                    listInfos.add(albumInfo);
                                }
                            } else {
                                listInfos.add(albumInfo);
                            }
                        }
                        callBack.onSuccess(listInfos);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        callBack.onFailed(msg);
                    }
                });
    }

    @Override
    public void searchAlbumFromUsb(String album, ResultCallBack<List<UsbMusic>> callBack) {
        final List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryUsbMusicByAlbum(album);
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    @Override
    public void searchArtistAndAlbumFromUsb(String artist, String album, ResultCallBack<List<UsbMusic>> callBack) {
        final List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryUsbMusicByAlbum(artist, album);
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    @Override
    public void searchNameAndArtistFromKw(String name, String artist, ResultCallBack<List<XMMusic>> callBack) {
        OnlineMusicFactory.getKWAudioFetch().search(name + " " + artist, IKuwoConstant.ISearchType.MUSIC, DEFAULT_PAGE, DEFAULT_COUNT,
                new OnAudioFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> o) {
                        callBack.onSuccess(o);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        callBack.onFailed(msg);
                    }
                });
    }

    @Override
    public void searchNameAndArtistFromUsb(String name, String artist, ResultCallBack<List<UsbMusic>> callBack) {
        final List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryUsbMusicByArtistAndName(artist, name);
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    @Override
    public void searchByNameFromKw(String name, ResultCallBack<List<XMMusic>> callBack) {
        OnlineMusicFactory.getKWAudioFetch().search(name, IKuwoConstant.ISearchType.MUSIC, DEFAULT_PAGE, DEFAULT_COUNT,
                new OnAudioFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> o) {
                        callBack.onSuccess(o);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        callBack.onFailed(msg);
                    }
                });
    }

    @Override
    public void searchByNameFromUsb(String name, ResultCallBack<List<UsbMusic>> callBack) {
        final List<UsbMusic> usbMusicList = MusicDbManager.getInstance().queryUsbMusicByName(name);
        if (!ListUtils.isEmpty(usbMusicList)) {
            callBack.onSuccess(usbMusicList);
        } else {
            callBack.onFailed("result is empty");
        }
    }

    private static class InstanceHolder {
        static final SearchMusicManager instance = new SearchMusicManager();
    }


}
