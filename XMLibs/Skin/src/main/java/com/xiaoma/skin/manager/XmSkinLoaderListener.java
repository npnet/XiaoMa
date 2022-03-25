package com.xiaoma.skin.manager;

/**
 * Created by ZhangYao.
 * Date ：2018/9/11 0011
 */
public interface XmSkinLoaderListener{
    /**
     * 开始加载.
     */
    void onStart();

    /**
     * 加载成功.
     */
    void onSuccess();

    /**
     * 加载失败.
     *
     * @param errMsg 错误信息.
     */
    void onFailed(String errMsg);
}
