package com.xiaoma.vr.iat;

import android.content.Context;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/6
 */

public abstract class BaseIatManager {
    protected Context context;
    protected OnIatListener onIatListener;

    abstract public void init(Context context, OnIatListener onIatListener);

    abstract public void setOnIatListener(OnIatListener onIatListener);

    abstract public void stopListening();

    abstract public void startListeningNormal();

    abstract public void startListeningRecord();

    abstract public void startListeningRecord(int timeOut);

    abstract public void startListeningForChoose();

    abstract public void startListeningForChoose(String srSceneStkCmd);

    abstract public void upLoadContact(boolean isPhoneContact, String contacts);

    abstract public void cancelListening();

    abstract public void release();

    abstract public void destroy();

}
