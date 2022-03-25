package com.xiaoma.assistant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:
 */
public class TrainResult {

    /**
     * data : [{"train_no":"C7128","train_type":"C","start_station":"深圳","start_station_type":"始","end_station":"广州东","end_station_type":"终","start_time":"06:20","end_time":"07:39","run_time":"1小时19分","price_list":[{"price_type":"二等座","price":"79.5"},{"price_type":"一等座","price":"99.5"}],"run_distance":"","m_chaxun_url":""},{"train_no":"C7044","train_type":"C","start_station":"深圳","start_station_type":"始","end_station":"广州东","end_station_type":"终","start_time":"06:28","end_time":"07:47","run_time":"1小时19分","price_list":[{"price_type":"二等座","price":"79.5"},{"price_type":"一等座","price":"99.5"}],"run_distance":"","m_chaxun_url":""},{"train_no":"C7178","train_type":"C","start_station":"深圳","start_station_type":"始","end_station":"广州东","end_station_type":"终","start_time":"06:36","end_time":"07:55","run_time":"1小时19分","price_list":[{"price_type":"二等座","price":"79.5"},{"price_type":"一等座","price":"99.5"}],"run_distance":"","m_chaxun_url":""}]
     * resultCode : 1
     * resultMessage : 操作成功
     */

    public int resultCode;
    public String resultMessage;
    public Data data;

    public class Data {
        public int count;
        public List<TrainBean> list = new ArrayList<>();
    }


}
