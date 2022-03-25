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
import com.xiaoma.club.common.util.UploadGroupScoreManager;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.contact.viewmodel.ContactVM;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.club.share.controller.ShareGroupCardAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.share.ShareClubBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介:分享的部落列表页面
 *
 * @author lingyan
 */
public class ShareToGroupFragment extends BaseFragment {

    private ShareGroupCardAdapter adapter;
    private XmScrollBar scrollBar;
    private ContactVM contactVM;
    private ShareClubBean bean;
    private ConfirmDialog dialog;
    private GroupCardInfo shareInfo;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateWrapView(inflater.inflate(R.layout.fmt_contact_group, null));
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
        RecyclerView contactGroupRv = view.findViewById(R.id.contact_group_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contactGroupRv.setLayoutManager(layoutManager);
        List<GroupCardInfo> groupCardInfos = new ArrayList<>();
        contactGroupRv.setItemAnimator(null);
        adapter = new ShareGroupCardAdapter(ImageLoader.with(this), groupCardInfos);
        adapter.setOnItemClickListener(new ShareGroupCardAdapter.OnGroupItemClickListener() {
            @Override
            public void itemClick(final GroupCardInfo info) {
                shareInfo = info;
                dialog = new ConfirmDialog(getActivity());
                dialog.setContent(getContext().getString(R.string.share_comfirm, info.getNick()))
                        .setPositiveButton(getContext().getString(R.string.share_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestGroupsInfo();
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
        contactGroupRv.setAdapter(adapter);
        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(contactGroupRv);
    }

    private void share(GroupCardInfo info) {
        if (bean == null) {
            showToastException(R.string.share_failed);
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage("[ ShareInfo ]",
                info.getHxGroupId());
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
        message.setChatType(EMMessage.ChatType.GroupChat);
        //上报发送部落消息事件
        UploadGroupScoreManager.getInstance().uploadGroupMessage(message.getTo(), message.getType().toString());
        IMUtils.sendMessage(message);
        XMToast.toastSuccess(getActivity(), R.string.share_success);
        if (NetworkUtils.isConnected(getContext())) {
            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.SENDMESSAGE.getType());
        }
        KLog.d("share " + bean.getShareTitle() + " to " + info.getNick());
        ChatActivity.start(getActivity(), info.getHxGroupId(), true);
    }

    private void requestGroupsInfo() {
        if (!NetworkUtils.isConnected(getContext())) {
            showToastException(R.string.net_work_error);
            return;
        } else {
            String userId = String.valueOf(UserUtil.getCurrentUid());
            contactVM.getContactGroup(userId, null);
        }
    }

    private void initVM() {
        contactVM = ViewModelProviders.of(getActivity()).get(ContactVM.class);
        contactVM.getGroupList().observe(this, new Observer<XmResource<List<GroupCardInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<GroupCardInfo>> data) {
                data.handle(new OnCallback<List<GroupCardInfo>>() {
                    @Override
                    public void onSuccess(List<GroupCardInfo> list) {
                        shareJudge(list);
                        bindGroupList(list);
                    }

                    @Override
                    public void onFailure(String msg) {
                        shareJudge(new ArrayList<GroupCardInfo>());
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
                contactVM.getContactGroup(String.valueOf(currentUser.getId()), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareJudge(List<GroupCardInfo> infos) {
        if (dialog != null && shareInfo != null && infos != null) {
            if (infos.isEmpty()) {
                showToastException(R.string.not_in_group);
            } else {
                for (GroupCardInfo info : infos) {
                    if (info.getId() == info.getId()) {
                        dialog.dismiss();
                        share(shareInfo);
                        return;
                    }
                }
                showToastException(R.string.not_in_group);
            }
            dialog.dismiss();
        }
    }

    private void bindGroupList(List<GroupCardInfo> list) {
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
                contactVM.getContactGroup(String.valueOf(currentUser.getId()), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkEmptyView(List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            showContentView();
        }
    }

}
