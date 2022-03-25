package com.xiaoma.xting.common.playerSource.loadmore;

import android.util.Log;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public abstract class IPlayerFetch {
    private static final String TAG = IPlayerFetch.class.getSimpleName();

    protected static final String SEQ_ASC = "asc";

    protected PlayerInfo mPlayerInfo;
    private IFetchListener mFetchListener;
    private int[] mPageBound = new int[2];
    private int[] mExtremesInfo = new int[3];
    private int[] mFetchState = new int[3]; //用于避免重复加载更多导致的数据紊乱问题
    private int mCurPage;

    /**
     * @param minPage 最小的page
     * @param maxPage 最大的page
     */
    public void setPageExtremes(int minPage, int maxPage, int totalCount) {
        if (mExtremesInfo[1] >= 0) {
            return;
        }
        mExtremesInfo[0] = minPage;
        mExtremesInfo[1] = maxPage;
        mExtremesInfo[2] = totalCount;
    }

    public int[] getPageInfo() {
        return new int[]{mCurPage, mExtremesInfo[1], mExtremesInfo[2]};
    }

    public int getTotalCount() {
        return mExtremesInfo[2];
    }

    public int[] getPageBound() {
        return mPageBound;
    }

    public void setPageBound(int page) {
        mCurPage = page;
        if (mPageBound[0] < 0) {
            mPageBound[0] = page;
            mPageBound[1] = page;
        } else {
            if (page > mPageBound[1]) {
                mPageBound[1] = page;
            } else if (page < mPageBound[0]) {
                mPageBound[0] = page;
            }
        }
    }

    public boolean isPageInside(int page) {
        return mCurPage > 0
                && mPageBound[0] >= page
                && mPageBound[1] <= page;
    }

    public int[] getExtremesInfo() {
        return mExtremesInfo;
    }

    public void setTotalCount(int count) {
        mExtremesInfo[2]=count;
    }

    private void initInfo() {
        mCurPage = -1;
        mExtremesInfo[0] = -1;
        mExtremesInfo[1] = -1;
        mPageBound[0] = -1;
        mPageBound[1] = -1;
        initLoadState();
    }

    private void initLoadState() {
        mFetchState[0] = 0;
        mFetchState[1] = 0;
        mFetchState[2] = 0;
    }

    public boolean isDownloadBottom() {
        return mExtremesInfo[1] <= mPageBound[1];
    }

    public boolean isUpFetchTop() {
        return mExtremesInfo[0] >= mPageBound[0];
    }

    public boolean fetch(PlayerInfo info, IFetchListener listener) {
        if (isThisAlbumFetching(info)) {
            this.mFetchListener = listener;
            return false;
        } else {
            dispatchLoading(listener);
            initInfo();
            mFetchState[0] = 1;
            this.mPlayerInfo = info;
            fetch(listener);

            return true;
        }
    }

    public boolean isThisAlbumFetching(PlayerInfo info) {
        return mFetchState[0] == 1 && info.equals(mPlayerInfo);
    }

    public void loadMore(boolean download) {
        if (mPlayerInfo.getType() == PlayerSourceType.KOALA && mPlayerInfo.getSourceSubType() == PlayerSourceSubType.KOALA_PGC_RADIO) {
            if (download) {
                download(0);
            }else{
                dispatchLoadMore(false, PlayerLoadMore.LOAD_END, null);
            }
            return;
        }
        if (download) {
            if (isDownloadBottom()) {
                dispatchLoadMore(true, PlayerLoadMore.LOAD_END, null);
            } else {
                Log.d(TAG, "{loadMore}-[download] : " + mFetchState[0]);
                if (mFetchState[1] == 0) {
                    mFetchState[1] = 1;
                    download(mPageBound[1] + 1);
                }
            }
        } else {
            if (isUpFetchTop()) {
                dispatchLoadMore(false, PlayerLoadMore.LOAD_END, null);
            } else {
                Log.d(TAG, "{loadMore}-[upFetch] : " + mFetchState[1]);
                if (mFetchState[2] == 0) {
                    mFetchState[2] = 1;
                    upFetch(mPageBound[0] - 1);
                }
            }
        }
    }

    protected void download(int page) {
    }

    protected void upFetch(int page) {

    }

    protected abstract void fetch(IFetchListener listener);

    private void dispatchLoading(IFetchListener listener) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
//                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                if (listener != null) {
                    listener.onLoading();
                }
            }
        });

    }

    protected void dispatchSuccess(Object obj, IFetchListener listener) {
        mFetchState[0] = 0;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSuccess(obj);
                }

                if (mFetchListener != null) {
                    mFetchListener.onSuccess(obj);
                }
            }
        });

    }

    protected void dispatchFail(IFetchListener listener) {
        mFetchState[0] = 0;
        BeanConverter.setAlbumUrl(null);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR_BY_DATA_SOURCE);
                if (listener != null) {
                    listener.onFail();
                }
                if (mFetchListener != null) {
                    mFetchListener.onFail();
                }
            }
        });

    }

    protected void dispatchError(int code, String msg, IFetchListener listener) {
        mFetchState[0] = 0;
        BeanConverter.setAlbumUrl(null);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                if (listener != null) {
                    listener.onError(code, msg);
                }

                if (mFetchListener != null) {
                    mFetchListener.onError(code, msg);
                }
            }
        });

    }

    protected void dispatchLoadMore(boolean loadMore, int loadMoreStatus, List<PlayerInfo> playerInfoList) {
        if (loadMore) {
            mFetchState[1] = 0;
        } else {
            mFetchState[2] = 0;
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                LoadMoreListenerImpl.newSingleton().notifyLoadMoreResult(loadMore, loadMoreStatus, playerInfoList);
            }
        });
    }

}
