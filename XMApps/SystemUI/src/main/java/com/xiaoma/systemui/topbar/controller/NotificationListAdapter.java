package com.xiaoma.systemui.topbar.controller;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.util.DateTimeUtil;
import com.xiaoma.systemui.topbar.model.NotificationModelHelper;
import com.xiaoma.systemui.topbar.view.SlideLayout;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.xiaoma.systemui.topbar.model.NotificationModelHelper.getNotificationTime;
import static com.xiaoma.systemui.topbar.model.NotificationModelHelper.hasCustomContent;

/**
 * Created by LKF on 2018/11/6 0006.
 */
public class NotificationListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "NotificationListAdapter";
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_REMOTE_VIEW = 1;
    private NotificationModelHelper mNotificationModelHelper;
    private Callback mCallback;
    private SlideLayout mLastSlide;
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (RecyclerView.SCROLL_STATE_IDLE != newState) {
                if (mLastSlide != null && mLastSlide.isMenuOpen()) {
                    mLastSlide.smoothCloseMenu();
                    mLastSlide = null;
                }
            }
        }
    };

    NotificationListAdapter(NotificationModelHelper notificationModelHelper, Callback callback) {
        mNotificationModelHelper = notificationModelHelper;
        mCallback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(mScrollListener);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                holder = new NotificationNormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_normal, parent, false));
                break;
            case VIEW_TYPE_REMOTE_VIEW:
                holder = new NotificationRemoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_remoteview, parent, false));
                break;
            default:
                holder = new RecyclerView.ViewHolder(new View(parent.getContext())) {};
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final StatusBarNotification sbn = mNotificationModelHelper.getDisplayingNotifications().get(position);
        bindCommon((NotificationRemoteViewHolder) holder, position, sbn);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_NORMAL:
                bindNormalNotification((NotificationNormalHolder) holder, sbn);
                break;
            case VIEW_TYPE_REMOTE_VIEW:
                bindRemoteViewNotification((NotificationRemoteViewHolder) holder, sbn);
                break;
        }
    }

    private void bindCommon(final NotificationRemoteViewHolder holder, int position, final StatusBarNotification sbn) {
        final Context context = holder.itemView.getContext();
        final SlideLayout slideLayout = holder.notificationItemSlideContainer;
        if (slideLayout.isMenuOpen()) {
            slideLayout.smoothCloseMenu();
        }
        slideLayout.setSlideChangeListener(new SlideLayout.SlideChangeListener() {
            @Override
            public void onMenuOpen(SlideLayout parent) {
                if (mLastSlide == null) {
                    mLastSlide = parent;
                } else if (mLastSlide != parent) {
                    mLastSlide.smoothCloseMenu();
                    mLastSlide = parent;
                }
                /*if (!sbn.isClearable()) {
                    parent.smoothCloseMenu();
                    SysUIToast.makeTextAndShow(parent.getContext(),R.string.notification_cannot_remove_tips, Toast.LENGTH_SHORT);
                }*/
            }

            @Override
            public void onMenuClose(SlideLayout parent) {
                if (mLastSlide == parent) {
                    mLastSlide = null;
                }
            }
        });


        final boolean isFirstGroupItem = mNotificationModelHelper.isFirstGroupItem(sbn);
        // 应用分组
        if (isFirstGroupItem) {
            holder.collapseItemContainer.setVisibility(View.VISIBLE);
            final String appName = AppUtils.getAppName(context, sbn.getPackageName());
            holder.tvAppName.setText(StringUtil.optString(appName));
            if (mNotificationModelHelper.isGroupCollapse(sbn)) {
                holder.tvCollapseState.setText(R.string.notification_expanding);
                holder.ivArrow.setRotation(0);
                holder.collapseItemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onGroupExpand(sbn);
                        }
                    }
                });
            } else {
                holder.tvCollapseState.setText(R.string.notification_collapsing);
                holder.ivArrow.setRotation(180);
                holder.collapseItemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onGroupCollapse(sbn);
                        }
                    }
                });
            }
        } else {
            holder.collapseItemContainer.setVisibility(View.GONE);
        }

        final boolean isShowDelAll = isFirstGroupItem
                && mNotificationModelHelper.isGroupCollapse(sbn);
        // 删除按钮
        if (isShowDelAll) {
            holder.tvDelItem.setText(R.string.notification_item_btn_del_all);
        } else {
            holder.tvDelItem.setText(R.string.notification_item_btn_del);
        }

        // 通知点击 + 删除点击
        slideLayout.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.notification_item_container:
                        if (slideLayout.isMenuOpen()) {
                            slideLayout.smoothCloseMenu();
                        } else {
                            if (mCallback != null) {
                                mCallback.onNotificationClick(sbn);
                            }
                        }
                        break;
                    case R.id.tv_del_item:
                        holder.notificationItemSlideContainer.smoothCloseMenu();
                        if (mCallback != null) {
                            if (isShowDelAll) {
                                mCallback.onCancelNotifications(mNotificationModelHelper.getGroupNotifications(sbn));
                            } else {
                                mCallback.onCancelNotification(sbn);
                            }
                        }
                        break;
                }
                if (slideLayout.isMenuOpen()) {
                    slideLayout.smoothCloseMenu();
                } else {
                    if (mCallback != null) {
                        mCallback.onNotificationClick(sbn);
                    }
                }
            }
        });
        // 时间轴
        boolean showTimeline;
        if (position == 0) {
            showTimeline = true;
        } else {
            final StatusBarNotification lastSbn = mNotificationModelHelper.getDisplayingNotifications().get(position - 1);
            showTimeline = !DateTimeUtil.isSameDay(getNotificationTime(lastSbn), getNotificationTime(sbn));
        }
        if (showTimeline) {
            holder.notificationItemTimelineContainer.setVisibility(View.VISIBLE);
            final String timeDesc = getTimelineDesc(context, getNotificationTime(sbn));
            holder.tvDateDesc.setText(timeDesc);
            final String dateDisplay = getTimelineDateDisplay(context, getNotificationTime(sbn));
            holder.tvDate.setText(dateDisplay);
            holder.btnDelCurrDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onCancelNotificationsByDate(new Date(getNotificationTime(sbn)));
                    }
                }
            });
        } else {
            holder.notificationItemTimelineContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void bindRemoteViewNotification(NotificationRemoteViewHolder holder, StatusBarNotification sbn) {
        final Notification notification = sbn.getNotification();
        final Context context = holder.itemView.getContext();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            remoteViews = notification.bigContentView;
        }
        final ViewGroup itemContainer = (ViewGroup) holder.notificationItemContainer;
        itemContainer.removeAllViewsInLayout();
        if (remoteViews != null) {
            itemContainer.setVisibility(View.VISIBLE);
            final View itemContent = remoteViews.apply(context, itemContainer);
            itemContainer.addView(itemContent);
            bindNotificationBg(holder, itemContent, sbn);
        } else {
            itemContainer.setVisibility(View.GONE);
        }
    }

    private void bindNormalNotification(NotificationNormalHolder holder, StatusBarNotification sbn) {
        bindNotificationBg(holder, holder.notificationItemContainer, sbn);

        final Context context = holder.itemView.getContext();
        final Notification notification = sbn.getNotification();
        final Bundle extras = notification.extras;
        holder.tvTitle.setText(extras.getString(NotificationCompat.EXTRA_TITLE, ""));
        holder.tvText.setText(extras.getString(NotificationCompat.EXTRA_TEXT, ""));
        // 需求变更: 固定24小时制
        SimpleDateFormat df = new SimpleDateFormat("HH:mm",Locale.getDefault());
        holder.tvTime.setText(df.format(getNotificationTime(sbn)));
        //holder.tvTime.setText(BarUtil.getNotificationItemTimeDisplay(context, getNotificationTime(sbn)));
        Drawable iconDr = null;
        Icon icon;
        if ((icon = notification.getLargeIcon()) == null) {
            icon = notification.getSmallIcon();
        }
        if (icon != null) {
            iconDr = icon.loadDrawable(context);
        }
        ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;
        if (iconDr != null) {
            final int iconW = iconDr.getIntrinsicWidth();
            final int iconH = iconDr.getIntrinsicHeight();
            final int iconSize = Math.max(iconW, iconH);
            final int viewSize = context.getResources().getDimensionPixelSize(R.dimen.notification_item_icon_size);
            if (iconSize >= viewSize) {
                scaleType = ImageView.ScaleType.CENTER_INSIDE;
            }
        }
        holder.ivIcon.setScaleType(scaleType);
        holder.ivIcon.setImageDrawable(iconDr);
    }

    private void bindNotificationBg(NotificationRemoteViewHolder holder, View contentContainer, StatusBarNotification sbn) {
        final Context context = holder.itemView.getContext();
        final Resources res = context.getResources();
        final @DrawableRes int contentBg;
        if (mNotificationModelHelper.isFirstGroupItem(sbn)
                && mNotificationModelHelper.isGroupCollapse(sbn)) {
            final List<StatusBarNotification> groupMates = mNotificationModelHelper.getGroupNotifications(sbn);
            if (groupMates == null || groupMates.size() <= 2) {
                contentBg = R.drawable.notification_item_collapsing_2_bg;
            } else {
                contentBg = R.drawable.notification_item_collapsing_3_bg;
            }
        } else {
            contentBg = R.drawable.notification_item_bg;
        }
        final Drawable contentBgDr = context.getDrawable(contentBg);
        final ViewGroup.LayoutParams contentLp = contentContainer.getLayoutParams();
        contentLp.width = contentBgDr.getIntrinsicWidth();
        contentLp.height = contentBgDr.getIntrinsicHeight();
        holder.notificationItemContainer.setBackground(contentBgDr);
        holder.notificationItemSlideContainer.getLayoutParams().width = contentBgDr.getIntrinsicWidth() +
                res.getDimensionPixelSize(R.dimen.notification_item_del_width);
    }

    @Override
    public int getItemCount() {
        return mNotificationModelHelper.getDisplayingNotifications().size();
    }

    @Override
    public int getItemViewType(int position) {
        final StatusBarNotification sbn = mNotificationModelHelper.getDisplayingNotifications().get(position);
        final Notification n = sbn.getNotification();
        if (hasCustomContent(n)) {
            return VIEW_TYPE_NORMAL;
        }
        return VIEW_TYPE_REMOTE_VIEW;
    }

    private static String getTimelineDesc(Context context, long time) {
        final Date today = new Date();
        final Date itemDate = new Date(time);
        if (DateTimeUtil.isSameDay(itemDate, today)) {
            return context.getString(R.string.timeline_desc_today);
        }
        if (today.after(itemDate)) {
            return context.getString(R.string.timeline_desc_before);
        }
        return context.getString(R.string.timeline_desc_after);
    }

    private static String getTimelineDateDisplay(Context context, long time) {
        final Date today = new Date();
        final Date itemDate = new Date(time);
        if (itemDate.getYear() == today.getYear()) {
            return new SimpleDateFormat(context.getString(R.string.timeline_date_display_same_year), Locale.CHINA).format(itemDate);
        }
        return new SimpleDateFormat(context.getString(R.string.timeline_date_display_different_year), Locale.CHINA).format(itemDate);
    }

    public interface Callback {
        void onNotificationClick(StatusBarNotification sbn);

        void onCancelNotification(StatusBarNotification sbn);

        void onCancelNotifications(List<StatusBarNotification> sbnList);

        void onCancelNotificationsByDate(Date date);

        void onGroupExpand(StatusBarNotification sbn);

        void onGroupCollapse(StatusBarNotification sbn);
    }

    static class NotificationRemoteViewHolder extends RecyclerView.ViewHolder {

        View notificationItemTimelineContainer;
        ImageView ivDot;
        TextView tvDateDesc;
        TextView tvDate;
        ImageButton btnDelCurrDate;
        View collapseItemContainer;
        TextView tvAppName;
        TextView tvCollapseState;
        ImageView ivArrow;
        TextView tvDelItem;
        SlideLayout notificationItemSlideContainer;
        View notificationItemContainer;

        NotificationRemoteViewHolder(View itemView) {
            super(itemView);
            //R.layout.item_notification_remoteview
            ivDot = itemView.findViewById(R.id.iv_dot);
            notificationItemTimelineContainer = itemView.findViewById(R.id.notification_item_timeline_container);
            tvDateDesc = itemView.findViewById(R.id.tv_date_desc);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnDelCurrDate = itemView.findViewById(R.id.btn_del_curr_date);
            collapseItemContainer = itemView.findViewById(R.id.collapse_item_container);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvCollapseState = itemView.findViewById(R.id.tv_collapse_state);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            tvDelItem = itemView.findViewById(R.id.tv_del_item);
            notificationItemSlideContainer = itemView.findViewById(R.id.notification_item_slide_container);
            notificationItemContainer = itemView.findViewById(R.id.notification_item_container);
        }
    }

    static class NotificationNormalHolder extends NotificationRemoteViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvTime;
        TextView tvText;

        NotificationNormalHolder(View itemView) {
            super(itemView);
            //R.layout.item_notification_normal
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvText = itemView.findViewById(R.id.tv_text);
        }
    }
}