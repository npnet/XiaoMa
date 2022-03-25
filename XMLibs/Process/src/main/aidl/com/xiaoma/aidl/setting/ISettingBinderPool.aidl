// ISettingAidlInterface.aidl
package com.xiaoma.aidl.setting;

// Declare any non-default types here with import statements

interface ISettingBinderPool {

    IBinder queryBinder(int binderCode);

}
