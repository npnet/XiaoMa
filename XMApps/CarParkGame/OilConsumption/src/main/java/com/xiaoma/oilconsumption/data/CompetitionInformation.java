package com.xiaoma.oilconsumption.data;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/24
 *     desc   :
 * </pre>
 */
public class CompetitionInformation {

    String date;
    String periods;
    double money;
    List<String> competitor;

    public CompetitionInformation(String date,String periods,double money,List<String> competitor){
        this.date=date;
        this.periods=periods;
        this.money=money;
        this.competitor=competitor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<String> getCompetitor() {
        return competitor;
    }

    public void setCompetitor(List<String> competitor) {
        this.competitor = competitor;
    }

    @Override
    public String toString() {
        return "CompetitionInformation{" +
                "date='" + date + '\'' +
                ", periods='" + periods + '\'' +
                ", money=" + money +
                ", competitor=" + competitor +
                '}';
    }
}
