package com.xiaoma.xting.common.playerSource.loadmore.impl;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.sdk.OnlineFM;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMRadioListById;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMScheduleList;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.sdk.bean.XMTrackList;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class HimalayanPlayerFetch extends IPlayerFetch {
    private static final String TAG = HimalayanPlayerFetch.class.getSimpleName();

    private final OnlineFM mRequest;

    public HimalayanPlayerFetch() {
        mRequest = OnlineFMFactory.getInstance().getSDK();
    }

    @Override
    protected void download(int page) {
        mRequest.getTracks(mPlayerInfo.getAlbumId(), SEQ_ASC, page, new AbsXMDataCallback<XMTrackList>() {

            @Override
            public void onSuccess(long albumId, @Nullable XMTrackList data) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                if (data != null) {
                    setPageBound(data.getCurrentPage());
                    List<XMTrack> tracks = data.getTracks();
                    if (!tracks.isEmpty()) {
                        OnlineFMPlayerFactory.getPlayer().addTracksToPlayList(tracks, true);
                        List<PlayerInfo> diffPlayerInfoList = new ArrayList<>(tracks.size());
                        for (XMTrack track : tracks) {
                            diffPlayerInfoList.add(BeanConverter.toPlayerInfo(track));
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
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }

                dispatchLoadMore(true, PlayerLoadMore.LOAD_ERROR, null);
            }
        });
    }

    @Override
    protected void upFetch(int page) {
        mRequest.getTracks(mPlayerInfo.getAlbumId(), SEQ_ASC, page, new AbsXMDataCallback<XMTrackList>() {
            @Override
            public void onSuccess(long albumId, @Nullable XMTrackList data) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                if (data != null) {
                    setPageBound(data.getCurrentPage());
                    List<XMTrack> tracks = data.getTracks();
                    if (!tracks.isEmpty()) {
                        OnlineFMPlayerFactory.getPlayer().addTracksToPlayList(tracks, false);

                        List<PlayerInfo> diffPlayerInfoList = new ArrayList<>(tracks.size());
                        for (XMTrack track : tracks) {
                            diffPlayerInfoList.add(BeanConverter.toPlayerInfo(track));
                        }
                        dispatchLoadMore(false, isUpFetchTop() ? PlayerLoadMore.LOAD_END : PlayerLoadMore.LOAD_COMPLETE, diffPlayerInfoList);
                        return;
                    }
                }
                dispatchLoadMore(false, PlayerLoadMore.LOAD_FAIL, null);
            }

            @Override
            public void onError(long albumId, int code, String msg) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }

                dispatchLoadMore(false, PlayerLoadMore.LOAD_ERROR, null);
            }
        });
    }

    @Override
    protected void fetch(final IFetchListener listener) {
        Log.d(TAG, "{fetch}-[] : ");
        int sourceSubType = mPlayerInfo.getSourceSubType();
        switch (sourceSubType) {
            case PlayerSourceSubType
                    .TRACK: //获取专辑列表
                mRequest.getTracks(mPlayerInfo.getAlbumId(), SEQ_ASC, mPlayerInfo.getPage(), new AbsXMDataCallback<XMTrackList>() {
                    @Override
                    public void onSuccess(long albumId, @Nullable XMTrackList data) {
                        if (albumId != mPlayerInfo.getAlbumId()) {
                            Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                            return;
                        }
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d("YG", "{Type}-[Change] : " + TAG);
                            return;
                        }
                        if (data != null) {
                            BeanConverter.setAlbumUrl(data.getCoverUrlMiddle());
                            setPageExtremes(1, data.getTotalPage(), data.getTotalCount());
                            setPageBound(data.getCurrentPage());
                            List<XMTrack> tracks = data.getTracks();
                            if (!tracks.isEmpty()) {
                                int playIndex = HimalayanPlayerUtils.checkIndexWithId(tracks, mPlayerInfo.getProgramId());
                                if (playIndex == -1) {
                                    playIndex = 0;
                                }
                                if (mPlayerInfo.getAction() == PlayerAction.SET_LIST) {
                                    OnlineFMPlayerFactory.getPlayer().setPlayList(tracks, playIndex);
                                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(tracks.get(playIndex)));
                                } else {
                                    OnlineFMPlayerFactory.getPlayer().playList(tracks, playIndex);
                                }

                                dispatchSuccess(tracks, listener);
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
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d("YG", "{Type}-[Change] : " + TAG);
                            return;
                        }
                        dispatchError(code, msg, listener);
                    }
                });
                break;
            case PlayerSourceSubType
                    .RADIO: //获取电台列表
                mRequest.getTodaySchedules(mPlayerInfo.getAlbumId(), new AbsXMDataCallback<XMScheduleList>() {
                    @Override
                    public void onSuccess(long albumId, @Nullable XMScheduleList data) {
                        if (albumId != mPlayerInfo.getAlbumId()) {
                            Log.d(TAG, "{albumId}-[Change] : ");
                            return;
                        }
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        if (data != null) {
                            BeanConverter.setAlbumUrl(null);
                            List<XMSchedule> xmSchedules = data.getmScheduleList();
                            if (xmSchedules != null && xmSchedules.size() > 0) {
                                if (mPlayerInfo.getAction() == PlayerAction.SET_LIST) {
                                    List<XMTrack> xmTracks = new ArrayList<>(xmSchedules.size());

                                    int playIndex = -1;

                                    for (int i = 0, n = xmSchedules.size(); i < n; i++) {
                                        XMSchedule xmSchedule = xmSchedules.get(i);
                                        xmSchedule.setRadioName(mPlayerInfo.getAlbumName());
                                        if (playIndex < 0
                                                && TextUtils.isEmpty(xmSchedule.getListenBackUrl())) {
                                            playIndex = i;
                                            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(xmSchedule));
                                        }
                                        xmTracks.add(HimalayanPlayerUtils.schedule2Track(xmSchedule));
                                    }

                                    OnlineFMPlayerFactory.getPlayer().setPlayList(xmTracks, playIndex);
                                } else {
                                    for (XMSchedule xmSchedule : xmSchedules) {
                                        xmSchedule.setRadioName(mPlayerInfo.getAlbumName());
                                        xmSchedule.setRadioPlayCount((int) mPlayerInfo.getPlayCount());
                                    }
                                    OnlineFMPlayerFactory.getPlayer().playSchedule(xmSchedules, -1);
                                }
                                dispatchSuccess(xmSchedules, listener);
                                return;
                            }
                        }
                        handleFail(listener);
                    }

                    @Override
                    public void onError(long albumId, int code, String msg) {
                        if (albumId != mPlayerInfo.getAlbumId()) {
                            Log.d(TAG, "{albumId}-[Change] : ");
                            return;
                        }
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        dispatchError(code, msg, listener);
                    }
                });
