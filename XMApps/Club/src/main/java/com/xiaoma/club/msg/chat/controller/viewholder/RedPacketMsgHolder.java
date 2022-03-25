package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-4-15 0015.
 */
public class RedPacketMsgHolder extends BaseMsgHolder {
    public View containerPacket;
    public ImageView ivRpIcon;
    public TextView tvGreeting;
    public TextView tvRpType;
    public TextView tvPoiDisplay;

    public RedPacketMsgHolder(View itemView, int childId) {
        super(itemView, childId);
        //R.layout.item_receive_message_rp
        containerPacket = itemView.findViewById(R.id.container_packet);
        ivRpIcon = itemView.findViewById(R.id.iv_rp_icon);
        tvGreeting = itemView.findViewById(R.id.tv_greeting);
        tvRpType = itemView.findViewById(R.id.tv_rp_type);
        tvPoiDisplay = itemView.findViewById(R.id.tv_poi_display);
    }
}
