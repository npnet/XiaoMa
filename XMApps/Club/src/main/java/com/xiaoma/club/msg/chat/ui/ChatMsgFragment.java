package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.util.ClubSettings;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.viewmodel.BooleanState;
import com.xiaoma.club.msg.chat.constant.ChatMsgEventTag;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.controller.ChatMsgListAdapter;
import com.xiaoma.club.msg.chat.model.HxChatParam;
import com.xiaoma.club.msg.chat.model.MessageStatusResult;
import com.xiaoma.club.msg.chat.vm.ChatMsgVM;
import com.xiaoma.club.msg.chat.vm.ChatVM;
import com.xiaoma.club.msg.conversation.util.ConversationUtil;
import com.xiaoma.club.msg.redpacket.datasource.RPRequest;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.ui.RPDetailActivity;
import com.xiaoma.club.msg.redpacket.ui.RPOpenDialog;
import com.xiaoma.club.msg.voiceplayer.IVoiceMsgPlayerManager;
import com.xiaoma.club.msg.voiceplayer.VoiceMsgPlayCallback;
import com.xiaoma.club.msg.voiceplayer.VoiceMsgPlayerFactory;
import com.xiaoma.club.msg.voiceplayer.VoicePlayError;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;
import java.util.Objects;

import static com.hyphenate.EMError.GROUP_NOT_EXIST;
import static com.hyphenate.EMError.GROUP_NOT_JOINED;
import static com.hyphenate.EMError.USER_MUTED;

/**
 * Created by LKF on 2018/10/10 0010.
 * 聊天消息列表
 */

public class ChatMsgFragment extends BaseFragment {
    private static final String TAG = "ChatMsgFragment";

    public static final String EXTRA_HX_CHAT_ID = "hxChatId";
    public static final String EXTRA_IS_GROUP_CHAT = "isGroupChat";
    private ChatVM mChatVM;
    private ChatMsgVM mChatMsgVM;
    private RecyclerView mRvMsgList;
    private View mAddFriendContainer;
    private Button mBtnAddFriend;
    private ChatMsgListAdapter mMsgAdapter;
    private EMMessageListener mMessageListener;
    private PagedList.BoundaryCallback<EMMessage> mChatMsgBoundaryCallback;
    private EMConversation mConversation;
    private boolean mScrollEndOnListChanged = true;
    private VoiceMsgPlayCallback mVoiceMsgPlayCallback;
    private RepoObserver mUserObserver;
    private IVoiceMsgPlayerManager mVoiceMsgPlayerManager;

