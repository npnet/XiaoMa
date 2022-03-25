package com.xiaoma.smarthome.common.processor;

/**
 * Created by zy 2018/8/1 19:19
 */
public interface IDateProcessor<T,D> {
    void process(T t, CompletedListener<D> listener);
}
