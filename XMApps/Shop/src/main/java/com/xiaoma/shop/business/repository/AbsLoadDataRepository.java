package com.xiaoma.shop.business.repository;

import android.util.Log;

import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.LoadMoreState;

/**
 * <des>
 * 加载数据的本地缓存管理类,需要继承这个进行本地数据缓存
 *
 * @author YangGang
 * @date 2019/3/6
 */
public abstract class AbsLoadDataRepository<T> {

    private static final String TAG = AbsLoadDataRepository.class.getSimpleName();

    public static final int INT_INVALID = -1;

    private int mCurPage;
    private int mMinPage = 0;
    private int[] mTotalInfo;
    private int[] mPageBounds;
    private ILoadDataListener mLoadDataListener;

    public AbsLoadDataRepository() {
        initRepository();
    }

    private void initRepository() {
        mCurPage = INT_INVALID;
        mPageBounds = new int[2];
        mTotalInfo = new int[2];
    }

    public void refreshRepositoryInfo() {
        mCurPage = INT_INVALID;
    }

    /**
     * 通过对page的比较,记录page的同时,刷新加载的状态
     * 这个方法必须在{@link #updateTotalInfo(int, int)} 之后调用
     *
     * @param page
     */
    public void updateCurPage(int page) {
        Log.d(TAG, "{updateCurPage}-[curPage / page] : " + mCurPage + " / " + page);
        if (page == INT_INVALID) {
            dispatchLoadMoreState(LoadMoreState.FAIL);
        } else {
            if (mCurPage == page) {
                return;
            }
            if (mCurPage == INT_INVALID) {
                mPageBounds[0] = page;
                mPageBounds[1] = page;
                dispatchLoadMoreState(page == mTotalInfo[1]
                        ? LoadMoreState.END
                        : LoadMoreState.COMPLETE);
            } else {
                //这里只需要做加载更多
                if (page > mPageBounds[1]) {
                    mPageBounds[1] = page;

                    dispatchLoadMoreState(page == mTotalInfo[1]
                            ? LoadMoreState.END
                            : LoadMoreState.COMPLETE);
                }
            }
            this.mCurPage = page;
        }
    }

    protected int getCurPage() {
        return mCurPage;
    }

    public void updateTotalInfo(int totalPage, int totalCount) {
        mTotalInfo[0] = totalPage;
        mTotalInfo[1] = totalCount;
    }

    public void updateMinPage(int page) {
        mMinPage = page;
    }

    public void setOnLoadStateListener(ILoadDataListener listener) {
        mLoadDataListener = listener;
    }

    public void dispatchLoadMoreState(@LoadMoreState int loadState) {
        Log.d("Jir", "dispatchLoadMoreState: " + loadState);
        if (mLoadDataListener != null) {
            mLoadDataListener.loadStateChanged(loadState);
        }
    }

    protected boolean isValidRequest() {
        if (mCurPage == INT_INVALID) {
            return true;
        } else {
            if ((mCurPage - mMinPage + 1) < mTotalInfo[0]) {
                return true;
            }
        }
        Log.d(TAG, "{isValidRequest}-[curpage / totalPage] : " + mCurPage + " / " + mTotalInfo[0]);
        dispatchLoadMoreState(LoadMoreState.END);
        return false;
    }

    public abstract boolean loadMore(ShopRequestProxy.IRequestCallback<T> callback);
}
