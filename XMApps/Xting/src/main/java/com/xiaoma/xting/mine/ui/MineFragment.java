package com.xiaoma.xting.mine.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.ViewPagerAdapter;

import java.util.Arrays;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_MINE)
public class MineFragment extends VisibilityFragment implements AbsMineTabFragment.IOnClearAllVisibleListener {

    private TabLayout mTabLayout;
    private ControllableViewPager mViewPager;
    private String[] titles;
    private ViewHolder mHolder;
    private ViewPagerAdapter mAdapter;
    private TextView tvClearAll;
    private Fragment[] mFragments = {CollectionFragment.newInstance(), HistoryFragment.newInstance()};

    private boolean[] mShowClearAll = new boolean[2];

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
    }

    private void bindView(View view) {
        mTabLayout = view.findViewById(R.id.tl_my);
        mViewPager = view.findViewById(R.id.vp_my);
        tvClearAll = view.findViewById(R.id.tv_clear_all);
    }

    private void initView() {
        if (mFragments[1] instanceof HistoryFragment) {
            ((CollectionFragment) mFragments[0]).setOnClearAllVisibleListener(this);
            ((HistoryFragment) mFragments[1]).setOnClearAllVisibleListener(this);
        }

        titles = new String[]{getString(R.string.my_collection), getString(R.string.play_history)};
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), Arrays.asList(mFragments));
        mViewPager.setScrollable(true);
        mViewPager.setAdapter(mAdapter);
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
                mTabText = changeTab(tab);
            }
        });
        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.ACTION_CLEAR_PLAY_HISTORY)
            public void onClick(View v) {
                Fragment fragment = mFragments[mViewPager.getCurrentItem()];
                ((AbsMineTabFragment) fragment).clearAllItem();
            }
        });
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ViewHolder(view);
                    mHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            //增加 收藏页的一键删除 1
//            tvClearAll.setVisibility(View.VISIBLE);
            tvClearAll.setVisibility(mShowClearAll[0] ? View.VISIBLE : View.INVISIBLE);
            tvClearAll.setText(R.string.clear_all_collect);
        } else {
            tvClearAll.setVisibility(mShowClearAll[1] ? View.VISIBLE : View.INVISIBLE);
            tvClearAll.setText(R.string.clear_all_history);
        }
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ViewHolder(customView);
            mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_light_blue);
        return mHolder.tabTv.getText().toString();
    }

    @Override
    public void clearAllVisible(boolean isVisible) {
        if (mViewPager.getCurrentItem() == 0) {
            mShowClearAll[0] = isVisible;
            tvClearAll.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        } else {
            mShowClearAll[1] = isVisible;
            tvClearAll.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.Xting.FGT_MY_COLLECT:
                mViewPager.setCurrentItem(0);
                return true;
            case NodeConst.Xting.FGT_MY_HISTORY:
                mViewPager.setCurrentItem(1);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_MY;
    }
}
