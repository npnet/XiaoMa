package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.FlightV2;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc:航班语音二级adapter
 */
public class FlightListAdapter extends BaseMultiPageAdapter<FlightV2.DataBean.ListBean> {


    private static final String TAG = FlightListAdapter.class.getSimpleName();

    public static class FlightViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNum;
        private TextView tvAviationName;
        private TextView tvTotalLength;
        private TextView tvStart;
        private TextView tvStartTime;
        private TextView tvEnd;
        private TextView tvEndTime;
        private LinearLayout itemParent;

        public FlightViewHolder(View itemView) {
            super(itemView);
            this.tvEndTime = (TextView) itemView.findViewById(R.id.tv_end_time);
            this.tvEnd = (TextView) itemView.findViewById(R.id.tv_end);
            this.tvStartTime = (TextView) itemView.findViewById(R.id.tv_start_time);
            this.tvStart = (TextView) itemView.findViewById(R.id.tv_start);
            this.tvTotalLength = (TextView) itemView.findViewById(R.id.tv_total_length);
            this.tvAviationName = (TextView) itemView.findViewById(R.id.tv_name);
            this.itemParent = itemView.findViewById(R.id.item_parent);
            this.tvNum = (TextView) itemView.findViewById(R.id.tv_num);
        }

    }


    public FlightListAdapter(Context context, List<FlightV2.DataBean.ListBean> list) {
        this.context = context;
        this.allList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multipage_flight, parent, false);
        return new FlightViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            FlightV2.DataBean.ListBean bean = allList.get(position);
            ((FlightViewHolder) holder).tvNum.setText(String.valueOf(position + 1));
            ((FlightViewHolder) holder).tvAviationName.setText(bean.airline);
            ((FlightViewHolder) holder).tvTotalLength.setText(context.getString(R.string.total_duration) + bean.totalTime);
            ((FlightViewHolder) holder).tvStartTime.setText(bean.depTime);
            ((FlightViewHolder) holder).tvEndTime.setText(bean.arrTime);
            ((FlightViewHolder) holder).tvStart.setText(bean.depCity);
            ((FlightViewHolder) holder).tvEnd.setText(bean.arrCity);
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}



