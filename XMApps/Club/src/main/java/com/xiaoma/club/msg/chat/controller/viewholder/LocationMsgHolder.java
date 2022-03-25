package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/10/18 0018
 */

public class LocationMsgHolder extends BaseMsgHolder {
    public TextView tvPoiName;
    public TextView tvPoiAddress;
    public View locationItemContainer;

    public LocationMsgHolder(View itemView, int childId) {
        super(itemView, childId);
//        R.layout.item_receive_message_location
        tvPoiName = itemView.findViewById(R.id.tv_poi_name);
        tvPoiAddress = itemView.findViewById(R.id.tv_poi_address);
        locationItemContainer = itemView.findViewById(R.id.location_item_container);
    }
}
