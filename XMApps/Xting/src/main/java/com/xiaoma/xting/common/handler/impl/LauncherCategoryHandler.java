package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.callback.YQRadioPlayerStatusListenerImpl;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.koala.KoalaRequest;
import com.xiaoma.xting.koala.bean.XMColumnMember;
import com.xiaoma.xting.koala.bean.XMRadioDetailColumnMember;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class LauncherCategoryHandler extends AbsActionHandler {

    public LauncherCategoryHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        int categoryId = bundle.getInt(AudioConstants.BundleKey.SEARCH_CATEGORY_ID);
        XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).setLauncherCategoryId(categoryId);
        boolean isLocalFM = bundle.getBoolean(AudioConstants.BundleKey.EXTRA, false);
        if (isLocalFM) {
            fetchLocal(bundle);
        } else {
            boolean isPentiumRadio = bundle.getBoolean(AudioConstants.BundleKey.EXTRA_2, false);
            if (isPentiumRadio) { //为了避免变化,所以每次都从网络上拉取
                fetchPentiumRadio();
            } else {
                fetchCategory(categoryId, -1);
            }
        }
    }

    private void fetchPentiumRadio() {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.KOALA);
        PlayerSourceFacade.newSingleton().getPlayerControl().switchPlayerAlbum(null);
        KoalaRequest.getColumnTree(true, null, new XMDataCallback<List<XMColumnMember>>() {
            @Override
            public void onSuccess(@Nullable List<XMColumnMember> data) {
                List<PlayerInfo> list = new ArrayList<>();
                Log.d("QQ", "{onSuccess}-[] : ");
                if (data != null) {
                    PlayerInfo playerInfo = BeanConverter.toPlayerInfo((XMRadioDetailColumnMember) data.get(0));
                    playerInfo.setAction(PlayerAction.PLAY_LIST);
                    PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onSuccess(Object t) {
                            share(AudioConstants.AudioResponseCode.SUCCESS);
                        }

                        @Override
                        public void onFail() {
                            share(AudioConstants.AudioResponseCode.FAIL);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            share(AudioConstants.AudioResponseCode.ERROR);
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String msg) {
                share(AudioConstants.AudioResponseCode.ERROR);
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
            }
        });
//
//        KoalaRequest.getAlbumDetails(1100000000078L, new AbsXMDataCallback<XMAlbumDetails>() {
//            @Override
//            public void onSuccess(@Nullable XMAlbumDetails data) {
//                PlayerInfo playerInfo = BeanConverter.toPlayerInfo(data);
//                playerInfo.setAction(PlayerAction.PLAY_LIST);
//
//                PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
//                    @Override
//                    public void onLoading() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object t) {
//                        share(AudioConstants.AudioResponseCode.SUCCESS);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        share(AudioConstants.AudioResponseCode.FAIL);
//                    }
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        share(AudioConstants.AudioResponseCode.ERROR);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//                share(AudioConstants.AudioResponseCode.ERROR);
//            }
//        });
    }

    private void fetchCategory(int categoryId, int page) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_XM);
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setCategoryId(categoryId);
        playerInfo.setPage(page);
        playerInfo.setAction(PlayerAction.PLAY_LIST);
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(Object t) {
                share(AudioConstants.AudioResponseCode.SUCCESS);
            }

            @Override
            public void onFail() {
                share(AudioConstants.AudioResponseCode.FAIL);
            }

            @Override
            public void onError(int code, String msg) {
                share(AudioConstants.AudioResponseCode.ERROR);
            }
        });

    }

    private void fetchLocal(Bundle data) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        YQRadioPlayerStatusListenerImpl.newSingleton().searchFromLauncher(true);
        LocalFMOperateManager.newSingleton().listenSearchResultToLauncher(new LocalFMOperateManager.IOnAutoSearchOkListener() {
            @Override
            public void autoSearchSuccess(int bandType) {
                if (bandType == XtingConstants.FMAM.TYPE_FM) {
                    share(AudioConstants.AudioResponseCode.SUCCESS);
                    YQRadioPlayerStatusListenerImpl.newSingleton().searchFromLauncher(false);
                    LocalFMOperateManager.newSingleton().removeAutoSearchOkListener(this);
                }
            }

            @Override
            public void autoOpenFailed() {
                share(AudioConstants.AudioResponseCode.ERROR);
                YQRadioPlayerStatusListenerImpl.newSingleton().searchFromLauncher(false);
                LocalFMOperateManager.newSingleton().removeAutoSearchOkListener(this);
            }
        });
    }

    private void share(@AudioConstants.AudioResponseCode int code) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, code);
        dispatchRequestCallback(callbackData);
    }
}
