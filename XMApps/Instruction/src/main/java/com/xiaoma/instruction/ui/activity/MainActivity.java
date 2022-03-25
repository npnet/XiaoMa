package com.xiaoma.instruction.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.instruction.R;
import com.xiaoma.instruction.adapter.ManualCatalogAdapter;
import com.xiaoma.instruction.common.constant.InstructionConstants;
import com.xiaoma.instruction.mode.ManualBean;
import com.xiaoma.instruction.vm.ManualVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.apptool.AppObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/6/1 0001
 * 使用手册
 */

public class MainActivity extends BaseActivity {
    public static final String TAG = "[MainActivity]";
    RecyclerView mRecyclerView;
    ManualCatalogAdapter mManualCatalogAdapter;
    private ViewModel mManualVm;
    private List<ManualBean> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_manual);
        initView();
        initData();
    }

    private void initData() {
        mManualVm = ViewModelProviders.of(this).get(ManualVM.class);
        ((ManualVM) mManualVm).getManualDates().observe(this, new Observer<XmResource<List<ManualBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ManualBean>> manualBeanXmResource) {
                if (manualBeanXmResource == null) {
                    return;
                }
                manualBeanXmResource.handle(new OnCallback<List<ManualBean>>() {

                    @Override
                    public void onSuccess(List<ManualBean> data) {
                        showContentView();
                        if (!ListUtils.isEmpty(data)) {
                            mList.addAll(data);
                            mManualCatalogAdapter.addData(mList);
                        }
                    }
                });
            }
        });
        ((ManualVM) mManualVm).fetchManual();
    }

    public void initView() {
        mRecyclerView = findViewById(R.id.manual_catalog);

        mManualCatalogAdapter = new ManualCatalogAdapter();
        mManualCatalogAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectItem(position);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
//        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
//        int left = 50;
//        int top = 50;
//        decor.setRect(left, top, 0, 0);
//
//        mRecyclerView.addItemDecoration(decor);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setClipToPadding(true);
        mRecyclerView.setAdapter(mManualCatalogAdapter);
    }

    public void selectItem(int position) {
        Intent intent = new Intent(MainActivity.this, ManualItemActivity.class);
        intent.putExtra(InstructionConstants.MANUAL_ITEM_BEAN, mList.get(position).getId());
        startActivity(intent);

    }

    @Override
    protected void noNetworkOnRetry() {
        if (NetworkUtils.isConnected(this)) {
            ((ManualVM) mManualVm).fetchManual();
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void errorOnRetry() {
        showContentView();
        ((ManualVM) mManualVm).fetchManual();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        AppObserver.getInstance().closeAllActivitiesAndExit();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppObserver.getInstance().closeAllActivitiesAndExit();
    }
}
