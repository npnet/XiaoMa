package com.xiaoma.login.sdk;

import android.content.Context;

/**
 * Created by kaka
 * on 19-4-22 上午11:54
 * <p>
 * desc: #a
 * </p>
 */
public interface IFace {
    void init(Context context);

    /**
     * 判断人脸识别模块是否可用
     *
     * @return 是否可用
     */
    boolean isFaceALive();

    /*===============================Identify Start==================================*/

    /**
     * 判断当前是否正在人脸识别中，返回是或否
     *
     * @return 是否正在人脸识别中
     */
    boolean isIdentifying();

    /**
     * 用于主动调用进行人脸识别的操作
     */
    void startIdentify();

    /**
     * 由于取消人脸识别的操作
     */
    void cancelIdentify();

    /**
     * 用于监听人脸识别的各个状态，包括开始、结束、成功、失败，成功时需要返回人脸特征值，失败返回错误码
     *
     * @param identifyListener 人脸识别的监听
     */
    void addIdentifyListener(IdentifyListener identifyListener);

    /**
     * 移除监听人脸识别监听
     *
     * @param identifyListener 人脸识别的监听
     */
    void removeIdentifyListener(IdentifyListener identifyListener);

    /*================================Identify End=================================*/


    /*================================Record Start=================================*/

    /**
     * 用于判断当前是否正在处于人脸录入操作
     *
     * @return 是否正在处于人脸录入操作
     */
    boolean isRecording();

    /**
     * 用于录入人脸数据，传入userId用于注册
     *
     * @param userId userId
     */
    void startRecord(int userId);

    /**
     * 用于取消人脸录入操作
     */
    void cancelRecord();

    /**
     * 人脸录入结果的各个状态，包括开始、中间引导（例如提示抬头、低头、侧脸等）、结束、成功、失败，成功时需要返回人脸特征值，失败返回错误码
     *
     * @param IRecordListener 录入人脸的各个状态监听
     */
    void addRecordListener(IRecordListener IRecordListener);

    /**
     * 移除人脸录入监听
     *
     * @param IRecordListener 录入人脸的各个状态监听
     */
    void removeRecordListener(IRecordListener IRecordListener);

    /**
     * 用于删除已经录入的人脸数据
     *
     * @param faceId 人脸数据id
     */
    void deleteRecord(int faceId);

    /*================================Record End=================================*/

    void mockFace();

    void setMockFace(int faceId);

    int getMockFace();
}
