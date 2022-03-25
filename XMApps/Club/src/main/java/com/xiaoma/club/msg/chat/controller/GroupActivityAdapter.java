package com.xiaoma.club.msg.chat.controller;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.UploadGroupScoreManager;
import com.xiaoma.club.common.util.ViewUtils;
import com.xiaoma.club.msg.chat.model.GroupActivityInfo;
import com.xiaoma.club.msg.chat.ui.GroupActivityWeb;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupActivityAdapter extends RecyclerView.Adapter<GroupActivityAdapter.GroupActivityHolder> {

    private Context context;
    private List<GroupActivityInfo> activityInfos;

    public GroupActivityAdapter(Context context, List<GroupActivityInfo> activityInfos) {
        this.context = context;
        this.activityInfos = activityInfos;
    }

    public void refreshData(List<GroupActivityInfo> infos) {
        if (infos != null && !infos.isEmpty()) {
            this.activityInfos = infos;
            notifyDataSetChanged();
        }
    }

    @Override
    public GroupActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_activity_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new GroupActivityHolder(view);
        return (GroupActivityHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupActivityHolder holder, int position) {
        if (activityInfos != null && !activityInfos.isEmpty()) {
            setItemData(holder, activityInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return activityInfos == null ? 0 : activityInfos.size();
    }

    private void setItemData(final GroupActivityHolder holder, final GroupActivityInfo activityInfo) {
        if (activityInfo == null) {
            return;
        }
        try {
            Glide.with(context)
                    .load(activityInfo == null ? R.drawable.default_placeholder : activityInfo.getNoticePic())
                    .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.groupActivityItemIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.groupActivityItemTitle.setText(activityInfo.getNoticeName());
        holder.groupActivityItemContent.setText(activityInfo.getContent());
        ViewUtils.setGroupActivityDate(context, holder.groupActivityItemDate, activityInfo.getBeginTime(), activityInfo.getEndTime());
        setActivityStatus(holder.groupActivityItemState, activityInfo.getOnLineFlag());
        if (activityInfo.getOnLineFlag().equals(ClubConstant.GroupActivity.ACTIVITY_END)) {
            holder.groupActivityItemTitle.setTextColor(context.getColor(R.color.text_sign_number));
            holder.groupActivityItemContent.setTextColor(context.getColor(R.color.text_sign_number));
            holder.groupActivityItemDate.setTextColor(context.getColor(R.color.text_sign_number));
            holder.groupActivityItemState.setTextColor(context.getColor(R.color.text_sign_number));
        } else {
            holder.groupActivityItemTitle.setTextColor(context.getColor(R.color.club_white));
            holder.groupActivityItemContent.setTextColor(context.getColor(R.color.club_white));
            holder.groupActivityItemDate.setTextColor(context.getColor(R.color.club_white));
            holder.groupActivityItemState.setTextColor(context.getColor(R.color.text_activity_state_item));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(activityInfo.getLinkUrl()) && !activityInfo.getOnLineFlag().equals(ClubConstant.GroupActivity.ACTIVITY_END)) {
                    if (ClubNetWorkUtils.isConnected(context)) {
//                        GroupActivityWeb.start(context, activityInfo.getLinkUrl());
                        Uri uri = Uri.parse(activityInfo.getLinkUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                        UploadGroupScoreManager.getInstance().uploadGroupActivity(String.valueOf(activityInfo.getQunId()), GsonHelper.toJson(activityInfo));
                    }
                }
            }
        });
    }

    private void setActivityStatus(TextView groupActivityItemState, String onLineFlag) {
        if (onLineFlag.equals(ClubConstant.GroupActivity.ACTIVITY_END)) {
            groupActivityItemState.setText(context.getString(R.string.activity_end));
        } else if (onLineFlag.equals(ClubConstant.GroupActivity.ACTIVITY_ONGOING)) {
            groupActivityItemState.setText(context.getString(R.string.activity_playing));
        } else if (onLineFlag.equals(ClubConstant.GroupActivity.ACTIVITY_FUTURE)) {
            groupActivityItemState.setText(context.getString(R.string.activity_not_start));
        }
    }


    class GroupActivityHolder extends RecyclerView.ViewHolder {

        private ImageView groupActivityItemIcon;
        private TextView groupActivityItemTitle;
        private TextView groupActivityItemContent;
        private TextView groupActivityItemDate;
        private TextView groupActivityItemState;

        public GroupActivityHolder(View itemView) {
            super(itemView);
            groupActivityItemIcon = itemView.findViewById(R.id.group_activity_item_icon);
            groupActivityItemTitle = itemView.findViewById(R.id.group_activity_item_title);
            groupActivityItemContent = itemView.findViewById(R.id.group_activity_item_content);
            groupActivityItemDate = itemView.findViewById(R.id.group_activity_item_date);
            groupActivityItemState = itemView.findViewById(R.id.group_activity_item_state);
        }
    }

}
