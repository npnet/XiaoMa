package com.xiaoma.facerecognize.sdk;

import android.content.Context;

/**
 * Created by kaka
 * on 19-3-27 下午3:15
 * <p>
 * desc: #a
 * </p>
 */
public interface IFaceRecognize {

    /**
     * 初始化
     */
    void init(Context context);

    /**
     * 注册识别监听
     */
    void registerRecognizeListener(RecognizeListener faultListener);

    /**
     * 移除识别监听
     */
    void removeRecognizeListener(RecognizeListener faultListener);

    /**
     * 用于测试识别
     *
     * @param recognizeType 识别类型
     */
    void mockHandleRecognize(RecognizeType recognizeType);
}
