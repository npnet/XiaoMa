package com.xiaoma.xting.common.playerSource.info.callback;

import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.time.ISystemTimeChangeListener;
import com.xiaoma.xting.common.playerSource.time.SystemLivingTimeReceiver;
import com.xiaoma.xting.online.model.PlayerState;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.PlayerStatusListener;
import com.xiaoma.xting.sdk.bean.XMAdvertis;
import com.xiaoma.xting.sdk.bean.XMAdvertisList;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/10/23
 */
public class HimalayanStatusListenerImpl implements PlayerStatusListener, ISystemTimeChangeListener {

    public static final String TAG = "Callback_Himalayan";
    private final PlayerInfoImpl mPlayerInfoListener;
    private XMPlayableModel mCurPlayModel;

    public HimalayanStatusListenerImpl() {
        mPlayerInfoListener = PlayerInfoImpl.newSingleton();
    }

    public static HimalayanStatusListenerImpl newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    public void onPlayStart() {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PLAYING);
        XMPlayableModel currSound = OnlineFMPlayerFactory.getPlayer().getCurrSound();
//        if (mCurPlayModel == null
//                || (currSound != null && mCurPlayModel.getDataId() != currSound.getDataId())) {
//            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(currSound));
//        } else {
//        }
        if (currSound != null) {
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(currSound));
        }
        logInfo("onPlayStart");
    }

    @Override
    public void onPlayPause() {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PAUSE);
        logInfo("onPlayPause");
    }

    @Override
    public void onPlayStop() {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PAUSE);
        logInfo("onPlayStop");
    }

    @Override
    public void onSoundPlayComplete() {
        if (OnlineFMPlayerFactory.getPlayer().getPlayerState() == PlayerState.STARTED) {
            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PLAYING);
        } else {
            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PAUSE);
        }
        logInfo("onSoundPlayComplete: ");
    }

    @Override
    public void onSoundPrepared() {
        if (getState() == PlayerState.STARTED) {
            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PLAYING);
        } else {
            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PAUSE);
        }

        logInfo("onSoundPrepared: ");
    }

    @Override
    public void onSoundSwitch(XMPlayableModel curSound) {
        logInfo("onSoundSwitch: ");
        if (curSound == null) {
            XMPlayableModel currSound = OnlineFMPlayerFactory.getPlayer().getCurrSound();
            if (XMPlayableModel.KIND_TRACK.equals(currSound.getKind())) {
                if (PlayerSourceFacade.newSingleton().getPlayerControl().playNext() == PlayerOperate.FAIL) {
                    OnlineFMPlayerFactory.getPlayer().pause();
                    onSoundPlayComplete();
                }
            } else {
                OnlineFMPlayerFactory.getPlayer().pause();
                onSoundPlayComplete();
            }
        } else {
            if (getState() == PlayerState.IDLE) {
                mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.LOADING);
            } else {
                mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PAUSE);
            }
            mCurPlayModel = curSound;
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(curSound));

        }
    }

    @Override
    public void onBufferingStart() {
        if (getState() == PlayerState.STARTED) {
            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PLAYING);
        } else {
//            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.LOADING);
        }
        logInfo("onBufferingStart: ");
    }

    @Override
    public void onBufferingStop() {
//        if (getState() == PlayerState.STARTED) {
//            mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.PLAYING);
//        }
        logInfo("onBufferingStop: ");
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        mPlayerInfoListener.onPlayerProgress(currPos, duration);
        if (duration == 0) {
            SystemLivingTimeReceiver.newSingleton().register(AppHolder.getInstance().getAppContext(), this);
        } else {
            if (mCurPlayModel != null) {
                SystemLivingTimeReceiver.newSingleton().unRegisterReceiver(AppHolder.getInstance().getAppContext(), this);
            }
        }
        logInfo("onPlayProgress: " + currPos + " - " + duration);
    }

    @Override
    public void onBufferProgress(int percent) {
        logInfo("onBufferProgress: ");
    }

    @Override
    public void onError(Exception exception) {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.ERROR_BY_PLAYER);
        logInfo("onError: " + exception);
    }

    @Override
    public void onStartGetAdsInfo() {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.LOADING);
        logInfo("onStartGetAdsInfo: ");
    }

    @Override
    public void onGetAdsInfo(XMAdvertisList ads) {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.LOADING);
        logInfo("onGetAdsInfo: ");
    }

    @Override
    public void onStartPlayAds(XMAdvertis ad, int position) {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.LOADING);

        logInfo("onStartPlayAds: ");
    }

    @Override
    public void onAdsStartBuffering() {
        logInfo("onAdsStartBuffering: ");
    }

    @Override
    public void onAdsStopBuffering() {
        logInfo("onAdsStopBuffering: ");
    }

    /**
     * 广告播放错误
     */
    @Override
    public void onError(int what, int extra) {
        mPlayerInfoListener.onPlayerStatusChanged(PlayerStatus.ERROR_BY_PLAYER);
        logInfo("onError: " + what + " / " + extra);
    }

    @Override
    public void onCompletePlayAds() {
        //针对电台 直播 波折突然暂停
        PlayerState state = getState();
        if (state == PlayerState.PAUSED) {
            logInfo("onCompletePlayAds: play manual");
            OnlineFMPlayerFactory.getPlayer().play();
        }
        logInfo("onCompletePlayAds: ");
    }

    private PlayerState getState() {
        return OnlineFMPlayerFactory.getPlayer().getPlayerState();
    }

    private void logInfo(String methodName) {
        Log.d(TAG, String.format("Method [ %1$s ] : State = ", methodName) + getState());
    }

    @Override
    public void onMinuteChanged() {
        Log.d(TAG, "{onMinuteChanged}-[] : ");
        List<? extends XMPlayableModel> listInPlayer = OnlineFMPlayerFactory.getPlayer().getListInPlayer();
        if (listInPlayer != null && listInPlayer.size() > 1) {
            String kind = listInPlayer.get(0).getKind();
            if (XMPlayableModel.KIND_SCHEDULE.equals(kind)) {
                int playIndex = HimalayanPlayerUtils.checkIndexWithTime((List<XMSchedule>) listInPlayer);
                int curPlayIndex = OnlineFMPlayerFactory.getPlayer().getPlayIndex();
                Log.d(TAG, "{onMinuteChanged}-[timePlayIndex / curPlayIndex] : " + playIndex + " / " + curPlayIndex);
                if (playIndex != curPlayIndex) {
                    if (!OnlineFMPlayerFactory.getPlayer().isPlaying()) return;
                    OnlineFMPlayerFactory.getPlayer().play(playIndex);
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(listInPlayer.get(playIndex)));
                }
                return;
            }
        }
        SystemLivingTimeReceiver.newSingleton().unRegisterReceiver(AppHolder.getInstance().getAppContext(), this);
    }

    @Override
    public void onSystemTimeChanged() {
        Log.d(TAG, "{onSystemTimeChanged}-[] : ");
    }

    @Override
    public void onSystemDateChanged() {
        Log.d(TAG, "{onSystemDateChanged}-[] : ");
    }

    interface Holder {
        HimalayanStatusListenerImpl sINSTANCE = new HimalayanStatusListenerImpl();
    }
}
