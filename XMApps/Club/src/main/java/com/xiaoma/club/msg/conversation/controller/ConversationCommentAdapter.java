package com.xiaoma.club.msg.conversation.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.conversation.model.CommentMsgInfo;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ConversationCommentAdapter extends RecyclerView.Adapter<ConversationCommentAdapter.CommentMsgHolder> {
    private Context context;
    private List<CommentMsgInfo> commentMsgInfos;

    public ConversationCommentAdapter(Context context, List<CommentMsgInfo> commentMsgInfos) {
        this.context = context;
        this.commentMsgInfos = commentMsgInfos;
    }

    @Override
    public CommentMsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_msg_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new CommentMsgHolder(view);
        return (CommentMsgHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentMsgHolder holder, int position) {
        if (commentMsgInfos != null && !commentMsgInfos.isEmpty()) {
            setItemData(holder, commentMsgInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return commentMsgInfos == null ? 0 : commentMsgInfos.size();
    }

    private void setItemData(CommentMsgHolder holder, CommentMsgInfo commentMsgInfo) {

        holder.commentFriendName.setText(commentMsgInfo.getCommentFriendName() + context.getString(R.string.comment_your_share));
        holder.commentNewMsg.setText(commentMsgInfo.getCommentNewMsg());
        holder.commentShareTitle.setText(commentMsgInfo.getShareTitle());
        holder.commentShareContent.setText(commentMsgInfo.getShareContent());

    }


    class CommentMsgHolder extends RecyclerView.ViewHolder {

        private ImageView commentFriendIcon;
        private TextView commentFriendName;
        private TextView commentNewMsg;
        private TextView commentMsgDate;
        private ImageView commentMsgDelete;
        private ImageView commentShareIcon;
        private TextView commentShareTitle;
        private TextView commentShareContent;
        private TextView commentShareDate;

        public CommentMsgHolder(View itemView) {
            super(itemView);
            commentFriendIcon = itemView.findViewById(R.id.comment_friend_icon);
            commentFriendName = itemView.findViewById(R.id.comment_friend_name);
            commentNewMsg = itemView.findViewById(R.id.comment_new_msg);
            commentMsgDate = itemView.findViewById(R.id.comment_msg_date);
            commentMsgDelete = itemView.findViewById(R.id.comment_msg_delete);
            commentShareIcon = itemView.findViewById(R.id.comment_share_icon);
            commentShareTitle = itemView.findViewById(R.id.comment_share_title);
            commentShareContent = itemView.findViewById(R.id.comment_share_content);
            commentShareDate = itemView.findViewById(R.id.comment_share_date);
        }
    }
}
