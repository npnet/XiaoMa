package com.xiaoma.club.msg.chat.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.chat.controller.GroupActivityAdapter;
import com.xiaoma.club.msg.chat.model.GroupActivityInfo;
import com.xiaoma.club.msg.chat.vm.GroupDetailsVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupDetailsActivityFragment extends BaseFragment {

    private RecyclerView groupDetailsActivityRv;
    private GroupDetailsVM groupDetailsVM;
    private GroupActivityAdapter adapter;
    private static final String KEY_HXID = "key_hxid";
    private String hxId;

    public static GroupDetailsActivityFragment newInstance(String hxId) {
        GroupDetailsActivityFragment fragment = new GroupDetailsActivityFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fmt_group_details_activity, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRv(view);
        initVM();
    }

    private void initVM() {
        hxId = getArguments().getString(KEY_HXID);
        if (TextUtils.isEmpty(hxId)) {
            return;
        }
        groupDetailsVM = ViewModelProviders.of(this).get(GroupDetailsVM.class);
        groupDetailsVM.getActivityInfos().observe(this, new Observer<List<GroupActivityInfo>>() {
            @Override
            public void onChanged(@Nullable List<GroupActivityInfo> groupActivityInfos) {
                if (groupActivityInfos != null) {
                    if (!groupActivityInfos.isEmpty()) {
                        showContentView();
                        adapter.refreshData(groupActivityInfos);
                    } else {
                        showEmptyView();
                    }
                }
            }
        });
        fetchData();
    }

    private void fetchData() {
        if (TextUtils.isEmpty(hxId)) {
            showEmptyView();
            return;
        }
        groupDetailsVM.requestGroupActivity(hxId, new GroupDetailCallBack() {
            @Override
            public void onSuccess(List<GroupActivityInfo> data) {
                if (data == null) {
                    showEmptyView();
                } else if (data != null && data.isEmpty()) {
                    showEmptyView();
                }
            }

            @Override
            public void onFailed() {
                showNoNetView();
            }
        });
    }

    @Override
    protected void noNetworkOnRetry() {
        if (!NetworkUtils.isConnected(getContext())) {
            showToastException(R.string.net_work_error);
            return;
        }
        fetchData();
    }

    public interface GroupDetailCallBack {
        void onSuccess(List<GroupActivityInfo> data);

        void onFailed();
    }

    private void initRv(View view) {
        groupDetailsActivityRv = view.findViewById(R.id.group_details_activity_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupDetailsActivityRv.setLayoutManager(layoutManager);
        List<GroupActivityInfo> groupActivityInfos = new ArrayList<>();
        groupDetailsActivityRv.setAdapter(adapter = new GroupActivityAdapter(getActivity(), groupActivityInfos));
    }

}
