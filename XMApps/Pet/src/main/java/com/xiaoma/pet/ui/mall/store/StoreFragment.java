package com.xiaoma.pet.ui.mall.store;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.ui.mall.PetSuppliesFragment;
import com.xiaoma.pet.ui.view.DisableSwipeViewPager;
import com.xiaoma.pet.ui.view.DrawStrokeTextView;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc: 宠物商店
 */
public class StoreFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private DisableSwipeViewPager mViewPager;
    private String[] tabTitle;
    private PetSuppliesFragment[] fragments = new PetSuppliesFragment[3];
    private TabItemViewHolder[] mItemViewHolder;


    //TODO 实际业务还需添加具体通信交互参数
    public static StoreFragment newFragmentInstance() {
        StoreFragment storeFragment = new StoreFragment();
        Bundle bundle = new Bundle();
        storeFragment.setArguments(bundle);
        return storeFragment;
    }


    public void refreshData() {
        for (PetSuppliesFragment fragment : fragments) {
            fragment.refreshData();
        }
    }


    public void forceRefreshData(String goodsType) {
        int typeIndex = Integer.parseInt(goodsType) - 1;
        fragments[typeIndex].forceRefreshData();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View contentView) {
        mTabLayout = contentView.findViewById(R.id.tab_layout_pet);
        mViewPager = contentView.findViewById(R.id.view_pager_pet);

        tabTitle = new String[]{getResources().getString(R.string.pet_food),
                getResources().getString(R.string.pet_decoration),
                getResources().getString(R.string.pet_cosplay)
        };
        fragments[0] = StoreGoodsFragment.newInstance(GoodsType.FOOD);
        fragments[1] = StoreGoodsFragment.newInstance(GoodsType.DECORATOR);
        fragments[2] = StoreGoodsFragment.newInstance(GoodsType.COS_PLAY);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
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
                return tabTitle[position];
            }
        });
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabItemView();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabStatus(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setupTabItemView() {
        if (mTabLayout == null) {
            KLog.e("mTabLayout is null");
            return;
        }

        int tabCount = fragments.length;
        mItemViewHolder = new TabItemViewHolder[tabCount];
        //初始化tab
        for (int index = 0; index < tabCount; index++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(index);
            if (tab != null) {
                tab.setCustomView(R.layout.mall_tab_item);
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    mItemViewHolder[index] = new TabItemViewHolder(tabView);
                    mItemViewHolder[index].tabItem.setText(tabTitle[index]);
                    mItemViewHolder[index].tabItem.updateStyle(false);
                    mItemViewHolder[index].tabItem.setBackgroundResource(R.drawable.mall_tab_item_unselect);
                }
            }

            if (index == 0) {
                mItemViewHolder[index].tabItem.setSelected(true);
                mItemViewHolder[index].tabItem.updateStyle(true);
                mItemViewHolder[index].tabItem.setBackgroundResource(R.drawable.mall_tab_item_select);
            }
        }
    }


    private String changeTabStatus(TabLayout.Tab tab) {
        int tabCount = mTabLayout.getTabCount();
        int selected = 0;
        for (int index = 0; index < tabCount; index++) {
            if (index == tab.getPosition()) {
                selected = index;
                mItemViewHolder[index].tabItem.updateStyle(true);
                mItemViewHolder[index].tabItem.setBackgroundResource(R.drawable.mall_tab_item_select);
            } else {
                mItemViewHolder[index].tabItem.updateStyle(false);
                mItemViewHolder[index].tabItem.setBackgroundResource(R.drawable.mall_tab_item_unselect);
            }
        }
        return mItemViewHolder[selected].tabItem.getText();
    }


    static class TabItemViewHolder {
        DrawStrokeTextView tabItem;

        TabItemViewHolder(View view) {
            tabItem = view.findViewById(R.id.tv_mall_tab_item);
        }
    }
}
