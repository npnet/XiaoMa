package com.xiaoma.travel.view.citypick.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.travel.R;

/**
 * @author wutao
 * @date 2018/11/6
 */
public class LocateViewHolder extends RecyclerView.ViewHolder {

    public TextView mTvLocatedCity;

    public LocateViewHolder(View itemView) {
        super(itemView);
        mTvLocatedCity = (TextView) itemView.findViewById(R.id.tv_located_city);
    }
}
