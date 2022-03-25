package com.xiaoma.assistant.model;

import java.io.Serializable;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:
 */
public class TrainBean implements Serializable{

    public String train_no;
    public String train_type;
    public String start_station;
    public String start_station_type;
    public String end_station;
    public String end_station_type;
    public String start_time;
    public String end_time;
    public String run_time;
    public String run_distance;
    public String m_chaxun_url;
    public List<PriceListBean> price_list;

    public static class PriceListBean {

        public String price_type;
        public String price;
    }
}
