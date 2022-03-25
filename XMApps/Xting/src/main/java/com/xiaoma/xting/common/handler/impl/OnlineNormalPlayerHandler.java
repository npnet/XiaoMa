package com.xiaoma.xting.common.handler.impl;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMAlbumList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class OnlineNormalPlayerHandler extends AbsActionHandler {

    public static final String ARG_DUAL_SCREEN_TYPE = "xiangTingType";
    public static final int INVALID_INT = -1;

    public OnlineNormalPlayerHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        bundle.setClassLoader(AudioCategoryBean.class.getClassLoader());
        AudioCategoryBean bean = bundle.getParcelable(AudioConstants.BundleKey.EXTRA);
        if (bean.getAction() == AudioConstants.PlayAction.DEFAULT) {
            boolean isFM = bundle.getBoolean(AudioConstants.BundleKey.EXTRA_2, false);
            if (isFM) {
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
                return;
            }
            int type = bundle.getInt(ARG_DUAL_SCREEN_TYPE, INVALID_INT);
            if (type == AudioConstants.XiangTingType.ONLINE) { //推荐页随机选择播放
                if (!NetworkUtils.isConnected(AppHolder.getInstance().getAppContext())) {
                    XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.network_error);
                } else {
                    PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                    if (playerInfo == null || playerInfo.getType() == PlayerSourceType.RADIO_YQ
                            || !XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).handlePlay(0, mClientCallback)) {
                        playRecommend(AppHolder.getInstance().getAppContext());
                    }
                }
            } else if (type == AudioConstants.XiangTingType.AM) {
                LocalFMOperateManager.newSingleton().playAMChannel();
            } else if (type == AudioConstants.XiangTingType.FM) {
                LocalFMOperateManager.newSingleton().playFMChannel();
            } else {
                XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).handlePlay(0, mClientCallback);
            }

        } else {
            int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
            if (sourceType == PlayerSourceType.RADIO_YQ) {
                int band = bundle.getInt(AudioConstants.BundleKey.LOCAL_FM_TYPE, AudioConstants.LocalFMTyep.FM);
                LocalFMOperateManager.newSingleton().playLocalFMAtPos(band, bean.getIndex());
            } else {
                PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(bean.getIndex());
            }
        }
    }

    private void playRecommend(Application appContext) {
        List<PlayerInfo> recommendList = SharedPrefUtils.getCachedRecommendList(AppHolder.getInstance().getAppContext());
        if (ListUtils.isEmpty(recommendList)) {
            //由于<考拉FM>容易出现问题,所以移除考拉部分数据,转为只播放<喜马拉雅>推荐专辑
            OnlineFMFactory.getInstance().getSDK().getAlbumList(0, 3, null, 1, new XMDataCallback<XMAlbumList>() {
                @Override
                public void onSuccess(@Nullable XMAlbumList data) {
                    List<PlayerInfo> list = new ArrayList<>();
                    if (data != null) {
                        List<AlbumBean> albums = AlbumBean.convert2Album(data.getAlbums());
                        PlayerInfo playerInfo = null;
                        for (AlbumBean albumBean : albums) {
                            playerInfo = new PlayerInfo();
                            playerInfo.setType(PlayerSourceType.HIMALAYAN);
                            playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
                            playerInfo.setAlbumId(albumBean.getId());
                            playerInfo.setImgUrl(albumBean.getValidCover());
                            playerInfo.setAlbumName(albumBean.getAlbumTitle());
                            playerInfo.setPlayCount(albumBean.getPlayCount());
                            list.add(playerInfo);
                        }
                    }
                    SharedPrefUtils.cacheRecommendList(appContext, list);
                    play(list);
                }

                @Override
                public void onError(int code, String msg) {
                    XMToast.showToast(appContext, msg);
                }
            });

        } else {
            play(recommendList);
        }
    }

    private void play(List<PlayerInfo> playerList) {
        if (playerList == null || playerList.isEmpty()) {
            Log.e("OnlineNormalPlayer", "Empty playerList");
            return;
        }
        int index = new Random().nextInt(playerList.size());
        PlayerInfo toPlayerInfo = playerList.get(index);

        PlayerSourceFacade.newSingleton().setSourceType(toPlayerInfo.getType());
        toPlayerInfo.setAction(PlayerAction.PLAY_LIST);
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(toPlayerInfo, new IFetchListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(Object t) {
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }
}
