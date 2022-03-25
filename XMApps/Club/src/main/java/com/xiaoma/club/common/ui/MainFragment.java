package com.xiaoma.club.common.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.constant.TabIndexMainFragment;
import com.xiaoma.club.common.constant.UnreadMsgEventTag;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.common.view.UserHeadView;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.club.contact.ui.ContactFragment;
import com.xiaoma.club.contact.ui.NewFriendDrawerFragment;
import com.xiaoma.club.discovery.ui.DiscoveryFragment;
import com.xiaoma.club.msg.conversation.ui.ConversationFragment;
import com.xiaoma.club.msg.conversation.viewmodel.ConversationMsgVM;
import com.xiaoma.club.msg.hyphenate.SimpleMessageListener;
import com.xiaoma.club.personal.ui.PersonalDrawerFragment;
import com.xiaoma.club.personal.ui.PersonalEditSignActivity;
import com.xiaoma.club.personal.ui.SettingDrawerFragment;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.utils.BackHandlerHelper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import static com.xiaoma.club.common.util.LogUtil.logI;

/**
 * Created by LKF on 2018/10/9 0009.
 */
@PageDescComponent(ClubEventConstants.PageDescribe.mainFragment)
public class MainFragment extends BaseFragment implements View.OnClickListener, BackHandlerHelper.FragmentBackHandler {
    private static final String TAG = MainFragment.class.getSimpleName();

    private static final Class CONTENT_FMT_CLZ[] = new Class[]{
            DiscoveryFragment.class,
            ConversationFragment.class,
            ContactFragment.class
    };

    private UserHeadView mIvHead;
    private TextView mTvDiscovery;
    private TextView mTvMsg, mTvMsgRedPot;
    private TextView mTvContact, mTvContactRedPot;

    private RelativeLayout mLayoutDiscovery, mLayoutTvMsg, mLayoutTvContact;

    private MainViewModel mViewModel;
    private ConversationMsgVM conversationMsgVM;
    private SimpleMessageListener mMessageListener;
    private NewGuide newGuide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FragmentActivity act = getActivity();
        if (act == null)
            return;
        mIvHead = view.findViewById(R.id.iv_head);
        mTvDiscovery = view.findViewById(R.id.tv_discovery);
        mTvMsg = view.findViewById(R.id.tv_msg);
        mTvMsgRedPot = view.findViewById(R.id.tv_msg_red_pot);
        mTvContact = view.findViewById(R.id.tv_contact);
        mTvContactRedPot = view.findViewById(R.id.tv_contact_red_pot);
        mLayoutDiscovery = view.findViewById(R.id.tv_discovery_rl);
        mLayoutTvContact = view.findViewById(R.id.tv_contact_rl);
        mLayoutTvMsg = view.findViewById(R.id.tv_msg_rl);

        view.findViewById(R.id.main_setting_btn).setOnClickListener(this);

        view.findViewById(R.id.tv_discovery_rl).setOnClickListener(this);
        view.findViewById(R.id.tv_msg_rl).setOnClickListener(this);
        view.findViewById(R.id.tv_contact_rl).setOnClickListener(this);

