package com.xiaoma.login.sdk;

/**
 * Created by kaka
 * on 19-4-26 下午8:11
 * <p>
 * desc: #a
 * </p>
 */
public interface IRecordListener {
    /**
     * 人脸录入开始的回调
     */
    void onStart();

    /**
     * 人脸录入引导提示的回调
     *
     * @param recordGuid
     */
    void onGuidTip(RecordGuid recordGuid);

    /**
     * 人脸录入成功的回调
     *
     * @param faceId 人脸数据id
     */
    void onSuccess(int faceId);

    /**
     * 人脸录入失败的回调
     *
     * @param code 错误码
     * @param msg  msg
     */
    void onFailure(int code, String msg);

    /**
     * 人脸录入已被绑定回调
     *
     * @param faceId 已绑定的人脸ID
     */
    void onFaceAlreadyBind(int faceId);

    /**
     * 人脸录入结束的回调
     */
    void onEnd();
}
