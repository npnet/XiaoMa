package com.xiaoma.pet.model;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 17:31
 *   desc:
 * </pre>
 */
public class UACall {

    private String Action;
    private String Params;

    public UACall(String action, String params){
        this.Action =action;
        this.Params = params;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getParams() {
        return Params;
    }

    public void setParams(String params) {
        Params = params;
    }
}
