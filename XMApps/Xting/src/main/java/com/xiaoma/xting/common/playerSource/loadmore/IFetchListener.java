package com.xiaoma.xting.common.playerSource.loadmore;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/28
 */
public interface IFetchListener {

    void onLoading();

    void onSuccess(Object t);

    void onFail();

    void onError(int code, String msg);
}
