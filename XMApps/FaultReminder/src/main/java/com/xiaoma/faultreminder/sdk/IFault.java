package com.xiaoma.faultreminder.sdk;

import android.content.Context;

import com.xiaoma.faultreminder.sdk.model.CarFault;

import java.util.List;

/**
 * @author KY
 * @date 12/26/2018
 */
public interface IFault {
    /**
     * 初始化
     */
    void init(Context context);

    /**
     * 获取当前的故障列表
     *
     * @return 故障列表
     */
    List<CarFault> getCurrentFaults();

    /**
     * 注册故障监听
     */
    void registerFaultListener(FaultListener faultListener);

    /**
     * 移除故障监听
     */
    void removeFaultListener(FaultListener faultListener);
}
