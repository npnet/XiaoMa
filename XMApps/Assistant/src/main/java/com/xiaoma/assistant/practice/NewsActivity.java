package com.xiaoma.assistant.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      NewsActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:46
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.practice.adapter.NewsChannelAdapter;
import com.xiaoma.assistant.practice.vm.NewsChannelVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {
    private static final int SPAN_COUNT = 3;

    private RecyclerView rvChannel;
    private XmScrollBar scrollBar;
    private NewsChannelAdapter mChannelAdapter;
    private List<NewsChannelBean> channelBeans;
    private NewsChannelVM mChannelVM;
    private int mActionPosition;
    private int mRequestCode;
    private String mContent;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        bindView();
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }
    private void bindView() {
        rvChannel = findViewById(R.id.rv_news_channel);
        scrollBar = findViewById(R.id.scroll_bar);
    }

    private void initView() {
        rvChannel.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = 0;
        int vertical = this.getResources().getDimensionPixelOffset(R.dimen.size_news_channel_item_vertical_margin);
        int extra = 0;
        decor.setRect(horizontal, vertical, horizontal, vertical);
        decor.setExtraMargin(extra, SPAN_COUNT);
        rvChannel.addItemDecoration(decor);
    }

    private void initData() {
        channelBeans = new ArrayList<>();
        mChannelAdapter = new NewsChannelAdapter(R.layout.item_news_channel, channelBeans);
        mChannelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(VrPracticeConstants.ACTION_JSON, GsonHelper.toJson(channelBeans.get(position)));
                bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                LaunchUtils.launchAppWithData(NewsActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                finish();
            }
        });
        rvChannel.setAdapter(mChannelAdapter);
        scrollBar.setRecyclerView(rvChannel);

        mChannelVM = ViewModelProviders.of(this).get(NewsChannelVM.class);

        mChannelVM.getNewsChannles().observe(this, new Observer<XmResource<List<NewsChannelBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<NewsChannelBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<NewsChannelBean>>() {
                    @Override
                    public void onSuccess(List<NewsChannelBean> data) {
                        //返回的数据里的第一项加入定位城市
                        channelBeans.clear();
                        channelBeans.addAll(data);
                        mChannelAdapter.notifyDataSetChanged();
                        if (!TextUtils.isEmpty(mContent)) {
                            //如果content不为空
                            NewsChannelBean newsChannelBean = GsonHelper.fromJson(mContent, NewsChannelBean.class);
                            if (channelBeans.contains(newsChannelBean)) {
                                rvChannel.scrollToPosition(channelBeans.indexOf(newsChannelBean));
                            }
                        }
                    }
                });
            }
        });

        mChannelVM.fetchNewsChannel();

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
        mContent = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);

    }
}
