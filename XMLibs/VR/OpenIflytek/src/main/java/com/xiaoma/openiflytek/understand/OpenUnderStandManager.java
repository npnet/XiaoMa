package com.xiaoma.openiflytek.understand;

import android.content.Context;

import com.xiaoma.vr.understand.BaseUnderStandManager;
import com.xiaoma.vr.understand.IUnderstandListener;
import com.xiaoma.vr.understand.UnderstandResult;
import com.xiaoma.vr.understand.XmUnderStand;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/20
 * Desc：semantic manager for iFly or xiaoma server
 */

public class OpenUnderStandManager extends BaseUnderStandManager {

    private static OpenUnderStandManager instance;
    private OpenUnderStand xfUnderstandManager;
    private XmUnderStand localUnderstandManager;

    public static OpenUnderStandManager getInstance() {
        if (instance == null) {
            instance = new OpenUnderStandManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (xfUnderstandManager == null) {
            xfUnderstandManager = new OpenUnderStand();
            xfUnderstandManager.init(context);
        }
        if (localUnderstandManager == null) {
            localUnderstandManager = new XmUnderStand();
        }
    }

    public void understandText(final String text, final IUnderstandListener understanderListener) {
        //讯飞
        xfUnderstandManager.understandText(text, new IUnderstandListener() {
            @Override
            public void onResult(final UnderstandResult result) {
                notifyUnderstandSuccess(result);
            }

            @Override
            public void onError() {
                notifyUnderstandError();
            }

            private void notifyUnderstandSuccess(UnderstandResult result) {
                if (understanderListener != null) {
                    understanderListener.onResult(result);
                }
            }

            private void notifyUnderstandError() {
                if (understanderListener != null) {
                    understanderListener.onError();
                }
            }
        });

        //小马后台
        /*localUnderstandManager.understandText(text, new IUnderstandListener() {
            @Override
            public void onResult(final UnderstandResult result) {
                notifyUnderstandSuccess(result);
            }

            @Override
            public void onError() {
                notifyUnderstandError();
            }

            private void notifyUnderstandSuccess(UnderstandResult result) {
                if (understanderListener != null) {
                    understanderListener.onResult(result);
                }
            }

            private void notifyUnderstandError() {
                if (understanderListener != null) {
                    understanderListener.onError();
                }
            }
        });*/
    }
}
