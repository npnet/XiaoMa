package com.xiaoma.club.discovery.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.club.R;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.view.ClubConfirmDialog;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.controller.DiscoveryGroupCardAdapter;
import com.xiaoma.club.discovery.viewmodel.SearchVM;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/26 0026
 */

public class DiscoverySearchGroupFragment extends LazyLoadFragment implements DiscoveryGroupCardAdapter.OnProgressStateListener {

    RecyclerView contactGroupRv;
    private SearchVM searchVM;
    private DiscoveryGroupCardAdapter adapter;
    private XmScrollBar scrollBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fmt_contact_group;
    }

    @Override
    protected void initView(View view) {
        contactGroupRv = view.findViewById(R.id.contact_group_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactGroupRv.setLayoutManager(layoutManager);
        List<GroupCardInfo> groupCardInfos = new ArrayList<>();
        contactGroupRv.setAdapter(adapter = new DiscoveryGroupCardAdapter(ImageLoader.with(this), groupCardInfos));
        adapter.setOnProgressStateListener(this);
        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactGroupRv);
    }

    @Override
    protected void loadData() {
        super.loadData();
        searchVM = ViewModelProviders.of(getActivity()).get(SearchVM.class);
        XmResource<List<GroupCardInfo>> value = searchVM.getGroupResults().getValue();
        if (value == null) {
            return;
        }
        value.handle(new OnCallback<List<GroupCardInfo>>() {
            @Override
            public void onSuccess(List<GroupCardInfo> data) {
                if (data != null && !data.isEmpty()) {
                    adapter.refreshData(data);
                    checkEmptyView(data);
                }
            }
        });
    }

    private void checkEmptyView(List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    @Override
    protected void cancelData() {

    }

    @Override
    public void onProgressState(boolean isShow) {
        if (isShow) {
            showProgressDialog(R.string.loading_add_group);
        } else {
            dismissProgress();
        }
    }

    @Override
    public void showKickOutDialog(final String groupId) {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getContext().getString(R.string.group_tips_removed))
                .setPositiveButton(getContext().getString(R.string.group_tips_chat_to_admin), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        final GroupCardInfo groupInfo = ClubRepo.getInstance().getGroupRepo().get(groupId);
                        if (groupInfo != null) {
                            final String adminHxId = groupInfo.getAdminHxId();
                            ChatActivity.start(getActivity(), adminHxId, false);
                        }
                    }
                })
                .setNegativeButton(getContext().getString(R.string.group_tips_quit), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
