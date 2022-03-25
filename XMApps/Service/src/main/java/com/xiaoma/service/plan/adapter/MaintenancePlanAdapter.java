package com.xiaoma.service.plan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.ui.OrderActivity;
import com.xiaoma.service.plan.model.MaintenancePlanBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * Created by ZouShao on 2018/11/19 0019.
 */

public class MaintenancePlanAdapter extends XMBaseAbstractBQAdapter<MaintenancePlanBean.PlansBean, BaseViewHolder> {
    private int mCurrentTime = -1;
    private Context mContext;
    private final int orderTypeNum = 3;

    public MaintenancePlanAdapter(Context context) {
        super(R.layout.item_maintenance_plan);
        this.mContext = context;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getRecommendKilometer() + "", getData().get(position).getId() + "");
    }

    @Override
    protected void convert(BaseViewHolder helper, MaintenancePlanBean.PlansBean item) {
        //去保养
        Button btnLeaveMaintain = helper.getView(R.id.leave_to_maintain);
        //没有更多计划text
        TextView tvNomMorePlan = helper.getView(R.id.no_more_plan);
        //保养次数
        TextView tvMaintainCount = helper.getView(R.id.maintenance_count);
        //保养时间
        TextView tvMaintainTime = helper.getView(R.id.maintenance_time);
        //保养公里数
        TextView tvMainKM = helper.getView(R.id.kilometre_detail);
        //保养项目
        TextView tvOptionName = helper.getView(R.id.option_name);
        //时间线
        View view = helper.getView(R.id.divide);
        //背景
        TextView suggestKMLable = helper.getView(R.id.suggest_kilometre);
        TextView suggestProjectLable = helper.getView(R.id.suggest_project);
        List<MaintenancePlanBean.PlansBean.OptionsBean> optionsBeanList = item.getOptions();
        tvOptionName.setText(getOrderType(optionsBeanList));
        tvMaintainCount.setText(String.format(mContext.getString(R.string.maintenance_count_text),  item.getRecommendTimes()));
        tvMaintainTime.setText(String.format(mContext.getString(R.string.maintenance_time_text),item.getRecommendMonthNum()));
        tvMainKM.setText(String.valueOf(item.getRecommendKilometer()) + mContext.getString(R.string.kilometre));

        //最后一项处理
        if (helper.getAdapterPosition() == getItemCount() - 1) {
            tvNomMorePlan.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);

        } else {
            tvNomMorePlan.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }

        //去保养按钮处理
        if (mCurrentTime == item.getRecommendTimes()) {
            btnLeaveMaintain.setVisibility(View.VISIBLE);

        } else {
            btnLeaveMaintain.setVisibility(View.GONE);
        }

        //保养前的数据置灰处理
        if (mCurrentTime > item.getRecommendTimes()) {

            suggestKMLable.setTextColor(mContext.getResources().getColor(R.color.color_999));
            suggestProjectLable.setTextColor(mContext.getResources().getColor(R.color.color_999));
            tvMaintainCount.setTextColor(mContext.getResources().getColor(R.color.color_999));
            tvMainKM.setTextColor(mContext.getResources().getColor(R.color.color_999));
            tvOptionName.setTextColor(mContext.getResources().getColor(R.color.color_999));
            tvMaintainTime.setTextColor(mContext.getResources().getColor(R.color.color_999));

        } else {
            suggestKMLable.setTextColor(mContext.getResources().getColor(R.color.white));
            suggestProjectLable.setTextColor(mContext.getResources().getColor(R.color.white));
            tvMaintainCount.setTextColor(mContext.getResources().getColor(R.color.maintenance_plan_line_color));
            tvMainKM.setTextColor(mContext.getResources().getColor(R.color.white));
            tvOptionName.setTextColor(mContext.getResources().getColor(R.color.white));
            tvMaintainTime.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        btnLeaveMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.carReservation})
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, OrderActivity.class));
            }
        });
    }

    public void setCurrentTime(int time) {
        this.mCurrentTime = time;
        notifyDataSetChanged();
    }

    private String getOrderType(List<MaintenancePlanBean.PlansBean.OptionsBean> orderType) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < orderType.size(); i++) {
            stringBuffer.append(orderType.get(i).getName()).append("；");
        }

        return stringBuffer.toString();
    }
}
