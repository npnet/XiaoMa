package com.xiaoma.music.common.constant;

/**
 * Created by ZYao.
 * Date ：2019/1/3 0003
 */
public interface ICallBack {
    void onSuccess(boolean haveCache);

    void onFailed(boolean haveCache);
}
