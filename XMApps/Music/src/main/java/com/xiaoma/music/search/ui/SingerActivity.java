package com.xiaoma.music.search.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.ViewPagerAdapter;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.search.model.SearchTabGenerateData;
import com.xiaoma.music.search.vm.SearchResultVM;
import com.xiaoma.ui.view.ControllableViewPager;

import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.searchSingerActivity)
public class SingerActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_SINGER_IMAGE = "singer_image";
    public static final String KEY_SINGER_NAME = "singer_name";
    private TabLayout singerTab;
    private ControllableViewPager singerViewPager;
    private String mSingerImage;
    private String mSingerName;
    private ImageView singerIcon;
    private TextView singerName;
    private int[] titles;
    private ViewPagerAdapter adapter;
    private ViewHolder mHolder;
    private SearchResultVM mSearchResultVM;


    public static void startActivity(Context context, XMArtistInfo singerBean) {
        Intent intent = new Intent(context, SingerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SINGER_IMAGE, singerBean.getSDKBean().getImageUrl());
        bundle.putString(KEY_SINGER_NAME, singerBean.getSDKBean().getName());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        handleIntent();
        initView();
        initEvent();
    }

    private void initEvent() {
        if (TextUtils.isEmpty(mSingerName)) {
            return;
        }
        singerName.setText(mSingerName);
        titles = new int[]{R.string.music, R.string.song_list};
        List<Fragment> singer = SearchTabGenerateData.getSingerFragments(mSingerName);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), singer);
        singerViewPager.setScrollable(true);
        singerViewPager.setAdapter(adapter);
        singerTab.setupWithViewPager(singerViewPager);
        setupTabView(singerTab);
        singerTab.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            String mString;
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mString, "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mString = changeTab(tab, true);
                singerViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                changeTab(tab, false);
            }
        });
    }

    private void handleIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSingerImage = extras.getString(KEY_SINGER_IMAGE);
            mSingerName = extras.getString(KEY_SINGER_NAME);
        }
    }

    private void initView() {
        singerTab = findViewById(R.id.fragment_singer_tab);
        singerViewPager = findViewById(R.id.fragment_singer_vp);
        singerIcon = findViewById(R.id.img_singer);
        singerIcon.setOnClickListener(this);
        singerName = findViewById(R.id.tv_singer_name);

    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < adapter.getCount(); i++) {
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
                mHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
                mHolder.tabTv.setSelected(true);
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
            mHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(this, R.style.text_view_normal);
        }
        return mHolder.tabTv.getText().toString();
    }

    @NormalOnClick(EventConstants.NormalClick.backToSearchResult)
    @ResId(R.id.img_singer)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_singer:
                finish();
                break;
        }
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
