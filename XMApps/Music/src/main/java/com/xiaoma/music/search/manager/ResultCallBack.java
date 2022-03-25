package com.xiaoma.music.search.manager;

/**
 * Created by ZYao.
 * Date ：2019/3/8 0008
 */
public interface ResultCallBack<T> {

    void onSuccess(T t);

    void onFailed(String msg);
}

