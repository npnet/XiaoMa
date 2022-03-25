package com.xiaoma.club.msg.chat.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.chat.model.GroupMemberInfo;
import com.xiaoma.image.transformation.CircleTransform;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberHolder> {

    private Context context;
    private List<GroupMemberInfo> memberInfos;
    private String identity = ClubConstant.GroupIdentity.GROUP_MEMBER;
    private long currentUserId;
    private int operationPosition = -1;

    public GroupMemberAdapter(Context context, List<GroupMemberInfo> memberInfos) {
        this.context = context;
        this.memberInfos = memberInfos;
        currentUserId = UserUtil.getCurrentUser().getId();
    }

    public List<GroupMemberInfo> getMemberInfos() {
        return memberInfos;
    }

    public void resetOperation() {
        operationPosition = -1;
        notifyDataSetChanged();
    }

    @Override
    public GroupMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_member_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new GroupMemberHolder(view);
        return (GroupMemberHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupMemberHolder holder, int position) {
        if (memberInfos != null && !memberInfos.isEmpty()) {
            setItemData(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return memberInfos == null ? 0 : memberInfos.size();
    }

    private void setItemData(final GroupMemberHolder holder, int position) {
        GroupMemberInfo memberInfo = memberInfos.get(position);
        if (memberInfo == null) {
            return;
        }
        Glide.with(context)
                .load(memberInfo.getPicPath())
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.default_head_icon)
                .error(R.drawable.default_head_icon)
                .into(holder.groupMemberItemIcon);
        boolean isMute = memberInfo.isMute();
        holder.groupMemberisMute.setVisibility(isMute ? View.VISIBLE : View.GONE);
        holder.groupMemberItemName.setText(memberInfo.getName());
        holder.groupMemberItemGender.setText(memberInfo.getGender() == 0 ? context.getString(R.string.girl) : context.getString(R.string.boy));
        holder.groupMemberItemAge.setText(memberInfo.getAge());
        //        holder.groupMemberItemLevel.setText(memberInfo.getMemberLevel());
        if (operationPosition == position) {
            showMemberOperation(holder, memberInfo, true);
        } else {
            showMemberOperation(holder, memberInfo, false);
        }
        setIdentity(holder, memberInfo.getUserRole());
        setPermission(holder, position, isMute);
        setOnClick(holder, memberInfo, isMute);
    }

    private void setIdentity(final GroupMemberHolder holder, String userRole) {
        if (TextUtils.isEmpty(userRole)) {
            return;
        }
        if (ClubConstant.GroupIdentity.GROUP_OWNER.equals(userRole)) {
            //群主
            holder.groupMemberIdentity.setVisibility(View.VISIBLE);
            holder.groupMemberIdentity.setImageResource(R.drawable.group_identity_owner);
        } else if (ClubConstant.GroupIdentity.GROUP_MANAGER.equals(userRole)) {
            //管理
            holder.groupMemberIdentity.setVisibility(View.VISIBLE);
            holder.groupMemberIdentity.setImageResource(R.drawable.group_identity_manager);
        } else {
            //成员
            holder.groupMemberIdentity.setVisibility(View.GONE);
        }
    }

    private void setPermission(final GroupMemberHolder holder, final int position, final boolean isMute) {
        holder.groupMemberItemForbidSpeakBtn.setText(isMute ? context.getString(R.string.un_forbid_speak) : context.getString(R.string.forbid_speak));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operationPosition == position) {
                    operationPosition = -1;
                } else {
                    operationPosition = position;
                }
                notifyDataSetChanged();
            }
        });
    }

    private void showMemberOperation(final GroupMemberHolder holder, final GroupMemberInfo memberInfo, boolean isShow) {
        if (!isShow) {
            holder.groupMemberItemPrivateChat.setVisibility(View.GONE);
            holder.groupMemberItemForbidSpeak.setVisibility(View.GONE);
            holder.groupMemberItemKickOut.setVisibility(View.GONE);
            //显示年龄性别
            holder.groupMemberItemAge.setVisibility(View.VISIBLE);
            holder.groupMemberItemGender.setVisibility(View.VISIBLE);
            return;
        }
        if (currentUserId != memberInfo.getId()) {
            //隐藏年龄性别
            holder.groupMemberItemAge.setVisibility(View.INVISIBLE);
            holder.groupMemberItemGender.setVisibility(View.INVISIBLE);

            holder.groupMemberItemPrivateChat.setVisibility(View.VISIBLE);
            if (identity.equals(ClubConstant.GroupIdentity.GROUP_OWNER)) {
                //群主
                holder.groupMemberItemForbidSpeak.setVisibility(View.VISIBLE);
                holder.groupMemberItemKickOut.setVisibility(View.VISIBLE);
            } else if (identity.equals(ClubConstant.GroupIdentity.GROUP_MANAGER)) {
                //管理
                if (!memberInfo.getUserRole().equals(ClubConstant.GroupIdentity.GROUP_OWNER) && !memberInfo.getUserRole().equals(ClubConstant.GroupIdentity.GROUP_MANAGER)) {
                    //管理不能禁言群主和管理
                    holder.groupMemberItemForbidSpeak.setVisibility(View.VISIBLE);
                    holder.groupMemberItemKickOut.setVisibility(View.GONE);
                }
            } else {
                //成员
                holder.groupMemberItemForbidSpeak.setVisibility(View.GONE);
                holder.groupMemberItemKickOut.setVisibility(View.GONE);
            }
        }
    }

    private void setOnClick(GroupMemberHolder holder, final GroupMemberInfo memberInfo, final boolean isMute) {
        holder.groupMemberItemPrivateChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.singleChat(memberInfo.getHxAccount());
                }
            }
        });
        holder.groupMemberItemForbidSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (isMute) {
                        listener.unForbidSpeak(memberInfo.getHxAccount());
                    } else {
                        listener.forbidSpeak(memberInfo.getHxAccount());
                    }
                }
            }
        });
        holder.groupMemberItemKickOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.kickOut(memberInfo.getId(), memberInfo.getName());
                }
            }
        });
    }

    public void refreshData(List<GroupMemberInfo> memberInfos) {
        if (memberInfos != null && !memberInfos.isEmpty()) {
            this.memberInfos = memberInfos;
            notifyDataSetChanged();
            getIdentityByLocalData();
        }
    }

    private void getIdentityByLocalData() {
        //获取当前用户在该部落身份
        String currentHxId = UserUtil.getCurrentUser().getHxAccount();
        if (currentHxId != null && this.memberInfos != null && !this.memberInfos.isEmpty()) {
            for (GroupMemberInfo member : this.memberInfos) {
                if (member.getHxAccount().equals(currentHxId)) {
                    refreshIdentity(member.getUserRole());
                    return;
                }
            }
        }
        refreshIdentity(ClubConstant.GroupIdentity.GROUP_MEMBER);
    }

    private void refreshIdentity(String s) {
        this.identity = s;
    }

    class GroupMemberHolder extends RecyclerView.ViewHolder {

        private ImageView groupMemberItemIcon;
        private ImageView groupMemberisMute;
        private ImageView groupMemberIdentity;
        private TextView groupMemberItemName;
        private TextView groupMemberItemGender;
        private TextView groupMemberItemAge;
        private TextView groupMemberItemLevel;
        private LinearLayout groupMemberItemRl;
        private RelativeLayout groupMemberItemPrivateChat;
        private Button groupMemberItemPrivateChatBtn;
        private RelativeLayout groupMemberItemForbidSpeak;
        private Button groupMemberItemForbidSpeakBtn;
        private RelativeLayout groupMemberItemKickOut;
        private Button groupMemberItemKickOutBtn;

        public GroupMemberHolder(View itemView) {
            super(itemView);
            groupMemberItemIcon = itemView.findViewById(R.id.group_member_item_icon);
            groupMemberisMute = itemView.findViewById(R.id.group_member_ismute);
            groupMemberIdentity = itemView.findViewById(R.id.group_member_identity);
            groupMemberItemName = itemView.findViewById(R.id.group_member_item_name);
            groupMemberItemGender = itemView.findViewById(R.id.group_member_item_gender);
            groupMemberItemAge = itemView.findViewById(R.id.group_member_item_age);
            groupMemberItemLevel = itemView.findViewById(R.id.group_member_item_level);
            groupMemberItemRl = itemView.findViewById(R.id.group_member_item_ll);
            groupMemberItemPrivateChat = itemView.findViewById(R.id.group_member_item_private_chat);
            groupMemberItemPrivateChatBtn = itemView.findViewById(R.id.group_member_item_private_chat_btn);
            groupMemberItemForbidSpeak = itemView.findViewById(R.id.group_member_item_forbid_speak);
            groupMemberItemForbidSpeakBtn = itemView.findViewById(R.id.group_member_item_forbid_speak_btn);
            groupMemberItemKickOut = itemView.findViewById(R.id.group_member_item_kick_out);
            groupMemberItemKickOutBtn = itemView.findViewById(R.id.group_member_item_kick_out_btn);
        }
    }

    private MemberOperationListener listener;

    public void setMemberOperationListener(MemberOperationListener listener) {
        this.listener = listener;
    }

    public interface MemberOperationListener {
        void singleChat(String hxId);

        void forbidSpeak(String hxId);

        void unForbidSpeak(String hxId);

        void kickOut(long userId, String name);
    }
}
