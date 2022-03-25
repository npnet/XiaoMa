package com.xiaoma.instruction.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.instruction.R;
import com.xiaoma.instruction.common.constant.InstructionConstants;
import com.xiaoma.instruction.mode.ManualItemBean;
import com.xiaoma.instruction.ui.fragment.GeneralFragment;
import com.xiaoma.instruction.utils.LanguageUtils;
import com.xiaoma.instruction.vm.ManualVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class ManualItemActivity extends BaseActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] titles;
    private Fragment[] fragments;
    private ViewHolder mViewHolder;
    private String mManualItemID;
    private ViewModel mManualVm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_item);
        bindView();
        initData();
    }

    private void initData() {
        mManualItemID = (String) getIntent().getSerializableExtra(InstructionConstants.MANUAL_ITEM_BEAN);
        mManualVm = ViewModelProviders.of(this).get(ManualVM.class);
        ((ManualVM) mManualVm).getManualItem().observe(this, new Observer<XmResource<List<ManualItemBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ManualItemBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<ManualItemBean>>() {
                    @Override
                    public void onSuccess(List<ManualItemBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            initView(data);
                        }
                    }
                });
            }
        });
        if (StringUtil.isNotEmpty(mManualItemID)) {
            ((ManualVM) mManualVm).fetchManualItem(mManualItemID);
        }
    }

    private void bindView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.vp);
    }

    private void initView(List<ManualItemBean> data) {

        fragments = new Fragment[data.size()];
        titles = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ManualItemBean itemBean = data.get(i);
            titles[i] = LanguageUtils.isChinese(this) ? itemBean.getMenuName() : itemBean.getMenuNameUs();
            fragments[i] = GeneralFragment.newInstance(LanguageUtils.isChinese(this) ? itemBean.getContent() : itemBean.getContentUs(), itemBean.getIcon());
        }

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTab(tab, true);
                mTabText = tab.getText();
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < fragments.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.manual_tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mViewHolder = new ViewHolder(view);
                    mViewHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mViewHolder.tabTv.setSelected(true);
                mViewHolder.tabTv.setTextAppearance(R.style.text_view_light_blue);
            }
        }
    }

    private void changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return;
        }
        mViewHolder = new ViewHolder(customView);
        mViewHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mViewHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
        } else {
            mViewHolder.tabTv.setTextAppearance(this, R.style.text_view_normal);
        }
    }


    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.manual_title_text);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            ((ManualVM) mManualVm).fetchManualItem(mManualItemID);
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void errorOnRetry() {
        showContentView();
        ((ManualVM) mManualVm).fetchManualItem(mManualItemID);
    }
}
