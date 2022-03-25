package com.xiaoma.shop.business.ui.bought.callback;

import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.ResourceType;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/24 0024 11:23
 *   desc:   一键缓存清理回调
 * </pre>
 */
public interface OneKeyCleanCacheCallback {

    /**
     * 初始化行为（无状态、清缓存、用户绑定）
     *
     * @param status -1:无状态  0:清缓存  1:用户绑定  -1：
     * @param type   资源类型
     */
    void initAction(@CacheBindStatus int status, @ResourceType int type);

    /**
     * 当前所选缓存大小
     *
     * @param cacheText
     */
    void selectedCacheSize(String cacheText);

    /**
     * 开始清理
     */
    void startClean();


    /**
     * 结束清理
     */
    void completeClean();


    /**
     * 下载完成后刷新占用空间size
     */
    void downloadCompleteUpdateCacheSize();

    /**
     *  请求到已购买列表后，刷新占用空间Size
     */
    void refreshCacheSize();

}
