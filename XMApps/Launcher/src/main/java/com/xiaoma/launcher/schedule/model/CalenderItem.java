package com.xiaoma.launcher.schedule.model;

/**
 * 日历item
 */
public class CalenderItem {

    private String date;
    //当天日期
    private int day;
    //是否被选中
    private boolean isSelected;
    //是否当前日期有备忘录数据
    private boolean hasData;
    //是否是当前月份日期
    private boolean isCurrentMouthDay;
    //是否是上个月份日期
    private boolean isLastMouthDay;
    //是否是下个月份日期
    private boolean isNextMouthDay;
    //农历月、日
    private String[] lunar;
    private String solarHoliday;//阳历节假日
    private String lunarHoliday;//阴历节假日
    private String term;//节气

    public CalenderItem(String date, int day, String[] lunar, String lunarHoliday, String solarHoliday, boolean isSelected, boolean hasData, boolean isCurrentMouthDay, boolean isLastMouthDay, boolean isNextMouthDay) {
        this.date = date;
        this.day = day;
        this.lunar = lunar;
        this.solarHoliday = solarHoliday;
        this.lunarHoliday = lunarHoliday;
        this.isSelected = isSelected;
        this.hasData = hasData;
        this.isCurrentMouthDay = isCurrentMouthDay;
        this.isLastMouthDay = isLastMouthDay;
        this.isNextMouthDay = isNextMouthDay;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }


    public String getSolarHoliday() {
        return solarHoliday;
    }

    public void setSolarHoliday(String solarHoliday) {
        this.solarHoliday = solarHoliday;
    }

    public String getLunarHoliday() {
        return lunarHoliday;
    }

    public void setLunarHoliday(String lunarHoliday) {
        this.lunarHoliday = lunarHoliday;
    }


    public String[] getLunar() {
        return lunar;
    }

    public void setLunar(String[] lunar) {
        this.lunar = lunar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public boolean isCurrentMouthDay() {
        return isCurrentMouthDay;
    }

    public void setCurrentMouthDay(boolean currentMouthDay) {
        isCurrentMouthDay = currentMouthDay;
    }

    public boolean isLastMouthDay() {
        return isLastMouthDay;
    }

    public void setLastMouthDay(boolean lastMouthDay) {
        isLastMouthDay = lastMouthDay;
    }

    public boolean isNextMouthDay() {
        return isNextMouthDay;
    }

    public void setNextMouthDay(boolean nextMouthDay) {
        isNextMouthDay = nextMouthDay;
    }

    @Override
    public String toString() {
        return "CalenderItem{" +
                "date='" + date + '\'' +
                ", day=" + day +
                ", isSelected=" + isSelected +
                ", hasData=" + hasData +
                ", isCurrentMouthDay=" + isCurrentMouthDay +
                ", isLastMouthDay=" + isLastMouthDay +
                ", isNextMouthDay=" + isNextMouthDay +
                '}';
    }
}
