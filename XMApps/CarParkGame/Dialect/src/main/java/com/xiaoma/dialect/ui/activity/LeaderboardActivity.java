package com.xiaoma.dialect.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.dialect.R;
import com.xiaoma.dialect.adapter.LeaderboardAdapter;
import com.xiaoma.ui.view.XmScrollBar;

public class LeaderboardActivity extends BaseActivity {
    private RecyclerView mRvActivity;
    private XmScrollBar mActicityScriollBar;
    private LeaderboardAdapter mLeaderboardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity_todo);
//        initView();
//        initData();
    }

//    private void initData() {
//
//    }
//
//    private void initView() {
//        mRvActivity = findViewById(R.id.rv_leaderboard_list);
//        mActicityScriollBar = findViewById(R.id.scroll_bar);
//        mRvActivity.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.HORIZONTAL, false));
//        mRvActivity.setAdapter(mLeaderboardAdapter = new LeaderboardAdapter(this));
//        mActicityScriollBar.setRecyclerView(mRvActivity);
//        mActicityScriollBar.setVisibility(View.VISIBLE);
//    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }
}
