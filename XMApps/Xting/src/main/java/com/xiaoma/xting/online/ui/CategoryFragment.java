package com.xiaoma.xting.online.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.VisibilityFragment;

/**
 * @author KY
 * @date 2018/10/10
 */
//@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_NET_CATEGORY)
public class CategoryFragment extends VisibilityFragment {

    private TabLayout mTabLayout;
    private ViewPager vp;
    private String[] titles;
    private Fragment[] fragments = {CategoryAlbumFragment.newInstance(), CategoryRadioFragment.newInstance()};
    private ViewHolder mHolder;


    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
    }

    private void bindView(@NonNull View view) {
        mTabLayout = view.findViewById(R.id.tab_layout);
        vp = view.findViewById(R.id.vp);
    }

    private void initView() {
        titles = new String[]{ResUtils.getString(mContext, R.string.Album_category),
                ResUtils.getString(mContext, R.string.radio_category)};
        vp.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        mTabLayout.setupWithViewPager(vp);
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
    }

    private String changeTab(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ViewHolder(customView);
            mHolder.tabTv.setBackgroundResource(R.drawable.bg_item_select_child_empty);
            mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setBackgroundResource(R.drawable.bg_main_item_select);
        mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_light_blue);
        return mHolder.tabTv.getText().toString();
    }


    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < fragments.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.child_tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new CategoryFragment.ViewHolder(view);
                    mHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
                mHolder.tabTv.setBackgroundResource(R.drawable.bg_main_item_select);
            }
        }
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
