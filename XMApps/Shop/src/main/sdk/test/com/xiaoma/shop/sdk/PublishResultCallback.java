package com.xiaoma.shop.sdk;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/6 0006 11:39
 *   desc:   推送结果回调
 * </pre>
 */
public interface PublishResultCallback {

    /**
     * 推送成功
     */
    void success();

    /**
     * 推送失败
     *
     * @param code 失败状态码
     */
    void failed(int code);
}
