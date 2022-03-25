package com.xiaoma.club.msg.conversation.ui;


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

import com.hyphenate.EMConversationListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.club.msg.chat.constant.ChatMsgEventTag;
import com.xiaoma.club.msg.conversation.controller.ConversationFriendAdapter;
import com.xiaoma.club.msg.conversation.model.ConversationAdapterCallBack;
import com.xiaoma.club.msg.conversation.viewmodel.ConversationMsgVM;
import com.xiaoma.club.msg.hyphenate.SimpleMessageListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */
@PageDescComponent(ClubEventConstants.PageDescribe.friendConversationFragment)
public class ConversationFriendFragment extends BaseFragment implements ConversationAdapterCallBack {

    RecyclerView conversationFriendRv;
    XmScrollBar friendScroll;
    private ConversationMsgVM conversationMsgVM;
    private ConversationFriendAdapter adapter;
    private EMMessageListener mMessageListener;
    private EMConversationListener conversationListener;
    private RepoObserver mTopConversationObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateWrapView(inflater.inflate(R.layout.fmt_conversation_friend, null));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener = new SimpleMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                requestFriendMsgs();
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                requestFriendMsgs();
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                requestFriendMsgs();
            }
        });
        EMClient.getInstance().chatManager().addConversationListener(conversationListener = new EMConversationListener() {
            @Override
            public void onCoversationUpdate() {
                requestFriendMsgs();
            }
        });
        initView(view);
        initVM();
        // 监听会话置顶变化
        ClubRepo.getInstance().getTopConversationRepo().addObserver(mTopConversationObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                requestFriendMsgs();
            }
        });
    }

    private void initVM() {
        conversationMsgVM = ViewModelProviders.of(getActivity()).get(ConversationMsgVM.class);
        conversationMsgVM.getFriendMsgs().observe(this, new Observer<XmResource<List<EMConversation>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<EMConversation>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<EMConversation>>() {
                        @Override
                        public void onSuccess(List<EMConversation> data) {
                            if (data != null && !data.isEmpty()) {
                                showContentView();
                                adapter.refreshData(data);
                                checkEmptyView(data);
                            } else {
                                showEmptyView();
                            }
                        }
                    });
                } else {
                    showEmptyView();
                }
            }
        });
    }

    private void checkEmptyView(List<EMConversation> conversations) {
        if (conversations.isEmpty()) {
            showEmptyView();
            friendScroll.setVisibility(View.GONE);
        } else {
            showContentView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        EMClient.getInstance().chatManager().removeConversationListener(conversationListener);
        ClubRepo.getInstance().getTopConversationRepo().removeObserver(mTopConversationObserver);
    }

    @Subscriber(tag = ChatMsgEventTag.SEND_MESSAGE)
    private void onMsgSend(final EMMessage message) {
        requestFriendMsgs();
    }

    @Subscriber(tag = ChatMsgEventTag.RECALL_MESSAGE)
    private void onMsgRecall(final EMMessage message) {
        requestFriendMsgs();
    }

    @Subscriber(tag = ChatMsgEventTag.SAVE_MESSAGE)
    private void onMsgSaved(final EMMessage message) {
        requestFriendMsgs();
    }

    private void initView(View view) {
        conversationFriendRv = view.findViewById(R.id.conversation_friend_rv);
        conversationFriendRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationFriendRv.setLayoutManager(layoutManager);
        List<EMConversation> friendMsgInfos = new ArrayList<>();
        conversationFriendRv.setAdapter(adapter = new ConversationFriendAdapter(ImageLoader.with(this), friendMsgInfos));
        adapter.setOnDeleteConversationCallBack(this);
        friendScroll = view.findViewById(R.id.club_msg_friend_scroll_bar);
        friendScroll.setRecyclerView(conversationFriendRv);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次显示都刷新会话,保证数据同步
        if (isVisibleToUser) {
            requestFriendMsgs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestFriendMsgs();
    }

    private void requestFriendMsgs() {
        if (conversationMsgVM != null) {
            conversationMsgVM.requestFriendMsgs();
        }
    }

    private void refreshConversation() {
        if (conversationMsgVM != null) {
            conversationMsgVM.refreshConversation();
        }
    }

    @Override
    public void delete(final String chatId,String friendName) {
        if (TextUtils.isEmpty(chatId)) {
            XMToast.toastException(getContext(), getString(R.string.delete_conversation_failed));
            return;
        }
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getContext().getString(R.string.confirm_delete_conversation))
                .setPositiveButton(getContext().getString(R.string.group_tips_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        EMClient.getInstance().chatManager().deleteConversation(chatId, true);
                        ViewModelProviders.of(getActivity()).get(MainViewModel.class).updateUnreadMsgCount();
                        refreshConversation();
                        requestFriendMsgs();
                    }
                })
                .show();
    }

    @Override
    public void onFetchModel(String chatId) {
        ClubRequestManager.getUserByHxAccount(chatId, null);
    }
}
