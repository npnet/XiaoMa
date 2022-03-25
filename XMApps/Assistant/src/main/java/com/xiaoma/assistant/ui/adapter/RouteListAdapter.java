package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.utils.MapUtil;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc：导航二级界面adapter
 */
public class RouteListAdapter extends BaseMultiPageAdapter<PoiBean> {


    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNum;
        private TextView tvDistance;
        private TextView tvName;
        private TextView tvAddress;

        public RouteViewHolder(View itemView) {
            super(itemView);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
        }

    }


    public RouteListAdapter(Context context, List<PoiBean> albums) {
        this.context = context;
        this.allList = albums;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_navi, parent, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            PoiBean poiBean = allList.get(position);
            ((RouteViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((RouteViewHolder) holder).tvAddress.setText(poiBean.getAddress());
            ((RouteViewHolder) holder).tvName.setText(poiBean.getName());
            // TODO: 2019/1/29 0029
            LatLng currentPosition = LocationManager.getInstance().getCurrentPosition();
            if (currentPosition == null && BuildConfig.DEBUG) {
                currentPosition = LocationManager.getInstance().getDebugLatLng();
            }
            double lineDistance = 0;
            if (currentPosition != null) {
                lineDistance = MapUtil.calculateLineDistance(currentPosition, new LatLng(poiBean.getLatitude(), poiBean.getLongitude())) / 1000;
                ((RouteViewHolder) holder).tvDistance.setText(CommonUtils.getFormattedDistance(context, lineDistance));
            }
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }
}