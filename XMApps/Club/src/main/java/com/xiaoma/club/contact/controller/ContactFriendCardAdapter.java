package com.xiaoma.club.contact.controller;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.image.transformation.SquareCornerLightTransForm;
import com.xiaoma.model.User;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.List;

import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.contactFriendFragment;

/**
 * Author: loren
 * Date: 2018/10/11 0011
 */

public class ContactFriendCardAdapter extends RecyclerView.Adapter<ContactFriendCardAdapter.FriendCardHolder> {

    private static final long ANIMATION_DURTION = 500;
    private Context context;
    private List<User> friendCardInfos;
    private int shakenPosition = -1;

    public ContactFriendCardAdapter(Context context, List<User> friendCardInfos) {
        this.context = context;
        this.friendCardInfos = friendCardInfos;
    }

    public void refreshData(final List<User> friendCardInfos) {
        if (friendCardInfos != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    ContactFriendCardAdapter.this.friendCardInfos = friendCardInfos;
                    notifyDataSetChanged();
                }
            });
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
            setItemData(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return friendCardInfos == null ? 0 : friendCardInfos.size();
    }

    private void setItemData(FriendCardHolder holder, final int position) {
        final User friend = friendCardInfos.get(position);
        if (friend == null) {
            return;
        }
        holder.friendCardName.setText(friend.getName());
        holder.friendCardAge.setText(friend.getAge() + context.getString(R.string.personal_sui));
        holder.friendCardSign.setVisibility(View.VISIBLE);
        holder.friendCardSign.setText(friend.getPersonalSignature());
        try {
            Glide.with(context)
                    .load(TextUtils.isEmpty(friend.getPicPath()) ? R.drawable.default_placeholder : friend.getPicPath())
                    .transform(new SquareCornerLightTransForm(context, context.getResources().getDimensionPixelOffset(R.dimen.group_friend_card_corner)))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.friendMainIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                ChatActivity.start(v.getContext(), friend.getHxAccount(), false);
                XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.groupConctact,
                        friend.getName(), String.valueOf(friend.getId()), "ContactFriendFragment",
                        contactFriendFragment);
            }
        });
        holder.exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanShaken();
                if (listener != null) {
                    listener.deleteFriend(String.valueOf(friend.getId()));
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

    private void startShaken(FriendCardHolder holder) {
//        Animation animation = holder.itemView.getAnimation();
//        if (animation == null) {
//            animation = AnimationUtils.loadAnimation(context, R.anim.shaken);
//        }
//        holder.itemView.startAnimation(animation);
        showExit(holder);
    }

    private void showExit(FriendCardHolder holder) {
        holder.exitLl.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.exitLl, "translationY", holder.exitLl.getTranslationY(), -holder.exitLl.getLayoutParams().height * 1.5f);
        animator.setDuration(ANIMATION_DURTION);
        animator.start();
        holder.friendCardSign.setVisibility(View.INVISIBLE);
    }

    private void hideExit(FriendCardHolder holder) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.exitLl, "translationY", holder.exitLl.getTranslationY(), holder.exitLl.getLayoutParams().height * 1.5f);
        animator.setDuration(ANIMATION_DURTION);
        animator.start();
        holder.friendCardSign.setVisibility(View.VISIBLE);
    }

    private void stopShaken(FriendCardHolder holder) {
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
    public void onViewAttachedToWindow(@NonNull FriendCardHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == shakenPosition) {
            startShaken(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull FriendCardHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getAdapterPosition() == shakenPosition) {
            stopShaken(holder);
        }
    }

    class FriendCardHolder extends RecyclerView.ViewHolder {

        private ImageView friendMainIcon;
        private TextView friendCardName;
        private TextView friendCardAge;
        private TextView friendCardSign;
        private RelativeLayout exitLl;
        private Button exitBtn;
        private Button exitCancle;

        public FriendCardHolder(View itemView) {
            super(itemView);
            friendMainIcon = itemView.findViewById(R.id.friend_main_icon);
            friendCardName = itemView.findViewById(R.id.friend_card_name);
            friendCardAge = itemView.findViewById(R.id.friend_card_age);
            friendCardSign = itemView.findViewById(R.id.friend_card_sign);
            exitLl = itemView.findViewById(R.id.friend_exit_ll);
            exitBtn = itemView.findViewById(R.id.friend_exit_btn);
            exitCancle = itemView.findViewById(R.id.friend_exit_cancle);
        }
    }

    private DeleteFriendListener listener;

    public void setDeleteFriendListener(DeleteFriendListener listener) {
        this.listener = listener;
    }

    public interface DeleteFriendListener {
        void deleteFriend(String otherUserId);
    }
}
