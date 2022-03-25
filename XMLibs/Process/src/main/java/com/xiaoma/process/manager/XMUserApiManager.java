package com.xiaoma.process.manager;

import com.xiaoma.aidl.model.User;

public class XMUserApiManager {

    private XMLauncherApiManager mXMLauncherApiManager;

    XMUserApiManager(XMLauncherApiManager xmLauncherApiManager){
        this.mXMLauncherApiManager = xmLauncherApiManager;
    }

    public User getCurrentXmUser(){
        if(mXMLauncherApiManager != null) {
            return mXMLauncherApiManager.getCurrentXmUser();
        } else {
            return null;
        }
    }

}