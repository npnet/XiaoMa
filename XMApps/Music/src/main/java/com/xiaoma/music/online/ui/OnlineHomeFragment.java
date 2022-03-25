package com.xiaoma.music.online.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.ViewPagerAdapter;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
@PageDescComponent(EventConstants.PageDescribe.onlineHomeFragment)
public class OnlineHomeFragment extends BaseFragment {

    public static final int VIEW_PAGE_LIMIT = 2;
    private TabLayout mTabLayout;
    private ControllableViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private int[] mTitles;
    private ViewHolder mHolder;
    private NewGuide newGuide;

    public static OnlineHomeFragment newInstance() {
        return new OnlineHomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
        showFirstGuideView();
    }

    private void bindView(@NonNull View view) {
        mTabLayout = view.findViewById(R.id.fragment_online_music_tab);
        mViewPager = view.findViewById(R.id.fragment_online_music_vp);
    }

    private void initEvent() {
        mTitles = new int[]{R.string.recommend, R.string.ranking, R.string.category};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(RecommendFragment.newInstance());
        fragments.add(BillBoardFragment.newInstance());
        fragments.add(CategoryFragment.newInstance());
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(VIEW_PAGE_LIMIT);
        mViewPager.setScrollable(true);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(0);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            String mString;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mString, "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mString = changeTab(tab, true);
                mViewPager.setCurrentItem(tab.getPosition());
                // 隐藏第一个guideview 显示第二个
                showSecondGuideView(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                changeTab(tab, false);
            }
        });
    }

    private void showFirstGuideView() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, true))
            return;
        mTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                final TabLayout.Tab tab = mTabLayout.getTabAt(1);
                View targetView = tab.getCustomView();
//                Spanned guideDesc = Html.fromHtml("<font color='#8a919d'>点击顶部导航</font><font color='#fbd3a4'>切换推荐<br/>、排行榜、音乐分类</font><font color='#8a919d'>等<br/>各模块。<br/><br/>点击播放列表内的资源<br/>即可跳转并播放该音乐<br/>专辑。</font>");
                Spanned guideDesc = Html.fromHtml(getString(R.string.guide_play_list_home_description));
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.MUSIC_SHOWED)
                        .setTargetView(targetView)
                        .setTargetRect(NewGuide.getViewRect(targetView))
                        .setGuideLayoutId(R.layout.guide_view_home_one)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setGuideTextId(R.id.tv_guide_desc)
                        .setGuideTextDesc(guideDesc)
                        .setTargetViewTranslationX(0.05f)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.MUSIC_SHOWED);
            }
        }, 200);
    }

    /**
     * 判断当前是否应该展示新手引导
     * 应该展示哪张新手引导
     *
     * @param tab
     */
    private void showSecondGuideView(TabLayout.Tab tab) {
        dismissGuideWindow();
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
            return;
        View targetView;
        TabLayout.Tab tab1 = mTabLayout.getTabAt(1);
        TabLayout.Tab tab2 = mTabLayout.getTabAt(2);
        if (tab == tab1)
            targetView = tab2.getCustomView();
        else {
            dismissGuideWindow();
            return;
        }
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.MUSIC_SHOWED)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setGuideLayoutId(R.layout.guide_view_home_two)
                .setNeedHande(true)
                .setNeedShake(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setTargetViewTranslationX(0.05f)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.showGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    private boolean checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            return true;
        }
        GuideDataHelper.finishGuideData(GuideConstants.MUSIC_SHOWED);
        // TODO: 2019/4/23 0023 弹框告知 新手引导结束
        return false;
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ViewHolder(view);
                    mHolder.tabTv.setText(mTitles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_normal);
        }
        return mHolder.tabTv.getText().toString();
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }


}
