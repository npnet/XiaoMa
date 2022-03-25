package com.xiaoma.xting.search.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingNodeHelper;
import com.xiaoma.xting.common.adapter.ViewPagerAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.player.ui.FMPlayerActivity;
import com.xiaoma.xting.search.ui.fragment.SearchAlbumFragment;
import com.xiaoma.xting.search.ui.fragment.SearchRadioFragment;
import com.xiaoma.xting.search.ui.fragment.SearchTracksFragment;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.ACTIVITY_SEARCH_RESULT)
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {
    public static final String INTENT_KEY_WORD = "INTENT_KEY_WORD";
    private static final int[] TITLES = new int[]{R.string.album, R.string.program, R.string.radio};

    private TabLayout tabLayout;
    private ControllableViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ViewHolder holder;
    private String keyWord;
    private List<Fragment> fragments;

    public static void launch(Context context, String keyword) {
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
        checkNet();
    }

    public void setCurrentItem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public void handlePlay(final PlayerInfo toPlayInfo) {
        if (toPlayInfo.equals(PlayerInfoImpl.newSingleton().getPlayerInfo())) {
            FMPlayerActivity.launch(this);
        } else {
            toPlayInfo.setAction(PlayerAction.PLAY_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(toPlayInfo, new IFetchListener() {
                @Override
                public void onLoading() {
                    showProgressDialog(R.string.loading_data);
                }

                @Override
                public void onSuccess(Object t) {
                    dismissProgress();
                    FMPlayerActivity.launch(SearchResultActivity.this);
                }

                @Override
                public void onFail() {
                    dismissProgress();
                    XMToast.toastException(SearchResultActivity.this, R.string.error_by_data_source);
                }

                @Override
                public void onError(int code, String msg) {
                    dismissProgress();
                    XMToast.toastException(SearchResultActivity.this, R.string.net_work_error);
                }
            });
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.ACT_SEARCH_RESULT;
    }

    @Command("(打开(我的)?|我的)收藏")
    public void showMyCollect(String voiceCmd) {
        Log.d("VoiceTest", "{showMyCollect}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2MyCollect(this);
    }

    @Command("(打开)?(节目分类)(界面)?")
    public void showCategory(String voiceCmd) {
        Log.d("VoiceTest", "{showCategory}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2Category(this);
    }

    @Command("打开播放列表")
    public void popPlayList() {
        XtingNodeHelper.popoutPlayList(this);
    }

    @Command("(（这是|现在放的是|正在播放）什么歌)|(这歌叫什么(名字)?)|(这是谁(唱的)|(的歌))|(打开听歌识曲)")
    public void listenToRecognize() {
        XtingNodeHelper.openListenToRecognize(this);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.ACTION_BACK_TO_SEARCH})
    @ResId({R.id.tv_search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                SearchActivity.launcher(this);
                break;
            default:
                break;
        }
    }

    private void handleIntent() {
        keyWord = getIntent().getStringExtra(INTENT_KEY_WORD);
    }

    private void initView() {
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.fragment_search_music_vp);
        findViewById(R.id.tv_search).setOnClickListener(this);
        TextView tvResult = findViewById(R.id.tvResult);

        tvResult.setText(Html.fromHtml(getString(R.string.search_result_about, "<font color=\"#fbd3a4\">" + keyWord + "</font>")));
    }

    private boolean mIsManualScroll = false;

    public boolean isManualScroll() {
        return mIsManualScroll;
    }

    public void setManualScroll(boolean isManualScroll) {
        mIsManualScroll = isManualScroll;
    }

    private void initData() {
        List<Fragment> mFragments = getFragments(keyWord);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setScrollable(true);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        setupTabView(tabLayout);
        tabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

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

    private List<Fragment> getFragments(String from) {
        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(SearchAlbumFragment.newInstance(from));
            fragments.add(SearchTracksFragment.newInstance(from));
            fragments.add(SearchRadioFragment.newInstance(from));
        }
        return fragments;
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int index = 0; index < adapter.getCount(); index++) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    holder = new ViewHolder(view);
                    holder.tabTv.setText(TITLES[index]);
                }
            }
            if (index == 0) {
                holder.tabTv.setSelected(true);
                holder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = tabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            holder = new ViewHolder(customView);
            holder.tabTv.setTextAppearance(this, R.style.text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        holder = new ViewHolder(customView);
        holder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
        return holder.tabTv.getText().toString();
    }

    private class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

}
