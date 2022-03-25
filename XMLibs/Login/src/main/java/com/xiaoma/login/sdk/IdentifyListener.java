package com.xiaoma.login.sdk;

/**
 * Created by kaka
 * on 19-4-26 下午8:06
 * <p>
 * desc: #a
 * </p>
 */
interface IdentifyListener {
    /**
     * 人脸识别开始的回调
     */
    void onStart();

    /**
     * 人脸识别成功的回调
     *
     * @param faceId 人脸数据id
     */
    void onSuccess(int faceId);

    /**
     * 人脸识别失败的回调
     *
     * @param code 错误码
     * @param msg  msg
     */
    void onFailure(int code, String msg);

    /**
     * 人脸识别结束的回调
     */
    void onEnd();
}
