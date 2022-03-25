package com.xiaoma.carpark.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.carpark.R;
import com.xiaoma.carpark.common.constant.CarParkConstants;
import com.xiaoma.carpark.main.adapter.TypeFragmentAdapter;
import com.xiaoma.carpark.main.vm.MainFragmentVM;
import com.xiaoma.carpark.main.model.XMPluginInfo;
import com.xiaoma.carpark.webview.ui.WebviewActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2019/4/11
 */
public class MainTypeFragment extends BaseFragment {

    public static final String BUNDLE_TYPE = "type";

    private RecyclerView mMainRecyclerView;
    private XmScrollBar mScrollBar;
    private TypeFragmentAdapter mTypeAdapter;
    private MainFragmentVM mainFragmentVM;
    private int mType;

    public static MainTypeFragment newTypeFragmentInstance(int type) {
        MainTypeFragment fragment = new MainTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TYPE, type);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mType = getArguments().getInt(BUNDLE_TYPE);
        bindView(view);
        initView();
        initData();
    }

    private void bindView(@NonNull View view) {
        mMainRecyclerView = view.findViewById(R.id.rv_main);
        mScrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = getResources().getDimensionPixelSize(R.dimen.item_horizontal_margin);
        int extra = getResources().getDimensionPixelSize(R.dimen.item_extra_margin);
        decor.setRect(horizontal, 40, horizontal, 0);
        decor.setExtraMargin(extra);
        mMainRecyclerView.addItemDecoration(decor);
    }

    private void initData() {
        mTypeAdapter = new TypeFragmentAdapter(mType);
        mMainRecyclerView.setAdapter(mTypeAdapter);
        mScrollBar.setRecyclerView(mMainRecyclerView);
        mTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                XMPluginInfo pluginInfo = mTypeAdapter.getData().get(position);
                if (pluginInfo.reflectType == CarParkConstants.INNER_JUMP) {
                    PluginEntryActivity.startActivity(mContext, pluginInfo);

                } else {
                    // TODO: 2019/5/28 打开H5页面
//                    showToast("start web view");
                    startActivity(new Intent(getActivity(), WebviewActivity.class));
                }
            }
        });
        mainFragmentVM = ViewModelProviders.of(this).get(MainFragmentVM.class);
        mainFragmentVM.getPluginInfoList().observe(this, new Observer<XmResource<List<XMPluginInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<XMPluginInfo>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<XMPluginInfo>>() {
                    @Override
                    public void onSuccess(List<XMPluginInfo> data) {
                        mTypeAdapter.setNewData(data);
                        mTypeAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mMainRecyclerView.getParent());
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        showNoNetView();
                    }
                });
            }
        });
        mainFragmentVM.fetchPluginInfoList(mType);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mainFragmentVM.fetchPluginInfoList(mType);
    }
}
