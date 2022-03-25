package com.xiaoma.launcher.schedule.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * Created by ZSH on 2019/1/9 0009.
 */
public class ScheduleDetailAdapter extends XMBaseAbstractBQAdapter<ScheduleInfo, BaseViewHolder> {


    private boolean locationClickable = true;

    public ScheduleDetailAdapter(List<ScheduleInfo> datas) {
        super(R.layout.item_schedule, datas);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }

    @Override
    protected void convert(BaseViewHolder holder, final ScheduleInfo scheduleInfo) {
        final ImageView ivDelete = holder.getView(R.id.iv_delete);
        final TextView tvMessage = holder.getView(R.id.tv_message);
        final TextView tvTime = holder.getView(R.id.tv_time);
        final TextView tvLocation = holder.getView(R.id.tv_location);
        if (scheduleInfo.isShowDeleteView()) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.GONE);
        }

        holder.addOnClickListener(R.id.tv_location);
        holder.addOnClickListener(R.id.iv_delete);

        if (TextUtils.isEmpty(scheduleInfo.getLocation())) {
            tvLocation.setText(R.string.add_location_msg);
            tvLocation.setTextSize(20);
            tvLocation.setBackgroundResource(R.drawable.btn_selector);
            tvLocation.setClickable(locationClickable);
            tvLocation.setGravity(Gravity.CENTER);
        } else {
            tvLocation.setTextSize(24);
            tvLocation.setText(scheduleInfo.getLocation());
            tvLocation.setClickable(false);
            tvLocation.setBackgroundColor(Color.TRANSPARENT);
            tvLocation.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        }

        tvMessage.setText(scheduleInfo.getMessage());
        tvTime.setText(scheduleInfo.getTime());
    }

    public void setLocationClickable(boolean clickable) {
        locationClickable = clickable;
        notifyDataSetChanged();
    }


}
