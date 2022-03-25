package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/10/18 0018
 */

public class ImageMsgHolder extends BaseMsgHolder {
    public ImageView messageImageIcon;

    public ImageMsgHolder(View itemView, int childId) {
        super(itemView, childId);
        messageImageIcon = itemView.findViewById(R.id.message_image_image);
    }
}
