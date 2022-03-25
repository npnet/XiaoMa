package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.online.adapter.CategoryListAdapter;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.model.TagBean;
import com.xiaoma.xting.online.vm.CategoryAlbumVM;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_SUB_ALBUM_MULTI)
public class CategoryMultiAlbumListFragment extends VisibilityFragment {


    private static final String CATEGORY = "CATEGORY";
    private TextView tvClassName;
    private TabLayout tlChildClass;
    private ViewPager vpChildClass;
    private CategoryBean categoryBean;
    private CategoryAlbumVM categoryVM;
    private CategoryListAdapter<TagBean> mAdapter;
    private CategoryMultiAlbumListFragment.ViewHolder mHolder;
    private boolean Loaded;
    private StateView stateView;

    public static CategoryMultiAlbumListFragment newInstance(CategoryBean categoryBean) {
        CategoryMultiAlbumListFragment categoryMultiAlbumListFragment = new CategoryMultiAlbumListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, GsonHelper.toJson(categoryBean));
        categoryMultiAlbumListFragment.setArguments(args);
        return categoryMultiAlbumListFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initArguments();
        bindView(view);
        initView();
        initData();
    }

    private void initArguments() {
        if (getArguments() != null) {
            categoryBean = GsonHelper.fromJson(getArguments().getString(CATEGORY), CategoryBean.class);
        }
    }

    private void bindView(View view) {
        tvClassName = view.findViewById(R.id.tv_class_name);
        tlChildClass = view.findViewById(R.id.tl_child_class);
        vpChildClass = view.findViewById(R.id.vp_child_class);
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
        tvClassName.setText(categoryBean.getName());
    }

    private void initData() {
        mAdapter = new CategoryListAdapter<>(new ArrayList<TagBean>(), getChildFragmentManager());
        vpChildClass.setAdapter(mAdapter);
        tlChildClass.setupWithViewPager(vpChildClass);
        tlChildClass.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            private String mString;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mString, "");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    return;
                }
                mHolder = new ViewHolder(customView);
                mString = mHolder.tabTv.getText().toString();
            }
        });
        vpChildClass.setOffscreenPageLimit(1);
        categoryVM = ViewModelProviders.of(this).get(CategoryAlbumVM.class);
        categoryVM.getTags().observe(this, new Observer<XmResource<List<TagBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<TagBean>> tags) {
                if (tags != null) {
                    tags.handle(new OnCallback<List<TagBean>>() {
                        @Override
                        public void onSuccess(List<TagBean> data) {
                            if (CollectionUtil.isListEmpty(data)) {
                                showEmptyView();
                            } else {
                                Loaded = true;
                                mAdapter.setChildCategories(categoryBean.getId(), data);
                                setupTabView(tlChildClass);
                                tlChildClass.scrollTo(0, 0);
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

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_child_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new CategoryMultiAlbumListFragment.ViewHolder(view);
                    mHolder.tabTv.setText(mAdapter.getPageTitle(i));
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
            }
        }
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryVM.fetchChildCategoryList(categoryBean);
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
        }
    }

    @Override
    public boolean onBackPressed() {
        return popBack();
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
