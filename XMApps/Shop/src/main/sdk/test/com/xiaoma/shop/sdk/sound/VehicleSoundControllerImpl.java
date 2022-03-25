package com.xiaoma.shop.sdk.sound;

import com.xiaoma.shop.sdk.PublishResultCallback;
import com.xiaoma.utils.log.KLog;

import java.util.Random;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/6 0006 11:52
 *   desc:   整车音效控制器具体逻辑处理
 * </pre>
 */
public class VehicleSoundControllerImpl implements IVehicleSoundController {

    @Override
    public void publish(String filePath, String tag, PublishResultCallback callback) {
        Random random = new Random();
        int interval = random.nextInt(10);
        if (interval <= 8) {
            callback.success();
        } else {
            callback.failed(interval);
        }
    }

    @Override
    public void upgrade() {
        KLog.d("Upgrade vehicle sound.");
    }

    @Override
    public String getCurrentTag() {
        return null;
    }

    @Override
    public boolean checkAllowUpdate() {
        /**
         * 检查是否允许更新，以下情况可以更新升级
         * <p>
         * 1、蓄电池电压＞12V（具体数字后续可能有调整）
         * 2、SOC＞99%（具体数字后续可能有调整）
         * 3、供电模式= IgnitionOn
         * 4、车速=0
         * 5、档位为P档
         * 6、EPB为LOCK状态
         */
        return false;
    }
}
