package com.xiaoma.club.contact.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.controller.ClubViewPagerAdapter;
import com.xiaoma.club.common.controller.TabViewHolder;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.contact.viewmodel.ContactVM;
import com.xiaoma.club.msg.chat.constant.ContactEventTag;
import com.xiaoma.club.msg.hyphenate.SimpleMessageListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.utils.BackHandlerHelper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: loren
 * Date: 2018/10/11 0011
 */
@PageDescComponent(ClubEventConstants.PageDescribe.contactFragment)
public class ContactFragment extends BaseFragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, BackHandlerHelper.FragmentBackHandler {

    TabLayout contactTab;
    ControllableViewPager viewPager;
    private ClubViewPagerAdapter mAdapter;
    private int[] mTitles;
    private TabViewHolder mHolder;
    private ContactVM contactVM;
    private TextView unReadNewFriend;
    private SimpleMessageListener messageListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fmt_contact, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
        initEMListener();
    }

    private void initView(View view) {
        contactTab = view.findViewById(R.id.contact_main_tab);
        viewPager = view.findViewById(R.id.contact_main_pager);
        unReadNewFriend = view.findViewById(R.id.contact_unread_new_friend_count);
        view.findViewById(R.id.contact_new_friend_btn).setOnClickListener(this);
        view.findViewById(R.id.contact_parent).setOnClickListener(this);

        List<Fragment> fragments = new ArrayList<>();
        mTitles = new int[]{R.string.group, R.string.friend};
        fragments.add(new ContactGroupFragment());
        fragments.add(new ContactFriendFragment());
        mAdapter = new ClubViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setScrollable(true);
        viewPager.setAdapter(mAdapter);
        contactTab.setupWithViewPager(viewPager);
        setupTabView(contactTab, null);
        contactTab.addOnTabSelectedListener(this);
    }

    private void initVM() {
        contactVM = ViewModelProviders.of(getActivity()).get(ContactVM.class);
        contactVM.getContactCounts().observe(this, new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(@Nullable Map<String, Integer> data) {
                if (data != null && !data.isEmpty() && data.size() == 2) {
                    setupTabView(contactTab, data);
                }
            }
        });
        contactVM.getNewFriendCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (unReadNewFriend != null) {
                    ViewUtils.setMsgCounts(unReadNewFriend, integer);
                }
            }
        });
        contactVM.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()));
    }

    private void initEMListener() {
        EMClient.getInstance().chatManager().addMessageListener(messageListener = new SimpleMessageListener() {
            @Override
            public void onMessageRead(List<EMMessage> list) {
            }

            @Override
            public void onMessageReceived(List<EMMessage> list) {
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                super.onCmdMessageReceived(messages);
                if (contactVM != null) {
                    contactVM.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()));
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    @Subscriber(tag = ContactEventTag.ON_CONTACT_ADDED)
    public void approveFriendRequest(String newFriendName) {
        updateFriendList();
    }

    @Subscriber(tag = ContactEventTag.ON_REQUEST_AGREE)
    private void onContactReqAgree(String contactHxAccount) {
        updateFriendList();
    }

    @Subscriber(tag = ContactEventTag.ON_CONTACT_DELETED)
    private void onContactDeleted(String contactHxAccount) {
        updateFriendList();
    }

    private void updateFriendList() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (contactVM != null) {
                    String curUid = String.valueOf(UserUtil.getCurrentUid());
                    contactVM.requestNewFriendList(curUid);
                    contactVM.getContactFriend(curUid);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contactVM != null) {
            contactVM.getAllContact(String.valueOf(UserUtil.getCurrentUser().getId()));
            //新朋友数量
            contactVM.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()));
        }
    }

    private void setupTabView(TabLayout tabLayout, Map<String, Integer> data) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (tab.getCustomView() == null) {
                    tab.setCustomView(R.layout.item_tab_layout);
                }
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new TabViewHolder(view);
                    mHolder.tabTv.setText(mContext.getString(mTitles[i]) + " " + 0);
                    if (data != null) {
                        if (contactVM != null) {
                            mHolder.tabTv.setText(mContext.getString(mTitles[i]) + " " + data.get(contactVM.COUNT_KEY[i]));
                        }
                    }
                }
            }
            ViewUtils.setTabTextStyle(getContext(), mHolder.tabTv, tab.isSelected());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_new_friend_btn:
                if (listener != null) {
                    listener.onNewFriendClick();
                }
                break;
            case R.id.contact_parent:
                EventBus.getDefault().post(true, ClubConstant.EventBusConstant.CLEAN_SHAKEN);
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        changeTab(tab, true);
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        changeTab(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return;
        }
        mHolder = new TabViewHolder(customView);
        ViewUtils.setTabTextStyle(getContext(), mHolder.tabTv, isSelected);
    }

    private OnNewFriendClickListener listener;

    public void setOnNewFriendClickListener(OnNewFriendClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    public interface OnNewFriendClickListener {
        void onNewFriendClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EventBus.getDefault().unregister(this);
    }
}
