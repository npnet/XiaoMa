package com.xiaoma.aidl.wechat;

import com.xiaoma.aidl.wechat.IWeChatNotifyAidlInterface;

interface IWeChatAidlInterface {

    boolean registerNotifyCallBack(IWeChatNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterNotifyCallBack(IWeChatNotifyAidlInterface notifyAidlInterface);

    //WeChat调用导航
    boolean startNavi(double lat, double lon, String poiName);

    //WeChat拨打电话
    boolean callPhone(String phoneNumber);

    //WeChat需要播报
    boolean startTTS(String speakContent);

    //停止tts播报
    boolean endTTS();

    boolean uploadContact(String contacts);

    boolean startRecord(int timeOut);

    boolean finishRecord();

}
