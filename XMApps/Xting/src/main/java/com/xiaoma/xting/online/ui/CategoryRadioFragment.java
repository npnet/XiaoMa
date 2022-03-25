package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.online.adapter.GridTextAdapter;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.vm.CategoryVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_SUB_RADIO)
public class CategoryRadioFragment extends VisibilityFragment {

    private static final int SPAN_COUNT = 2;

    private RecyclerView rvCategory;
    private XmScrollBar scrollBar;
    private CategoryVM categoryVM;
    private GridTextAdapter<CategoryBean> mAdapter;
    private boolean Loaded;


    public static CategoryRadioFragment newInstance() {
        return new CategoryRadioFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_category, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    private void bindView(@NonNull View view) {
        rvCategory = view.findViewById(R.id.rv_category);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        rvCategory.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false));
//        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
//        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin);
//        int vertical = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_vertical_margin);
//        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_extra_margin);
//        decor.setRect(horizontal, vertical, horizontal, vertical);
//        decor.setExtraMargin(extra, SPAN_COUNT);
//        rvCategory.addItemDecoration(decor);
        DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getLayoutManager().getPosition(view);
                if (position % 2 == 0) {
                    outRect.top = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_sub_item_vertical_margin);
                }else{
                    outRect.top=mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_vertical_margin);

                }

                int extraMargin =mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_extra_margin);

                if (position == 0 || position == 1) {
                    outRect.left += mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin) + extraMargin;
                }

                outRect.right += mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin) + extraMargin+20;
                // 通过设置一个空的drawable来隐藏默认分割线
                setDrawable(new ColorDrawable());

            }
        };
        rvCategory.addItemDecoration(divider);
    }

    private void initData() {
        mAdapter = new GridTextAdapter<CategoryBean>(null) {
            @Override
            public ItemEvent returnPositionEventMsg(int position) {
                return new ItemEvent(getData().get(position).getName(), EventConstants.PageDescribe.TAG_RADIO);
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (NetworkUtils.isConnected(getContext())) {
                    CategoryBean data = (CategoryBean) adapter.getData().get(position);
                    if (data.isHeader) return;
                    if (getParentFragment() != null && getParentFragment().getParentFragment() != null
                            && getParentFragment().getParentFragment().getParentFragment() != null) {
                        NetFMFragment parentFragment = (NetFMFragment) getParentFragment().getParentFragment().getParentFragment();
                        String province = getString(R.string.radio_province);
                        if (province.equals(data.getName())) {
                            parentFragment.addFragment(CategoryMultiRadioListFragment.newInstance(data),
                                    NetFMFragment.FRAGMENT_TAG_CHILD_MULTI_RADIO_CLASS);
                        } else {
                            parentFragment.addFragment(CategorySingleRadioListFragment.newInstance(data),
                                    NetFMFragment.FRAGMENT_TAG_CHILD_RADIO_CLASS);
                        }
                    }

                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
            }
        });
        rvCategory.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvCategory);

        categoryVM = ViewModelProviders.of(this).get(CategoryVM.class);
        categoryVM.getRadioCategories().observe(this, new Observer<XmResource<List<CategoryBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CategoryBean>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<CategoryBean>>() {
                        @Override
                        public void onLoading() {
                            if (checkVisible()) {
                                super.onLoading();
                            }
                        }

                        @Override
                        public void onSuccess(List<CategoryBean> data) {
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
            }
        });
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryVM.fetchRadioCategory();
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void emptyOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible && !Loaded) {
            checkNetWork();
        }
    }
}
