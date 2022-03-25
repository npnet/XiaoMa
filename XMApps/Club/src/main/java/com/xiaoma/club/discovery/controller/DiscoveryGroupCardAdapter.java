package com.xiaoma.club.discovery.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.view.ActiveStarsView;
import com.xiaoma.club.common.view.MemberHeadsView;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;

import java.util.List;


/**
 * Author: loren
 * Date: 2018/12/27 0027
 */

public class DiscoveryGroupCardAdapter extends RecyclerView.Adapter<DiscoveryGroupCardAdapter.GroupHolder> {
    private RequestManager requestManager;
    private List<GroupCardInfo> groupCardInfos;

    public DiscoveryGroupCardAdapter(RequestManager requestManager, List<GroupCardInfo> groupCardInfos) {
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
    public DiscoveryGroupCardAdapter.GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_card_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new DiscoveryGroupCardAdapter.GroupHolder(view);
        return (DiscoveryGroupCardAdapter.GroupHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscoveryGroupCardAdapter.GroupHolder holder, int position) {
        if (groupCardInfos != null && !groupCardInfos.isEmpty()) {
            setItemData(holder, groupCardInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return groupCardInfos == null ? 0 : groupCardInfos.size();
    }

    private void setItemData(DiscoveryGroupCardAdapter.GroupHolder holder, final GroupCardInfo cardInfo) {
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
                    listener.onProgressState(true);
                }
                //申请加群
                ClubRequestManager.requestAddGroup(cardInfo.getId(), new CallbackWrapper<ClubBaseResult>() {

                    @Override
                    public ClubBaseResult parse(String data) throws Exception {
                        return GsonHelper.fromJson(data, ClubBaseResult.class);
                    }

                    @Override
                    public void onSuccess(ClubBaseResult model) {
                        super.onSuccess(model);
                        if (listener != null) {
                            listener.onProgressState(false);
                        }
                        if (model != null && (model.isSuccess() || model.isRepeat())) {
                            ClubRepo.getInstance().getGroupRepo().insert(cardInfo);
                            //添加成功才能进入会话
                            ChatActivity.start(context, cardInfo.getHxGroupId(), true);
                            // 第一次进群,弹出Toast
                            if (!model.isRepeat()) {
                                XMToast.showToast(context, R.string.first_time_join_group_tips);
                                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.JOINGROUP.getType());
                            }
                        } else {
                            if (model != null) {
                                if (model.isKickOut() && listener != null) {
                                    listener.showKickOutDialog(cardInfo.getHxGroupId());
                                } else if (model.isDissolution()) {
                                    XMToast.showToast(context, R.string.group_dissolution);
                                } else {
                                    XMToast.showToast(context, R.string.add_group_failed);
                                }
                            } else {
                                XMToast.showToast(context, R.string.add_group_failed);
                            }
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
//                        XMToast.showToast(context, R.string.add_group_failed);
                        XMToast.toastException(context, R.string.net_work_error);
                        if (listener != null) {
                            listener.onProgressState(false);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull GroupHolder holder) {
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

    private OnProgressStateListener listener;

    public void setOnProgressStateListener(OnProgressStateListener listener) {
        this.listener = listener;
    }

    public interface OnProgressStateListener {
        void onProgressState(boolean isShow);

        void showKickOutDialog(String groupId);
    }
}
