package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.CinemaBean;
import com.xiaoma.assistant.utils.CommonUtils;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/18 0018
 * 电影院二级界面
 */
public class CinemaListAdapter extends BaseMultiPageAdapter<CinemaBean> {

    public static class CinemaBeanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvDistance;
        ImageView ivPhoto;
        TextView tvScore;
        TextView tvName;
        TextView tvTab;
        TextView tvPrice;

        public CinemaBeanViewHolder(View itemView) {
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

    public CinemaListAdapter(Context context, List<CinemaBean> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_cinema, parent, false);
        return new CinemaListAdapter.CinemaBeanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            CinemaBean bean = allList.get(position);
            CinemaListAdapter.CinemaBeanViewHolder mHolder = (CinemaListAdapter.CinemaBeanViewHolder) holder;
            mHolder.tvNum.setText(String.valueOf(position + 1));
            mHolder.tvDistance.setText(bean.getDistance());
            mHolder.tvName.setText(bean.getCinemaName());
            //TODO 影院评分、标签
//            mHolder.tvTab.setText(bean.getSubcate());
//            mHolder.tvScore.setText(bean.getAvgscore() + "分");
            CommonUtils.setItemImage(context,bean.getImgUrl(),mHolder.ivPhoto);
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}
