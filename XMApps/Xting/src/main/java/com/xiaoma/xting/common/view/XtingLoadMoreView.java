package com.xiaoma.xting.common.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.xiaoma.xting.R;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/21
 */
public class XtingLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.view_load_more_xting;
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
