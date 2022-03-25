package com.xiaoma.xting.online.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;

/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_NET)
public class IndexFragment extends VisibilityFragment {

    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private IndexFragment.ViewHolder mHolder;
    private ImageButton identifyMusic;
    private String[] titles;
    private Fragment[] fragments = {RecommendFragment.newInstance(), RankFragment.newInstance(), CategoryFragment.newInstance()};
    //    private GuideUtils guideUtils;
    private NewGuide newGuide;

    public static IndexFragment newInstance() {
        return new IndexFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_net_fm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.vp);
        mTabLayout = view.findViewById(R.id.tab_layout);
        identifyMusic = view.findViewById(R.id.identify_music);
        titles = new String[]{ResUtils.getString(mContext, R.string.recommend),
                ResUtils.getString(mContext, R.string.leader_board),
                ResUtils.getString(mContext, R.string.category)};

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }
        });
        mTabLayout.setupWithViewPager(viewPager);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabText = changeTab(tab);
                // 隐藏第一个guideview 显示第二个
                showSecondGuideView(tab);
            }
        });

        identifyMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMToast.showToast(mContext, R.string.identify_songs);
            }
        });
        showFirstGuideView();
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.Xting.FGT_NET_RECOMMENDED:
                viewPager.setCurrentItem(0);
                return true;
            case NodeConst.Xting.FGT_NET_RANK:
                viewPager.setCurrentItem(1);
                return true;
            case NodeConst.Xting.FGT_NET_CATEGORY:
                viewPager.setCurrentItem(2);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_NET_INDEX;
    }

    private void showFirstGuideView() {
        // 二次引导获取文件中保存的状态
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, true))
            return;
        pauseMusic();
        mTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                final TabLayout.Tab tab = mTabLayout.getTabAt(1);
                View targetView = tab.getCustomView();
                Rect viewRect = NewGuide.getViewRect(targetView);
                Rect targetRect = new Rect(viewRect.left - 25, viewRect.top, viewRect.right - 25, viewRect.bottom);
                Spanned guideDesc = Html.fromHtml(getString(R.string.guide_index_play_list_description));
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.XTING_SHOWED)
                        .setTargetView(targetView)
                        .setTargetRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_view_index_one)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setGuideTextId(R.id.tv_guide_desc)
                        .setGuideTextDesc(guideDesc)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.XTING_SHOWED);
            }
        }, 200);
    }

    private boolean checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            return true;
        }
        GuideDataHelper.finishGuideData(GuideConstants.XTING_SHOWED);
        // TODO: 2019/4/23 0023 弹框告知 新手引导结束
        return false;
    }

    private void pauseMusic() {
        if (OnlineFMPlayerFactory.getPlayer().isPlaying())
            OnlineFMPlayerFactory.getPlayer().pause();
    }


    private void showSecondGuideView(TabLayout.Tab tab) {
        dismissGuideWindow();
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, false))
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
                .setLebal(GuideConstants.XTING_SHOWED)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setGuideLayoutId(R.layout.guide_view_index_two)
                .setNeedHande(true)
                .setNeedShake(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
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

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < fragments.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new IndexFragment.ViewHolder(view);
                    mHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
            } else if (i == 2) {
                mHolder.tabTv.setGravity(Gravity.CENTER);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ViewHolder(customView);
            mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_light_blue);
        return mHolder.tabTv.getText().toString();
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
