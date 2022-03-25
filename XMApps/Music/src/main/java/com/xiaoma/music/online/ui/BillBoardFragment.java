package com.xiaoma.music.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.xiaoma.music.online.adapter.BillboardAdapter;
import com.xiaoma.music.online.model.BillboardBean;
import com.xiaoma.music.online.vm.BillboardVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

/**
 * @author zs
 * @date 2018/10/16 0016.
 */
@PageDescComponent(EventConstants.PageDescribe.billboardFragment)
public class BillBoardFragment extends LazyLoadFragment implements KwPlayInfoManager.AlbumTypeChangedListener {

    private RecyclerView mRecyclerView;
    private BillboardAdapter mAdapter;
    private BillboardVM mBillboardVM;
    private XmScrollBar scrollBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        KwPlayInfoManager.getInstance().addAlbumTypeChangedListener(this);
    }

    public static BillBoardFragment newInstance() {
        return new BillBoardFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_billboard;
    }

    @Override
    protected void initView(final View view) {
        bindView(view);
    }

    private void bindView(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.fragment_billboard_rv);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    @Override
    protected void loadData() {
        super.loadData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0);
        dividerItem.setRect(0, 0, offset, 0, first);
        mRecyclerView.addItemDecoration(dividerItem);
        mAdapter = new BillboardAdapter(this, null);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(mContext)) {
                    showToastException(mContext.getString(R.string.net_error));
                    return;
                }
                mBillboardVM.playBillboard(position);
//                mContext.sendBroadcast(new Intent(AudioShareManager.PLAY_MUSIC_ACTION));
            }
        });
        mBillboardVM = ViewModelProviders.of(this).get(BillboardVM.class);
        mBillboardVM.getBillboards().observe(this, new Observer<XmResource<List<BillboardBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<BillboardBean>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<BillboardBean>>() {
                        @Override
                        public void onSuccess(List<BillboardBean> data) {
                            if (data != null) {
                                mAdapter.setNewData(data);
                                checkEmptyView(data);
                            }
                        }
                    });
                }
            }
        });
        mBillboardVM.getPlayPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null && mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        fetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void fetchData() {
        mBillboardVM.fetchData();
    }

    private void checkEmptyView(List<BillboardBean> data) {
        if (data.isEmpty()) {
            scrollBar.setVisibility(View.GONE);
            showEmptyView();
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
