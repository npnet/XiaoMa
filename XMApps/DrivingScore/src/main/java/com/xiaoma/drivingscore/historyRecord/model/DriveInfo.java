package com.xiaoma.drivingscore.historyRecord.model;


/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class DriveInfo {

    private String date;
    private String time;
    private DriveRecordDetails recordDetails;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DriveRecordDetails getRecordDetails() {
        return recordDetails;
    }

    public void setRecordDetails(DriveRecordDetails recordDetails) {
        this.recordDetails = recordDetails;
    }
}
