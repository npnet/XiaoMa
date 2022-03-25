package com.xiaoma.club.msg.chat.controller.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-1-3 0003.
 */
public class MsgTimeHolder extends RecyclerView.ViewHolder {
    public TextView tvMsgTime;

    public MsgTimeHolder(View itemView) {
        super(itemView);
        tvMsgTime = itemView.findViewById(R.id.tv_msg_time);
    }
}
