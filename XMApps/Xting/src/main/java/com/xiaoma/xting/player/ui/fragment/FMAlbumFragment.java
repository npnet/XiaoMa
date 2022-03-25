package com.xiaoma.xting.player.ui.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_ALBUM)
public class FMAlbumFragment extends BaseFragment implements View.OnClickListener {

    public static final String ARG_ID_SEARCH = "searchId";
    private TabLayout mTabLayout;
    private ViewPager mContentVP;
    private String[] mTabs;
    private List<BaseFragment> mFragments;
    private TextView mBackTV;
    private ImageView mShringIV;
    private FragmentPagerAdapter mAdapter;
    private ViewHolder mHolder;
    private ImageView mArrowUpIV;

    /**
     * @return
     */
    public static FMAlbumFragment newInstance() {
        FMAlbumFragment fragment = new FMAlbumFragment();
//        Bundle args = new Bundle();
//        args.putLong(ARG_ID_SEARCH, searchId);
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);
        initData();
        initEvent();
    }

    private void bindView(View view) {
        mBackTV = view.findViewById(R.id.tvBack);
        mTabLayout = view.findViewById(R.id.tab_container);
        mContentVP = view.findViewById(R.id.vpContent);
        mShringIV = view.findViewById(R.id.ivShrink);
        mArrowUpIV = view.findViewById(R.id.ivArrowUp);
    }

    private void initData() {
        mFragments = new ArrayList<>(2);
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
            localFMSetting();
        } else {
            onlineFMSetting(playerInfo);
        }

    }

    private void initEvent() {
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments != null ? mFragments.get(position) : null;
            }

            @Override
            public int getCount() {
                return mFragments != null ? mFragments.size() : 0;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTabs != null ? mTabs[position] : "";
            }
        };
        mContentVP.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mContentVP);
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
        mBackTV.setOnClickListener(this);
        mShringIV.setOnClickListener(this);
        mArrowUpIV.setOnClickListener(this);
    }

    private void localFMSetting() {
        mTabs = getResources().getStringArray(R.array.localFMTab);
        mFragments.add(LocalSwitchFragment.newInstance());
        mFragments.add(LocalCollectFragment.newInstance());
    }

    private void onlineFMSetting(PlayerInfo playerInfo) {
        mTabs = getResources().getStringArray(R.array.onlineFMTab);
        if (playerInfo.getType() != PlayerSourceType.KOALA) {
            mFragments.add(OnlineSimilarFragment.newInstance(playerInfo.getAlbumId()));
        } else {
            mTabs[0] = mTabs[1];
        }
        mFragments.add(OnlineStoreFragment.newInstance());
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ViewHolder(view);
                    mHolder.tabTv.setText(mTabs[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_light_blue);
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

    @Override
    @ResId({R.id.ivShrink, R.id.tvBack, R.id.ivArrowUp})
    @NormalOnClick({EventConstants.NormalClick.ACTION_BACK_PLAYER_DETAILS,
            EventConstants.NormalClick.ACTION_BACK_PLAYER_DETAILS,
            EventConstants.NormalClick.ACTION_BACK_PLAYER_DETAILS})
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.tvBack || vId == R.id.ivShrink || vId == R.id.ivArrowUp) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
