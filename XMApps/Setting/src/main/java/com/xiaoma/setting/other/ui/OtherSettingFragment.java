package com.xiaoma.setting.other.ui;

import android.content.Intent;
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
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.FragmentUtils;
import com.xiaoma.setting.common.views.SettingTabView;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.otherSettingFragmentPagePathDesc)
public class OtherSettingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    private DisplayFragment mDisplayFragment/* = new DisplayFragment()*/;
    private ThemeFragment mThemeFragment /*= new ThemeFragment()*/;
    private LanguageFragment mLanguageFragment /*= new LanguageFragment()*/;
    private VersionFragment mVersionFragment /*= new VersionFragment()*/;
    private ResetFragment mResetFragment /*= new ResetFragment()*/;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager fManager;
    private int prePosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view, savedInstanceState);
    }

    private void bindView(View view, Bundle savedInstanceState) {
        fManager = getFragmentManager();
        mDisplayFragment = (DisplayFragment) FragmentUtils.newFragmentInstance(fManager, DisplayFragment.class.getName());
        mThemeFragment = (ThemeFragment) FragmentUtils.newFragmentInstance(fManager, ThemeFragment.class.getName());
        mLanguageFragment = (LanguageFragment) FragmentUtils.newFragmentInstance(fManager, LanguageFragment.class.getName());
        mVersionFragment = (VersionFragment) FragmentUtils.newFragmentInstance(fManager, VersionFragment.class.getName());
        mResetFragment = (ResetFragment) FragmentUtils.newFragmentInstance(fManager, ResetFragment.class.getName());

        fragments.add(mDisplayFragment);
        fragments.add(mThemeFragment);
        fragments.add(mLanguageFragment);
        fragments.add(mVersionFragment);
        fragments.add(mResetFragment);
        initTabLayout(view, savedInstanceState);
    }

    private void initTabLayout(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            prePosition = savedInstanceState.getInt("position", 0);
        }
        int unselectedColor = getResources().getColor(R.color.setting_tab_unselect);
        TabLayout tab = view.findViewById(R.id.tablayout);
        String[] tabName = getResources().getStringArray(R.array.setting_other_menu);
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[0])));
        SettingTabView view1 = new SettingTabView(getContext(), tabName[1]);
        SettingTabView view2 = new SettingTabView(getContext(), tabName[2]);
        SettingTabView view3 = new SettingTabView(getContext(), tabName[3]);
        SettingTabView view4 = new SettingTabView(getContext(), tabName[4]);
        view1.setTextColor(unselectedColor);
        view2.setTextColor(unselectedColor);
        view3.setTextColor(unselectedColor);
        view4.setTextColor(unselectedColor);
        tab.addTab(tab.newTab().setCustomView(view1));
        tab.addTab(tab.newTab().setCustomView(view2));
        tab.addTab(tab.newTab().setCustomView(view3));
        tab.addTab(tab.newTab().setCustomView(view4));
        if (!ConfigManager.ApkConfig.isDebug()) {
            if (!LoginManager.getInstance().isUserLogin()) {
                prePosition = 4;
            }
        }
        tab.getTabAt(prePosition).select();
        setSelectTab(tab.getTabAt(prePosition), true);
        switchMenu(prePosition, prePosition);
        tab.addOnTabSelectedListener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", prePosition);
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
        switchFragment(fManager, R.id.fragment_other_container, fromFragment, toFragment);
        mContext.sendBroadcast(new Intent(LanguageFragment.ACTION_UPDATE_TIME));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (!ConfigManager.ApkConfig.isDebug()){
            if(!LoginManager.getInstance().isUserLogin() && tab.getPosition()!=4){
                XMToast.showToast(mContext, R.string.unable_to_use_this_feature);
                return;
            }
        }
        setSelectTab(tab, true);
        switchMenu(prePosition, tab.getPosition());
        prePosition = tab.getPosition();
        switch (tab.getPosition()) {
            case 0:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.displaySettingFragmentPagePathDesc,
                        "com.xiaoma.setting.other.ui.OtherSettingFragment",
                        EventConstants.PageDescribe.displaySettingFragmentPagePathDesc);
                break;
            case 1:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.themeSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.other.ui.OtherSettingFragment",
                        EventConstants.PageDescribe.themeSettingFragmentPagePathDesc);
                break;
            case 2:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.languageSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.other.ui.OtherSettingFragment",
                        EventConstants.PageDescribe.languageSettingFragmentPagePathDesc);
                break;
            case 3:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.versionSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.other.ui.OtherSettingFragment",
                        EventConstants.PageDescribe.versionSettingFragmentPagePathDesc);
                break;
            case 4:
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.resetSettingFragmentPagePathDesc,
                        "com.xiaoma.setting.other.ui.OtherSettingFragment",
                        EventConstants.PageDescribe.resetSettingFragmentPagePathDesc);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (!ConfigManager.ApkConfig.isDebug()) {
            if (!LoginManager.getInstance().isUserLogin() && tab.getPosition() == 4) return;
        }
        setSelectTab(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void switchFragment(FragmentManager fManager, int containerViewId, Fragment
            from, Fragment to) {
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
