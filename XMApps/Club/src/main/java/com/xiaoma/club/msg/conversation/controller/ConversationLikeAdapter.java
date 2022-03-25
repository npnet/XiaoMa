package com.xiaoma.club.msg.conversation.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.conversation.model.LikeMsgInfo;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ConversationLikeAdapter extends RecyclerView.Adapter<ConversationLikeAdapter.LikeMsgHolder> {
    private Context context;
    private List<LikeMsgInfo> likeMsgInfos;

    public ConversationLikeAdapter(Context context, List<LikeMsgInfo> likeMsgInfos) {
        this.context = context;
        this.likeMsgInfos = likeMsgInfos;
    }

    @Override
    public LikeMsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_like_msg_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new LikeMsgHolder(view);
        return (LikeMsgHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(LikeMsgHolder holder, int position) {
        if (likeMsgInfos != null && !likeMsgInfos.isEmpty()) {
            setItemData(holder, likeMsgInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return likeMsgInfos == null ? 0 : likeMsgInfos.size();
    }

    private void setItemData(LikeMsgHolder holder, LikeMsgInfo likeMsgInfo) {

        holder.likeFriendName.setText(likeMsgInfo.getLikeFriendName() + context.getString(R.string.like_your_share));
        holder.likeShareTitle.setText(likeMsgInfo.getLikeShareTitle());
        holder.likeShareContent.setText(likeMsgInfo.getLikeShareContent());

    }


    class LikeMsgHolder extends RecyclerView.ViewHolder {

        private ImageView likeFriendIcon;
        private TextView likeFriendName;
        private TextView likeMsgDate;
        private ImageView likeMsgDelete;
        private ImageView likeShareIcon;
        private TextView likeShareTitle;
        private TextView likeShareContent;
        private TextView likeShareDate;

        public LikeMsgHolder(View itemView) {
            super(itemView);
            likeFriendIcon = itemView.findViewById(R.id.like_friend_icon);
            likeFriendName = itemView.findViewById(R.id.like_friend_name);
            likeMsgDate = itemView.findViewById(R.id.like_msg_date);
            likeMsgDelete = itemView.findViewById(R.id.like_msg_delete);
            likeShareIcon = itemView.findViewById(R.id.like_share_icon);
            likeShareTitle = itemView.findViewById(R.id.like_share_title);
            likeShareContent = itemView.findViewById(R.id.like_share_content);
            likeShareDate = itemView.findViewById(R.id.like_share_date);
        }
    }
}
