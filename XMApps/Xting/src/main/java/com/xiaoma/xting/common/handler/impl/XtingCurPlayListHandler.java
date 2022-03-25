package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.launcher.ToLauncherBeanConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class XtingCurPlayListHandler extends AbsActionHandler {

    private boolean mHasLoadingF;

    public XtingCurPlayListHandler(ClientCallback callback) {
        super(callback);
        mHasLoadingF = false;
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo != null) {
            int type = playerInfo.getType();
            if (type == PlayerSourceType.RADIO_YQ) {
//                LocalFMOperateManager.newSingleton().listenSearchResultToLauncher(new LocalFMOperateManager.IOnAutoSearchOkListener() {
//                    @Override
//                    public void autoSearchSuccess(int bandType) {
//                        if (bandType == XtingConstants.FMAM.TYPE_FM) {
//                            showPlayerList(AudioConstants.AudioResponseCode.SUCCESS, ToLauncherBeanConvert.fmList2AudioInfoList(LocalPlayList.getInstance().getFmChannelBeans()));
//                            LocalFMOperateManager.newSingleton().removeAutoSearchOkListener(this);
//                        }
//                    }
//
//                    @Override
//                    public void autoOpenFailed() {
//                        showPlayerList(AudioConstants.AudioResponseCode.ERROR, null);
//                        LocalFMOperateManager.newSingleton().removeAutoSearchOkListener(this);
//                    }
//                });
                showPlayerList(AudioConstants.AudioResponseCode.SUCCESS, ToLauncherBeanConvert.fmList2AudioInfoList(LocalPlayList.getInstance().getFmChannelBeans()));
            } else {
                List<PlayerInfo> playerInfoList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
                if (ListUtils.isEmpty(playerInfoList)) {
                    playerInfo.setAction(PlayerAction.SET_LIST);
                    PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onSuccess(Object t) {
                            showPlayerList(AudioConstants.AudioResponseCode.SUCCESS, ToLauncherBeanConvert.toAudioInfo(PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList()));
                        }

                        @Override
                        public void onFail() {
                            showPlayerList(AudioConstants.AudioResponseCode.FAIL, null);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            showPlayerList(AudioConstants.AudioResponseCode.ERROR, null);
                        }
                    });
                } else {
                    showPlayerList(AudioConstants.AudioResponseCode.SUCCESS, ToLauncherBeanConvert.toAudioInfo(playerInfoList));
                }
            }
        }
    }


    private void showPlayerList(@AudioConstants.AudioResponseCode int code, ArrayList<AudioInfo> playList) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, code);
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_PLAYING_INDEX, PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex());

        if (!ListUtils.isEmpty(playList)) {
            callbackData.putParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST, playList);
        }

        int[] pageInfo = PlayerSourceFacade.newSingleton().getPlayerFetch().getPageInfo();
        if (pageInfo[2] > 0) {
            callbackData.putIntArray(
                    AudioConstants.BundleKey.PAGE_INFO,
                    PlayerSourceFacade.newSingleton().getPlayerFetch().getPageInfo());
        }

        dispatchRequestCallback(callbackData);
    }

}
