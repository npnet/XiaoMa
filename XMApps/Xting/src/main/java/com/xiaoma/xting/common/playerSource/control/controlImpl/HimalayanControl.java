package com.xiaoma.xting.common.playerSource.control.controlImpl;

import android.content.Context;
import android.util.Log;

import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.callback.HimalayanStatusListenerImpl;
import com.xiaoma.xting.common.playerSource.info.db.SubscribeDao;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;
import com.xiaoma.xting.sdk.OnlineFMPlayer;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/4
 */
public class HimalayanControl implements IPlayerControl<XMPlayableModel> {

    private static final String TAG = HimalayanControl.class.getSimpleName();

    private OnlineFMPlayer mPlayer;
    private @PlayerPlayMode
    int mPlayMode;

    public HimalayanControl() {
        mPlayer = OnlineFMPlayerFactory.getPlayer();
    }

    @Override
    public void init(Context context) {
        Log.d("Jir", "[ init ] at ");
        mPlayMode = PlayerPlayMode.SEQUENTIAL;
        mPlayer.addPlayerStatusListener(HimalayanStatusListenerImpl.newSingleton());
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public boolean isCurPlayerInfoAlive() {
        return mPlayer.getCurrSound() != null;
    }

    @Override
    @PlayerOperate
    public int play() {
        if (!isPlaying()) {
            mPlayer.play();
            return PlayerOperate.SUCCESS;
        }
        return PlayerOperate.FAIL;
    }

    @Override
    @PlayerOperate
    public int pause() {
        PlayerInfoImpl.newSingleton().shareStatusWithAssistant(PlayerStatus.PAUSE, true);
        return pause(true);
    }

    @Override
    public int pause(boolean abandonFocus) {
        mPlayer.pause();
        if (abandonFocus) {
            AudioFocusManager.getInstance().abandonXmlyFocus();
        }
        return PlayerOperate.SUCCESS;
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    @PlayerOperate
    public int playWithModel(XMPlayableModel playableModel) {
        if (XMPlayableModel.KIND_RADIO.equals(playableModel.getKind())) {
            mPlayer.playRadio((XMRadio) playableModel);
            return PlayerOperate.SUCCESS;
        }
        return PlayerOperate.FAIL;
    }

    @Override
    @PlayerOperate
    public int playWithIndex(int index) {
        if (index < 0) index = 0;
        mPlayer.play(index);
        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int playPre() {
        if (mPlayer.hasPreSound() || isSpecialPlayModel()) {
            mPlayer.playPre();
            return PlayerOperate.SUCCESS;
        } else {
            IPlayerFetch playerFetch = PlayerSourceFacade.newSingleton().getPlayerFetch();
            if (!playerFetch.isUpFetchTop()) {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                LoadMoreListenerImpl.newSingleton().addLoadMoreListener(getLoadMoreListener());
                playerFetch.loadMore(false);
            } else {
                return PlayerOperate.FAIL;
            }
        }

        return PlayerOperate.DEFAULT;
    }

    @Override
    @PlayerOperate
    public int playNext() {
        if (mPlayer.hasNextSound() || isSpecialPlayModel()) {
            mPlayer.playNext();
            return PlayerOperate.SUCCESS;
        } else {
            IPlayerFetch playerFetch = PlayerSourceFacade.newSingleton().getPlayerFetch();
            if (!playerFetch.isDownloadBottom()) {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                LoadMoreListenerImpl.newSingleton().addLoadMoreListener(getLoadMoreListener());
                playerFetch.loadMore(true);
            } else {
                return PlayerOperate.FAIL;
            }
        }
        return PlayerOperate.DEFAULT;

    }

    @Override
    @PlayerOperate
    public int seekProgress(long progress) {
        mPlayer.seekTo((int) progress);
        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int subscribe(boolean subscribe) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
//        if (playerInfo.getType() == PlayerSourceType.RADIO_XM) {
//            return PlayerOperate.UNSUPPORTED;
//        }
        SubscribeInfo subscribeInfo = getSubscribeDao().selectBy(PlayerSourceType.HIMALAYAN, playerInfo.getAlbumId());
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
        return PlayerOperate.FAIL;
    }

    @Override
    @PlayerOperate
    public int setPlayMode(int mode) {
        mPlayMode = mode;
        mPlayer.setPlayMode(HimalayanPlayerUtils.mapXmlyPlayMode(mode));
        return PlayerOperate.SUCCESS;
    }

    @Override
    public int getPlayIndex() {
        return mPlayer.getPlayIndex();
    }

    @Override
    public int getPlayListSize() {
        return mPlayer.getPlayListSize();
    }

    @Override
    public List<PlayerInfo> getPlayList() {
        List<? extends XMPlayableModel> playerList = mPlayer.getListInPlayer();
        if (playerList == null || playerList.isEmpty()) return null;
        List<PlayerInfo> playerInfoList = new ArrayList<>();
        for (XMPlayableModel xmPlayableModel : playerList) {
            playerInfoList.add(BeanConverter.toPlayerInfo(xmPlayableModel));
        }
        return playerInfoList;
    }

    @Override
    public PlayerInfo getCurPlayerInfo() {
        return BeanConverter.toPlayerInfo(mPlayer.getCurrSound());
    }

    @Override
    public void setVoiceScale(float voiceScale) {
        mPlayer.setVolume(voiceScale, voiceScale);
    }

    @Override
    public void switchPlayerAlbum(XMPlayableModel playableModel) {
        mPlayer.pause();
        mPlayer.resetPlayList();
        mPlayer.clearPlayCache();
        PlayerInfoImpl.newSingleton().reset();
    }

    @Override
    public void switchPlayerSource(@PlayerSourceType int type) {
        if (type != PlayerSourceType.HIMALAYAN
                && type != PlayerSourceType.RADIO_XM) {
            switchPlayerAlbum(null);
            AudioFocusManager.getInstance().abandonXmlyFocus();
            mPlayer.removePlayerStatusListener(HimalayanStatusListenerImpl.newSingleton());
        }
    }

    @Override
    public void exitPlayer() {
        switchPlayerAlbum(null);
        mPlayer.resetPlayer();
        mPlayer.removePlayerStatusListener(HimalayanStatusListenerImpl.newSingleton());
    }

    private SubscribeDao getSubscribeDao() {
        return XtingUtils.getSubscribeDao();
    }

    private boolean isSpecialPlayModel() {
        return mPlayMode == PlayerPlayMode.LIST_LOOP
                || mPlayMode == PlayerPlayMode.SHUFFLE;
    }

    private ILoadMoreListener getLoadMoreListener() {
        return new ILoadMoreListener() {
            @Override
            public void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list) {
                LoadMoreListenerImpl.newSingleton().removeLoadMoreListener(this);
                if (loadMoreStatus == PlayerLoadMore.LOAD_COMPLETE
                        || loadMoreStatus == PlayerLoadMore.LOAD_END) {
                    if (isDownload) {
                        mPlayer.playNext();
                    } else {
                        mPlayer.playPre();
                    }
                    Log.d(TAG, "{notifyLoadMoreResult}-[LOAD_END / LOAD_COMPLETE] : ");
                } else if (loadMoreStatus == PlayerLoadMore.LOAD_FAIL) {
                    mPlayer.pause();
                    Log.d(TAG, "{notifyLoadMoreResult}-[LOAD_FAIL] : ");
                } else if (loadMoreStatus == PlayerLoadMore.LOAD_ERROR) {
                    mPlayer.pause();
                    Log.d(TAG, "{notifyLoadMoreResult}-[LOAD_ERROR] : ");
                }
            }
        };
    }
}
