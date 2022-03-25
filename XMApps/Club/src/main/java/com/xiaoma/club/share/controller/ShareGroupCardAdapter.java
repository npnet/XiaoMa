package com.xiaoma.club.share.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.view.ActiveStarsView;
import com.xiaoma.club.common.view.MemberHeadsView;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/5/5 0005
 */

public class ShareGroupCardAdapter extends RecyclerView.Adapter<ShareGroupCardAdapter.GroupHolder> {

    private RequestManager requestManager;
    private List<GroupCardInfo> groupCardInfos;

    public ShareGroupCardAdapter(RequestManager requestManager, List<GroupCardInfo> groupCardInfos) {
        this.requestManager = requestManager;
        this.groupCardInfos = groupCardInfos;
    }

    public void refreshData(List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos != null) {
            this.groupCardInfos = groupCardInfos;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ShareGroupCardAdapter.GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_card_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new ShareGroupCardAdapter.GroupHolder(view);
        return (ShareGroupCardAdapter.GroupHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(ShareGroupCardAdapter.GroupHolder holder, int position) {
        if (groupCardInfos != null && !groupCardInfos.isEmpty()) {
            setItemData(holder, groupCardInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return groupCardInfos == null ? 0 : groupCardInfos.size();
    }

    private void setItemData(ShareGroupCardAdapter.GroupHolder holder, final GroupCardInfo cardInfo) {
        if (cardInfo == null) {
            return;
        }
        final Context context = holder.itemView.getContext();
        requestManager.load(cardInfo.getPicPath())
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                .into(holder.groupMainIcon);
        holder.groupName.setText(cardInfo.getNick());
        holder.groupSign.setText(cardInfo.getBrief());
        holder.groupLiveness.setGroupHot(cardInfo.getShowHotStars());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClubNetWorkUtils.isConnected(context)) {
                    return;
                }
                if (listener != null) {
                    listener.itemClick(cardInfo);
                }
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ShareGroupCardAdapter.GroupHolder holder) {
        super.onViewAttachedToWindow(holder);
        GroupCardInfo groupCardInfo = groupCardInfos.get(holder.getAdapterPosition());
        if (groupCardInfo != null) {
            holder.groupMembers.setHeadListWithUrl(groupCardInfo.getShowQunPics());
        }
    }

    class GroupHolder extends RecyclerView.ViewHolder {

        private ImageView groupMainIcon;
        private TextView groupName;
        private TextView groupSign;
        private ActiveStarsView groupLiveness;
        private MemberHeadsView groupMembers;

        public GroupHolder(View itemView) {
            super(itemView);
            groupMainIcon = itemView.findViewById(R.id.group_main_icon);
            groupName = itemView.findViewById(R.id.group_name);
            groupSign = itemView.findViewById(R.id.group_sign);
            groupLiveness = itemView.findViewById(R.id.group_liveness);
            groupMembers = itemView.findViewById(R.id.group_members);
        }
    }

    private OnGroupItemClickListener listener;

    public void setOnItemClickListener(OnGroupItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnGroupItemClickListener {
        void itemClick(GroupCardInfo info);
    }
}
