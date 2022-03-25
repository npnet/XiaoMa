package com.xiaoma.launcher.common.manager;

/**
 * @author taojin
 * @date 2019/4/9
 */
public interface WeatherResultCallBack<T> {

    void onSuccess(T t);

    void onFailure(int code, String msg);
}
