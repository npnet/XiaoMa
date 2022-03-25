package com.qiming.fawcard.synthesize.data.entity;

/**
 * Created by My on 2018/8/1.
 */

public class DriveScoreTboxSnResponse {
    public String resultCode = "";
    public String resultMessage = "";
    public TboxSn data;

    public class TboxSn{
        public String vin;
        public String tboxSN;

        public TboxSn() {
        }
    }

    public DriveScoreTboxSnResponse() {
    }
}
