package com.xiaoma.service.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.utils.ResUtils;

/**
 * Created by Thomas on 2018/11/13 0013
 * 我的订单--all order list
 */
@PageDescComponent(EventConstants.PageDescribe.orderListActivityPagePathDesc)
public class OrderListActivity extends BaseActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] titles;
    private Fragment[] fragments = new Fragment[4];
    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        bindView();
        initView();
    }

    private void bindView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.vp);
    }

    private void initView() {
        titles = new String[]{ResUtils.getString(this, R.string.order_received),
                ResUtils.getString(this, R.string.completed),
                ResUtils.getString(this, R.string.cancel),
                ResUtils.getString(this, R.string.all_order)};
        fragments[0] = OrderFragment.newInstance(OrderFragment.TYPE_COMMITTED);
        fragments[1] = OrderFragment.newInstance(OrderFragment.TYPE_CONFIRMED);
        fragments[2] = OrderFragment.newInstance(OrderFragment.TYPE_CANCELED);
        fragments[3] = OrderFragment.newInstance(OrderFragment.TYPE_ALL);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
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
                changeTab(tab, true);
                mTabText = tab.getText();
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < fragments.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.order_tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mViewHolder = new ViewHolder(view);
                    mViewHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mViewHolder.tabTv.setSelected(true);
                mViewHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
            }
        }
    }

    private void changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return;
        }
        mViewHolder = new ViewHolder(customView);
        mViewHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mViewHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
        } else {
            mViewHolder.tabTv.setTextAppearance(this, R.style.text_view_normal);
        }
    }


    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.order_title_text);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
