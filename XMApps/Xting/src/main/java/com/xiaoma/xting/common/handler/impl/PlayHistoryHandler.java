package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class PlayHistoryHandler extends AbsActionHandler {

    public PlayHistoryHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        int action = bundle.getInt(AudioConstants.BundleKey.ACTION);
        if (action == AudioConstants.Action.Option.PLAY) {
            playWithHistory(playerInfo, 0);
        } else if (action == AudioConstants.Action.Option.NEXT) {
            playWithHistory(playerInfo, 1);
        } else if (action == AudioConstants.Action.Option.PREVIOUS) {
            playWithHistory(playerInfo, -1);
        }
    }

    private void playWithHistory(PlayerInfo playerInfo, int playOffset) {
        playerInfo.setAction(PlayerAction.SET_LIST);
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(Object t) {
                int playingIndex = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex();
                if (playOffset == 0) {
                    PlayerSourceFacade.newSingleton().getPlayerControl().play();
                } else if (playOffset == -1) {
                    if (playingIndex == 0) {
                        XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.no_pre_sound);
                    } else {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex - 1);
                    }
                } else {
                    int playListSize = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayListSize();
                    if (playingIndex + 1 == playListSize) {
                        XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.no_next_sound);
                    } else {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex + 1);
                    }
                }
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

//    private void playWithHistory(HistoryBean historyBean, int diff) {
//        if (OnlineFetchInfoRepository.newSingleton().getCurDataSource() == AudioConstants.OnlineInfoSource.XMLY) {
//            playAlbumInHistory(historyBean, diff);
//        } else {
//            String curCategoryId = OnlineRecorderManager.newSingleton().getCurCategoryId();
//            if (!OnlineRecorderManager.DEF_CATEGORY.equals(curCategoryId)) {
//                OnlineFetchInfoRepository.newSingleton().updateFetchMoreType(OnlineFMConstracts.FetchMoreType.DEFAULT);
//                OnlineLauncherFetchMore.newSingleton().fetchCategory(curCategoryId, 0, historyBean.getUniqueId(), diff, true);
//            }
//        }
//    }
//
//    private void playAlbumInHistory(final HistoryBean historyBean, final int diff) {
//        OnlineFMInfo curFMInfo = OnlineFetchInfoRepository.newSingleton().getCurFMInfo();
//        if (curFMInfo != null
//                && curFMInfo.getAlbumId() == historyBean.getAlbumId()) {
//            XmlyPlayerProxy.newSingleton().play();
//        } else {
//            @OnlineFMConstracts.OnlineFMType int type = historyBean.getType();
//            OnlineFetchInfoRepository.newSingleton().updateFetchMoreType(OnlineFMConstracts.FetchMoreType.DEFAULT);
//            if (type == OnlineFMConstracts.OnlineFMType.RADIO) {
//                OnlineFetcher.fetchRadioById(new long[]{historyBean.getAlbumId()}, new OnlineFetcher.OnFetchListener<XMRadioListById>() {
//                    @Override
//                    public void onLoading() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.LOADING);
//                    }
//
//                    @Override
//                    public void onSuccess(XMRadioListById data) {
//                        List<XMRadio> radios = data.getRadios();
//                        if (!ListUtils.isEmpty(radios)) {
//                            XMRadio xmRadio = radios.get(0);
//                            OnlineFetchInfoRepository.newSingleton().updateCurPlayModel(xmRadio, false);
//                            //radio没有设置进入Player的方法,所以只有调用播放
//                            XmlyPlayerProxy.newSingleton().playRadio(xmRadio);
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                    }
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                    }
//                });
//            } else if (type == OnlineFMConstracts.OnlineFMType.TRACK) {
//                OnlineFetcher.fetchTracks(historyBean.getAlbumId(), historyBean.getOrderNum() / XtingConstants.PAGE_COUNT + 1, new OnlineFetcher.OnFetchListener<XMTrackList>() {
//                    @Override
//                    public void onLoading() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.LOADING);
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.LOADING);
//                    }
//
//                    @Override
//                    public void onSuccess(XMTrackList trackList) {
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.COMPLETE);
//
//                        List<XMTrack> fetchTrackList = OnlineFetchInfoRepository.newSingleton().getTrackLists();
//                        int playIndex = HimalayanPlayerUtils.checkIndexWithId(fetchTrackList, historyBean.getUniqueId());
//                        if (playIndex == HimalayanPlayerUtils.INDEX_NOT_FOUND) {
//                            playIndex = 0;
//                        }
//                        if (diff == -1) {
//                            if (playIndex == 0) {
//                                share(AudioConstants.AudioControlCode.TOP);
//                            } else {
//                                share(AudioConstants.AudioControlCode.MIDDLE);
//                                playIndex += diff;
//                            }
//                        } else if (diff == 1) {
//                            if (playIndex == (fetchTrackList.size() - 1)) {
//                                share(AudioConstants.AudioControlCode.BOTTOM);
//                            } else {
//                                share(AudioConstants.AudioControlCode.MIDDLE);
//                                playIndex += diff;
//                            }
//                        }
//                        OnlineFetchInfoRepository.newSingleton().updateCurPlayModel(fetchTrackList.get(playIndex), false);
//                        XmlyPlayerProxy.newSingleton()
//                                .playTrackListAtIndex(fetchTrackList, playIndex);
//
//                    }
//
//                    @Override
//                    public void onFail() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.FAIL);
//                    }
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.ERROR);
//                    }
//                });
//            }
//        }
//    }

    private void share(@AudioConstants.AudioControlCode int code) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_CONTROL_CODE);
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_CONTROL_CODE, code);

        dispatchRequestCallback(callbackData);
    }

}
