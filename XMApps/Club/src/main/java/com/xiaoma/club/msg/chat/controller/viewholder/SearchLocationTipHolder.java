package com.xiaoma.club.msg.chat.controller.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class SearchLocationTipHolder extends RecyclerView.ViewHolder {
    public TextView tvPoiName;
    public TextView tvPoiAddress;

    public SearchLocationTipHolder(View itemView) {
        super(itemView);
        tvPoiName = itemView.findViewById(R.id.tv_poi_name);
        tvPoiAddress = itemView.findViewById(R.id.tv_poi_address);

    }
}
