package com.xiaoma.club.contact.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.common.view.UserHeadView;
import com.xiaoma.club.contact.model.NewFriendInfo;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/13 0017
 */

public class NewFriendAdapter extends RecyclerView.Adapter<NewFriendAdapter.NewFriendHolder> {

    private Context context;
    private List<NewFriendInfo> newFriendInfos;

    public NewFriendAdapter(Context context, List<NewFriendInfo> newFriendInfos) {
        this.context = context;
        this.newFriendInfos = newFriendInfos;
    }

    @Override
    public NewFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_friend_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new NewFriendHolder(view);
        return (NewFriendHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(NewFriendHolder holder, int position) {
        if (newFriendInfos != null && !newFriendInfos.isEmpty()) {
            setItemData(holder, newFriendInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return newFriendInfos == null ? 0 : newFriendInfos.size();
    }

    private void setItemData(NewFriendHolder holder, final NewFriendInfo friendCardInfo) {

        holder.newFriend.setUserHeadByHxid(friendCardInfo.getFromHxAccount());

        holder.newFriendName.setText(friendCardInfo.getFromName());
        ViewUtils.setDateText(context, holder.newFriendDate, friendCardInfo.getCreateDate());
        holder.newFriendMsg.setText(context.getString(R.string.become_my_friend));
        if (friendCardInfo.isApproved()) {
            holder.newFriendApproveBtn.setText(R.string.has_agreed);
            holder.newFriendApproveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.singleChat(friendCardInfo.getFromHxAccount());
                    }
                }
            });
        } else {
            holder.newFriendApproveBtn.setText(R.string.agree);
            holder.newFriendApproveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.approve(friendCardInfo);
                    }
                }
            });
        }
    }

    public void refreshData(List<NewFriendInfo> data) {
        if (data != null) {
            this.newFriendInfos = data;
            notifyDataSetChanged();
        }
    }


    class NewFriendHolder extends RecyclerView.ViewHolder {

        private UserHeadView newFriend;
        private TextView newFriendDate;
        private TextView newFriendName;
        private TextView newFriendMsg;
        private Button newFriendApproveBtn;

        public NewFriendHolder(View itemView) {
            super(itemView);
            newFriend = itemView.findViewById(R.id.new_friend_user);
            newFriendDate = itemView.findViewById(R.id.new_friend_date);
            newFriendName = itemView.findViewById(R.id.new_friend_name);
            newFriendMsg = itemView.findViewById(R.id.new_friend_msg);
            newFriendApproveBtn = itemView.findViewById(R.id.new_friend_approve_btn);
        }
    }

    private ApproveClickListener listener;

    public void setApproveClickListener(ApproveClickListener listener) {
        this.listener = listener;
    }

    public interface ApproveClickListener {
        void approve(NewFriendInfo friendInfo);

        void singleChat(String hxId);
    }
}
