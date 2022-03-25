package com.xiaoma.club.common.controller;

import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/12/26 0026
 */

public class TabViewHolder {

    public TextView tabTv;
    public TextView msgTv;

    public TabViewHolder(View tabView) {
        tabTv = tabView.findViewById(R.id.club_view_tab_tv);
        msgTv = tabView.findViewById(R.id.unread_msg);
    }

}
