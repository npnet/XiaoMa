package com.xiaoma.wechat.manager;

/**
 * Created by qiuboxiang on 2019/5/21 14:30
 * Desc: 车载微信
 */
public class WeChatManagerFactory {

    public static IWeChatManager getManager(){
        return WeChatManager.getInstance();
    }

}
