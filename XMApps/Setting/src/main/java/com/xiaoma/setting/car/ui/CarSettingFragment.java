package com.xiaoma.setting.car.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.car.vm.CarSettingVM;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.FragmentUtils;
import com.xiaoma.setting.common.views.SettingTabView;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.carSettingFragmentPagePathDesc)
public class CarSettingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {
    private CarSettingVM settingVM;
    private List<Fragment> fragments = new ArrayList<>();
    private int prePosition = 0;
    private FragmentManager fManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view, savedInstanceState);
    }


    private void bindView(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_container);
        fManager = getFragmentManager();
        settingVM = ViewModelProviders.of(this).get(CarSettingVM.class).initData();
        /*fragments.add(new SafetyFragment());
        fragments.add(new ComfortFragment());
        fragments.add(new LamplightFragment());*/
        fragments.add(FragmentUtils.newFragmentInstance(fManager, SafetyFragment.class.getName()));
        fragments.add(FragmentUtils.newFragmentInstance(fManager, ComfortFragment.class.getName()));
        fragments.add(FragmentUtils.newFragmentInstance(fManager, LamplightFragment.class.getName()));
        if (XmCarConfigManager.hasFaceRecognition() || XmCarConfigManager.hasFcwAndAeb()) { //有前防撞预警或者人脸识别
            fragments.add(FragmentUtils.newFragmentInstance(fManager, FaceSettingFragment.class.getName()));
        }
        initTabLayout(view, savedInstanceState);
    }


    private void initTabLayout(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            prePosition = savedInstanceState.getInt("position", 0);
        }
        TabLayout tab = view.findViewById(R.id.tablayout);
        String[] tabName = getResources().getStringArray(R.array.setting_drive_menu);
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[0])));
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[1])));
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[2])));
        if (XmCarConfigManager.hasFaceRecognition() || XmCarConfigManager.hasFcwAndAeb()) {
            tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[3])));
        }
        tab.getTabAt(prePosition).select();
        setSelectTab(tab.getTabAt(prePosition), true);
        switchMenu(0, prePosition);
        tab.addOnTabSelectedListener(this);
    }

    public void setSelectTab(TabLayout.Tab tab, boolean isSelected) {
        final SettingTabView tabView = (SettingTabView) tab.getCustomView();
        tabView.setSelect(isSelected);
    }

    public void switchMenu(int from, int to) {
        int viewSize = fragments.size();
        if (to < 0 || from < 0 || to >= viewSize || from > viewSize || fManager == null) {
            KLog.e("switchMenu error:" + to + " fManager is null:" + (fManager == null));
            return;
        }
        Fragment fromFragment = fragments.get(from);
        Fragment toFragment = fragments.get(to);
        switchFragment(fManager, R.id.fragment_car_container, fromFragment, toFragment);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", prePosition);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setSelectTab(tab, true);
        switchMenu(prePosition, tab.getPosition());
        prePosition = tab.getPosition();
        switch (tab.getPosition()) {
            case 0:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.safetySettingFragmentPagePathDesc,
                        "com.xiaoma.setting.car.ui.CarSettingFragment",
                        EventConstants.PageDescribe.safetySettingFragmentPagePathDesc);
                break;
            case 1:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.comfortSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.car.ui.CarSettingFragment",
                        EventConstants.PageDescribe.comfortSettingFragmentPagePathDesc);
                break;
            case 2:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.lamplightSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.car.ui.CarSettingFragment",
                        EventConstants.PageDescribe.lamplightSettingFragmentPagePathDesc);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        setSelectTab(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void switchFragment(FragmentManager fManager, int containerViewId, Fragment from, Fragment to) {
        FragmentTransaction transaction = fManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from);
//            transaction.add(containerViewId, to, to.getTag());
            transaction.add(containerViewId, to, to.getClass().getName());
            transaction.show(to).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

}
