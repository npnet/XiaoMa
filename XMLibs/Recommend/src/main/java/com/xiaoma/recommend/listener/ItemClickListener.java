package com.xiaoma.recommend.listener;

import android.view.View;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 */
public interface ItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
