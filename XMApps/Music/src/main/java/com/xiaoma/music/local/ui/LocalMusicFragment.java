package com.xiaoma.music.local.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.adapter.ViewPagerAdapter;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.UsbMemroySignal;
import com.xiaoma.ui.view.ControllableViewPager;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/9 0009
 */
@PageDescComponent(EventConstants.PageDescribe.localFragment)
public class LocalMusicFragment extends VisibleFragment {
    private TabLayout mTabLayout;
    private ControllableViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private int[] mTitles;
    private ViewHolder mHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mViewPager != null) {
                int pageIndex = mViewPager.getCurrentItem();
                if (pageIndex == 1) {
                    //usb已接入，需告知usb页面进行扫描记忆文件
                    EventBus.getDefault().post(new UsbMemroySignal(), EventBusTags.SEARCH_USB_MEMORY_FILE);
                }
            }
        }
    }

    private void bindView(View view) {
        mTabLayout = view.findViewById(R.id.fragment_local_music_tab);
        mViewPager = view.findViewById(R.id.fragment_local_music_vp);
        mTitles = new int[]{R.string.string_bt, R.string.string_usb};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(BtFragment.newInstance());
        fragments.add(UsbFragment.newInstance());
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setScrollable(true);
        mViewPager.setAdapter(mAdapter);
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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                changeTab(tab, false);
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

    @Override
    @CallSuper
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.BLUETOOTH_FRAGMENT:
                mTabLayout.getTabAt(0).select();
                return true;
            case NodeConst.MUSIC.USB_FRAGMENT:
                mTabLayout.getTabAt(1).select();
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.LOCAL_FRAGMENT;
    }
}
