package com.xiaoma.music.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.ShowHideEvent;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.online.adapter.RecommendAdapter;
import com.xiaoma.music.online.model.RecommendModel;
import com.xiaoma.music.online.vm.RecommendVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

/**
 * @author zs
 * @date 2018/10/9 0009.
 */
@PageDescComponent(EventConstants.PageDescribe.recommendFragment)
public class RecommendFragment extends LazyLoadFragment implements KwPlayInfoManager.AlbumTypeChangedListener {
    private RecyclerView mRecyclerView;
    private XmScrollBar scrollBar;
    private RecommendAdapter<RecommendModel> mAdapter;
    private RecommendVM mRecommendVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        KwPlayInfoManager.getInstance().addAlbumTypeChangedListener(this);
    }

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void initView(View view) {
        bindView(view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        mRecyclerView.addItemDecoration(dividerItem);
        mAdapter = new RecommendAdapter<>(this, null);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext,getString(R.string.net_error));
                    return;
                }
                showProgressDialog(R.string.loading_data);
                if (mRecommendVM != null) {
                    mRecommendVM.play(position);
                }
            }
        });
    }

    private void bindView(View view) {
        mRecyclerView = view.findViewById(R.id.fragment_recommend_rv);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    @Override
    protected void loadData() {
        super.loadData();
        mRecommendVM = ViewModelProviders.of(this).get(RecommendVM.class);
        mRecommendVM.getRecommends().observe(this, new Observer<XmResource<List<RecommendModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<RecommendModel>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<RecommendModel>>() {
                        @Override
                        public void onSuccess(List<RecommendModel> data) {
                            if (data != null) {
                                mAdapter.setNewData(data);
                                switchEmptyView(data);
                            }
                        }
                    });
                }
            }
        });
        mRecommendVM.getPlaySuccess().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                if (position != null && mAdapter != null) {
//                    mAdapter.notifyDataSetChanged();
                    dismissProgress();
                }
            }
        });
        fetchData();
    }

    private void fetchData() {
        mRecommendVM.fetchRecommend();
    }

    private void switchEmptyView(List<RecommendModel> data) {
        if (data.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
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
    protected void cancelData() {
        KLog.d("cancelData");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Subscriber(tag = EventBusTags.SHOW_OR_HIDE_MINE)
    public void onHideEvent(ShowHideEvent event) {
        if (event.isShowOnline()) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        KwPlayInfoManager.getInstance().removeAlbumTypeChangedListener(this);
    }

    @Override
    public void onChanged(String currentAlbumId, int currentType) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
