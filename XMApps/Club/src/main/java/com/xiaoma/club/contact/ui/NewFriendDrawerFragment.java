package com.xiaoma.club.contact.ui;


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

import com.hyphenate.chat.EMMessage;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.club.contact.controller.NewFriendAdapter;
import com.xiaoma.club.contact.model.NewFriendInfo;
import com.xiaoma.club.msg.chat.constant.ContactEventTag;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/13 0017
 */

public class NewFriendDrawerFragment extends SlideInFragment implements View.OnClickListener, NewFriendAdapter.ApproveClickListener {

    private final String TAG = getClass().getSimpleName();
    private RecyclerView newFriendRv;
    private XmScrollBar newFriendScroll;
    private MainViewModel newFriendVM;
    private NewFriendAdapter adapter;
    private StateView newFriendStateView;

    @Override
    protected int getSlideViewId() {
        return R.id.newfriend_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_new_friend_drawer, (ViewGroup) childView, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();

    }

    private void initView(View view) {
        newFriendStateView = view.findViewById(R.id.new_friend_stateview);

        view.findViewById(R.id.new_friend_outside).setOnClickListener(this);
        newFriendRv = view.findViewById(R.id.new_friend_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newFriendRv.setLayoutManager(layoutManager);
        List<NewFriendInfo> newFriendInfos = new ArrayList<>();
        newFriendRv.setAdapter(adapter = new NewFriendAdapter(getActivity(), newFriendInfos));
        adapter.setApproveClickListener(this);

        newFriendScroll = view.findViewById(R.id.club_newfriend_scroll_bar);
        newFriendScroll.setRecyclerView(newFriendRv);
    }

    private void initVM() {
        newFriendVM = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        newFriendVM.getNewFriendlist().observe(this, new Observer<List<NewFriendInfo>>() {
            @Override
            public void onChanged(@Nullable List<NewFriendInfo> data) {
                if (data != null && !data.isEmpty()) {
                    adapter.refreshData(data);
                    newFriendStateView.showContent();
                } else {
                    newFriendStateView.showEmpty();

                }
            }
        });
        newFriendStateView.showLoading();
        List<NewFriendInfo> infos = newFriendVM.getNewFriendlist().getValue();
        if (infos != null && !infos.isEmpty()) {
            adapter.refreshData(infos);
            newFriendStateView.showContent();
        } else {
            newFriendVM.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()), callBack);
        }
    }

    NewFriendDrawerCallBack callBack = new NewFriendDrawerCallBack() {
        @Override
        public void onSuccess(List<NewFriendInfo> data) {
            if (data == null) {
                newFriendStateView.showEmpty();
            } else if (data != null && data.isEmpty()) {
                newFriendStateView.showEmpty();
            }
        }

        @Override
        public void onFailed() {
            newFriendStateView.showNoNetwork();
        }
    };

    public interface NewFriendDrawerCallBack {
        void onSuccess(List<NewFriendInfo> data);

        void onFailed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_friend_outside:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }
    }

    @Override
    public void approve(final NewFriendInfo friendInfo) {
        if (friendInfo != null && friendInfo.getId() != 0 && newFriendVM != null) {
            if (!ClubNetWorkUtils.isConnected(getActivity())) {
                return;
            }
            newFriendVM.approveNewFriend(friendInfo.getId(), new SimpleCallback<Void>() {
                @Override
                public void onSuccess(Void rlt) {
                    hasBeenFriend(friendInfo, true);
                }

                @Override
                public void onError(int code, String msg) {
                    if (!TextUtils.isEmpty(msg)) {
                        showToastException(msg);
                    }
                    if (3004 == code) {
                        hasBeenFriend(friendInfo, false);
                    }
                    LogUtil.logE(TAG, "onError -> code: %s, msg: %s", code, msg);
                }

                private void hasBeenFriend(NewFriendInfo friendInfo, boolean sendMsg) {
                    // 发送一条通知到会话中
                    if (sendMsg) {
                        final EMMessage message = EMMessage.createTxtSendMessage(
                                newFriendVM.getApplication().getString(R.string.chat_be_friend_notify),
                                friendInfo.getFromHxAccount());
                        message.setAttribute(MessageKey.MSG_TYPE, MessageType.SYSTEM_NOTIFY);
                        IMUtils.sendMessage(message);
                    }
                    //主动刷新通讯录列表及新朋友按钮红点，规避有时候环信没有回调
                    EventBus.getDefault().post(friendInfo.getFromHxAccount(), ContactEventTag.ON_CONTACT_ADDED);
                    // 刷新主页通讯录上红点
                    final User currUser = UserUtil.getCurrentUser();
                    if (currUser != null) {
                        newFriendVM.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()), callBack);
                    }
                    XMToast.toastSuccess(getContext(), R.string.tips_has_been_friend);
                    XmTracker.getInstance().uploadEvent(-1, TrackerCountType.ADDFRIEND.getType());
                    friendInfo.setMessageType(NewFriendInfo.IS_APPROVED);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void singleChat(String hxId) {
        if (!TextUtils.isEmpty(hxId)) {
            ChatActivity.start(getActivity(), hxId, false);
        }
    }
}
