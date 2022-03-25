package com.xiaoma.club.msg.chat.ui;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.controller.ClubViewPagerAdapter;
import com.xiaoma.club.common.controller.TabViewHolder;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.msg.conversation.repo.TopConversationRepo;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.ControllableViewPager;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupDetailsFragment extends SlideInFragment implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private TabLayout groupTab;
    private ControllableViewPager viewPager;
    private ClubViewPagerAdapter mAdapter;
    private int[] mTitles;
    private TabViewHolder mHolder;
    private TextView mTvIsTop;
    private RepoObserver mTopConversationObserver;
    private static final String KEY_HXID = "key_hxid";
    private NewGuide newGuide;

    public static GroupDetailsFragment newInstance(String hxId) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getSlideViewId() {
        return R.id.group_details_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_group_details, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                initView(view);
                try {
                    showGuideWindow(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void initView(View view) {
        groupTab = view.findViewById(R.id.group_details_tab);
        viewPager = view.findViewById(R.id.group_details_pager);
        view.findViewById(R.id.group_details_outside).setOnClickListener(this);
        mTvIsTop = view.findViewById(R.id.group_details_msg_top_btn);
        mTvIsTop.setOnClickListener(this);

        List<Fragment> fragments = getChildFragments();
        mTitles = new int[]{R.string.group_details_main, R.string.group_details_member, R.string.group_details_activity};
        mAdapter = new ClubViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setScrollable(true);
        viewPager.setAdapter(mAdapter);
        groupTab.setupWithViewPager(viewPager);
        setupTabView(groupTab);
        groupTab.addOnTabSelectedListener(this);
        ClubRepo.getInstance().getTopConversationRepo().addObserver(mTopConversationObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        bindConversationIsTop();
                    }
                });
            }
        });
        bindConversationIsTop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ClubRepo.getInstance().getTopConversationRepo().removeObserver(mTopConversationObserver);
        try {
            dismissGuideWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindConversationIsTop() {
        // 当前环信群组id即是会话id
        final String hxGroupId = getHxGroupId();
        if (TextUtils.isEmpty(hxGroupId))
            return;
        final TopConversationRepo repo = ClubRepo.getInstance().getTopConversationRepo();
        if (repo.isTop(hxGroupId)) {
            mTvIsTop.setText(getString(R.string.group_details_cancel_top));
            mTvIsTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repo.delete(hxGroupId);
                    showToast(getString(R.string.group_detail_toast_cancel_top));
                }
            });
        } else {
            mTvIsTop.setText(getString(R.string.group_details_msg_top));
            mTvIsTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repo.append(hxGroupId);
                    showToast(getString(R.string.group_detail_toast_set_top));
                }
            });
        }
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (tab.getCustomView() == null) {
                    tab.setCustomView(R.layout.item_tab_layout);
                }
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new TabViewHolder(view);
                    mHolder.tabTv.setText(getString(mTitles[i]));
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_details_outside:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }
    }

    private String getHxGroupId() {
        final Bundle args = getArguments();
        if (args == null)
            return null;
        return args.getString(KEY_HXID);
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
        mHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_normal);
        }
    }

    private List<Fragment> getChildFragments() {
        List<Fragment> fragments = new ArrayList<>();
        String hxGid = getArguments().getString(KEY_HXID);
        if (TextUtils.isEmpty(hxGid)) {
            return fragments;
        }
        fragments.add(GroupDetailsMainFragment.newInstance(hxGid));
        fragments.add(GroupDetailsMemberFragment.newInstance(hxGid));
        fragments.add(GroupDetailsActivityFragment.newInstance(hxGid));
        return fragments;
    }

    private void showGuideWindow(View view) {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
            return;
        try {
//            final View targetView = ((ViewGroup) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0)).getChildAt(1);
            final View targetView = getSlideNaviBar().getBackPreView();
            if (view == null) return;
            view.post(new Runnable() {
                @Override
                public void run() {
                    showWindow(targetView);
                }
            });
        } catch (Exception e) {
            Log.d("GroupDetailsFragment", "showGuideWindow: e=" + e.getMessage());
        }
    }

    private void showWindow(View view) {
        Rect viewRect = NewGuide.getViewRect(view);
        Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.CLUB_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_group_details_back)
                .setTargetView(view)
                .setHighLightRect(targetRect)
                .setNeedShake(true)
                .setNeedHande(true)
                .needMoveUpALittle(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setViewSkipId(R.id.tv_guide_skip)
                .setCallBack(new GuideCallBack() {
                    private boolean hasTriggered;

                    @Override
                    public void onHighLightClicked() {
                        if (hasTriggered) return;
                        hasTriggered = true;
                        dismissGuideWindow();
                        getActivity().onBackPressed();
                        if (GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
                            EventBus.getDefault().post("", "left_frg_show_third_guide");
                    }
                })
                .build();
        newGuide.showGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
