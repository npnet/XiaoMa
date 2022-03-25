package com.xiaoma.setting.wifi.ui;

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
import android.widget.LinearLayout;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.FragmentUtils;
import com.xiaoma.setting.common.views.SettingTabView;
import com.xiaoma.setting.sdk.ui.WifiConnectionFragment;
import com.xiaoma.setting.sdk.ui.WifiHotSpotFragment;
import com.xiaoma.utils.log.KLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.wifiSettingFragmentPagePathDesc)
public class WifiSettingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    private int defPosition = 0;
    private int prePosition = 0;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initFragment();
        bindView(view, savedInstanceState);
    }

    private void initFragment() {
        fragmentManager = getFragmentManager();
       /* fragments.add(new WifiConnectionFragment());
        fragments.add(new WifiHotSpotFragment());*/
        fragments.add(FragmentUtils.newFragmentInstance(fragmentManager, WifiConnectionFragment.class.getName()));
        fragments.add(FragmentUtils.newFragmentInstance(fragmentManager, WifiHotSpotFragment.class.getName()));
    }

    private void bindView(View view, Bundle savedInstanceState) {
        initTabLayout(view, savedInstanceState);
    }

    private void initTabLayout(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            prePosition = savedInstanceState.getInt("position", 0);
        }
        TabLayout tab = view.findViewById(R.id.tablayout);
        String[] tabName = getResources().getStringArray(R.array.wifi_settings_menu);
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[0])));
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[1])));
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

    private void switchMenu(int from, int to) {
        int viewSize = fragments.size();
        if (to < 0 || from < 0 || to >= viewSize || from > viewSize || fragmentManager == null) {
            KLog.e("switchMenu error:" + to + " fManager is null:" + (fragmentManager == null));
            return;
        }
        Fragment fromFragment = fragments.get(from);
        Fragment toFragment = fragments.get(to);
        switchFragment(fragmentManager, R.id.wifi_content, fromFragment, toFragment);
    }

    private void switchFragment(FragmentManager fragmentManager, int wifi_content, Fragment from, Fragment to) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from);
//            transaction.add(wifi_content, to, to.getTag());
            transaction.add(wifi_content, to, to.getClass().getName());
            transaction.show(to).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

    public void setSelectTab(TabLayout.Tab tab, boolean isSelected) {
        final SettingTabView tabView = (SettingTabView) tab.getCustomView();
        tabView.setSelect(isSelected);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setSelectTab(tab, true);
        switchMenu(prePosition, tab.getPosition());
        prePosition = tab.getPosition();
        if (tab.getPosition() == 0) {
            XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.wifiConnectionFragmentPagePathDesc,
                    "com.xiaoma.setting.wifi.ui.WifiSettingFragment",
                    EventConstants.PageDescribe.wifiConnectionFragmentPagePathDesc);
        } else {
            XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.wifiHotSpotFragmentPagePathDesc,
                    "com.xiaoma.setting.wifi.ui.WifiSettingFragment",
                    EventConstants.PageDescribe.wifiHotSpotFragmentPagePathDesc);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (fragments == null || fragments.isEmpty()) return;
        if (hidden) {
            for (Fragment fragment : fragments) {
                getFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
            }
        } else {
            getFragmentManager().beginTransaction().show(fragments.get(prePosition)).commitAllowingStateLoss();
        }
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        setSelectTab(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
