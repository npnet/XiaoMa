package com.xiaoma.smarthome.common.processor;

/**
 * Created by zy 2018/8/1 19:22
 */
public interface CompletedListener<T> {

    void completed(T t);
}
