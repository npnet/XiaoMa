package com.xiaoma.aidl.bluetoothphone;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.bluetoothphone.IBluetoothPhoneNotifyAidlInterface;
import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/19 15:31
 */
interface IBluetoothPhoneAidlInterface {

    /**
     * 拨号
     *
     * @param phoneNum
     */
    boolean dial(String phoneNum);

    /**
     * 双音多频，发送给被叫号码的用户信号
     *
     * @param code
     */
    boolean sendDTMF(char code);

    /**
     * 接听
     */
    boolean acceptCall();

    /**
     * 拒接
     */
    boolean rejectCall();

    /**
     * 结束当前通话
     */
    boolean terminateCall();

    /**
     * 三方通话时切换通话对象
     */
    boolean holdCall();

    /**
     * 连接蓝牙音频(车机接听)
     */
    boolean connectAudio();

    /**
     * 断开蓝牙音频（手机接听）
     */
    boolean disconnectAudio();

    /**
     * 获取所有联系人
     *
     * @return
     */
    List<ContactBean> getAllContact();

    /**
     * 回拨
     */
    boolean dialBack();

    /**
     * 重拨
     */
    boolean redial();

    /**
     * 注册通话状态回调
     *
     * @param callback
     */
    void registerPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback);

    /**
     * 移除注册通话状态回调
     *
     * @param callback
     */
    void unregisterPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback);

    /**
     * 获取通话历史
     *
     * @return
     */
    List<ContactBean> getHistoryCall();

    /**
     * 蓝牙是否已连接
     */
    boolean isBluetoothConnected();

    /*
    * 未接来电数量
    */
    int missCallNum();
}
