package com.xiaoma.assistant.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.RestaurantInfo;
import com.xiaoma.assistant.utils.CommonUtils;

import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/21 0021
 * 美食列表
 */
public class RestaurantListAdapter extends BaseMultiPageAdapter<RestaurantInfo> {

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvDistance;
        ImageView ivPhoto;
        TextView tvScore;
        TextView tvName;
        TextView tvTab;
        TextView tvPrice;

        public RestaurantViewHolder(View itemView) {
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

    public RestaurantListAdapter(Context context, List<RestaurantInfo> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_restaurant, parent, false);
        return new RestaurantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            RestaurantInfo bean = allList.get(position);
            RestaurantViewHolder mHolder = (RestaurantViewHolder) holder;
            mHolder.tvNum.setText(String.valueOf(position + 1));
            if (bean.getAvgscore()!=-1){
                mHolder.tvScore.setText(String.format(context.getString(R.string.minute),CommonUtils.getFormattedNumber(bean.getAvgscore())+""));
            }else {
                mHolder.tvScore.setText(String.format(context.getString(R.string.miqilinstar),bean.getStar()+""));
            }
            mHolder.tvDistance.setText(CommonUtils.getFormattedDistance(context, bean.getDistance()));
            mHolder.tvName.setText(bean.getName());
            if (!TextUtils.isEmpty(bean.getSubcate())) {
                mHolder.tvTab.setVisibility(View.VISIBLE);
                mHolder.tvTab.setText(bean.getSubcate());
            } else {
                mHolder.tvTab.setVisibility(View.GONE);
            }
            if (bean.getAvgprice() != 0) {
                mHolder.tvPrice.setText(CommonUtils.getPriceSpannableString(context, bean.getAvgprice(), "¥", context.getString(R.string.per_person)));
            } else {
                mHolder.tvPrice.setText(R.string.no_price);
            }
            CommonUtils.setItemImage(context, bean.getFrontimg(), mHolder.ivPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }
}
