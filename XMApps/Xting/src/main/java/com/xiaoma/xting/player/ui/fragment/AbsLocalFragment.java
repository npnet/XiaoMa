package com.xiaoma.xting.player.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.xting.R;
import com.xiaoma.xting.player.adapter.LocalFunctionAdapter;
import com.xiaoma.xting.player.adapter.SimpleLinearDivider;
import com.xiaoma.xting.player.vm.LocalPlayUpVM;

/**
 * <des>
 *
 * @author wutao
 * @date 2018/12/5
 */
public abstract class AbsLocalFragment extends BaseFragment {

    protected RecyclerView mContentRV;
    protected LocalFunctionAdapter mAdapter;
    protected LocalPlayUpVM mVM;
    private XmScrollBar scrollbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_playlist_content, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initData();
    }

    private void bindView(View view) {
        mContentRV = view.findViewById(R.id.rvContent);
        scrollbar = view.findViewById(R.id.viewScrolbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mContentRV.setLayoutManager(linearLayoutManager);
        int headMarginLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.size_similar_albums_head_margin_left);
        int middleMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.size_similar_albums_middle_maring_left);
        SimpleLinearDivider similarDivider = new SimpleLinearDivider(mContext, DividerItemDecoration.HORIZONTAL);
        similarDivider.setDividerSpace(headMarginLeft, middleMargin, 0);
        mContentRV.addItemDecoration(similarDivider);
    }

    private void initData() {
        mAdapter = new LocalFunctionAdapter(this);
        mAdapter.bindToRecyclerView(mContentRV);
        scrollbar.setRecyclerView(mContentRV);
        mVM = ViewModelProviders.of(this).get(LocalPlayUpVM.class);

        mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mContentRV.getParent());
        mAdapter.getEmptyView().setPadding(0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen.size_similar_albums_head_margin_left), 0);
        getData();
    }

    protected abstract void getData();
}
