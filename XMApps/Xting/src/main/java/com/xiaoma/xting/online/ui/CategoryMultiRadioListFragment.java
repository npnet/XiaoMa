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

import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.online.adapter.CategoryListAdapter;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.model.ProvinceBean;
import com.xiaoma.xting.online.vm.CategoryRadioVM;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_SUB_RADIO_MULTI)
public class CategoryMultiRadioListFragment extends VisibilityFragment {


    private static final String CATEGORY = "CATEGORY";
    private TextView tvClassName;
    private TabLayout tlChildClass;
    private ViewPager vpChildClass;
    private CategoryBean categoryBean;
    private CategoryRadioVM categoryRadioVM;
    private CategoryListAdapter<ProvinceBean> mAdapter;
    private boolean Loaded;

    public static CategoryMultiRadioListFragment newInstance(CategoryBean categoryBean) {
        CategoryMultiRadioListFragment categoryMultiListFragment = new CategoryMultiRadioListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, GsonHelper.toJson(categoryBean));
        categoryMultiListFragment.setArguments(args);
        return categoryMultiListFragment;
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
        mAdapter = new CategoryListAdapter<>(new ArrayList<ProvinceBean>(), getChildFragmentManager());
        vpChildClass.setAdapter(mAdapter);
        tlChildClass.setupWithViewPager(vpChildClass);

        vpChildClass.setOffscreenPageLimit(1);
        categoryRadioVM = ViewModelProviders.of(this).get(CategoryRadioVM.class);
        categoryRadioVM.getProvinces().observe(this, new Observer<XmResource<List<ProvinceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ProvinceBean>> channelBeans) {
                if (channelBeans != null) {
                    channelBeans.handle(new OnCallback<List<ProvinceBean>>() {
                        @Override
                        public void onLoading() {
                            if (checkVisible()) {
                                super.onLoading();
                            }
                        }

                        @Override
                        public void onSuccess(List<ProvinceBean> data) {
                            if (CollectionUtil.isListEmpty(data)) {
                                showEmptyView();
                            } else {
                                Loaded = true;
                                mAdapter.setChildCategories(categoryBean.getId(), data);
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

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryRadioVM.fetchProvinces();
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
}
