package com.xiaoma.launcher.travel.hotel.calendar;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.calendar
 *  @file_name:      CalendarAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/10 11:24
 *  @description：   TODO             */

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractLvGvAdapter;
import com.xiaoma.ui.vh.XMLvViewHolder;
import com.xiaoma.utils.TimeUtils;

import java.util.List;

public class CalendarAdapter extends XMBaseAbstractLvGvAdapter<DateBean> {

    public DateBean mStart;
    public DateBean mEnd;
    private int[] mStartSolar;
    private int[] mEndSolar;

    public CalendarAdapter(Context context, List<DateBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }

    @Override
    protected void convert(XMLvViewHolder viewHolder, DateBean dateBean, int position) {

        if (dateBean.getType() == 0 || dateBean.getType() == 2) {
            TextView tvDay = viewHolder.getView(R.id.solar_day);
            tvDay.setText("");
            tvDay.setClickable(false);
            tvDay.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        //如果是过去和下月 颜色置灰
        if (dateBean.isExpire()) {
            viewHolder.setTextColor(R.id.solar_day, Color.parseColor("#999999"));
        } else {
            viewHolder.setTextColor(R.id.solar_day, Color.WHITE);
        }
        viewHolder.setText(R.id.solar_day, String.valueOf(dateBean.getSolar()[2]));

        int[] solar = dateBean.getSolar();
        if (mStart == null && mEnd == null) {
            viewHolder.setBackgroundColor(R.id.solar_day, Color.TRANSPARENT);
        } else if (mStart != null) {
            mStartSolar = mStart.getSolar();
            int compareStart = TimeUtils.compareDate(mStartSolar[0], mStartSolar[1], mStartSolar[2], solar[0], solar[1], solar[2]);

            if (mEnd != null) {
                mEndSolar = mEnd.getSolar();
                int compareEnd = TimeUtils.compareDate(mEndSolar[0], mEndSolar[1], mEndSolar[2], solar[0], solar[1], solar[2]);
                if (compareStart == 0) {
                    //刚好是入住日期
                    viewHolder.setText(R.id.solar_day, mContext.getString(R.string.check_in));
                    viewHolder.setTextColor(R.id.solar_day, R.color.color_594226);
                    viewHolder.setBackgroundRes(R.id.solar_day, R.drawable.bg_day);
                } else if (compareEnd == 0) {
                    //刚好是离店日期
                    viewHolder.setText(R.id.solar_day, mContext.getString(R.string.check_out));
                    viewHolder.setTextColor(R.id.solar_day, R.color.color_594226);
                    viewHolder.setBackgroundRes(R.id.solar_day, R.drawable.bg_day);
                } else if (compareEnd == 1 && compareStart == -1) {
                    //如果大于入住日期 小于离店日期
                    viewHolder.setTextColor(R.id.solar_day, R.color.color_594226);
                    viewHolder.setBackgroundRes(R.id.solar_day, R.drawable.bg_day);
                } else {
                    viewHolder.setBackgroundColor(R.id.solar_day, Color.TRANSPARENT);
                }

            } else {
                if (compareStart == 0) {
                    viewHolder.setText(R.id.solar_day, mContext.getString(R.string.check_in));
                    viewHolder.setTextColor(R.id.solar_day, R.color.color_594226);
                    viewHolder.setBackgroundRes(R.id.solar_day, R.drawable.bg_day);
                } else {
                    viewHolder.setBackgroundColor(R.id.solar_day, Color.TRANSPARENT);
                }
            }
        } else {
            viewHolder.setBackgroundColor(R.id.solar_day, Color.TRANSPARENT);
        }
    }

    public void setStartAndEndDate(DateBean startDate, DateBean endDate) {
        mStart = startDate;
        mEnd = endDate;
        notifyDataSetChanged();
    }

}
