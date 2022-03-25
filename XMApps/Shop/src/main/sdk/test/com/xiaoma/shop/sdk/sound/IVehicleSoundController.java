package com.xiaoma.shop.sdk.sound;

import com.xiaoma.shop.sdk.PublishResultCallback;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/6 0006 11:37
 *   desc:   整车音效接口推送控制器
 * </pre>
 */
public interface IVehicleSoundController {

    /**
     * 推送音效文件
     *
     * @param tag      文件对应tag参数
     * @param callback 推送结果回调
     */
    void publish(String filePath, String tag, PublishResultCallback callback);

    /**
     * 升级音效
     */
    void upgrade();

    /**
     * 获取当前使用音效tag
     */
    String getCurrentTag();

    /**
     * 检查是否允许更新，以下情况可以更新升级
     * <p>
     * 1、蓄电池电压＞12V（具体数字后续可能有调整）
     * 2、SOC＞99%（具体数字后续可能有调整）
     * 3、供电模式= IgnitionOn
     * 4、车速=0
     * 5、档位为P档
     * 6、EPB为LOCK状态
     *
     * @return 是否可更新升级状态
     */
    boolean checkAllowUpdate();
}
