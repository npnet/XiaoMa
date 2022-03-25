package com.qiming.fawcard.synthesize.base;

/**
 * Created by summit on 6/5/17.
 */

public class BaseResponse {

    public Status status;
    public String errorMessage;
    public String errorCode;

    public boolean isSuccess() {
        if (status == Status.SUCCEED) {
            return true;
        }
        return false;
    }


    public enum Status {
        SUCCEED("SUCCEED"), FAILED("FAILED"), NO_DATA("NO_DATA");

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
}
