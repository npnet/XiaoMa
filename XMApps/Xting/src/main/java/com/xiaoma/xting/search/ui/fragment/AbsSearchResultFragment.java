package com.xiaoma.xting.search.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.NetworkReceiver;
import com.xiaoma.xting.search.ui.SearchResultActivity;
import com.xiaoma.xting.search.vm.SearchResultVM;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/17
 *     desc   :
 * </pre>
 */
public abstract class AbsSearchResultFragment extends LazyLoadFragment {
    protected SearchResultVM mSearchResultVM;
    protected BaseQuickAdapter mAdapter;
    protected String mKeyword = "";
    protected RecyclerView mRecyclerView;
    private NetworkReceiver.NetWorkCallback mNetWorkCallback;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected void initView(View view) {
        handleIntent();
        mSearchResultVM = ViewModelProviders.of(this).get(SearchResultVM.class);
        subscriberList();
        initRecycleView(view);
        mNetWorkCallback = new NetworkReceiver.NetWorkCallback() {
            @Override
            public void onNetworkDisconnect() {

            }

            @Override
            public void onNetWorkConnect() {
                checkNet();
            }
        };
        NetworkReceiver.addNetWorkListener(mNetWorkCallback);
    }

    private void initRecycleView(View view) {
        mRecyclerView = view.findViewById(R.id.recycleview);
        XmScrollBar scrollBar = view.findViewById(R.id.scroll_bar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        DividerItemDecoration decor = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = mActivity.getResources().getDimensionPixelOffset(R.dimen.size_search_result_recycleview);
                outRect.set(0, 0, offset, 0);
            }
        };
        // 通过设置一个空的drawable来隐藏默认分割线
        decor.setDrawable(new ColorDrawable());
        mRecyclerView.addItemDecoration(decor);
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
    }

    protected abstract BaseQuickAdapter getAdapter();

    @Override
    protected void loadData() {
        super.loadData();
        refreshData();
    }

    private void checkNet() {
        if (NetworkUtils.isConnected(getContext())) {
            showContentView();
            refreshData();
        } else {
//            View noNetworkView = mStateView.getNoNetworkView();
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) noNetworkView.getLayoutParams();
//            layoutParams.topMargin = mActivity.getResources().getDimensionPixelOffset(R.dimen.size_no_net_bottom);
//            layoutParams.gravity = Gravity.CENTER;
            showNoNetView();
        }
    }

    @Override
    protected void cancelData() {
        NetworkReceiver.removeNetWorkListener(mNetWorkCallback);
    }

    protected void refreshData() {
        requestNetData(mKeyword);
        execute();
    }

    protected boolean isManualScroll() {
        FragmentActivity activity = getActivity();
        return activity != null && ((SearchResultActivity) getActivity()).isManualScroll();
    }

    protected void setManualScroll() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((SearchResultActivity) getActivity()).setManualScroll(true);
        }
    }

    protected void notifyEmptyView(int index) {
        dismissProgress();
        mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
        ((SearchResultActivity) mActivity).setCurrentItem(index);
    }

    protected void execute() {
    }

    @Override
    protected void noNetworkOnRetry() {
        checkNet();
    }

    protected abstract void subscriberList();

    protected abstract void requestNetData(String keyword);

    private void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mKeyword = bundle.getString(SearchResultActivity.INTENT_KEY_WORD);
        }
    }
}
