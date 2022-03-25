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

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.controller.GroupMemberAdapter;
import com.xiaoma.club.msg.chat.model.GroupMemberInfo;
import com.xiaoma.club.msg.chat.vm.GroupDetailsVM;
import com.xiaoma.club.msg.hyphenate.SimpleGroupListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupDetailsMemberFragment extends BaseFragment implements GroupMemberAdapter.MemberOperationListener {
    private RecyclerView groupDetailsMemberRv;
    private XmScrollBar memberScroll;
    private GroupDetailsVM groupDetailsVM;
    private GroupMemberAdapter adapter;
    private static final String KEY_HXID = "key_hxid";
    private String hxId;
    private SimpleGroupListener mGroupListener;

    public static GroupDetailsMemberFragment newInstance(String hxId) {
        GroupDetailsMemberFragment fragment = new GroupDetailsMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fmt_group_details_member, container, false);
        return super.onCreateWrapView(inflater.inflate(R.layout.fmt_group_details_member, container, false));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRv(view);
        initVM();
        EMClient.getInstance().groupManager().addGroupChangeListener(mGroupListener = new SimpleGroupListener() {
            @Override
            public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
                LogUtil.logI("club_mute", String.format("onMuteListAdded( groupId: %s, mutes: %s, muteExpire: %s )", groupId, mutes, muteExpire));
                if (Objects.equals(groupId, hxId))
                    fetchMuteData();
            }

            @Override
            public void onMuteListRemoved(String groupId, List<String> mutes) {
                LogUtil.logI("club_mute", String.format("onMuteListRemoved( groupId: %s, mutes: %s )", groupId, mutes));
                if (Objects.equals(groupId, hxId))
                    fetchMuteData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupListener);
    }

    private void initVM() {
        hxId = getArguments().getString(KEY_HXID);
        if (TextUtils.isEmpty(hxId)) {
            return;
        }
        groupDetailsVM = ViewModelProviders.of(this).get(GroupDetailsVM.class);
        groupDetailsVM.getMembers().observe(this, new Observer<List<GroupMemberInfo>>() {
            @Override
            public void onChanged(@Nullable List<GroupMemberInfo> memberInfos) {
                adapter.notifyDataSetChanged();
            }
        });
        showLoadingView();
        groupDetailsVM.requestGroupMember(hxId, new GroupDetailMemberCallBack() {
            @Override
            public void onSuccess(List<GroupMemberInfo> data) {
                if (data != null && !data.isEmpty()) {
                    adapter.refreshData(data);
                    checkEmptyView(data);
                }
            }

            @Override
            public void onFailed() {
                showNoNetView();
            }
        });
    }

    private void initRv(View view) {
        groupDetailsMemberRv = view.findViewById(R.id.group_details_member_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupDetailsMemberRv.setLayoutManager(layoutManager);
        List<GroupMemberInfo> groupMemberInfos = new ArrayList<>();
        groupDetailsMemberRv.setAdapter(adapter = new GroupMemberAdapter(getActivity(), groupMemberInfos));
        adapter.setMemberOperationListener(this);
        memberScroll = view.findViewById(R.id.club_member_scroll_bar);
        memberScroll.setRecyclerView(groupDetailsMemberRv);
    }


    private void checkEmptyView(List<GroupMemberInfo> memberInfos) {
        if (memberInfos.isEmpty()) {
            showEmptyView();
            memberScroll.setVisibility(View.GONE);
        } else {
            memberScroll.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    @Override
    public void singleChat(String hxId) {
        if (!TextUtils.isEmpty(hxId)) {
            ChatActivity.start(getActivity(), hxId, false);
        }
    }

    @Override
    public void forbidSpeak(final String userHxId) {
        if (TextUtils.isEmpty(hxId)) {
            XMToast.toastException(getActivity(), R.string.get_group_msg_failed);
            return;
        }
        if (groupDetailsVM != null) {
            groupDetailsVM.forbidSpeak(userHxId, hxId, new SimpleCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    showToast(getActivity().getString(R.string.forbid_success));
                    fetchMuteData();
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    XMToast.toastException(getActivity(), getActivity().getString(R.string.forbid_failed));
                }
            });
        }
    }

    @Override
    public void unForbidSpeak(String userHxId) {
        if (TextUtils.isEmpty(hxId)) {
            XMToast.toastException(getActivity(), R.string.get_group_msg_failed);
            return;
        }
        if (groupDetailsVM != null) {
            groupDetailsVM.unForbidSpeak(userHxId, hxId, new SimpleCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    showToast(getActivity().getString(R.string.unforbid_success));
                    fetchMuteData();
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    XMToast.toastException(getActivity(), getActivity().getString(R.string.unforbid_failed));
                }
            });
        }
    }

    @Override
    public void kickOut(final long userId, String name) {
        if (TextUtils.isEmpty(hxId)) {
            XMToast.toastException(getActivity(), R.string.get_group_msg_failed);
            return;
        }
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getActivity().getString(R.string.confirm_kick_out, name.length() > 6 ? name.substring(0, 5) + "..." : name))
                .setPositiveButton(getActivity().getString(R.string.group_tips_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (groupDetailsVM != null) {
                            groupDetailsVM.kickOut(userId, hxId, new SimpleCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    showToast(getActivity().getString(R.string.kick_out_success));
                                    if (adapter != null) {
                                        adapter.resetOperation();
                                    }
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    XMToast.toastException(getActivity(), getActivity().getString(R.string.kick_out_failed));
                                }
                            });
                        }
                    }
                })
                .show();
    }

    private void fetchMuteData() {
        if (isDestroy() || groupDetailsVM == null || adapter == null)
            return;
        groupDetailsVM.fetchMuteData(hxId, adapter.getMemberInfos());
    }

    public interface GroupDetailMemberCallBack {
        void onSuccess(List<GroupMemberInfo> data);

        void onFailed();
    }
}
