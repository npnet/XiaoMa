package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.model.TribeBean;

import java.util.List;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.utils.CommonUtils;

/**
 * Created by qiuboxiang on 2019/2/18 20:44
 * Desc: 部落列表适配器
 */
public class TribeAdapter extends BaseMultiPageAdapter<TribeBean> {

    public static class TribeViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPhoto;
        public TextView tvNum;
        public TextView tvName;
        public TextView tvCount;
        public TextView tvDistance;
        public TextView tvTab;
        TextView tvScore;

        public TribeViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvNum = itemView.findViewById(R.id.tv_number);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvTab = itemView.findViewById(R.id.tv_tab);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvScore = itemView.findViewById(R.id.tv_score);
        }

    }

    public TribeAdapter(Context context, List<TribeBean> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tribe, parent, false);
        return new TribeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            TribeBean bean = allList.get(position);
            TribeViewHolder mHolder = (TribeViewHolder) holder;
            mHolder.tvNum.setText(String.valueOf(position + 1));
            //TODO 部落信息
/*            mHolder.tvScore.setText(CommonUtils.getFormattedNumber(bean.getAvgscore()) + "分");
            mHolder.tvDistance.setText(CommonUtils.getFormattedDistance(context, bean.getDistance()));
            mHolder.tvName.setText(bean.getName());
            if (!TextUtils.isEmpty(bean.getSubcate())) {
                mHolder.tvTab.setVisibility(View.VISIBLE);
                mHolder.tvTab.setText(bean.getSubcate());
            } else {
                mHolder.tvTab.setVisibility(View.GONE);
            }
            if (bean.getAvgprice() != 0) {
                mHolder.tvPrice.setText(CommonUtils.getPriceSpannableString(context, bean.getAvgprice()));
            } else {
                mHolder.tvPrice.setText(R.string.no_price);
            }
            CommonUtils.setItemImage(context, bean.getFrontimg(), mHolder.ivPhoto);*/
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}

