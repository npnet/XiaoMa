package com.xiaoma.xting.common.playerSource.info.callback;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.player.model.AcrRadioBean;
import com.xiaoma.xting.sdk.AcrFactory;
import com.xiaoma.xting.sdk.LocalFMStatusListener;
import com.xiaoma.xting.sdk.listener.OnRadioResultListener;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/4
 */
public class YQRadioPlayerStatusListenerImpl implements LocalFMStatusListener {

    private static final String TAG = YQRadioPlayerStatusListenerImpl.class.getSimpleName();

    private boolean mSwitchBandF;
    private boolean mSearchFromLauncherF;
    /**
     * 用于判断是否是外部音源抢夺焦点，在fm关闭时，是否需要将缩略播放器状态置为默认状态
     */
    private boolean mSwitchByExternalF;

    private YQRadioPlayerStatusListenerImpl() {
        mSwitchBandF = false;
        mSearchFromLauncherF = false;
        mSwitchByExternalF = false;
    }

    public static YQRadioPlayerStatusListenerImpl newSingleton() {
        return Holder.sINSTANCE;
    }

    public void searchFromLauncher(boolean fromLauncher) {
        mSearchFromLauncherF = fromLauncher;
    }

    /**
     * 用于设置判断是否是外部音源抢夺焦点的标志位，在fm关闭时，是否需要将缩略播放器状态置为默认状态
     *
     * @param byInternal 是否是外部音源抢的音频焦点
     */
    public void setSwitchByExternal(boolean byInternal) {
        mSwitchByExternalF = byInternal;
    }

    @Override
    public void onRadioOpen() {
        Log.d(TAG, "{onRadioOpen}-[] : " + mSearchFromLauncherF);
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        PlayerInfo curPlayerInfo = PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo();
        RemoteIatManager.getInstance().uploadPlayState(true, AppType.RADIO);
        if (curPlayerInfo != null) {
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(curPlayerInfo);
            shareStatusToIflytek(curPlayerInfo, true);
        } else {
            PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.NONE);
            BaseChannelBean lastYQPlayer = SharedPrefUtils.getLastYQPlayerInfo(true);
            PlayerInfo playerInfo = BeanConverter.toPlayerInfo(lastYQPlayer);
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);
            shareStatusToIflytek(playerInfo, true);
        }

    }

    private void shareStatusToIflytek(PlayerInfo playerInfo, boolean isOpen) {
        if (playerInfo != null && playerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
            SourceType sourceType = null;
            if (playerInfo.getSourceSubType() == PlayerSourceSubType.YQ_RADIO_FM) {
                sourceType = SourceType.FM;
            } else {
                sourceType = SourceType.AM;
            }

            SourceUtils.setSourceStatus(sourceType, isOpen);
        }
    }

    @Override
    public void onRadioClose(XMRadioStation xmRadioStation) {
        Log.d(TAG, "{onRadioClose}-[] : ");
        shareAudioState(AudioConstants.AudioStatus.EXIT);
        shareStatusToIflytek(PlayerInfoImpl.newSingleton().getPlayerInfo(), false);
//        RemoteIatManager.getInstance().uploadPlayState(false, AppType.RADIO);
        if (mSwitchByExternalF) {
            mSwitchByExternalF = false;
            PlayerInfoImpl.newSingleton().reset();
        }
    }

    @Override
    public void onTuningAM(int frequency) {
        Log.d(TAG, "{onTuningAM}-[frequency] : " + frequency);
        //修改备注，这里是onTuning回调，SDK实际上还没有真的切换，不能从SDK获取当前频率，只能用回调的频率
        BaseChannelBean channelBean = XtingUtils.getChannelByValue(frequency);
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(channelBean));
        shareAudioState(AudioConstants.AudioStatus.PLAYING);
        SharedPrefUtils.cacheYQRadioLast(frequency);
        SharedPrefUtils.cacheLastAM(frequency);
    }

    @Override
    public void onTuningFM(int frequency) {
        Log.d(TAG, "{onTuningFM}-[frequency] : " + frequency);
        //修改备注，这里是onTuning回调，SDK实际上还没有真的切换，不能从SDK获取当前频率，只能用回调的频率
        BaseChannelBean channelBean = XtingUtils.getChannelByValue(frequency);
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(channelBean));
        shareAudioState(AudioConstants.AudioStatus.PLAYING);
        SharedPrefUtils.cacheYQRadioLast(frequency);
        SharedPrefUtils.cacheLastFM(frequency);
    }

    @Override
    public void onScanStart() {
        Log.d(TAG, "{onScanStart}-[] : ");
        shareAudioState(AudioConstants.AudioStatus.LOADING);
    }

    @Override
    public void onNewStation(final XMRadioStation station) {
        Log.d(TAG, "{onNewStation}-[] : ");
        PlayerInfo curPlayerInfo = PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo();
        if (curPlayerInfo != null) {
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(curPlayerInfo);
            shareAudioState(AudioConstants.AudioStatus.PLAYING);
        } else {
            if (!mSwitchBandF) {
                SharedPrefUtils.cacheYQRadioLast(station.getChannel());
                if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
                    SharedPrefUtils.cacheLastFM(station.getChannel());
                } else {
                    SharedPrefUtils.cacheLastAM(station.getChannel());
                }
            }
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    PlayerInfo playerInfo = BeanConverter.toPlayerInfo(station);
                    if (playerInfo != null) {
                        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);
                        shareAudioState(AudioConstants.AudioStatus.PLAYING);
                    } else {
                        requestRadio(station);
                    }
                }
            }, mSwitchBandF ? 0 : 500);
            mSwitchBandF = false;
        }
