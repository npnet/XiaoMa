package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.model.LocationClientOption;

/**
 * 定位客户端的行为定义
 * Created by minxiwen on 2017/12/11 0011.
 */

public interface ILocation {
    //开始定位
    void start();

    //停止定位
    void stop();

    //定位完毕，回收资源
    void destroy();

    //设置定位参数
    void setLocOption(LocationClientOption option);

    //设置定位监听
    void setLocationListener(OnLocationChangeListener listener);

}
