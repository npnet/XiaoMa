package com.xiaoma.club.common.hyphenate.callback;

import android.content.Context;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;
import com.xiaoma.utils.log.KLog;

/**
 * 即时通讯状态连接监听器。 如果遇到弱网掉线情况，应用收到onDisconnected，此时不需要处理重连操作，
 * Hyphenate SDK在底层自动处理重练。 回调函数只有onConnected, onDisconnected, 无需考虑连接中，断开中一类的中间状态.
 * 请注意, 应用不要在这两个回调函数中更新界面，这两个现成属于工作线程，直接更新界面会导致界面的并发错误。
 */
public class EMConnectionListenerImpl implements EMConnectionListener {
    Context context;

    public EMConnectionListenerImpl(Context context){
        this.context = context;
    }

    final static String TAG = EMConnectionListenerImpl.class.getName();

    /**
     * 成功连接到Hyphenate IM服务器时触发
     */
    @Override
    public void onConnected() {
        KLog.d(TAG, "hx client connected ->");
    }

    /**
     * 和Hyphenate IM服务器断开连接时触发
     * @param error
     */
    @Override
    public void onDisconnected(int error) {
        if(error == EMError.USER_REMOVED){
            // 显示帐号已经被移除
            KLog.e(TAG,"hx client user be removed ->");
        }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
            KLog.e(TAG,"hx client user login on an other device ->");
        } else {
            if (NetUtils.hasNetwork(context)){
                //连接不到聊天服务器
                KLog.e(TAG,"hx client can not connect server, network is ok ->");
            }else{
                //当前网络不可用，请检查网络设置
                KLog.e(TAG,"hx client can not connect server, network is not ok ->");
            }
        }
    }
}
