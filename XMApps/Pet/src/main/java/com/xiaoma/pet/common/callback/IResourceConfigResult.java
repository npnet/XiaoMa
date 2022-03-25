package com.xiaoma.pet.common.callback;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/27 0027 19:42
 *   desc:   资源配置结果回调
 * </pre>
 */
public interface IResourceConfigResult {
    void start();

    void result(boolean success);
}
