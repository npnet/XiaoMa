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

import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.controller.ContactFriendCardAdapter;
import com.xiaoma.club.contact.viewmodel.ContactVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.BackHandlerHelper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/11 0011
 */

public class ContactFriendFragment extends BaseFragment implements BackHandlerHelper.FragmentBackHandler, ContactFriendCardAdapter.DeleteFriendListener {

    private ContactVM contactVM;
    RecyclerView contactFriendRv;
    private ContactFriendCardAdapter adapter;
    private XmScrollBar scrollBar;
    private RepoObserver mRepoObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateWrapView(inflater.inflate(R.layout.fmt_contact_friend, null));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView(view);
        initVM();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ClubRepo.getInstance().getContactUserRepo().removeObserver(mRepoObserver);
    }

    private void initView(View view) {
        contactFriendRv = view.findViewById(R.id.contact_friend_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactFriendRv.setLayoutManager(layoutManager);
        List<User> friendCardInfos = new ArrayList<>();
        contactFriendRv.setAdapter(adapter = new ContactFriendCardAdapter(getActivity(), friendCardInfos));
        adapter.setDeleteFriendListener(this);
        view.findViewById(R.id.contact_friend_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanShaken(true);
            }
        });
        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactFriendRv);
    }

    private void initVM() {
        contactVM = ViewModelProviders.of(getActivity()).get(ContactVM.class);
        contactVM.getFriendList().observe(this, new Observer<XmResource<List<User>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<User>> data) {
                data.handle(new OnCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        bindFriendsData(list);
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
        });
        ClubRepo.getInstance().getContactUserRepo().addObserver(mRepoObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                List<User> users = ClubRepo.getInstance().getContactUserRepo().getContactUsers();
                if (users != null && !users.isEmpty()) {
                    contactVM.setFriendCount(users.size());
                } else {
                    contactVM.setFriendCount(0);
                }
                bindFriendsData(users);
                LogUtil.logI("LKF_Room", "getContactUserRepo : size: " + (users != null ? users.size() : "null"));
            }
        });
    }

    private void bindFriendsData(final List<User> list) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (list != null && !list.isEmpty()) {
                    adapter.refreshData(list);
                    checkEmptyView(list);
                } else {
                    showEmptyView();
                }
            }
        });
    }

    @Override
    protected void noNetworkOnRetry() {
        if (contactVM != null) {
            contactVM.getContactFriend(String.valueOf(UserUtil.getCurrentUser().getId()));
        }
    }

    private void checkEmptyView(List<User> friendCardInfos) {
        if (friendCardInfos.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            showContentView();
        }
    }

    @Subscriber(tag = ClubConstant.EventBusConstant.CLEAN_SHAKEN)
    public void cleanShaken(boolean clean) {
        if (clean) {
            if (adapter != null) {
                adapter.cleanShaken();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        cleanShaken(true);
    }

    @Override
    public boolean onBackPressed() {
        if (adapter != null && adapter.isShakening()) {
            cleanShaken(true);
            return true;
        } else {
            return BackHandlerHelper.handleBackPress(this);
        }
    }

    @Override
    public void deleteFriend(String otherUserId) {
        if (TextUtils.isEmpty(otherUserId)) {
            XMToast.toastException(getActivity(), R.string.delete_friend_failed);
            return;
        }
        if (contactVM != null) {
            contactVM.deleteFriend(otherUserId);
        }
    }
}
