package com.xiaoma.wechat.manager;

import android.content.Context;

import com.xiaoma.wechat.callback.WeChatCallback;
import com.xiaoma.wechat.model.WeChatContact;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/5/21 14:18
 * Desc: 车载微信
 */
public interface IWeChatManager {

    /**
     * 初始化
     */
    void init(Context context);

    /**
     * 是否已登录
     */
    boolean isLogined();

    /**
     * 发送消息
     *
     * @param contactId 微信联系人id
     * @param content   消息内容
     * @return 消息id
     */
    String sendMessage(String contactId, String content);

    /**
     * 播报消息
     */
    void playMsg();

    /**
     * 获取联系人列表
     */
    List<WeChatContact> getContactList();

    /**
     * 打开"车载微信"最近联系人页面，如果没有登录显示扫码登录页面
     */
    void startWechat();

    /**
     * 查询当前"车载微信"界面是否可见
     */
    boolean isWeMain();

    /**
     * 查询当前"车载微信-最近联系人"界面是否可见
     */
    boolean isContact();

    /**
     * 查询当前"车载微信-聊天"界面是否可见
     */
    boolean isConversion();

    /**
     * 在最近联系人界面时执行"上一页"
     */
    void onPrevKeyEvent();

    /**
     * 在最近联系人界面时执行"下一页"
     */
    void onNextKeyEvent();

    /**
     * 发送语音按键Down事件,用于在"车载微信-聊天"界面时,方控"语音按键"按下/弹起时,通知"车载微信"开始/结束录制语音消息
     */
    void onDownKeyEvent();

    /**
     * 发送语音按键Up事件,用于在"车载微信-聊天"界面时,方控"语音按键"按下/弹起时,通知"车载微信"开始/结束录制语音消息
     */
    void onUpKeyEvent();

    /**
     * 搜索联系人或群聊会话
     *
     * @param name 联系人昵称、备注或者群聊名称
     * @return 联系人或群聊列表
     */
    List<WeChatContact> queryContacts(String name);

    /**
     * 将联系人数据转化为WeChatContact对象
     */
    WeChatContact convertIntoWxContactBean(Object data);

    /**
     * 添加回调
     */
    void addCallback(WeChatCallback callback);

    /**
     * 删除回调
     */
    void removeCallback(WeChatCallback callback);

    /**
     * 获取请求头像时需要设置的cookie
     */
    String getCookie();

    /**
     * 打开聊天界面
     *
     * @param id 联系人或群聊会话的id
     */
    void openChatPage(String id);

}
