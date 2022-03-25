package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.TrainBean;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc:火车语音二级adapter
 */
public class TrainListAdapter extends BaseMultiPageAdapter<TrainBean> {
    private RecyclerView view;

    public static class TrainViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNum;
        private TextView tvTrainName;
        private TextView tvTotalLength;
        private TextView tvStart;
        private TextView tvStartTime;
        private TextView tvEnd;
        private TextView tvEndTime;


        public TrainViewHolder(View itemView) {
            super(itemView);
            this.tvEndTime = (TextView) itemView.findViewById(R.id.tv_end_time);
            this.tvEnd = (TextView) itemView.findViewById(R.id.tv_end);
            this.tvStartTime = (TextView) itemView.findViewById(R.id.tv_start_time);
            this.tvStart = (TextView) itemView.findViewById(R.id.tv_start);
            this.tvTotalLength = (TextView) itemView.findViewById(R.id.tv_total_length);
            this.tvTrainName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tvNum = (TextView) itemView.findViewById(R.id.tv_num);
        }

    }


    public TrainListAdapter(Context context, List<TrainBean> trainBeanList) {
        this.context = context;
        this.allList = trainBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_flight, parent, false);
        return new TrainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        if (allList != null) {
            TrainBean album = allList.get(position);
            ((TrainViewHolder) holder).tvNum.setText(String.valueOf(position+1));
            ((TrainViewHolder) holder).tvTrainName.setText(album.train_no);
            ((TrainViewHolder) holder).tvTotalLength.setText(context.getString(R.string.total_duration)+album.run_time);
            ((TrainViewHolder) holder).tvStartTime.setText(album.start_time);
            ((TrainViewHolder) holder).tvEndTime.setText(album.end_time);
            ((TrainViewHolder) holder).tvStart.setText(album.start_station);
            ((TrainViewHolder) holder).tvEnd.setText(album.end_station);
        }

    }

    @Override
    public int getItemCount() {
        return allList == null? 0 : allList.size();
    }

}



