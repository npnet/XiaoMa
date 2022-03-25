package com.xiaoma.xting.common.playerSource.loadmore.impl;

import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/14
 */
public class LoadMoreListenerImpl implements ILoadMoreListener {

    private CopyOnWriteArrayList<ILoadMoreListener> mLoadMoreListenerList;

    private LoadMoreListenerImpl() {
        mLoadMoreListenerList = new CopyOnWriteArrayList<>();
    }

    public static LoadMoreListenerImpl newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    public void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list) {
        if (mLoadMoreListenerList != null && !mLoadMoreListenerList.isEmpty()) {
            for (ILoadMoreListener loadMoreListener : mLoadMoreListenerList) {
                if (loadMoreListener != null) {
                    loadMoreListener.notifyLoadMoreResult(isDownload, loadMoreStatus, list);
                }
            }
        }
    }

    public void addLoadMoreListener(ILoadMoreListener listener) {
        if (!mLoadMoreListenerList.contains(listener)) {
            mLoadMoreListenerList.add(listener);
        }
    }

    public void removeLoadMoreListener(ILoadMoreListener listener) {
        if (mLoadMoreListenerList != null) {
            mLoadMoreListenerList.remove(listener);
        }
    }

    public void clearLoadMoreListener() {
        mLoadMoreListenerList.clear();
    }

    interface Holder {
        LoadMoreListenerImpl sINSTANCE = new LoadMoreListenerImpl();
    }

}
