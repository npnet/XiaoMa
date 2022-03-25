package com.xiaoma.pet.model;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 17:15
 *   desc:   接收unity result实体
 * </pre>
 */
public class UAResultInfo {

    private String Action;
    private long RequestCode;
    private int Return;

    public UAResultInfo(String action, long requestCode, int aReturn) {
        this.Action = action;
        this.RequestCode = requestCode;
        this.Return = aReturn;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public long getRequestCode() {
        return RequestCode;
    }

    public void setRequestCode(long requestCode) {
        RequestCode = requestCode;
    }

    public int getResult() {
        return Return;
    }

    public void setResult(int aReturn) {
        Return = aReturn;
    }
}
