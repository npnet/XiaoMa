package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.StationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/30
 * Desc：
 */
public class TrainDetailAdapter extends RecyclerView.Adapter<TrainDetailAdapter.StationViewHolder> {

    public static class RadioViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;

        public RadioViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }

    }

    //默认分页大小
    private static final int DEFAULT_PAGE_SIZE = 4;
    private Context context;
    //总的list数据
    protected List<StationInfo> allList;
    //当前显示页面的list
    protected List<StationInfo> currentList;

    private int currentPage = 1;

    public TrainDetailAdapter(Context context, List<StationInfo> list) {
        this.context = context;
//        this.allList = list;
        this.currentList = list;
//        setPage(1);
    }

    public void setPage(int page) {
        if (page <= 0 && allList == null || allList.size() == 0 ||
                (allList.size() + DEFAULT_PAGE_SIZE - 1) / DEFAULT_PAGE_SIZE < page) {
            return;
        }
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        this.currentPage = page;
        currentList.clear();
        currentList.addAll(allList.subList((page - 1) * DEFAULT_PAGE_SIZE,
                allList.size() / DEFAULT_PAGE_SIZE >= page ? page * DEFAULT_PAGE_SIZE : (page - 1) * DEFAULT_PAGE_SIZE + allList.size() % DEFAULT_PAGE_SIZE));
        notifyDataSetChanged();
    }

    public void setNextPage(){
        setPage(++currentPage);
    }

    public void setLastPage(){
        setPage(++currentPage);
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_train_station, parent, false);
        return new StationViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return currentList == null ? 0 : currentList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        StationInfo info = currentList.get(position);
        if (info.arrived_time.equals("-")) {
            info.arrived_time = "--";
        }
        holder.tvPosition.setText(String.valueOf(position + 1));
        holder.tvName.setText(info.station_name);
        holder.tvArriveTime.setText(info.arrived_time);
        String stayDuration = info.stay.equals("-") ? "--" : info.stay + " Min";
        holder.tvStayDuration.setText(stayDuration);
        holder.divider.setVisibility(position == currentList.size() - 1 ? View.GONE : View.VISIBLE);
    }

    public void setData(List<StationInfo> list){
        this.currentList = list;
    }

    static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition;
        TextView tvName;
        TextView tvArriveTime;
        TextView tvStayDuration;
        View divider;

        StationViewHolder(View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvName = itemView.findViewById(R.id.tv_name);
            tvArriveTime = itemView.findViewById(R.id.tv_arrive_time);
            tvStayDuration = itemView.findViewById(R.id.tv_stay_duration);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
