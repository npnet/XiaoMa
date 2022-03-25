package com.xiaoma.music.kuwo.listener;

/**
 * Author: loren
 * Date: 2019/6/25 0025
 */
public interface OnPayTokenListener {

    /**
     * 结果回调
     *
     * @param code    状态 -1:未登录
     * @param message 说明
     * @param token   支付token
     */
    void onFetch(int code, String message, String token);
}
