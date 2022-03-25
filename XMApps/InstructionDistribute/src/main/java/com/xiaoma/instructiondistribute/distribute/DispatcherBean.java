package com.xiaoma.instructiondistribute.distribute;

import android.os.Bundle;

public class DispatcherBean {
    private int port;
    private int action;
    private Bundle bundle;
    private String remoteApp;

    public DispatcherBean(int action, Bundle bundle) {
        this.action = action;
        this.bundle = bundle;
    }

    public DispatcherBean(int port, int action, Bundle bundle, String remoteApp) {
        this.port = port;
        this.action = action;
        this.bundle = bundle;
        this.remoteApp = remoteApp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getRemoteApp() {
        return remoteApp;
    }

    public void setRemoteApp(String remoteApp) {
        this.remoteApp = remoteApp;
    }
}
