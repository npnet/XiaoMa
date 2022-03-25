package com.xiaoma.launcher.travel.hotel.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.xiaoma.launcher.R;

/**
 * Created by Thomas on 2019/2/13 0013
 * loadmore view
 */

public class HotelLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.view_load_more_hotel;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.rl_Loading;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.tv_Failed;
    }

    /**
     * isLoadEndGone()为true，可以返回0
     * isLoadEndGone()为false，不能返回0
     */
    @Override
    protected int getLoadEndViewId() {
        return R.id.tv_End;
    }

}
