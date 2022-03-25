package com.xiaoma.pet.model;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 15:07
 *   desc:   向untiy发送消息实体
 * </pre>
 */
public class AUCallForReturn {

    private String Action;
    private boolean Return;
    private long RequestCode;
    private Object[] Params;

    public AUCallForReturn(String action, boolean aReturn, long requestCode, Object[] params) {
        this.Action = action;
        this.Return = aReturn;
        this.RequestCode = requestCode;
        this.Params = params;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public boolean getReturn() {
        return Return;
    }

    public void setReturn(boolean aReturn) {
        Return = aReturn;
    }

    public long getRequestCode() {
        return RequestCode;
    }

    public void setRequestCode(long requestCode) {
        RequestCode = requestCode;
    }

    public Object[] getParams() {
        return Params;
    }

    public void setParams(Object[] params) {
        Params = params;
    }


}