//        if (mSearchFromLauncherF) {
//            PlayerInfo curPlayerInfo = PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo();
//            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(curPlayerInfo);
//        } else {
//            if (!mSwitchBandF) {
//                SharedPrefUtils.cacheYQRadioLast(station.getChannel());
//            }
//            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    PlayerInfo playerInfo = BeanConverter.toPlayerInfo(station);
//                    if (playerInfo != null) {
//                        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);
//                    } else {
//                        requestRadio(station);
//                    }
//                }
//            }, mSwitchBandF ? 0 : 500);
//            mSwitchBandF = false;
//        }

    }

    @Override
    public void onScanAllResult(List<XMRadioStation> stations) {
        Log.d(TAG, "{onScanAllResult}-[] : ");
    }

    @Override
    public void onBandChanged(BandType band) {
        Log.d(TAG, "{onBandChanged}-[] : ");
        mSwitchBandF = true;
//        if (mSearchFromLauncherF) {
        PlayerInfo curPlayerInfo = PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo();
        if (curPlayerInfo != null) {
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(curPlayerInfo);
            shareAudioState(AudioConstants.AudioStatus.PLAYING);
        }
//        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(int code, String msg) {
        Log.d(TAG, "{onError}-[] : ");
    }

    interface Holder {
        YQRadioPlayerStatusListenerImpl sINSTANCE = new YQRadioPlayerStatusListenerImpl();
    }

    private void requestRadio(XMRadioStation station) {
        String lat = "";
        String lon = "";
        LocationInfo location = LocationManager.getInstance().getRealCurrentLocation();
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }
        final BaseChannelBean curChannel;
        if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
            curChannel = new FMChannelBean(station.getChannel());
        } else {
            curChannel = new AMChannelBean(station.getChannel());
        }
        AcrFactory.getInstance().getSDK().requestRadioMetadata(station.getRadioBand(), lat, lon,
                station.getChannel(), new OnRadioResultListener() {
                    @Override
                    public void onRadioResult(String json) {
                        AcrRadioBean mRadioItem = GsonHelper.fromJson(json, AcrRadioBean.class);
                        if (mRadioItem != null
                                && mRadioItem.status.code == 0
                                && !CollectionUtil.isListEmpty(mRadioItem.data.list)
                                && mRadioItem.data.list.get(0) != null) {
                            curChannel.setChannelCover(mRadioItem.data.list.get(0).logo);
                            if (mRadioItem.data.list.get(0).external_ids != null
                                    && !TextUtils.isEmpty(mRadioItem.data.list.get(0).external_ids.ximalaya)) {
                                curChannel.setXmlyId(mRadioItem.data.list.get(0).external_ids.ximalaya);
                            }
                            if (!TextUtils.isEmpty(mRadioItem.data.list.get(0).name)) {
                                curChannel.setChannelName(mRadioItem.data.list.get(0).name);
                            }
                        }
                        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(curChannel));
                        shareAudioState(AudioConstants.AudioStatus.PLAYING);
                    }
                });
    }

    private void shareAudioState(@AudioConstants.AudioState int audioState) {
        XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).shareAudioState(audioState, AudioConstants.AudioTypes.XTING);
    }
}
