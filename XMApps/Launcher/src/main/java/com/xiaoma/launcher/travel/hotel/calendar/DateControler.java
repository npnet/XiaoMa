package com.xiaoma.launcher.travel.hotel.calendar;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.calendar
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/1/9 20:26
 *  @description：   TODO             */

import com.xiaoma.utils.TimeUtils;

public class DateControler {


    /**
     * 入住对应的item
     */
    private DateBean startItem;
    /**
     * 离店对应的item
     */
    private DateBean endItem;

    public DateBean getStartItem() {
        return startItem;
    }

    public void setStartItem(DateBean startItem) {
        this.startItem = startItem;
    }

    public DateBean getEndItem() {
        return endItem;
    }

    public void setEndItem(DateBean endItem) {
        this.endItem = endItem;
    }


    private boolean startClick = true;

    private CalendarAdapter mStartAdapter;
    private CalendarAdapter mEndAdapter;


    public interface OnStartDayClickListener {
        void startClick(DateBean dateBean);
    }

    public interface OnEndDayClickListener {
        void endClick(DateBean startBean, DateBean endBean);
    }

    private OnStartDayClickListener mStartDayClickListener;
    private OnEndDayClickListener mOnEndDayClickListener;

    public void setOnStartClick(OnStartDayClickListener onStartDayClickListener) {
        mStartDayClickListener = onStartDayClickListener;
    }

    public void setOnEndClick(OnEndDayClickListener onEndDayClickListener) {
        mOnEndDayClickListener = onEndDayClickListener;
    }

    public DateControler(CalendarAdapter startAdapter, CalendarAdapter endAdapter) {
        mStartAdapter = startAdapter;
        mEndAdapter = endAdapter;
    }

    /**
     * 当点击一个日期时，调用该方法
     *
     * @param item
     */
    public void viewClicked(DateBean item) {

        if (startClick) {     //选择入住时间

            startItem = item;
            endItem = null;
            startClick = false;

            if (mStartDayClickListener != null) {
                mStartDayClickListener.startClick(item);        //选择开始时间的回调函数
            }

        } else {
            //已经选择过入住时间，此次为设置离店时间

            //如果第二次选择的时间晚于开始时间
            int[] solarEnd = item.getSolar();
            int[] solarStart = startItem.getSolar();

            int compareDate = TimeUtils.compareDate(solarStart[0], solarStart[1], solarStart[2], solarEnd[0], solarEnd[1], solarEnd[2]);

            if (compareDate==0) {
                //如果第二次 和 第一次点的同一天
                startItem=null;
                endItem=null;
                startClick = true;

            } else if (compareDate==-1) {
                //如果第二次点击的时间大于开始的时间
                endItem = item;
                startClick = true;

                if (mOnEndDayClickListener != null) {
                    mOnEndDayClickListener.endClick(startItem,  item);      //结束时间的回调函数
                }

            } else {
                //如果第二次点击的时间早于开始的时间，相当于选择入住时间，所以逻辑和第一种情况相同

                startItem = item;
                endItem = null;
                startClick = false;

                if (mStartDayClickListener != null) {
                    mStartDayClickListener.startClick(item);        //选择开始时间的回调函数
                }

            }

        }

        if (mStartAdapter !=null){
            mStartAdapter.setStartAndEndDate(startItem,endItem);
        }
        if (mEndAdapter !=null){
            mEndAdapter.setStartAndEndDate(startItem,endItem);
        }
    }

    public void resetData(){
        startItem=null;
        endItem=null;
        startClick=true;
        if (mStartAdapter !=null){
            mStartAdapter.setStartAndEndDate(startItem,endItem);
        }
        if (mEndAdapter !=null){
            mEndAdapter.setStartAndEndDate(startItem,endItem);
        }
    }

    public void resetData(DateBean startItem,DateBean endItem){
        this.startItem=startItem;
        this.endItem=endItem;
        startClick=true;
        if (mStartAdapter !=null){
            mStartAdapter.setStartAndEndDate(startItem,endItem);
        }
        if (mEndAdapter !=null){
            mEndAdapter.setStartAndEndDate(startItem,endItem);
        }
    }


}
