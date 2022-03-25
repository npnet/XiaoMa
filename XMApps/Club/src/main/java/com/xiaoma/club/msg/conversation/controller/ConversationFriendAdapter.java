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
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.club.msg.conversation.model.ConversationAdapterCallBack;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.model.User;

import java.util.List;

import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.friendConversationFragment;
import static com.xiaoma.club.msg.conversation.util.ConversationUtil.getMessageDate;
import static com.xiaoma.club.msg.conversation.util.ConversationUtil.getNewMsg;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ConversationFriendAdapter extends RecyclerView.Adapter<ConversationFriendAdapter.FriendMsgHolder> {
    private RequestManager requestManager;
    private List<EMConversation> friendMsgInfos;

    public ConversationFriendAdapter(RequestManager requestManager, List<EMConversation> friendMsgInfos) {
        this.requestManager = requestManager;
        this.friendMsgInfos = friendMsgInfos;
    }

    @NonNull
    @Override
    public FriendMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_msg_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new FriendMsgHolder(view);
        return (FriendMsgHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendMsgHolder holder, int position) {
        if (friendMsgInfos != null && !friendMsgInfos.isEmpty()) {
            setItemData(holder, friendMsgInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return friendMsgInfos == null ? 0 : friendMsgInfos.size();
    }

    private void setItemData(FriendMsgHolder holder, final EMConversation friendMsgInfo) {
        final Context context = holder.itemView.getContext();
        String friendName = "";
        final User friend = ClubRepo.getInstance().getUserRepo().getByKey(friendMsgInfo.conversationId());
        if (friend != null) {
            friendName = friend.getName();
        } else {
            if (callBack != null) {
                callBack.onFetchModel(friendMsgInfo.conversationId());
            }
        }
        try {
            final String picPath = friend != null ? friend.getPicPath() : "";
            requestManager.load(picPath)
                    .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.friendMsgIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.friendMsgName.setText(friendName);
        holder.friendMsgNewmsg.setText(getNewMsg(context, friendMsgInfo));
        if (friendMsgInfo.getLastMessage() != null) {
            holder.friendMsgDate.setText(getMessageDate(context, friendMsgInfo.getLastMessage().getMsgTime()));
        }
        ViewUtils.setMsgCounts(holder.friendNewMsgCount, friendMsgInfo.getUnreadMsgCount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendMsgInfo.markAllMessagesAsRead();
                ChatActivity.start(v.getContext(), friendMsgInfo.conversationId(), false);
                if (friend != null) {
                    XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.chatWithFriend,
                            friend.getName(), String.valueOf(friend.getId()), "ConversationFriendFragment",
                            friendConversationFragment);
                }
            }
        });
        final String finalFriendName = friendName;
        holder.friendMsgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.delete(friendMsgInfo.conversationId(), finalFriendName);
                    XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.deleteConversation,
                            friend.getName(), String.valueOf(friend.getId()), "ConversationFriendFragment",
                            friendConversationFragment);
                }
            }
        });

    }

    public void refreshData(List<EMConversation> msgInfos) {
        if (msgInfos != null) {
            this.friendMsgInfos.clear();
            this.friendMsgInfos.addAll(msgInfos);
            notifyDataSetChanged();
        }
    }


    static class FriendMsgHolder extends RecyclerView.ViewHolder {

        private ImageView friendMsgIcon;
        private TextView friendMsgName;
        private TextView friendMsgNewmsg;
        private TextView friendMsgDate;
        private ImageView friendMsgDelete;
        private TextView friendNewMsgCount;

        FriendMsgHolder(View itemView) {
            super(itemView);
            friendMsgIcon = itemView.findViewById(R.id.friend_msg_icon);
            friendMsgName = itemView.findViewById(R.id.friend_msg_name);
            friendMsgNewmsg = itemView.findViewById(R.id.friend_msg_newmsg);
            friendMsgDate = itemView.findViewById(R.id.friend_msg_date);
            friendMsgDelete = itemView.findViewById(R.id.friend_msg_delete);
            friendNewMsgCount = itemView.findViewById(R.id.friend_item_new_msg);
        }
    }

    private ConversationAdapterCallBack callBack;

    public void setOnDeleteConversationCallBack(ConversationAdapterCallBack callBack) {
        this.callBack = callBack;
    }
}
