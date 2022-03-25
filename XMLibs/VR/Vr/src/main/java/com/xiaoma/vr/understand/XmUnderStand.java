package com.xiaoma.vr.understand;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * Desc:semantic by xiaoma server
 */

public class XmUnderStand {

    public void understandText(final String text, final IUnderstandListener understanderListener) {
        /*TokenTripLogicManager.newInstance().xmNlu(text, new HttpCallback() {
            @Override
            public void onSuccess(String textResult) {
                if (understanderListener != null) {
                    UnderstandResult result = new UnderstandResult();
                    result.setResultString(textResult);
                    understanderListener.onResult(result);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                if (understanderListener != null) {
                    understanderListener.onError();
                }
            }
        });*/
        //网络接口
    }
}
