package com.xiaoma.music.kuwo.proxy;

import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.impl.IKuwoFetchList;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.manager.KuwoAudioFetchWrapper;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMCategoryListInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMSongListInfo;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMKWAudioFetchProxy implements IKuwoFetchList {

    public static XMKWAudioFetchProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMKWAudioFetchProxy instance = new XMKWAudioFetchProxy();
    }


    private IKuwoFetchList kwFetchList;

    private XMKWAudioFetchProxy() {
        kwFetchList = new KuwoAudioFetchWrapper();
    }

    @Override
    public void fetchRecommendSongList(OnAudioFetchListener<List<XMBaseQukuItem>> listener) {
        kwFetchList.fetchRecommendSongList(listener);
    }

    @Override
    public void fetchMusicCategories(OnAudioFetchListener<List<XMBaseQukuItem>> listener) {
        kwFetchList.fetchMusicCategories(listener);
    }

    @Override
    public void fetchBillBroad(OnAudioFetchListener<List<XMBillboardInfo>> listener) {
        kwFetchList.fetchBillBroad(listener);
    }

    @Override
    public void fetchAllArtist(boolean isOrderByHot, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchAllArtist(isOrderByHot, page, count, listener);
    }

    @Override
    public void fetchArtistByType(int type, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchArtistByType(type, page, count, listener);
    }

    @Override
    public void fetchRadio(OnAudioFetchListener listener) {
        kwFetchList.fetchRadio(listener);
    }

    @Override
    public void search(String key, int type, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.search(key, type, page, count, listener);
    }

    @Override
    public void fetchDailyRecommend(OnAudioFetchListener listener) {
        kwFetchList.fetchDailyRecommend(listener);
    }

    @Override
    public void fetchCategoryListMusic(XMCategoryListInfo item, int page, int count, OnAudioFetchListener<List<XMSongListInfo>> listener) {
        kwFetchList.fetchCategoryListMusic(item, page, count, listener);
    }

    @Override
    public void fetchSongListMusic(XMSongListInfo item, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchSongListMusic(item, page, count, listener);
    }

    @Override
    public void fetchBillboardMusic(XMBillboardInfo item, int page, int count, OnAudioFetchListener<List<XMMusic>> listener) {
        kwFetchList.fetchBillboardMusic(item, page, count, listener);
    }

    @Override
    public void fetchArtistMusic(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchArtistMusic(artist, page, count, listener);
    }

    @Override
    public void fetchArtistAlbum(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchArtistAlbum(artist, page, count, listener);
    }

    @Override
    public void fetchAlbumMusic(XMAlbumInfo album, int page, int count, OnAudioFetchListener<List<XMMusic>> listener) {
        kwFetchList.fetchAlbumMusic(album, page, count, listener);
    }

    @Override
    public void fetchSimilarSong(XMMusic pMusic, int pMusicCount, OnAudioFetchListener listener) {
        kwFetchList.fetchSimilarSong(pMusic, pMusicCount, listener);
    }

    @Override
    public void fetchLyric(XMMusic music, OnAudioFetchListener<String> listener) {
        kwFetchList.fetchLyric(music, listener);
    }

    @Override
    public void fetchImage(XMMusic pMusic, OnAudioFetchListener<String> pListener, int pSize) {
        kwFetchList.fetchImage(pMusic, pListener, pSize);
    }

    @Override
    public void fetchSearchHotKeywords(OnAudioFetchListener<List<String>> listener) {
        kwFetchList.fetchSearchHotKeywords(listener);
    }

    @Override
    public void fetchHotSongList(int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchHotSongList(page, count, listener);
    }

    @Override
    public void fetchNewSongList(int page, int count, OnAudioFetchListener listener) {
        kwFetchList.fetchNewSongList(page, count, listener);
    }

    @Override
    public void fetchSearchTips(String keywords, OnAudioFetchListener listener) {
        kwFetchList.fetchSearchTips(keywords, listener);
    }

    @Override
    public void fetchMusicById(long id, OnAudioFetchListener<XMMusic> listener) {
        kwFetchList.fetchMusicById(id, listener);
    }

    @Override
    public void fetchMusicByIds(List<Long> ids, OnAudioFetchListener<List<XMMusic>> listener) {
        kwFetchList.fetchMusicByIds(ids, listener);
    }

    public void fetchSmallImag(XMMusic music, OnAudioFetchListener<String> listener) {
        this.fetchImage(music, listener, IKuwoConstant.IImageSize.SIZE_120);
    }

    public void fetchMiddleImage(XMMusic music, OnAudioFetchListener<String> listener) {
        this.fetchImage(music, listener, IKuwoConstant.IImageSize.SIZE_320);
    }

}
