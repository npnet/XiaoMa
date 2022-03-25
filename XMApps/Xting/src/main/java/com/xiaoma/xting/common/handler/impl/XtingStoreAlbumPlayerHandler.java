package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.sdk.LocalFMFactory;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class XtingStoreAlbumPlayerHandler extends AbsActionHandler {

    public XtingStoreAlbumPlayerHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        List<SubscribeInfo> subscribeList = XtingUtils.getSubscribeDao().selectAll();

        if (bundle != null) {
            bundle.setClassLoader(AudioCategoryBean.class.getClassLoader());
            AudioCategoryBean audioCategoryBean = bundle.getParcelable(AudioConstants.BundleKey.EXTRA);
            int index = audioCategoryBean.getIndex();
            SubscribeInfo subscribeInfo = subscribeList.get(index);
            int type = subscribeInfo.getType();
            PlayerSourceFacade.newSingleton().setSourceType(type);
            if (type == PlayerSourceType.RADIO_YQ) {
                if (!LocalFMFactory.getSDK().isRadioOpen()) {
                    LocalFMFactory.getSDK().openRadio();
                }
                long channelValue = subscribeInfo.getAlbumId();
                LocalFMOperateManager.newSingleton().playChannel((int) channelValue);
            } else {
                playAlbumWithRecord(subscribeInfo);
            }

        } else {
            if (ListUtils.isEmpty(subscribeList)) {
                //默认从0开始
                shareBoolean(false);
            } else {
                playAlbumWithRecord(subscribeList.get(0));
            }
        }
    }

    private void playAlbumWithRecord(SubscribeInfo subscribeInfo) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo != null
                && playerInfo.getType() == subscribeInfo.getType()
                && playerInfo.getAlbumId() == subscribeInfo.getAlbumId()) {
            PlayerSourceFacade.newSingleton().getPlayerControl().play();
        } else {
            XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).restoreLauncherCategoryId();
            PlayerInfo selectPlayerInfo = BeanConverter.toPlayerInfo(subscribeInfo);
            if (selectPlayerInfo.getType() == PlayerSourceType.RADIO_XM) {
                selectPlayerInfo.setType(PlayerSourceType.HIMALAYAN);
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
            }
            selectPlayerInfo.setAction(PlayerAction.PLAY_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(selectPlayerInfo, new IFetchListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onSuccess(Object t) {
                    shareBoolean(true);
                    shareCode(AudioConstants.AudioResponseCode.SUCCESS);
                }

                @Override
                public void onFail() {
                    shareBoolean(false);
                    shareCode(AudioConstants.AudioResponseCode.FAIL);
                }

                @Override
                public void onError(int code, String msg) {
                    shareBoolean(false);
                    shareCode(AudioConstants.AudioResponseCode.ERROR);
                }
            });
        }
    }

//    private void playAlbumInHistory(final HistoryBean historyBean) {
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
//
//                            shareBoolean(true);
//                            shareCode(AudioConstants.AudioResponseCode.SUCCESS);
//                        } else {
//                            shareBoolean(false);
//                            shareCode(AudioConstants.AudioResponseCode.FAIL);
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        shareBoolean(false);
//                        shareCode(AudioConstants.AudioResponseCode.FAIL);
//                    }
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        shareBoolean(false);
//                        shareCode(AudioConstants.AudioResponseCode.ERROR);
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
//                        List<XMTrack> tracks = trackList.getTracks();
//                        int playIndex = HimalayanPlayerUtils.checkIndexWithId(tracks, historyBean.getUniqueId());
//                        if (playIndex == HimalayanPlayerUtils.INDEX_NOT_FOUND) {
//                            playIndex = 0;
//                        }
//
//                        OnlineFetchInfoRepository.newSingleton().updateCurPlayModel(tracks.get(playIndex), false);
//                        XmlyPlayerProxy.newSingleton()
//                                .playTrackListAtIndex(tracks, playIndex);
//                        shareBoolean(true);
//
//                    }
//
//                    @Override
//                    public void onFail() {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.FAIL);
//                        shareBoolean(false);
//                    }
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        OnlineFetchInfoRepository.newSingleton().onPlayerStateChanged(OnlineFMConstracts.PlayerStatus.PAUSE);
//                        OnlineFetchInfoRepository.newSingleton().dispatchFetchMoreStatus(OnlineFMConstracts.FetchMoreStatus.ERROR);
//                        shareBoolean(false);
//                    }
//                });
//            }
//        }
//    }

    /**
     * 主要用于语音助手的反馈
     *
     * @param operateOk
     */
    private void shareBoolean(boolean operateOk) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.XtingThirdBundleKey.RESULT, operateOk);
        dispatchRequestCallback(bundle);
    }

    private void shareCode(@AudioConstants.AudioResponseCode int responseCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, responseCode);

        dispatchRequestCallback(bundle);
    }
}
