package com.xiaoma.xting.common.playerSource.control.controlImpl;

import android.content.Context;

import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.callback.YQRadioPlayerStatusListenerImpl;
import com.xiaoma.xting.common.playerSource.info.db.SubscribeDao;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.sdk.LocalFM;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class YQRadioControl implements IPlayerControl<BaseChannelBean> {

    private static final String TAG = "YQ_Control";
    private final LocalFM mControl;

    public YQRadioControl() {
        mControl = LocalFMFactory.getSDK();
    }

    @Override
    public void init(Context context) {
        PrintInfo.print(TAG, "init");
        if (!mControl.isRadioOpen()) {
            mControl.addLocalFMStatusListener(YQRadioPlayerStatusListenerImpl.newSingleton());
            BaseChannelBean channelBean = SharedPrefUtils.getLastYQPlayerInfo(true);
            mControl.openRadio();
            if (channelBean != null) {
                LocalFMOperateManager.newSingleton().playChannel(channelBean);
            } else {
                mControl.tuneFM(XtingConstants.FMAM.getFMStart());
            }
            AudioFocusManager.getInstance().requestFMFocus();
        }
    }

    @Override
    public boolean isPlaying() {
        if (!mControl.isRadioOpen()) return false;
        return mControl.getCurrentStation() != null;
    }

    @Override
    public boolean isCurPlayerInfoAlive() {
        return mControl.isRadioOpen();
    }

    @Override
    @PlayerOperate
    public int play() {
        if (!mControl.isRadioOpen()) {
            mControl.openRadio();
        }
        return PlayerOperate.DEFAULT;
    }

    @Override
    @PlayerOperate
    public int pause() {
        mControl.closeRadio();
        AudioFocusManager.getInstance().abandonFMFocus();
//        SharedPrefUtils.clearCachedPlayerInfo(AppHolder.getInstance().getAppContext());
//        mControl.removeLocalFMStatusListener(YQRadioPlayerStatusListenerImpl.newSingleton());
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.DEFAULT);
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
        return PlayerOperate.SUCCESS;
    }

    @Override
    public int pause(boolean abandonFocus) {
        mControl.cancel();
        mControl.closeRadio();
        if (abandonFocus) {
//            AudioFocusManager.getInstance().ab
        }
        return 0;
    }

    @Override
    public void stop() {
        pause(false);
    }

    @Override
    public int playWithModel(BaseChannelBean channelBean) {
        if (channelBean == null) return PlayerOperate.FAIL;
        if (channelBean instanceof FMChannelBean) {
            playChannel(XtingConstants.FMAM.TYPE_FM, channelBean.getChannelValue());
        } else {
            playChannel(XtingConstants.FMAM.TYPE_AM, channelBean.getChannelValue());
        }
        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int playWithIndex(int index) {
        return 0;
    }

    @Override
    @PlayerOperate
    public int playPre() {
        boolean preResult = LocalFMOperateManager.newSingleton().playClosestChannel(false);
        return preResult ? PlayerOperate.SUCCESS : PlayerOperate.FAIL;
    }

    @Override
    @PlayerOperate
    public int playNext() {
        boolean nextResult = LocalFMOperateManager.newSingleton().playClosestChannel(true);
        return nextResult ? PlayerOperate.SUCCESS : PlayerOperate.FAIL;
    }

    @Override
    @PlayerOperate
    public int seekProgress(long progress) {
        return PlayerOperate.UNSUPPORTED;
    }

    @Override
    @PlayerOperate
    public int subscribe(boolean subscribe) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        SubscribeInfo subscribeInfo = getSubscribeDao().selectBy(playerInfo.getType(), playerInfo.getProgramId());
        if (subscribeInfo == null) {
            if (subscribe) {
                getSubscribeDao().insert(BeanConverter.toSubscribeInfo(playerInfo));
                return PlayerOperate.SUCCESS;
            }
        } else {
            if (!subscribe) {
                getSubscribeDao().delete(BeanConverter.toSubscribeInfo(playerInfo));
                return PlayerOperate.SUCCESS;
            }
        }
        return PlayerOperate.DEFAULT;
    }

    @Override
    @PlayerOperate
    public int setPlayMode(int mode) {
        return PlayerOperate.UNSUPPORTED;
    }

    @Override
    public int getPlayIndex() {
        PlayerInfo curPlayerInfo = getCurPlayerInfo();
        PrintInfo.print(TAG, "getPlayIndex", String.format("curPlayerInfo = %1$s", curPlayerInfo));
        if (curPlayerInfo == null) {
            return 0;
        }
        if (curPlayerInfo.getAlbumId() == -1) {
            return 0;
        } else {
            List<? extends XMPlayableModel> playerList = OnlineFMPlayerFactory.getPlayer().getListInPlayer();
            if (playerList == null || playerList.size() <= 1) return 0;
            return HimalayanPlayerUtils.checkIndexWithTime((List<XMSchedule>) playerList);
        }
    }

    @Override
    public int getPlayListSize() {
        return 0;
    }

    @Override
    public List<PlayerInfo> getPlayList() {
        List<PlayerInfo> playerInfoList = new ArrayList<>();
        PlayerInfo curPlayerInfo = getCurPlayerInfo();
        if (curPlayerInfo == null) {
            curPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        }
        if (curPlayerInfo != null && curPlayerInfo.getAlbumId() == -1) {
            playerInfoList.add(curPlayerInfo);
        } else {
            List<? extends XMPlayableModel> playerList = OnlineFMPlayerFactory.getPlayer().getListInPlayer();
            if (playerList == null || playerList.isEmpty()) return null;
            for (XMPlayableModel xmPlayableModel : playerList) {
                PlayerInfo playerInfo = BeanConverter.toPlayerInfo(xmPlayableModel);
                playerInfo.setType(PlayerSourceType.RADIO_YQ);
                playerInfo.setSourceSubType(curPlayerInfo.getSourceSubType());
                playerInfoList.add(playerInfo);
            }
        }
        return playerInfoList;
    }

    @Override
    public PlayerInfo getCurPlayerInfo() {
        BaseChannelBean currentRadioInfo = LocalFMOperateManager.newSingleton().getCurrentRadioInfo();
        return BeanConverter.toPlayerInfo(currentRadioInfo);
    }

    @Override
    public void setVoiceScale(float voiceScale) {
        // TODO: 2019/6/22 本地音量设置
    }

    @Override
    public void switchPlayerAlbum(BaseChannelBean channelBean) {
        if (channelBean == null) {
            return;
        }
        BandType band = mControl.getCurrentBand();
        if (band == null) {
            return;
        }
        if (channelBean instanceof FMChannelBean) {
            if (band.getBand() != BandType.FM.getBand()) {
                mControl.switchBand(BandType.FM);
            }
        } else {
            if (band.getBand() != BandType.AM.getBand()) {
                mControl.switchBand(BandType.AM);
            }
        }
    }

    @Override
    public void switchPlayerSource(@PlayerSourceType int type) {
        if (type != PlayerSourceType.RADIO_YQ) {
            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
            if (playerInfo != null && playerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
                int sourceSubType = playerInfo.getSourceSubType();
                if (sourceSubType == PlayerSourceSubType.YQ_RADIO_FM) {
                    SourceUtils.setSourceStatus(SourceType.FM, false);
                } else {
                    SourceUtils.setSourceStatus(SourceType.AM, false);
                }
            }
            RemoteIatManager.getInstance().uploadPlayState(false, AppType.RADIO);
            mControl.closeRadio();
//            PlayerInfoImpl.newSingleton().reset();
        }
    }

    @Override
    public void exitPlayer() {
        mControl.cancel();
        mControl.closeRadio();
        AudioFocusManager.getInstance().abandonFMFocus();
//        SharedPrefUtils.clearCachedPlayerInfo(AppHolder.getInstance().getAppContext());
//        mControl.removeLocalFMStatusListener(YQRadioPlayerStatusListenerImpl.newSingleton());
//        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.DEFAULT);
//        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
        PlayerSourceFacade.newSingleton().switchPlayerType();
    }

    private void playChannel(int bandValue, int channel) {
        if (!mControl.isRadioOpen()) mControl.openRadio();
        BandType band = mControl.getCurrentBand();
        if (band != null && band.getBand() != bandValue) {
            mControl.switchBand(BandType.valueOf(bandValue));
        }

        if (bandValue == XtingConstants.FMAM.TYPE_FM) {
            mControl.tuneFM(channel);
        } else {
            mControl.tuneAM(channel);
        }
    }

    private SubscribeDao getSubscribeDao() {
        return XtingUtils.getSubscribeDao();
    }
}
