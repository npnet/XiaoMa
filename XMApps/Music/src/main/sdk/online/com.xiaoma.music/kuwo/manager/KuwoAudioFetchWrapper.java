package com.xiaoma.music.kuwo.manager;

import android.support.annotation.NonNull;

import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.handler.DispatchHandlerFactory;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.impl.IKuwoFetchList;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMCategoryListInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMOnlineRootInfo;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.online.OnlineRootInfo;
import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.RadioInfo;
import cn.kuwo.base.bean.quku.SongListInfo;
import cn.kuwo.mod.quku.QukuRequestState;
import cn.kuwo.open.ImageSize;
import cn.kuwo.open.KwApi;
import cn.kuwo.open.OnDailyRecommendFetchListener;
import cn.kuwo.open.OnHotKeywordsFetchListener;
import cn.kuwo.open.OnImageFetchListener;
import cn.kuwo.open.OnMusicFetchListener;
import cn.kuwo.open.OnMusicsFetchListener;
import cn.kuwo.open.OnSearchTipsSearchListener;
import cn.kuwo.open.base.ArtistType;
import cn.kuwo.open.base.SearchType;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class KuwoAudioFetchWrapper implements IKuwoFetchList {


    public static final int TIME_OUT_DELAY = 8000;
    private OnAudioFetchListener currentListener;

    @Override
    public void fetchRecommendSongList(OnAudioFetchListener<List<XMBaseQukuItem>> listener) {
        KwApi.fetchRecommendSongList(getRecommendFetchListener(listener));
    }

    @Override
    public void fetchMusicCategories(OnAudioFetchListener<List<XMBaseQukuItem>> listener) {
        KwApi.fetchMusicCategories(getFetchListener(listener));
    }

    @Override
    public void fetchBillBroad(OnAudioFetchListener<List<XMBillboardInfo>> listener) {
        KwApi.fetchBillBroad(getFetchListener(listener));
    }

    @Override
    public void fetchAllArtist(boolean isOrderByHot, int page, int count, OnAudioFetchListener listener) {
        KwApi.fetchAllArtist(isOrderByHot, page, count, getFetchListener(listener));
    }

    @Override
    public void fetchArtistByType(@IKuwoConstant.IArtistType int type, int page, int count, OnAudioFetchListener listener) {
        ArtistType artistType = IKuwoConstant.getArtistType(type);
        if (artistType != null) {
            KwApi.fetchArtistByType(artistType, page, count, getFetchListener(listener));
        }
    }

    @Override
    public void fetchRadio(OnAudioFetchListener listener) {
        KwApi.fetchRadio(getFetchListener(listener));
    }

    @Override
    public void search(String key, @IKuwoConstant.ISearchType int type, int page, int count, OnAudioFetchListener listener) {
        SearchType searchType = IKuwoConstant.getSearchType(type);
        if (searchType != null && key != null) {
            KwApi.search(key, searchType, page, count, getFetchListener(listener));
        } else {
            listener.onFetchFailed("input is empty");
        }
    }

    @Override
    public void fetchDailyRecommend(final OnAudioFetchListener listener) {
        KwApi.fetchDailyRecommend(getOnDailyRecommendFetchListener(listener));
    }

    @Override
    public void fetchCategoryListMusic(XMCategoryListInfo item, int page, int count, OnAudioFetchListener<List<XMSongListInfo>> listener) {
        if (item == null || item.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetch(item.getSDKBean(), page, count, getFetchListener(listener));
    }

    @Override
    public void fetchSongListMusic(XMSongListInfo item, int page, int count, OnAudioFetchListener listener) {
        if (item == null || item.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetch(item.getSDKBean(), page, count, getFetchListener(listener));
    }

    @Override
    public void fetchBillboardMusic(XMBillboardInfo item, int page, int count, OnAudioFetchListener<List<XMMusic>> listener) {
        if (item == null || item.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetch(item.getSDKBean(), page, count, getFetchListener(listener));
    }

    @Override
    public void fetchArtistMusic(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener) {
        if (artist == null || artist.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetchArtistMusic(artist.getSDKBean(), page, count, getFetchListener(listener));
    }

    @Override
    public void fetchArtistAlbum(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener) {
        if (artist == null || artist.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetchArtistAlbum(artist.getSDKBean(), page, count, getFetchListener(listener));
    }


    @Override
    public void fetchAlbumMusic(XMAlbumInfo album, int page, int count, OnAudioFetchListener<List<XMMusic>> listener) {
        if (album == null || album.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetchAlbumMusic(album.getSDKBean(), page, count, getFetchListener(listener));
    }

    @Override
    public void fetchSimilarSong(XMMusic music, int pMusicCount, OnAudioFetchListener<List<XMMusic>> listener) {
        if (music == null || music.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetchSimilarSong(music.getSDKBean(), pMusicCount, getFetchListener(listener));
    }

    @Override
    public void fetchLyric(XMMusic music, final OnAudioFetchListener<String> listener) {
        if (music == null || music.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        KwApi.fetchLyric(music.getSDKBean(), getOnLyricFetchListener(listener));
    }

    @Override
    public void fetchImage(XMMusic music, final OnAudioFetchListener<String> listener, @IKuwoConstant.IImageSize int pSize) {
        if (music == null || music.getSDKBean() == null) {
            listener.onFetchFailed("item is empty");
            return;
        }
        final ImageSize imageSize = IKuwoConstant.getImageSize(pSize);
        KwApi.fetchImage(music.getSDKBean(), getOnImageFetchListener(listener), imageSize);
    }

    @Override
    public void fetchSearchHotKeywords(final OnAudioFetchListener<List<String>> listener) {
        KwApi.fetchSearchHotKeywords(getOnHotKeywordsFetchListener(listener));
    }

    @Override
    public void fetchHotSongList(int page, int count, OnAudioFetchListener listener) {
        KwApi.fetchHotSongList(page, count, getFetchListener(listener));
    }

    @Override
    public void fetchNewSongList(int page, int count, OnAudioFetchListener listener) {
        KwApi.fetchNewSongList(page, count, getFetchListener(listener));
    }

    @Override
    public void fetchSearchTips(String keywords, final OnAudioFetchListener listener) {
        KwApi.fetchSearchTips(keywords, getOnSearchTipsSearchListener(listener));
    }

    @Override
    public void fetchMusicById(long id, OnAudioFetchListener<XMMusic> listener) {
        KwApi.fetchMusic(id, getMusicFetchListener(listener));
    }

    @Override
    public void fetchMusicByIds(List<Long> ids, OnAudioFetchListener<List<XMMusic>> listener) {
        KwApi.fetchMusics(ids, getMusicsFetchListener(listener));
    }

    private KwApi.OnFetchListener getFetchListener(final OnAudioFetchListener listener) {
        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
        ThreadDispatcher.getDispatcher().postDelayed(timeoutRunnable, TIME_OUT_DELAY);
        currentListener = listener;
        return new KwApi.OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                if (listener == null) {
                    ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                    return;
                }
                dealResults(qukuRequestState, s, onlineRootInfo, listener);
            }
        };
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentListener != null) {
                currentListener.onFetchFailed("time out");
            }
            ThreadDispatcher.getDispatcher().remove(this);
        }
    };

    private KwApi.OnFetchListener getRecommendFetchListener(final OnAudioFetchListener listener) {
        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
        ThreadDispatcher.getDispatcher().postDelayed(timeoutRunnable, 3000);
        currentListener = listener;
        return new KwApi.OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                if (listener == null) {
                    ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                    return;
                }
                dealRecommendResults(qukuRequestState, s, onlineRootInfo, listener);
            }
        };
    }

    private OnMusicFetchListener getMusicFetchListener(OnAudioFetchListener<XMMusic> listener) {
        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
        ThreadDispatcher.getDispatcher().postDelayed(timeoutRunnable, 3000);
        return new OnMusicFetchListener() {
            @Override
            public void onFetch(QukuRequestState state, String s, Music music) {
                if (listener == null) {
                    ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                    return;
                }
                switch (state) {
                    case SUCCESS:
                        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                        if (music != null) {
                            listener.onFetchSuccess(new XMMusic(music));
                        } else {
                            listener.onFetchFailed("music is null");
                        }
                        break;
                    case LOADING:
                        break;
                    case NET_UNAVAILABLE:
                    case FAILURE:
                    case DATA_PARSE_ERROR:
                    case UNSUPPORTED_PARSER:
                    case HTTP_ERROR:
                    case READ_CACHE_ERROR:
                        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                        listener.onFetchFailed(s);
                        shareNoNetErrorState(listener);
                        break;
                    default:
                        ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                        listener.onFetchFailed(s);
                        KLog.d("default " + s);
                        break;
                }
            }
        };
    }

    private OnMusicsFetchListener getMusicsFetchListener(OnAudioFetchListener<List<XMMusic>> listener) {
        return new OnMusicsFetchListener() {
            @Override
            public void onFetch(QukuRequestState state, String s, List<Music> list) {
                switch (state) {
                    case SUCCESS:
                        if (!ListUtils.isEmpty(list)) {
                            List<XMMusic> musicList = new ArrayList<>();
                            for (Music music : list) {
                                if (music == null) {
                                    continue;
                                }
                                musicList.add(new XMMusic(music));
                            }
                            listener.onFetchSuccess(musicList);
                        }else {
                            listener.onFetchFailed("music list is null");
                        }
                        break;
                    case LOADING:
                        break;
                    case NET_UNAVAILABLE:
                    case FAILURE:
                    case DATA_PARSE_ERROR:
                    case UNSUPPORTED_PARSER:
                    case HTTP_ERROR:
                    case READ_CACHE_ERROR:
                        listener.onFetchFailed(s);
                        shareNoNetErrorState(listener);
                        break;
                    default:
                        listener.onFetchFailed(s);
                        KLog.d("default " + s);
                        break;
                }
            }
        };
    }

    @NonNull
    private KwApi.OnLyricFetchListener getOnLyricFetchListener(final OnAudioFetchListener listener) {
        return new KwApi.OnLyricFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, String s1) {
                dealResult(qukuRequestState, s, s1, listener);
            }
        };
    }

    @NonNull
    private OnSearchTipsSearchListener getOnSearchTipsSearchListener(final OnAudioFetchListener listener) {
        return new OnSearchTipsSearchListener() {
            @Override
            public void onFetch(QukuRequestState qukuRequestState, String s, List<String> list) {
                dealResult(qukuRequestState, s, list, listener);
            }
        };
    }

    @NonNull
    private OnHotKeywordsFetchListener getOnHotKeywordsFetchListener(final OnAudioFetchListener listener) {
        return new OnHotKeywordsFetchListener() {
            @Override
            public void onFetch(QukuRequestState qukuRequestState, String s, List<String> list) {
                dealResult(qukuRequestState, s, list, listener);
            }
        };
    }

    @NonNull
    private OnImageFetchListener getOnImageFetchListener(final OnAudioFetchListener listener) {
        return new OnImageFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, String s1) {
                dealResult(qukuRequestState, s, s1, listener);
            }
        };
    }

    @NonNull
    private OnDailyRecommendFetchListener getOnDailyRecommendFetchListener(final OnAudioFetchListener listener) {
        return new OnDailyRecommendFetchListener() {
            @Override
            public void onFetch(QukuRequestState qukuRequestState, String s, List<Music> list) {
                List<XMMusic> xmMusiclist = new ArrayList<>();
                for (Music music : list) {
                    if (music == null) {
                        continue;
                    }
                    xmMusiclist.add(new XMMusic(music));
                }
                dealResult(qukuRequestState, s, xmMusiclist, listener);
            }
        };
    }

    private void shareNoNetErrorState(OnAudioFetchListener listener){
        if(listener != null && listener instanceof PlayAfterSuccessFetchListener && ((PlayAfterSuccessFetchListener) listener).playAfterFetchSuccess()){
            AudioShareManager.getInstance().shareAudioErrorStateOnNet();
        }
    }

    private <T> void dealResult(final QukuRequestState state, final String rtnMsg,
                                final Object obj, final OnAudioFetchListener<T> listener) {
        if (listener == null) {
            return;
        }
        if (obj == null) {
            listener.onFetchFailed("fetch error");
            return;
        }
        switch (state) {
            case SUCCESS:
                listener.onFetchSuccess((T) obj);
                break;
            case LOADING:
                break;
            case NET_UNAVAILABLE:
            case FAILURE:
            case DATA_PARSE_ERROR:
            case UNSUPPORTED_PARSER:
            case HTTP_ERROR:
            case READ_CACHE_ERROR:
                listener.onFetchFailed(rtnMsg);
                shareNoNetErrorState(listener);
                break;
            default:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                listener.onFetchFailed(rtnMsg);
                KLog.d("Default " + rtnMsg);
                break;
        }
    }

    private void dealRecommendResults(QukuRequestState state, String msg, OnlineRootInfo info,
                                      OnAudioFetchListener listener) {
        switch (state) {
            case SUCCESS:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                if (info == null) {
                    listener.onFetchFailed("fetch error");
                    return;
                }
                dispatchRecommendListener(info, listener);
                break;
            case LOADING:
                break;
            case NET_UNAVAILABLE:
            case FAILURE:
            case DATA_PARSE_ERROR:
            case UNSUPPORTED_PARSER:
            case HTTP_ERROR:
            case READ_CACHE_ERROR:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                listener.onFetchFailed(msg);
                shareNoNetErrorState(listener);
                break;
            default:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                listener.onFetchFailed(msg);
                KLog.d("default " + msg);
                break;
        }
    }

    private void dealResults(QukuRequestState state, String msg, OnlineRootInfo info,
                             OnAudioFetchListener listener) {
        switch (state) {
            case SUCCESS:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                if (info == null) {
                    listener.onFetchFailed("fetch error");
                    return;
                }
                dispatchListener(info, listener);
                break;
            case LOADING:
                break;
            case NET_UNAVAILABLE:
            case FAILURE:
            case DATA_PARSE_ERROR:
            case UNSUPPORTED_PARSER:
            case HTTP_ERROR:
            case READ_CACHE_ERROR:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                listener.onFetchFailed(msg);
                shareNoNetErrorState(listener);
                break;
            default:
                ThreadDispatcher.getDispatcher().remove(timeoutRunnable);
                listener.onFetchFailed(msg);
                KLog.d("default " + msg);
                break;
        }
    }

    private void dispatchListener(OnlineRootInfo onlineRootInfo, OnAudioFetchListener listener) {
        try {
            XMOnlineRootInfo xmOnlineRootInfo = new XMOnlineRootInfo(onlineRootInfo);
            final List<XMBaseOnlineSection> onlineSections = xmOnlineRootInfo.getOnlineSections();
            XMBaseOnlineSection xmBaseOnlineSection = onlineSections.get(0);
            BaseOnlineSection sdkBean = xmBaseOnlineSection.getSDKBean();
            List<BaseQukuItem> onlineInfoList = sdkBean.getOnlineInfos();
            String type = onlineInfoList.get(0).getQukuItemType();
            DispatchHandlerFactory.createHandler(type).handle(listener, onlineSections);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFetchFailed("data is empty");
        }
    }

    private void dispatchRecommendListener(OnlineRootInfo onlineRootInfo, OnAudioFetchListener listener) {
        try {
            XMOnlineRootInfo xmOnlineRootInfo = new XMOnlineRootInfo(onlineRootInfo);
            final List<XMBaseOnlineSection> onlineSections = xmOnlineRootInfo.getOnlineSections();
            List<XMBaseQukuItem> xmBaseQukuItemList = new ArrayList<>();
            for (XMBaseOnlineSection onlineSection : onlineSections) {
                if (onlineSection == null || onlineSection.getSDKBean() == null) {
                    continue;
                }
                if (onlineSection.getType().equals("banner")){
                    continue;
                }
                List<BaseQukuItem> onlineInfos = onlineSection.getSDKBean().getOnlineInfos();
                for (BaseQukuItem onlineInfo : onlineInfos) {
                    if (onlineInfo == null) {
                        continue;
                    }
                    if (onlineInfo instanceof SongListInfo) {
                        xmBaseQukuItemList.add(new XMBaseQukuItem(onlineInfo));
                    }
                    if (onlineInfo instanceof AlbumInfo) {
                        xmBaseQukuItemList.add(new XMBaseQukuItem(onlineInfo));
                    }
                    if (onlineInfo instanceof RadioInfo) {
                        xmBaseQukuItemList.add(new XMBaseQukuItem(onlineInfo));
                    }
                }
            }
            listener.onFetchSuccess(xmBaseQukuItemList);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFetchFailed("data is empty");
        }
    }
}
