package com.xiaoma.model;

/**
 * @author youthyJ
 * @date 2018/10/15
 */
public interface ResultCallback<Result extends XMResult> {
    void onSuccess(Result result);

    void onFailure(int code, String msg);
}
