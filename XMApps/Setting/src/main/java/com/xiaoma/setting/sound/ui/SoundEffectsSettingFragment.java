package com.xiaoma.setting.sound.ui;

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
import com.xiaoma.utils.log.KLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.soundEffectsSettingFragmentPagePathDesc)
public class SoundEffectsSettingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    public static final String TAG = "SoundEffectsSettingFragment";
    private List<Fragment> fragments = new ArrayList<>();
    private int prePosition = 0;
    private FragmentManager fManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sound_effect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view, savedInstanceState);
    }

    private void bindView(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_container);
        fManager = getChildFragmentManager();
        /*fragments.add(new VolumeSettingsFragment());
        fragments.add(new EffectsSettingsFragment());*/
        fragments.add(FragmentUtils.newFragmentInstance(fManager, VolumeSettingsFragment.class.getName()));
        fragments.add(FragmentUtils.newFragmentInstance(fManager, EffectsSettingsFragment.class.getName()));
        initTabLayout(view, savedInstanceState);
    }

    private void initTabLayout(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            prePosition = savedInstanceState.getInt("position", 0);
        }
        final TabLayout tab = view.findViewById(R.id.tabLayout);
        String[] tabName = getResources().getStringArray(R.array.sound_settings_menu);
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[0])));
        tab.addTab(tab.newTab().setCustomView(new SettingTabView(getContext(), tabName[1])));
        tab.getTabAt(prePosition).select();
        setSelectTab(tab.getTabAt(prePosition), true);
        switchMenu(prePosition, prePosition);
        tab.addOnTabSelectedListener(this);
    }

    public void setSelectTab(TabLayout.Tab tab, boolean isSelected) {
        final SettingTabView tabView = (SettingTabView) tab.getCustomView();
        tabView.setSelect(isSelected);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", prePosition);
    }

    public void switchMenu(int from, int to) {
        int viewSize = fragments.size();
        if (to < 0 || from < 0 || to >= viewSize || from > viewSize || fManager == null) {
            KLog.e("switchMenu error:" + to + " fManager is null:" + (fManager == null));
            return;
        }
        Fragment fromFragment = fragments.get(from);
        Fragment toFragment = fragments.get(to);
        switchFragment(fManager, R.id.sound_fragment_container, fromFragment, toFragment);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setSelectTab(tab, true);
        switchMenu(prePosition, tab.getPosition());
        prePosition = tab.getPosition();
        if (tab.getPosition() == 0) {
            XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.volumeSettingFragmentPagePathDesc,
                    "com.xiaoma.setting.sound.ui.SoundEffectsSettingFragment",
                    EventConstants.PageDescribe.volumeSettingFragmentPagePathDesc);
        } else {
            XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc,
                    "com.xiaoma.setting.sound.ui.SoundEffectsSettingFragment",
                    EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc);
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
