package com.xiaoma.pet.common.callback;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/17 0017 9:56
 *   desc:   商城网络异常加载失败，在PetMallActivity 处理，
 *           统一显示网络失败页
 * </pre>
 */
public interface ILoadDataOfMallListener {


    void loadSuccessOfMall();

    void loadFailedOfMall(String fragmentTag);
}
