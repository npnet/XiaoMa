package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xiaoma.assistant.R;
import com.xiaoma.aidl.model.ScheduleBean;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/2/18 19:52
 * Desc: 日程列表适配器
 */
public class ScheduleAdapter extends BaseMultiPageAdapter<ScheduleBean> {

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public TextView tvTime;
        public TextView tvContent;
        public TextView tvDestination;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDestination = itemView.findViewById(R.id.tv_destination);
        }

    }

    public ScheduleAdapter(Context context, List<ScheduleBean> list) {
        this.context = context;
        this.allList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (allList != null) {
            ScheduleBean bean = allList.get(position);
//            ((ScheduleViewHolder) holder).tvIndex.setText(String.valueOf(position + 1));
//            ((ScheduleViewHolder) holder).tvTime.setText(bean.getDatetime().getDateOrig() + " " + bean.getDatetime().getTimeOrig());
//            ((ScheduleViewHolder) holder).tvContent.setText(bean.getContent());
//            if (!TextUtils.isEmpty()) {
//            ((ScheduleViewHolder)holder).tvDestination.setText("目的地："+);
//            }else{
//                ((ScheduleViewHolder)holder).tvDestination.setText("目的地：暂未设置");
//            }
        }

    }

    @Override
    public int getItemCount() {
        return allList == null ? 0 : allList.size();
    }

}

