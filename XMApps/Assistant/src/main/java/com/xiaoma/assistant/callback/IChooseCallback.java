package com.xiaoma.assistant.callback;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：
 */
public interface IChooseCallback {
    void previousPageAction();

    void nextPageAction();

    void chooseItemAction(int action);

    void lastAction();

    void cancelChooseAction();

    void errorChooseActon();

    void assignPageAction(int page);
}
