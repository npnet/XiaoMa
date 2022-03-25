package com.xiaoma.personal.qrcode.ui;

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
import com.xiaoma.personal.R;
import com.xiaoma.personal.order.ui.view.DisableSwipeViewPager;
import com.xiaoma.personal.qrcode.ui.fragment.BinderHologramFragment;
import com.xiaoma.personal.qrcode.ui.fragment.NumberKeyFragment;
import com.xiaoma.personal.qrcode.ui.fragment.RemoteControllerFragment;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/19 0019 10:34
 *   desc:   二维码管理
 * </pre>
 */
public class QRCodeManageActivity extends BaseActivity {


    private TabLayout mTabLayout;
    private DisableSwipeViewPager mDisableSwipeViewPager;
    private TabItemViewHolder[] mItemViewHolder;
    private String[] tabTitle;

    private Fragment[] fragments = {NumberKeyFragment.newInstance(),
            RemoteControllerFragment.newInstance(),
            BinderHologramFragment.newInstance()};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_manage);
        initView();
        checkNet();
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_code_manage);
        mDisableSwipeViewPager = findViewById(R.id.view_pager_code_manage);

        tabTitle = new String[]{
                getString(R.string.number_key),
                getString(R.string.remote_controller),
                getString(R.string.hologram_text)};

        mDisableSwipeViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        mTabLayout.setupWithViewPager(mDisableSwipeViewPager);
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
