package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.player.adapter.SimilarAdapter;
import com.xiaoma.music.player.vm.SimilarVM;
import com.xiaoma.ui.OnRvItemClickListener;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
@PageDescComponent(EventConstants.PageDescribe.similarFragment)
public class SimilarFragment extends LazyLoadFragment {

    private RecyclerView mRecyclerView;
    private SimilarVM mSimilarVM;
    private SimilarAdapter mAdapter;
    private XmScrollBar scrollBar;

    public static SimilarFragment newInstance() {
        return new SimilarFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_similar;
    }

    @Override
    protected void initView(View view) {
        bindView(view);
        initView();
    }

    @Override
    protected void cancelData() {

    }

    @Override
    protected void loadData() {
        super.loadData();
        initData();
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

    private void bindView(View view) {
        mRecyclerView = view.findViewById(R.id.fragment_similar_rv);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_fragment_album_switch_left);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        mRecyclerView.addItemDecoration(dividerItem);
        mAdapter = new SimilarAdapter(this, new ArrayList<XMMusic>());
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setClickListener(new OnRvItemClickListener<List<XMMusic>>() {
            @Override
            public void onItemClick(int position, List<XMMusic> xmMusicList) {
                OnlineMusicFactory.getKWPlayer().play(xmMusicList, position);
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                AlbumSwitchFragment parentFragment = (AlbumSwitchFragment) getParentFragment();
                if (parentFragment != null) {
                    parentFragment.backToOnlinePlayFragment();
                }
            }

            @Override
            public void onItemDelete() {

            }
        });
    }



    private void initData() {
        mSimilarVM = ViewModelProviders.of(this).get(SimilarVM.class);
        mSimilarVM.getSimilarList().observe(this, new Observer<XmResource<List<XMMusic>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<XMMusic>> musicInfos) {
                if (musicInfos != null) {
                    musicInfos.handle(new OnCallback<List<XMMusic>>() {
                        @Override
                        public void onSuccess(List<XMMusic> data) {
                            if (data != null) {
                                mAdapter.setData(data);
                                mAdapter.notifyDataSetChanged();
                                checkEmptyView(data);
                            }
                        }
                    });
                }
            }
        });
        fetchData();
    }

    private void checkEmptyView(List<XMMusic> data) {
        if (data.isEmpty()) {
            scrollBar.setVisibility(View.GONE);
            showEmptyView();
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    private void fetchData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        mSimilarVM.initVM();
    }
}
