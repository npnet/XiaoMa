package com.xiaoma.club.discovery.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.club.R;
import com.xiaoma.club.discovery.controller.DiscoveryFriendCardAdapter;
import com.xiaoma.club.discovery.viewmodel.SearchVM;
import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/26 0026
 */

public class DiscoverySearchFriendFragment extends LazyLoadFragment {

    RecyclerView contactFriendRv;
    private SearchVM searchVM;
    private DiscoveryFriendCardAdapter adapter;
    private XmScrollBar scrollBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fmt_contact_friend;
    }

    @Override
    protected void initView(View view) {
        contactFriendRv = view.findViewById(R.id.contact_friend_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactFriendRv.setLayoutManager(layoutManager);

        List<User> friendCardInfos = new ArrayList<>();
        contactFriendRv.setAdapter(adapter = new DiscoveryFriendCardAdapter(getActivity(), friendCardInfos));

        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactFriendRv);
    }

    @Override
    protected void loadData() {
        super.loadData();
        searchVM = ViewModelProviders.of(getActivity()).get(SearchVM.class);
        XmResource<List<User>> value = searchVM.getFriendResults().getValue();
        if (value == null) {
            return;
        }
        value.handle(new OnCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                if (data != null && !data.isEmpty()) {
                    adapter.refreshData(data);
                    checkEmptyView(data);
                }
            }
        });
    }

    private void checkEmptyView(List<User> friendCardInfos) {
        if (friendCardInfos.isEmpty()) {
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
}
