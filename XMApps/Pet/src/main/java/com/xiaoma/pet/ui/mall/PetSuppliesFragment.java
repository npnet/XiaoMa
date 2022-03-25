package com.xiaoma.pet.ui.mall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.pet.R;
import com.xiaoma.pet.adapter.BasePetAdapter;
import com.xiaoma.pet.adapter.RVGridItemDecoration;
import com.xiaoma.pet.common.callback.OnUpdateLayoutCallback;
import com.xiaoma.pet.ui.view.PetScrollBar;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc: 目前只有食品列表，后面版本如果添加新列表，可直接在此基础上扩展
 */
public abstract class PetSuppliesFragment<T extends BasePetAdapter> extends BaseFragment {

    protected RecyclerView mRecyclerView;
    private PetScrollBar mPetScrollBar;
    private LinearLayout mEmptyLayout;
    private T basePetAdapter;
    private OnUpdateLayoutCallback mOnUpdateLayoutCallback;

    private boolean isFirst;
    private boolean isVisible;
    private boolean isEnableRefresh;

    protected final T getAdapter() {
        return basePetAdapter;
    }


    protected void initData() {
        //empty
    }

    protected abstract T createRVAdapter();

    protected abstract void onRVItemClick(BaseQuickAdapter adapter, View view, int position);


    protected final void updateLayout(Fragment fragment, String tag, boolean isStack) {
        if (mOnUpdateLayoutCallback != null) {
            mOnUpdateLayoutCallback.updateLayoutOnFragment(fragment, tag, isStack);
        }
    }


    protected final void showEmptyView() {
        mEmptyLayout.setVisibility(View.VISIBLE);
    }


    protected final void hideEmptyView() {
        mEmptyLayout.setVisibility(View.GONE);
    }


    public void refreshData() {
        if (isEnableRefresh) {
            initData();
        }
    }


    public void forceRefreshData() {
        initData();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnUpdateLayoutCallback = (OnUpdateLayoutCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet_supplies, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        isFirst = true;
        fetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isEnableRefresh = true;
            isVisible = true;
            fetchData();
        } else {
            isEnableRefresh = false;
            isVisible = false;
        }
    }


    private void initView(View contentView) {
        mEmptyLayout = contentView.findViewById(R.id.goods_empty_layout);
        mPetScrollBar = contentView.findViewById(R.id.pet_scroll_bar);
        mRecyclerView = contentView.findViewById(R.id.rv_pet_supplies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        RVGridItemDecoration itemDecoration = new RVGridItemDecoration(20);

        basePetAdapter = createRVAdapter();
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(basePetAdapter);

        basePetAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismissGuideWindow();
                if (!NetworkUtils.isConnected(getContext())) {
                    PetToast.showException(mContext, R.string.not_net);
                    return;
                }
                onRVItemClick(adapter, view, position);
            }
        });
        mPetScrollBar.setRecyclerViewBar(mRecyclerView);
    }


    private void fetchData() {
        if (isFirst && isVisible) {
            initData();

            //防止数据重复加载
            isFirst = false;
            isVisible = false;
        }
    }


    // 防止没有网络时 点击之后 新手引导无法消失问题
    protected void dismissGuideWindow() {

    }

}
