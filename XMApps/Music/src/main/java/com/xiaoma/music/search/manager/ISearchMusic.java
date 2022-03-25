package com.xiaoma.music.search.manager;

import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.UsbMusic;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/8 0008
 */
public interface ISearchMusic {

    void searchArtistFromUsb(String artist, ResultCallBack<List<UsbMusic>> callBack);

    void searchArtistFromKw(String artist, ResultCallBack<List<XMMusic>> callBack);

    void searchAlbumFromKw(String album, ResultCallBack<List<XMAlbumInfo>> callBack);

    void searchArtistAndAlbumFromKw(String artist, String album, ResultCallBack<List<XMAlbumInfo>> callBack);

    void searchAlbumFromUsb(String album, ResultCallBack<List<UsbMusic>> callBack);

    void searchArtistAndAlbumFromUsb(String artist, String album, ResultCallBack<List<UsbMusic>> callBack);

    void searchNameAndArtistFromKw(String name, String artist, ResultCallBack<List<XMMusic>> callBack);

    void searchNameAndArtistFromUsb(String name, String artist, ResultCallBack<List<UsbMusic>> callBack);

    void searchByNameFromKw(String name, ResultCallBack<List<XMMusic>> callBack);

    void searchByNameFromUsb(String name, ResultCallBack<List<UsbMusic>> callBack);

    void searchAllFromUsb(ResultCallBack<List<UsbMusic>> callBack);

}
