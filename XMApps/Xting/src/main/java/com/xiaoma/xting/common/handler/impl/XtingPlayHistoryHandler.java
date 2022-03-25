package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.assistant.model.AssistantAlbum;
import com.xiaoma.xting.common.RequestManager;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.launcher.ToLauncherBeanConvert;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/19
 */
public class XtingPlayHistoryHandler extends AbsActionHandler {
    public XtingPlayHistoryHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        if (bundle == null) {
//            PlayerInfo toPlayerInfo = SharedPrefUtils.getCachedPlayerInfo(AppHolder.getInstance().getAppContext());
//            if (toPlayerInfo != null) {
//                int sourceType = toPlayerInfo.getSourceType();
//                PlayerSourceFacade.newSingleton().setSourceType(sourceType);
//                if (sourceType == PlayerSourceType.RADIO_YQ) {
//                    PlayerSourceFacade.newSingleton().getPlayerControl().play();
//                } else {
//                    playOnline(toPlayerInfo);
//                }
//            } else {
            List<RecordInfo> recordInfoList = XtingUtils.getRecordDao().selectAll();
            //如果播放记录为null,则播放推荐,默认从0开始播放
            if (ListUtils.isEmpty(recordInfoList)) {
                RequestManager.requestRecommendList(0, new ResultCallback<XMResult<AssistantAlbum>>() {
                    @Override
                    public void onSuccess(XMResult<AssistantAlbum> result) {
                        AssistantAlbum data = result.getData();
                        ArrayList<XMTrack> tracks = data.getTrackList();
                        if (ListUtils.isEmpty(tracks)) {
                            onFailure(-1, "");
                            return;
                        }
//                        AssistantAlbum.PageInfoBean pageInfo = data.getPageInfo();
//                        OnlineFetchInfoRepository.newSingleton()
//                                .dispatchTrackList(tracks, new int[]{pageInfo.getPageNum(), pageInfo.getTotalPage(), pageInfo.getTotalRecord()});
//
//                        OnlineFetchInfoRepository.newSingleton().updateCurPlayModel(tracks.get(0), true);

                        sharePlayList(tracks, false, true);
//                        if (tracks.size() == 1) { //如果推荐的只有一首 那么直接开始播放
//                        } else {
//
//                        }
                        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_XM);
                        OnlineFMPlayerFactory.getPlayer().playList(tracks, 0);
//                            PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(0);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        sharePlayList(null, false, false);
                    }
                });
            } else {
                RecordInfo recordInfo = recordInfoList.get(0);
                PlayerInfo playerInfo = BeanConverter.toPlayerInfo(recordInfo);
                //这个指令只播放处理在线，不播放本地电台节目
                if (playerInfo.getType() == PlayerSourceType.RADIO_YQ) {
                    playerInfo.setType(PlayerSourceType.HIMALAYAN);
                }
                playOnline(playerInfo);
            }
//            }
        }
    }

    private void playOnline(PlayerInfo playerInfo) {
        playerInfo.setAction(PlayerAction.PLAY_LIST);
        PlayerSourceFacade.newSingleton().setSourceType(playerInfo.getType());
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onSuccess(Object t) {
                List<PlayerInfo> playList = (ArrayList<PlayerInfo>) PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
                share(playList, true, true);
            }

            @Override
            public void onFail() {
                sharePlayList(null, true, false);
            }

            @Override
            public void onError(int code, String msg) {
                sharePlayList(null, true, false);
            }
        });
    }

    /**
     * @param list        播放列表
     * @param fromHistory 是否有历史记录
     * @param requestOK   请求结果
     */
    private void sharePlayList(List<XMTrack> list, boolean fromHistory, boolean requestOK) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putBoolean(AudioConstants.BundleKey.AUDIO_DATA_SOURCE, fromHistory);
        callbackData.putBoolean(CenterConstants.XtingThirdBundleKey.RESULT, requestOK);
        if (list != null && !list.isEmpty()) {
            callbackData.putParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST, ToLauncherBeanConvert.trackList2AudioInfoList(list));
        }
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_PLAYING_INDEX, 0);
        dispatchRequestCallback(callbackData);
    }

    private void share(List<PlayerInfo> list, boolean fromHistory, boolean requestOK) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putBoolean(AudioConstants.BundleKey.AUDIO_DATA_SOURCE, fromHistory);
        callbackData.putBoolean(CenterConstants.XtingThirdBundleKey.RESULT, requestOK);
        if (list != null && !list.isEmpty()) {

            callbackData.putParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST, ToLauncherBeanConvert.toAudioInfo(list));
        }
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_PLAYING_INDEX, 0);
        dispatchRequestCallback(callbackData);
    }
}
