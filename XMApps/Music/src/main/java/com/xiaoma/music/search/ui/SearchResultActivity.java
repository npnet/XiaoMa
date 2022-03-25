package com.xiaoma.music.search.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.xiaoma.music.search.model.SearchTabGenerateData;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.progress.loading.ProgressDrawable;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.utils.log.KLog;

import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.searchResultActivity)
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    private static final int VIEW_PAGE_LIMIT = 2;
    public static final int MAX_LENGTH = 15;
    private ImageView mTvSearch;
    private TabLayout mTabLayout;
    private static final String INTENT_KEY_WORD = "INTENT_KEY_WORD";
    private String mKeyWord;
    private ControllableViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private int[] mTitles;
    private ViewHolder mHolder;
    private int lastIndex = 0;
    private RelativeLayout mProgress;

    public static void startActivity(Context context, String keyword) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra(INTENT_KEY_WORD, keyword);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        handleIntent();
        initView();
        initData();
    }

    private void handleIntent() {
        mKeyWord = getIntent().getStringExtra(INTENT_KEY_WORD);
    }

    private void initView() {
        mTvSearch = findViewById(R.id.tv_search);
        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.fragment_search_music_vp);
        mProgress = findViewById(R.id.rl_content_view);
        ProgressBar mProgressBar = findViewById(R.id.pb_progress);
        mTvSearch.setOnClickListener(this);
        ProgressDrawable drawable = new ProgressDrawable(this, mProgressBar);
        mProgressBar.setIndeterminateDrawable(drawable);
        TextView mTvShow = findViewById(R.id.tv_show);
        String text;
        if (mKeyWord.length() >= MAX_LENGTH) {
            text = getResources().getString(R.string.about_search_result, mKeyWord.substring(0, MAX_LENGTH) + "...");
        } else {
            text = getResources().getString(R.string.about_search_result, mKeyWord);
        }
        if (getResources().getConfiguration().getLocales().get(0).getCountry().equals("CN")){
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(XmSkinManager.getInstance().getColor(R.color.search_key_word)), 3, text.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvShow.setText(spannableString);
        }else {
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(XmSkinManager.getInstance().getColor(R.color.search_key_word)), 19, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvShow.setText(spannableString);
        }

    }

    private void initData() {
        mTitles = new int[]{R.string.music, R.string.singer, R.string.song_list};
        List<Fragment> mFragments = SearchTabGenerateData.getFragments(mKeyWord);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setOffscreenPageLimit(VIEW_PAGE_LIMIT);
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

    public void setCurrentItem(int index) {
        KLog.d("MrMine","setCurrentItem: "+index);
        try {
            if (mViewPager != null) {
                if (index == lastIndex + 1) {
                    mViewPager.setCurrentItem(index,false);
                    lastIndex = index;
                } else if (index == 0) {
                    mViewPager.setCurrentItem(index,false);
                    lastIndex = index;
                    dismissLoading();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading() {
       mProgress.setVisibility(View.VISIBLE);
    }

    public void dismissLoading() {
        mProgress.setVisibility(View.GONE);
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
                mHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
                mHolder.tabTv.setSelected(true);
            }
        }
    }

    @NormalOnClick(EventConstants.NormalClick.backToSearch)
    @ResId(R.id.tv_search)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_search:
                finish();
                break;
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

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }
}
