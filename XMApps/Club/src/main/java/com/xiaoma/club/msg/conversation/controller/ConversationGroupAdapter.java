package com.xiaoma.club.msg.conversation.controller;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.hyphenate.chat.EMConversation;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.club.msg.conversation.model.ConversationAdapterCallBack;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.utils.StringUtil;

import java.util.List;

import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.friendConversationFragment;
import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.groupConversationFragment;
import static com.xiaoma.club.msg.conversation.util.ConversationUtil.getMessageDate;
import static com.xiaoma.club.msg.conversation.util.ConversationUtil.getNewMsg;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ConversationGroupAdapter extends RecyclerView.Adapter<ConversationGroupAdapter.GroupMsgHolder> {

    private RequestManager requestManager;
    private List<EMConversation> groupMsgInfos;

    public ConversationGroupAdapter(RequestManager requestManager, List<EMConversation> groupMsgInfos) {
        this.requestManager = requestManager;
        this.groupMsgInfos = groupMsgInfos;
    }

    @NonNull
    @Override
    public GroupMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_msg_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new GroupMsgHolder(view);
        return (GroupMsgHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMsgHolder holder, int position) {
        if (groupMsgInfos != null && !groupMsgInfos.isEmpty()) {
            setItemData(holder, groupMsgInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return groupMsgInfos == null ? 0 : groupMsgInfos.size();
    }

    private void setItemData(GroupMsgHolder holder, final EMConversation groupMsgInfo) {
        String groupName = null;
        final GroupCardInfo groupCardInfo = ClubRepo.getInstance().getGroupRepo().get(groupMsgInfo.conversationId());
        if (groupCardInfo != null) {
            groupName = groupCardInfo.getNick();
        } else {
            if (callBack != null) {
                callBack.onFetchModel(groupMsgInfo.conversationId());
            }
        }
        Context context = holder.itemView.getContext();
        try {
            final String picPath = groupCardInfo != null ? groupCardInfo.getPicPath() : "";
            requestManager.load(picPath)
                    .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.groupMsgIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.groupMsgName.setText(StringUtil.optString(groupName));
        holder.groupMsgNewmsg.setText(getNewMsg(context, groupMsgInfo));
        if (groupMsgInfo.getLastMessage() != null) {
            holder.groupMsgDate.setText(getMessageDate(context, groupMsgInfo.getLastMessage().getMsgTime()));
        }
        ViewUtils.setMsgCounts(holder.groupNewMsgCount, groupMsgInfo.getUnreadMsgCount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupMsgInfo.markAllMessagesAsRead();
                ChatActivity.start(v.getContext(), groupMsgInfo.conversationId(), true);
                XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.chatInGroup,
                        groupCardInfo.getNick(), groupCardInfo.getHxGroupId(),
                        "ConversationGroupFragment", groupConversationFragment);
            }
        });
        holder.groupMsgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.delete(groupMsgInfo.conversationId(), groupCardInfo.getBrief());
                    XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.deleteConversation,
                            groupCardInfo.getNick(), groupCardInfo.getHxGroupId(),
                            "ConversationGroupFragment", groupConversationFragment);
                }
            }
        });
    }

    public void refreshData(List<EMConversation> msgInfos) {
        if (msgInfos != null) {
            this.groupMsgInfos.clear();
            this.groupMsgInfos.addAll(msgInfos);
            notifyDataSetChanged();
        }
    }

    class GroupMsgHolder extends RecyclerView.ViewHolder {

        private ImageView groupMsgIcon;
        private TextView groupMsgName;
        private TextView groupMsgNewmsg;
        private TextView groupMsgDate;
        private ImageView groupMsgDelete;
        private TextView groupNewMsgCount;

        GroupMsgHolder(View itemView) {
            super(itemView);
            groupMsgIcon = itemView.findViewById(R.id.group_msg_icon);
            groupMsgName = itemView.findViewById(R.id.group_msg_name);
            groupMsgNewmsg = itemView.findViewById(R.id.group_msg_newmsg);
            groupMsgDate = itemView.findViewById(R.id.group_msg_date);
            groupMsgDelete = itemView.findViewById(R.id.group_msg_delete);
            groupNewMsgCount = itemView.findViewById(R.id.group_item_new_msg);
        }
    }

    private ConversationAdapterCallBack callBack;

    public void setOnDeleteConversationCallBack(ConversationAdapterCallBack callBack) {
        this.callBack = callBack;
    }

}
