package com.xiaoma.club.discovery.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.view.ClubSearchVoiceView;
import com.xiaoma.club.discovery.viewmodel.SearchVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;

import java.util.List;

import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.searchNow;

/**
 * Author: loren
 * Date: 2018/10/12 0017
 */
@PageDescComponent(ClubEventConstants.PageDescribe.searchActivity)
public class DiscoverySearchActivity extends BaseActivity implements View.OnClickListener, DiscoverySearchHistoryFragment.OnClickHistoryItemListener, ClubSearchVoiceView.OnOpenAnimationFinishListener {

    private ClubSearchVoiceView searchVoiceView;
    FragmentTransaction transaction;
    DiscoverySearchHistoryFragment historyFragment;
    private SearchVM searchVM;
    private String keyWord = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_discovery_search);
        initView();
        addFragment(historyFragment = new DiscoverySearchHistoryFragment());
        historyFragment.setOnClickHistoryItemListener(this);
        initVM();
    }

    private void initView() {
        searchVoiceView = findViewById(R.id.discovery_search_voice_view);
        searchVoiceView.setOnSearchClickListener(this);
        searchVoiceView.setOnOpenAnimationFinishListener(this);
        searchVoiceView.setHint(getString(R.string.search_hint));
    }

    private void addFragment(BaseFragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_bottom, R.anim.slide_out_bottom,
                R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                .replace(R.id.discovery_search_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


    private void initVM() {
        searchVM = ViewModelProviders.of(this).get(SearchVM.class);
        searchVM.getSearchCounts().observe(this, new Observer<XmResource<List<Integer>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<Integer>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<Integer>>() {
                        @Override
                        public void onSuccess(List<Integer> data) {
                            if (data != null && !TextUtils.isEmpty(keyWord)) {
                                searchVoiceView.closeWidthAnimation();
                                addFragment(DiscoverySearchResultFragment.newInstance(keyWord));
                            } else {
                                XMToast.toastException(DiscoverySearchActivity.this, R.string.search_failed);
                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            XMToast.toastException(DiscoverySearchActivity.this, R.string.search_failed);
                        }

                        @Override
                        public void onError(int code, String message) {
                            XMToast.toastException(DiscoverySearchActivity.this, R.string.search_failed);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchVoiceView != null) {
            searchVoiceView.initVoiceEngine(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchVoiceView != null) {
            searchVoiceView.release(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            searchVoiceView.setNormal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (searchVoiceView != null) {
                searchVoiceView.registerIatListener();
            }
        } else {
            if (searchVoiceView != null) {
                searchVoiceView.unregisterIatListener();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchVoiceView != null) {
            searchVoiceView.release(true);
        }
    }

    private void startSearch(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            showToast(R.string.search_content_not_empty);
            searchVoiceView.setEtText("");
            return;
        }
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(DiscoverySearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ClubNetWorkUtils.isConnected(this)) {
            return;
        }
        searchVoiceView.setEtText("");
        this.keyWord = keyWord;
        if (historyFragment != null) {
            historyFragment.insertHistory(keyWord);
        }
        searchVM.searchAll(keyWord);
        XmAutoTracker.getInstance().onEvent(searchNow, keyWord,
                "DiscoverySearchActivity", ClubEventConstants.PageDescribe.searchActivity);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.discovery_search_container);
        if (fragment instanceof DiscoverySearchResultFragment) {
            if (searchVoiceView != null) {
                searchVoiceView.openWidthAnimation();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discovery_search_now_btn:
                startSearch(searchVoiceView.getText().trim());
                break;
        }
    }

    @Override
    public void itemSearch(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            showToast(R.string.search_content_not_empty);
            return;
        }
        searchVoiceView.setEtText(keyWord);
        startSearch(keyWord);
    }

    @Override
    public void onAnimationStart() {
        if (historyFragment == null) {
            historyFragment = new DiscoverySearchHistoryFragment();
        }
        addFragment(historyFragment);
    }

    @Override
    public void onAnimationFinish() {

    }
}
