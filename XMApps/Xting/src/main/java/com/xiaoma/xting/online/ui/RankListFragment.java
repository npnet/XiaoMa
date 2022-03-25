package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.GalleryAdapter;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.online.vm.PlayWithPlayerInfoVM;
import com.xiaoma.xting.online.vm.RankVM;
import com.xiaoma.xting.sdk.bean.XMRankCategory;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/11
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_RANK_LIST)
public class RankListFragment extends VisibilityFragment {

    public static final String RANK = "rank";
    public static final int MIN_SCROLL_DISTANCE = 100;
    public static final int MIN_VELOCITY = 10;
    private TextView boardName;
    private RecyclerView rvBoard;
    private XmScrollBar scrollBar;
    private XMRankCategory rankCategoryBean;
    private GestureDetector detector;
    private RankVM rankVM;
    private GalleryAdapter<PlayerInfo> mAdapter;
    private boolean Loaded;

    public static RankListFragment newInstance(XMRankCategory rankCategoryBean) {
        RankListFragment rankListFragment = new RankListFragment();
        Bundle args = new Bundle();
        args.putString(RANK, GsonHelper.toJson(rankCategoryBean));
        rankListFragment.setArguments(args);
        return rankListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    private void bindView(View view) {
        boardName = view.findViewById(R.id.board_name);
        rvBoard = view.findViewById(R.id.rv_board);
        scrollBar = view.findViewById(R.id.scroll_bar);
        mStateView = view.findViewById(R.id.state_view);
        mStateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                switch (type) {
                    case ERROR:
                        errorOnRetry();
                        break;
                    case EEMPTY:
                        emptyOnRetry();
                        break;
                    case NONETWORK:
                        noNetworkOnRetry();
                        break;
                }
            }
        });
    }

    private void initView() {
        rvBoard.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_horizontal_margin);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_extra_margin);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        rvBoard.addItemDecoration(decor);
        this.detector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getY() - e1.getY() > MIN_SCROLL_DISTANCE && Math.abs(velocityY) > MIN_VELOCITY) {
                    if (getFragmentManager() != null) {
                        FragmentUtils.pop(getFragmentManager());
                    }
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        if (getView() != null) {
            getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }
            });
        }
    }

    private void initData() {
        if (getArguments() != null) {
            rankCategoryBean = GsonHelper.fromJson(getArguments().getString(RANK), XMRankCategory.class);
        }
        final PlayWithPlayerInfoVM playVM = ViewModelProviders.of(getActivity()).get(PlayWithPlayerInfoVM.class);
        if (rankCategoryBean != null) {
            boardName.setText(rankCategoryBean.getName());
            mAdapter = new GalleryAdapter<>(this);
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (NetworkUtils.isConnected(getContext())) {
                        PlayerInfo playerInfo = mAdapter.getData().get(position);
                        playVM.play(playerInfo);
                    } else {
                        XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                    }
                }
            });
            mAdapter.bindToRecyclerView(rvBoard);
            scrollBar.setRecyclerView(rvBoard);

            rankVM = ViewModelProviders.of(this).get(RankVM.class);

            rankVM.getRankDetails().observe(this, new Observer<XmResource<List<PlayerInfo>>>() {
                @Override
                public void onChanged(@Nullable XmResource<List<PlayerInfo>> listXmResource) {
                    listXmResource.handle(new OnCallback<List<PlayerInfo>>() {
                        @Override
                        public void onSuccess(List<PlayerInfo> data) {
                            if (CollectionUtil.isListEmpty(data)) {
                                showEmptyView();
                            } else {
                                Loaded = true;
                                mAdapter.setNewData(data);
                                showContentView();
                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            if (!Loaded) {
                                super.onFailure(msg);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (!Loaded) {
                                super.onError(code, message);
                            }
                        }
                    });
                }
            });
        }
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            rankVM.fetchRank(rankCategoryBean.getRankListId());
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    protected void emptyOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible && !Loaded) {
            checkNetWork();
        } else {
            mAdapter.stopMarquee();
        }
    }

    @Override
    public boolean onBackPressed() {
        return popBack();
    }
}
