package com.xiaoma.xting.common.playerSource.loadmore.impl;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.koala.KoalaRequest;
import com.xiaoma.xting.koala.bean.XMAudioDetails;
import com.xiaoma.xting.koala.bean.XMListPageAudioDetails;
import com.xiaoma.xting.koala.bean.XMPlayItem;
import com.xiaoma.xting.koala.utils.KoalaPlayerUtils;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class KoalaPlayerFetch extends IPlayerFetch {

    private static final String TAG = KoalaPlayerFetch.class.getSimpleName();
    private String mClockId;

    @Override
    protected void fetch(IFetchListener listener) {
        mClockId = "";
        int sourceSubType = mPlayerInfo.getSourceSubType();
        if (sourceSubType == PlayerSourceSubType.KOALA_ALBUM) {
            KoalaPlayer.newSingleton().playAudio(mPlayerInfo.getAlbumId());
            KoalaRequest.getPlaylist(mPlayerInfo.getAlbumId(), mPlayerInfo.getProgramId(), true, 20, 1, new AbsXMDataCallback<XMListPageAudioDetails>() {
                @Override
                public void onSuccess(long albumId, @Nullable XMListPageAudioDetails data) {
                    if (albumId != mPlayerInfo.getAlbumId()) {
                        Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                        return;
                    }
                    if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                        Log.d("YG", "{Type}-[Change] : " + TAG);
                        return;
                    }
                    if (data != null) {
                        BeanConverter.setAlbumUrl(null);
                        setPageExtremes(1, data.getSumPage(), data.getCount());
                        setPageBound(data.getCurrentPage());

                        List<XMAudioDetails> dataList = data.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            BeanConverter.setAlbumUrl(dataList.get(0).getAudioPic());
                            KoalaPlayer.newSingleton().addToPlayer(data, true);
                            if (mPlayerInfo.getAction() == PlayerAction.PLAY_LIST) {
                                KoalaPlayer.newSingleton().playAudioFromPlayList(mPlayerInfo.getProgramId());
                            } else {
                                KoalaPlayer.newSingleton().setPlayItemById(mPlayerInfo.getProgramId());
                                PlayerInfoImpl.newSingleton().onPlayerInfoChanged(KoalaPlayer.newSingleton().getCurPlayerInfo());
                                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
                            }
                            dispatchSuccess(data, listener);
                            return;
                        }
                    }
                    dispatchFail(listener);
                }

                @Override
                public void onError(long albumId, int code, String msg) {
                    if (albumId != mPlayerInfo.getAlbumId()) {
                        Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                        return;
                    }
                    if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                        Log.d("YG", "{Type}-[Change] : " + TAG);
                        return;
                    }
                    dispatchError(code, msg, listener);
                }

                @Override
                public void onError(int code, String msg) {
                    dispatchError(code, msg, listener);
                }
            });
        } else if (sourceSubType == PlayerSourceSubType.KOALA_PGC_RADIO) {
            setTotalCount(-2);
            //这个很重要,删除会影像连续播放
            KoalaPlayer.newSingleton().playPgc(mPlayerInfo.getAlbumId());
            KoalaRequest.getPlaylist(mPlayerInfo.getAlbumId(), "", new XMDataCallback<List<XMAudioDetails>>() {
                @Override
                public void onSuccess(List<XMAudioDetails> data) {
                    if (data != null) {
                        BeanConverter.setAlbumUrl(null);
                        List<XMPlayItem> playItemList = new ArrayList<>(data.size());
                        mClockId = data.get(0).getClockId();
                        for (XMAudioDetails bean : data) {
                            playItemList.add(bean.toPlayItem());
                        }

//                        BeanConverter.setAlbumUrl(data.get(0).getAudioPic());
                        KoalaPlayer.newSingleton().setPlayType(PlayerSourceSubType.KOALA_PGC_RADIO);
                        KoalaPlayer.newSingleton().addPlayList(playItemList);
                        if (mPlayerInfo.getAction() == PlayerAction.PLAY_LIST) {
                            KoalaPlayer.newSingleton().playAudioFromPlayList(mPlayerInfo.getProgramId());
                        } else {
                            KoalaPlayer.newSingleton().setPlayItemById(mPlayerInfo.getProgramId());
                            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(KoalaPlayer.newSingleton().getCurPlayerInfo());
                            PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
                        }
                        dispatchSuccess(data, listener);
                        return;
                    }
                    dispatchFail(listener);
                }

                @Override
                public void onError(int code, String msg) {
                    dispatchError(code, msg, listener);
                }
            });
        }

    }

    @Override
    protected void download(int page) {
        if (mPlayerInfo.getSourceSubType() == PlayerSourceSubType.KOALA_ALBUM) {
            KoalaRequest.getPlayList(mPlayerInfo.getAlbumId(), true, 20, page, new AbsXMDataCallback<XMListPageAudioDetails>() {
                @Override
                public void onSuccess(long albumId, @Nullable XMListPageAudioDetails data) {
                    if (albumId != mPlayerInfo.getAlbumId()) {
                        Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                        return;
                    }
                    if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                        Log.d("YG", "{Type}-[Change] : " + TAG);
                        return;
                    }
                    if (data != null) {
                        setPageBound(data.getCurrentPage());
                        List<XMAudioDetails> dataList = data.getDataList();
                        List<PlayerInfo> diffPlayerInfoList = new ArrayList<>();
                        if (dataList != null) {
                            KoalaPlayer.newSingleton().addToPlayer(data, true);
                            for (XMAudioDetails xmAudioDetails : dataList) {
                                diffPlayerInfoList.add(KoalaPlayerUtils.toPlayerInfo(xmAudioDetails));
                            }

                            dispatchLoadMore(true, isDownloadBottom() ? PlayerLoadMore.LOAD_END : PlayerLoadMore.LOAD_COMPLETE, diffPlayerInfoList);
                            return;
                        }
                    }
                    dispatchLoadMore(true, PlayerLoadMore.LOAD_FAIL, null);
                }

                @Override
                public void onError(long albumId, int code, String msg) {
                    if (albumId != mPlayerInfo.getAlbumId()) {
                        Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                        return;
                    }
                    if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                        Log.d("YG", "{Type}-[Change] : " + TAG);
                        return;
                    }
                    dispatchLoadMore(true, PlayerLoadMore.LOAD_ERROR, null);
                }
            });
        } else {
            if (TextUtils.isEmpty(mClockId)) {
                return;
            }
            KoalaRequest.getPlaylist(mPlayerInfo.getAlbumId(), mClockId, new XMDataCallback<List<XMAudioDetails>>() {
                @Override
                public void onSuccess(List<XMAudioDetails> data) {
                    if (data != null) {
                        BeanConverter.setAlbumUrl(null);
                        List<XMPlayItem> playItemList = new ArrayList<>(data.size());
                        List<PlayerInfo> diffPlayerInfoList = new ArrayList<>();
                        mClockId = data.get(0).getClockId();
                        KoalaPlayer.newSingleton().setPlayType(PlayerSourceSubType.KOALA_PGC_RADIO);
                        for (XMAudioDetails bean : data) {
                            XMPlayItem xmPlayItem = bean.toPlayItem();
                            playItemList.add(xmPlayItem);
                            diffPlayerInfoList.add(KoalaPlayerUtils.toPlayerInfo(xmPlayItem));
                        }

                        KoalaPlayer.newSingleton().addPlayList(playItemList);
                        dispatchLoadMore(true, PlayerLoadMore.LOAD_COMPLETE, diffPlayerInfoList);
                        return;
                    }
                    dispatchLoadMore(true, PlayerLoadMore.LOAD_END, null);
                }

                @Override
                public void onError(int code, String msg) {
                    dispatchLoadMore(true, PlayerLoadMore.LOAD_ERROR, null);
                }
            });
        }
    }

    @Override
    protected void upFetch(int page) {
        KoalaRequest.getPlayList(mPlayerInfo.getAlbumId(), true, 20, page, new AbsXMDataCallback<XMListPageAudioDetails>() {
            @Override
            public void onSuccess(long albumId, @Nullable XMListPageAudioDetails data) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                if (data != null) {
                    setPageBound(data.getCurrentPage());
                    List<XMAudioDetails> dataList = data.getDataList();
                    List<PlayerInfo> diffPlayerInfoList = new ArrayList<>();
                    if (dataList != null) {
                        KoalaPlayer.newSingleton().addToPlayer(data, false);
                        for (XMAudioDetails xmAudioDetails : dataList) {
                            diffPlayerInfoList.add(KoalaPlayerUtils.toPlayerInfo(xmAudioDetails));
                        }

                        dispatchLoadMore(false, isUpFetchTop() ? PlayerLoadMore.LOAD_END : PlayerLoadMore.LOAD_COMPLETE, diffPlayerInfoList);
                    }
                    return;
                }
                dispatchLoadMore(false, PlayerLoadMore.LOAD_FAIL, null);
            }

            @Override
            public void onError(long albumId, int code, String msg) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.KOALA)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                dispatchLoadMore(false, PlayerLoadMore.LOAD_ERROR, null);
            }
        });
    }
}
