package com.xiaoma.recommend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.recommend.holder.BaseViewHolder;
import com.xiaoma.recommend.R;
import com.xiaoma.recommend.holder.RecommendViewHolder;
import com.xiaoma.recommend.listener.ItemClickListener;
import com.xiaoma.recommend.model.RecommendCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 */
public class RecommendAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private Context mContext;
    private List<RecommendCategory> mData = new ArrayList<>();
    private ItemClickListener clickListener;

    public RecommendAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<RecommendCategory> data) {
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
        }
    }

    public void setItemClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder;
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recommend, parent, false);
        holder = new RecommendViewHolder(view).setItemClickListener(clickListener);
        return holder;
    }




    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bindViewHolder(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
