package com.xiaoma.process.manager;

import android.content.Context;

public class XMApi {

    private XMClubApiManager mXMClubApiManager;
    private XMServiceApiManager mXMServiceApiManager;
    private XMXTingApiManager mXMXTingApiManager;
    private XMSmartApiManager mXMSmartApiManager;
    private XMFactoryApiManager mXMFactoryApiManager;
    private XMLauncherApiManager mXMLauncherApiManager;
    private XMUserApiManager mXMUserApiManager;
    private XMSettingApiManager mXMSettingApiManager;
    private XMBluetoothPhoneApiManager mXMBluetoothPhoneApiManager;
    private Context mContext;

    private static class AidlManagerHolder {
       private static final XMApi manager = new XMApi();
    }

    public synchronized static XMApi getInstance() {
        return AidlManagerHolder.manager;
    }

    public synchronized void init(Context context) {
        mContext = context;
        checkContextIsNull();
        initApiServiceManager(mContext);
    }

    private void initApiServiceManager(Context context) {
        mXMClubApiManager = new XMClubApiManager(context);
        mXMServiceApiManager = new XMServiceApiManager(context);
        mXMXTingApiManager = new XMXTingApiManager(context);
        mXMSmartApiManager = new XMSmartApiManager(context);
        mXMFactoryApiManager = new XMFactoryApiManager(context);
        mXMLauncherApiManager = new XMLauncherApiManager(context);
        mXMUserApiManager = new XMUserApiManager(mXMLauncherApiManager);
        mXMSettingApiManager = new XMSettingApiManager(context);
        mXMBluetoothPhoneApiManager = new XMBluetoothPhoneApiManager(context);
    }

    private void checkContextIsNull() {
        if (mContext == null) {
            throw new NullPointerException("context is null, please check init method.");
        }
    }

    public XMClubApiManager getXMClubApiManager(){
        checkContextIsNull();
        return mXMClubApiManager;
    }

    public XMServiceApiManager getXMServiceApiManager() {
        checkContextIsNull();
        return mXMServiceApiManager;
    }

    public XMXTingApiManager getXMXTingApiManager(){
        checkContextIsNull();
        return mXMXTingApiManager;
    }

    public XMSmartApiManager getXMSmartApiManager(){
        checkContextIsNull();
        return mXMSmartApiManager;
    }

    public XMLauncherApiManager getXMLauncherApiManager() {
        checkContextIsNull();
        return mXMLauncherApiManager;
    }

    public XMUserApiManager getXMUserApiManager(){
        checkContextIsNull();
        return mXMUserApiManager;
    }

    public XMFactoryApiManager getXMFactoryApiManager() {
        checkContextIsNull();
        return mXMFactoryApiManager;
    }

    public XMSettingApiManager getXMSettingApiManager(){
        checkContextIsNull();
        return mXMSettingApiManager;
    }

    public XMBluetoothPhoneApiManager getXMBluetoothPhoneApiManager(){
        checkContextIsNull();
        return mXMBluetoothPhoneApiManager;
    }
}
