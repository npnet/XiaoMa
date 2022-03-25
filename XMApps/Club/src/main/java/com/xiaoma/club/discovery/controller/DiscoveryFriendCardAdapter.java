package com.xiaoma.club.discovery.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.model.User;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/27 0027
 */

public class DiscoveryFriendCardAdapter extends RecyclerView.Adapter<DiscoveryFriendCardAdapter.FriendCardHolder> {


    private Context context;
    private List<User> friendCardInfos;

    public DiscoveryFriendCardAdapter(Context context, List<User> friendCardInfos) {
        this.context = context;
        this.friendCardInfos = friendCardInfos;
    }

    public void refreshData(List<User> friendCardInfos) {
        if (friendCardInfos != null) {
            this.friendCardInfos = friendCardInfos;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FriendCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_card_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new FriendCardHolder(view);
        return (FriendCardHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendCardHolder holder, int position) {
        if (friendCardInfos != null && !friendCardInfos.isEmpty()) {
            setItemData(holder, friendCardInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return friendCardInfos == null ? 0 : friendCardInfos.size();
    }

    private void setItemData(FriendCardHolder holder, final User friendCardInfo) {
        if (friendCardInfo == null) {
            return;
        }
        holder.friendCardName.setText(friendCardInfo.getName());
        holder.friendCardAge.setText(friendCardInfo.getAge() + context.getString(R.string.personal_sui));
        holder.friendCardSign.setText(friendCardInfo.getPersonalSignature());
        try {
            Glide.with(context)
                    .load(TextUtils.isEmpty(friendCardInfo.getPicPath()) ? R.drawable.default_placeholder : friendCardInfo.getPicPath())
                    .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.friendMainIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClubNetWorkUtils.isConnected(context)) {
                    if (friendCardInfo.getId() != UserUtil.getCurrentUser().getId()) {
                        ChatActivity.start(v.getContext(), friendCardInfo.getHxAccount(), false);
                    }
                }
            }
        });
    }


    class FriendCardHolder extends RecyclerView.ViewHolder {

        private ImageView friendMainIcon;
        private TextView friendCardName;
        private TextView friendCardAge;
        private TextView friendCardSign;

        public FriendCardHolder(View itemView) {
            super(itemView);
            friendMainIcon = itemView.findViewById(R.id.friend_main_icon);
            friendCardName = itemView.findViewById(R.id.friend_card_name);
            friendCardAge = itemView.findViewById(R.id.friend_card_age);
            friendCardSign = itemView.findViewById(R.id.friend_card_sign);
        }
    }
}