    public static ChatMsgFragment create(String hxChatId, boolean isGroupChat, Callback callback) {
        final ChatMsgFragment fragment = new ChatMsgFragment();
        final Bundle args = new Bundle();
        args.putString(EXTRA_HX_CHAT_ID, hxChatId);
        args.putBoolean(EXTRA_IS_GROUP_CHAT, isGroupChat);
        fragment.setArguments(args);
        fragment.setCallback(callback);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_chat_msg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();
        mAddFriendContainer = view.findViewById(R.id.add_friend_container);
        mBtnAddFriend = view.findViewById(R.id.btn_add_friend);
        mRvMsgList = view.findViewById(R.id.rv_msg_list);

        mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onAddFriend();
                }
            }
        });
        // 语音消息播放器
        mVoiceMsgPlayerManager = VoiceMsgPlayerFactory.createPlayer();
        mVoiceMsgPlayerManager.init(context);
        mVoiceMsgPlayerManager.addVoiceMsgPlayCallback(mVoiceMsgPlayCallback = new VoiceMsgPlayCallback() {
            @Override
            public void onPlay(final EMMessage message) {
                update(false);
            }

            @Override
            public void onStop(final EMMessage message) {
                update(false);
            }

            @Override
            public void onComplete(final EMMessage message) {
                update(false);
                playVoiceMsgSerial(message);
            }

            @Override
            public void onError(final int code, final String msg) {
                if (VoicePlayError.ERR_AUDIO_FOCUS_OCCUPIED == code) {
                    update(true, R.string.tips_audio_focus_occupied_cannot_play);
                } else {
                    update(true);
                }
            }

            private void update(final boolean showError) {
                update(showError, 0);
            }

            private void update(final boolean showError, @StringRes final int errorRes) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        if (mMsgAdapter != null) {
                            mMsgAdapter.notifyDataSetChanged();
                        }
                        if (showError) {
                            XMToast.toastException(context, errorRes != 0 ? errorRes : R.string.voice_msg_play_error);
                        }
                    }
                });
            }
        });

        mRvMsgList.setLayoutManager(new LinearLayoutManager(context));
        mRvMsgList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 10);
            }
        });
        mRvMsgList.setItemAnimator(null);
        mRvMsgList.setHasFixedSize(true);
        mRvMsgList.setAdapter(mMsgAdapter = new ChatMsgListAdapter(Glide.with(ChatMsgFragment.this), mVoiceMsgPlayerManager, new ChatMsgListAdapter.Callback() {
            @Override
            public void onUserHeadClick(EMMessage message) {
                if (mCallback != null) {
                    mCallback.onUserHeadClick(message);
                }
            }

            @Override
            public void onFaceClick(EMMessage message, String uri) {
                final View v = getView();
                if (v == null)
                    return;
                final int w = v.getMeasuredWidth();
                final int h = v.getMeasuredHeight();
                new ImageDisplayPopup(v.getContext(), w, h, uri).showAtLocation(v, Gravity.END | Gravity.BOTTOM, 0, 0);
            }

            @Override
            public void onVoiceClick(EMMessage message) {
                // 产品需求:当发送失败时,点击热区改为整个语音消息
                if (EMMessage.Direct.SEND == message.direct()
                        && EMMessage.Status.FAIL == message.status()) {
                    onReSendMsg(message);
                    return;
                }
                message.setListened(true);
                EMClient.getInstance().chatManager().updateMessage(message);
                final IVoiceMsgPlayerManager player = mVoiceMsgPlayerManager;
                if (message.equals(player.getCurrMessage())) {
                    if (player.isPlaying()) {
                        player.stop();
                    } else {
                        player.play(message);
                    }
                } else {
                    player.play(message);
                }
            }

            @Override
            public void onFetchUserInfo(EMMessage message, final String hxAccount) {
                ClubRequestManager.getUserByHxAccount(hxAccount, new CallbackWrapper<User>() {
                    @Override
                    public User parse(String data) {
                        return GsonHelper.fromJson(data, User.class);
                    }
                });
            }

            @Override
            public void onReSendMsg(final EMMessage message) {
                if (getContext() == null)
                    return;
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                final ConfirmDialog dlg = new ConfirmDialog(getActivity());
                dlg.setContent(getString(R.string.resend_msg_tips))
                        .setPositiveButton(getString(R.string.resend_msg_confirm), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (checkNetworkDisable()) {
                                    return;
                                }
                                IMUtils.reSendMessage(message);
                                dlg.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.club_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.dismiss();
                            }
                        })
                        .setCancelable(true);
                dlg.show();
                /*new AlertDialog.Builder(getContext())
                        .setTitle(R.string.resend_msg_title)
                        .setMessage(R.string.resend_msg_tips)
                        .setCancelable(true)
                        .setPositiveButton(R.string.resend_msg_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (checkNetworkDisable()) {
                                    return;
                                }
                                IMUtils.reSendMessage(message);
                            }
                        })
                        .setNegativeButton(R.string.club_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();*/
            }

            @Override
            public void onOpenRedPacket(final EMMessage message) {
                // 拆红包
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                showProgressDialog(R.string.base_loading);
                // 查询红包状态,以确定Dialog的初始状态
                final RedPacketInfo info = RedPacketInfo.fromMessage(message);
                RPRequest.queryPacket(info.getPacketId(), new SimpleCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer openStatus) {
                        if (getActivity() == null)
                            return;
                        dismissProgress();
                        updateRPOpenStatus(openStatus);
                        info.setOpenStatus(openStatus);
                        if (Objects.equals(message.getFrom(), EMClient.getInstance().getCurrentUser())
                                && EMMessage.ChatType.Chat == message.getChatType()) {
                            // 自己发的私聊红包不能抢
                            RPDetailActivity.launch(getActivity(), UserUtil.getCurrentUser(), info);
                        } else {
                            RPOpenDialog dlg = new RPOpenDialog(getActivity(), info, message.getFrom(),
                                    EMMessage.ChatType.GroupChat == message.getChatType()
                                    , openStatus);
                            dlg.setCallback(new RPOpenDialog.Callback() {
                                @Override
                                public void onPacketOpenResult(int rpOpenResult) {
                                    updateRPOpenStatus(rpOpenResult);
                                }
                            });
                            dlg.show();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        if (isDestroy())
                            return;
                        dismissProgress();
                        showToastException(StringUtil.optString(msg, getString(R.string.rp_msg_item_query_fail)));
                    }

                    private void updateRPOpenStatus(int openStatus) {
                        message.setAttribute(MessageKey.RedPacket.OPEN_STATUS, openStatus);
                        mMsgAdapter.notifyDataSetChanged();
                        EMClient.getInstance().chatManager().updateMessage(message);
                    }
                });
            }

            @Override
            public boolean isGroupChat() {
                return ChatMsgFragment.this.isGroupChat();
            }
        }));

        XmScrollBar scrollBar = view.findViewById(R.id.scroll_bar);
        scrollBar.setRecyclerView(mRvMsgList);

        mUserObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                LogUtil.logI(TAG, "onChanged( tables: %s )", table);
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mMsgAdapter != null && !isDestroy()) {
                            mMsgAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        };
        ClubRepo.getInstance().getUserRepo().addObserver(mUserObserver);
        if (isGroupChat()) {
            // 如果是群聊,要监听群组变化和全局用户数据的变化
            ClubRepo.getInstance().getGroupRepo().addObserver(mUserObserver);
        }
        if (getConversation() == null) {
            KLog.e("No conversation to current ChatId");
        }

        EMClient.getInstance().chatManager().addMessageListener(mMessageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                LogUtil.logI(TAG, "onMessageReceived( messages: %s )", messages);
                updateChatMsgList(true);
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                LogUtil.logI(TAG, "onCmdMessageReceived( messages: %s )", messages);
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                LogUtil.logI(TAG, "onMessageRead( messages: %s )", messages);
                updateChatMsgList(false);
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
                LogUtil.logI(TAG, "onMessageDelivered( messages: %s )", messages);
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                LogUtil.logI(TAG, "onMessageRecalled( messages: %s )", messages);
                if (messages == null || messages.isEmpty())
                    return;
                for (EMMessage message : messages) {
                    // 如果是语音消息被撤回,要停止播放
                    final EMMessage playingMsg = mVoiceMsgPlayerManager.getCurrMessage();
                    if (playingMsg != null && playingMsg.equals(message)) {
                        mVoiceMsgPlayerManager.stop();
                    }
                    LogUtil.logI(TAG, "onMessageRecalled( messages: %s )", messages);
                }
                updateChatMsgList(false);
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                LogUtil.logI(TAG, "onMessageChanged( messages: %s, change: %s )", message, change);
                updateChatMsgList(false);
            }
        });

        mChatVM = ViewModelProviders.of(getActivity()).get(ChatVM.class);
        mChatVM.getIsFriend().observe(this, new Observer<BooleanState>() {
            @Override
            public void onChanged(@Nullable BooleanState isFriend) {
                bindIsFriend(isFriend);
            }
        });
        bindIsFriend(mChatVM.getIsFriend().getValue());

        mChatMsgVM = ViewModelProviders.of(this).get(ChatMsgVM.class);
        mChatMsgVM.getEMMessagePageList().observe(this, new Observer<PagedList<EMMessage>>() {
            @Override
            public void onChanged(@Nullable PagedList<EMMessage> messages) {
                LogUtil.logI(TAG, String.format("EMMessagePageList # onChanged( PagedList<EMMessage> ) size: %s", messages != null ? messages.size() : "null"));
                mMsgAdapter.submitList(messages);
                if (isScrollEndOnListChanged()) {
                    mRvMsgList.scrollToPosition(mRvMsgList.getAdapter().getItemCount() - 1);
                }
            }
        });
        mChatMsgVM.addEMMessagePageListCallback(mChatMsgBoundaryCallback = new PagedList.BoundaryCallback<EMMessage>() {
            @Override
            public void onItemAtEndLoaded(@NonNull EMMessage itemAtEnd) {
                mRvMsgList.scrollToPosition(mRvMsgList.getAdapter().getItemCount() - 1);
            }
        });
        updateChatMsgList(true);// 刷新聊天记录List
        EventBus.getDefault().register(this);
    }

    private void bindIsFriend(BooleanState isFriend) {
        HxChatParam chatParam = mChatVM.getHxChatParam().getValue();
        if (chatParam != null && !chatParam.isGroupChat()) {
            User chatToUser = ClubRepo.getInstance().getUserRepo().getByKey(chatParam.getHxChatId());
            // 系统用户不显示加好友
            if (chatToUser != null && chatToUser.isSystemAccount()) {
                mAddFriendContainer.setVisibility(View.GONE);
                return;
            }
        }
        final EMConversation conversation = getConversation();
        if (conversation == null || conversation.isGroup()) {
            mAddFriendContainer.setVisibility(View.GONE);
        } else {
            if (isFriend == null)
                return;
            switch (isFriend) {
                case FETCHING:
                case TRUE:
                    mAddFriendContainer.setVisibility(View.GONE);
                    break;
                case FALSE:
                    mAddFriendContainer.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mVoiceMsgPlayerManager != null) {
                mVoiceMsgPlayerManager.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        if (mChatMsgVM != null) {
            mChatMsgVM.removeEMMessagePageListCallback(mChatMsgBoundaryCallback);
        }
        EMClient.getInstance().chatManager().removeConversationListener(mEMConversationListener);

        mVoiceMsgPlayerManager.removeVoiceMsgPlayCallback(mVoiceMsgPlayCallback);
        mVoiceMsgPlayerManager.release();
        ClubRepo.getInstance().getUserRepo().removeObserver(mUserObserver);
        if (isGroupChat()) {
            ClubRepo.getInstance().getGroupRepo().removeObserver(mUserObserver);
        }
        EventBus.getDefault().unregister(this);
    }

    private void playVoiceMsgSerial(EMMessage lastPlayCompleteMessage) {
        if (lastPlayCompleteMessage == null)
            return;
        try {
            if (!isDestroy() && ClubSettings.isAutoPlayOpen()) {
                // 自动顺序播放语音
                final PagedList<EMMessage> msgList = mChatMsgVM.getEMMessagePageList().getValue();
                if (msgList != null) {
                    final int size = msgList.size();
                    final int currIndex = msgList.indexOf(lastPlayCompleteMessage) + 1;
                    for (int i = currIndex; i < size; i++) {
                        final EMMessage willPlayMsg = msgList.get(i);
                        if (willPlayMsg != null
                                && EMMessage.Direct.RECEIVE == willPlayMsg.direct()
                                && EMMessage.Type.VOICE == willPlayMsg.getType()
                                && !willPlayMsg.isListened()) {
                            willPlayMsg.setListened(true);
                            EMClient.getInstance().chatManager().updateMessage(willPlayMsg);
                            mVoiceMsgPlayerManager.play(willPlayMsg);
                            mRvMsgList.scrollToPosition(i);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkNetworkDisable() {
        if (!NetworkUtils.isConnected(getContext())) {
            XMToast.toastException(getContext(), R.string.net_work_error);
            return true;
        }
        return false;
    }

    @Subscriber(tag = ChatMsgEventTag.SEND_MESSAGE)
    private void onMsgSend(final EMMessage message) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (message == null || isDestroy())
                    return;
                updateChatMsgList(true);
            }
        });
    }

    @Subscriber(tag = ChatMsgEventTag.SEND_MESSAGE_STATUS_CHANGED)
    private void onMsgStatusChanged(final MessageStatusResult statusResult) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                EMConversation conversation = null;
                EMMessage message = null;
                if (statusResult != null
                        && (message = statusResult.getMessage()) != null
                        && (conversation = getConversation()) != null
                        && Objects.equals(message.getTo(), conversation.conversationId())) {
                    LogUtil.logI(TAG, "onMsgStatusChanged( status: %s, progress %s)", message.status(), message.progress());
                    updateChatMsgList(false);
                    if (EMMessage.Status.FAIL == message.status()) {
                        final int code = statusResult.getErrorCode();
                        @StringRes int tipsRes = 0;
                        switch (code) {
                            case USER_MUTED:
                                tipsRes = R.string.group_send_msg_fail_was_mute;
                                break;
                            case GROUP_NOT_JOINED:
                                tipsRes = R.string.group_send_msg_fail_not_join;
                                EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                                break;
                            case GROUP_NOT_EXIST:
                                tipsRes = R.string.group_send_msg_fail_not_exist;
                                break;
                        }
                        final String tipsContent;
                        if (tipsRes != 0) {
                            tipsContent = getString(tipsRes);
                        } else {
                            tipsContent = getString(R.string.msg_send_failed);
                        }
                        XMToast.toastException(getContext(), tipsContent);
                    }
                } else {
                    LogUtil.logW(TAG, "onMsgStatusChanged() [ message: %s, conversation: %s ]", message, conversation);
                }
            }
        });
    }

    @Subscriber(tag = ChatMsgEventTag.RECALL_MESSAGE)
    private void onMsgRecall(final EMMessage message) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                // 如果是语音消息被撤回,要停止播放
                final EMMessage playingMsg = mVoiceMsgPlayerManager.getCurrMessage();
                if (playingMsg != null && playingMsg.equals(message)) {
                    mVoiceMsgPlayerManager.stop();
                }
                updateChatMsgList(false);
            }
        });
    }

    @Subscriber(tag = ChatMsgEventTag.SAVE_MESSAGE)
    private void onMsgSaved(final EMMessage message) {
        updateChatMsgList(true);
    }

    private EMConversationListener mEMConversationListener;

    private boolean isGroupChat() {
        final EMConversation conversation = getConversation();
        return conversation != null && conversation.isGroup();
    }

    private EMConversation getConversation() {
        EMConversation conversation = mConversation;
        if (conversation != null) {
            return conversation;
        }
        final Bundle args = getArguments();
        if (args == null) {
            showToast(getString(R.string.conversation_arg_error));
            KLog.e("Arguments is null, return");
            return null;
        }
        final String hxChatId = args.getString(EXTRA_HX_CHAT_ID);
        final boolean isGroupChat = args.getBoolean(EXTRA_IS_GROUP_CHAT);
        if (isGroupChat) {
            conversation = ConversationUtil.getConversation(hxChatId, EMConversation.EMConversationType.GroupChat);
        } else {
            conversation = ConversationUtil.getConversation(hxChatId, EMConversation.EMConversationType.Chat);
        }
        if (conversation != null) {
            EMClient.getInstance().chatManager().addConversationListener(mEMConversationListener = new EMConversationListener() {
                @Override
                public void onCoversationUpdate() {
                    LogUtil.logI(TAG, "onConversationUpdate()");
                    updateChatMsgList(false);
                }
            });
        }
        mConversation = conversation;
        return conversation;
    }

    private void updateChatMsgList(boolean scrollEnd) {
        // 进入消息刷新列表则表示全部已读
        if (getConversation() != null) {
            getConversation().markAllMessagesAsRead();
        }
        setScrollEndOnListChanged(scrollEnd);
        mChatMsgVM.setConversation(getConversation());
        ViewCompat.postOnAnimation(mRvMsgList, new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                if (mMsgAdapter != null) {
                    mMsgAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private boolean isScrollEndOnListChanged() {
        return mScrollEndOnListChanged;
    }

    private void setScrollEndOnListChanged(boolean scrollEndOnListChanged) {
        mScrollEndOnListChanged = scrollEndOnListChanged;
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onAddFriend();

        void onUserHeadClick(EMMessage message);
    }
}