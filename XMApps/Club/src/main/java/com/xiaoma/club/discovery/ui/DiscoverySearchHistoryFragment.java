package com.xiaoma.club.discovery.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.discovery.model.SearchHistoryInfo;
import com.xiaoma.club.discovery.viewmodel.SearchHistoryVM;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.view.FlowLayout;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/25 0025
 */
@PageDescComponent(ClubEventConstants.PageDescribe.searchHistoryAFragment)
public class DiscoverySearchHistoryFragment extends LazyLoadFragment implements View.OnClickListener {
    private String TAG = DiscoverySearchHistoryFragment.class.getSimpleName();
    FlowLayout searchHistoryFl;
    Button cleanBtn;
    RelativeLayout historyRl;
    SearchHistoryVM historyVM;
    private OnClickHistoryItemListener listener;

    @Override
    protected int getLayoutResource() {
        return R.layout.fmt_discovery_history;
    }

    @Override
    protected void initView(View view) {
        searchHistoryFl = view.findViewById(R.id.flow_search_history);
        historyRl = view.findViewById(R.id.discovery_history_rl);
        cleanBtn = view.findViewById(R.id.discovery_clean_history);
        cleanBtn.setOnClickListener(this);
        cleanBtn.setEnabled(false);
        loadData();
    }

    @Override
    protected void loadData() {
        super.loadData();
        historyVM = ViewModelProviders.of(this).get(SearchHistoryVM.class);
        historyVM.getHistoryList().observe(this, new Observer<XmResource<List<SearchHistoryInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SearchHistoryInfo>> responses) {
                if (responses != null) {
                    responses.handle(new OnCallback<List<SearchHistoryInfo>>() {
                        @Override
                        public void onSuccess(List<SearchHistoryInfo> data) {
                            if (data == null || ListUtils.isEmpty(data)) {
                                historyRl.setVisibility(View.GONE);
                                return;
                            }
                            historyRl.setVisibility(View.VISIBLE);
                            List<String> list = new ArrayList<>();
                            for (SearchHistoryInfo bean : data) {
                                list.add(bean.getSearchContent());
                            }
                            updateFlowView(list, searchHistoryFl);
                        }
                    });
                } else {
                    historyRl.setVisibility(View.GONE);
                }
            }
        });
        historyVM.getSearchHistory(this);
    }

    private void updateFlowView(@Nullable List<String> list, FlowLayout flowLayout) {
        flowLayout.removeAllViews();
        if (ListUtils.isEmpty(list)) {
            return;
        }
        flowLayout.setViews(list, new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(String content) {
                if (!TextUtils.isEmpty(content)) {
                    historyVM.insertSearchHistory(DiscoverySearchHistoryFragment.this, content);
                    if (listener != null) {
                        listener.itemSearch(content);
                        XmAutoTracker.getInstance().onEvent(ClubEventConstants.NormalClick.serachHistoryTab,
                                content, TAG, ClubEventConstants.PageDescribe.searchHistoryAFragment);
                    }
                }
            }
        });
        cleanBtn.setEnabled(true);
    }

    public void insertHistory(String keyWord) {
        if (historyVM != null) {
            historyVM.insertSearchHistory(this, keyWord);
        }
    }

    @Override
    protected void cancelData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discovery_clean_history:
                historyVM.clearAllHistory();
                searchHistoryFl.removeAllViews();
                break;
        }
    }

    public void setOnClickHistoryItemListener(OnClickHistoryItemListener listener) {
        this.listener = listener;
    }

    public interface OnClickHistoryItemListener {
        void itemSearch(String keyWord);
    }
}
