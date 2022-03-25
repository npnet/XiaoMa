package com.xiaoma.club.msg.chat.controller.viewholder;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/10/17 0017
 */

public class BaseMsgHolder extends RecyclerView.ViewHolder {
    public ImageView ivStatus;
    public TextView msgUserName;
    public ImageView msgUserImg;
    public TextView tvOccupy;
    public ViewGroup msgContainer;

    public BaseMsgHolder(View itemView, @LayoutRes int childId) {
        super(itemView);
        //R.layout.item_send_container
        ivStatus = itemView.findViewById(R.id.iv_status);
        msgUserName = itemView.findViewById(R.id.msg_user_name);
        msgUserImg = itemView.findViewById(R.id.msg_user_img);
        tvOccupy = itemView.findViewById(R.id.tv_occupy);
        msgContainer = itemView.findViewById(R.id.msg_container);
        msgContainer.removeAllViewsInLayout();
        View.inflate(msgContainer.getContext(), childId, msgContainer);
    }
}
