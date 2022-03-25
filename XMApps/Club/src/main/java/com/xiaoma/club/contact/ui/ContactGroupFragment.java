package com.xiaoma.club.contact.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.constant.TabIndexMainFragment;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.controller.ContactGroupCardAdapter;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.contact.viewmodel.ContactVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
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

public class ContactGroupFragment extends BaseFragment implements BackHandlerHelper.FragmentBackHandler, ContactGroupCardAdapter.QuitGroupListener, View.OnClickListener {

    private ContactVM contactVM;
    RecyclerView contactGroupRv;
    private ContactGroupCardAdapter adapter;
    private RelativeLayout content;
    private View empty, error;
    private Button emptyBtn;
    private XmScrollBar scrollBar;
    private RepoObserver mRepoObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateWrapView(inflater.inflate(R.layout.fmt_contact_group, null));
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
        ClubRepo.getInstance().getContactGroupsRepo().removeObserver(mRepoObserver);
    }

    private void initView(View view) {
        content = view.findViewById(R.id.contact_group_parent);
        empty = view.findViewById(R.id.contact_group_empty);
        error = view.findViewById(R.id.contact_group_error);
        emptyBtn = view.findViewById(R.id.contact_to_find_group);
        emptyBtn.setOnClickListener(this);
        view.findViewById(R.id.tv_retry).setOnClickListener(this);
        contactGroupRv = view.findViewById(R.id.contact_group_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactGroupRv.setLayoutManager(layoutManager);
        List<GroupCardInfo> groupCardInfos = new ArrayList<>();
        contactGroupRv.setItemAnimator(null);
        adapter = new ContactGroupCardAdapter(view.getContext(), ImageLoader.with(this), groupCardInfos);
        adapter.setHasStableIds(true);
        contactGroupRv.setAdapter(adapter);
        adapter.setQuitGroupListener(this);
        content.setOnClickListener(this);
        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactGroupRv);
    }

    private void initVM() {
        contactVM = ViewModelProviders.of(getActivity()).get(ContactVM.class);
        ClubRepo.getInstance().getContactGroupsRepo().addObserver(mRepoObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                List<GroupCardInfo> groupCardInfos = ClubRepo.getInstance().getContactGroupsRepo().getContactGroups();
                if (groupCardInfos != null && !groupCardInfos.isEmpty()) {
                    contactVM.setGroupCount(groupCardInfos.size());
                } else {
                    contactVM.setGroupCount(0);
                }
                bindGroupList(groupCardInfos);
                LogUtil.logI("LKF_Room", "getContactGroupsRepo # onChanged : size: " + (groupCardInfos != null ? groupCardInfos.size() : "null"));
            }
        });
        contactVM.getGroupList().observe(this, new Observer<XmResource<List<GroupCardInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<GroupCardInfo>> data) {
                data.handle(new OnCallback<List<GroupCardInfo>>() {
                    @Override
                    public void onSuccess(List<GroupCardInfo> list) {
                        bindGroupList(list);
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
    }

    private void bindGroupList(final List<GroupCardInfo> list) {
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

    private void checkEmptyView(List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            showContentView();
        }
    }

    @Override
    protected void showEmptyView() {
        content.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
    }

    @Override
    protected void showContentView() {
        content.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    protected void showNoNetView() {
        content.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
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
    public void quitGroup(long groupId, String hxId) {
        if (groupId != 0 && contactVM != null) {
            contactVM.quitGroup(groupId, hxId);
        } else {
            XMToast.toastException(getActivity(), R.string.quit_group_failed);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_to_find_group:
                EventBus.getDefault().post(TabIndexMainFragment.TAB_DISCOVER, TabIndexMainFragment.TAB_EVENT_TAG);
                break;
            case R.id.contact_group_parent:
                cleanShaken(true);
                break;
            case R.id.tv_retry:
                if (contactVM != null) {
                    contactVM.getContactGroup(String.valueOf(UserUtil.getCurrentUser().getId()), null);
                }
                break;
        }
    }
}
