package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;
import com.xiaoma.xting.launcher.ToLauncherBeanConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class PageListHandler extends AbsActionHandler {

    public PageListHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        int searchPage = bundle.getInt(AudioConstants.BundleKey.CURRENT_PAGE);
        int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
        if (sourceType != PlayerSourceType.DEFAULT) {
            int[] pageBound = PlayerSourceFacade.newSingleton().getPlayerFetch().getPageBound();
            List<PlayerInfo> playList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
            int[] extremesInfo = PlayerSourceFacade.newSingleton().getPlayerFetch().getExtremesInfo();
            ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
            if (searchPage > pageBound[1]) {
                if (extremesInfo[1] < searchPage) {
                    share(AudioConstants.AudioResponseCode.SUCCESS, null);
                } else {
                    int maxDownloadedPage = playList.get(playList.size() - 1).getPage();
                    if (maxDownloadedPage >= searchPage) {
                        for (PlayerInfo playerInfo : playList) {
                            if (playerInfo.getPage() >= searchPage) {
                                playerInfoList.add(playerInfo);
                            }
                        }
                        share(AudioConstants.AudioResponseCode.SUCCESS, playerInfoList);
                    } else {
                        LoadMoreListenerImpl.newSingleton().addLoadMoreListener(new ILoadMoreListener() {
                            @Override
                            public void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list) {
                                LoadMoreListenerImpl.newSingleton().removeLoadMoreListener(this);
                                if (loadMoreStatus == PlayerLoadMore.LOAD_END
                                        || loadMoreStatus == PlayerLoadMore.LOAD_COMPLETE) {
                                    if (list == null) {
                                        share(AudioConstants.AudioResponseCode.SUCCESS, null);
                                    } else {
                                        share(AudioConstants.AudioResponseCode.SUCCESS, new ArrayList<>(list));
                                    }
                                } else if (loadMoreStatus == PlayerLoadMore.LOAD_FAIL) {
                                    share(AudioConstants.AudioResponseCode.FAIL, null);
                                } else if (loadMoreStatus == PlayerLoadMore.LOAD_ERROR) {
                                    share(AudioConstants.AudioResponseCode.ERROR, null);
                                }
                            }
                        });
                        PlayerSourceFacade.newSingleton().getPlayerFetch().loadMore(true);
                    }
                }
            } else {
                if (extremesInfo[0] < searchPage) {
                    share(AudioConstants.AudioResponseCode.SUCCESS, null);
                } else {
                    int minFetchPage = playList.get(0).getPage();
                    if (minFetchPage <= searchPage) {
                        for (PlayerInfo playerInfo : playList) {
                            if (playerInfo.getPage() <= searchPage) {
                                playerInfoList.add(playerInfo);
                            }
                        }
                        share(AudioConstants.AudioResponseCode.SUCCESS, playerInfoList);
                    } else {
                        LoadMoreListenerImpl.newSingleton().addLoadMoreListener(new ILoadMoreListener() {
                            @Override
                            public void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list) {
                                LoadMoreListenerImpl.newSingleton().removeLoadMoreListener(this);
                                if (!isDownload) {
                                    if (loadMoreStatus == PlayerLoadMore.LOAD_END
                                            || loadMoreStatus == PlayerLoadMore.LOAD_COMPLETE) {
                                        if (list == null) {
                                            share(AudioConstants.AudioResponseCode.SUCCESS, null);
                                        } else {
                                            share(AudioConstants.AudioResponseCode.SUCCESS, new ArrayList<>(list));
                                        }
                                    } else if (loadMoreStatus == PlayerLoadMore.LOAD_FAIL) {
                                        share(AudioConstants.AudioResponseCode.FAIL, null);
                                    } else if (loadMoreStatus == PlayerLoadMore.LOAD_ERROR) {
                                        share(AudioConstants.AudioResponseCode.ERROR, null);
                                    }
                                }
                            }
                        });

                        PlayerSourceFacade.newSingleton().getPlayerFetch().loadMore(false);
                    }
                }
            }
        }
    }

    private void share(int code, ArrayList<PlayerInfo> playerInfoList) {
        Bundle callbackData = new Bundle();
        callbackData.putString(
                AudioConstants.BundleKey.ACTION,
                AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putInt(
                AudioConstants.BundleKey.AUDIO_RESPONSE_CODE,
                code);
        callbackData.putInt(
                AudioConstants.BundleKey.AUDIO_PLAYING_INDEX,
                PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex());
        if (playerInfoList != null && !playerInfoList.isEmpty()) {
            callbackData.putParcelableArrayList(
                    AudioConstants.BundleKey.AUDIO_LIST,
                    ToLauncherBeanConvert.toAudioInfo(playerInfoList));
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