//                }
                break;
            case PlayerSourceSubType
                    .SCHEDULE:
                mPlayerInfo.setSourceSubType(PlayerSourceSubType.RADIO);
                XtingUtils.getRecordDao().insert(BeanConverter.toRecordInfo(mPlayerInfo));
                mRequest.getTodaySchedules(mPlayerInfo.getAlbumId(), new AbsXMDataCallback<XMScheduleList>() {
                    @Override
                    public void onSuccess(long albumId, @Nullable XMScheduleList data) {
                        if (albumId != mPlayerInfo.getAlbumId()) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        if (data != null) {
                            List<XMSchedule> xmSchedules = data.getmScheduleList();
                            if (xmSchedules != null && xmSchedules.size() > 0) {
                                dispatchSuccess(xmSchedules, listener);
                                return;
                            }
                        }
                        dispatchFail(listener);
                    }

                    @Override
                    public void onError(long albumId, int code, String msg) {
                        if (albumId != mPlayerInfo.getAlbumId()) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                            Log.d(TAG, "{Type}-[Change] : ");
                            return;
                        }
                        dispatchError(code, msg, listener);
                    }
                });
                break;
            default:
                dispatchError(-1, "", listener);
                break;
        }
    }

    private void handleFail(IFetchListener listener) {
        mRequest.getRadioById(mPlayerInfo.getAlbumId(), new AbsXMDataCallback<XMRadioListById>() {
            @Override
            public void onSuccess(long albumId, @Nullable XMRadioListById data) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d(TAG, "{AlbumId}-[Change] : ");
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d(TAG, "{Type}-[Change] : ");
                    return;
                }
                if (data != null) {
                    List<XMRadio> radioList = data.getRadios();
                    if (radioList != null && radioList.size() > 0) {
                        XMRadio xmRadio = radioList.get(0);
                        if (xmRadio.getDataId() != mPlayerInfo.getAlbumId()) {
                            Log.d("YG", "{AlbumId}-[Change] : " + TAG);
                            return;
                        }
                        if (mPlayerInfo.getAction() == PlayerAction.SET_LIST) {
                            OnlineFMPlayerFactory.getPlayer().setRadio(xmRadio);
                        } else {
                            OnlineFMPlayerFactory.getPlayer().playRadio(xmRadio);
                        }
                        dispatchSuccess(xmRadio, listener);
                        return;
                    }
                }

                dispatchFail(listener);
            }

            @Override
            public void onError(long albumId, int code, String msg) {
                if (albumId != mPlayerInfo.getAlbumId()) {
                    Log.d(TAG, "{AlbumId}-[Change] : ");
                    return;
                }
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.HIMALAYAN)) {
                    Log.d(TAG, "{Type}-[Change] : ");
                    return;
                }
                dispatchError(code, msg, listener);
            }
        });
    }
}
