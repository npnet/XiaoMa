package com.xiaoma.music.search.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.search.adapter.SongListAdapter;
import com.xiaoma.music.search.vm.SearchResultVM;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/10
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.searchResultSongListFragment)
public class SearchSongListFragment extends LazyLoadFragment implements SongListAdapter.OnProgressStateListener {

    private RecyclerView mRecyclerView;
    private SearchResultVM mSearchResultVM;
    private SongListAdapter mAdapter;
    private XmScrollBar scrollBar;
    private String keyWord;
    private boolean isFirstLoad = true;

    public static SearchSongListFragment newInstance(String from) {
        SearchSongListFragment fragment = new SearchSongListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MusicConstants.MUSIC_SEARCH_KEY, from);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initVM();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_search_result;
    }

    private void initVM() {
        if (mSearchResultVM == null) {
            mSearchResultVM = ViewModelProviders.of(this).get(SearchResultVM.class);
        }
        mSearchResultVM.getSongLists().observe(this, new Observer<XmResource<List<XMSongListInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<XMSongListInfo>> singerBeans) {
                dismissProgress();
                if (singerBeans != null) {
                    singerBeans.handle(new OnCallback<List<XMSongListInfo>>() {
                        @Override
                        public void onSuccess(List<XMSongListInfo> data) {
                            mAdapter.setData(data);
                            mAdapter.notifyDataSetChanged();
                            checkEmptyView(data);
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (Pattern.matches(getString(R.string.base_data_empty), message)
                                    && NetworkUtils.isConnected(mContext)) {
                                //空数据
                                showEmptyView();
                                setCurrentItem();
                            } else {
                                showNoNetView();
                                dismissLoading();
                            }
                        }
                    });
                }
            }
        });

        mAdapter = new SongListAdapter(this, new ArrayList<XMSongListInfo>());
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setOnProgressStateListener(this);
    }

    @Override
    protected void showErrorView() {
        if (mStateView != null && !isDestroy()) {
            mStateView.setErrorViewResId(R.layout.view_search_empty);
            mStateView.showError();
        }
    }

    private void checkEmptyView(List<XMSongListInfo> data) {
        if (data.isEmpty()) {
            scrollBar.setVisibility(View.GONE);
            showEmptyView();
            setCurrentItem();
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
            dismissLoading();
        }
    }

    private void dismissLoading() {
        try {
            if (getActivity() instanceof SearchResultActivity) {
                ((SearchResultActivity) Objects.requireNonNull(getActivity())).dismissLoading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrentItem() {
        if (getActivity() instanceof SearchResultActivity) {
            final SearchResultActivity activity = (SearchResultActivity) getActivity();
            if (activity != null && isFirstLoad) {
                (activity).setCurrentItem(0);
                isFirstLoad = false;
            }
        }
    }

    private void initEvent() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        DividerItemDecoration decor = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = getContext().getResources().getDimensionPixelOffset(R.dimen.size_search_result_item_space);
                outRect.set(0, 0, offset, 0);
            }
        };
        // 通过设置一个空的drawable来隐藏默认分割线
        decor.setDrawable(new ColorDrawable());
        mRecyclerView.addItemDecoration(decor);

    }

    private void fetchData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        if (!TextUtils.isEmpty(keyWord)) {
            mSearchResultVM.getSongListData(keyWord, isFirstLoad);
        }
    }

    @Override
    protected void errorOnRetry() {
        super.errorOnRetry();
        fetchData();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        fetchData();
    }


    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycleview);
        scrollBar = view.findViewById(R.id.search_scroll_bar);
    }

    @Override
    protected void loadData() {
        super.loadData();
        initData();
    }

    private void initData() {
        if (mSearchResultVM == null && getActivity() != null) {
            mSearchResultVM = ViewModelProviders.of(getActivity()).get(SearchResultVM.class);
        }
        keyWord = getArguments().getString(MusicConstants.MUSIC_SEARCH_KEY);
        fetchData();
    }

    @Override
    protected void cancelData() {

    }

    @Override
    public void onProgressState(boolean isShow) {
        if (isShow) {
            showProgressDialog(com.xiaoma.component.R.string.base_loading);
        } else {
            dismissProgress();
        }
    }
}
