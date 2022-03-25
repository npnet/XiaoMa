package com.xiaoma.push.callback;

import android.support.annotation.Nullable;

/**
 * 这个参数回调是用来保证handler中所需要的参数都是通过接口实时获取的
 *
 * @author KY
 * @date 2018/9/20
 */
public interface ParameterCallback<T> {
    @Nullable
    T get();
}
