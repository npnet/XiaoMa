package com.xiaoma.club.share.ui;

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

import com.hyphenate.chat.EMMessage;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.viewmodel.ContactVM;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.chat.model.IsMyFriendResult;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.club.share.controller.ShareFriendCardAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.share.ShareClubBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介:分享的朋友列表页面
 *
 * @author lingyan
 */
public class ShareToFriendFragment extends BaseFragment {

    private ShareFriendCardAdapter adapter;
    private XmScrollBar scrollBar;
    private ContactVM contactVM;
    private ShareClubBean bean;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateWrapView(inflater.inflate(R.layout.fmt_contact_friend, null));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBundle();
        initView(view);
        initVM();
    }

    private void initBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            bean = (ShareClubBean) bundle.get(ShareUtils.SHARE_KEY);
        }
        currentUser = UserUtil.getCurrentUser();
    }

    private void initView(View view) {
        RecyclerView contactFriendRv = view.findViewById(R.id.contact_friend_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactFriendRv.setLayoutManager(layoutManager);
        List<User> friendCardInfos = new ArrayList<>();
        contactFriendRv.setAdapter(adapter = new ShareFriendCardAdapter(getActivity(), friendCardInfos));
        adapter.setOnItemClickListener(new ShareFriendCardAdapter.OnFriendItemClickListener() {
            @Override
            public void itemClick(final User info) {
                final ConfirmDialog dialog = new ConfirmDialog(getActivity());
                dialog.setContent(getContext().getString(R.string.share_comfirm, info.getName()))
                        .setPositiveButton(getContext().getString(R.string.share_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shareJudge(dialog, info);
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.club_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactFriendRv);
    }

    private void share(User info) {
        if (bean == null) {
            showToastException(R.string.share_failed);
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage("[ ShareInfo ]",
                info.getHxAccount());
        message.setAttribute(MessageKey.MSG_TYPE, MessageType.SHARE);
        message.setAttribute(MessageKey.Share.FROM_PACK, bean.getFromPackage());
        message.setAttribute(MessageKey.Share.BACK, bean.getBackAction());
        message.setAttribute(MessageKey.Share.CORE, bean.getCoreKey());
        message.setAttribute(MessageKey.Share.TITLE, bean.getShareTitle());
        message.setAttribute(MessageKey.Share.CONTENT, bean.getShareContent());
        message.setAttribute(MessageKey.Share.URL, bean.getShareImage());
        message.setAttribute(MessageKey.Share.FROM_DETAIL, bean.getCarTeamDetail());
        message.setAttribute(MessageKey.Share.SHARE_CAR_ID, bean.getCarTeamId());
        // 会话类型
        message.setChatType(EMMessage.ChatType.Chat);
        IMUtils.sendMessage(message);
        XMToast.toastSuccess(getActivity(), R.string.share_success);
        if (NetworkUtils.isConnected(getContext())) {
            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.SENDMESSAGE.getType());
        }
        KLog.d("share " + bean.getShareTitle() + " to " + info.getName());
        ChatActivity.start(getActivity(), info.getHxAccount(), false);
    }

    private void shareJudge(final ConfirmDialog dialog, final User info) {
        if (!NetworkUtils.isConnected(getContext())) {
            showToastException(R.string.net_work_error);
            return;
        } else {
            ClubRequestManager.isMyFriend(String.valueOf(UserUtil.getCurrentUid()),
                    String.valueOf(info.getId()), new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            IsMyFriendResult result = GsonHelper.fromJson(response.body(), IsMyFriendResult.class);
                            if (result != null) {
                                // 是好友关系
                                if (result.isMyFriend()) {
                                    dialog.dismiss();
                                    share(info);
                                } else {
                                    showToastException(R.string.not_your_friend);
                                    dialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                        }
                    });
        }
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
        try {
            if (currentUser != null) {
                contactVM.getContactFriend(String.valueOf(currentUser.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindFriendsData(List<User> list) {
        if (list != null && !list.isEmpty()) {
            adapter.refreshData(list);
            checkEmptyView(list);
        } else {
            showEmptyView();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        try {
            if (currentUser != null && contactVM != null) {
                contactVM.getContactFriend(String.valueOf(currentUser.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
