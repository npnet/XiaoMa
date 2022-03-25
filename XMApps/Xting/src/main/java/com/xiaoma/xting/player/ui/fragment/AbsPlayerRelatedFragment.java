package com.xiaoma.xting.player.ui.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.player.adapter.OnlineFuctionAdapter;
import com.xiaoma.xting.player.adapter.SimpleLinearDivider;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/7
 */
public abstract class AbsPlayerRelatedFragment extends LazyLoadFragment {

    protected RecyclerView mContentRV;
    protected OnlineFuctionAdapter mAdapter;
    private XmScrollBar mScrollBar;
    private FrameLayout.LayoutParams mLayoutParams;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_online_function;
    }

    @Override
    protected void initView(View view) {
        preBindView();
        bindView(view);
        afterBindView();
    }

    private void bindView(View view) {
        mContentRV = view.findViewById(R.id.rvContent);
        mScrollBar = view.findViewById(R.id.viewScrolbar);
    }

    private void preBindView() {
        mAdapter = new OnlineFuctionAdapter();
        dealObserver();
    }

    private void afterBindView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mContentRV.setLayoutManager(linearLayoutManager);
        mAdapter.bindToRecyclerView(mContentRV);
        mScrollBar.setRecyclerView(mContentRV);
        int headMarginLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.size_similar_albums_head_margin_left);
        int middleMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.size_similar_albums_middle_maring_left);
        SimpleLinearDivider similarDivider = new SimpleLinearDivider(mContext, DividerItemDecoration.HORIZONTAL);
        similarDivider.setDividerSpace(headMarginLeft, middleMargin, 0);
        mContentRV.addItemDecoration(similarDivider);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dispatchItemClick(position);
            }
        });
    }

    @Override
    protected void cancelData() {

    }

    @Override
    protected void loadData() {
        super.loadData();
        checkNet();
    }

    @Override
    protected void noNetworkOnRetry() {
        checkNet();
    }

    private void checkNet() {
        if (!NetworkUtils.isConnected(getContext())) {
            showNoNetView();
        } else {
            fetchRelatedInfo();
        }
    }

    protected void showLoading() {
        showProgressDialog(R.string.base_loading);
    }

    protected void dismissLoading() {
        dismissProgress();
    }

    protected void showEmpty() {
        showEmptyView();
    }

    protected abstract void dealObserver();

    protected abstract void fetchRelatedInfo();

    protected abstract void dispatchItemClick(int position);

}
