package com.xiaoma.club.msg.chat.controller.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-1-2 0002.
 */
public class SystemNotifyMsgHolder extends RecyclerView.ViewHolder {
    public TextView tvNotifyContent;

    public SystemNotifyMsgHolder(View itemView) {
        super(itemView);
        tvNotifyContent = itemView.findViewById(R.id.tv_notify_content);
    }
}
