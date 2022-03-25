package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.TravelInfo;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/18 0018
 * 景点二级界面
 */
public class TravelListAdapter extends BaseMultiPageAdapter<TravelInfo> {


    public TravelListAdapter(Context context, List<TravelInfo> travelinfos) {
        this.context = context;
        this.allList = travelinfos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvDistance;
        ImageView ivPhoto;
        TextView tvScore;
        TextView tvName;
        TextView tvTab;
        TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTab = itemView.findViewById(R.id.tv_tab);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvNum = itemView.findViewById(R.id.tv_num);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_travel, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            TravelInfo bean = allList.get(position);
            // TODO: 2019/1/18 0018

        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }
}
