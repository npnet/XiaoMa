package com.xiaoma.personal.newguide.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.personal.R;
import com.xiaoma.personal.newguide.adapter.NewGuideReopenAdapter;
import com.xiaoma.personal.newguide.model.AppInfo;
import com.xiaoma.personal.newguide.vm.NewGuideReopenVM;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;

public class NewGuideReopenActivity extends BaseActivity {

    private final String TAG = "NewGuideReopenActivity";
    private RecyclerView mRvApps;
    private XmScrollBar mScrollBar;
    private ArrayList<AppInfo.DataBean> appInfos = new ArrayList<>();
    private NewGuideReopenAdapter mAdapter;
    private NewGuideReopenVM mGuideReopenVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newguide_reopen);
        initView();
        initData();
        setListeners();
    }

    private void setListeners() {
        mAdapter.setOnItemClickListener((adapter, view, holder, position) -> {
            String packName = appInfos.get(position).getPackageName();
//            String guideStatusFlag = appInfos.get(position).getGuideStatusFlag();
            if (TextUtils.isEmpty(packName) /*|| TextUtils.isEmpty(guideStatusFlag)*/) {
                Log.d(TAG, "onItemClick: packName or guideStatusFlag is empty!!!");
                return;
            }
            // 启动目标app 并修改新手引导标识
            resetGuideData(packName/*, guideStatusFlag*/);
        });
    }

    private void initData() {
        mGuideReopenVM = ViewModelProviders.of(this).get(NewGuideReopenVM.class);
        mGuideReopenVM.getAppList().observe(this, dataBeanXmResource -> {
            if (dataBeanXmResource == null) return;
            dataBeanXmResource.handle(new OnCallback<ArrayList<AppInfo.DataBean>>() {
                @Override
                public void onSuccess(ArrayList<AppInfo.DataBean> data) {
                    showContentView();
                    appInfos.addAll(data);
                    mAdapter.setDatas(appInfos);
                }
            });
        });
        mGuideReopenVM.getGuideAppList();
    }

    private void initView() {
        mRvApps = findViewById(R.id.rv_apps);
        mScrollBar = findViewById(R.id.xsb_scrollbar);
        mRvApps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvApps.setAdapter(mAdapter = new NewGuideReopenAdapter(this, appInfos, R.layout.item_view_guide_reopen));
        mScrollBar.setRecyclerView(mRvApps);
    }

    private void resetGuideData(String packName/*, String guideStatusFlag*/) { // 将引导标识改为包名 方便处理
        GuideDataHelper.resetGuideData(packName);
        if ("com.xiaoma.launcher".equals(packName)){ // 桌面的话 启动MainActivity
            LaunchUtils.launchApp(this, packName, "com.xiaoma.launcher.main.ui.MainActivity", null);
            return;
        }
        boolean isAppInstall = LaunchUtils.launchApp(this, packName, "", null, true);
        if (!isAppInstall) {
            Log.d(TAG, "resetGuideData: current app is not installed");
        }
    }

    @Override
    protected void errorOnRetry() {
        mGuideReopenVM.getGuideAppList();
    }

    @Override
    protected void emptyOnRetry() {
        mGuideReopenVM.getGuideAppList();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mGuideReopenVM.getGuideAppList();
        }
    }
}
