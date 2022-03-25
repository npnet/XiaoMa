package com.xiaoma.travel.view.citypick.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.travel.R;

/**
 * @author wutao
 * @date 2018/11/6
 */
public class CityHolder extends RecyclerView.ViewHolder {

    public TextView mCityName;

    public CityHolder(View itemView) {
        super(itemView);
        mCityName = (TextView) itemView.findViewById(R.id.city_name);
    }
}
