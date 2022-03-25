package com.xiaoma.shop.business.ui.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.xiaoma.shop.R;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/29
 */
public class DownloadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.view_load_more_shop;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.groupLoading;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.tvFailed;
    }

    /**
     * isLoadEndGone()为true，可以返回0
     * isLoadEndGone()为false，不能返回0
     */
    @Override
    protected int getLoadEndViewId() {
        return R.id.tvEnd;
    }
}
