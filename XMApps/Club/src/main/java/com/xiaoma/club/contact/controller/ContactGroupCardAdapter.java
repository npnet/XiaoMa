package com.xiaoma.club.contact.controller;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.view.ActiveStarsView;
import com.xiaoma.club.common.view.MemberHeadsView;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.List;

import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.contactGroupFragment;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ContactGroupCardAdapter extends RecyclerView.Adapter<ContactGroupCardAdapter.GroupHolder> {
    private static final long ANIMATION_DURTION = 500;
    private List<GroupCardInfo> groupCardInfos;
    private int shakenPosition = -1;
    private RequestManager glideRequestManager;
    private final int roundedCornerRadius;

    public ContactGroupCardAdapter(Context context, RequestManager glideRequestManager, List<GroupCardInfo> groupCardInfos) {
        this.groupCardInfos = groupCardInfos;
        this.glideRequestManager = glideRequestManager;
        roundedCornerRadius = context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner);
    }

    public void refreshData(final List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    ContactGroupCardAdapter.this.groupCardInfos = groupCardInfos;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_card_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new GroupHolder(view);
        return (GroupHolder) viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return groupCardInfos.get(position).getId();
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        if (groupCardInfos != null && !groupCardInfos.isEmpty()) {
            setItemData(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return groupCardInfos == null ? 0 : groupCardInfos.size();
    }

    private void setItemData(GroupHolder holder, final int position) {
        final GroupCardInfo group = groupCardInfos.get(position);
        if (group == null) {
            return;
        }
        final Context context = holder.itemView.getContext();
        glideRequestManager.load(group.getPicPath())
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .transform(new SquareCornerLightTransForm(context, roundedCornerRadius))
                .into(holder.groupMainIcon);
        holder.groupName.setText(group.getNick());
        holder.groupSign.setText(group.getBrief());
        holder.groupBottom.setVisibility(View.VISIBLE);
        holder.groupLiveness.setGroupHot(group.getShowHotStars());
        if (shakenPosition == position) {
            startShaken(holder);
        } else {
            stopShaken(holder);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (shakenPosition == position) {
                    cleanShaken();
                    return true;
                }
                shakenPosition = position;
                notifyDataSetChanged();
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShakening()) {
                    cleanShaken();
                    return;
                }
                ChatActivity.start(v.getContext(), group.getHxGroupId(), true);
                XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.groupConctact,
                        group.getNick(), String.valueOf(group.getId()), "ContactGroupFragment",
                        contactGroupFragment);
            }
        });
        holder.exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanShaken();
                if (listener != null) {
                    listener.quitGroup(group.getId(), group.getHxGroupId());
                    XmAutoTracker.getInstance().onEvent(ClubEventConstants.NormalClick.exitGroup, group.getBrief(),
                            "ContactGroupFragment", ClubEventConstants.PageDescribe.contactGroupFragment);
                }
            }
        });
        holder.exitCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanShaken();
            }
        });
    }

    private void startShaken(GroupHolder holder) {
//        Animation animation = holder.itemView.getAnimation();
//        if (animation == null) {
//            animation = AnimationUtils.loadAnimation(context, R.anim.shaken);
//        }
//        holder.itemView.startAnimation(animation);
        showExit(holder);
    }

    private void showExit(GroupHolder holder) {
        holder.exitLl.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.exitLl, "translationY", holder.exitLl.getTranslationY(), -holder.exitLl.getLayoutParams().height);
        animator.setDuration(ANIMATION_DURTION);
        animator.start();
        holder.groupBottom.setVisibility(View.INVISIBLE);
    }

    private void hideExit(GroupHolder holder) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.exitLl, "translationY", holder.exitLl.getTranslationY(), holder.exitLl.getLayoutParams().height);
        animator.setDuration(ANIMATION_DURTION);
        animator.start();
        holder.groupBottom.setVisibility(View.VISIBLE);
    }

    private void stopShaken(GroupHolder holder) {
//        holder.itemView.clearAnimation();
        holder.exitLl.setVisibility(View.GONE);
        hideExit(holder);
    }

    public void cleanShaken() {
        shakenPosition = -1;
        notifyDataSetChanged();
    }

    public boolean isShakening() {
        return shakenPosition != -1;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull GroupHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == shakenPosition) {
            startShaken(holder);
        }
        GroupCardInfo groupCardInfo = groupCardInfos.get(holder.getAdapterPosition());
        if (groupCardInfo != null) {
            holder.groupMembers.setHeadListWithUrl(groupCardInfo.getShowQunPics());
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull GroupHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getAdapterPosition() == shakenPosition) {
            stopShaken(holder);
        }
    }

    class GroupHolder extends RecyclerView.ViewHolder {

        private ImageView groupMainIcon;
        private TextView groupName;
        private TextView groupSign;
        private RelativeLayout groupBottom;
        private ActiveStarsView groupLiveness;
        private MemberHeadsView groupMembers;
        private RelativeLayout exitLl;
        private Button exitBtn;
        private Button exitCancle;

        public GroupHolder(View itemView) {
            super(itemView);
            groupMainIcon = itemView.findViewById(R.id.group_main_icon);
            groupName = itemView.findViewById(R.id.group_name);
            groupSign = itemView.findViewById(R.id.group_sign);
            groupBottom = itemView.findViewById(R.id.group_bottom_rl);
            groupLiveness = itemView.findViewById(R.id.group_liveness);
            groupMembers = itemView.findViewById(R.id.group_members);
            exitLl = itemView.findViewById(R.id.group_exit_ll);
            exitBtn = itemView.findViewById(R.id.group_exit_btn);
            exitCancle = itemView.findViewById(R.id.group_exit_cancle);
        }
    }

    private QuitGroupListener listener;

    public void setQuitGroupListener(QuitGroupListener listener) {
        this.listener = listener;
    }

    public interface QuitGroupListener {
        void quitGroup(long groupId, String hxId);
    }
}
