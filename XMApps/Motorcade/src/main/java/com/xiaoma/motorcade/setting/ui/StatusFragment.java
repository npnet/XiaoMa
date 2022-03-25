package com.xiaoma.motorcade.setting.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.common.view.RecyclerDividerItem;
import com.xiaoma.motorcade.setting.adapter.StatusAdapter;
import com.xiaoma.motorcade.setting.vm.StatusVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * 车主在线状态
 *
 * @author zs
 * @date 2019/1/21 0021.
 */
public class StatusFragment extends BaseFragment {

    private static final int SPAN_COUNT = 2;
    private static final String CAR_TEAM_ID_KEY = "carTeamId";
    private StatusVM statusVM;
    private RecyclerView mRecyclerView;
    private TextView onlineCountTv;
    private StatusAdapter mAdapter;
    private CallBack callBack;
    private View lastSelectedItem;
    private XmScrollBar scrollBar;

    public static StatusFragment newInstance(long carTeamId) {
        StatusFragment statusFragment = new StatusFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CAR_TEAM_ID_KEY, carTeamId);
        statusFragment.setArguments(bundle);
        return statusFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_status, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initData();
    }

    private void initView(View view) {
        onlineCountTv = view.findViewById(R.id.online_count_tv);
        mRecyclerView = view.findViewById(R.id.fragment_status_rv);
        scrollBar = view.findViewById(R.id.setting_scroll);
        initRv();
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lastSelectedItem != null) {
                    lastSelectedItem.setBackgroundResource(R.drawable.frame_normal);
                }
                view.setBackgroundResource(R.drawable.frame_pressed);
                lastSelectedItem = view;
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), getContext().getString(R.string.net_work));
                    return;
                }
                callBack.onItemClick((GroupMemberInfo) adapter.getItem(position));
            }
        });
    }

    private void initRv() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT, RecyclerView.VERTICAL, false));
        int offset = 24;
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(getContext(), RecyclerDividerItem.VERTICAL);
        dividerItem.setRect(0, 0, offset, offset);
        mRecyclerView.addItemDecoration(dividerItem);
        mAdapter = new StatusAdapter();
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);

    }

    private void initData() {
        //监听成员变化
        statusVM = ViewModelProviders.of(this).get(StatusVM.class);
        statusVM.getGroupMemberList().observe(this, new Observer<XmResource<List<GroupMemberInfo>>>() {

            @Override
            public void onChanged(@Nullable XmResource<List<GroupMemberInfo>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<GroupMemberInfo>>() {
                        @Override
                        public void onSuccess(List<GroupMemberInfo> data) {
                            showContentView();
                            mAdapter.setNewData(data);
                            int count = 0;
                            for (GroupMemberInfo groupMemberInfo : data) {
                                if (groupMemberInfo.getOnline() == 1) {
                                    count++;
                                }
                                if (UserUtil.getCurrentUser().getId() == groupMemberInfo.getId()) {
                                    callBack.getNickCompleted(groupMemberInfo.getNickName());
                                }
                            }
                            String size = String.valueOf(data.size());
                            onlineCountTv.setText(String.format(getString(R.string.current_online_number),
                                    String.valueOf(count), size));
                        }

                        @Override
                        public void onLoading() {
                            KLog.d("onLoading: ");
                            showProgressDialog(com.xiaoma.component.R.string.base_loading);
                        }

                        @Override
                        public void onFailure(String msg) {
                            showEmptyView();
                        }

                        @Override
                        public void onError(int code, String message) {
                            showNoNetView();
                        }
                    });
                }
            }
        });
        fetchMembers();
    }

    /**
     * 加载当前车队的成员信息
     */
    public void fetchMembers() {
        long carTeamId = getArguments().getLong(CAR_TEAM_ID_KEY);
        statusVM.fetchMember(String.valueOf(carTeamId));
    }

    public void setCallback(CallBack callback) {
        this.callBack = callback;
    }

    public interface CallBack {
        void getNickCompleted(String nickName);

        void onItemClick(GroupMemberInfo info);
    }

    @Override
    protected void noNetworkOnRetry() {
        if (!NetworkUtils.isConnected(getContext())) {
            showNoNetView();
        } else {
            fetchMembers();
        }
    }

}
