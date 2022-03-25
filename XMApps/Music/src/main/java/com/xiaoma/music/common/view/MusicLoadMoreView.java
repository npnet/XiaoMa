package com.xiaoma.music.common.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.xiaoma.music.R;

/**
 * @author zs
 * @date 2018/12/29 0029.
 */
public class MusicLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.view_load_more_music;
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
