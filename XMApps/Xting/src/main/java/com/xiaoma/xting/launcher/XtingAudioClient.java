package com.xiaoma.xting.launcher;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.handler.ActionParser;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.IPlayerInfo;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;
import com.xiaoma.xting.sdk.LocalFMFactory;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/12
 */
public class XtingAudioClient extends AbsAudioClient implements IPlayerInfo, ILoadMoreListener {

    private static final String TAG = XtingAudioClient.class.getSimpleName();

    private static XtingAudioClient sClient;
    private int mCurCategoryId = -2;

    private XtingAudioClient(Context context) {
        super(context, AudioConstants.AudioTypes.XTING);
    }

    public static XtingAudioClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (XtingAudioClient.class) {
                if (sClient == null) {
                    sClient = new XtingAudioClient(context.getApplicationContext());
                }
            }
        }
        return sClient;
    }

    private int mAudioType;

    public void setLauncherCategoryId(int categoryId) {
        Log.d("LPL", "{setLauncherCategoryId}-[LPL ~ categoryId] : " + categoryId);
        mCurCategoryId = categoryId;
    }

    public int getCurCategoryId() {
        return mCurCategoryId;
    }

    @Override
    protected void onFavorite() {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        int sourceType = playerInfo.getSourceType();
        boolean subscribed;
        if (sourceType == PlayerSourceType.RADIO_YQ) {
            subscribed = (XtingUtils.getSubscribeDao().selectBy(playerInfo.getType(), playerInfo.getProgramId()) != null);
        } else {
            if (sourceType == PlayerSourceType.RADIO_XM) {
                sourceType = PlayerSourceType.HIMALAYAN;
            }
            subscribed = (XtingUtils.getSubscribeDao().selectBy(sourceType, playerInfo.getAlbumId()) != null);
        }

        PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(!subscribed);
        PlayerInfoImpl.newSingleton().refreshSubscribe();
    }

    @Override
    protected void onPrevious(ClientCallback callback) {
        handlePlay(-1, callback);
//        int operateResult = PlayerSourceFacade.newSingleton().getPlayerControl().playPre();
//        if (operateResult != PlayerOperate.FAIL) {
//            shareAudioControlCode(AudioConstants.AudioControlCode.MIDDLE, callback);
//        } else {
//            shareAudioControlCode(AudioConstants.AudioControlCode.TOP, callback);
//        }
    }

    @Override
    protected void onNext(ClientCallback callback) {
        handlePlay(1, callback);
    }

    public void restoreLauncherCategoryId() {
        setLauncherCategoryId(-2);
    }

    @Override
    protected void onPause() {
        if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.RADIO_YQ) {
            PlayerSourceFacade.newSingleton().getPlayerControl().exitPlayer();
        } else {
            PlayerSourceFacade.newSingleton().getPlayerControl().pause();
        }
    }

    @Override
    protected void onPlay(Bundle data, ClientCallback callback) {
        AbsActionHandler handler = ActionParser.parseLauncherPlayAction(data, callback);
        if (handler != null) {
            handler.handleRequestAction(data);
        }
    }

    @Override
    protected void onConnect() {
        //连接成功之后注册同步,避免出现数据更新失败的情况
        Log.d(TAG, "{onConnect}-[] : ");
        PlayerInfoImpl.newSingleton().registerPlayerInfoListener(this);
        LoadMoreListenerImpl.newSingleton().addLoadMoreListener(this);
    }

    @Override
    protected void onSearchRequest(Bundle bundle, ClientCallback callback) {
        AbsActionHandler handler = ActionParser.parseLauncherSearchAction(bundle, callback);
        if (handler != null) {
            handler.handleRequestAction(bundle);
        }
    }

    @Override
    public void onPlayerInfoChanged(PlayerInfo playerInfo) {
        Log.d(TAG, "{onPlayerInfoChanged}-[playerInfo] : " + playerInfo + " / " + mCurCategoryId);
        if (playerInfo != null) {
            AudioInfo audioInfo = new AudioInfo();
            audioInfo.setCover(playerInfo.getCoverUrl());
            audioInfo.setTitle(playerInfo.getAlbumName());
            audioInfo.setSubTitle(playerInfo.getProgramName());
            audioInfo.setUniqueId(playerInfo.getProgramId());
            audioInfo.setAlbumId(playerInfo.getAlbumId());
            audioInfo.setPlayCount((int) playerInfo.getPlayCount());
            audioInfo.setLauncherCategoryId(mCurCategoryId);
            audioInfo.setHistory(playerInfo.isFromRecordF());

            int sourceType = playerInfo.getSourceType();
            if (sourceType == PlayerSourceType.HIMALAYAN) {
                int sourceSubType = playerInfo.getSourceSubType();
                if (sourceSubType == PlayerSourceSubType.TRACK) {
                    audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
                } else {
                    audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_NET_RADIO);
                }
                shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.XMLY);
            } else if (sourceType == PlayerSourceType.KOALA) {
                audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_KOALA_ALBUM);
                if (mCurCategoryId > 0) {
                    audioInfo.setLauncherCategoryId(mCurCategoryId);
                    shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
                } else {
                    audioInfo.setLauncherCategoryId(-2);
                    shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.XMLY);
                }
            } else if (sourceType == PlayerSourceType.RADIO_YQ) {
                audioInfo.setLauncherCategoryId(mCurCategoryId);
                audioInfo.setTitle(playerInfo.getProgramName());
                audioInfo.setSubTitle(playerInfo.getAlbumName());
                if (playerInfo.getSourceSubType() == PlayerSourceSubType.YQ_RADIO_FM) {
                    audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_LOCAL_FM);
                    shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
                } else {
                    audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_LOCAL_AM);
                    shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.XMLY);
                }
            } else if (sourceType == PlayerSourceType.RADIO_XM) {
                audioInfo.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
                shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
            }
            mAudioType = audioInfo.getAudioType();
            shareAudioTypeToLauncher(mAudioType);

            shareAudioInfo(audioInfo);
        }
    }

    @Override
    public void onPlayerStatusChanged(int status) {
        // TODO: 2019/8/10 0010 添加  AUDIO TYPE
        Log.d(TAG, "{onPlayerStatusChanged}-[status] : " + status);
        if (status == PlayerStatus.LOADING) {
            shareAudioState(AudioConstants.AudioStatus.LOADING, AudioConstants.AudioTypes.XTING);
        } else if (status == PlayerStatus.PAUSE) {
            shareAudioState(AudioConstants.AudioStatus.PAUSING, AudioConstants.AudioTypes.XTING);
        } else if (status == PlayerStatus.PLAYING) {
            shareAudioState(AudioConstants.AudioStatus.PLAYING, AudioConstants.AudioTypes.XTING);
        } else if (status == PlayerStatus.ERROR
                || status == PlayerStatus.ERROR_BY_DATA_SOURCE
                || status == PlayerStatus.ERROR_BY_PLAYER) {
            shareAudioState(AudioConstants.AudioStatus.ERROR, AudioConstants.AudioTypes.XTING);
        }
    }

    @Override
    public void onPlayerProgress(long progress, long duration) {
        Log.d(TAG, "{onPlayerProgress}-[] : ");
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setAudioType(mAudioType);
        progressInfo.setTotal(duration);
        progressInfo.setCurrent(progress);
        progressInfo.setPercent(progress * 1f / duration);
        shareAudioProgress(progressInfo);
    }

    @Override
    public void onProgramSubscribeChanged(boolean subscribe) {
        Log.d(TAG, "{onProgramSubscribeChanged}-[] : ");
        shareAudioFavorite(subscribe);
    }

    public boolean handlePlay(final int playOffset, ClientCallback callback) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo == null) {
            return false;
        }
        if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
            if (playOffset == 0) {
                PlayerSourceFacade.newSingleton().getPlayerControl().play();
            } else if (playOffset == -1) {
                if (PlayerSourceFacade.newSingleton().getPlayerControl().playPre() == PlayerOperate.FAIL) {
                    shareAudioControlCode(AudioConstants.AudioControlCode.TOP, callback);
                } else {
                    shareAudioControlCode(AudioConstants.AudioControlCode.MIDDLE, callback);
                }
            } else {
                if (PlayerSourceFacade.newSingleton().getPlayerControl().playNext() == PlayerOperate.FAIL) {
                    shareAudioControlCode(AudioConstants.AudioControlCode.BOTTOM, callback);
                } else {
                    shareAudioControlCode(AudioConstants.AudioControlCode.MIDDLE, callback);
                }
            }

        } else {

            playerInfo.setType(PlayerAction.SET_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                @Override
                public void onLoading() {
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                }

                @Override
                public void onSuccess(Object t) {
                    int playingIndex = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex();
                    if (playOffset == 0) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().play();
                    } else if (playOffset == -1) {
                        if (playingIndex == 0) {
                            shareAudioControlCode(AudioConstants.AudioControlCode.TOP, callback);
                        } else {
                            PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex - 1);
                            shareAudioControlCode(AudioConstants.AudioControlCode.MIDDLE, callback);
                        }
                    } else {
                        int playListSize = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayListSize();
                        if (playingIndex + 1 == playListSize) {
                            shareAudioControlCode(AudioConstants.AudioControlCode.BOTTOM, callback);
                        } else {
                            PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex + 1);
                            shareAudioControlCode(AudioConstants.AudioControlCode.MIDDLE, callback);
                        }
                    }
                }

                @Override
                public void onFail() {
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                }

                @Override
                public void onError(int code, String msg) {
                    XMToast.showToast(context, R.string.net_work_error);
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                }
            });
        }
        return true;
    }

    @Override
    protected void onOtherRequest(int action, Bundle data, ClientCallback callback) {
        if (action == AudioConstants.Action.CANCEL_SCAN) {
            LocalFMFactory.getSDK().cancel();
        }
    }

    @Override
    public void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list) {
        if (loadMoreStatus == PlayerLoadMore.LOAD_COMPLETE
                || loadMoreStatus == PlayerLoadMore.LOAD_END) {
//            int[] pageInfo = PlayerSourceFacade.newSingleton().getPlayerFetch().getPageInfo();
//            shareAudioList(AudioConstants.AudioResponseCode.SUCCESS,   ToLauncherBeanConvert.toAudioInfo(list),pageInfo,
//                    PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex(),);
        }
    }
}
