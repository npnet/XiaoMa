package com.xiaoma.launcher.schedule.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.schedule.model.CalenderItem;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.vh.XMViewHolder;

import java.util.List;

public class CalenderAdapter extends XMBaseAbstractRyAdapter<CalenderItem> {

    private Context mContext;
    private static final int GRAY_COLOR = Color.parseColor("#8a919d");

    //最后一行第一个position
    private static final int LAST_LINE_FIRST_ONE = 35;

    private int curSelectPos;

    public CalenderAdapter(Context context, List<CalenderItem> data, int layoutId) {
        super(context, data, layoutId);
        mContext = context;
    }

    @Override
    protected void convert(XMViewHolder holder, final CalenderItem calenderItem, final int position) {
        final TextView tvDay = holder.getView(R.id.tv_day);
        final TextView tvLunar = holder.getView(R.id.lunar_day);
        final View itemView = holder.getView(R.id.rl_item_view);
        tvDay.setText(String.valueOf(calenderItem.getDay()));
        holder.setVisible(R.id.iv_dot, calenderItem.isHasData());

        if (!calenderItem.isCurrentMouthDay()) {
            tvDay.setTextColor(GRAY_COLOR);
            tvLunar.setTextColor(GRAY_COLOR);
        } else {
            tvDay.setTextColor(Color.WHITE);
            if (mContext.getString(R.string.lunar_first_day).equals(calenderItem.getLunar()[1])) {
                tvLunar.setTextColor(Color.parseColor("#fbd3a4"));
            } else {
                tvLunar.setTextColor(Color.WHITE);
            }
        }

        if (mContext.getString(R.string.lunar_first_day).equals(calenderItem.getLunar()[1])) {
            tvLunar.setText(calenderItem.getLunar()[0]);
            if (mContext.getString(R.string.lunar_first_month).equals(calenderItem.getLunar()[0])) {
                tvLunar.setText(R.string.lunar_spring_festival);
            }
        } else {
            if (!TextUtils.isEmpty(calenderItem.getSolarHoliday())) {//阳历节日
                tvLunar.setText(calenderItem.getSolarHoliday());
            } else if (!TextUtils.isEmpty(calenderItem.getLunarHoliday())) {//农历节日
                tvLunar.setText(calenderItem.getLunarHoliday());
            } else {
                if (TextUtils.isEmpty(calenderItem.getLunar()[1])) {
                    tvLunar.setVisibility(View.GONE);
                } else {
                    tvLunar.setText(calenderItem.getLunar()[1]);//农历日期
                }
            }
        }
        //如果最后一行第一个是下一个月，那么最后一行就不显示
        if (position >= LAST_LINE_FIRST_ONE) {
            if (getDatas().get(LAST_LINE_FIRST_ONE).isNextMouthDay()) {
                itemView.setVisibility(View.GONE);
            } else {
                itemView.setVisibility(View.VISIBLE);
            }
        } else {
            itemView.setVisibility(View.VISIBLE);
        }

        itemView.setBackgroundResource(calenderItem.isSelected() ? R.drawable.icon_sign_red : Color.TRANSPARENT);

        if (calenderItem.isSelected()) {
            curSelectPos = position;
        }


    }

    public int getCurSelPos() {
        return curSelectPos;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.CALENDAR_DAY, getDatas().get(position).getDate());
    }
}