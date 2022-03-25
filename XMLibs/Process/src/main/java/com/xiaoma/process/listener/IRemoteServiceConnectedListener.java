package com.xiaoma.process.listener;

public interface IRemoteServiceConnectedListener {

    void onConnected();

    void onDisConnected();

    void onConnectedDeath();

}
