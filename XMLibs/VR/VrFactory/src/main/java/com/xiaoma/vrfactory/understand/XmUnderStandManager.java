package com.xiaoma.vrfactory.understand;

import android.content.Context;

import com.xiaoma.vr.understand.BaseUnderStandManager;
import com.xiaoma.vr.understand.IUnderstandListener;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/20
 * Desc：Semantic manager
 */

public class XmUnderStandManager {

    private static XmUnderStandManager instance;
    private BaseUnderStandManager underStandManager;

    public static XmUnderStandManager getInstance() {
        if (instance == null) {
            instance = new XmUnderStandManager();
        }

        return instance;
    }


    public void init(Context context) {
        underStandManager = UnderStandFactory.getUnderStandManager();
        underStandManager.init(context);
    }


    public void underStandText(String text, final IUnderstandListener understanderListener){
        if (null == underStandManager) {
            underStandManager = UnderStandFactory.getUnderStandManager();
        }

        underStandManager.understandText(text,understanderListener);

    }

}
