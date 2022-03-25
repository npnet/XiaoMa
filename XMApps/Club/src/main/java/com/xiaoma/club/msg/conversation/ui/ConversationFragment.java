package com.xiaoma.club.msg.conversation.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.xiaoma.club.R;
import com.xiaoma.club.common.controller.ClubViewPagerAdapter;
import com.xiaoma.club.common.controller.TabViewHolder;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.club.msg.conversation.viewmodel.ConversationMsgVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.view.ControllableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */
@PageDescComponent(ClubEventConstants.PageDescribe.msgFragment)
public class ConversationFragment extends BaseFragment implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private static final float ONE_KEY_READ_DISABLED_ALPHA = .3f;
    TabLayout conversationTab;
    ControllableViewPager viewPager;
    private ClubViewPagerAdapter mAdapter;
    private int[] mTitles;
    private TabViewHolder mHolder;
    private ConversationMsgVM conversationMsgVM;
    private TextView oneKeyRead;
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            try {
                TabLayout.Tab tab = conversationTab.getTabAt(position);
                if (tab != null && tab.getCustomView() != null) {
                    View unreadView = tab.getCustomView().findViewById(R.id.unread_msg);
                    if (View.VISIBLE == unreadView.getVisibility()) {
                        oneKeyRead.setEnabled(true);
                        oneKeyRead.setAlpha(1f);
                    } else {
                        oneKeyRead.setEnabled(false);
                        oneKeyRead.setAlpha(ONE_KEY_READ_DISABLED_ALPHA);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fmt_conversation, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }

    private void initView(View view) {
        conversationTab = view.findViewById(R.id.conversation_main_tab);
        viewPager = view.findViewById(R.id.conversation_main_pager);
        oneKeyRead = view.findViewById(R.id.conversation_onekey_read);
        oneKeyRead.setOnClickListener(this);

        List<Fragment> fragments = new ArrayList<>();
        mTitles = new int[]{R.string.group, R.string.friend};
        fragments.add(new ConversationGroupFragment());
        fragments.add(new ConversationFriendFragment());
        mAdapter = new ClubViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setScrollable(true);
        viewPager.setAdapter(mAdapter);
        conversationTab.setupWithViewPager(viewPager);
        setupTabView(conversationTab, null);
        conversationTab.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        conversationTab.removeOnTabSelectedListener(this);
        viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    private void initVM() {
        conversationMsgVM = ViewModelProviders.of(getActivity()).get(ConversationMsgVM.class);
        conversationMsgVM.getMsgCounts().observe(this, new Observer<XmResource<List<Integer>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<Integer>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<Integer>>() {
                        @Override
                        public void onSuccess(List<Integer> data) {
                            if (data != null && !data.isEmpty() && data.size() == 2) {
                                setupTabView(conversationTab, data);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && conversationMsgVM != null) {
            conversationMsgVM.refreshConversation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (conversationMsgVM != null) {
            conversationMsgVM.refreshConversation();
        }
    }

    private void setupTabView(TabLayout tabLayout, List<Integer> data) {
        int curPageMsgCount = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (tab.getCustomView() == null) {
                    tab.setCustomView(R.layout.item_tab_layout);
                }
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new TabViewHolder(view);
                    if (data == null) {
                        mHolder.tabTv.setText(mContext.getString(mTitles[i]));
                    } else {
                        ViewUtils.setMsgCounts(mHolder.msgTv, data.get(i));
                        if (i == viewPager.getCurrentItem()) {
                            curPageMsgCount = data.get(i);
                        }
                    }
                }
            }
            if (data == null) {
                //首次create才需要
                if (i == 0) {
                    mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_light_blue);
                    mHolder.tabTv.setSelected(true);
                } else {
                    mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_normal);
                    mHolder.tabTv.setSelected(false);
                }
            }
        }
        if (curPageMsgCount > 0) {
            oneKeyRead.setEnabled(true);
            oneKeyRead.setAlpha(1f);
        } else {
            oneKeyRead.setEnabled(false);
            oneKeyRead.setAlpha(ONE_KEY_READ_DISABLED_ALPHA);
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
        mHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(getContext(), R.style.text_view_normal);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conversation_onekey_read:
                if (conversationMsgVM != null) {
                    markConversation(conversationTab.getSelectedTabPosition());
                }
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).updateUnreadMsgCount();
                break;
        }
    }

    private void markConversation(int tabIndex) {
        switch (tabIndex) {
            case 0:
                List<EMConversation> groupMsgs = conversationMsgVM.getGroupMsgs().getValue().data;
                if (groupMsgs != null && !groupMsgs.isEmpty()) {
                    for (EMConversation con : groupMsgs) {
                        if (con != null && con.getUnreadMsgCount() != 0) {
                            con.markAllMessagesAsRead();
                        }
                    }
                }
                conversationMsgVM.refreshConversation();
                conversationMsgVM.requestGroupMsgs();
                showToast(getString(R.string.mark_group_msg_read));
                break;
            case 1:
                List<EMConversation> friendMsgs = conversationMsgVM.getFriendMsgs().getValue().data;
                if (friendMsgs != null && !friendMsgs.isEmpty()) {
                    for (EMConversation con : friendMsgs) {
                        if (con != null && con.getUnreadMsgCount() != 0) {
                            con.markAllMessagesAsRead();
                        }
                    }
                }
                conversationMsgVM.refreshConversation();
                conversationMsgVM.requestFriendMsgs();
                showToast(getString(R.string.mark_friend_msg_read));
                break;
        }
    }
}
