package com.xiaoma.xting.assistant;

import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.handler.impl.PlayRadioHandler;
import com.xiaoma.xting.common.handler.impl.SearchRadiosHandler;
import com.xiaoma.xting.common.handler.impl.XtingPlayHistoryHandler;
import com.xiaoma.xting.common.handler.impl.XtingStoreAlbumPlayerHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.sdk.LocalFMFactory;

import java.util.List;
import java.util.Objects;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/4
 */
public class AssistantHandler {

    public static final long LONG_INVALID = -1;

    public static boolean handleAssistantAction(int action, Bundle data, ClientCallback callback) {
        if (data != null) {
            data.setClassLoader(AssistantHandler.class.getClassLoader());
        }
        if (!handleOnlinePart(action, data, callback)) {
            return handleLocalPart(action, data, callback);
        }
        return true;
    }

    private static boolean handleOnlinePart(int action, Bundle data, final ClientCallback callback) {
        int playSourceType = PlayerSourceType.DEFAULT;
        switch (action) {
            case CenterConstants.XtingThirdAction.PLAY_PROGRAM_COLLECTION:
                XtingStoreAlbumPlayerHandler playStoreAlbumHandler = new XtingStoreAlbumPlayerHandler(callback);
                playStoreAlbumHandler.handleRequestAction(null);
                break;
            case CenterConstants.XtingThirdAction.COLLECT_PROGRAM:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    int subscribe = PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(true);
                    PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(true);
                    dispatchOperateResult(subscribe == PlayerOperate.SUCCESS, callback);
                }

                break;
            case CenterConstants.XtingThirdAction.CANCEL_COLLECT_PROGRAM:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    int subscribe = PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(false);
                    PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(false);
                    dispatchOperateResult(subscribe == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.PRE_PROGRAM:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    int operateResult = PlayerSourceFacade.newSingleton().getPlayerControl().playPre();
                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.NEXT_PROGRAM:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    int operateResult = PlayerSourceFacade.newSingleton().getPlayerControl().playNext();
                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.PAUSE_PROGRAM:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    PlayerSourceFacade.newSingleton().getPlayerControl().pause();
                    dispatchOperateResult(true, callback);
                }

                break;
            case CenterConstants.XtingThirdAction.CONTINUE_PLAY_PROGRAM:
            case CenterConstants.XtingThirdAction.PLAY_ONLINE_RADIO_STATION:
                int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
                if (sourceType == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    if (sourceType == PlayerSourceType.RADIO_YQ) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().play();
                        dispatchOperateResult(true, callback);
                    } else {
                        if (PlayerSourceFacade.newSingleton().getPlayerControl().getPlayListSize() == 0) {
                            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                            playerInfo.setAction(PlayerAction.PLAY_LIST);
                            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                                @Override
                                public void onLoading() {

                                }

                                @Override
                                public void onSuccess(Object t) {
                                    dispatchOperateResult(true, callback);
                                }

                                @Override
                                public void onFail() {
                                    dispatchOperateResult(false, callback);
                                }

                                @Override
                                public void onError(int code, String msg) {
                                    dispatchOperateResult(false, callback);
                                }
                            });
                        } else {
                            PlayerSourceFacade.newSingleton().getPlayerControl().play();
                        }

                    }

                }
                break;
            case CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_ORDER:
                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
                    dispatchOperateResult(false, callback);
                } else {
                    int operateResult = PlayerSourceFacade.newSingleton().setPlayMode(PlayerPlayMode.SEQUENTIAL);
                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_RANDOM:
                //考拉PGC电台不支持设置播放模式
                playSourceType = PlayerSourceFacade.newSingleton().getSourceType();
                if (playSourceType == PlayerSourceType.DEFAULT
                        || (playSourceType == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio())) {
                    dispatchOperateResult(false, callback);
                } else {
                    int operateResult = PlayerSourceFacade.newSingleton().setPlayMode(PlayerPlayMode.SHUFFLE);
                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_CYCLE:
                playSourceType = PlayerSourceFacade.newSingleton().getSourceType();
                if (playSourceType == PlayerSourceType.DEFAULT
                        || (playSourceType == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio())) {
                    dispatchOperateResult(false, callback);
                } else {
                    int operateResult = PlayerSourceFacade.newSingleton().setPlayMode(PlayerPlayMode.SINGLE_LOOP);
                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.SWITCH_PLAY_MODE_TO_LOOP:
                //奔腾电台 之后不再支持列表循环模式
//                if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT) {
//
//                } else {
//                    int operateResult = PlayerSourceFacade.newSingleton().setPlayMode(PlayerPlayMode.LIST_LOOP);
//                    dispatchOperateResult(operateResult == PlayerOperate.SUCCESS, callback);
//                }
                dispatchOperateResult(false, callback);
                break;
            case CenterConstants.XtingThirdAction.PLAY_ALBUM:
                long albumId = data.getLong(CenterConstants.XtingThirdBundleKey.ALBUM_ID, LONG_INVALID);
                if (!Objects.equals(albumId, LONG_INVALID)) {
                    PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                    PlayerInfo playerInfo = new PlayerInfo();
                    playerInfo.setType(PlayerSourceType.HIMALAYAN);
                    playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
                    playerInfo.setPage(1);
                    playerInfo.setAlbumId(albumId);
                    playerInfo.setAction(PlayerAction.PLAY_LIST);
                    PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onSuccess(Object t) {
                            dispatchOperateResult(true, callback);
                        }

                        @Override
                        public void onFail() {
                            dispatchOperateResult(false, callback);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            dispatchOperateResult(false, callback);
                        }
                    });
                } else {
                    dispatchOperateResult(false, callback);
                }
                break;
            case CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_NAME:
                SearchRadiosHandler searchRadiosHandler = new SearchRadiosHandler(callback);
                String name = data.getString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_NAME);
                boolean localTTS = data.getBoolean(CenterConstants.XtingThirdBundleKey.RADIO_LOCAL_TTS,false);
                Bundle nameBundle = new Bundle();
                nameBundle.putString("Key", name);
                nameBundle.putBoolean("play", true);
                nameBundle.putBoolean(CenterConstants.XtingThirdBundleKey.RADIO_LOCAL_TTS,localTTS);
                searchRadiosHandler.handleRequestAction(nameBundle);
                break;
            case CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_TYPE:
                SearchRadiosHandler typeRadiosHandler = new SearchRadiosHandler(callback);
                String type = data.getString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_TYPE);
                Bundle typeBundle = new Bundle();
                typeBundle.putString("Key", type);
                typeBundle.putBoolean("play", true);
                typeRadiosHandler.handleRequestAction(typeBundle);
                break;
            case CenterConstants.XtingThirdAction.PLAY_PROGRAM:
                XtingPlayHistoryHandler historyPlayHandler = new XtingPlayHistoryHandler(callback);
                historyPlayHandler.handleRequestAction(null);
                break;
            case CenterConstants.XtingThirdAction.PLAY_PROGRAM_BY_ID:
                int playListSize = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayListSize();
                if (playListSize > 0) {
                    int index = data.getInt(CenterConstants.XtingThirdBundleKey.PROGRAM_INDEX);
                    long programId = data.getLong(CenterConstants.XtingThirdBundleKey.PROGRAM_ID);
                    if (playListSize > index) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(index);
                    } else {
                        List<PlayerInfo> playList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
                        for (int i = 0; i < playListSize; i++) {
                            if (playList.get(i).getProgramId() == programId) {
                                PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(index);
                                break;
                            }
                        }
                    }
                }
                break;
            case CenterConstants.XtingThirdAction.PLAY_RADIO_BY_ID:
                PlayRadioHandler playRadioHandler = new PlayRadioHandler(callback);
                playRadioHandler.handleRequestAction(null);
                break;
            case CenterConstants.XtingThirdAction.GET_HAS_STATIONS:
                List<FMChannelBean> fmChannelBeans = LocalPlayList.getInstance().getFmChannelBeans();
                List<AMChannelBean> amChannelBeans = LocalPlayList.getInstance().getAmChannelBeans();
                if (!CollectionUtil.isListEmpty(fmChannelBeans) || !CollectionUtil.isListEmpty(amChannelBeans)) {
                    dispatchOperateResult(true, callback);
                } else {
                    dispatchOperateResult(false, callback);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private static boolean handleLocalPart(int action, Bundle data, final ClientCallback callback) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        switch (action) {
            case CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_COLLECTION:
                boolean b = LocalFMOperateManager.newSingleton().playLocalFMAtFirst();
                dispatchOperateResult(b, callback);
                break;
            case CenterConstants.XtingThirdAction.COLLECT_RADIO_STATION:
                boolean collectResult = LocalFMOperateManager.newSingleton().collectCurFM(true);
                dispatchOperateResult(collectResult, callback);
                break;
            case CenterConstants.XtingThirdAction.CANCEL_COLLECT_RADIO_STATION:
                boolean cancelResult = LocalFMOperateManager.newSingleton().collectCurFM(false);
                dispatchOperateResult(cancelResult, callback);
                break;
            case CenterConstants.XtingThirdAction.PRE_RADIO_STATION:
                boolean preResult = LocalFMOperateManager.newSingleton().playClosestChannel(false);
                dispatchOperateResult(preResult, callback);
                break;
            case CenterConstants.XtingThirdAction.NEXT_RADIO_STATION:
                boolean nextResult = LocalFMOperateManager.newSingleton().playClosestChannel(true);
                dispatchOperateResult(nextResult, callback);
                break;
            case CenterConstants.XtingThirdAction.PAUSE_RADIO_STATION:
                LocalFMFactory.getSDK().closeRadio();
                PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.RADIO_YQ).exitPlayer();
                dispatchOperateResult(true, callback);
                break;
            case CenterConstants.XtingThirdAction.PLAY_RADIO_STATION:
                LocalFMFactory.getSDK().openRadio();
                dispatchOperateResult(true, callback);
                break;
            case CenterConstants.XtingThirdAction.SCAN_RADIO_STATION:
                LocalFMOperateManager.newSingleton().searchLocalFM();
                dispatchOperateResult(true, callback);
                break;
            case CenterConstants.XtingThirdAction.GET_RADIO_STATION_INFO:
                LocalFMOperateManager.newSingleton().getRadioInfo(new SimpleCallback<BaseChannelBean>() {
                    @Override
                    public void onSuccess(BaseChannelBean model) {
                        Bundle bundle = new Bundle();
                        bundle.putDouble(CenterConstants.XtingThirdBundleKey.RADIO_STATION_FREQUENCY, model.getChannelValue());
                        bundle.putString(CenterConstants.XtingThirdBundleKey.RADIO_STATION_NAME, model.getChannelName());
                        bundle.putBoolean(AudioConstants.BundleKey.RESULT, true);

                        dispatchOperateResult(bundle, callback);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        dispatchOperateResult(false, callback);
                    }
                });

                break;
            case CenterConstants.XtingThirdAction.PLAY_LCOAL_RADIO_STATION:
                LocalFMFactory.getSDK().openRadio();
                dispatchOperateResult(true, callback);
                break;
            case CenterConstants.XtingThirdAction.PLAY_RADIO_STATION_BY_FREQUENCY:
                String band = data.getString(CenterConstants.XtingThirdBundleKey.AM_OR_FM, "fm");//值为"fm" 或者"am"
                int bandValue = band.equals("am") ? XtingConstants.FMAM.TYPE_AM : XtingConstants.FMAM.TYPE_FM;
                int channel = data.getInt(CenterConstants.XtingThirdBundleKey.RADIO_STATION_FREQUENCY, 87500);
                LocalFMOperateManager.newSingleton().playChannel(bandValue, channel);
                dispatchOperateResult(true, callback);
                break;
            case CenterConstants.XtingThirdAction.PLAY_AM:
                BaseChannelBean cacheLastAM = SharedPrefUtils.getCacheLastAM();
                boolean b1 = LocalFMOperateManager.newSingleton().playChannel(cacheLastAM);
                dispatchOperateResult(b1, callback);
                break;
            case CenterConstants.XtingThirdAction.PLAY_FM:
                BaseChannelBean cacheLastFM = SharedPrefUtils.getCacheLastFM();
                boolean b2 = LocalFMOperateManager.newSingleton().playChannel(cacheLastFM);
                dispatchOperateResult(b2, callback);
                break;
            case CenterConstants.XtingThirdAction.GET_PLAY_STATE:
                //播放状态通过XtingAudioClient分发出去
                XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).onPlayerStatusChanged(PlayerInfoImpl.newSingleton().getPlayStatus());
                break;
        }
        return false;
    }

    private static void dispatchOperateResult(boolean operateOk, ClientCallback callback) {
        if (callback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putBoolean(AudioConstants.BundleKey.RESULT, operateOk);
            dispatchOperateResult(callbackData, callback);
        }
    }

    private static void dispatchOperateResult(Bundle bundle, ClientCallback callback) {
        if (callback != null) {
            callback.setData(bundle);
            callback.callback();
        }
    }
}
