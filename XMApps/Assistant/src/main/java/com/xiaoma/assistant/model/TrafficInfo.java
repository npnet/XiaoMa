package com.xiaoma.assistant.model;

import android.os.Parcel;

import com.xiaoma.model.XMResult;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/12
 * Desc:路况
 */

public class TrafficInfo extends XMResult<TrafficInfo.DataBean> {

    public static final int CITY_IS_WRONG = 2004;
    public static final int ROAD_IS_WRONG = 2005;
    public static final int SERVICE_DOWN = 2002;

    protected TrafficInfo(Parcel in) {
        super(in);
    }

    public static class DataBean {
        /**
         * status : 3
         * description : 高新南六道：双向缓慢。
         */

        private String status;
        private String description;

        public void setStatus(String status) {
            this.status = status;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }
    }

    public boolean cityIsWrong(){
        return getResultCode() == CITY_IS_WRONG;
    }

    public boolean roadIsWrong(){
        return getResultCode() == ROAD_IS_WRONG;
    }
    public boolean serviceDown(){
        return getResultCode() == SERVICE_DOWN;
    }

}
