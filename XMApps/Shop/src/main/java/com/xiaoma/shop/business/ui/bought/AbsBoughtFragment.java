package com.xiaoma.shop.business.ui.bought;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.business.ui.bought.callback.ChangeFragmentCallback;
import com.xiaoma.shop.business.ui.bought.callback.OneKeyCleanCacheCallback;
import com.xiaoma.shop.business.ui.flow.MainFlowFragment2;
import com.xiaoma.shop.business.ui.view.DownloadMoreView;
import com.xiaoma.shop.business.vm.MineBoughtVm;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 我的购买基类
 */
public abstract class AbsBoughtFragment<M, A extends BaseQuickAdapter<M, BaseViewHolder>> extends ShopBaseFragment {

    private static final String TAG = AbsBoughtFragment.class.getName();
    private RecyclerView recyclerView;
    private XmScrollBar mXmScrollBar;
    protected A mBaseQuickAdapter;
    private boolean isFragmentVisible;
    private MineBoughtVm mineBoughtVm;
    private ChangeFragmentCallback changeFragmentCallback;
    private OneKeyCleanCacheCallback oneKeyCleanCacheCallback;

    private static final int PAGE_SIZE = 10;
    private int currentPage = -1;
    private int totalPage = 0;

    abstract protected String getItemDownloadUrl(M m);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFragmentCallback = (ChangeFragmentCallback) getActivity();
        oneKeyCleanCacheCallback = (OneKeyCleanCacheCallback) getParentFragment();
    }

    private BroadcastReceiver mSkinChangeReceiver;
    private DownloadListener mDownloadListener;
    private SkinDownload mSkinDownload;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_bought, container, false);
        return onCreateWrapView(mView);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            // 监听皮肤变化
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SkinConstants.SKIN_ACTION);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_XM);
            getActivity().registerReceiver(mSkinChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ViewCompat.postOnAnimationDelayed(recyclerView, new Runnable() {
                        @Override
                        public void run() {
                            if (isDestroy())
                                return;
                            if (mBaseQuickAdapter != null)
                                if (getParentFragment() instanceof BoughtMainFragment) {
                                    if (((BoughtMainFragment) getParentFragment()).isAllSelected()) {
                                        ((BoughtMainFragment) getParentFragment()).selectAll(true);
                                    } else {
                                        ((BoughtMainFragment) getParentFragment()).refreshCacheSize();
                                    }
                                }
                            mBaseQuickAdapter.notifyDataSetChanged();
                        }
                    }, 200);
                }
            }, intentFilter);
            // 监听所有下载内容
            mSkinDownload = SkinDownload.getInstance();
            mSkinDownload.addDownloadListener(mDownloadListener = new DownloadListener() {
                @Override
                public void onDownloadStatus(final DownloadStatus downloadStatus) {
                    ViewCompat.postOnAnimation(view, new Runnable() {
                        @Override
                        public void run() {
                            if (isDestroy() || mBaseQuickAdapter == null)
                                return;
                            SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
                                @Override
                                public void doWork(Object lastResult) {
                                    List<Integer> posList = new ArrayList<>();
                                    List<M> data = mBaseQuickAdapter.getData();
                                    for (int i = 0; i < data.size(); i++) {
                                        M m = data.get(i);
                                        if (Objects.equals(getItemDownloadUrl(m), downloadStatus.downUrl)) {
                                            posList.add(i);
                                        }
                                    }
                                    if (!posList.isEmpty()) {
                                        doNext(posList);
                                    }
                                }
                            }).next(new Work<List<Integer>>() {
                                @Override
                                public void doWork(List<Integer> posList) {
                                    for (Integer integer : posList) {
                                        mBaseQuickAdapter.notifyItemChanged(integer);
                                    }
                                }
                            }).start();
                        }
                    });
                }
            });
        }

        initView(view);
        initListener();
        enableLoadMore(true);
        checkNetwork();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            if (mSkinChangeReceiver != null)
                getActivity().unregisterReceiver(mSkinChangeReceiver);
            if (mDownloadListener != null)
                mSkinDownload.removeDownloadListener(mDownloadListener);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isVisibleToUser) {
            if (getOneKeyCleanCacheCallback() != null) {
                getOneKeyCleanCacheCallback().initAction(cacheBindStatus(), requestResourceType());
            }
        }
    }

    private void initView(View view) {
        mXmScrollBar = view.findViewById(R.id.scroll_bar);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int left = 0;
                if (parent.getChildAdapterPosition(view) == 0) {
                    left = mContext.getResources().getDimensionPixelOffset(R.dimen.size_buy_pay_title_margin_left);
                }
                outRect.set(left, 0, 90, 0);
            }
        });
        mBaseQuickAdapter = createAdapter();
        recyclerView.setAdapter(mBaseQuickAdapter);
        mXmScrollBar.setRecyclerView(recyclerView);
    }


    private void initListener() {
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AbsBoughtFragment.this.onItemClick(adapter, view, position);
            }
        });

        mBaseQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                AbsBoughtFragment.this.onItemChildClick(adapter, view, position);
            }
        });

    }

    protected void checkNetwork() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();
        initData();

        if (isFragmentVisible) {
            if (getOneKeyCleanCacheCallback() != null) {
                getOneKeyCleanCacheCallback().initAction(cacheBindStatus(), requestResourceType());
            }
        }
    }

    private void initData() {
        currentPage = 0;
        mineBoughtVm = ViewModelProviders.of(this).get(MineBoughtVm.class);
        fetchData();
    }


    private void fetchData() {
        String userId = LoginManager.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(userId)) {
            XMToast.showToast(mContext, "用户不存在");
            return;
        }
        XMProgress.showProgressDialog(this, mContext.getString(R.string.loading));
        mineBoughtVm.getMineBought(requestResourceType(), Long.parseLong(userId), currentPage, PAGE_SIZE)
                .observe(this, new Observer<XmResource<MineBought>>() {
                    @Override
                    public void onChanged(@Nullable XmResource<MineBought> mineBoughtXmResource) {
                        XMProgress.dismissProgressDialog(AbsBoughtFragment.this);
                        if (mineBoughtXmResource == null) {
                            KLog.e(TAG, "mineBoughtXmResource is null.");
                            return;
                        }
                        mineBoughtXmResource.handle(new XmResource.OnHandleCallback<MineBought>() {
                            @Override
                            public void onLoading() {

                            }

                            @Override
                            public void onSuccess(MineBought data) {
                                if (data == null || data.getPageInfo() == null) {
                                    showEmptyView();
                                    return;
                                }

                                totalPage = data.getPageInfo().getTotalPage();
                                if (currentPage <= totalPage) {
                                    //第一页数据
                                    if (currentPage == 0) {
                                        obtainActuallyData(false, data);
                                    } else {
                                        obtainActuallyData(true, data);
                                    }
                                    notifyLoadMoreState(LoadMoreState.COMPLETE);
                                    ++currentPage;


                                    //超过页数,关闭加载更多
                                    if (currentPage >= totalPage) {
                                        notifyLoadMoreState(LoadMoreState.END);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                showErrorView();
                            }

                            @Override
                            public void onError(int code, String message) {
                                if (!NetworkUtils.isConnected(mContext)|| getString(R.string.network_error).equals(message)) {
                                    showNoNetView();
                                } else {
                                    showErrorView();
                                }
                            }

                            @Override
                            public void onCompleted() {

                            }
                        });
                    }
                });
    }


    @Override
    protected void noNetworkOnRetry() {
        checkNetwork();
    }


    @Override
    protected void errorOnRetry() {
        checkNetwork();
    }

    @Override
    public void onStop() {
        super.onStop();
        XMProgress.dismissProgressDialog(this);
    }


    protected abstract A createAdapter();

    protected abstract int requestResourceType();

    protected abstract int cacheBindStatus();

    protected abstract void obtainActuallyData(boolean more, MineBought data);


    protected final void enableLoadMore(boolean enable) {
        if (mBaseQuickAdapter == null) {
            KLog.e("mBaseQuickAdapter field is null");
            return;
        }

        mBaseQuickAdapter.setEnableLoadMore(enable);
        mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                fetchData();
            }
        }, recyclerView);

        mBaseQuickAdapter.setLoadMoreView(new DownloadMoreView());

    }


    /**
     * 如果支持加载更多,每次加载之后一定要调用 告诉adapter加载的状态
     */
    protected final void notifyLoadMoreState(@LoadMoreState int loadState) {
        if (loadState == LoadMoreState.COMPLETE) {
            mBaseQuickAdapter.loadMoreComplete();
        } else if (loadState == LoadMoreState.END) {
            mBaseQuickAdapter.loadMoreEnd(true);//表示加载到底之后 会给提示
        } else if (loadState == LoadMoreState.FAIL) {
            mBaseQuickAdapter.loadMoreFail();
        } else {
            KLog.e("loadState: " + loadState);
        }
    }


    protected final ChangeFragmentCallback getChangeFragmentCallback() {
        return changeFragmentCallback;
    }


    protected final OneKeyCleanCacheCallback getOneKeyCleanCacheCallback() {
        return oneKeyCleanCacheCallback;
    }


    protected final A getAdapter() {
        return mBaseQuickAdapter;
    }


    protected void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // Nothing to do, child Override
    }

    protected void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        // Nothing to do, child Override
    }


    public void cleanCacheOperation(boolean open) {
        //Nothing to do
        //向BoughtMainFragment 提供一键删除操作，控制皮肤和语音音色 true打开，false 关闭
    }

    //是否开启清理缓存，开启状态，点击返回键取消缓存清理（而非退出页面）
    public boolean isExecutingCleanCache() {
        return false;
    }

    public void executeCleanCache() {
        //执行删除缓存 子类实现
    }

    public String selectAll(boolean select) {
        //选择所有文件,返回所有文件大小
        return null;
    }

    public List<M> getBoughtData() {
        // 获取已购买列表 数据。由子类实现
        return null;
    }

    public long calcuCacheSize() {
        // 计算占用空间大小
        return 0;
    }

    public int calcuCanDeleteFileNum() {
        // 计算所有文件数，由子类实现
        return 0;
    }

    public int calcuSelectedNum() {
        // 计算选择的文件数，由子类实现
        return 0;
    }

    public long calcuSelectedCacheSize() {
        // 计算选择的需要清理的文件总大小
        return 0;
    }
}
