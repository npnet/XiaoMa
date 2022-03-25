package com.xiaoma.club.discovery.ui;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.controller.ClubViewPagerAdapter;
import com.xiaoma.club.common.controller.TabViewHolder;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.discovery.viewmodel.SearchVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.view.ControllableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/12 0017
 */

public class DiscoverySearchResultFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {


    TabLayout searchTab;
    ControllableViewPager viewPager;
    RelativeLayout emptyLl;
    private ClubViewPagerAdapter mAdapter;
    private List<String> mTitles;
    private SearchVM searchVM;
    private TabViewHolder mHolder;

    public static DiscoverySearchResultFragment newInstance(String keyWord) {
        DiscoverySearchResultFragment fragment = new DiscoverySearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ClubConstant.DISCOVERY_SEARCH_KEY, keyWord);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fmt_discovery_search_result, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }


    private void initView(View view) {
        String keyWord = getArguments().getString(ClubConstant.DISCOVERY_SEARCH_KEY);
        searchTab = view.findViewById(R.id.discovery_search_tab);
        TextView rightTips = view.findViewById(R.id.search_result_right_text);
        if (!TextUtils.isEmpty(keyWord)) {
            String text = getResources().getString(R.string.adout_search_result, keyWord);
            String result = text.split("\"")[1];
            int start = text.indexOf("\"");
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.search_key_word)), start + 1, start + 1 + result.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            rightTips.setText(spannableString);
        }
        viewPager = view.findViewById(R.id.discovery_search_pager);
        emptyLl = view.findViewById(R.id.discovery_result_empty);
    }

    private void initVM() {
        searchVM = ViewModelProviders.of(getActivity()).get(SearchVM.class);
        XmResource<List<Integer>> value = searchVM.getSearchCounts().getValue();
        if (value == null) {
            return;
        }
        value.handle(new OnCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> data) {
                if (data != null && !data.isEmpty()) {
                    if (data.size() != 2) {
                        return;
                    }
                    initFragments(data);
                }
            }
        });
    }

    private void initFragments(List<Integer> resultCounts) {
        if (resultCounts == null || resultCounts.isEmpty() || resultCounts.size() != 2) {
            return;
        }
        List<Fragment> fragments = new ArrayList<>();
        mTitles = new ArrayList<>();
        if (resultCounts.get(0) != 0) {
            fragments.add(new DiscoverySearchGroupFragment());
            mTitles.add(mContext.getString(R.string.group) + " " + resultCounts.get(0));
        }
        if (resultCounts.get(1) != 0) {
            fragments.add(new DiscoverySearchFriendFragment());
            mTitles.add(mContext.getString(R.string.friend) + " " + resultCounts.get(1));
        }
        mAdapter = new ClubViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setScrollable(true);
        viewPager.setAdapter(mAdapter);
        searchTab.setupWithViewPager(viewPager);
        setupTabView(searchTab);
        searchTab.addOnTabSelectedListener(this);
        if (fragments.isEmpty()) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.item_tab_layout);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new TabViewHolder(view);
                    mHolder.tabTv.setText(mTitles.get(i));
                }
            }
            if (i == 0) {
                ViewUtils.setTabTextStyle(getContext(), mHolder.tabTv, true);
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        changeTab(tab, true);
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        changeTab(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return;
        }
        mHolder = new TabViewHolder(customView);
        ViewUtils.setTabTextStyle(getContext(), mHolder.tabTv, isSelected);
    }

    @Override
    protected void showContentView() {
        viewPager.setVisibility(View.VISIBLE);
        emptyLl.setVisibility(View.GONE);
    }

    @Override
    protected void showEmptyView() {
        viewPager.setVisibility(View.GONE);
        emptyLl.setVisibility(View.VISIBLE);
    }
}
