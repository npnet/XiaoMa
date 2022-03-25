package com.xiaoma.assistant.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZhangYao.
 * Date ：2017/6/8 0008
 */

public class NearbyParkInfo implements Serializable {

    private ParkInfos data;
    private String resultCode;
    private String resultMessage;

    public void setData(ParkInfos data) {
        this.data = data;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public ParkInfos getData() {
        return data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public static class ParkInfos implements Serializable{

        private int count;
        private List<ParkInfo> data;

        public void setCount(int count) {
            this.count = count;
        }

        public void setData(List<ParkInfo> data) {
            this.data = data;
        }

        public int getCount() {
            return count;
        }

        public List<ParkInfo> getParkInfo() {
            return data;
        }
    }
}
