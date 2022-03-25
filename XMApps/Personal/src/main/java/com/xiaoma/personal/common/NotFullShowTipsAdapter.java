package com.xiaoma.personal.common;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by kaka
 * on 19-2-28 上午10:44
 * <p>
 * desc: #a
 * </p>
 */
public abstract class NotFullShowTipsAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    public NotFullShowTipsAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public NotFullShowTipsAdapter(@Nullable List<T> data) {
        super(data);
    }

    public NotFullShowTipsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public void loadMoreEnd() {
        super.loadMoreEnd(!isFullScreen());
    }

    private boolean isFullScreen() {
        checkNotNull();
        LinearLayoutManager llm = (LinearLayoutManager) getRecyclerView().getLayoutManager();
        return (llm.findLastCompletelyVisibleItemPosition() + 1) != getItemCount() ||
                llm.findFirstCompletelyVisibleItemPosition() != 0;
    }

    private void checkNotNull() {
        if (getRecyclerView() == null) {
            throw new RuntimeException("please bind recyclerView first!");
        }
    }
}
