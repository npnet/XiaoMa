package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2019/5/5 0005
 */

public class ShareMsgHolder extends BaseMsgHolder {

    public View containerShare;
    public ImageView ivShareIcon;
    public TextView tvShareTitle;
    public TextView tvShareContent;

    public ShareMsgHolder(View itemView, int childId) {
        super(itemView, childId);
        //R.layout.item_receive_message_share
        containerShare = itemView.findViewById(R.id.msg_share_contain);
        ivShareIcon = itemView.findViewById(R.id.msg_share_img);
        tvShareTitle = itemView.findViewById(R.id.msg_share_title);
        tvShareContent = itemView.findViewById(R.id.msg_share_content);
    }
}
