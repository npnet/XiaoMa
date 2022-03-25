package com.xiaoma.vr.understand;

import android.content.Context;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/20
 */

public abstract class BaseUnderStandManager {

    public abstract void init(Context context);

    public abstract void understandText(String text, IUnderstandListener understanderListener);
}