        final EMClient emClient = EMClient.getInstance();
        if (emClient.isLoggedInBefore()) {
            emClient.groupManager().loadAllGroups();
            emClient.chatManager().loadAllConversations();
        }
        emClient.chatManager().addMessageListener(mMessageListener = new SimpleMessageListener() {
            @Override
            public void onMessageRead(List<EMMessage> list) {
                updateUnreadMsgCount(true);
                if (conversationMsgVM != null) {
                    conversationMsgVM.refreshConversation();
                }
            }

            @Override
            public void onMessageReceived(List<EMMessage> list) {
                updateUnreadMsgCount(true);
                if (conversationMsgVM != null) {
                    conversationMsgVM.refreshConversation();
                }
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                updateUnreadMsgCount(true);
                if (conversationMsgVM != null) {
                    conversationMsgVM.refreshConversation();
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                super.onCmdMessageReceived(messages);
                final User currUser = UserUtil.getCurrentUser();
                if (currUser != null) {
                    mViewModel.addNewFriendMsg(String.valueOf(UserUtil.getCurrentUser().getId()));
                    XmTracker.getInstance().uploadEvent(-1, TrackerCountType.ADDFRIEND.getType());
                }
            }
        });
        mViewModel = ViewModelProviders.of(act).get(MainViewModel.class);
        conversationMsgVM = ViewModelProviders.of(act).get(ConversationMsgVM.class);
        mViewModel.getNewMessageCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer msgCount) {
                if (msgCount != null) {
                    ViewUtils.setMsgCounts(mTvMsgRedPot, msgCount);
                }
            }
        });
        mViewModel.getHaveNewFriend().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (mTvContactRedPot == null) {
                    return;
                }
                if (aBoolean != null && aBoolean) {
                    mTvContactRedPot.setVisibility(View.VISIBLE);
                } else {
                    mTvContactRedPot.setVisibility(View.GONE);
                }
            }
        });
        mViewModel.getTabSelectIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer tabIndex) {
                if (tabIndex == null)
                    return;
                showContentFragment(tabIndex);
            }
        });
        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null)
                    return;
                mIvHead.setUserHeadMsg(user);
            }
        });
        if (UserUtil.getCurrentUser() != null) {
            mViewModel.requestNewFriendList(String.valueOf(UserUtil.getCurrentUser().getId()), null);
        }
        mIvHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalDrawerFragment fragment = new PersonalDrawerFragment();
                fragment.setCallback(new PersonalDrawerFragment.Callback() {
                    @Override
                    public void onHeadImageClick() {
                        //XMToast.showToast(getContext(), R.string.click_head);
                    }

                    @Override
                    public void onEditSignClick(String oldSign) {
                        if (ClubNetWorkUtils.isConnected(getActivity())) {
                            PersonalEditSignActivity.start(getContext(), oldSign);
                        }
                    }

                    @Override
                    public void onOutSideClick() {
                        onBackPressed();
                    }
                });
                showOptFragment(fragment);
            }
        });
        EventBus.getDefault().register(this);
        // 默认选中Tab
        getUIHandler().getLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                if (!isDestroy()) {
                    final Integer tabIndex = mViewModel.getTabSelectIndex().getValue();
                    mViewModel.getTabSelectIndex().setValue(tabIndex != null ? tabIndex : 0);
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        EventBus.getDefault().unregister(this);
    }

    private void showContentFragment(final int tab) {
        final View[] tabs = new View[]{mLayoutDiscovery, mLayoutTvMsg, mLayoutTvContact};
        final FragmentManager mgr = getChildFragmentManager();
        final FragmentTransaction transaction = mgr.beginTransaction();
        for (int i = 0; i < tabs.length; i++) {
            final String clzName = CONTENT_FMT_CLZ[i].getName();
            Fragment fmt = mgr.findFragmentByTag(clzName);
            if (i == tab) {
                tabs[i].setSelected(true);
                if (fmt != null) {
                    if (fmt.isHidden()) {
                        transaction.show(fmt);
                    }
                } else {
                    fmt = Fragment.instantiate(getContext(), clzName);
                    addNewFriendClickListener(fmt);
                    transaction.add(R.id.fmt_child_content, fmt, clzName);
                }
            } else {
                tabs[i].setSelected(false);
                if (fmt != null && !fmt.isHidden()) {
                    transaction.hide(fmt);
                }
            }
        }
        transaction.commitNow();
        EventBus.getDefault().post(true, ClubConstant.EventBusConstant.CLEAN_SHAKEN);
        logI(TAG, "showContentFragment( clz: %s )", CONTENT_FMT_CLZ[tab].getSimpleName());
    }

    private void addNewFriendClickListener(Fragment existFmt) {
        if (existFmt instanceof ContactFragment) {
            ((ContactFragment) existFmt).setOnNewFriendClickListener(new ContactFragment.OnNewFriendClickListener() {
                @Override
                public void onNewFriendClick() {
                    if (ClubNetWorkUtils.isConnected(getActivity())) {
                        showOptFragment(new NewFriendDrawerFragment());
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 更新消息数
        updateUnreadMsgCount(true);
        // 更新用户信息
        if (mViewModel != null) {
            mViewModel.getUser().setValue(UserUtil.getCurrentUser());
        }
    }

    @Subscriber(tag = UnreadMsgEventTag.MAIN_UNREAD_EVENT_TAG)
    public void updateUnreadMsgCount(boolean update) {
        if (mViewModel != null) {
            mViewModel.updateUnreadMsgCount();
        }
    }

    @Override
    @NormalOnClick({ClubEventConstants.NormalClick.discover, ClubEventConstants.NormalClick.msg, ClubEventConstants.NormalClick.contact, ClubEventConstants.NormalClick.setting})
    @ResId({R.id.tv_discovery_rl, R.id.tv_msg_rl, R.id.tv_contact_rl, R.id.main_setting_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_discovery_rl:
                mViewModel.getTabSelectIndex().setValue(TabIndexMainFragment.TAB_DISCOVER);
                break;
            case R.id.tv_msg_rl:
                mViewModel.getTabSelectIndex().setValue(TabIndexMainFragment.TAB_MSG);
                dismissGuideWindow();
                showLastGuide();
                break;
            case R.id.tv_contact_rl:
                mViewModel.getTabSelectIndex().setValue(TabIndexMainFragment.TAB_CONTACT);
                break;
            case R.id.main_setting_btn:
                showOptFragment(new SettingDrawerFragment());
                break;
        }
    }

    @Subscriber(tag = TabIndexMainFragment.TAB_EVENT_TAG)
    public void selectTab(@TabIndexMainFragment int index) {
        if (mViewModel != null) {
            mViewModel.getTabSelectIndex().setValue(index);
        }
    }

    private void showOptFragment(final Fragment fragment) {
        logI(TAG, "showOptFragment( fragment: %s )", fragment.getClass().getSimpleName());
        if (isDestroy())
            return;
        EventBus.getDefault().post(true, ClubConstant.EventBusConstant.CLEAN_SHAKEN);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fmt_child_overlay, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        // 快速切换Content Tab时,概率性导致此Fragment不显示
        // 具体原因不知,调试时发现此Fragment的生命周期,以及当前Activity的View状态均正常,猜测是系统bug
        // 只要让当前Activity走一次onResume,就会正常显示,因此这里强行开启一个不可见并迅速finish掉的Activity
        startActivity(new Intent(getContext(), FlickerActivity.class));
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Subscriber(tag = "show_main_guide_message")
    public void showGuide(String s) {
        Rect viewRect = NewGuide.getViewRect(mTvMsg);
        Rect targetRect = new Rect(viewRect.left - 30, viewRect.top , viewRect.right + 30, viewRect.bottom );
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.CLUB_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_main_message)
                .setTargetView(mTvMsg)
                .setTargetRect(targetRect)
                .setViewSkipId(R.id.tv_guide_skip)
                .setNeedShake(true)
                .setNeedHande(true)
                .needMoveUpALittle(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setTargetViewTranslationX(0.05f)
                .build();
        newGuide.showGuide();
    }

    private void showLastGuide() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
            return;
        NewGuide.with(getActivity())
                .setLebal(GuideConstants.CLUB_SHOWED)
                .build()
                .showLastGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
