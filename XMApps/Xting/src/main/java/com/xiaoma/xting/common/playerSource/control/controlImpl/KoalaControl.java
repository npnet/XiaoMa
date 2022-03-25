package com.xiaoma.xting.common.playerSource.control.controlImpl;

import android.content.Context;

import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.db.SubscribeDao;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.koala.bean.XMPlayItem;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class KoalaControl implements IPlayerControl<XMPlayItem> {

    private KoalaPlayer mPlayer;
    private @PlayerPlayMode
    int mPlayMode;

    public KoalaControl() {
        mPlayer = KoalaPlayer.newSingleton();
    }

    @Override
    public void init(Context context) {
        mPlayer.init(context);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public boolean isCurPlayerInfoAlive() {
        return mPlayer.isCurPlayerInfoAlive();
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
        pause(true);
        return PlayerOperate.SUCCESS;
    }

    @Override
    public int pause(boolean abandonFocus) {
        mPlayer.pause();
        if (abandonFocus) {
            AudioFocusManager.getInstance().abandonKoalaFocus();
        }
        return PlayerOperate.SUCCESS;
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    @PlayerOperate
    public int playWithModel(XMPlayItem xmPlayItem) {
        mPlayer.play(xmPlayItem);
        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int playWithIndex(int index) {
        mPlayer.playWithIndex(index);
        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int playPre() {
        if (mPlayer.hasPre()) {
            mPlayer.playPre();
            return PlayerOperate.SUCCESS;
        } else if (isSpecialPlayModel()) {
            //考拉没有针对第一首 , 并且 随机播放的时候进行处理,所以单独处理
            if (getPlayIndex() == 0) {
                return playNext();
            }
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
        if (mPlayer.hasNext()) {
            mPlayer.playNext();
            return PlayerOperate.SUCCESS;
        } else if (isSpecialPlayModel()) {
            if (getPlayIndex() == getPlayListSize() - 1) {
                return playPre();
            }
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
        mPlayer.seek((int) progress);

        return PlayerOperate.SUCCESS;
    }

    @Override
    @PlayerOperate
    public int subscribe(boolean subscribe) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
//        if (playerInfo.getType() == PlayerSourceType.RADIO_XM) {
//            return PlayerOperate.UNSUPPORTED;
//        }
        SubscribeInfo subscribeInfo = getSubscribeDao().selectBy(playerInfo.getType(), playerInfo.getAlbumId());
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
        mPlayer.setPlayMode(mode);
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
        return mPlayer.getPlayList();
    }

    @Override
    public PlayerInfo getCurPlayerInfo() {
        return mPlayer.getCurPlayerInfo();
    }

    @Override
    public void setVoiceScale(float voiceScale) {
        mPlayer.setVoice(voiceScale, voiceScale);
    }

    @Override
    public void switchPlayerAlbum(XMPlayItem xmPlayItem) {
        mPlayer.pause();
        mPlayer.clearPlayerList();
        PlayerInfoImpl.newSingleton().reset();
    }

    @Override
    public void switchPlayerSource(int type) {
        switchPlayerAlbum(null);
        mPlayer.reset();
        mPlayer.disablePlayer();
        mPlayer.removePlayerStateListener();
        AudioFocusManager.getInstance().abandonKoalaFocus();
    }

    @Override
    public void exitPlayer() {
        mPlayer.stop();
        mPlayer.destroy();
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
                } else if (loadMoreStatus == PlayerLoadMore.LOAD_FAIL) {
                    mPlayer.pause();
                } else if (loadMoreStatus == PlayerLoadMore.LOAD_ERROR) {
                    mPlayer.pause();
                }
            }
        };
    }
}