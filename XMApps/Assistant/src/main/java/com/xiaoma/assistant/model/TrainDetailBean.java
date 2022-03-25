package com.xiaoma.assistant.model;

import java.io.Serializable;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/30
 * Desc:
 */

public class TrainDetailBean implements Serializable {

    /**
     * start : 深圳北
     * endtime : 20:03
     * end : 武汉
     * starttime : 15:05
     * time : 4h58m
     * info : G1018
     * mileage :
     * station_list : [{"station_name":"深圳北","leave_time":"15:05","arrived_time":"-","stay":"-"},{"station_name":"虎门","leave_time":"15:24","arrived_time":"15:22","stay":"2"},{"station_name":"广州南","leave_time":"15:44","arrived_time":"15:41","stay":"3"},{"station_name":"韶关","leave_time":"16:37","arrived_time":"16:35","stay":"2"},{"station_name":"郴州西","leave_time":"17:10","arrived_time":"17:08","stay":"2"},{"station_name":"衡阳东","leave_time":"17:45","arrived_time":"17:43","stay":"2"},{"station_name":"长沙南","leave_time":"18:31","arrived_time":"18:27","stay":"4"},{"station_name":"汨罗东","leave_time":"18:51","arrived_time":"18:49","stay":"2"},{"station_name":"岳阳东","leave_time":"19:13","arrived_time":"19:11","stay":"2"},{"station_name":"武汉","leave_time":"20:03","arrived_time":"20:03","stay":"-"}]
     */

    public String start;
    public String endtime;
    public String end;
    public String starttime;
    public String time;
    public String info;
    public String mileage;
    public String startType;
    public String endType;
    /**
     * station_name : 深圳北
     * leave_time : 15:05
     * arrived_time : -
     * stay : -
     */
    public List<StationInfo> station_list;
    public List<TrainBean.PriceListBean> price_list;

}
