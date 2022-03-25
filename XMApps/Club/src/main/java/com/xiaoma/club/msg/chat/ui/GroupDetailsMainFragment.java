package com.xiaoma.club.msg.chat.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.repo.impl.PushDisabledConversationRepo;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.vm.GroupDetailsVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */
@PageDescComponent(ClubEventConstants.PageDescribe.groupDetailMainAFragment)
public class GroupDetailsMainFragment extends BaseFragment implements View.OnClickListener {

    private GroupDetailsVM groupDetailsVM;
    private static final String KEY_HXID = "key_hxid";
    private ImageView groupIcon, groupSignEdit;
    private TextView groupName, groupSign, groupId, groupMemberCount, groupNotice;
    private Switch noticeSwich;
    private String hxId;
    private long qunId;
    private String oldSign;
    private String groupNick;
    private final PushDisabledConversationRepo mPushDisabledConversationRepo = ClubRepo.getInstance().getPushDisabledConversationRepo();

    public static GroupDetailsMainFragment newInstance(String hxId) {
        GroupDetailsMainFragment fragment = new GroupDetailsMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_group_details_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            hxId = getArguments().getString(KEY_HXID);
        }
        initView(view);
        initVM();
    }

    private RepoObserver mPushDisabledConversationObserver;

    private void initView(View view) {
        view.findViewById(R.id.group_details_exit_btn).setOnClickListener(this);
        groupIcon = view.findViewById(R.id.group_details_icon);
        groupSignEdit = view.findViewById(R.id.group_details_sign_edit_btn);
        groupSignEdit.setOnClickListener(this);
        groupName = view.findViewById(R.id.group_details_main_name);
        groupSign = view.findViewById(R.id.group_details_main_sign);
        groupId = view.findViewById(R.id.group_details_id);
        groupMemberCount = view.findViewById(R.id.group_details_member_count);
        groupNotice = view.findViewById(R.id.group_details_notice);
        noticeSwich = view.findViewById(R.id.msg_notice_switch);
        setPushCheck(!mPushDisabledConversationRepo.isPushDisabled(hxId));
        mPushDisabledConversationRepo.addObserver(mPushDisabledConversationObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                setPushCheck(!mPushDisabledConversationRepo.isPushDisabled(hxId));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPushDisabledConversationRepo.removeObserver(mPushDisabledConversationObserver);
    }

    private void setPushCheck(final boolean check) {
        noticeSwich.post(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                noticeSwich.setOnCheckedChangeListener(null);
                noticeSwich.setChecked(check);
                noticeSwich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mPushDisabledConversationRepo.delete(hxId);
                        } else {
                            mPushDisabledConversationRepo.append(hxId);
                        }
                        XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.messageNotifySwitch,
                                groupNick, String.valueOf(qunId), "GroupDetailsMainFragment", ClubEventConstants.PageDescribe.groupDetailMainAFragment);
                    }
                });
            }
        });
    }

    private void initVM() {
        if (TextUtils.isEmpty(hxId)) {
            return;
        }
        groupDetailsVM = ViewModelProviders.of(this).get(GroupDetailsVM.class);
        groupDetailsVM.getGroup().observe(this, new Observer<GroupCardInfo>() {
            @Override
            public void onChanged(@Nullable GroupCardInfo groupCardInfo) {
                if (groupCardInfo != null) {
                    setUpData(groupCardInfo);
                }
            }
        });
        groupDetailsVM.getIdentity().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String identity) {
                if (identity != null) {
                    showEditIcon(identity);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (groupDetailsVM != null) {
            groupDetailsVM.requestGroupBasicInfo(hxId, null);
        }
    }

    private void setUpData(GroupCardInfo groupCardInfo) {
        qunId = groupCardInfo.getId();
        oldSign = groupCardInfo.getBrief();
        groupNick = groupCardInfo.getNick();
        Glide.with(this)
                .load(groupCardInfo.getPicPath())
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .transform(new SquareCornerLightTransForm(getActivity(), getActivity().getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                .into(groupIcon);
        groupName.setText(groupCardInfo.getNick());
        groupSign.setText(mContext.getString(R.string.group_sign) + "  " + groupCardInfo.getBrief());
        groupId.setText(mContext.getString(R.string.group_id) + "  " + groupCardInfo.getQunNo());
        groupMemberCount.setText(groupCardInfo.getCount() + mContext.getString(R.string.person));
        groupNotice.setText(mContext.getString(R.string.group_notice_content) + "  " + groupCardInfo.getQunInform());
    }

    private void showEditIcon(String identity) {
        if (TextUtils.isEmpty(identity)) {
            return;
        }
        if (identity.equals(ClubConstant.GroupIdentity.GROUP_OWNER) || identity.equals(ClubConstant.GroupIdentity.GROUP_MANAGER)) {
            setSignEditShow(true);
        } else {
            setSignEditShow(false);
        }
    }

    private void setSignEditShow(final boolean isShow) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    groupSignEdit.setVisibility(View.VISIBLE);
                } else {
                    groupSignEdit.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_details_sign_edit_btn:
                if (qunId == 0) {
                    XMToast.toastException(getContext(), getString(R.string.group_id_empty));
                    return;
                }
                if (ClubNetWorkUtils.isConnected(getActivity())) {
                    GroupEditSignActivity.start(getContext(), qunId, oldSign);
                }
                break;
            case R.id.group_details_exit_btn:
                if (!ClubNetWorkUtils.isConnected(getContext())) {
                    return;
                }
                final ConfirmDialog dialog = new ConfirmDialog(getActivity());
                dialog.setContent(getActivity().getString(R.string.confirm_quit_group))
                        .setPositiveButton(getActivity().getString(R.string.group_tips_confirm), new XMAutoTrackerEventOnClickListener() {
                            @Override
                            public ItemEvent returnPositionEventMsg(View view) {
                                return new ItemEvent(ClubEventConstants.NormalClick.exitGroup, groupNick);
                            }

                            @Override
                            @BusinessOnClick
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (groupDetailsVM != null) {
                                    groupDetailsVM.quitGroup(hxId, new SimpleCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            if (getActivity() != null) {
                                                getActivity().finish();
                                            }
                                        }

                                        @Override
                                        public void onError(int errorCode, String errorMessage) {
                                            XMToast.toastException(getActivity(), getActivity().getString(R.string.quit_group_failed));
                                        }
                                    });
                                }
                            }
                        })
                        .show();
                break;
        }
    }
}
