package com.xiaoma.club.share.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;

import com.xiaoma.club.R;
import com.xiaoma.component.base.BaseActivity;

/**
 * 简介:分享口令页面
 *
 * @author lingyan
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private static final Class FMT_CLZ_ARRAY[] = new Class[]{
            ShareToGroupFragment.class,
            ShareToFriendFragment.class
    };
    private Bundle bundle;
    private CheckBox group, friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_share_command);
        handleIntent();
        initView();
        showOptionFragment(0);
    }

    private void handleIntent() {
        bundle = getIntent().getExtras();
        if (bundle == null) {
            showToastException(R.string.share_failed);
            finish();
        }
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        group = findViewById(R.id.group_selected);
        group.setOnClickListener(this);
        friend = findViewById(R.id.friend_selected);
        friend.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    private void showOptionFragment(int tab) {
        group.setChecked(tab == 0);
        friend.setChecked(tab == 1);
        final FragmentManager mgr = getSupportFragmentManager();
        final FragmentTransaction transaction = mgr.beginTransaction();
        for (int i = 0; i < FMT_CLZ_ARRAY.length; i++) {
            final String clzName = FMT_CLZ_ARRAY[i].getName();
            Fragment fmt = mgr.findFragmentByTag(clzName);
            if (fmt != null) {
                fmt.setArguments(bundle);
            }
            if (i == tab) {
                if (fmt != null) {
                    transaction.show(fmt);
                } else {
                    fmt = Fragment.instantiate(this, clzName);
                    fmt.setArguments(bundle);
                    transaction.add(R.id.share_friends, fmt, clzName);
                }
            } else {
                if (fmt != null) {
                    transaction.hide(fmt);
                }
            }
        }
        transaction.commitNow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_selected:
                showOptionFragment(1);
                break;
            case R.id.group_selected:
                showOptionFragment(0);
                break;
            case R.id.back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
