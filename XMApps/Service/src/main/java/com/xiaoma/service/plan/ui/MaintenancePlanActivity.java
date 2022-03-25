package com.xiaoma.service.plan.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventBusTags;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.plan.adapter.MaintenancePlanAdapter;
import com.xiaoma.service.plan.model.MaintenancePlanBean;
import com.xiaoma.service.plan.vm.MaintenancePlanVM;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * Created by Thomas on 2018/11/13 0013
 * 养车计划
 */
@PageDescComponent(EventConstants.PageDescribe.planActivityPagePathDesc)
public class MaintenancePlanActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private XmScrollBar xmScrollBar;
    private MaintenancePlanVM maintenancePlanVM;
    private MaintenancePlanAdapter planAdapter;
    private boolean guideLoaded;
    private NewGuide newGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_plan);
        initView();
        initData();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        xmScrollBar = findViewById(R.id.scroll_bar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decoration = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        ;
        int extra = getResources().getDimensionPixelOffset(R.dimen.maintain_paln_list_extra);
        decoration.setExtraMargin(extra);
        recyclerView.addItemDecoration(decoration);
        planAdapter = new MaintenancePlanAdapter(this);
        recyclerView.setAdapter(planAdapter);
        xmScrollBar.setRecyclerView(recyclerView);
    }

    private void initData() {
        maintenancePlanVM = ViewModelProviders.of(this).get(MaintenancePlanVM.class);
        maintenancePlanVM.getMaintenancePlanList().observe(this, new Observer<XmResource<MaintenancePlanBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<MaintenancePlanBean> maintenancePlanBeanXmResource) {
                if (maintenancePlanBeanXmResource == null) return;
                maintenancePlanBeanXmResource.handle(new OnCallback<MaintenancePlanBean>() {
                    @Override
                    public void onSuccess(MaintenancePlanBean data) {
                        List<MaintenancePlanBean.PlansBean> planBeanList = data.getPlans();
                        if (planBeanList == null || planBeanList.isEmpty()) {
                            planAdapter.setEmptyView(R.layout.maintenance_plab_notdata, (ViewGroup) recyclerView.getParent());
                        }
                        planAdapter.setCurrentTime(data.getTime());
                        planAdapter.setNewData(planBeanList);
                        //列表滑动到当前保养项
                        recyclerView.scrollToPosition(data.getTime() - 1);
                        showGuideWindow();
                    }
                });
            }
        });

        maintenancePlanVM.fetchMaintenancePlanList(CarDataManager.getInstance().getVinInfo());
    }

    private void showGuideWindow() {
        if (guideLoaded) return;
        guideLoaded = true;
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, false))
            return;
//        final View view = ((ViewGroup) ((ViewGroup) getNaviBar().getChildAt(0)).getChildAt(0)).getChildAt(0);
        final View view = getNaviBar().getBackView();
        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(view);
                Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 60), viewRect.right, viewRect.top + (viewRect.height() / 2 + 60));
                newGuide = NewGuide.with(MaintenancePlanActivity.this)
                        .setLebal(GuideConstants.SERVICE_SHOWED)
                        .setTargetView(view)
                        .setGuideLayoutId(R.layout.guide_view_plan)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .needMoveUpALittle(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setTargetViewTranslationX(0.02f)
                        .setHighLightRect(targetRect)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                dismissGuideWindow();
                                EventBus.getDefault().post("", EventBusTags.SHOW_SECOND_GUIDE);
                                onBackPressed();
                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();


            newGuide = null;
        }
    }
    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        checkNetWork();
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(this) ) {
            maintenancePlanVM.fetchMaintenancePlanList(CarDataManager.getInstance().getVinInfo());
        } else {
            showNoNetView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("MaintenancePlanActivity", "onKeyDown: " + event.getAction());
        return super.onKeyDown(keyCode, event);
    }
}
