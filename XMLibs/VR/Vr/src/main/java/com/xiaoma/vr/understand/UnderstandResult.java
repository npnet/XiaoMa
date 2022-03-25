package com.xiaoma.vr.understand;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */

public class UnderstandResult {
    private String resultString;

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public static UnderstandResult parse(UnderstandResult understanderResult) {
        UnderstandResult result = new UnderstandResult();
        result.resultString = understanderResult.getResultString();

        return result;
    }

    public UnderstandResult(){

    }

    public UnderstandResult(String resultString) {
        this.resultString = resultString;
    }

    public boolean isSuccess() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(resultString);
            int rc = jsonObject.getInt("rc");
            return rc == 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
