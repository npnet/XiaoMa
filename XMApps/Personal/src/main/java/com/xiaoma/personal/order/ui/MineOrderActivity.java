package com.xiaoma.personal.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.ui.fragment.ClosedFragment;
import com.xiaoma.personal.order.ui.fragment.CompletedFragment;
import com.xiaoma.personal.order.ui.fragment.WaitPayFragment;
import com.xiaoma.personal.order.ui.fragment.WaitUseFragment;
import com.xiaoma.personal.order.ui.view.DisableSwipeViewPager;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 8:44
 *       desc：我的订单
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.mineOrderActivity)
public class MineOrderActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private DisableSwipeViewPager mViewPager;

    private TabItemViewHolder[] mItemViewHolder;
    private String[] tabTitle;
    private Fragment[] fragments = {WaitPayFragment.newInstance(),
            WaitUseFragment.newInstance(),
            CompletedFragment.newInstance(),
            ClosedFragment.newInstance()};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order);
        initView();
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_mine_order);
        mViewPager = findViewById(R.id.view_pager_mine_order);

        tabTitle = new String[]{ResUtils.getString(this, R.string.order_wait_pay),
                ResUtils.getString(this, R.string.order_wait_use),
                ResUtils.getString(this, R.string.order_completed),
                ResUtils.getString(this, R.string.order_closed)};

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments != null ? fragments[position] : null;
            }

            @Override
            public int getCount() {
                return fragments != null ? fragments.length : 0;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitle[position];
            }
        });
//        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabItemView();

        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            private CharSequence tempText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(tempText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tempText = changeTabStatus(tab);
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
                tab.setCustomView(R.layout.item_tab);
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    mItemViewHolder[index] = new TabItemViewHolder(tabView);
                    mItemViewHolder[index].tabItem.setText(tabTitle[index]);
                }
            }

            if (index == 0) {
                mItemViewHolder[index].tabItem.setSelected(true);
                mItemViewHolder[index].tabItem.setTextAppearance(R.style.order_tab_item_selected);
            }
        }
    }


    private String changeTabStatus(TabLayout.Tab tab) {
        int tabCount = mTabLayout.getTabCount();
        int selected = 0;
        for (int index = 0; index < tabCount; index++) {
            if (index == tab.getPosition()) {
                selected = index;
                mItemViewHolder[index].tabItem.setTextAppearance(R.style.order_tab_item_selected);
            } else {
                mItemViewHolder[index].tabItem.setTextAppearance(R.style.order_tab_item_normal);
            }
        }
        return mItemViewHolder[selected].tabItem.getText().toString();
    }


    static class TabItemViewHolder {
        TextView tabItem;

        TabItemViewHolder(View view) {
            tabItem = view.findViewById(R.id.tv_tab_item);
        }
    }

}
