package com.xiaoma.music.player.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.ViewPagerAdapter;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.mine.ui.CollectionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
@PageDescComponent(EventConstants.PageDescribe.albumSwitchFragment)
public class AlbumSwitchFragment extends BaseFragment implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private int[] mTitles;
    private ViewHolder mHolder;

    public static AlbumSwitchFragment newInstance() {
        return new AlbumSwitchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album_switch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
    }

    private void bindView(@NonNull View view) {
        mTabLayout = view.findViewById(R.id.fragment_switch_music_tab);
        mViewPager = view.findViewById(R.id.fragment_switch_music_vp);
        view.findViewById(R.id.back).setOnClickListener(this);
    }

    private void initView() {
        mTitles = new int[]{R.string.similar_playlist, R.string.mine_collector};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SimilarFragment.newInstance());
        fragments.add(CollectionFragment.newInstance(mContext.getResources()
                .getDimensionPixelOffset(R.dimen.size_fragment_album_switch_left), CollectionFragment.KEY_ENTRY_PLAY));
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
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
                    mHolder = new AlbumSwitchFragment.ViewHolder(view);
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
        mHolder = new AlbumSwitchFragment.ViewHolder(customView);
        mHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mHolder.tabTv.setTextAppearance(mContext,R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(mContext,R.style.text_view_normal);
        }
        return mHolder.tabTv.getText().toString();
    }

    @NormalOnClick(EventConstants.NormalClick.albumSwitchBackToPlayer)
    @ResId(R.id.back)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            backToOnlinePlayFragment();
        }
    }

    public void backToOnlinePlayFragment() {
        PlayerActivity parentActivity = getParentActivity();
        if (parentActivity != null) {
            parentActivity.switchFragment(OnlinePlayFragment.newInstance(), parentActivity.FRAGMENT_ONLINE_PLAY_TAG);
        }
    }

    private PlayerActivity getParentActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayerActivity) {
            return ((PlayerActivity) activity);
        }
        return null;
    }

    private class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

}
