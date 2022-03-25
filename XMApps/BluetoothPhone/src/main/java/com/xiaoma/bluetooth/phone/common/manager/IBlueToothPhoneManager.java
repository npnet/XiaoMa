package com.xiaoma.bluetooth.phone.common.manager;

import android.bluetooth.BluetoothDevice;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;

import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/24 11:34
 */
public interface IBlueToothPhoneManager {

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
    boolean sendDTMF(Character code);

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
     * 使用安富接口进行挂断并接听,保持并接听,直接接听
     * @param operate 0表示接听,1表示保持并接听,2表示挂断并接听
     */
    void answerCallByNForeApi(int operate);

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
     * 获取上一个来电号码
     */
    String getDialBackNumber();

    /**
     * 获取上一个去电号码
     */
    String getRedialNumber();

    /**
     * 获取音频状态
     */
    int getAudioState();

    /**
     * 销毁
     */
    void onDestroy();


    /**
     * 获取通讯录同步状态
     */
    boolean isContactBookSynchronized();

    /**
     * 同步通讯录操作
     */
    void synchronizeContactBook(PullPhoneBookResultCallback callback);

    /**
    * 获取通话记录
    */

    List<ContactBean> getCallHistory();

    /**
     * 蓝牙是否已连接
     */
    boolean isBluetoothConnected();
    /*
    * 通话静音
    * @ return 返回的是当前麦克风的状态
    * */
    boolean mutePhone();

    /*
    * 未接电话数量
    * */
    int missCallNum();

    /**
     * 下载通讯信息
     */
    void downloadByType(BlueToothPhoneManagerFactory.PhoneType phoneType);

    /**
     * 下载所有通讯信息
     */
    void downloadAll();

    void setPullResultCallback(PullContactbookCallback callback);

    /**
     * Stop sending HFP stream data to audio track.
     */
    void pauseHfpRender();

    /**
     * Start sending HFP stream data to audio track.
     */
    void startHfpRender();

    /**
     *  中断蓝牙下载
     * @param macAddress 远程蓝牙地址
     */
    void stopDownload(String macAddress);

    /**
     *  设备是否已断开
     * @param device
     * @return
     */
    boolean isDeviceDisconnected(BluetoothDevice device);


    /**
     *  设备是否已经连接上
     * @param device
     * @return
     */
    boolean isDeviceConnected(BluetoothDevice device);

    /**
     * 清空任務棧
     */
    void cleanTask();


    boolean isHfpDisconnected();
}
